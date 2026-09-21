package com.example.employee_manage_project.repository;

import com.example.employee_manage_project.entity.Department;
import com.example.employee_manage_project.entity.Employee;
import io.micrometer.common.KeyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,Long> {
    @Query(value = """
SELECT e.id,e.email,e.full_name,e.department_id
FROM user u
JOIN employee e ON u.employee_id = e.id
WHERE u.username = :username;
""",nativeQuery = true)
    Optional<Employee> findEmployeeByUsername(@Param("username") String username);

    List<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Employee> findAllByDepartment(Department department);
}
