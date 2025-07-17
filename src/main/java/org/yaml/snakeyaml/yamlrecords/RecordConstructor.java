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
        if (targetType != null && targetType.isRecord() && node instanceof MappingNode mappingNode) {
            return constructRecord(targetType, mappingNode);
        }
        return super.constructObject(node);
    }

    private Object constructRecord(Class<?> recordClass, MappingNode node) {
        final Map<String, Object> values = node.getValue().stream()
            .collect(Collectors.toMap(this::getKey, this::getValue));
        return RecordUtils.instantiateRecord(recordClass, values);
    }

    protected Object getValue(NodeTuple tuple) {
        return constructObject(tuple.getValueNode());
    }

    private String getKey(NodeTuple tuple) {
        final Object key = constructObject(tuple.getKeyNode());
        if (!(key instanceof String)) {
            throw new YAMLException("Record keys must be strings: " + key);
        }
        return (String) key;
    }

}
