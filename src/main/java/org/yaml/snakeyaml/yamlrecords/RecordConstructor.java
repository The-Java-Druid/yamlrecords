package org.yaml.snakeyaml.yamlrecords;

import java.util.Map;
import java.util.stream.Collectors;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;

public class RecordConstructor extends Constructor {
    public RecordConstructor(Class<? extends Record> rootType) {
        super(rootType, new LoaderOptions());
    }

    @Override
    protected Object constructObject(Node node) {
        final Class<?> targetType = typeTags.get(node.getTag());
        if (isRecord(targetType) && node instanceof MappingNode mappingNode) {
            return constructRecord((Class<? extends Record>) targetType, mappingNode);
        }
        return super.constructObject(node);
    }

    private static boolean isRecord(final Class<?> targetType) {
        // Ideally we should use Class.isRecord() here. However, Android desugaring always return false.
        return targetType != null && Record.class.isAssignableFrom(targetType);
    }

    private Object constructRecord(Class<?extends Record> recordClass, MappingNode node) {
        final Map<String, Object> values = node.getValue().stream()
            .collect(Collectors.toMap(this::getKey, this::getValue));
        return RecordUtils.instantiateRecord(recordClass, values);
    }

    private String getKey(NodeTuple tuple) {
        final Object key = constructObject(tuple.getKeyNode());
        if (!(key instanceof String)) {
            throw new YAMLException("Record keys must be strings: " + key);
        }
        return (String) key;
    }

    private Object getValue(NodeTuple tuple) {
        return constructObject(tuple.getValueNode());
    }

}
