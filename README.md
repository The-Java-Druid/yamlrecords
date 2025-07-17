# Record-YAML-Snakeyaml

Custom SnakeYAML Constructor and RecordUtils for deserializing Java records.

## Features

- Instantiate records (JDK 17+) via SnakeYAML
- Supports nested records, collections, `Map<String, Record>`
- Handles `Optional<T>` and primitive coercion

## Usage

```java
Yaml yaml = new Yaml(new RecordConstructor(Question.class));
Question q = yaml.load(new FileReader("examples/question.yaml"));
