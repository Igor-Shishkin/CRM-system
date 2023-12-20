package com.crm.system.models.history;

import com.crm.system.models.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.util.Pair;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "history_messages")
@Getter
@Setter
public class HistoryMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @NotBlank
    @Column(name = "text_of_message")
    private String messageText;

    @Column(name = "date_of_creation")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateOfCreation;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deadline;

    @Column(name = "is_important")
    @JsonProperty("isImportant")
    private boolean isImportant;

    @Column(name = "is_done")
    @JsonProperty("isDone")
    private boolean isDone;

    private String note;

    @Column(name = "tag_name")
    @Enumerated(EnumType.STRING)
    @JsonDeserialize(using = TagNameDeserializer.class)
    private TagName tagName;

    @Column(name = "tag_id")
    private long tagId;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    public HistoryMessage(String messageText) {
        this.messageText = messageText;
        this.dateOfCreation = LocalDateTime.now();
    }
    public HistoryMessage() {
    }
}
