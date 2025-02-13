package com.shifter.shifter_back.utils;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

@Component
@AllArgsConstructor
public class Utils {

    private final EntityManager entityManager;

    /**
     * Extract non-null properties of an entity object along with their values.
     */
    public Map<String, Object> getNonNullProperties(Class<?> entityClass, Object entityValues) {
        Field[] fields = entityClass.getDeclaredFields();
        Map<String, Object> nonNullProperties = new HashMap<>();

        for (Field field : fields) {
            field.setAccessible(true);

            try {
                Object value = field.get(entityValues);
                if (value != null) {
                    if (!field.getName().equals("id")) {
                        if (field.getType() == double.class || field.getType() == int.class) {
                            if ((double) value > 0) {
                                nonNullProperties.put(field.getName(), value);
                            }
                        } else {
                            nonNullProperties.put(field.getName(), value);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println("nonNullProperties: " + nonNullProperties);
        return nonNullProperties;
    }
}
