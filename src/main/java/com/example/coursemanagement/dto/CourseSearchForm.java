package com.example.coursemanagement.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Giu lai cac dieu kien tim kiem/loc/sap xep de hien thi lai tren form
 * sau khi submit (theo yeu cau de bai).
 */
@Getter
@Setter
public class CourseSearchForm {

    private String name;

    private Integer categoryId;

    /** null = tat ca, true = dang tuyen sinh, false = ngung tuyen sinh */
    private Boolean isActive;

    private BigDecimal minFee;

    private BigDecimal maxFee;

    /** fee_asc | fee_desc | duration_asc */
    private String sortBy;
}
