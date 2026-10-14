package com.example.codeplayfx.manager;

import com.example.codeplayfx.model.Project;
import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private final String DB_PATH = System.getProperty("user.home") + "/CodePlayGallery/codeplay.db";
    private final String STORAGE_PATH = System.getProperty("user.home") + "/CodePlayGallery/files/";

    private DatabaseManager() {
        createStorageDirectory();
        initializeDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void createStorageDirectory() {
        File dir = new File(STORAGE_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
            System.out.println("Created storage directory: " + STORAGE_PATH);
        }
    }

    private void initializeDatabase() {
        try {
            new File(System.getProperty("user.home") + "/CodePlayGallery").mkdirs();
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);

            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS projects (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE,
                    file_location TEXT NOT NULL,
                    doc_location TEXT,
                    code_size INTEGER,
                    date_created TEXT NOT NULL,
                    last_modified TEXT NOT NULL,
                    last_opened TEXT,
                    version INTEGER DEFAULT 1
                )
            """;

            try (Statement stmt = connection.createStatement()) {
                stmt.execute(createTableSQL);
                System.out.println(" Database initialized at: " + DB_PATH);
            }

        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Find project by name in database
     */
    public Project findProjectByName(String name) {
        String sql = "SELECT * FROM projects WHERE name = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String fileLocation = rs.getString("file_location");
                String docLocation = rs.getString("doc_location");

                String code = "";
                String documentation = "";

                if (fileLocation != null && new File(fileLocation).exists()) {
                    code = new String(Files.readAllBytes(Paths.get(fileLocation)));
                }

                if (docLocation != null && new File(docLocation).exists()) {
                    documentation = new String(Files.readAllBytes(Paths.get(docLocation)));
                }

                Project project = new Project(name, code, documentation);
                project.setId(rs.getInt("id"));
                project.setFileLocation(fileLocation);
                project.setDocLocation(docLocation);
                project.setCodeSize(rs.getInt("code_size"));
                project.setVersion(rs.getInt("version"));

                if (rs.getString("date_created") != null) {
                    project.setDateCreated(LocalDateTime.parse(rs.getString("date_created")));
                }
                if (rs.getString("last_modified") != null) {
                    project.setLastModified(LocalDateTime.parse(rs.getString("last_modified")));
                }

                return project;
            }

        } catch (SQLException | IOException e) {
            System.err.println("Failed to find project: " + e.getMessage());
        }

        return null;
    }

    /**
     * Save project - creates file and stores location in database
     */
    public void saveProject(Project project) {
        try {
            String safeName = project.getName().replaceAll("[^a-zA-Z0-9.-]", "_");
            String timestamp = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            String filePath = STORAGE_PATH + safeName + "_" + timestamp + ".java";
            String docPath = STORAGE_PATH + safeName + "_" + timestamp + ".doc";

            Files.write(Paths.get(filePath), project.getCode().getBytes());
            Files.write(Paths.get(docPath), project.getDocumentation().getBytes());

            System.out.println(" Files saved to:");
            System.out.println("   Code: " + filePath);
            System.out.println("   Doc:  " + docPath);

            Project existing = findProjectByName(project.getName());
            if (existing != null) {
                deleteFileIfExists(existing.getFileLocation());
                deleteFileIfExists(existing.getDocLocation());
                updateProjectRecord(project, filePath, docPath);
            } else {
                insertProjectRecord(project, filePath, docPath);
            }

        } catch (IOException e) {
            System.err.println("Failed to save project files: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void insertProjectRecord(Project project, String filePath, String docPath) {
        String sql = """
            INSERT INTO projects 
            (name, file_location, doc_location, code_size, date_created, last_modified, version)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, project.getName());
            pstmt.setString(2, filePath);
            pstmt.setString(3, docPath);
            pstmt.setInt(4, project.getCode().length());
            pstmt.setString(5, LocalDateTime.now().toString());
            pstmt.setString(6, LocalDateTime.now().toString());
            pstmt.setInt(7, 1);

            pstmt.executeUpdate();
            System.out.println(" Database record created for: " + project.getName());

        } catch (SQLException e) {
            System.err.println("Failed to insert record: " + e.getMessage());
        }
    }

    private void updateProjectRecord(Project project, String filePath, String docPath) {
        String sql = """
            UPDATE projects 
            SET file_location = ?, doc_location = ?, code_size = ?, last_modified = ?, 
                version = version + 1
            WHERE name = ?
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, filePath);
            pstmt.setString(2, docPath);
            pstmt.setInt(3, project.getCode().length());
            pstmt.setString(4, LocalDateTime.now().toString());
            pstmt.setString(5, project.getName());

            pstmt.executeUpdate();
            System.out.println(" Database record updated for: " + project.getName() + " (version +1)");

        } catch (SQLException e) {
            System.err.println("Failed to update record: " + e.getMessage());
        }
    }

    /**
     * Load project - reads file from location stored in database
     */
    public Project loadProject(String projectName) {
        return findProjectByName(projectName);
    }

    /**
     * Get all projects from database (metadata only)
     */
    public List<ProjectMetadata> getAllProjectMetadata() {
        List<ProjectMetadata> projects = new ArrayList<>();
        String sql = "SELECT id, name, file_location, doc_location, code_size, date_created, last_modified, last_opened, version FROM projects ORDER BY last_modified DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ProjectMetadata metadata = new ProjectMetadata(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("file_location"),
                        rs.getString("doc_location"),
                        rs.getInt("code_size"),
                        rs.getString("date_created"),
                        rs.getString("last_modified"),
                        rs.getString("last_opened"),
                        rs.getInt("version")
                );
                projects.add(metadata);
            }

        } catch (SQLException e) {
            System.err.println("Failed to get project list: " + e.getMessage());
        }

        return projects;
    }

    /**
     * Delete project - removes files and database record
     */
    public void deleteProject(String projectName) {
        try {
            Project project = findProjectByName(projectName);
            if (project != null) {
                deleteFileIfExists(project.getFileLocation());
                deleteFileIfExists(project.getDocLocation());
            }

            String deleteSQL = "DELETE FROM projects WHERE name = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
                pstmt.setString(1, projectName);
                pstmt.executeUpdate();
            }

            System.out.println("🗑 Deleted project: " + projectName);

        } catch (SQLException e) {
            System.err.println("Failed to delete project: " + e.getMessage());
        }
    }

    /**
     * Search projects by name
     */
    public List<ProjectMetadata> searchProjects(String searchTerm) {
        List<ProjectMetadata> results = new ArrayList<>();
        String sql = "SELECT * FROM projects WHERE name LIKE ? ORDER BY last_modified DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            String pattern = "%" + searchTerm + "%";
            pstmt.setString(1, pattern);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                results.add(new ProjectMetadata(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("file_location"),
                        rs.getString("doc_location"),
                        rs.getInt("code_size"),
                        rs.getString("date_created"),
                        rs.getString("last_modified"),
                        rs.getString("last_opened"),
                        rs.getInt("version")
                ));
            }

        } catch (SQLException e) {
            System.err.println("Search failed: " + e.getMessage());
        }

        return results;
    }

    /**
     * Check if project exists
     */
    public boolean projectExists(String name) {
        String sql = "SELECT 1 FROM projects WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Export database to CSV (for teacher requirement)
     */
    public void exportToCSV(String outputPath) {
        String sql = "SELECT id, name, code_size, date_created, last_modified, version FROM projects ORDER BY id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql);
             PrintWriter writer = new PrintWriter(outputPath)) {

            writer.println("ID,Name,Code Size(bytes),Date Created,Last Modified,Version");

            while (rs.next()) {
                writer.printf("%d,%s,%d,%s,%s,%d%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("code_size"),
                        rs.getString("date_created"),
                        rs.getString("last_modified"),
                        rs.getInt("version")
                );
            }

            System.out.println(" Database exported to: " + outputPath);

        } catch (SQLException | IOException e) {
            System.err.println("Export failed: " + e.getMessage());
        }
    }

    /**
     * Print database contents to console (for debugging)
     */
    public void printDatabaseContents() {
        System.out.println("\n=== DATABASE CONTENTS ===");
        String sql = "SELECT id, name, code_size, date_created, last_modified, version FROM projects";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.printf("%-3s | %-20s | %-10s | %-20s | %-20s | %-6s%n",
                    "ID", "NAME", "SIZE", "CREATED", "LAST MODIFIED", "VERSION");
            System.out.println("—".repeat(95));

            while (rs.next()) {
                System.out.printf("%-3d | %-20s | %-8d | %-20s | %-20s | %-6d%n",
                        rs.getInt("id"),
                        truncate(rs.getString("name"), 20),
                        rs.getInt("code_size"),
                        rs.getString("date_created"),
                        rs.getString("last_modified"),
                        rs.getInt("version")
                );
            }

        } catch (SQLException e) {
            System.err.println("Failed to print database: " + e.getMessage());
        }
        System.out.println("========================\n");
    }

    private void deleteFileIfExists(String filePath) {
        if (filePath != null) {
            try {
                Files.deleteIfExists(Paths.get(filePath));
                System.out.println("   Deleted file: " + filePath);
            } catch (IOException e) {
                System.err.println("   Failed to delete: " + filePath);
            }
        }
    }

    private String truncate(String str, int length) {
        if (str == null) return "null";
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database: " + e.getMessage());
        }
    }

    // Metadata class for lightweight project info
    public static class ProjectMetadata {
        public int id;
        public String name;
        public String fileLocation;
        public String docLocation;
        public int codeSize;
        public String dateCreated;
        public String lastModified;
        public String lastOpened;
        public int version;

        public ProjectMetadata(int id, String name, String fileLocation, String docLocation,
                               int codeSize, String dateCreated, String lastModified,
                               String lastOpened, int version) {
            this.id = id;
            this.name = name;
            this.fileLocation = fileLocation;
            this.docLocation = docLocation;
            this.codeSize = codeSize;
            this.dateCreated = dateCreated;
            this.lastModified = lastModified;
            this.lastOpened = lastOpened;
            this.version = version;
        }
    }
}