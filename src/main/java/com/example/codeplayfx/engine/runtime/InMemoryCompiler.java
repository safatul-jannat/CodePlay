package com.example.codeplayfx.engine.runtime;

import javax.tools.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class InMemoryCompiler {
    private static Map<String, byte[]> compiledClasses = new HashMap<>();
    private static Class<?> lastCompiledClass = null;

    public static Class<?> compile(String className, String sourceCode) {
        try {
            System.out.println("=== IN-MEMORY COMPILATION ===");
            System.out.println("Compiling: " + className);

            // Get Java compiler
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                throw new RuntimeException("No Java compiler found. Use JDK (not JRE)!");
            }

            // Create in-memory file manager
            MemoryFileManager fileManager = new MemoryFileManager(
                    compiler.getStandardFileManager(null, null, null)
            );

            // Create compilation unit from string
            JavaFileObject source = new JavaSourceFromString(className, sourceCode);

            // Prepare compilation
            List<String> options = Arrays.asList(
                    "-classpath", getClassPath(),
                    "-Xlint:unchecked"
            );

            List<JavaFileObject> compilationUnits = Arrays.asList(source);

            StringWriter output = new StringWriter();
            JavaCompiler.CompilationTask task = compiler.getTask(
                    output, fileManager, null, options, null, compilationUnits
            );

            // Compile!
            boolean success = task.call();
            System.out.println("Compilation " + (success ? "SUCCESS" : "FAILED"));

            if (!success) {
                System.err.println("Compilation output:\n" + output.toString());
                return null;
            }

            // Get compiled bytes
            compiledClasses = fileManager.getClassBytes();
            System.out.println("Compiled classes: " + compiledClasses.keySet());

            // Load the main class
            InMemoryClassLoader classLoader = new InMemoryClassLoader(compiledClasses);
            lastCompiledClass = classLoader.loadClass(className);

            System.out.println("Class loaded: " + lastCompiledClass.getName());
            return lastCompiledClass;

        } catch (Exception e) {
            System.err.println("Compilation error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static Class<?> getLastCompiledClass() {
        return lastCompiledClass;
    }

    public static void clearCache() {
        compiledClasses.clear();
        lastCompiledClass = null;
    }

    private static String getClassPath() {
        return System.getProperty("java.class.path");
    }

    // ===== HELPER CLASSES =====

    static class JavaSourceFromString extends SimpleJavaFileObject {
        final String code;

        JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension),
                    Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    static class MemoryFileManager extends ForwardingJavaFileManager<JavaFileManager> {
        private final Map<String, byte[]> classBytes = new HashMap<>();

        MemoryFileManager(JavaFileManager fileManager) {
            super(fileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String className,
                                                   JavaFileObject.Kind kind,
                                                   FileObject sibling) throws IOException {
            if (kind == JavaFileObject.Kind.CLASS) {
                return new SimpleJavaFileObject(
                        URI.create("mem:///" + className.replace('.', '/') + kind.extension),
                        kind
                ) {
                    @Override
                    public OutputStream openOutputStream() {
                        return new ByteArrayOutputStream() {
                            @Override
                            public void close() throws IOException {
                                super.close();
                                classBytes.put(className, toByteArray());
                                System.out.println("  -> Stored class: " + className);
                            }
                        };
                    }
                };
            }
            return super.getJavaFileForOutput(location, className, kind, sibling);
        }

        Map<String, byte[]> getClassBytes() {
            return classBytes;
        }
    }

    static class InMemoryClassLoader extends ClassLoader {
        private final Map<String, byte[]> classBytes;

        InMemoryClassLoader(Map<String, byte[]> classBytes) {
            this.classBytes = classBytes;
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            byte[] bytes = classBytes.get(name);
            if (bytes == null) {
                throw new ClassNotFoundException(name);
            }
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
}