package com.example.codeplayfx.compiler;

import com.example.codeplayfx.util.Logger;
import com.example.codeplayfx.scripting.runtime.UserGame;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

/**
 * InMemoryCompiler - Compiles Java source code using external javac process.
 */
public class InMemoryCompiler {

    private final String className;
    private final String sourceCode;

    public InMemoryCompiler(String className, String sourceCode) {
        this.className = className;
        this.sourceCode = sourceCode;
    }

    /**
     * Compile and load the user game class
     */
    public UserGame compile() {
        Path tempDir = null;
        try {
            Logger.info("Compiling: " + className);

            // Create temporary directory
            tempDir = Files.createTempDirectory("codeplay_");
            Path classesDir = tempDir.resolve("classes");
            Files.createDirectories(classesDir);

            // Write source file
            String javaFileName = className.replace('.', '/') + ".java";
            Path javaFile = tempDir.resolve(javaFileName);
            Files.createDirectories(javaFile.getParent());
            Files.writeString(javaFile, sourceCode, StandardCharsets.UTF_8);

            // Build classpath
            String classpath = buildClassPath();

            // Get javac path
            String javacPath = getJavaCompilerPath();

            // Compile with javac process
            ProcessBuilder pb = new ProcessBuilder(
                    javacPath,
                    "-cp", classpath,
                    "-d", classesDir.toString(),
                    "-Xlint:unchecked",
                    javaFile.toString()
            );

            Process process = pb.start();

            // Read error stream
            String errorOutput = readStream(process.getErrorStream());
            String normalOutput = readStream(process.getInputStream());

            // Wait for completion
            boolean finished = process.waitFor(10, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new CompilationException("Compilation timed out after 10 seconds");
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                throw new CompilationException("Compilation failed:\n" + errorOutput);
            }

            // Load compiled class
            URLClassLoader classLoader = new URLClassLoader(
                    new URL[]{classesDir.toUri().toURL()},
                    getClass().getClassLoader()
            );

            Class<?> clazz = classLoader.loadClass(className);
            Object instance = clazz.getDeclaredConstructor().newInstance();

            if (!(instance instanceof UserGame)) {
                throw new CompilationException("Compiled class must extend UserGame");
            }

            Logger.info("Compilation successful: " + className);
            return (UserGame) instance;

        } catch (Exception e) {
            throw new CompilationException("Compilation error: " + e.getMessage(), e);
        } finally {
            // Clean up temp directory
            if (tempDir != null) {
                try {
                    deleteDirectory(tempDir);
                } catch (IOException e) {
                    Logger.error("Failed to clean temp directory: " + e.getMessage());
                }
            }
        }
    }

    private String getJavaCompilerPath() {
        String javaHome = System.getProperty("java.home");

        // Check for javac in JDK bin directory
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            Path javacExe = Paths.get(javaHome, "bin", "javac.exe");
            if (Files.exists(javacExe)) {
                return javacExe.toString();
            }
        }

        Path javacPath = Paths.get(javaHome, "bin", "javac");
        if (Files.exists(javacPath)) {
            return javacPath.toString();
        }

        // Fallback - assume javac is in PATH
        return "javac";
    }

    private String buildClassPath() {
        StringBuilder classpath = new StringBuilder();

        // Add current classpath
        classpath.append(System.getProperty("java.class.path"));

        // Add module path if exists
        String modulePath = System.getProperty("jdk.module.path");
        if (modulePath != null && !modulePath.isEmpty()) {
            classpath.append(File.pathSeparator).append(modulePath);
        }

        return classpath.toString();
    }

    private String readStream(InputStream stream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            return result.toString();
        }
    }

    private void deleteDirectory(Path dir) throws IOException {
        if (Files.exists(dir)) {
            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}