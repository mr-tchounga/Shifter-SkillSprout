package com.shifter.shifter_back.controllers;

import com.shifter.shifter_back.models.Category;
import com.shifter.shifter_back.models.User;
import com.shifter.shifter_back.services.EntityInterface;
import com.shifter.shifter_back.services.auth.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/category")
@AllArgsConstructor
public class CategoryController {

    private final AuthService authService;
    EntityInterface<Category> categoryEntityInterface;

    @GetMapping
    public Object getCategory(@RequestParam(required = false) Long id, @RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        User currentUser = authService.getCurrentUserFromToken(jwtToken);

        if (id != null) {
            return categoryEntityInterface.findEntityById(id);
        }
        return  categoryEntityInterface.findAllEntity(currentUser);
    }

    @PostMapping
    public Object postCategory(@RequestBody(required = false) Category category, @RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        User currentUser = authService.getCurrentUserFromToken(jwtToken);

        if (category != null) {
            if (category.getId() != null) {
                return categoryEntityInterface.findEntityById(category.getId());
            }
            return categoryEntityInterface.findFilterEntity(currentUser, category);
        }
        return  categoryEntityInterface.findAllEntity(currentUser);
    }

    @PostMapping("/add")
    public Object addCategory(@RequestBody Category category, @RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        User currentUser = authService.getCurrentUserFromToken(jwtToken);

        return  categoryEntityInterface.addEntity(currentUser, category);
    }

    @PutMapping
    public Object updateCategory(@RequestBody Category category, @RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        User currentUser = authService.getCurrentUserFromToken(jwtToken);

        return  categoryEntityInterface.updateEntity(currentUser, category);
    }

    @DeleteMapping("/{id}")
    public Object removeCategory(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String jwtToken = token.substring(7);
        User currentUser = authService.getCurrentUserFromToken(jwtToken);

        categoryEntityInterface.deleteEntity(currentUser, id);
        return "Operation successful";
    }
}
