package app.tasks.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Table(name = "sessions")
@Entity
@NoArgsConstructor
@Setter
@Getter
@ToString
@IdClass(SessionId.class)
@AllArgsConstructor
public class SessionModel implements Serializable {
    @Id
    String userId;
    @Id
    String deviceId;
    String deviceInfo;
    String email;
    @Column(nullable = false)
    String sessionToken;
    long createTimestamp;
}
