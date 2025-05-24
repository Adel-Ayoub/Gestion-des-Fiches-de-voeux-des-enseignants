package usthb.fi.fichevoeux.messaging;
import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "CHAT_MESSAGE")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_ID")
    private String id;
    @Column(name = "CONTENT", nullable = false)
    private String content;
    @Column(name = "SENDER", nullable = false)
    private String sender;
    @Column(name = "USER_ID", nullable = false)
    private String userId;
    @Column(name = "TIMESTAMP", nullable = false)
    private LocalDateTime timestamp;
    @Column(name = "IS_ADMIN", nullable = false)
    private boolean isAdmin;
    @Column(name = "READ_STATUS", nullable = false)
    private boolean read;

 

   }
