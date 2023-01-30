package app.tasks.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
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
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    private String id;

    @Column(name = "user_id", updatable=false)
    private String userId;
    private long deadlineDate;
    private long priority;
    @Type(io.hypersistence.utils.hibernate.type.array.StringArrayType.class)
    @Column(name = "tags", columnDefinition = "text[]")
    private String[] tags;
    private String repeatFreq;
    private String description;
    private boolean isStarred;
    private boolean isDone;

//    @Type(JsonType.class)
//    @Column(name = "sub_tasks", columnDefinition = "jsonb")
//    private SubTasks subTasks;
    private long lastUpdateTs;
//    @OneToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
//    @JoinColumns(
//            {@JoinColumn(name = "id", referencedColumnName = "task_id", updatable=false),
//                    @JoinColumn(name = "user_uuid", referencedColumnName = "user_id", updatable=false  )}
//    )
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumns({
//            @JoinColumn(name = "user_ID",referencedColumnName = "user_id", nullable = false),
            @JoinColumn(name = "task_id",referencedColumnName = "id", nullable = false)
    })
    private List<ShareModel> shareModel;

}