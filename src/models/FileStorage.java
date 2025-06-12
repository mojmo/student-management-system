package models;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class FileStorage<T> implements Storage<T> {

    @Override
    @SuppressWarnings("unchecked")
    public void add(Map<String, Object> map) {
        String model = map.get("model").toString();
        T obj = (T) map.get("obj");
        String fileHeader = map.get("fileHeader").toString();

        String line = obj.toString();
        String fileName = "data/" + model + ".csv";
        Path filePath = Paths.get(fileName).toAbsolutePath();

        try {
            boolean fileExits = Files.exists(filePath);

            // If file doesn't exist, write the header first
            if (!fileExits) {
                Files.write(filePath, List.of(fileHeader), StandardCharsets.UTF_8);
            }

            // Append the new line to the file
            Files.write(
                    filePath,
                    Collections.singletonList(line),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.APPEND
            );

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public T get(String model, String id) {
        // TODO
        return null;
    }
    @Override
    public void remove(String model, String id) {
        // TODO
        System.out.println("Update " + model + " to a file...");
    }

    @Override
    public void update(String model, String id, T obj) {
        // TODO
        System.out.println("Remove " + model + " to a file...");
    }
}
