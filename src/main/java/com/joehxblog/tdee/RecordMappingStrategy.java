package com.joehxblog.tdee;

import com.joehxblog.converter.Converters;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;


public class RecordMappingStrategy<T extends Record> extends HeaderColumnNameMappingStrategy<T> {

    public RecordMappingStrategy(Class<T> type) {
        this.setType(type);
    }

    public T populateNewBean(String[] line) {
        var constructor = this.type.getConstructors()[0];

        var recordComponents = this.type.getRecordComponents();

        var initArgs = Stream.of(recordComponents)
                .map(recordComponent -> {


                    var index = this.headerIndex.getByName(recordComponent.getName())[0];
                    var cell = line[index];

                    if (cell.isBlank()) {
                        return null;
                    } else {
                        var type = recordComponent.getType();
                        var converter = Converters.get(type);

                        return converter.fromString(cell);
                    }
                })
                .toArray();

        T newInstance = null;
        try {
            newInstance = (T) constructor.newInstance(initArgs);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return newInstance;
    }

}
