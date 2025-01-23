package com.shifter.shifter_back.services;

import com.shifter.shifter_back.models.Category;
import com.shifter.shifter_back.models.Question;
import com.shifter.shifter_back.models.User;
import com.shifter.shifter_back.repositories.CategoryRepository;
import com.shifter.shifter_back.repositories.QuestionRepository;
import com.shifter.shifter_back.utils.Utils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class QuestionService implements EntityInterface<Question> {
    private final QuestionRepository questionRepository;
    private Utils utils;

    private CategoryService categoryService;


    @Override
    public Optional<Question> findEntityById(Long id) {
        Question question = new Question();
        question.setId(id);
        Map<String, Object> nonNullElements = utils.getNonNullProperties(Question.class, question);
        List<Question> categories = utils.findAllByCustomQuery(nonNullElements, Question.class);
        return categories.isEmpty() ? Optional.empty() : Optional.of(categories.get(0));
    }

    @Override
    public List<Question> findAllEntity(User user) {
        Question question = new Question();
        Map<String, Object> nonNullElements = utils.getNonNullProperties(Question.class, question);
        return utils.findAllByCustomQuery(nonNullElements, Question.class);
    }

    @Override
    public List<Question> findFilterEntity(User user, Question question) {
        question.setVisible(true);
        Map<String, Object> nonNullElements = utils.getNonNullProperties(Question.class, question);

        // Retrieve category
        if (nonNullElements.containsKey("category")) {
            Optional<Category> category = categoryService.findEntityById(question.getCategory().getId());
            if (category.isEmpty()) {
                throw new EntityNotFoundException("Category not found for ID: " + question.getCategory().getId());
            }
            nonNullElements.replace("category", category.get());
        }
        return utils.findAllByCustomQuery(nonNullElements, Question.class);
    }

    @Override
    public Question addEntity(User user, Question question) {
        try {
            question.setCreatedAt(Calendar.getInstance().getTime());
            question.setCreatedBy(user.getCreatedBy());
            Map<String, Object> nonNullElements = utils.getNonNullProperties(Question.class, question);
            // Retrieve category
            if (nonNullElements.containsKey("category")) {
                Optional<Category> category = categoryService.findEntityById(question.getCategory().getId());
                if (category.isEmpty()) {
                    throw new EntityNotFoundException("Category not found for ID: " + question.getCategory().getId());
                }
                nonNullElements.replace("category", category.get());
            } else {
                throw new EntityNotFoundException("Category not specified");
            }
            return questionRepository.save(question);
        } catch (Exception e) {
            throw new RuntimeException("Error adding new question: " + e.getMessage(), e);
        }
    }

    @Override
    public Question updateEntity(User user, Question question) {
        Optional<Question> previousElement = findEntityById(question.getId());

        if (previousElement.isPresent()) {
            previousElement.get().setUpdatedAt(Calendar.getInstance().getTime());
            previousElement.get().setUpdatedBy(user.getId());
            Map<String, Object> nonNullElements = utils.getNonNullProperties(Question.class, question);

            // Retrieve category
            if (nonNullElements.containsKey("category")) {
                Optional<Category> category = categoryService.findEntityById(question.getCategory().getId());
                if (category.isEmpty()) {
                    throw new EntityNotFoundException("Category not found for ID: " + question.getCategory().getId());
                }
                nonNullElements.replace("category", category.get());
            }

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
