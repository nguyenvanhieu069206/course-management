package com.example.coursemanagement.service;

import com.example.coursemanagement.dto.CourseSearchForm;
import com.example.coursemanagement.entity.Course;
import com.example.coursemanagement.entity.CourseCategory;
import com.example.coursemanagement.exception.ResourceNotFoundException;
import com.example.coursemanagement.repository.CourseCategoryRepository;
import com.example.coursemanagement.repository.CourseRepository;
import com.example.coursemanagement.repository.CourseSpecification;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseCategoryRepository categoryRepository;

    public CourseService(CourseRepository courseRepository, CourseCategoryRepository categoryRepository) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Tim kiem/loc/sap xep hoan toan o tang database thong qua
     * Specification + Sort, khong lay toan bo du lieu ve roi loc bang vong lap.
     */
    public List<Course> search(CourseSearchForm form) {
        Sort sort = resolveSort(form.getSortBy());
        return courseRepository.findAll(CourseSpecification.fromSearchForm(form), sort);
    }

    private Sort resolveSort(String sortBy) {
        if (sortBy == null) {
            return Sort.unsorted();
        }
        return switch (sortBy) {
            case "fee_asc" -> Sort.by("tuitionFee").ascending();
            case "fee_desc" -> Sort.by("tuitionFee").descending();
            case "duration_asc" -> Sort.by("duration").ascending();
            default -> Sort.unsorted();
        };
    }

    public Course findById(Integer id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khóa học không tồn tại (id=" + id + ")"));
    }

    public List<CourseCategory> findAllCategories() {
        return categoryRepository.findAll();
    }

    public CourseCategory getCategoryOrThrow(Integer categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Loại khóa học không tồn tại (id=" + categoryId + ")"));
    }

    /** Kiem tra ten khoa hoc da trung trong cung loai hay chua (khi tao moi). */
    public boolean isDuplicateName(Integer categoryId, String name) {
        return courseRepository.existsByCategoryIdAndNameIgnoreCase(categoryId, name);
    }

    /** Kiem tra trung ten khi sua, bo qua chinh ban ghi dang sua. */
    public boolean isDuplicateNameForUpdate(Integer categoryId, String name, Integer courseId) {
        return courseRepository.existsByCategoryIdAndNameIgnoreCaseAndIdNot(categoryId, name, courseId);
    }

    @Transactional
    public Course create(Course course) {
        return courseRepository.save(course);
    }

    @Transactional
    public Course update(Integer id, Course data) {
        Course existing = findById(id);
        existing.setName(data.getName());
        existing.setTuitionFee(data.getTuitionFee());
        existing.setDuration(data.getDuration());
        existing.setActive(data.isActive());
        existing.setDescription(data.getDescription());
        existing.setCategory(data.getCategory());
        return courseRepository.save(existing);
    }
}
