[![Java CI with Maven](https://github.com/The-Java-Druid/yamlrecords/actions/workflows/maven.yml/badge.svg)](https://github.com/The-Java-Druid/yamlrecords/actions/workflows/maven.yml)

[![Publish package to GitHub Packages](https://github.com/The-Java-Druid/yamlrecords/actions/workflows/release-package.yml/badge.svg)](https://github.com/The-Java-Druid/yamlrecords/actions/workflows/release-package.yml)

# Record-YAML-Snakeyaml

Custom SnakeYAML Constructor and RecordUtils for deserializing Java records.

## Features

- Instantiate records (JDK 17+) via SnakeYAML
- Supports nested records, collections, `Map<String, Record>`
- Handles `Optional<T>` and primitive coercion

## Latest version
[![GitHub Release](https://img.shields.io/github/v/release/The-Java-Druid/yamlrecords)](https://github.com/The-Java-Druid/yamlrecords/releases/latest)


## Usage
```xml
<dependency>
  <groupId>org.yaml.snakeyaml</groupId>
  <artifactId>yamlrecords</artifactId>
  <version>1.0.6</version>
</dependency>
```
```java
Yaml yaml = new Yaml(new RecordConstructor(Question.class));
Question q = yaml.load(new FileReader("examples/question.yaml"));
```
