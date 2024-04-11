package com.crm.system.models.logForUser;

import com.crm.system.models.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_for_user")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "entry_id")
    private Long entryId;

    @NotBlank
    @Column(name = "text")
    private String text;

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

    @Column(name = "additional_information")
    private String additionalInformation;

    @Column(name = "tag_name")
    @Enumerated(EnumType.STRING)
    @JsonDeserialize(using = TagNameDeserializer.class)
    private TagName tagName;

    @Column(name = "tag_id")
    private long tagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    public LogEntry(String text) {
        this.text = text;
        this.dateOfCreation = LocalDateTime.now();
    }


    public static class Builder {
        private Long entryId;
        private String text;
        private LocalDateTime deadline;
        private boolean isImportant;
        private boolean isDone;
        private String additionalInformation;
        private TagName tagName;
        private long tagId;
        private User user;

        public Builder() {
        }
        public Builder(LogEntry logEntry) {
            this.text = logEntry.getText();
            this.deadline = logEntry.getDeadline();
            this.isImportant = logEntry.isImportant();
            this.isDone = logEntry.isDone();
            this.additionalInformation = logEntry.getAdditionalInformation();
            this.tagId = logEntry.getTagId();
            this.tagName = logEntry.getTagName();
            this.user = logEntry.getUser();
        }

        public Builder withEntryId(long entryId) {
            this.entryId = entryId;
            return this;
        }
        public Builder withText(String text) {
            this.text = text;
            return this;
        }
        public Builder withDeadline(LocalDateTime deadline) {
            this.deadline = deadline;
            return this;
        }
        public Builder withIsImportant(boolean isImportant) {
            this.isImportant = isImportant;
            return this;
        }
        public Builder withIsDone(boolean isDone) {
            this.isDone = isDone;
            return this;
        }
        public Builder withAdditionalInformation(String note) {
            this.additionalInformation = note;
            return this;
        }
        public Builder withTagName(TagName tagName) {
            this.tagName = tagName;
            return this;
        }
        public Builder withTagId(long tagId) {
            this.tagId = tagId;
            return this;
        }
        public Builder withUser(User user) {
            this.user = user;
            return this;
        }
        public LogEntry build(){
            return new LogEntry(this.entryId,
                    this.text,
                    LocalDateTime.now(),
                    this.deadline,
                    this.isImportant,
                    this.isDone,
                    this.additionalInformation,
                    this.tagName,
                    this.tagId,
                    this.user);
        }
    }



    @Override
    public String toString() {
        return "HistoryMessage{" +
                "messageId=" + entryId +
                ", text='" + text + '\'' +
                ", additionalInformation='" + text + '\'' +
                ", dateOfCreation=" + dateOfCreation +
                ", deadline=" + deadline +
                ", tagName=" + tagName +
                ", tagId=" + tagId +
                '}';
    }
}
