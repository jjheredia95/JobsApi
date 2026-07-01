package com.jobshub.junit;

import com.jobshub.dto.category.CategoryFormResponseDto;
import com.jobshub.error.BadRequestException;
import com.jobshub.error.NotFoundException;
import com.jobshub.model.Category;
import com.jobshub.repository.CategoryRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// En @BeforeEach, asegúrate de tener testCategory como la "existente"
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepo categoryRepo;

    @InjectMocks
    private CategoryService categoryService;

    // ============ createCategory ============

    @Test
    void createCategory_Success() {
        CategoryFormResponseDto request = new CategoryFormResponseDto("Technology", "Valid description here");
        when(categoryRepo.existsByNameIgnoreCase("Technology")).thenReturn(false);

        categoryService.createCategory(request);

        verify(categoryRepo).save(any(Category.class));
    }

    @Test
    void createCategory_ShouldThrow_WhenNameAlreadyExists() {
        CategoryFormResponseDto request = new CategoryFormResponseDto("Technology", "Valid description here");
        when(categoryRepo.existsByNameIgnoreCase("Technology")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> categoryService.createCategory(request));
        verify(categoryRepo, never()).save(any());
    }

    @Test
    void createCategory_ShouldThrow_WhenNameTooShort() {
        CategoryFormResponseDto request = new CategoryFormResponseDto("ab", "Valid description here");

        assertThrows(BadRequestException.class, () -> categoryService.createCategory(request));
        verify(categoryRepo, never()).save(any());
    }

    @Test
    void createCategory_ShouldThrow_WhenNameTooLong() {
        String longName = "a".repeat(41);
        CategoryFormResponseDto request = new CategoryFormResponseDto(longName, "Valid description here");

        assertThrows(BadRequestException.class, () -> categoryService.createCategory(request));
        verify(categoryRepo, never()).save(any());
    }

    @Test
    void createCategory_ShouldThrow_WhenDescriptionTooShort() {
        CategoryFormResponseDto request = new CategoryFormResponseDto("Technology", "short");

        assertThrows(BadRequestException.class, () -> categoryService.createCategory(request));
        verify(categoryRepo, never()).save(any());
    }

    @Test
    void createCategory_ShouldThrow_WhenDescriptionTooLong() {
        String longDescription = "a".repeat(101);
        CategoryFormResponseDto request = new CategoryFormResponseDto("Technology", longDescription);

        assertThrows(BadRequestException.class, () -> categoryService.createCategory(request));
        verify(categoryRepo, never()).save(any());
    }


    // ============ getCategoryById ============
    @Test
    void getCategoryById_Success() {
        Category category = new Category();
        category.setId(1);
        category.setName("Technology");
        category.setDescription("Valid description here");
        when(categoryRepo.findById(1)).thenReturn(Optional.of(category));

        CategoryFormResponseDto result = categoryService.getCategoryById(1);

        assertThat(result.name()).isEqualTo("Technology");
        assertThat(result.description()).isEqualTo("Valid description here");
    }

    @Test
    void getCategoryById_ShouldThrow_WhenNotFound() {
        when(categoryRepo.findById(99)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.getCategoryById(99));
    }

    // ============ updateCategory ============

    @Test
    void updateCategory_Success() {
        Category existing = new Category();
        existing.setId(1);
        existing.setName("Technology");
        existing.setDescription("Old description here");

        CategoryFormResponseDto request = new CategoryFormResponseDto("Science", "New valid description");

        when(categoryRepo.findById(1)).thenReturn(Optional.of(existing));
        when(categoryRepo.existsByNameIgnoreCaseAndIdNot("Science", 1)).thenReturn(false);

        categoryService.updateCategory(1, request);

        verify(categoryRepo).save(existing);
        assertThat(existing.getName()).isEqualTo("Science");
    }

    @Test
    void updateCategory_ShouldThrow_WhenCategoryNotFound() {
        when(categoryRepo.findById(99)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> categoryService.updateCategory(99, new CategoryFormResponseDto("Science", "New valid description")));
    }

    @Test
    void updateCategory_ShouldThrow_WhenNameAlreadyExistsForOtherId() {
        Category existing = new Category();
        existing.setId(1);
        existing.setName("Technology");
        existing.setDescription("Old description here");

        when(categoryRepo.findById(1)).thenReturn(Optional.of(existing));
        when(categoryRepo.existsByNameIgnoreCaseAndIdNot("Science", 1)).thenReturn(true);

        assertThrows(BadRequestException.class,
                () -> categoryService.updateCategory(1, new CategoryFormResponseDto("Science", "New valid description")));
        verify(categoryRepo, never()).save(any());
    }

}
