package com.crm.system.playload.request;

import com.crm.system.models.history.TagName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SentEmailDTO {
    private String email;
    private String subjectOfMail;
    private String textOfEmail;
    private TagName tagName;
    private long tagId;
}
