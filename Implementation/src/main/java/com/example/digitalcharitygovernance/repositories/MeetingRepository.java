package com.example.digitalcharitygovernance.repositories;

import com.example.digitalcharitygovernance.models.Meeting;
import org.springframework.data.repository.CrudRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called meetingRepository

public interface MeetingRepository extends CrudRepository<Meeting, Long> {

}