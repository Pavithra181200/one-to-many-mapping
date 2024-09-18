package com.complex.oneToManyMappingGraphQl.resolver;

import com.complex.oneToManyMappingGraphQl.domain.Department;
import com.complex.oneToManyMappingGraphQl.domain.Employee;
import com.complex.oneToManyMappingGraphQl.domain.Organization;
import com.complex.oneToManyMappingGraphQl.domain.OrganizationInput;
import com.complex.oneToManyMappingGraphQl.repository.OrganizationRepository;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;


@Controller
public class OrganizationController {

    OrganizationRepository repository;

    OrganizationController(OrganizationRepository repository) {
        this.repository = repository;
    }

    @MutationMapping
    public Organization newOrganization(@Argument OrganizationInput organization) {
        return repository.save(new Organization(null, organization.getName(),null,null));
    }

    @QueryMapping
    public Iterable<Organization> organizations() {
        return repository.findAll();
    }

    @QueryMapping
    public Organization organization(@Argument Integer id, DataFetchingEnvironment environment) {
        Specification<Organization> spec = byId(id);
        DataFetchingFieldSelectionSet selectionSet = environment.getSelectionSet();
        if (selectionSet.contains("employees"))
            spec = spec.and(fetchEmployees());
        if (selectionSet.contains("departments"))
            spec = spec.and(fetchDepartments());
        return repository.findOne(spec).orElseThrow();
    }

    private Specification<Organization> fetchDepartments() {
        return (root, query, builder) -> {
            Fetch<Organization, Department> f = root.fetch("departments", JoinType.LEFT);
            Join<Organization, Department> join = (Join<Organization, Department>) f;
            return join.getOn();
        };
    }

    private Specification<Organization> fetchEmployees() {
        return (root, query, builder) -> {
            Fetch<Organization, Employee> f = root.fetch("employees", JoinType.LEFT);
            Join<Organization, Employee> join = (Join<Organization, Employee>) f;
            return join.getOn();
        };
    }

    private Specification<Organization> byId(Integer id) {
        return (root, query, builder) -> builder.equal(root.get("id"), id);
    }
}
