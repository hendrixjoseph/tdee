package com.joehxblog.converter;

import java.util.function.Function;

public class AbstractConverter<E> implements Converter<E> {

    private final Function<String, E> converter;
    private final Class<E> clazz;

    protected AbstractConverter(Class<E> clazz) {
        this(null, clazz);
    }

    public AbstractConverter(Function<String, E> converter, Class<E> clazz) {
        this.converter = converter;
        this.clazz = clazz;
    }

    @Override
    public E fromString(String value) {
        return converter.apply(value);
    }

    public String toString(E entity) {
        return entity.toString();
    }

    public Class<E> getType() {
        return this.clazz;
    }
}
