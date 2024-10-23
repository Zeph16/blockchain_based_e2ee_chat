package md.proj.encryptedchat.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private ErrorType type;
    private String details;
    private String path;
    private LocalDateTime timestamp;
}
