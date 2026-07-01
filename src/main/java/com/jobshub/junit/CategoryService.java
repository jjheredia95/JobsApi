package com.jobshub.junit;

import com.jobshub.dto.category.CategoryFormResponseDto;
import com.jobshub.dto.category.CategoryResponse;
import com.jobshub.error.BadRequestException;
import com.jobshub.error.NotFoundException;
import com.jobshub.model.Category;
import com.jobshub.repository.CategoryRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepo categoryRepo;

    public CategoryService(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepo.findAll();
        return categories.stream()
                .map(c -> new CategoryResponse(
                        c.getId(),
                        c.getName(),
                        c.getDescription())).toList();
    }

    public List<CategoryResponse> createCategory(CategoryFormResponseDto category) {
        validateNameLength(category.name());
        validateDescriptionLength(category.description());

        if (categoryRepo.existsByNameIgnoreCase(category.name())) {
            throw new BadRequestException("Category with name " + category.name() + " already exists (case-insensitive)");
        }

        Category newCategory = new Category();
        newCategory.setName(category.name());
        newCategory.setDescription(category.description());
        categoryRepo.save(newCategory);
        return getAllCategories();
    }

    public CategoryFormResponseDto getCategoryById(Integer id) {
        Category category = findCategoryOrThrow(id);  // Reutiliza la lógica
        return new CategoryFormResponseDto(category.getName(), category.getDescription());
    }

    public List<CategoryResponse> updateCategory(Integer id, CategoryFormResponseDto request) {
        Category categoryToUpdate = findCategoryOrThrow(id);  // Reutiliza la lógica
        validateNameLength(request.name());
        validateDescriptionLength(request.description());

        if (categoryRepo.existsByNameIgnoreCaseAndIdNot(request.name(), id)) {
            throw new BadRequestException("Category with name " + request.name() + " already exists (case-insensitive)");
        }

        categoryToUpdate.setName(request.name());
        categoryToUpdate.setDescription(request.description());
        categoryRepo.save(categoryToUpdate);
        return getAllCategories();
    }

    private Category findCategoryOrThrow(Integer id) {
        return categoryRepo.findById(id).orElseThrow(() -> new NotFoundException("Category not found with id: " + id));
    }

    public void deleteCategory(Integer id) {
        Category category = findCategoryOrThrow(id);
        categoryRepo.delete(category);
    }


    private void validateDescriptionLength(String description) {
        if (description.trim().length() < 10) {
            throw new BadRequestException("Description must be at least 10 characters");
        }
        if (description.trim().length() > 100) {
            throw new BadRequestException("Description must be at most 100 characters");
        }
    }

    private void validateNameLength(String name) {
        if (name.trim().length() < 3) {
            throw new BadRequestException("Name must be at least 3 characters");
        }

        if (name.trim().length() > 40) {
            throw new BadRequestException("Name must be at most 40 characters");
        }
    }



}
