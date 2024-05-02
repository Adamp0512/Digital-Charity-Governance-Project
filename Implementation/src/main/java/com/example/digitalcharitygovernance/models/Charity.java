package com.example.digitalcharitygovernance.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity
public class Charity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String charityName;



    @OneToMany(mappedBy = "charity", cascade = CascadeType.ALL)
    private List<CharitablePurpose> charitablePurposesList = new ArrayList<>();

    public String getCharityName() {
        return charityName;
    }

    public void setCharityName(String charityName) {
        this.charityName = charityName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CharitablePurpose> getCharitablePurposesList() {
        return charitablePurposesList;
    }

    public void setCharitablePurposesList(List<CharitablePurpose> charitablePurposesList) {
        this.charitablePurposesList = charitablePurposesList;
    }
}
