package org.yaml.snakeyaml.yamlrecords;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RecordUtils {

    @SuppressWarnings("unchecked")
    public static <T extends Record> T instantiateRecord(Class<T> recordClass, Map<String, Object> values) {
        if (!isRecord(recordClass)) {
            throw new IllegalArgumentException(recordClass + " is not a record");
        }

        try {
            final RecordComponent[] components = recordClass.getRecordComponents();

            final Class<?>[] paramTypes = Arrays.stream(components)
                    .map(RecordComponent::getType)
                    .toArray(Class[]::new);

            final Object[] args = Arrays.stream(components)
                .map(rc -> convertValue(rc.getType(), values.get(rc.getName()), rc.getGenericType()))
                .toArray(Object[]::new );

            return recordClass.getDeclaredConstructor(paramTypes).newInstance(args);

        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Failed to instantiate record " + recordClass.getName(), e);
        }
    }

    private static Object convertValue(Class<?> type, Object value, Type genericType) {
        if (value == null && type.isPrimitive()) {
            throw new IllegalArgumentException("Missing value for primitive field of type " + type.getName());
        }

        if (isOptional(type)) {
            Type wrappedType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
            Class<?> wrappedClass = (Class<?>) wrappedType;
            Object inner = (value == null) ? null : convertValue(wrappedClass, value, wrappedType);
            return Optional.ofNullable(inner);
        }

        if (isRecord(type) && value instanceof Map<?, ?> mapValue) {
            return instantiateRecord((Class<? extends Record>)type, (Map<String, Object>) mapValue);
        }

        if ((List.class.isAssignableFrom(type) || Set.class.isAssignableFrom(type)) && value instanceof Collection<?> collection) {
            Type elementType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
            Class<?> elementClass = (Class<?>) elementType;

            final Collection<Object> result = type == List.class ? new ArrayList<>() : new HashSet<>();
            collection.stream()
                .map(item -> convertValue(elementClass, item, elementType))
                .forEach(result::add);
            return result;
        }

        if (Map.class.isAssignableFrom(type) && value instanceof Map<?, ?> mapValue) {
            final Type[] typeArgs = ((ParameterizedType) genericType).getActualTypeArguments();
            final Class<?> keyType = (Class<?>) typeArgs[0];
            final Class<?> valType = (Class<?>) typeArgs[1];

            return mapValue.entrySet().stream()
                .collect(Collectors.toMap(
                    entry -> convertSimple(keyType, entry.getKey()),
                    entry -> convertValue(valType, entry.getValue(), valType)));
        }

        return convertSimple(type, value);
    }

    private static boolean isOptional(Class<?> clazz) {
        return clazz == Optional.class;
    }

    private static Object convertSimple(Class<?> targetType, Object value) {
        if (value == null) return null;

        if (targetType.isInstance(value)) return value;

        if (targetType == String.class) return value.toString();

        if (targetType == int.class || targetType == Integer.class) {
            if (value instanceof Number n) return n.intValue();
            if (value instanceof String s) return Integer.valueOf(s);
        }

        if (targetType == long.class || targetType == Long.class) {
            if (value instanceof Number n) return n.longValue();
            if (value instanceof String s) return Long.valueOf(s);
        }

        if (targetType == double.class || targetType == Double.class) {
            if (value instanceof Number n) return n.doubleValue();
            if (value instanceof String s) return Double.valueOf(s);
        }

        if (targetType == float.class || targetType == Float.class) {
            if (value instanceof Number n) return n.floatValue();
            if (value instanceof String s) return Float.valueOf(s);
        }

        if (targetType == boolean.class || targetType == Boolean.class) {
            if (value instanceof Boolean b) return b;
            if (value instanceof String s) return Boolean.valueOf(s);
        }

        if (targetType.isEnum()) {
            if (value instanceof String s) {
                return Enum.valueOf((Class<Enum>) targetType, s);
            }
        }
        throw new IllegalArgumentException("Cannot convert value: " + value + " to type " + targetType.getName());
    }

    public static boolean isRecord(final Class<?> targetType) {
        // Ideally we should use Class.isRecord() here. However, Android desugaring always return false.
        return targetType != null && Record.class.isAssignableFrom(targetType);
    }
}
