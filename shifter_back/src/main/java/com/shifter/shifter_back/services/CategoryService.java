package com.shifter.shifter_back.services;

import com.shifter.shifter_back.models.Category;
import com.shifter.shifter_back.models.User;
import com.shifter.shifter_back.repositories.CategoryRepository;
import com.shifter.shifter_back.utils.Utils;
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
public class CategoryService implements EntityInterface<Category> {
    private final CategoryRepository categoryRepository;
    private Utils utils;

    @Override
    public Optional<Category> findEntityById(Long id) {
        Category category = new Category();
        category.setId(id);
        Map<String, Object> nonNullElements = utils.getNonNullProperties(Category.class, category);
        List<Category> categories = utils.findAllByCustomQuery(nonNullElements, Category.class);
        return categories.isEmpty() ? Optional.empty() : Optional.of(categories.get(0));
    }

    @Override
    public List<Category> findAllEntity(User user) {
        Category category = new Category();
        Map<String, Object> nonNullElements = utils.getNonNullProperties(Category.class, category);
        return utils.findAllByCustomQuery(nonNullElements, Category.class);
    }

    @Override
    public List<Category> findFilterEntity(User user, Category category) {
        category.setVisible(true);
        Map<String, Object> nonNullElements = utils.getNonNullProperties(Category.class, category);
        return utils.findAllByCustomQuery(nonNullElements, Category.class);
    }

    @Override
    public Category addEntity(User user, Category category) {
        try {
            category.setCreatedAt(Calendar.getInstance().getTime());
            category.setCreatedBy(user.getCreatedBy());
            return categoryRepository.save(category);
        } catch (Exception e) {
            throw new RuntimeException("Error adding new category: " + e.getMessage(), e);
        }
    }

    @Override
    public Category updateEntity(User user, Category category) {
        Optional<Category> previousElement = findEntityById(category.getId());

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
            Category category = new Category();
            category.setId(id);
            category.setVisible(false);
            updateEntity(user, category);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting category with ID " + id + ": " + e.getMessage(), e);
        }
    }
}
