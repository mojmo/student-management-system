package models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public String get(String model, String id) {
        String fileName = "data/" + model + ".csv";
        Path filePath = Paths.get(fileName).toAbsolutePath();
        String line, objectLine = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            while ((line = reader.readLine()) != null) {
                if (line.contains(id)) {
                    objectLine = line;
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return objectLine;
    }
    @Override
    public void remove(String model, String id) {
        String fileName = "data/" + model + ".csv";
        Path filePath = Paths.get(fileName).toAbsolutePath();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath.toUri()));
            List<String> updatedLines = lines.stream()
                    .filter(line -> !line.startsWith(id + ","))
                    .collect(Collectors.toList());
            Files.write(Paths.get(filePath.toUri()), updatedLines);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void update(String model, String id, T obj) {
        String fileName = "data/" + model + ".csv";
        Path filePath = Paths.get(fileName).toAbsolutePath();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePath.toUri()));
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.startsWith(id + ",")) {
                    lines.set(i, obj.toString());
                    System.out.println(lines.get(i));
                    break;
                }
            }
            Files.write(filePath, lines);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public List<String> getAll(String model) {
        String fileName = "data/" + model + ".csv";
        Path filePath = Paths.get(fileName).toAbsolutePath();
        List<String> lines = List.of();

        try {
            lines = Files.readAllLines(Paths.get(filePath.toUri()));
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return lines;
    }
}
