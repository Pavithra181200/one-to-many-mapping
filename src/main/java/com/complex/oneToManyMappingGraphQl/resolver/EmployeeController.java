package com.complex.oneToManyMappingGraphQl.resolver;

import com.complex.oneToManyMappingGraphQl.domain.Department;
import com.complex.oneToManyMappingGraphQl.domain.Employee;
import com.complex.oneToManyMappingGraphQl.domain.EmployeeInput;
import com.complex.oneToManyMappingGraphQl.domain.Organization;
import com.complex.oneToManyMappingGraphQl.filter.EmployeeFilter;
import com.complex.oneToManyMappingGraphQl.filter.FilterField;
import com.complex.oneToManyMappingGraphQl.repository.DepartmentRepository;
import com.complex.oneToManyMappingGraphQl.repository.EmployeeRepository;
import com.complex.oneToManyMappingGraphQl.repository.OrganizationRepository;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;


@Controller
public class EmployeeController {

    DepartmentRepository departmentRepository;
    EmployeeRepository employeeRepository;
    OrganizationRepository organizationRepository;

    EmployeeController(DepartmentRepository departmentRepository, EmployeeRepository employeeRepository, OrganizationRepository organizationRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.organizationRepository = organizationRepository;
    }

    @QueryMapping
    public Iterable<Employee> employees() {
        return employeeRepository.findAll();
    }

    @QueryMapping
    public Employee employee(@Argument Integer id) {
        return employeeRepository.findById(id).orElseThrow();
    }

    @MutationMapping
    public Employee newEmployee(@Argument EmployeeInput employee) {
        Department department = departmentRepository.findById(employee.getDepartmentId()).get();
        Organization organization = organizationRepository.findById(employee.getOrganizationId()).get();
        return employeeRepository.save(new Employee(null, employee.getFirstName(), employee.getLastName(),
                employee.getPosition(), employee.getAge(), employee.getSalary(),
                department, organization));
    }

    @QueryMapping
    public Iterable<Employee> employeesWithFilter(@Argument EmployeeFilter filter) {
        Specification<Employee> spec = null;
        if (filter.getSalary() != null)
            spec = bySalary(filter.getSalary());
        if (filter.getAge() != null)
            spec = (spec == null ? byAge(filter.getAge()) : spec.and(byAge(filter.getAge())));
        if (filter.getPosition() != null)
            spec = (spec == null ? byPosition(filter.getPosition()) :
                    spec.and(byPosition(filter.getPosition())));
        if (spec != null)
            return employeeRepository.findAll(spec);
        else
            return employeeRepository.findAll();
    }

    private Specification<Employee> bySalary(FilterField filterField) {
        return (root, query, builder) -> filterField.generateCriteria(builder, root.get("salary"));
    }

    private Specification<Employee> byAge(FilterField filterField) {
        return (root, query, builder) -> filterField.generateCriteria(builder, root.get("age"));
    }

    private Specification<Employee> byPosition(FilterField filterField) {
        return (root, query, builder) -> filterField.generateCriteria(builder, root.get("position"));
    }
}
