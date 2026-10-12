package com.example.coursemanagement.service;

import com.example.coursemanagement.entity.CourseCategory;
import com.example.coursemanagement.exception.BusinessRuleException;
import com.example.coursemanagement.exception.ResourceNotFoundException;
import com.example.coursemanagement.repository.CourseCategoryRepository;
import com.example.coursemanagement.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseCategoryService {

    private final CourseCategoryRepository categoryRepository;
    private final CourseRepository courseRepository;

    public CourseCategoryService(CourseCategoryRepository categoryRepository, CourseRepository courseRepository) {
        this.categoryRepository = categoryRepository;
        this.courseRepository = courseRepository;
    }

    public List<CourseCategory> findAll() {
        return categoryRepository.findAll();
    }

    public CourseCategory findById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loại khóa học không tồn tại (id=" + id + ")"));
    }

    public long countCourses(Integer categoryId) {
        return courseRepository.countByCategoryId(categoryId);
    }

    @Transactional
    public CourseCategory create(CourseCategory category) {
        return categoryRepository.save(category);
    }

    @Transactional
    public CourseCategory update(Integer id, CourseCategory data) {
        CourseCategory existing = findById(id);
        existing.setName(data.getName());
        existing.setDescription(data.getDescription());
        return categoryRepository.save(existing);
    }

    @Transactional
    public void delete(Integer id) {
        CourseCategory category = findById(id);
        long courseCount = courseRepository.countByCategoryId(category.getId());
        if (courseCount > 0) {
            throw new BusinessRuleException(
                    "Không thể xóa loại khóa học \"" + category.getName() + "\" vì vẫn còn " + courseCount
                            + " khóa học thuộc loại này.");
        }
        categoryRepository.delete(category);
    }
}
