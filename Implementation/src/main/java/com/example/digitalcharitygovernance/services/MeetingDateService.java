package com.example.digitalcharitygovernance.services;

import com.example.digitalcharitygovernance.models.Meeting;
import com.example.digitalcharitygovernance.models.MeetingType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@Service
public class MeetingDateService {
    public LocalDateTime getCurrentDate(){
        return LocalDateTime.now(); //returns the local users system date time data
    }
    public int dayDifferenceBetweenDates(LocalDateTime date1, LocalDateTime date2){
        return (int)ChronoUnit.DAYS.between(date1,date2); //calculates the number of days between 2 dates
    }
    public boolean isDate1AfterDate2(LocalDateTime date1, LocalDateTime date2){
        return date1.isAfter(date2); //boolean value returning true if variable date 1 falls after date 2
    }
    public LocalDateTime nextMeetingDate(LocalDateTime lastMeetingDate, int recurringFreqValNum, String recurringFreqValText) {
        //method used to calculate when the next meeting should be held. takes the text value and adds that frequency on recurringFreqValNum times.
        return switch (recurringFreqValText) {
            case "Day_freq" -> lastMeetingDate.plusDays(recurringFreqValNum);
            case "Week_freq" -> lastMeetingDate.plusWeeks(recurringFreqValNum);
            case "Month_freq" -> lastMeetingDate.plusMonths(recurringFreqValNum);
            case "Year_freq" -> lastMeetingDate.plusYears(recurringFreqValNum);
            default -> throw new IllegalArgumentException("Frequency Unknown");
        };
    }
    public static int getDaysUntilNextMeeting(LocalDateTime currentDate, LocalDateTime nextMeetingDate) {
        return (int)ChronoUnit.DAYS.between(currentDate,nextMeetingDate); // calculates the number of days until the next meeting
    }
    public static LocalDateTime calculateReminderDate(LocalDateTime nextMeetingDate, int noticePeriod) {
        return nextMeetingDate.minusDays(noticePeriod); //calulates the date from which the users need to be reminded a meeting should be held for the meeting type
    }
    public boolean calcIsMeetingScheduledSinceLastMeetingOfType(ArrayList<Meeting> meetingsOfRole, MeetingType currentMeetingType, LocalDateTime lastMeetingDate) {
        //method used to determine since the last meeting held of a given type whether a meeting is scheduled so that if not a reminder can be shown
        for (Meeting meeting : meetingsOfRole){
            if(meeting.getMeetingType().equals(currentMeetingType) && meeting.getDateTime().isAfter(lastMeetingDate)){
                return true;
            }
        }
        return false;
    }
}
