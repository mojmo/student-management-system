package models;

public interface Storage<T> {
    void add(String model, T obj);
    T get(String model, String id);
    void remove(String model, String id);
    void update(String model, String id, T obj);
}
