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
        loadProjects(); // load existing projects when app starts
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
     * load all saved projects from disk when app starts
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

                    // create project object from file
                    Project project = new Project(name, code);

                    // set last modified time from file
                    project.setLastModified(LocalDateTime.ofEpochSecond(
                            file.lastModified() / 1000, 0, java.time.ZoneOffset.UTC));

                    projects.add(project);
                    System.out.println("Loaded project: " + name);
                } catch (IOException e) {
                    System.err.println("Failed to load project: " + file.getName());
                }
            }
        }
        System.out.println("Loaded " + projects.size() + " projects from gallery");
    }

    /**
     * save project to disk
     */
    public void saveProject(String name, String code) {
        try {
            // remove invalid characters from filename
            String safeName = name.replaceAll("[^a-zA-Z0-9.-]", "_");
            Path filePath = Paths.get(GALLERY_PATH + safeName + ".java");

            // check if file already exists
            boolean exists = Files.exists(filePath);

            // write code to file
            Files.write(filePath, code.getBytes());

            // update or add to projects list
            Project existing = findProjectByName(name);
            if (existing != null) {
                existing.setCode(code);
                existing.setLastModified(LocalDateTime.now());
                System.out.println("Project updated: " + name);
            } else {
                Project newProject = new Project(name, code);
                projects.add(newProject);
                System.out.println("New project saved: " + name);
            }

            // show message if overwritten
            if (exists) {
                System.out.println("Overwrote existing file: " + name + ".java");
            }

        } catch (IOException e) {
            System.err.println("Failed to save project: " + name);
            e.printStackTrace();
        }
    }

    /**
     * delete project from disk
     */
    public void deleteProject(Project project) {
        try {
            String safeName = project.getName().replaceAll("[^a-zA-Z0-9.-]", "_");
            Path filePath = Paths.get(GALLERY_PATH + safeName + ".java");

            // delete file
            boolean deleted = Files.deleteIfExists(filePath);

            // remove from list
            projects.remove(project);

            if (deleted) {
                System.out.println("Deleted project: " + project.getName());
            } else {
                System.out.println("Project file not found: " + project.getName());
            }

        } catch (IOException e) {
            System.err.println("Failed to delete project: " + project.getName());
            e.printStackTrace();
        }
    }

    /**
     * import project from external file
     */
    public void importProject(File file) {
        try {
            String code = new String(Files.readAllBytes(file.toPath()));
            String name = file.getName().replace(".java", "").replace(".cpg", "");
            saveProject(name, code);
        } catch (IOException e) {
            System.err.println("Failed to import project: " + file.getName());
            e.printStackTrace();
        }
    }

    /**
     * export project to external location
     */
    public void exportProject(Project project, File destination) {
        try {
            Files.write(destination.toPath(), project.getCode().getBytes());
            System.out.println("Exported project to: " + destination.getAbsolutePath());
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