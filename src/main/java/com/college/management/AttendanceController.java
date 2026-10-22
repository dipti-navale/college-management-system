
package com.college.management;

import com.college.management.entity.Attendance;
import com.college.management.entity.Student;
import com.college.management.repository.AttendanceRepository;
import com.college.management.repository.StudentRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class AttendanceController {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    public AttendanceController(
            AttendanceRepository attendanceRepository,
            StudentRepository studentRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping("/attendance")
    public String showAttendancePage(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("attendances", attendanceRepository.findAll());
        model.addAttribute("today", LocalDate.now());

        return "attendance";
    }

    @PostMapping("/attendance")
    public String saveAttendance(
            Attendance attendance,
            Long studentId,
            RedirectAttributes redirectAttributes) {

        if (studentId == null || attendance.getAttendanceDate() == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Please select a student and attendance date.");
            return "redirect:/attendance";
        }

        Student student = studentRepository.findById(studentId)
                .orElse(null);

        if (student == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Selected student was not found.");
            return "redirect:/attendance";
        }

        boolean alreadyExists =
                attendanceRepository.existsForStudentAndDate(
                        studentId, attendance.getAttendanceDate());

        if (alreadyExists) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Attendance for this student and date already exists.");
            return "redirect:/attendance";
        }

        attendance.setStudent(student);
        attendanceRepository.save(attendance);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Attendance saved successfully!");

        return "redirect:/attendance";
    }

    @GetMapping("/attendance/edit/{id}")
    public String showEditAttendanceForm(
            @PathVariable Long id,
            Model model,
            RedirectAttributes redirectAttributes) {

        Attendance attendance = attendanceRepository.findById(id)
                .orElse(null);

        if (attendance == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Attendance record not found.");
            return "redirect:/attendance";
        }

        model.addAttribute("attendance", attendance);
        model.addAttribute("students", studentRepository.findAll());

        return "attendance-edit";
    }

    @PostMapping("/attendance/update/{id}")
    public String updateAttendance(
            @PathVariable Long id,
            Long studentId,
            LocalDate attendanceDate,
            String status,
            RedirectAttributes redirectAttributes) {

        Attendance existing = attendanceRepository.findById(id)
                .orElse(null);

        if (existing == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Attendance record not found.");
            return "redirect:/attendance";
        }

        if (studentId == null || attendanceDate == null
                || status == null
                || (!status.equals("Present") && !status.equals("Absent"))) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Please enter valid attendance details.");
            return "redirect:/attendance";
        }

        Student student = studentRepository.findById(studentId)
                .orElse(null);

        if (student == null) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage", "Selected student was not found.");
            return "redirect:/attendance";
        }

        boolean duplicate = attendanceRepository
                .existsForStudentAndDate(studentId, attendanceDate);

        boolean sameOriginalRecord =
                existing.getStudent().getId().equals(studentId)
                && existing.getAttendanceDate().equals(attendanceDate);

        if (duplicate && !sameOriginalRecord) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Attendance already exists for this student and date.");
            return "redirect:/attendance";
        }

        existing.setStudent(student);
        existing.setAttendanceDate(attendanceDate);
        existing.setStatus(status);

        attendanceRepository.save(existing);

        redirectAttributes.addFlashAttribute(
                "successMessage", "Attendance updated successfully!");

        return "redirect:/attendance";
    }
}
