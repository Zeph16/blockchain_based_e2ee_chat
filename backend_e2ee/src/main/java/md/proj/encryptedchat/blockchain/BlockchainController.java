package md.proj.encryptedchat.blockchain;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test/blockchain")
@RequiredArgsConstructor
public class BlockchainController {
    private final BlockchainService blockchainService;

    @GetMapping("/prekey")
    public ResponseEntity<String> getClientVersion() {
        try {
            String clientVersion = blockchainService.hasPrekeyBundle("alice") ? "Alice has a prekey bundle" : "Alice doesn't have a prekey bundle";
            return ResponseEntity.ok(clientVersion);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}