package app.tasks.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

@Table(name = "task")
@Entity
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Task {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    private String userUuid;

    private long deadlineDate;

    private long priority;

    @Type(io.hypersistence.utils.hibernate.type.array.StringArrayType.class)
    private String[] tags;

    private String repeatFreq;

    private String description;

    private boolean isStarred;

    private boolean isDone;

//    @Type(JsonType.class)
//    @Column(name = "sub_tasks", columnDefinition = "jsonb")
//    private SubTasks subTasks;

    private long lastUpdateTs;

}