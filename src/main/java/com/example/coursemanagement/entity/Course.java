package com.example.coursemanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Courses")
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "Tên khóa học không được để trống")
    @Column(name = "Name", nullable = false, length = 255)
    private String name;

    @NotNull(message = "Học phí không được để trống")
    @DecimalMin(value = "0.01", message = "Học phí phải lớn hơn 0")
    @Column(name = "TuitionFee", nullable = false, precision = 18, scale = 2)
    private BigDecimal tuitionFee;

    @NotNull(message = "Thời lượng không được để trống")
    @Min(value = 1, message = "Thời lượng phải lớn hơn 0")
    @Column(name = "Duration", nullable = false)
    private Integer duration;

    @Column(name = "IsActive", nullable = false)
    private boolean isActive = true;

    @Column(name = "Description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @NotNull(message = "Vui lòng chọn loại khóa học")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CourseCategoryId", nullable = false)
    private CourseCategory category;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CourseImage> images = new ArrayList<>();

    /**
     * Phan loai thoi luong - chi tinh khi hien thi, KHONG luu vao database.
     */
    @Transient
    public String getDurationType() {
        if (duration == null) {
            return null;
        }
        return duration <= 30 ? "Ngắn hạn" : "Dài hạn";
    }
}
