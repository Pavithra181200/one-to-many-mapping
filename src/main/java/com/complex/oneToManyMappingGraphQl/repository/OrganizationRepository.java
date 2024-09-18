package com.complex.oneToManyMappingGraphQl.repository;

import com.complex.oneToManyMappingGraphQl.domain.Organization;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface OrganizationRepository extends CrudRepository<Organization, Integer>,
        JpaSpecificationExecutor<Organization> {
}
