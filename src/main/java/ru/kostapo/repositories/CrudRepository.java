package ru.kostapo.repositories;

import java.util.List;
import java.util.Optional;

public interface CrudRepository <T> {

    Optional<List<T>> findAll();

    Optional<T> findById(final Integer id);

    T save (final T entity);

    T update (final T entity);

    void delete(Integer id);
}
