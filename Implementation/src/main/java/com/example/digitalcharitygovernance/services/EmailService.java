package com.example.digitalcharitygovernance.services;

import com.example.digitalcharitygovernance.models.Meeting;
import com.example.digitalcharitygovernance.models.Role;
import com.example.digitalcharitygovernance.models.User;
import com.example.digitalcharitygovernance.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.spec.PSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;



@Service
public class EmailService {

    @Autowired
    private UserRepository userRepository;

    public void SendEmail(String messageText, String subject, List<String> toList){
        final String username = "SENDER-EMAIL-ADDRESS";//System.getenv("EMAIL_UNAME");
        final String password = "SENDER-EMAIL-ADDRESS-MAIL-SERVER-PASSWORD";//System.getenv("EMAIL_KEY");
        final String host = "MAIL-SERVER-HOST-ADDRESS";
        final int port = 587;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new javax.mail.Authenticator(){
            protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(username,password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username)); //should be using sender email but since same as smtp username reusing the key
            toList.forEach((individualAddress) -> {
                try {
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(individualAddress));
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            });
            message.setSubject(subject);
            message.setContent(messageText, "text/html");

            Transport.send(message);

            System.out.println("Email sent successfully");
        } catch (MessagingException e){
            throw new RuntimeException(e);
        }
    }

    public void SendNewMeetingMessage(Meeting meeting, Set<Role> meetingRoles){
        String message = "<h1>New Meeting</h1><br><br><p>A new meeting has been created</p><br><p><b>Title</b>: " + meeting.getTitle() + "</p><br><p><b>Date and Time</b>: "+ meeting.getDateTime().toString() + "</p><br><p><b>Location</b>: " + meeting.getLocation() + "</p><br>";
        String subject = "New Meeting " + meeting.getTitle() + " " + meeting.getDateTime().toString();
        Iterator<User> allUsers = userRepository.findAll().iterator();
        ArrayList<User> usersToBeEmailed = new ArrayList<>();

        System.out.println("Reached email page");
        while (allUsers.hasNext()){
            User currentUser = allUsers.next();
            Boolean userCanBeEmailed = false;
            Iterator<Role> meetingRolesIterator = meetingRoles.iterator();
            while (meetingRolesIterator.hasNext()){
                if (currentUser.getRole().getName().equals(meetingRolesIterator.next().getName())){
                    userCanBeEmailed = true;
                }
            }
            if (userCanBeEmailed == true){
                System.out.println("MATCHING USER FOUND");
                usersToBeEmailed.add(currentUser);
            }



        }
        System.out.println("reached list initialisation");
        Iterator<User> recipients = usersToBeEmailed.iterator();
        while (recipients.hasNext()){
            System.out.println(recipients.next().getEmail());
        }
        List<String> toList = new ArrayList<String>();
        usersToBeEmailed.forEach((user -> toList.add(user.getEmail())));
        SendEmail(message, subject, toList);
    }



}
