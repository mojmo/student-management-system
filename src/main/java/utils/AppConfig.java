package utils;

import models.StorageConfig;

/**
 * Application-wide configuration
 */
public class AppConfig {
    private static final String DEFAULT_DATA_DIR = "src/main/resources/data/";
    private static final String DEFAULT_REPORTS_DIR = "src/main/resources/reports/";
    
    private final StorageConfig storageConfig;
    private String reportsDirectory;
    
    // Add singleton implementation
    private static AppConfig instance;

    /**
     * Gets the singleton instance
     */
    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    // Make constructor private
    private AppConfig() {
        this.storageConfig = new StorageConfig(DEFAULT_DATA_DIR, ".csv");
        this.reportsDirectory = DEFAULT_REPORTS_DIR;
    }
    
    /**
     * Gets the storage configuration
     */
    public StorageConfig getStorageConfig() {
        return storageConfig;
    }
    
    /**
     * Gets the reports directory
     */
    public String getReportsDirectory() {
        return reportsDirectory;
    }
    
    /**
     * Sets the reports directory
     */
    public void setReportsDirectory(String reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
    }
    
    /**
     * Ensures all required directories exist
     */
    public void ensureDirectoriesExist() {
        // Ensure data directory exists
        new java.io.File(storageConfig.getDataDirectory()).mkdirs();
        
        // Ensure reports directory exists
        new java.io.File(reportsDirectory).mkdirs();
    }
}