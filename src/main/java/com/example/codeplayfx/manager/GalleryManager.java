package com.example.codeplayfx.manager;

import com.example.codeplayfx.model.Project;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;

public class GalleryManager {
    private static GalleryManager instance;
    private ObservableList<Project> projects = FXCollections.observableArrayList();
    private DatabaseManager dbManager = DatabaseManager.getInstance();

    private GalleryManager() {
        loadProjectsFromDatabase();
    }

    public static GalleryManager getInstance() {
        if (instance == null) {
            instance = new GalleryManager();
        }
        return instance;
    }

    /**
     * Load projects from database (metadata only)
     */
    private void loadProjectsFromDatabase() {
        projects.clear();
        var metadataList = dbManager.getAllProjectMetadata();
        for (var metadata : metadataList) {
            // Load FULL project content from files
            Project project = dbManager.loadProject(metadata.name);
            if (project != null) {
                projects.add(project);
                System.out.println(" Loaded: " + metadata.name);
            } else {
                System.err.println(" Failed to load: " + metadata.name);
            }
        }
        System.out.println(" Loaded " + projects.size() + " projects from database");
    }

    /**
     * Save project - database handles file creation and storage
     */
    public void saveProject(String name, String code, String documentation) {
        Project project = new Project(name, code, documentation);
        dbManager.saveProject(project);

        // Update observable list
        Project existing = findProjectByName(name);
        if (existing != null) {
            existing.setCode(code);
            existing.setDocumentation(documentation);
        } else {
            projects.add(project);
        }

        // Export to CSV for teacher viewing
        dbManager.exportToCSV(System.getProperty("user.home") + "/CodePlayGallery/projects_export.csv");
    }

    /**
     * Load full project content (lazy loading from files)
     */
    public Project loadFullProject(String projectName) {
        return dbManager.loadProject(projectName);
    }

    /**
     * Save project with just name and code (without documentation)
     * This fixes the "cannot be applied to given types" error
     */
    public void saveProject(String name, String code) {
        saveProject(name, code, ""); // Call the 3-parameter version with empty documentation
    }

    /**
     * Export project to external file
     */
    public void exportProject(Project project, File destination) {
        try {
            // Load full project to get the code
            Project fullProject = loadFullProject(project.getName());
            if (fullProject != null) {
                java.nio.file.Files.write(destination.toPath(), fullProject.getCode().getBytes());
                System.out.println(" Exported project to: " + destination.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Failed to export project: " + project.getName());
            e.printStackTrace();
        }
    }

    /**
     * Import project from external file
     */
    public void importProject(File file) {
        try {
            String code = new String(java.nio.file.Files.readAllBytes(file.toPath()));
            String name = file.getName().replace(".java", "");

            // Remove timestamp if present (e.g., "MyProject_20241201_123456" -> "MyProject")
            if (name.contains("_")) {
                name = name.split("_")[0];
            }

            saveProject(name, code, "");
            System.out.println(" Imported project: " + name);

        } catch (Exception e) {
            System.err.println("Failed to import project: " + file.getName());
            e.printStackTrace();
        }
    }

    /**
     * Delete project
     */
    public void deleteProject(Project project) {
        dbManager.deleteProject(project.getName());
        projects.remove(project);
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
    public void refreshProjects() {
        loadProjectsFromDatabase();
    }

    public void exportDatabaseToCSV() {
        dbManager.exportToCSV(System.getProperty("user.home") + "/Desktop/CodePlayDB_Export.csv");
        System.out.println(" Database exported to Desktop for teacher viewing");
    }
}