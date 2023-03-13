package app.tasks.model;

import app.tasks.enums.AccessType;
import app.tasks.model.idModel.ShareId;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "sharing", indexes = {
        @Index(columnList = "taskId"),
        @Index(columnList = "taskId, userId")
})
@IdClass(ShareId.class)
@Entity
@NoArgsConstructor
@Setter
@Getter
@ToString
@AllArgsConstructor
public class ShareModel {
    @Id
    private String userId;
    @Id
    private String taskId;
    private long updateTs;
    @Enumerated(EnumType.STRING)
    private AccessType accessType;
}
