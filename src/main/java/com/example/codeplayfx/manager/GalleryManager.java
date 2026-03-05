package com.example.codeplayfx.manager;

import com.example.codeplayfx.model.Project;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GalleryManager {
    private static GalleryManager instance;
    private ObservableList<Project> projects = FXCollections.observableArrayList();
    private final String GALLERY_PATH = System.getProperty("user.home") + "/CodePlayGallery/";

    private GalleryManager() {
        createDirectory();
        loadProjects();
    }

    public static GalleryManager getInstance() {
        if (instance == null) {
            instance = new GalleryManager();
        }
        return instance;
    }

    private void createDirectory() {
        File dir = new File(GALLERY_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("Created gallery directory: " + GALLERY_PATH);
        }
    }

    /**
     * Load all projects from disk on startup
     */
    private void loadProjects() {
        projects.clear();
        File dir = new File(GALLERY_PATH);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".java"));

        if (files != null) {
            for (File file : files) {
                try {
                    String code = new String(Files.readAllBytes(file.toPath()));
                    String name = file.getName().replace(".java", "");

                    // Try to load documentation from separate .doc file
                    String documentation = loadDocumentation(name);

                    Project project = new Project(name, code, documentation);

                    // Also try to load last modified time from file
                    File docFile = new File(GALLERY_PATH + name + ".doc");
                    long lastModified = Math.max(
                            file.lastModified(),
                            docFile.exists() ? docFile.lastModified() : 0
                    );

                    project.setLastModified(LocalDateTime.ofEpochSecond(
                            lastModified / 1000, 0, java.time.ZoneOffset.UTC));

                    projects.add(project);
                    System.out.println("Loaded project: " + name + " (doc: " + (documentation.isEmpty() ? "empty" : "has content") + ")");
                } catch (IOException e) {
                    System.err.println("Failed to load project: " + file.getName());
                }
            }
        }
        System.out.println("Loaded " + projects.size() + " projects from gallery");
    }

    /**
     * Load documentation for a project from .doc file
     */
    private String loadDocumentation(String projectName) {
        try {
            // Sanitize filename to be safe
            String safeName = projectName.replaceAll("[^a-zA-Z0-9.-]", "_");
            Path docPath = Paths.get(GALLERY_PATH + safeName + ".doc");

            if (Files.exists(docPath)) {
                String content = new String(Files.readAllBytes(docPath));
                System.out.println(" Loaded documentation for: " + projectName + " (" + content.length() + " chars)");
                return content;
            }
        } catch (IOException e) {
            System.err.println("Failed to load documentation for: " + projectName);
        }
        return "";
    }

    /**
     * Save documentation for a project to .doc file
     */
    private void saveDocumentation(String projectName, String documentation) {
        try {
            // Sanitize filename to be safe
            String safeName = projectName.replaceAll("[^a-zA-Z0-9.-]", "_");
            Path docPath = Paths.get(GALLERY_PATH + safeName + ".doc");

            if (documentation == null) {
                documentation = "";
            }

            Files.write(docPath, documentation.getBytes());
            System.out.println(" Saved documentation for: " + projectName + " (" + documentation.length() + " chars) to: " + docPath);
        } catch (IOException e) {
            System.err.println("Failed to save documentation for: " + projectName);
            e.printStackTrace();
        }
    }

    /**
     * Save project to disk (including documentation)
     */
    public void saveProject(String name, String code, String documentation) {
        try {
            // Sanitize filename to be safe
            String safeName = name.replaceAll("[^a-zA-Z0-9.-]", "_");
            Path filePath = Paths.get(GALLERY_PATH + safeName + ".java");

            // Save code
            Files.write(filePath, code.getBytes());
            System.out.println(" Saved code for: " + name + " to: " + filePath);

            // Save documentation (even if empty, to ensure consistency)
            saveDocumentation(name, documentation);

            // Update or create project in memory
            Project existing = findProjectByName(name);
            if (existing != null) {
                existing.setCode(code);
                existing.setDocumentation(documentation);
                existing.setLastModified(LocalDateTime.now());
                System.out.println(" Updated existing project: " + name);
            } else {
                Project newProject = new Project(name, code, documentation);
                projects.add(newProject);
                System.out.println(" Created new project: " + name);
            }

        } catch (IOException e) {
            System.err.println(" Failed to save project: " + name);
            e.printStackTrace();
        }
    }

    /**
     * Overloaded method for backward compatibility
     */
    public void saveProject(String name, String code) {
        Project existing = findProjectByName(name);
        String documentation = (existing != null) ? existing.getDocumentation() : "";
        saveProject(name, code, documentation);
    }

    /**
     * Get documentation for a specific project
     */
    public String getProjectDocumentation(String projectName) {
        Project project = findProjectByName(projectName);
        if (project != null) {
            return project.getDocumentation();
        }
        return loadDocumentation(projectName); // Try to load from disk directly
    }

    /**
     * Delete project from disk (including documentation)
     */
    public void deleteProject(Project project) {
        try {
            String safeName = project.getName().replaceAll("[^a-zA-Z0-9.-]", "_");
            Path filePath = Paths.get(GALLERY_PATH + safeName + ".java");
            Path docPath = Paths.get(GALLERY_PATH + safeName + ".doc");

            boolean deleted = Files.deleteIfExists(filePath);
            boolean docDeleted = Files.deleteIfExists(docPath);

            projects.remove(project);

            if (deleted) {
                System.out.println("🗑 Deleted project file: " + project.getName() + ".java");
            }
            if (docDeleted) {
                System.out.println("🗑 Deleted documentation file: " + project.getName() + ".doc");
            }

        } catch (IOException e) {
            System.err.println("Failed to delete project: " + project.getName());
            e.printStackTrace();
        }
    }

    /**
     * Import project from external file
     */
    public void importProject(File file) {
        try {
            String code = new String(Files.readAllBytes(file.toPath()));
            String name = file.getName().replace(".java", "").replace(".cpg", "");

            // When importing, documentation starts empty
            saveProject(name, code, "");

            System.out.println(" Imported project: " + name);
        } catch (IOException e) {
            System.err.println("Failed to import project: " + file.getName());
            e.printStackTrace();
        }
    }

    /**
     * Export project to external location
     */
    public void exportProject(Project project, File destination) {
        try {
            // Export just the code (not documentation)
            Files.write(destination.toPath(), project.getCode().getBytes());
            System.out.println(" Exported project to: " + destination.getAbsolutePath());

            // Optionally, also export documentation as a separate .txt file
            if (project.getDocumentation() != null && !project.getDocumentation().isEmpty()) {
                File docDest = new File(destination.getParent(),
                        destination.getName().replace(".java", "_doc.txt"));
                Files.write(docDest.toPath(), project.getDocumentation().getBytes());
                System.out.println(" Exported documentation to: " + docDest.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Failed to export project: " + project.getName());
            e.printStackTrace();
        }
    }

    public Project findProjectByName(String name) {
        for (Project p : projects) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }

    public ObservableList<Project> getProjects() {
        return projects;
    }

    public String getGalleryPath() {
        return GALLERY_PATH;
    }
}