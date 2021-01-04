package com.crittermap.backcountrynavigator.xe.core.data;

public interface SharedPrefRepository<T> {
    Boolean persist(T data);

    T get();
}
