package com.example.digitalcharitygovernance.repositories;

import com.example.digitalcharitygovernance.models.Meeting;
import com.example.digitalcharitygovernance.models.MeetingAction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MeetingActionRepository extends CrudRepository<MeetingAction, Long> {
    List<MeetingAction> findByMeeting(Meeting meeting);
}
