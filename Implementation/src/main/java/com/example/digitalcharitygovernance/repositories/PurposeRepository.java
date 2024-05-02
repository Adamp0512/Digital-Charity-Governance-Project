package com.example.digitalcharitygovernance.repositories;

import com.example.digitalcharitygovernance.models.CharitablePurpose;
import com.example.digitalcharitygovernance.models.Charity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PurposeRepository extends CrudRepository<CharitablePurpose, Long> {
    List<CharitablePurpose> findByCharity(Charity charity);
}
