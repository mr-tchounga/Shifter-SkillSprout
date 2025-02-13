package com.shifter.shifter_back.utils;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Component
@AllArgsConstructor
public class UtilsCopy {

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
        return nonNullProperties;
    }

    /**
     * Find all entities based on dynamically built JPQL query.
     */
    public <T> List<T> findAllByCustomQuery(Map<String, Object> queryParams, Class<T> entityClass) {
        // Start building JPQL query
        String entityName = entityClass.getSimpleName(); // Assume entity class name matches table name
        StringBuilder jpql = new StringBuilder("SELECT e FROM " + entityName + " e WHERE 1 = 1");

        // Dynamically build query conditions
        StringJoiner conditions = new StringJoiner(" AND ");
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();
            System.out.println("entityName: " + entityName + ",  " + fieldName + ":  " + fieldValue);

            if (fieldValue instanceof String) {
                conditions.add("LOWER(e." + fieldName + ") LIKE LOWER(CONCAT('%', :" + fieldName + ", '%'))");
            } else if (fieldValue.getClass().isAnnotationPresent(Entity.class)) {
                // Assume nested entity; query on its 'id' field
                conditions.add("e." + fieldName + ".id = :" + fieldName + "Id");
            } else {
                conditions.add("e." + fieldName + " = :" + fieldName);
            }
        }

        // Append conditions to the JPQL query
        jpql.append(conditions);
        System.out.println("jpql: " + jpql);

        // Create a Query object
        Query query = entityManager.createQuery(jpql.toString(), entityClass);

        // Set query parameters
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            String fieldName = entry.getKey();
            Object fieldValue = entry.getValue();

            if (fieldValue instanceof String) {
                query.setParameter(fieldName, fieldValue);
            } else if (fieldValue.getClass().isAnnotationPresent(Entity.class)) {
                // Use the 'id' field of the nested entity
                try {
                    Field idField = fieldValue.getClass().getDeclaredField("id");
                    idField.setAccessible(true);
                    Object idValue = idField.get(fieldValue);
                    query.setParameter(fieldName + "Id", idValue);
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                query.setParameter(fieldName, fieldValue);
            }
        }

        // Execute query and return results
        return query.getResultList();
    }
}
