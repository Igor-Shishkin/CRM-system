package com.crm.system.services.utils.logUtils;

import com.crm.system.models.User;
import com.crm.system.playload.response.TagForUserLogDTO;

import java.util.Set;

public interface LogTagsCreator {
    Set<TagForUserLogDTO> getTags(User activeUser);
}
