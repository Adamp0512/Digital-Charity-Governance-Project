package com.example.digitalcharitygovernance.controllers;

import com.example.digitalcharitygovernance.models.*;
import com.example.digitalcharitygovernance.repositories.*;
import com.example.digitalcharitygovernance.security.AuthDetails;
import com.example.digitalcharitygovernance.services.EmailService;
import com.example.digitalcharitygovernance.services.MeetingDateService;
import com.example.digitalcharitygovernance.services.PDFService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Controller
@RequestMapping("/meetings")
public class MeetingController {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MeetingTypeRepository meetingTypeRepository;



    @Autowired
    private MeetingActionRepository meetingActionRepository;

    private final EmailService emailService;

    private final MeetingDateService meetingDateService;


    private final PDFService pdfService;

    private final AuthDetails authDetails;


    @Autowired
    public MeetingController(EmailService emailService, AuthDetails authDetails, PDFService pdfService, MeetingDateService meetingDateService){

    this.emailService = emailService;
    this.pdfService = pdfService;
    this.authDetails = authDetails;
    this.meetingDateService = meetingDateService;

    }


    @GetMapping("/list")
    public String showMeetings(Model model) {
        Iterator<MeetingType> allMeetingtypes = meetingTypeRepository.findAll().iterator();



        System.out.println(authDetails.getAuthenticatedUser().getAuthorities().toString());
        Iterator<Role> allRoles = roleRepository.findAll().iterator();
        Iterable<Meeting> allMeetings = meetingRepository.findAll();
        ArrayList<Meeting> meetingsOfRole = new ArrayList<>();

        for (Meeting meeting : allMeetings) {
            Iterator<Role> rolesOfMeetingIterator = meeting.getMeetingRoles().iterator();
            boolean roleFound = false;
            while (rolesOfMeetingIterator.hasNext()){
                String[] userRole = authDetails.getAuthenticatedUser().getAuthorities().toString().split(",");
                if (userRole[0].substring(1,userRole[0].length()-1).equals(rolesOfMeetingIterator.next().getName())){
                    roleFound = true;
                }
            }
            if (roleFound == true){
                meetingsOfRole.add(meeting);
            }
        }
        Collections.sort(meetingsOfRole, Comparator.comparing(Meeting::getDateTime).reversed()); //sorts into date descending order


        ArrayList<Object[]> meetingTypesWithMeetingsDueInfo = new ArrayList<>();

        while (allMeetingtypes.hasNext()) {

            LocalDateTime currentDate = meetingDateService.getCurrentDate();


            MeetingType currentMeetingType = allMeetingtypes.next();

            if ((currentMeetingType.getRecurringMeeting() == true) &&(currentMeetingType.getLastMeetingOfTypeDate() != null)) {

                System.out.println("Currently Checking = " + currentMeetingType.getMeetingTypeName());
                System.out.println("Current Date = " + currentDate);

                LocalDateTime lastMeetingDate = currentMeetingType.getLastMeetingOfTypeDate();
                System.out.println("Last Meeting Date = " + lastMeetingDate);
                int noticePeriod = currentMeetingType.getDaysNotice();
                System.out.println("Notice Period = " + noticePeriod);
                LocalDateTime nextMeetingDate = meetingDateService.nextMeetingDate(lastMeetingDate, currentMeetingType.getRecurringFreqValNum(), currentMeetingType.getRecurringFreqValText());

                System.out.println("Next Meeting Date = " + nextMeetingDate);

                LocalDateTime reminderDate = MeetingDateService.calculateReminderDate(nextMeetingDate, noticePeriod);
                System.out.println("Reminder Date = " + reminderDate);

                boolean isMeetingScheduledSinceLastMeetingOfType = meetingDateService.calcIsMeetingScheduledSinceLastMeetingOfType(meetingsOfRole, currentMeetingType, lastMeetingDate);

                System.out.println("Does Current Date = reminder Date" + currentDate.equals(reminderDate));
                System.out.println("Is Current Date After reminder Date" + currentDate.isAfter(reminderDate));
                System.out.println("Is there a meeting already scheduled " + isMeetingScheduledSinceLastMeetingOfType);

                if ((currentDate.equals(reminderDate) || currentDate.isAfter(reminderDate)) && (isMeetingScheduledSinceLastMeetingOfType == false)) {
                    meetingTypesWithMeetingsDueInfo.add(new Object[]{currentMeetingType, nextMeetingDate, noticePeriod});
                    System.out.println("Meeting Type added to list");
                }

            }

        }

        System.out.println("Calculated Meeting Types that are due");
        for (Object[] mtype : meetingTypesWithMeetingsDueInfo) {
            MeetingType meetingTypeRequired = (MeetingType) mtype[0];
            System.out.println("Meeting Due for " + meetingTypeRequired.getMeetingTypeName());
        }
        model.addAttribute("meetings", meetingsOfRole);
        model.addAttribute("allUsers", userRepository.findAll());
        model.addAttribute("mtypesDue", meetingTypesWithMeetingsDueInfo);
        return "meetings-list"; // Renders meetings-list.html
    }

    @GetMapping("/add-meeting-type")
    public String addMeetingType(Model model) {
        model.addAttribute("backAddress","meetings/list");
        model.addAttribute("meetingType", new MeetingType());
        return "new-meeting-type-form";
    }

    @PostMapping("/add-new-meeting-type") // Handle form submission
    public String addMeetingTypeSubmit(@ModelAttribute MeetingType meetingType, @RequestParam( name = "recurring-meeting-box", required = false) boolean recurringMeetingBoxInput, @RequestParam( name = "quorum-required-box", required = false) boolean quorumRequiredBoxInput) {
        meetingType.setRecurringMeeting(recurringMeetingBoxInput);
        meetingType.setQuorumRequired(quorumRequiredBoxInput);

        if (meetingType.getRecurringMeeting() == false){
            meetingType.setRecurringFreqValNum(0);
            meetingType.setRecurringFreqValText(null);
        }
        if (meetingType.getQuorumRequired() == false){
            meetingType.setQuorumNum(null);
        }
        meetingTypeRepository.save(meetingType);
        System.out.println(meetingType);

        return "redirect:/meetings/list"; // Redirect to show all meetings after submission
    }

    @GetMapping("/view-meeting-types")
    public String addCharity(Model model) {
        model.addAttribute("backAddress","meetings/list");
        model.addAttribute("allmtypes", meetingTypeRepository.findAll());
        return "view-meeting-type";
    }


    @GetMapping("/new")
    public String showNewMeetingForm(Model model) {
        model.addAttribute("allMeetingTypes", meetingTypeRepository.findAll());
        model.addAttribute("backAddress","meetings/list");
        model.addAttribute("meeting", new Meeting());
        ArrayList<String> allRolesNames = new ArrayList<>();
        Iterator<Role> allRoles = roleRepository.findAll().iterator();
        while (allRoles.hasNext())
            allRolesNames.add(allRoles.next().getName());
        System.out.println(allRolesNames);
        model.addAttribute("allRolesNames", allRolesNames);
        return "new-meeting-form"; // HTML template for new meeting form
    }

    @PostMapping("/new")
    public String createMeeting(@ModelAttribute Meeting meeting, @RequestParam("selectedRoleNames") ArrayList<String> selectedRoleNames, @RequestParam("mtypes") long selectedMeetingTypeID) {
        System.out.println("ROLES HERE");
        System.out.println("SELECTED ROLES - " + selectedRoleNames.toString());
        Set<Role> meetingRoles = new HashSet<>();
        for(String roleName : selectedRoleNames){
            Role foundRole = roleRepository.findByName(roleName);
            meetingRoles.add(foundRole);

        }

        Optional<MeetingType> tempMeetingType = meetingTypeRepository.findById(selectedMeetingTypeID);
        if (tempMeetingType.isPresent()){
            meeting.setMeetingType(meetingTypeRepository.findById(selectedMeetingTypeID).get());
        }
        else {
            meeting.setMeetingType(null);
        }
        meeting.setMeetingComplete(false);
        meeting.setMeetingRoles(meetingRoles);
        meetingRepository.save(meeting); // Save the new meeting to the database
        try {
            emailService.SendNewMeetingMessage(meeting, meetingRoles);
        }
        catch (Exception e){
            System.out.println("WARNING - Unable to send emails to meeting participant. Meeting still added");
            System.out.println(e);

        }
        return "redirect:/meetings/list"; // Redirect to the meetings list
    }

    @GetMapping("/{id}")
    public String viewMeetingDetails(@PathVariable Long id, Model model) {
        Meeting meeting = meetingRepository.findById(id).orElse(null);
        model.addAttribute("backAddress","meetings/list");
        if (meeting != null) {
            model.addAttribute("meeting", meeting);
            List<MeetingAction> meetingActions = meetingActionRepository.findByMeeting(meeting);
            // TODO: 17/03/2024 take into account the charity purposes so that all actions added must be compliant 
            model.addAttribute("meetingActions", meetingActions);
            if (meeting.getDateTime() != null) {
                String formattedDateTime = meeting.getDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"));
                model.addAttribute("formattedDateTime", formattedDateTime);
            }
            return "edit-meeting-form"; // HTML template for combined details and edit form
        } else {
            return "redirect:/meetings/list"; // when meeting not found return to list
        }
    }

    @PostMapping("/{id}/edit")
    public String editMeetingDetails(@PathVariable Long id, @ModelAttribute Meeting editedMeeting, @RequestParam( name = "meetingCompleteCheckbox", required = false) boolean meetingCompleteBoxInput) {
        Meeting meeting = meetingRepository.findById(id).orElse(null);
        if (meeting != null) {
            meeting.setTitle(editedMeeting.getTitle());
            meeting.setDateTime(editedMeeting.getDateTime());
            meeting.setLocation(editedMeeting.getLocation());
            meeting.setMeetingComplete(meetingCompleteBoxInput);
            if(meetingCompleteBoxInput == true){
                MeetingType changedDateMeetingType = meeting.getMeetingType();
                if (changedDateMeetingType.getLastMeetingOfTypeDate() == null){
                    System.out.println("Existing meeting type date null so replacing");
                    changedDateMeetingType.setLastMeetingOfTypeDate(editedMeeting.getDateTime());
                    System.out.println("Updating last meeting of type date");
                    meetingTypeRepository.save(changedDateMeetingType);
                }
                else if(meetingDateService.isDate1AfterDate2(editedMeeting.getDateTime(),meeting.getMeetingType().getLastMeetingOfTypeDate())){

                    changedDateMeetingType.setLastMeetingOfTypeDate(editedMeeting.getDateTime());
                    System.out.println("Updating last meeting of type date");
                    meetingTypeRepository.save(changedDateMeetingType);
                }
            }
            meetingRepository.save(meeting);
        }
        return "redirect:/meetings/" + id; // Redirect back to the meeting details page
    }


    @PostMapping("/{id}/addAction")
    public String addActionToMeeting(@PathVariable Long id, @RequestParam String actionInfo) {
        Meeting meeting = meetingRepository.findById(id).orElse(null);
        if (meeting != null) {
            MeetingAction newAction = new MeetingAction();
            newAction.setActionInfo(actionInfo);
            newAction.setTimestamp(LocalDateTime.now());
            newAction.setMeeting(meeting);
            meetingActionRepository.save(newAction);
        }
        return "redirect:/meetings/" + id;
    }
    @GetMapping("/{id}/download-meeting-pdf")
    public ResponseEntity<byte[]> downloadMeetingPDF(@PathVariable Long id){
        Meeting meetingToDownload = meetingRepository.findById(id).orElse(null);


        byte[] pdfToReturn;

        HttpHeaders returnHeaders = new HttpHeaders();
        returnHeaders.setContentType(MediaType.APPLICATION_PDF);
        returnHeaders.setContentDispositionFormData("attachment",meetingToDownload.getTitle() + ".pdf");
        try {
            pdfToReturn = pdfService.CreatePDF(meetingToDownload);
        }
        catch (Exception e){
            System.out.println(e);
            pdfToReturn = null;
        }
        System.out.println("Title of meeting to download" + meetingToDownload.getTitle());

        return ResponseEntity.ok().headers(returnHeaders).body(pdfToReturn);
    }


    @GetMapping("/delete/{id}")
    public String deleteMeeting(@PathVariable Long id) {
        meetingRepository.deleteById(id);

        return "redirect:/meetings/list";
    }
}