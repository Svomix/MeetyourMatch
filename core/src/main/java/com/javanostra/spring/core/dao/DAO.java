package com.javanostra.spring.core.dao;

import java.util.List;

public interface DAO<T> {
    void save(T entity);
    void update(T entity);
    void delete(int id);
    T findById(int id);
    List<T> findAll();
}
