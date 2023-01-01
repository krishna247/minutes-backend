package app.tasks.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Table(name = "task")
@Entity
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Task {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "user_uuid", nullable = false)
    private String userUuid;

    @Column(name = "deadline_date")
    private long deadlineDate;

    @Column(name = "priority")
    private long priority;

    @Column(name = "tags")
    private List<String> tags;

    @Column(name = "repeat_freq")
    private String repeatFreq;

    @Column(name = "description")
    private String description;

    @Column(name = "is_starred")
    private boolean isStarred;

    @Column(name = "is_done")
    private boolean isDone;

//    @Type(JsonType.class)
//    @Column(name = "sub_tasks", columnDefinition = "jsonb")
//    private SubTasks subTasks;

    @Column(name = "last_update_ts")
    private long lastUpdateTs;

}