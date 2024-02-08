package com.joehxblog.converter;

public interface Converter<E> {

    E fromString(String string);

    String toString(E entity);
}
