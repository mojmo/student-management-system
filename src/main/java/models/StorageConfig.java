package models;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuration class for storage settings
 */
public class StorageConfig {
    private static final String DEFAULT_DATA_DIR = "src/main/resources/data/";
    private static final String DEFAULT_FILE_EXT = ".csv";
    
    private String dataDirectory;
    private String fileExtension;
    
    /**
     * Creates a new StorageConfig with default settings
     */
    public StorageConfig() {
        this(DEFAULT_DATA_DIR, DEFAULT_FILE_EXT);
    }
    
    /**
     * Creates a new StorageConfig with custom settings
     * 
     * @param dataDirectory The directory for data files
     * @param fileExtension The file extension to use
     */
    public StorageConfig(String dataDirectory, String fileExtension) {
        this.dataDirectory = dataDirectory;
        this.fileExtension = fileExtension;
    }
    
    /**
     * Gets the path for storing data for a specific model
     * 
     * @param modelName The name of the model
     * @return The full file path
     */
    public Path getPathForModel(String modelName) {
        return Paths.get(dataDirectory + modelName + fileExtension);
    }
    
    public String getDataDirectory() {
        return dataDirectory;
    }
    
    public void setDataDirectory(String dataDirectory) {
        this.dataDirectory = dataDirectory;
    }
    
    public String getFileExtension() {
        return fileExtension;
    }
    
    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }
}