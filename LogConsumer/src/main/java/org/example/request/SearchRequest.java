package org.example.request;

import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;

@Data
public class SearchRequest  extends IRequest{
    private String level;
    private String message;
    private String resourceId;
    private String[] timestamps;
    private String traceId;
    private String spanId;

    private String commit;

    private Metadata metadata;
}
