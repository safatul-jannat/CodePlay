package com.example.codeplayfx.engine.runtime;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.util.*;

public class InMemoryCompiler {

    private static Class<?> lastCompiledClass;

    // ==================================================
    // MAIN COMPILE METHOD
    // ==================================================
    public static Class<?> compile(String className, String sourceCode) {

        try {
            System.out.println("=== IN-MEMORY COMPILATION ===");
            System.out.println("Compiling: " + className);

            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                throw new IllegalStateException(
                        "JavaCompiler not found. Make sure you are using a JDK, not a JRE."
                );
            }

            StandardJavaFileManager standardFileManager =
                    compiler.getStandardFileManager(null, null, null);

            MemoryFileManager fileManager =
                    new MemoryFileManager(standardFileManager);

            JavaFileObject source =
                    new JavaSourceFromString(className, sourceCode);

            List<String> options = Arrays.asList(
                    "-classpath", getClassPath(),
                    "--module-path", getModulePath(),
                    "--add-modules", "ALL-MODULE-PATH"
            );

            JavaCompiler.CompilationTask task =
                    compiler.getTask(
                            null,
                            fileManager,
                            null,
                            options,
                            null,
                            List.of(source)
                    );

            boolean success = task.call();

            if (!success) {
                System.err.println("Compilation FAILED");
                return null;
            }

            System.out.println("Compilation SUCCESS");

            InMemoryClassLoader classLoader =
                    new InMemoryClassLoader(fileManager.getClassBytes());

            lastCompiledClass = classLoader.loadClass(className);
            System.out.println("Loaded class: " + className);

            return lastCompiledClass;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ==================================================
    // HELPERS
    // ==================================================
    private static String getClassPath() {
        return System.getProperty("java.class.path");
    }

    private static String getModulePath() {
        return System.getProperty("jdk.module.path");
    }

    public static Class<?> getLastCompiledClass() {
        return lastCompiledClass;
    }

    // ==================================================
    // SOURCE FROM STRING
    // ==================================================
    static class JavaSourceFromString extends SimpleJavaFileObject {

        private final String code;

        JavaSourceFromString(String className, String code) {
            super(
                    URI.create("string:///" +
                            className.replace('.', '/') +
                            Kind.SOURCE.extension),
                    Kind.SOURCE
            );
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }

    // ==================================================
    // IN-MEMORY FILE MANAGER
    // ==================================================
    static class MemoryFileManager
            extends ForwardingJavaFileManager<StandardJavaFileManager> {

        private final Map<String, byte[]> classBytes = new HashMap<>();

        MemoryFileManager(StandardJavaFileManager fileManager) {
            super(fileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(
                Location location,
                String className,
                JavaFileObject.Kind kind,
                FileObject sibling
        ) {
            return new SimpleJavaFileObject(
                    URI.create("mem:///" +
                            className.replace('.', '/') +
                            kind.extension),
                    kind
            ) {
                @Override
                public OutputStream openOutputStream() {
                    return new ByteArrayOutputStream() {
                        @Override
                        public void close() throws IOException {
                            classBytes.put(className, toByteArray());
                            super.close();
                        }
                    };
                }
            };
        }

        Map<String, byte[]> getClassBytes() {
            return classBytes;
        }
    }

    // ==================================================
    // CLASS LOADER
    // ==================================================
    static class InMemoryClassLoader extends ClassLoader {

        private final Map<String, byte[]> classBytes;

        InMemoryClassLoader(Map<String, byte[]> classBytes) {
            super(ClassLoader.getSystemClassLoader());
            this.classBytes = classBytes;
        }

        @Override
        protected Class<?> findClass(String name)
                throws ClassNotFoundException {

            byte[] bytes = classBytes.get(name);
            if (bytes == null) {
                return super.findClass(name);
            }
            return defineClass(name, bytes, 0, bytes.length);
        }
    }
}
