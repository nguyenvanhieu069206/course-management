package com.example.coursemanagement.exception;

/**
 * Loi nghiep vu (vi du: xoa loai khoa hoc con khoa hoc, khoang hoc phi
 * khong hop le...). Duoc bat lai va hien thi thong bao than thien,
 * khong lam vo man hinh ung dung.
 */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
