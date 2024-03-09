package com.crm.system.models.history;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class TagNameDeserializer extends JsonDeserializer<TagName> {
    @Override
    public TagName deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String value = p.getValueAsString();
        return TagName.valueOf(value);
    }
}
