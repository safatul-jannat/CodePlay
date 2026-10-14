package com.example.codeplayfx.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Project {
    // Database fields
    private int id;
    private String name;
    private String code;
    private String documentation;
    private String fileLocation;      // Path to .java file
    private String docLocation;       // Path to .doc file
    private int codeSize;
    private LocalDateTime dateCreated;
    private LocalDateTime lastModified;
    private LocalDateTime lastOpened;
    private int version;
//    private String tags;
//    private String description;
//    private boolean isFavorite;

    // Constructors
    public Project(String name, String code, String documentation) {
        this.name = name;
        this.code = code;
        this.documentation = documentation;
        this.dateCreated = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
        this.codeSize = code != null ? code.length() : 0;
//        this.tags = "";
//        this.description = "";
//        this.isFavorite = false;
        this.version = 1;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) {
        this.code = code;
        this.codeSize = code != null ? code.length() : 0;
        this.lastModified = LocalDateTime.now();
    }

    public String getDocumentation() { return documentation; }
    public void setDocumentation(String documentation) {
        this.documentation = documentation;
        this.lastModified = LocalDateTime.now();
    }

    public String getFileLocation() { return fileLocation; }
    public void setFileLocation(String fileLocation) { this.fileLocation = fileLocation; }

    public String getDocLocation() { return docLocation; }
    public void setDocLocation(String docLocation) { this.docLocation = docLocation; }

    public int getCodeSize() { return codeSize; }
    public void setCodeSize(int codeSize) { this.codeSize = codeSize; }

    public LocalDateTime getDateCreated() { return dateCreated; }
    public void setDateCreated(LocalDateTime dateCreated) { this.dateCreated = dateCreated; }

    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }

    public LocalDateTime getLastOpened() { return lastOpened; }
    public void setLastOpened(LocalDateTime lastOpened) { this.lastOpened = lastOpened; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

//    public String getTags() { return tags; }
//    public void setTags(String tags) { this.tags = tags; }
//
//    public String getDescription() { return description; }
//    public void setDescription(String description) { this.description = description; }
//
//    public boolean isFavorite() { return isFavorite; }
//    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return lastModified.format(formatter);
    }

    @Override
    public String toString() {
        return name + " (v" + version + ")";
    }
}