package org.yaml.snakeyaml.yamlrecords;

import java.io.InputStream;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MapFieldTest {

    record Entries(Map<String, String> entries){}

    @Test
    void ageOptionalWorks() throws Exception {
        Yaml yaml = new Yaml(new RecordConstructor(Entries.class));
        try (final InputStream is = getClass().getClassLoader().getResourceAsStream("examples/map.yml")) {

            Entries entries = yaml.load(is);
            final Map<String, String> e = entries.entries();

            assertEquals(2, e.size());
            assertEquals("value01", e.get("key01"));
            assertEquals("value02", e.get("key02"));
        }
    }
}
