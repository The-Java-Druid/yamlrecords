package org.yaml.snakeyaml.yamlrecords;

import java.io.InputStream;
import org.yaml.snakeyaml.Yaml;
import org.junit.jupiter.api.Test;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OptionalFieldTest {

    record User(String name, Optional<Integer> age) {}

    @Test
    void ageOptionalWorks() throws Exception {
        Yaml yaml = new Yaml(new RecordConstructor(User.class));
        try (final InputStream is = getClass().getClassLoader().getResourceAsStream("examples/optional.yml")) {
            User u1 = yaml.load(is);

            assertEquals("Alied", u1.name());
            assertTrue(u1.age().isPresent());
            assertEquals(42, u1.age().get());
        }
    }

}
