package mk.dad.services.camel;

import lombok.extern.slf4j.Slf4j;
import mk.dad.services.entity.Employee;
import mk.dad.services.service.EmployeeService;
import org.apache.camel.Exchange;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class CsvFileProcessor {

    @Autowired
    private EmployeeService employeeService;

    public void processCsvFile(Exchange exchange) {
        try {
            String fileName = (String) exchange.getIn().getHeader("CamelFileName");
            
            // Check if it's a CSV file
            if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
                log.warn("Ignoring non-CSV file: {}", fileName);
                return;
            }

            log.info("========================================");
            log.info("📄 PROCESSING CSV FILE");
            log.info("========================================");
            log.info("File Name: {}", fileName);

            // Get file content
            byte[] fileContent = exchange.getIn().getBody(byte[].class);
            
            if (fileContent == null || fileContent.length == 0) {
                log.warn("CSV file is empty: {}", fileName);
                return;
            }

            log.info("File Size: {} bytes", fileContent.length);

            // Parse CSV and create Employee objects
            List<Employee> employees = parseCsvAndCreateEmployees(fileContent);

            log.info("Parsed {} employee records from CSV", employees.size());

            // Save employees to database
            if (!employees.isEmpty()) {
                List<Employee> savedEmployees = employeeService.saveEmployees(employees);
                
                log.info("========================================");
                log.info("✅ CSV PROCESSING COMPLETE");
                log.info("========================================");
                log.info("Total Employees Saved: {}", savedEmployees.size());
                log.info("Timestamp: {}", System.currentTimeMillis());
                log.info("========================================");

                // Log each saved employee
                for (Employee emp : savedEmployees) {
                    log.info("  ✓ Employee ID: {} | Name: {} | Dept: {} | Salary: ${}", 
                             emp.getId(), emp.getEmployeeName(), emp.getDepartment(), emp.getSalary());
                }
            } else {
                log.warn("No valid employee records found in CSV file");
            }

        } catch (Exception e) {
            log.error("Error processing CSV file", e);
            throw new RuntimeException("Error processing CSV file: " + e.getMessage(), e);
        }
    }

    private List<Employee> parseCsvAndCreateEmployees(byte[] fileContent) throws Exception {
        List<Employee> employees = new ArrayList<>();

        try (InputStreamReader reader = new InputStreamReader(new java.io.ByteArrayInputStream(fileContent));
             BufferedReader bufferedReader = new BufferedReader(reader);
             CSVParser csvParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .withIgnoreHeaderCase()
                     .withTrim())) {

            for (CSVRecord csvRecord : csvParser) {
                try {
                    String employeeName = csvRecord.get("employeeName").trim();
                    String gender = csvRecord.get("gender").trim();
                    Double salary = Double.parseDouble(csvRecord.get("salary").trim());
                    Integer age = Integer.parseInt(csvRecord.get("age").trim());
                    String department = csvRecord.get("department").trim();

                    // Validate data
                    if (employeeName.isEmpty()) {
                        log.warn("Skipping record with empty employee name at record number {}", csvRecord.getRecordNumber());
                        continue;
                    }

                    Employee employee = new Employee(employeeName, gender, salary, age, department);
                    employees.add(employee);

                    log.debug("Parsed employee: {} | Gender: {} | Salary: ${} | Age: {} | Dept: {}", 
                              employeeName, gender, salary, age, department);

                } catch (NumberFormatException e) {
                    log.warn("Invalid number format in record {}: {}", csvRecord.getRecordNumber(), e.getMessage());
                } catch (IllegalArgumentException e) {
                    log.warn("Missing required column in record {}: {}", csvRecord.getRecordNumber(), e.getMessage());
                }
            }
        }

        return employees;
    }
}