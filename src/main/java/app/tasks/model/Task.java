package app.tasks.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema
public class Task {
    @Id
    @Column(nullable = false)
    private String id;
    @Column(name = "user_id", nullable = false)
    private String userId;
    private Long deadlineDate;
    private Long priority;
    //    @Type(io.hypersistence.utils.hibernate.type.array.ListArrayType.class)
//    @Column(name = "tags", columnDefinition = "text[]")
    @Type(JsonType.class)
    @Column(name = "tags", columnDefinition = "jsonb")
    private List<String> tags;
    private String repeatFreq;
    @Schema(defaultValue = "sample desc")
    private String description;
    private Boolean isStarred;
    @Column(name = "is_done")
    @Schema(description = "Is task done", type = "boolean", example = "true/false",defaultValue = "false")
    private Boolean isDone;
//    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long lastUpdateTs;
}