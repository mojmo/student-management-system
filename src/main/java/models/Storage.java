package models;

import java.util.List;
import java.util.Map;

public interface Storage<T> {

    /**
     * Add an object to storage
     * @param map A map containing the object data, where keys are field names and values are field values
     */
    void add(Map<String, Object> map);

    /**
     * Retrieve an object from storage
     * @param model The model name
     * @param id The ID of the object to retrieve
     * @return The object as a String, or empty String if not found
     */
    String get(String model, String id);

    /**
     * Remove an object from storage
     * @param model The model name
     * @param id The ID of the object to remove
     */
    void remove(String model, String id);

    /**
     * Update an object in storage
     * @param model The model name
     * @param id The ID of the object to update
     * @param obj The object to update
     */
    void update(String model, String id, T obj);

    /**
     * Retrieve all objects from storage
     * @param model The model name
     * @return List of all objects in storage for the given model
     */
    List<String> getAll(String model);
    
    /**
     * Batch adds multiple objects to storage
     * 
     * @param model The model name
     * @param objects List of objects to add
     * @param fileHeader The file header
     */
    void batchAdd(String model, List<T> objects, String fileHeader);
    
    /**
     * Batch updates multiple objects in storage
     * 
     * @param model The model name
     * @param objects Map of ID to object
     */
    void batchUpdate(String model, Map<String, T> objects);
}
