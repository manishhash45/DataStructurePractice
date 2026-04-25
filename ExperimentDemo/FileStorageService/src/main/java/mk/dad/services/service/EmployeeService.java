package mk.dad.services.service;

import lombok.extern.slf4j.Slf4j;
import mk.dad.services.entity.Employee;
import mk.dad.services.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Transactional
    public Employee saveEmployee(Employee employee) {
        log.info("Saving employee: {}", employee.getEmployeeName());
        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Employee saved with ID: {}", savedEmployee.getId());
        return savedEmployee;
    }

    @Transactional
    public List<Employee> saveEmployees(List<Employee> employees) {
        log.info("Saving {} employees", employees.size());
        List<Employee> savedEmployees = employeeRepository.saveAll(employees);
        log.info("Successfully saved {} employees", savedEmployees.size());
        return savedEmployees;
    }

    public Optional<Employee> getEmployeeById(Long id) {
        log.info("Fetching employee with ID: {}", id);
        return employeeRepository.findById(id);
    }

    public List<Employee> getAllEmployees() {
        log.info("Fetching all employees");
        return employeeRepository.findAll();
    }

    public List<Employee> getEmployeesByDepartment(String department) {
        log.info("Fetching employees from department: {}", department);
        return employeeRepository.findByDepartment(department);
    }

    public List<Employee> getEmployeesByName(String name) {
        log.info("Fetching employees with name: {}", name);
        return employeeRepository.findByEmployeeName(name);
    }

    public List<Employee> getEmployeesWithMinSalary(Double minSalary) {
        log.info("Fetching employees with minimum salary: {}", minSalary);
        return employeeRepository.findEmployeesWithSalaryGreaterThan(minSalary);
    }

    public List<Employee> getEmployeesByAgeRange(Integer minAge, Integer maxAge) {
        log.info("Fetching employees with age between {} and {}", minAge, maxAge);
        return employeeRepository.findEmployeesByAgeRange(minAge, maxAge);
    }

    public long getTotalEmployeeCount() {
        long count = employeeRepository.count();
        log.info("Total employees in database: {}", count);
        return count;
    }

    @Transactional
    public void deleteEmployee(Long id) {
        log.info("Deleting employee with ID: {}", id);
        employeeRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllEmployees() {
        log.info("Deleting all employees");
        employeeRepository.deleteAll();
    }
}