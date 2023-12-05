package com.crm.system.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
public class HistoryMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(name = "text_of_message")
    private String messageText;
    @Column(name = "data_of_creation")
    private LocalDateTime dataOfCreation;
    private boolean isImportant;
    private boolean isDone;
    @OneToMany(mappedBy = "message", cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private Set<Note> notes;
    @ManyToOne
    @JoinColumn(name = "lid_id")
    @JsonBackReference
    private Lid lid;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;



}
