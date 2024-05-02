package com.example.digitalcharitygovernance.models;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Entity
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;



    @ManyToOne
    @JoinColumn(name = "meeting_type_id")
    private MeetingType meetingType;

    private String title;
    private LocalDateTime dateTime;
    private String location;

    @ManyToMany
    @JoinTable(
            name = "meeting_roles",
            joinColumns = @JoinColumn(name = "meeting_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> meetingRoles;

    @OneToMany(mappedBy = "meeting", cascade = CascadeType.ALL)
    private List<MeetingAction> meetingActions = new ArrayList<>();

    private Boolean meetingComplete;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MeetingType getMeetingType() {
        return meetingType;
    }

    public void setMeetingType(MeetingType meetingType) {
        this.meetingType = meetingType;
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Set<Role> getMeetingRoles() {
        return meetingRoles;
    }

    public void setMeetingRoles(Set<Role> meetingRoles) {
        this.meetingRoles = meetingRoles;
    }

    public Boolean getMeetingComplete() {return meetingComplete;}

    public void setMeetingComplete(Boolean meetingComplete) {this.meetingComplete = meetingComplete;}

    public List<MeetingAction> getMeetingActions() {return meetingActions;}

    public void setMeetingActions(List<MeetingAction> meetingActions) {this.meetingActions = meetingActions;}



}
