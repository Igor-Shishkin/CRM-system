package com.crm.system.playload.response;

import com.crm.system.models.history.TagName;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TagForHistoryMessageDTO {
    @Enumerated(EnumType.STRING)
    private TagName tagName;
    private long entityId;
    private String entityName;
}
