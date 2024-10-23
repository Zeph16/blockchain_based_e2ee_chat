package md.proj.encryptedchat.exception;

import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.resource.NoResourceFoundException;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}