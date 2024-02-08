package com.joehxblog.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter extends AbstractConverter<LocalDate> {
    
    private final DateTimeFormatter dateTimeFormatter;

    public LocalDateConverter(String format) {
        this(DateTimeFormatter.ofPattern(format));
    }

    public LocalDateConverter(final DateTimeFormatter dateTimeFormatter) {
        super(LocalDate.class);
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public LocalDate fromString(String text) {
        return LocalDate.parse(text, dateTimeFormatter);
    }

    @Override
    public String toString(LocalDate value) {
        return value.format(dateTimeFormatter);
    }
}
