package models;

import java.util.List;
import java.util.Map;

public interface Storage<T> {
    void add(Map<String, Object> map);
    String get(String model, String id);
    void remove(String model, String id);
    void update(String model, String id, T obj);
    List<String> getAll(String model);
}
