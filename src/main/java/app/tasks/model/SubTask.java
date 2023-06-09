package app.tasks.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Table(name = "sub_task",indexes = {
        @Index(columnList = "taskId")
})
@Entity
@NoArgsConstructor
@Setter
@Getter
@ToString
public class SubTask {
    @Id
    private String id;
    private String taskId;
    private String description;
    private Boolean completed;
    private Long lastUpdateTs;
    private String parent;
}