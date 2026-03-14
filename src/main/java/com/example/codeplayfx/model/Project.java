package com.example.codeplayfx.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Project {
    private String name;
    private String code;
    private String documentation;  // <-- NEW: Documentation field
    private LocalDateTime dateCreated;
    private LocalDateTime lastModified;

    public Project(String name, String code) {
        this(name, code, "");  // Empty documentation by default
    }

    public Project(String name, String code, String documentation) {
        this.name = name;
        this.code = code;
        this.documentation = documentation;
        this.dateCreated = LocalDateTime.now();
        this.lastModified = LocalDateTime.now();
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) {
        this.code = code;
        this.lastModified = LocalDateTime.now();
    }

    public String getDocumentation() { return documentation; }
    public void setDocumentation(String documentation) {
        this.documentation = documentation;
        this.lastModified = LocalDateTime.now();
    }

    public LocalDateTime getDateCreated() { return dateCreated; }
    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return lastModified.format(formatter);
    }

    @Override
    public String toString() {
        return name;
    }
}