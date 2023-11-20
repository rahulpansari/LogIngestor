package org.example.request;

import lombok.Data;

@Data
public class NewTestRequest {
    private String level;
    private String message;
    private String resourceId;
    private String timestamp;
    private String traceId;
    private String spanId;

    private String commit;

    private Metadata metadata;

    private String metaDataId;
}
