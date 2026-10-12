package com.example.coursemanagement.controller;

import com.example.coursemanagement.dto.CourseSearchForm;
import com.example.coursemanagement.entity.Course;
import com.example.coursemanagement.entity.CourseCategory;
import com.example.coursemanagement.service.CourseImageService;
import com.example.coursemanagement.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;
    private final CourseImageService courseImageService;

    public CourseController(CourseService courseService, CourseImageService courseImageService) {
        this.courseService = courseService;
        this.courseImageService = courseImageService;
    }

    @GetMapping
    public String list(@ModelAttribute("searchForm") CourseSearchForm searchForm, Model model) {
        String feeRangeError = null;
        if (searchForm.getMinFee() != null && searchForm.getMaxFee() != null
                && searchForm.getMinFee().compareTo(searchForm.getMaxFee()) > 0) {
            feeRangeError = "Học phí tối thiểu không được lớn hơn học phí tối đa.";
            model.addAttribute("feeRangeError", feeRangeError);
        }

        List<Course> courses = feeRangeError == null ? courseService.search(searchForm) : List.of();

        model.addAttribute("courses", courses);
        model.addAttribute("categories", courseService.findAllCategories());
        model.addAttribute("noResult", courses.isEmpty() && feeRangeError == null);
        return "courses/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("categories", courseService.findAllCategories());
        model.addAttribute("isEdit", false);
        return "courses/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("course") Course course, BindingResult bindingResult,
                          Model model, RedirectAttributes redirectAttributes) {
        validateCategoryAndName(course, null, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", courseService.findAllCategories());
            model.addAttribute("isEdit", false);
            return "courses/form";
        }

        CourseCategory category = courseService.getCategoryOrThrow(course.getCategory().getId());
        course.setCategory(category);
        courseService.create(course);
        redirectAttributes.addFlashAttribute("successMessage", "Tạo khóa học thành công.");
        return "redirect:/courses";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("course", courseService.findById(id));
        model.addAttribute("categories", courseService.findAllCategories());
        model.addAttribute("isEdit", true);
        return "courses/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id, @Valid @ModelAttribute("course") Course course,
                          BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        validateCategoryAndName(course, id, bindingResult);

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", courseService.findAllCategories());
            model.addAttribute("isEdit", true);
            return "courses/form";
        }

        CourseCategory category = courseService.getCategoryOrThrow(course.getCategory().getId());
        course.setCategory(category);
        courseService.update(id, course);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật khóa học thành công.");
        return "redirect:/courses/" + id;
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        Course course = courseService.findById(id);
        model.addAttribute("course", course);
        model.addAttribute("images", courseImageService.findByCourseId(id));
        return "courses/detail";
    }

    @PostMapping("/{id}/images")
    public String addImage(@PathVariable Integer id, @RequestParam String imageUrl,
                            RedirectAttributes redirectAttributes) {
        if (imageUrl == null || imageUrl.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đường dẫn ảnh không được để trống.");
        } else {
            courseImageService.addImage(id, imageUrl.trim());
            redirectAttributes.addFlashAttribute("successMessage", "Thêm ảnh thành công.");
        }
        return "redirect:/courses/" + id;
    }

    @PostMapping("/{id}/images/{imageId}/thumbnail")
    public String setThumbnail(@PathVariable Integer id, @PathVariable Integer imageId,
                                RedirectAttributes redirectAttributes) {
        courseImageService.setThumbnail(id, imageId);
        redirectAttributes.addFlashAttribute("successMessage", "Đổi ảnh đại diện thành công.");
        return "redirect:/courses/" + id;
    }

    /** Kiem tra loai khoa hoc ton tai va ten khong trung trong cung loai. */
    private void validateCategoryAndName(Course course, Integer currentId, BindingResult bindingResult) {
        if (course.getCategory() == null || course.getCategory().getId() == null) {
            bindingResult.rejectValue("category", "required", "Vui lòng chọn loại khóa học");
            return;
        }

        Integer categoryId = course.getCategory().getId();
        boolean categoryExists = courseService.findAllCategories().stream()
                .anyMatch(c -> c.getId().equals(categoryId));
        if (!categoryExists) {
            bindingResult.rejectValue("category", "notfound", "Loại khóa học không tồn tại");
            return;
        }

        if (course.getName() != null && !course.getName().isBlank()) {
            boolean duplicate = currentId == null
                    ? courseService.isDuplicateName(categoryId, course.getName())
                    : courseService.isDuplicateNameForUpdate(categoryId, course.getName(), currentId);
            if (duplicate) {
                bindingResult.rejectValue("name", "duplicate",
                        "Tên khóa học đã tồn tại trong loại khóa học này");
            }
        }
    }
}
