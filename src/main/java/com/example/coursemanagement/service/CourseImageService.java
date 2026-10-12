package com.example.coursemanagement.service;

import com.example.coursemanagement.entity.Course;
import com.example.coursemanagement.entity.CourseImage;
import com.example.coursemanagement.exception.ResourceNotFoundException;
import com.example.coursemanagement.repository.CourseImageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseImageService {

    private final CourseImageRepository imageRepository;
    private final CourseService courseService;

    public CourseImageService(CourseImageRepository imageRepository, CourseService courseService) {
        this.imageRepository = imageRepository;
        this.courseService = courseService;
    }

    public List<CourseImage> findByCourseId(Integer courseId) {
        return imageRepository.findByCourseId(courseId);
    }

    @Transactional
    public CourseImage addImage(Integer courseId, String imageUrl) {
        Course course = courseService.findById(courseId);

        boolean hasThumbnail = imageRepository.findByCourseId(courseId).stream().anyMatch(CourseImage::isThumbnail);

        CourseImage image = new CourseImage();
        image.setImageUrl(imageUrl);
        image.setCourse(course);
        // Neu khoa hoc chua co anh dai dien thi tu dong chon anh dau tien lam dai dien
        image.setThumbnail(!hasThumbnail);
        return imageRepository.save(image);
    }

    @Transactional
    public void setThumbnail(Integer courseId, Integer imageId) {
        courseService.findById(courseId); // dam bao course ton tai

        List<CourseImage> images = imageRepository.findByCourseId(courseId);
        boolean found = false;
        for (CourseImage image : images) {
            boolean shouldBeThumbnail = image.getId().equals(imageId);
            image.setThumbnail(shouldBeThumbnail);
            if (shouldBeThumbnail) {
                found = true;
            }
        }
        if (!found) {
            throw new ResourceNotFoundException("Ảnh không tồn tại (id=" + imageId + ") trong khóa học này");
        }
        imageRepository.saveAll(images);
    }
}
