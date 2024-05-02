package com.example.digitalcharitygovernance.repositories;

import com.example.digitalcharitygovernance.models.Role;
import com.example.digitalcharitygovernance.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import org.springframework.data.repository.query.Param;

import java.util.Optional;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository

public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUsername(String username);

    Iterable<User> findAllByRole(Role role);

    int countByUsername(String username);

    int countByRole(Optional<Role> role);

    @Query("SELECT COUNT(user) FROM User user WHERE user.role = :role")
    int countByRole(Role role);

    @Query("SELECT user FROM User user WHERE LOWER(CONCAT(user.firstName, ' ', user.surname)) LIKE %:search_query%")
    Iterable<User> findByConcatenatingNames(@Param("search_query") String search_query);


}
