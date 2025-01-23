package com.shifter.shifter_back.services;

import com.shifter.shifter_back.models.User;
import com.shifter.shifter_back.models.User;
import com.shifter.shifter_back.repositories.UserRepository;
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
public class UserService implements EntityInterface<User> {
    private final UserRepository userRepository;
    private Utils utils;

    @Override
    public Optional<User> findEntityById(Long id) {
        User user = new User();
        user.setId(id);
        Map<String, Object> nonNullElements = utils.getNonNullProperties(User.class, user);
        List<User> categories = utils.findAllByCustomQuery(nonNullElements, User.class);
        return categories.isEmpty() ? Optional.empty() : Optional.of(categories.get(0));
    }

    @Override
    public List<User> findAllEntity(User authUser) {
        User user = new User();
        Map<String, Object> nonNullElements = utils.getNonNullProperties(User.class, user);
        return utils.findAllByCustomQuery(nonNullElements, User.class);
    }

    @Override
    public List<User> findFilterEntity(User authUser, User user) {
        user.setVisible(true);
        Map<String, Object> nonNullElements = utils.getNonNullProperties(User.class, user);
        return utils.findAllByCustomQuery(nonNullElements, User.class);
    }

    @Override
    public User addEntity(User authUser, User user) {
        try {
            user.setCreatedAt(Calendar.getInstance().getTime());
            user.setCreatedBy(authUser.getCreatedBy());
            return userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error adding new user: " + e.getMessage(), e);
        }
    }

    @Override
    public User updateEntity(User authUser, User user) {
        Optional<User> previousElement = findEntityById(user.getId());

        if (previousElement.isPresent()) {
            previousElement.get().setUpdatedAt(Calendar.getInstance().getTime());
            previousElement.get().setUpdatedBy(authUser.getId());
            Map<String, Object> nonNullElements = utils.getNonNullProperties(User.class, user);

            nonNullElements.forEach((fieldName, value) -> {
                try {
                    Field field = User.class.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(previousElement.get(), value);
                } catch (NoSuchFieldException | IllegalAccessException e){
                    System.out.println("Error updating field: " + e.getMessage());
                    throw new RuntimeException("Error updating field '" + fieldName + "': " + e.getMessage(), e);
                }
            });
            return userRepository.save(previousElement.get());
        } else {
            throw new RuntimeException("User with ID " + user.getId() + " not found for update.");
        }
    }

    @Override
    public void deleteEntity(User authUser, Long id) {
        try {
            User user = new User();
            user.setId(id);
            user.setVisible(false);
            updateEntity(authUser, user);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user with ID " + id + ": " + e.getMessage(), e);
        }
    }
}
