package com.example.digitalcharitygovernance;

import com.example.digitalcharitygovernance.models.Role;
import com.example.digitalcharitygovernance.models.User;
import com.example.digitalcharitygovernance.repositories.RoleRepository;
import com.example.digitalcharitygovernance.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BlankAdminCreator implements CommandLineRunner {
    //this method is used to create a user with "ROLE_ADMIN" to gain initial access to the system
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public BlankAdminCreator(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public void run(String... args){
        try{
            if(userRepository.count()==0){
                Role initRoleAdmin = roleRepository.findByName("ROLE_ADMIN");
                if(initRoleAdmin == null){
                    initRoleAdmin = new Role();
                    initRoleAdmin.setName("ROLE_ADMIN");
                    roleRepository.save(initRoleAdmin);
                }
                User initUserAdmin = new User();
                initUserAdmin.setUsername("admin");
                initUserAdmin.setPassword(passwordEncoder.encode("admin"));
                initUserAdmin.setEmail("EMAILADDRESSFORADMINACCOUNT");
                initUserAdmin.setRoles(initRoleAdmin);
                userRepository.save(initUserAdmin);
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
