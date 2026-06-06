package com.swoovo.users.service.helpers;

@FunctionalInterface
public interface MinioAction<T, R> {
    R execute(T t) throws Exception;
}
