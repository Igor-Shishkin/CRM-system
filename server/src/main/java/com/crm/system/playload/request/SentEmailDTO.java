package com.crm.system.playload.request;

import com.crm.system.models.logForUser.TagName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SentEmailDTO {
    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    private String subjectOfMail;
    @NotEmpty
    private String textOfEmail;
    private TagName tagName = TagName.EMAIL;
}
