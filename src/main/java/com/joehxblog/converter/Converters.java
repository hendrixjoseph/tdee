package com.joehxblog.converter;

import org.apache.commons.beanutils.BeanUtilsBean;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Converters {

    private static final Map<Class<?>, Converter<?>> DEFAULTS = Stream.of(
            new LocalDateConverter("M/d/yyyy"),
            new AbstractConverter<>(String::new, String.class),
            new AbstractConverter<>(Integer::parseInt, int.class),
            new AbstractConverter<>(Double::parseDouble, Double.class)
    ).collect(Collectors.toMap(
            AbstractConverter::getType,
            c -> c
    ));

    public static <E> Converter<E> get(Class<E> clazz) {
        return (Converter<E>) DEFAULTS.get(clazz);
    }

    public static void loadApacheConverters() {
        var beanUtils = BeanUtilsBean.getInstance().getConvertUtils();

        DEFAULTS.values().stream()
                .filter(converter -> converter instanceof AbstractConverter<?>)
                .map(converter -> (AbstractConverter<?>)converter)
                .forEach(converter -> {
                    var apacheConverter = new org.apache.commons.beanutils.Converter() {

                        @Override
                        public <T> T convert(Class<T> type, Object value) {
                            var string = value.toString();
                            var converted = converter.fromString(string);
                            return (T) converted;
                        }
                    };

                    beanUtils.register(apacheConverter, converter.getType());
                });
    }
}
