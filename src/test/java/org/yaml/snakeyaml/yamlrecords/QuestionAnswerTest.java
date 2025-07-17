package org.yaml.snakeyaml.yamlrecords;

import org.yaml.snakeyaml.Yaml;
import org.junit.jupiter.api.Test;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QuestionAnswerTest {
    enum Category {WORKOUT}

    record Answer(String text, int score, long order, float probability, double weight, boolean top) {}

    record Question(String title, Category category, List<Answer> answers) {}

    @Test
    void testDeserializeQuestion() throws Exception {
        Yaml yaml = new Yaml(new RecordConstructor(Question.class));
        try (final InputStream is = getClass().getClassLoader().getResourceAsStream("examples/question.yml")) {
            final Question q = yaml.load(is);

            assertEquals("Workout survey", q.title());
            assertEquals(Category.WORKOUT, q.category());
            final List<Answer> answers = q.answers();
            assertEquals(2, answers.size());
            final Answer answer1 = answers.get(0);
            assertEquals(5, answer1.score());
            assertEquals("Daily", answer1.text());
            assertEquals(65536L, answer1.order());
            assertEquals(0.1f, answer1.probability());
            assertEquals(0.3d, answer1.weight());
            assertTrue(answer1.top());
            final Answer answer2 = answers.get(1);
            assertEquals(2, answer2.score());
            assertEquals("Sometimes", answer2.text());
            assertEquals(65537L, answer2.order());
            assertEquals(0.2f, answer2.probability());
            assertEquals(0.4d, answer2.weight());
            assertFalse(answer2.top());
        }
    }

}
