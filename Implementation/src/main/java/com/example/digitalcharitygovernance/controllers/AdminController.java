package com.example.digitalcharitygovernance.controllers;


import com.example.digitalcharitygovernance.models.*;
import com.example.digitalcharitygovernance.repositories.*;
import com.example.digitalcharitygovernance.security.AuthDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Controller	// This means that this class is a Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CharityRepository charityRepository;

	@Autowired
	private PurposeRepository purposeRepository;

	@Autowired
	private MeetingRepository meetingRepository;

	@Autowired
	private MeetingTypeRepository meetingTypeRepository;

	@Autowired
	private MeetingActionRepository meetingActionRepository;

	@Autowired // This means to get the bean called userRepository
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private final AuthDetails authDetails;

	public AdminController(AuthDetails authDetails) {
		this.authDetails = authDetails;
	}




	@GetMapping("/panel") // Display form
	public String showAddUserForm(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("role", new Role());
		model.addAttribute("allRoles", roleRepository.findAll());
		model.addAttribute("charityCount", charityRepository.count());

		model.addAttribute("charityID", getCharityID());
		System.out.println("Current charity count " + charityRepository.count());
		return "add-user-form"; // HTML template name for the form
	}

	private Long getCharityID() {
		if (charityRepository.count() == 1){
			Iterable<Charity> onlyCharity = charityRepository.findAll();
			System.out.println(onlyCharity.iterator().next().getId());
			return onlyCharity.iterator().next().getId();
		}
		else{
			return(null);
		}

	}

	@GetMapping(path="/charity/add")
	public String addCharity(Model model) {
		model.addAttribute("backAddress","admin/panel");
		model.addAttribute("charity", new Charity());
		model.addAttribute("meetingType", new MeetingType());
		return "add-charity-form";
	}
	@GetMapping(path="/charity/{id}")
	public String viewCharitablePurposes(@PathVariable Long id, Model model){
		Charity charity = charityRepository.findById(id).orElse(null);
		model.addAttribute("backAddress","admin/panel");
		if(charity != null){
			model.addAttribute("charity", charity);
			List<CharitablePurpose> charitablePurposes = purposeRepository.findByCharity(charity);
			model.addAttribute("charitablePurposesList", charitablePurposes);

			return "edit-charitable-purposes-form";
		}
		else {
			return "redirect:/admin/panel";
		}
	}


	@PostMapping("/charity/add/add-charity") // Handle form submission
	public String addCharitySubmit(@ModelAttribute Charity charity) {

		System.out.println(charityRepository.count());
		charityRepository.save(charity);
		System.out.println(charity.getCharityName());

		return "redirect:/admin/panel";
	}



	@PostMapping("/charity/{id}/addCharitablePurpose")
	public String addCharitablePurposeToCharity(@PathVariable Long id, @RequestParam String charitablePurpose) {
		System.out.println("Found add method");

		Charity charity = charityRepository.findById(getCharityID()).orElse(null);
		if (charity != null) {
			System.out.println("Found charity");
			CharitablePurpose newPurpose = new CharitablePurpose();
			newPurpose.setCharitablePurpose(charitablePurpose);
			newPurpose.setCharity(charity);
			purposeRepository.save(newPurpose);
		}
		return "redirect:/admin/charity/" + id;
	}


	@GetMapping("/delete")
	public String deleteAllData(){
		System.out.println("Delete all");

		purposeRepository.deleteAll();
		charityRepository.deleteAll();

		meetingActionRepository.deleteAll();
		meetingRepository.deleteAll();

		Iterator<User> allUsersIterator = userRepository.findAll().iterator();
		while (allUsersIterator.hasNext()){
			User nextUser = allUsersIterator.next();
			if (nextUser.getRole().getName().equals("ROLE_ADMIN") == false){
				userRepository.delete(nextUser);
			}
		}
		Iterator<Role> allRolesIterator = roleRepository.findAll().iterator();
		while (allRolesIterator.hasNext()){
			Role nextRole = allRolesIterator.next();
			if (nextRole.getName().equals("ROLE_ADMIN") == false){
				roleRepository.delete(nextRole);
			}
		}

		return "redirect:/logout";
	}




	@PostMapping("/users/add") // Handle form submission
	public String addUserSubmit(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
		int numUsersWithUsername = userRepository.countByUsername(user.getUsername());
		if(numUsersWithUsername >= 1){
			redirectAttributes.addFlashAttribute("addUserMessage","Error - There already exists a user with the username " + user.getUsername());
		}
		else{
			String pText = user.getPassword();
			String pEncoded = passwordEncoder.encode(pText);
			user.setPassword(pEncoded);
			userRepository.save(user);
			redirectAttributes.addFlashAttribute("addUserMessage","Success - Added user with username - " + user.getUsername());
		}

		return "redirect:/admin/panel";
	}

	@GetMapping(path="/users/all/json")
	public @ResponseBody Iterable<User> getAllUsersJSON() {
		// This returns a JSON or XML with the users
		return userRepository.findAll();
	}



	@GetMapping(path="/users/all")
	public String getAllUsers(Model model) {
		Iterable<User> users = userRepository.findAll();
		model.addAttribute("users", users);
		model.addAttribute("backAddress","admin/panel");
		return "users-list";
	}



	@GetMapping(path="/users/search")
	public String searchUsers(@RequestParam String search_query, Model model) {
		Iterable<User> users = userRepository.findByConcatenatingNames(search_query);
		model.addAttribute("users", users);
		model.addAttribute("backAddress","admin/panel");
		return "users-list";
	}



	@PostMapping(path="/users/update/{id}")
	public String updateUsers(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
		User userToEdit = userRepository.findById(id).orElse(null);
		model.addAttribute("backAddress","admin/users/all");
		//String decryptedPassword = passwordEncoder.
		if(userToEdit != null){
			User tempUserToEdit = new User();
			tempUserToEdit.setId(userToEdit.getId());
			tempUserToEdit.setFirstName(userToEdit.getFirstName());
			tempUserToEdit.setSurname(userToEdit.getSurname());
			tempUserToEdit.setEmail(userToEdit.getEmail());
			tempUserToEdit.setUsername(userToEdit.getUsername());
			// leave password field.
			tempUserToEdit.setRoles(userToEdit.getRole());

			model.addAttribute("tempUserToEdit", tempUserToEdit);
			model.addAttribute("allRoles", roleRepository.findAll());
			model.addAttribute("userToEditRole", tempUserToEdit.getRole().getId());
			return "edit-user";
		}
		else
		{
			redirectAttributes.addFlashAttribute("userMessage", "Error - Unable to find user");
			return "redirect:/admin/users/all";
		}

	}

	@PostMapping("/users/{id}/edit")
	public String updateUserPost(@PathVariable Integer id, @ModelAttribute("tempUserToEdit") User tempUserToEdit, RedirectAttributes redirectAttributes){
		User existingUser = userRepository.findById(id).orElse(null);
		if((userRepository.countByUsername(tempUserToEdit.getUsername()) != 0) && (tempUserToEdit.getUsername().equals(existingUser.getUsername()) == false)){
			System.out.println("This username already exists");
			redirectAttributes.addFlashAttribute("editUserMessage","Error - The user " + tempUserToEdit.getUsername() + " already exists");
		}
		else if(existingUser == null){
			redirectAttributes.addFlashAttribute("editUserMessage","Error - Unable to find user");
		}
		else if((existingUser.getRole().getName().equals("ROLE_ADMIN")) &&  (tempUserToEdit.getRole().getName().equals("ROLE)ADMIN") == false) && (roleRepository.countByName("ROLE_ADMIN") <=1)){
			redirectAttributes.addFlashAttribute("editUserMessage","Error - Cannot change the role of the only ROLE_ADMIN user");
		}
		else{
			existingUser.setFirstName(tempUserToEdit.getFirstName());
			existingUser.setSurname(tempUserToEdit.getSurname());
			existingUser.setEmail(tempUserToEdit.getEmail());
			existingUser.setUsername(tempUserToEdit.getUsername());
			existingUser.setRoles(tempUserToEdit.getRole());

			if (tempUserToEdit.getPassword().equals("") == false){
				existingUser.setPassword(passwordEncoder.encode(tempUserToEdit.getPassword()));
			}
			userRepository.save(existingUser);
			redirectAttributes.addFlashAttribute("editUserMessage","Success - Updated user record");

		}

		return "redirect:/admin/users/all";
	}

	@PostMapping("/users/delete/{id}")
	public String DeleteUser(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {

		Optional<User> userToDelete = userRepository.findById(id);
		if(userToDelete.isPresent()){
			if(userToDelete.get().getUsername().equals(authDetails.getAuthenticatedUser().getUsername())){
				redirectAttributes.addFlashAttribute("userMessage","Error - Cannot delete currently logged-in user");
			}
			else if(userToDelete.get().getRole().getName().equals("ROLE_ADMIN")){
				System.out.println("Dealing with admin deletion");
				System.out.println(userRepository.countByRole(userToDelete.get().getRole()));

				if(userRepository.countByRole(userToDelete.get().getRole()) == 1){
					System.out.println("Can't Delete Last Admin User");
					redirectAttributes.addFlashAttribute("userMessage","Error - Cannot delete the last remaining ROLE_ADMIN user");
				}
				else{
					userRepository.deleteById(id);
					redirectAttributes.addFlashAttribute("userMessage","Success - Deleted user " + userToDelete.get().getUsername());
				}
			}
			else{
				userRepository.deleteById(id);
				redirectAttributes.addFlashAttribute("userMessage","Success - Deleted user " + userToDelete.get().getUsername());
			}
		}
		else{
			redirectAttributes.addFlashAttribute("userMessage","Error - User Not Found");
		}
		return "redirect:/admin/users/all";
	}

	@PostMapping("/roles/add") // Handle form submission
	public String addUserSubmit(@ModelAttribute Role role, RedirectAttributes redirectAttributes) {
		if (roleRepository.countByName(role.getName()) >=1 ){
			redirectAttributes.addFlashAttribute("addRoleMessage", "Error - A role with name " + role.getName() + " already exists");
		}
		else{
			roleRepository.save(role);
			redirectAttributes.addFlashAttribute("addRoleMessage", "Success - Added role - " + role.getName());
		}
		return "redirect:/admin/panel#admin-roles";
	}

	@PostMapping("/roles/update/{id}")
	public String UpdateRole(@PathVariable Integer id, @RequestParam String newRoleName, RedirectAttributes redirectAttributes) {
		Role role = roleRepository.findById(id).orElse(null);

		if(roleRepository.findById(id).get().getName().equals("ROLE_ADMIN")){
			System.out.println("Can't Update Admin Role");
			redirectAttributes.addFlashAttribute("roleMessage", "Error - Updating ROLE_ADMIN is not allowed");
		}
		else if (role != null) {
			redirectAttributes.addFlashAttribute("roleMessage", "Success - " + role.getName() + " is now called " + newRoleName);
			role.setName(newRoleName);
			roleRepository.save(role);
		}
		else{
			System.out.println("Error - Cant find role");
			redirectAttributes.addFlashAttribute("roleMessage", "Error - Unable to find role");

		}

		return "redirect:/admin/panel#admin-roles";
	}

	@PostMapping("/roles/delete/{id}")
	public String DeleteRole(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {

		Optional<Role> selectedRole = roleRepository.findById(id);
		if (selectedRole.isPresent()){
			System.out.println("Num of users with that role " + userRepository.countByRole(selectedRole));
		}

		if(userRepository.countByRole(selectedRole)==0){
			try{
			redirectAttributes.addFlashAttribute("roleMessage", "Success - Deleted role " + roleRepository.findById(id).get().getName());
			roleRepository.deleteById(id);
			}
			catch (Exception e){
				redirectAttributes.addFlashAttribute("roleMessage", "Error - Unable to delete role" + e);
			}

		} else if (roleRepository.findById(id).get().getName().equals("ROLE_ADMIN")) {
			redirectAttributes.addFlashAttribute("roleMessage", "Error - Deleting ROLE_ADMIN not allowed");
			System.out.println("Can't delete admin role");

		} else {
			redirectAttributes.addFlashAttribute("confirmDeletion", true);
			redirectAttributes.addFlashAttribute("deletePendingId", id);
			String message = "Warning - Deleting the role " + selectedRole.get().getName() + " will delete the users who belong to that role. To proceed select the 'Confirm Deletion' button below the role.";
			redirectAttributes.addFlashAttribute("roleMessage", message);

		}

		return "redirect:/admin/panel#admin-roles";
	}

	@PostMapping("/roles/delete/confirm/{id}")
	public String DeleteRole(@PathVariable Integer id) {

		Optional<Role> selectedRole = roleRepository.findById(id);
		if(selectedRole.isPresent()){
			Iterable<User> usersWithRole = userRepository.findAllByRole(selectedRole.get());
			usersWithRole.forEach((user) -> {
				int userToDeleteID = user.getId();
				userRepository.deleteById(userToDeleteID);
				System.out.println("Deleted User - " + user.getUsername());
			});


			roleRepository.deleteById(id);
		}
		else{
			System.out.println("Unable to delete records");
		}



		return "redirect:/admin/panel#admin-roles";
	}
}