package com.crm.system.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "history_messages")
@Getter
@Setter
public class HistoryMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long message_id;

    @NotBlank
    @Column(name = "text_of_message")
    private String messageText;

    @Column(name = "data_of_creation")
    private LocalDateTime dataOfCreation;

    @Column(name = "is_important")
    private boolean isImportant;

    @Column(name = "is_done")
    private boolean isDone;

    private String note;
    @ManyToOne
    @JoinColumn(name = "lid_id")
    @JsonBackReference
    private Client client;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
}
