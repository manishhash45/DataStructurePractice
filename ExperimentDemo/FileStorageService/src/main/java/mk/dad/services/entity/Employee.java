package mk.dad.services.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "EMPLOYEE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "EMPLOYEE_NAME", nullable = false)
    private String employeeName;

    @Column(name = "GENDER")
    private String gender;

    @Column(name = "SALARY")
    private Double salary;

    @Column(name = "AGE")
    private Integer age;

    @Column(name = "DEPARTMENT")
    private String department;

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private Long createdAt = System.currentTimeMillis();

    public Employee(String employeeName, String gender, Double salary, Integer age, String department) {
        this.employeeName = employeeName;
        this.gender = gender;
        this.salary = salary;
        this.age = age;
        this.department = department;
    }
}