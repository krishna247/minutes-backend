package app.tasks.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Table(name = "sub_task")
@Entity
@NoArgsConstructor
@Setter
@Getter
@ToString
public class SubTask {
    @Id
    private String id;
    private String taskUuid;
    private String text;
    private Boolean completed;
    private Long lastUpdateTs;
}