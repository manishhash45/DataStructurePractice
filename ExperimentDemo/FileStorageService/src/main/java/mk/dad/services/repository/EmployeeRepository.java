package mk.dad.services.repository;

import mk.dad.services.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByDepartment(String department);

    List<Employee> findByEmployeeName(String employeeName);

    @Query("SELECT e FROM Employee e WHERE e.salary > :minSalary ORDER BY e.salary DESC")
    List<Employee> findEmployeesWithSalaryGreaterThan(Double minSalary);

    @Query("SELECT e FROM Employee e WHERE e.age >= :minAge AND e.age <= :maxAge")
    List<Employee> findEmployeesByAgeRange(Integer minAge, Integer maxAge);
}