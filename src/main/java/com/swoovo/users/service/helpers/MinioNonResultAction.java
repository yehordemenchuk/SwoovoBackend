package com.swoovo.users.service.helpers;

@FunctionalInterface
public interface MinioNonResultAction<T> {
    void execute(T arg) throws Exception;
}
