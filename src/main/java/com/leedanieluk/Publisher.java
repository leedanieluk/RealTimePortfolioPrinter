package com.leedanieluk;

import java.io.IOException;

public interface Publisher<T> {
    void start() throws Exception;
    void subscribe(Subscriber<T> subscriber);
}
