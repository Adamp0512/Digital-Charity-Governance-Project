package com.example.digitalcharitygovernance.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class MeetingType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "meeting_type_name", nullable = false)
    private String meetingTypeName;

    @Column(nullable = false)
    private Boolean quorumRequired;

    private Float quorumNum;

    private int daysNotice;

    @Column(nullable = false)
    private Boolean recurringMeeting;

    private int recurringFreqValNum;

    private String recurringFreqValText;

    private LocalDateTime lastMeetingOfTypeDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMeetingTypeName() {
        return meetingTypeName;
    }

    public void setMeetingTypeName(String meetingTypeName) {
        this.meetingTypeName = meetingTypeName;
    }

    public Boolean getQuorumRequired() {
        return quorumRequired;
    }

    public void setQuorumRequired(Boolean quorumRequired) {
        this.quorumRequired = quorumRequired;
    }

    public Float getQuorumNum() {
        return quorumNum;
    }

    public void setQuorumNum(Float quorumNum) {
        this.quorumNum = quorumNum;
    }

    public int getDaysNotice() {
        return daysNotice;
    }

    public void setDaysNotice(int daysNotice) {
        this.daysNotice = daysNotice;
    }

    public Boolean getRecurringMeeting() {
        return recurringMeeting;
    }

    public void setRecurringMeeting(Boolean recurringMeeting) {
        this.recurringMeeting = recurringMeeting;
    }

    public int getRecurringFreqValNum() {
        return recurringFreqValNum;
    }

    public void setRecurringFreqValNum(int recurringFreqValNum) {
        this.recurringFreqValNum = recurringFreqValNum;
    }

    public String getRecurringFreqValText() {
        return recurringFreqValText;
    }

    public void setRecurringFreqValText(String recurringFreqValText) {
        this.recurringFreqValText = recurringFreqValText;
    }

    public LocalDateTime getLastMeetingOfTypeDate() {
        return lastMeetingOfTypeDate;
    }

    public void setLastMeetingOfTypeDate(LocalDateTime lastMeetingOfTypeDate) {
        this.lastMeetingOfTypeDate = lastMeetingOfTypeDate;
    }
}
