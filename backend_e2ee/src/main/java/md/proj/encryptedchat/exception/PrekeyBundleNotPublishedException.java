package md.proj.encryptedchat.exception;

import org.springframework.security.core.AuthenticationException;

public class PrekeyBundleNotPublishedException extends AuthenticationException {
    public PrekeyBundleNotPublishedException(String message) {
        super(message);
    }
}
