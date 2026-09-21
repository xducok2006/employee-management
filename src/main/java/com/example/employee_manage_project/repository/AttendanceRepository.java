package com.example.employee_manage_project.repository;

import com.example.employee_manage_project.entity.Attendance;
import com.example.employee_manage_project.entity.Department;
import io.micrometer.common.KeyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance,Long> {
    @Query("""
SELECT COUNT(a)>0
FROM Employee e
JOIN User u ON u.employee.id= e.id
JOIN Attendance a ON a.employee.id= e.id
WHERE u.username = :username AND a.workDate = :workDate
""")
    boolean existsByEmployee_UsernameAndWorkDate(@Param("username") String username,@Param("workDate") LocalDate workDate);
    @Query("""
SELECT a
FROM Employee e
JOIN User u ON u.employee.id= e.id
JOIN Attendance a ON a.employee.id= e.id
WHERE u.username = :username AND a.workDate = :workDate
""")
    Optional<Attendance> findByEmployee_UsernameAndWorkDate(@Param("username") String username,@Param("workDate") LocalDate workDate);
    List<Attendance> findAllByEmployee_Id(Long employeeId);
    @Query("""
SELECT a FROM Attendance a WHERE a.workDate >= :startDate AND a.workDate <= :endDate 
""")
    List<Attendance> findAllByMonth(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query(value = """
SELECT a FROM Attendance a WHERE a.workDate >= :startDate AND a.workDate <= :endDate AND a.status = :status
""")
    List<Attendance> findAllByStatusInTime(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("status") Attendance.Status status);

    @Query(value = """
SELECT a.employee.id, a.employee.fullName, COUNT(a.employee) AS lateCount 
FROM Attendance a WHERE a.workDate >= :startDate AND a.workDate <= :endDate AND a.status = :status
GROUP BY a.employee.id,a.employee.fullName
""")
    List<Object[]> findAllByStatusInMonth(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("status") Attendance.Status status);
    @Query(value = """
SELECT e.id, e.full_name, COUNT(a.employee_id) AS late_count
FROM employee e
JOIN attendance a ON e.id = a.employee_id
WHERE a.work_date >= :startDate AND a.work_date<= :endDate AND a.`status` = 'LATE'
GROUP BY e.id,e.full_name
HAVING COUNT(a.employee_id)=
(
	SELECT MAX(late_count)
	FROM
	(
		SELECT COUNT(a.employee_id) AS late_count
		FROM attendance a
		WHERE a.work_date >= :startDate AND a.work_date<= :endDate AND a.`status` = 'LATE'
		GROUP BY a.employee_id
	) AS counts
);
""",nativeQuery = true)
    List<Object[]> findByEmployeeWithMostLateInMonth(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<Attendance> findAllByEmployee_Department(Department employeeDepartment);

    @Query(value = """
SELECT a
FROM Attendance a
JOIN Employee e ON a.employee = e
WHERE a.workDate >= :startDate AND a.workDate <= :endDate AND e.department = :department
""")
    List<Attendance> findAllByEmployee_DepartmentAndInWorkDate(@Param("startDate")LocalDate startDate,@Param("endDate")LocalDate endDate, @Param("department")Department department);
}
