package models;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import customexceptions.StorageException;

public class FileStorage<T> implements Storage<T> {

    private final StorageConfig config;

    /**
     * Creates a new FileStorage with default configuration
     */
    public FileStorage() {
        this(new StorageConfig());
    }

    /**
     * Creates a new FileStorage with custom configuration
     * 
     * @param config Storage configuration
     */
    public FileStorage(StorageConfig config) {
        this.config = config;
    }

    /**
     * Gets the file path for a given model
     * 
     * @param model The model name
     * @return Path object representing the file path
     */
    private Path getFilePath(String model) {
        return config.getPathForModel(model);
    }
    
    /**
     * Ensures the data directory exists
     */
    private void ensureDirectoryExists() {
        File directory = new File(config.getDataDirectory());
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("Created data directory: " + config.getDataDirectory());
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void add(Map<String, Object> map) {
        String model = map.get("model").toString();
        T obj = (T) map.get("obj");
        String fileHeader = map.get("fileHeader").toString();
        Path filePath = getFilePath(model);
        
        ensureDirectoryExists();

        try {
            boolean fileExists = Files.exists(filePath);

            // If file doesn't exist, write the header first
            if (!fileExists) {
                try (BufferedWriter writer = Files.newBufferedWriter(filePath, 
                        StandardCharsets.UTF_8)) {
                    writer.write(fileHeader);
                    writer.newLine();
                }
            }

            // Append the new line to the file using BufferedWriter for better performance
            try (BufferedWriter writer = Files.newBufferedWriter(filePath, 
                    StandardCharsets.UTF_8, 
                    StandardOpenOption.APPEND)) {
                writer.write(obj.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new StorageException("Error adding record", e);
        }
    }

    @Override
    public String get(String model, String id) {
        Path filePath = getFilePath(model);
        if (!Files.exists(filePath)) {
            return "";
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(id + ",")) {
                    return line;
                }
            }
            return "";
        } catch (IOException e) {
            throw new StorageException("Error retrieving record with ID: " + id, e);
        }
    }

    @Override
    public void remove(String model, String id) {
        Path filePath = getFilePath(model);
        if (!Files.exists(filePath)) {
            return;
        }
        
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            List<String> updatedLines = lines.stream()
                    .filter(line -> !line.startsWith(id + ","))
                    .collect(Collectors.toList());
            
            // Use BufferedWriter for better performance
            try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
                for (String line : updatedLines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new StorageException("Error removing record with ID: " + id, e);
        }
    }

    @Override
    public void update(String model, String id, T obj) {
        Path filePath = getFilePath(model);
        if (!Files.exists(filePath)) {
            throw new StorageException("File not found for model: " + model);
        }
        
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            boolean found = false;
            
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith(id + ",")) {
                    lines.set(i, obj.toString());
                    found = true;
                    break;
                }
            }
            
            if (!found) {
                throw new StorageException("Record with ID " + id + " not found");
            }
            
            // Use BufferedWriter for better performance
            try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new StorageException("Error updating record with ID: " + id, e);
        }
    }

    @Override
    public List<String> getAll(String model) {
        Path filePath = getFilePath(model);
        if (!Files.exists(filePath)) {
            return Collections.emptyList();
        }
        
        try {
            return Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new StorageException("Error retrieving all records for model: " + model, e);
        }
    }

    @Override
    public void batchAdd(String model, List<T> objects, String fileHeader) {
        if (objects == null || objects.isEmpty()) {
            return;
        }
        
        Path filePath = getFilePath(model);
        ensureDirectoryExists();
        
        try {
            boolean fileExists = Files.exists(filePath);
            
            try (BufferedWriter writer = Files.newBufferedWriter(filePath, 
                    StandardCharsets.UTF_8, 
                    fileExists ? StandardOpenOption.APPEND : StandardOpenOption.CREATE)) {
                    
                // Write header if new file
                if (!fileExists) {
                    writer.write(fileHeader);
                    writer.newLine();
                }
                
                // Write all objects in a single operation
                for (T obj : objects) {
                    writer.write(obj.toString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new StorageException("Error during batch add operation", e);
        }
    }

    @Override
    public void batchUpdate(String model, Map<String, T> objects) {
        if (objects == null || objects.isEmpty()) {
            return;
        }
        
        Path filePath = getFilePath(model);
        if (!Files.exists(filePath)) {
            throw new StorageException("File not found for model: " + model);
        }
        
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            boolean anyUpdates = false;
            
            // First line is typically the header
            List<String> updatedLines = new ArrayList<>();
            updatedLines.add(lines.get(0));
            
            // Process all other lines
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                String id = line.substring(0, line.indexOf(','));
                
                if (objects.containsKey(id)) {
                    updatedLines.add(objects.get(id).toString());
                    objects.remove(id); // Remove processed object
                    anyUpdates = true;
                } else {
                    updatedLines.add(line);
                }
            }
            
            if (!anyUpdates) {
                throw new StorageException("No matching records found for batch update");
            }
            
            // Write back the updated lines
            try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
                for (String line : updatedLines) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new StorageException("Error during batch update operation", e);
        }
    }

    /**
     * Checks if a value already exists in a specific column
     *
     * @param model The model name
     * @param columnValue The value to check
     * @param columnIndex The column index to check (0-based)
     * @param excludeId Optional ID to exclude from the check (for updates)
     * @return true if the value exists, false otherwise
     */
    public boolean valueExistsInColumn(String model, String columnValue, int columnIndex, String... excludeId) {
        Path filePath = getFilePath(model);
        if (!Files.exists(filePath)) {
            return false;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                // Skip header line
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                String[] columns = line.split(",");
                if (columns.length > columnIndex) {
                    // Check if this row should be excluded (for update operations)
                    if (excludeId.length > 0 && columns[0].equals(excludeId[0])) {
                        continue;
                    }
                    
                    // Compare the column value (trimmed)
                    if (columns[columnIndex].trim().equalsIgnoreCase(columnValue.trim())) {
                        return true; // Value exists
                    }
                }
            }
            return false; // Value not found
        } catch (IOException e) {
            throw new StorageException("Error checking if value exists: " + columnValue, e);
        }
    }
}
