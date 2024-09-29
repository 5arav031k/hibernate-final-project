package com.javarush.repository;

import java.util.List;

public interface CrudRepository<T, ID> {

    long getCount();

    List<T> getAll();

    T getById(final ID id);

    T save(final T entity);
}
