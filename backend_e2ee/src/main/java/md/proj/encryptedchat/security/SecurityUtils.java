package md.proj.encryptedchat.security;

import md.proj.encryptedchat.user.User;
import md.proj.encryptedchat.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class SecurityUtils {

    private static UserService userService;

    @Autowired
    public SecurityUtils(UserService userService) {
        SecurityUtils.userService = userService;
    }

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null ? authentication.getName() : null;
    }

    public static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }

    public static void checkOwnershipOrAdmin(UUID id) {
        if (!isAdmin()) {
            String currentUsername = getCurrentUsername();
            User user = userService.findUserById(id);
            if (!user.getUsername().equals(currentUsername)) {
                throw new SecurityException("Access denied. You are not the owner of this user.");
            }
        }
    }

    public static UUID getAuthenticatedUserId() {
        String currentUsername = getCurrentUsername();
        User user = userService.findByUsername(currentUsername);
        if (user != null) {
            return user.getId();
        }
        throw new SecurityException("Authenticated user not found.");
    }
}
