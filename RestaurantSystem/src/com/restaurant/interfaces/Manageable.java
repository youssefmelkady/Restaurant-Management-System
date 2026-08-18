package com.restaurant.interfaces;

import java.util.List;

public interface Manageable<T> {
    void add(T item);
    void update(T item);
    void delete(String id);
    List<T> getAll();
}
