
package com.college.management;

import com.college.management.entity.Student;
import com.college.management.repository.StudentRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // Display all students
    @GetMapping("/students")
    public String listStudents(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        return "students";
    }

    // Show Add Student form
    @GetMapping("/students/new")
    public String showAddForm(Model model) {
        model.addAttribute("student", new Student());
        return "student-form";
    }

    // Save new student
    @PostMapping("/students")
    public String saveStudent(
            Student student,
            RedirectAttributes redirectAttributes) {

        if (studentRepository.existsByEmailIgnoreCase(student.getEmail())) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "This email is already registered. Please use another email."
            );
            return "redirect:/students/new";
        }

        studentRepository.save(student);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Student added successfully."
        );

        return "redirect:/students";
    }

    // Show Edit Student form
    @GetMapping("/students/edit/{id}")
    public String showEditForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Student student = studentRepository.findById(id).orElse(null);

        if (student == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Student not found."
            );
            return "redirect:/students";
        }

        model.addAttribute("student", student);
        return "student-form";
    }

    // Update existing student
    @PostMapping("/students/update/{id}")
    public String updateStudent(
            @PathVariable Long id,
            Student formStudent,
            RedirectAttributes redirectAttributes) {

        Student student = studentRepository.findById(id).orElse(null);

        if (student == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Student not found."
            );
            return "redirect:/students";
        }

        boolean emailUsedByAnotherStudent =
                studentRepository.existsByEmailIgnoreCase(formStudent.getEmail())
                && !student.getEmail().equalsIgnoreCase(formStudent.getEmail());

        if (emailUsedByAnotherStudent) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "This email is already registered to another student."
            );
            return "redirect:/students/edit/" + id;
        }

        student.setName(formStudent.getName());
        student.setEmail(formStudent.getEmail());
        student.setCourse(formStudent.getCourse());

        studentRepository.save(student);

        redirectAttributes.addFlashAttribute(
                "successMessage",
                "Student updated successfully."
        );

        return "redirect:/students";
    }

    // Delete student safely
    @PostMapping("/students/delete/{id}")
    public String deleteStudent(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        if (!studentRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Student not found."
            );
            return "redirect:/students";
        }

        try {
            studentRepository.deleteById(id);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Student deleted successfully."
            );

        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "This student may have attendance records. Remove or reassign those records before deleting this student."
            );
        }

        return "redirect:/students";
    }
}
