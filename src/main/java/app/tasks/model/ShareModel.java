package app.tasks.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@Table(name = "sharing")
//@IdClass(ShareId.class)
@Entity
@NoArgsConstructor
@Setter
@Getter
@ToString
@AllArgsConstructor
public class ShareModel {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;
    @Column(name="user_id")
    private String userId;
    @Column(name = "task_id", updatable=false, insertable = false)
    private String taskId;
    private long updateTs;
    private String accessType;

}
