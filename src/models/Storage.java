package models;

import java.util.Map;

public interface Storage<T> {
    void add(Map<String, Object> map);
    T get(String model, String id);
    void remove(String model, String id);
    void update(String model, String id, T obj);
}
