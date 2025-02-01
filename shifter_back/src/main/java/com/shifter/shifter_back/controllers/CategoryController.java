package com.shifter.shifter_back.controllers;

import com.shifter.shifter_back.models.Category;
import com.shifter.shifter_back.services.EntityInterface;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/category")
@AllArgsConstructor
public class CategoryController {

    EntityInterface<Category> categoryEntityInterface;

    @GetMapping
    public Object getCategory(@RequestParam(required = false) String id) {
        return null;
    }
}
