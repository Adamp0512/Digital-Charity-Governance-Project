package com.example.digitalcharitygovernance.services;

import com.example.digitalcharitygovernance.models.Meeting;
import com.example.digitalcharitygovernance.models.Role;
import com.example.digitalcharitygovernance.models.User;
import org.springframework.stereotype.Service;

import java.util.Iterator;


@Service
public class QuorumCalcService {
    public static int calculateQuorumValue(Meeting meeting, Iterable<User> allUsers) {
        //method calculates the quorum value based on the number of users invited to the meeting and
        //the quorum percentage for that type
        Float quorumNum = meeting.getMeetingType().getQuorumNum();
        int totalNumUsersInvited = numInvited(meeting, allUsers);

        System.out.println("TOTAL NUM INVITED = " + totalNumUsersInvited);
        int numRequiredForQuorum = (int) Math.ceil((quorumNum/100 * totalNumUsersInvited));

        System.out.println("QUORUM CALC");
        return numRequiredForQuorum;
    }

    public static int numInvited(Meeting meeting, Iterable<User> allUsers) {
        //method iterates over all roles for the meeting type and counts the number of users that
        //possess that role keeping the number as a counter which is then returned
        Iterator<Role> rolesPresentIter = meeting.getMeetingRoles().iterator();
        int totalNumUsersInvited = 0;
        while (rolesPresentIter.hasNext()){
            Role currentRole = rolesPresentIter.next();
            Iterator<User> allUsersIter = allUsers.iterator();
            while (allUsersIter.hasNext()){
                User currentUser = allUsersIter.next();
                if (currentUser.getRole() == currentRole){
                    totalNumUsersInvited++;
                }
            }
        }
        return totalNumUsersInvited;
    }
}
