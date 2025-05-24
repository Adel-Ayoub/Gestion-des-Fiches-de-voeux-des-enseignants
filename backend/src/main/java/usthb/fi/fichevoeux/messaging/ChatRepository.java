package usthb.fi.fichevoeux.messaging;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
public interface ChatRepository extends JpaRepository<ChatMessage, String> {

    // Method to find messages by userId
    List<ChatMessage> findByUserId(String userId);

    // Method to find messages by sender
    List<ChatMessage> findBySender(String sender);

    // Method to find messages by timestamp range
    List<ChatMessage> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    // Method to find messages by isAdmin status
    List<ChatMessage> findByIsAdmin(boolean isAdmin);
}
