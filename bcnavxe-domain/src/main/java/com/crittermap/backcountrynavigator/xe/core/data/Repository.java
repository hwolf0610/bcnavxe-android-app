package com.crittermap.backcountrynavigator.xe.core.data;

import io.reactivex.Maybe;
import io.reactivex.Single;

interface Repository<T> {
    Maybe<T> findById(String id);

    Single<T> updateOrInsert(T t);
}

