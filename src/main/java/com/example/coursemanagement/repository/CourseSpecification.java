package com.example.coursemanagement.repository;

import com.example.coursemanagement.dto.CourseSearchForm;
import com.example.coursemanagement.entity.Course;
import org.springframework.data.jpa.domain.Specification;

/**
 * Xay dung dieu kien loc dong o tang database (Criteria API) de tranh
 * viec lay toan bo du lieu ve roi dung vong lap de loc trong Java.
 */
public class CourseSpecification {

    public static Specification<Course> fromSearchForm(CourseSearchForm form) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (form.getName() != null && !form.getName().isBlank()) {
                predicates = cb.and(predicates,
                        cb.like(cb.lower(root.get("name")), "%" + form.getName().trim().toLowerCase() + "%"));
            }

            if (form.getCategoryId() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("category").get("id"), form.getCategoryId()));
            }

            if (form.getIsActive() != null) {
                predicates = cb.and(predicates,
                        cb.equal(root.get("isActive"), form.getIsActive()));
            }

            if (form.getMinFee() != null) {
                predicates = cb.and(predicates,
                        cb.greaterThanOrEqualTo(root.get("tuitionFee"), form.getMinFee()));
            }

            if (form.getMaxFee() != null) {
                predicates = cb.and(predicates,
                        cb.lessThanOrEqualTo(root.get("tuitionFee"), form.getMaxFee()));
            }

            return predicates;
        };
    }
}
