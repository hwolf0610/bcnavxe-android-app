package com.crittermap.backcountrynavigator.xe.controller.database.dao;


import java.util.List;

interface ICommonDAO<T> {
    String insertOrUpdate(T object) throws IllegalArgumentException, IllegalAccessException;

    void deleteAll();

    List<T> getAll() throws NoSuchFieldException, IllegalAccessException;

    long count() throws NoSuchFieldException, IllegalAccessException;

    List<T> findByField(String fieldName, String value) throws NoSuchFieldException, IllegalAccessException;

    T findById(String id) throws NoSuchFieldException, IllegalAccessException;

    int delete(String id);
}
