package app.tasks.model;

import app.tasks.model.idModel.ShareId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.*;

@Table(name = "sharing")
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
    private String accessType;
}
