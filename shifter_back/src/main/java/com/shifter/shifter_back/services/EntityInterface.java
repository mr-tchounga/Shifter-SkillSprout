package com.shifter.shifter_back.services;

import com.shifter.shifter_back.models.User;

import java.util.List;
import java.util.Optional;

public interface EntityInterface<T> {
    Optional<T> findEntityById(Long id);
    List<T> findAllEntity(User user);
    List<T> findFilterEntity(User user, T entity);
    T addEntity(User user, T entity);
    T updateEntity(User user, T entity);
    void deleteEntity(User user, Long id);
}
