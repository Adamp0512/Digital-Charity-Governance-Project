package com.example.digitalcharitygovernance.repositories;

import com.example.digitalcharitygovernance.models.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RoleRepository extends CrudRepository <Role,Integer>{
    Role findByName(String name);
    List<Role> findAllByName(String name);

    int countByName(String name);


}
