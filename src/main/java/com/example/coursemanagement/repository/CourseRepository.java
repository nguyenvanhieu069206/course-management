package com.example.coursemanagement.repository;

import com.example.coursemanagement.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CourseRepository extends JpaRepository<Course, Integer>, JpaSpecificationExecutor<Course> {

    boolean existsByCategoryIdAndNameIgnoreCase(Integer categoryId, String name);

    boolean existsByCategoryIdAndNameIgnoreCaseAndIdNot(Integer categoryId, String name, Integer id);

    long countByCategoryId(Integer categoryId);
}
