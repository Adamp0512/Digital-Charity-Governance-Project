package com.example.digitalcharitygovernance.repositories;

import com.example.digitalcharitygovernance.models.Charity;
import org.springframework.data.repository.CrudRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called charityRepository
// CRUD refers Create, Read, Update, Delete

public interface CharityRepository extends CrudRepository<Charity, Long> {

}