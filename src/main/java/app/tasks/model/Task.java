package app.tasks.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Type;

import java.util.List;

@Table(name = "task")
@Entity
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Task {
    @Id
    @Column(nullable = false)
    private String id;
    @Column(nullable = false)
    private String userId;
    private long deadlineDate;
    private long priority;
//    @Type(io.hypersistence.utils.hibernate.type.array.ListArrayType.class)
//    @Column(name = "tags", columnDefinition = "text[]")
    @Type(JsonType.class)
    @Column(name = "tags", columnDefinition = "jsonb")
    private List<String> tags;
    private String repeatFreq;
    private String description;
    private boolean isStarred;
    private boolean isDone;
    private long lastUpdateTs;
}