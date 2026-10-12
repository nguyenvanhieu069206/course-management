package com.example.coursemanagement.controller;

import com.example.coursemanagement.entity.CourseCategory;
import com.example.coursemanagement.service.CourseCategoryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/categories")
public class CourseCategoryController {

    private final CourseCategoryService categoryService;

    public CourseCategoryController(CourseCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model) {
        var categories = categoryService.findAll();
        Map<Integer, Long> courseCounts = new HashMap<>();
        for (CourseCategory category : categories) {
            courseCounts.put(category.getId(), categoryService.countCourses(category.getId()));
        }
        model.addAttribute("categories", categories);
        model.addAttribute("courseCounts", courseCounts);
        return "categories/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("category", new CourseCategory());
        model.addAttribute("isEdit", false);
        return "categories/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("category") CourseCategory category, BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "categories/form";
        }
        categoryService.create(category);
        redirectAttributes.addFlashAttribute("successMessage", "Tạo loại khóa học thành công.");
        return "redirect:/categories";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model) {
        model.addAttribute("category", categoryService.findById(id));
        model.addAttribute("isEdit", true);
        return "categories/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Integer id, @Valid @ModelAttribute("category") CourseCategory category,
                          BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "categories/form";
        }
        categoryService.update(id, category);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật loại khóa học thành công.");
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id) {
        categoryService.delete(id);
        return "redirect:/categories";
    }
}
