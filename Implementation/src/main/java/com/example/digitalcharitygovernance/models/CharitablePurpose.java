package com.example.digitalcharitygovernance.models;

import jakarta.persistence.*;


@Entity
public class CharitablePurpose {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String charitablePurpose;

    @ManyToOne
    @JoinColumn(name = "charity_id")
    private Charity charity;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCharitablePurpose() {
        return charitablePurpose;
    }

    public void setCharitablePurpose(String charitablePurpose) {
        this.charitablePurpose = charitablePurpose;
    }

    public Charity getCharity() {
        return charity;
    }

    public void setCharity(Charity charity) {
        this.charity = charity;
    }
}
