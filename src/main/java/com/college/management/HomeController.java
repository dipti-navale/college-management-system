
package com.college.management;

import com.college.management.entity.Attendance;
import com.college.management.entity.Student;
import com.college.management.repository.AttendanceRepository;
import com.college.management.repository.StudentRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;

@Controller
public class HomeController {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;

    public HomeController(
            StudentRepository studentRepository,
            AttendanceRepository attendanceRepository) {

        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @GetMapping("/")
    public String home(Model model) {

        // Student statistics
        List<Student> students = studentRepository.findAll();

        long totalStudents = students.size();

        long totalCourses = students.stream()
                .map(Student::getCourse)
                .filter(course -> course != null && !course.isBlank())
                .distinct()
                .count();

        List<Student> recentStudents = students.stream()
                .sorted(Comparator.comparing(
                        Student::getId,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .limit(5)
                .toList();

        // Attendance statistics
        List<Attendance> attendances = attendanceRepository.findAll();

        long totalAttendance = attendances.size();

        long presentCount = attendances.stream()
                .filter(a -> a.getStatus() != null
                        && a.getStatus().equalsIgnoreCase("Present"))
                .count();

        long absentCount = attendances.stream()
                .filter(a -> a.getStatus() != null
                        && a.getStatus().equalsIgnoreCase("Absent"))
                .count();

        // Send statistics to dashboard
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("totalCourses", totalCourses);
        model.addAttribute("recentStudents", recentStudents);

        model.addAttribute("totalAttendance", totalAttendance);
        model.addAttribute("presentCount", presentCount);
        model.addAttribute("absentCount", absentCount);

        return "home";
    }
}
