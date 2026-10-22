
package com.college.management.repository;

import com.college.management.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    boolean existsByStudent_IdAndAttendanceDate(
            Long studentId,
            LocalDate attendanceDate
    );

    default boolean existsForStudentAndDate(
            Long studentId,
            LocalDate attendanceDate
    ) {
        return existsByStudent_IdAndAttendanceDate(
                studentId, attendanceDate
        );
    }
}