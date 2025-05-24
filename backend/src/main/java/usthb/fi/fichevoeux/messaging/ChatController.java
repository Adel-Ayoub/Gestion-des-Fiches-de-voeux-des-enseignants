package usthb.fi.fichevoeux.messaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/messages")
public class ChatController {
    @Autowired
    private ChatRepository repo;

    @PostMapping
    public ResponseEntity<ChatMessage> saveMessage(@RequestBody ChatMessage message) {
        message.setTimestamp(LocalDateTime.now());
        ChatMessage savedMessage = repo.save(message);
        return ResponseEntity.ok(savedMessage);
    
    }
    @GetMapping("/history/{userId}")
    public List<ChatMessage> getMessageHistory(@PathVariable String userId) {
        return repo.findByUserId(userId);
    }
    @GetMapping("/sent/{userId}")
    public List<ChatMessage> getSentMessages(@PathVariable String userId) {
        return repo.findBySender(userId);
    }
    @PutMapping("/read/{messageId}")
    public void markMessageAsRead(@PathVariable String messageId) {
        Optional<ChatMessage> optionalMessage = repo.findById(messageId);
        if (optionalMessage.isPresent()) {
            ChatMessage message = optionalMessage.get();
            message.setRead(true);
            repo.save(message);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found");
        }
    }
};
