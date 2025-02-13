package com.shifter.shifter_back.services;

import com.shifter.shifter_back.exceptions.ResourceNotFoundException;
import com.shifter.shifter_back.models.Category;
import com.shifter.shifter_back.models.Question;
import com.shifter.shifter_back.models.User;
import com.shifter.shifter_back.repositories.CategoryRepository;
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
public class CategoryService implements EntityInterface<Category> {
    private final CategoryRepository categoryRepository;
    private Utils utils;

    @Override
    public Optional<Category> findEntityById(User user, Long id) {
        List<Category> categories = findAllEntity(user);
        Optional<Category> cat = Optional.of(new Category());

//        return categories.isEmpty() ? Optional.empty() : Optional.of(categories.get(0));
        return categories.stream()
                .filter(category -> Objects.equals(category.getId(), id))
                .findFirst();
    }

    @Override
    public List<Category> findAllEntity(User user) {
        Category category = new Category();
        return findFilterEntity(user, category);
    }

    @Override
    public List<Category> findFilterEntity(User user, Category category) {
        category.setCreatedBy(user.getId());
        Map<String, Object> nonNullElements = utils.getNonNullProperties(Category.class, category);
        nonNullElements.put("createdBy", user.getId());
        nonNullElements.put("isVisible", true);

        return categoryRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            for (Map.Entry<String, Object> entry : nonNullElements.entrySet()) {
                String fieldName =entry.getKey();
                Object  fieldValue = entry.getValue();

                if (fieldName.equals("questions")) {
                    Join<Category, Question> join = root.join(fieldName, JoinType.INNER);
                    predicates.add(join.get("id").in((List<Long>) fieldValue));
                } else {
                    predicates.add(criteriaBuilder.equal(root.get(fieldName), fieldValue));
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }

    @Override
    public Category addEntity(User user, Category category) {
        try {
            category.setCreatedAt(Calendar.getInstance().getTime());
            category.setCreatedBy(user.getId());
            return categoryRepository.save(category);
        } catch (Exception e) {
            throw new RuntimeException("Error adding new category: " + e.getMessage(), e);
        }
    }

    @Override
    public Category updateEntity(User user, Category category) {
        Optional<Category> previousElement = findEntityById(user, category.getId());

        if (previousElement.isPresent()) {
            previousElement.get().setUpdatedAt(Calendar.getInstance().getTime());
            previousElement.get().setUpdatedBy(user.getId());
            Map<String, Object> nonNullElements = utils.getNonNullProperties(Category.class, category);

            nonNullElements.forEach((fieldName, value) -> {
                try {
                    Field field = Category.class.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(previousElement.get(), value);
                } catch (NoSuchFieldException | IllegalAccessException e){
                    System.out.println("Error updating field: " + e.getMessage());
                    throw new RuntimeException("Error updating field '" + fieldName + "': " + e.getMessage(), e);
                }
            });
            return categoryRepository.save(previousElement.get());
        } else {
            throw new RuntimeException("Category with ID " + category.getId() + " not found for update.");
        }
    }

    @Override
    public void deleteEntity(User user, Long id) {
        try {
            Optional<Category> category = findEntityById(user, id);
            if (category.isEmpty()) {
                throw new ResourceNotFoundException("Category not found for id: " + id);
            }
            category.get().setVisible(false);
            updateEntity(user, category.get());
        } catch (Exception e) {
            throw new RuntimeException("Error deleting category with ID " + id + ": " + e.getMessage(), e);
        }
    }
}
