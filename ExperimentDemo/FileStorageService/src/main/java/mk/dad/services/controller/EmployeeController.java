package mk.dad.services.controller;

import lombok.extern.slf4j.Slf4j;
import mk.dad.services.entity.Employee;
import mk.dad.services.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * API to get all employees
     * GET /api/employees/all
     * @return ResponseEntity with list of all employees
     */
    @GetMapping("/all")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        log.info("GET /api/employees/all - Fetching all employees");
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            log.info("Successfully fetched {} employees", employees.size());
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Error fetching all employees", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * API to get total employee count
     * GET /api/employees/count
     * @return ResponseEntity with total count
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalEmployeeCount() {
        log.info("GET /api/employees/count - Fetching total employee count");
        try {
            long count = employeeService.getTotalEmployeeCount();
            log.info("Total employees in database: {}", count);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            log.error("Error fetching employee count", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * API to get employee by ID
     * GET /api/employees/{id}
     * @param id employee ID
     * @return ResponseEntity with employee details
     */
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        log.info("GET /api/employees/{} - Fetching employee by ID", id);
        try {
            return employeeService.getEmployeeById(id)
                    .map(employee -> {
                        log.info("Employee found with ID: {}", id);
                        return ResponseEntity.ok(employee);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error fetching employee by ID: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * API to get employees by department
     * GET /api/employees/department/{department}
     * @param department department name
     * @return ResponseEntity with list of employees in that department
     */
    @GetMapping("/department/{department}")
    public ResponseEntity<List<Employee>> getEmployeesByDepartment(@PathVariable String department) {
        log.info("GET /api/employees/department/{} - Fetching employees by department", department);
        try {
            List<Employee> employees = employeeService.getEmployeesByDepartment(department);
            log.info("Found {} employees in {} department", employees.size(), department);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Error fetching employees by department: {}", department, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * API to get employees by name
     * GET /api/employees/name/{name}
     * @param name employee name
     * @return ResponseEntity with list of employees matching the name
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<List<Employee>> getEmployeesByName(@PathVariable String name) {
        log.info("GET /api/employees/name/{} - Fetching employees by name", name);
        try {
            List<Employee> employees = employeeService.getEmployeesByName(name);
            log.info("Found {} employees with name: {}", employees.size(), name);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Error fetching employees by name: {}", name, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * API to get employees with minimum salary
     * GET /api/employees/salary/{minSalary}
     * @param minSalary minimum salary threshold
     * @return ResponseEntity with list of employees with salary >= minSalary
     */
    @GetMapping("/salary/{minSalary}")
    public ResponseEntity<List<Employee>> getEmployeesWithMinSalary(@PathVariable Double minSalary) {
        log.info("GET /api/employees/salary/{} - Fetching employees with minimum salary", minSalary);
        try {
            List<Employee> employees = employeeService.getEmployeesWithMinSalary(minSalary);
            log.info("Found {} employees with salary >= {}", employees.size(), minSalary);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Error fetching employees with minimum salary: {}", minSalary, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * API to get employees by age range
     * GET /api/employees/age?minAge=30&maxAge=40
     * @param minAge minimum age
     * @param maxAge maximum age
     * @return ResponseEntity with list of employees in age range
     */
    @GetMapping("/age")
    public ResponseEntity<List<Employee>> getEmployeesByAgeRange(
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        log.info("GET /api/employees/age - Fetching employees aged {} to {}", minAge, maxAge);
        try {
            List<Employee> employees = employeeService.getEmployeesByAgeRange(minAge, maxAge);
            log.info("Found {} employees in age range {}-{}", employees.size(), minAge, maxAge);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            log.error("Error fetching employees by age range: {}-{}", minAge, maxAge, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * API to create a new employee
     * POST /api/employees
     * @param employee employee object to create
     * @return ResponseEntity with created employee
     */
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        log.info("POST /api/employees - Creating new employee: {}", employee.getEmployeeName());
        try {
            Employee savedEmployee = employeeService.saveEmployee(employee);
            log.info("Employee created successfully with ID: {}", savedEmployee.getId());
            return ResponseEntity.ok(savedEmployee);
        } catch (Exception e) {
            log.error("Error creating employee", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * API to delete employee by ID
     * DELETE /api/employees/{id}
     * @param id employee ID to delete
     * @return ResponseEntity with deletion status
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        log.info("DELETE /api/employees/{} - Deleting employee", id);
        try {
            employeeService.deleteEmployee(id);
            log.info("Employee deleted successfully with ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting employee with ID: {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * API to delete all employees
     * DELETE /api/employees/all
     * @return ResponseEntity with deletion status
     */
    @DeleteMapping("/all")
    public ResponseEntity<Void> deleteAllEmployees() {
        log.info("DELETE /api/employees/all - Deleting all employees");
        try {
            employeeService.deleteAllEmployees();
            log.info("All employees deleted successfully");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting all employees", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}