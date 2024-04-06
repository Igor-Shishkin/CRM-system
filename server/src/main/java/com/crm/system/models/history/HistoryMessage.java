package com.crm.system.models.history;

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
@Table(name = "history_messages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    public HistoryMessage(String messageText) {
        this.messageText = messageText;
        this.dateOfCreation = LocalDateTime.now();
    }
    public static class Builder {
        private Long messageId;
        private String messageText;
        private LocalDateTime deadline;
        private boolean isImportant;
        private boolean isDone;
        private String note;
        private TagName tagName;
        private long tagId;
        private User user;
        public Builder withMessageId(long messageId) {
            this.messageId = messageId;
            return this;
        }
        public Builder withMessageText(String messageText) {
            this.messageText = messageText;
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
        public Builder withNote(String note) {
            this.note = note;
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
        public HistoryMessage build(){
            return new HistoryMessage(this.messageId,
                    this.messageText,
                    LocalDateTime.now(),
                    this.deadline,
                    this.isImportant,
                    this.isDone,
                    this.note,
                    this.tagName,
                    this.tagId,
                    this.user);
        }
    }


    @Override
    public String toString() {
        return "HistoryMessage{" +
                "messageId=" + messageId +
                ", messageText='" + messageText + '\'' +
                ", dateOfCreation=" + dateOfCreation +
                ", deadline=" + deadline +
                ", tagName=" + tagName +
                ", tagId=" + tagId +
                '}';
    }
}
