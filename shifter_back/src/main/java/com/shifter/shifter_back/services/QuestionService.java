package com.shifter.shifter_back.services;

import com.shifter.shifter_back.models.Question;
import com.shifter.shifter_back.models.Category;
import com.shifter.shifter_back.models.User;
import com.shifter.shifter_back.repositories.QuestionRepository;
import com.shifter.shifter_back.utils.Utils;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@Service
@Transactional
@AllArgsConstructor
public class QuestionService implements EntityInterface<Question> {
    private final QuestionRepository questionRepository;
    private Utils utils;

    @Override
    public Optional<Question> findEntityById(User user, Long id) {
        Question question = new Question();
        question.setId(id);
        question.setVisible(true);
        Map<String, Object> nonNullElements = utils.getNonNullProperties(Question.class, question);
        List<Question> categories = findFilterEntity(user, question);
        return categories.isEmpty() ? Optional.empty() : Optional.of(categories.get(0));
    }

    @Override
    public List<Question> findAllEntity(User user) {
        Question question = new Question();
        question.setVisible(true);
        return findFilterEntity(user, question);
    }

    @Override
    public List<Question> findFilterEntity(User user, Question question) {
        question.setCreatedBy(user.getId());
        Map<String, Object> nonNullElements = utils.getNonNullProperties(Question.class, question);

        return questionRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, Object> entry : nonNullElements.entrySet()) {
                String fieldName =entry.getKey();
                Object  fieldValue = entry.getValue();

                predicates.add(criteriaBuilder.equal(root.get(fieldName), fieldValue));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    @Override
    public Question addEntity(User user, Question question) {
        try {
            question.setCreatedAt(Calendar.getInstance().getTime());
            question.setCreatedBy(user.getId());
            return questionRepository.save(question);
        } catch (Exception e) {
            throw new RuntimeException("Error adding new question: " + e.getMessage(), e);
        }
    }

    @Override
    public Question updateEntity(User user, Question question) {
        Optional<Question> previousElement = findEntityById(user, question.getId());

        if (previousElement.isPresent()) {
            previousElement.get().setUpdatedAt(Calendar.getInstance().getTime());
            previousElement.get().setUpdatedBy(user.getId());
            Map<String, Object> nonNullElements = utils.getNonNullProperties(Question.class, question);

            nonNullElements.forEach((fieldName, value) -> {
                try {
                    Field field = Question.class.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(previousElement.get(), value);
                } catch (NoSuchFieldException | IllegalAccessException e){
                    System.out.println("Error updating field: " + e.getMessage());
                    throw new RuntimeException("Error updating field '" + fieldName + "': " + e.getMessage(), e);
                }
            });
            return questionRepository.save(previousElement.get());
        } else {
            throw new RuntimeException("Question with ID " + question.getId() + " not found for update.");
        }
    }

    @Override
    public void deleteEntity(User user, Long id) {
        try {
            Question question = new Question();
            question.setId(id);
            question.setVisible(false);
            updateEntity(user, question);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting question with ID " + id + ": " + e.getMessage(), e);
        }
    }
}
