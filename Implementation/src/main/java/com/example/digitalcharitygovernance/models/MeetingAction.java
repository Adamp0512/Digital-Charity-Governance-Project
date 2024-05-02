package com.example.digitalcharitygovernance.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class MeetingAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String actionInfo;

    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "meeting_id")
    private Meeting meeting;

    public long getId() {return id;}

    public void setId(long id) {this.id = id;}

    public String getActionInfo() {return actionInfo;}

    public void setActionInfo(String actionInfo) {this.actionInfo = actionInfo;}

    public LocalDateTime getTimestamp() {return timestamp;}

    public void setTimestamp(LocalDateTime timestamp) {this.timestamp = timestamp;}

    public Meeting getMeeting() {return meeting;}

    public void setMeeting(Meeting meeting) {this.meeting = meeting;}
}
