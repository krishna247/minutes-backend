package app.tasks.model.idModel;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
public class SessionId implements Serializable {
    String userId;
    String deviceId;
}