package org.example.request;

import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;
import org.bson.conversions.Bson;

@Data
public class Metadata   {
    private String parentResourceId;

    public Metadata(String parentResourceId) {
        this.parentResourceId = parentResourceId;
    }
}
