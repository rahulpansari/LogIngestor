package org.example.request;

import jnr.posix.Times;
import lombok.Data;
import org.apache.kafka.common.protocol.types.Field;

import java.security.Timestamp;
import java.sql.Time;

@Data
public class TestRequest extends IRequest{
    private String level;
    private String message;
    private String resourceId;
    private String timestamp;
    private String traceId;
    private String spanId;

    private String commit;

    private Metadata metadata;

}
