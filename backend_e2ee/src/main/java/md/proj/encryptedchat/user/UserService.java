package md.proj.encryptedchat.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;
    public User findUserById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    public User findByUsername(String username) {
        return repository.findByUsername(username).orElse(null);
    }

    public List<User> findAllUsers() {
        return repository.findAll();
    }

    public User saveUser(User user) {
        return repository.save(user);
    }

    public User updateUser(UUID id, User updatedUser) {
        return repository.findById(id).map(user -> {
            user.setUsername(updatedUser.getUsername());
                if (!updatedUser.getPassword().isEmpty()) {
                    user.setPassword(updatedUser.getPassword());
                }
            return repository.save(user);
        }).orElse(null);
    }

    public void deleteUser(UUID id) {
        repository.deleteById(id);
    }

    public boolean existsByUsername(String username) {
        return repository.findByUsername(username).isPresent();
    }

    public User disconnect(User user) {
        User storedUser = repository.findByUsername(user.getUsername()).orElse(null);
        if (storedUser == null) {
            return null;
        }
        storedUser.setState(State.OFFLINE);
        storedUser.setLastChanged(LocalDateTime.now());
        return repository.save(storedUser);
    }

    public User connect(User user) {
        User storedUser = repository.findByUsername(user.getUsername()).orElse(null);
        if (storedUser == null) {
            return null;
        }
        storedUser.setState(State.ONLINE);
        storedUser.setLastChanged(LocalDateTime.now());
        return repository.save(storedUser);
    }

    public List<User> findConnectedUsers() {
        return repository.findAllByState(State.ONLINE);
    }
}