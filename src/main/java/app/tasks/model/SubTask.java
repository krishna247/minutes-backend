package app.tasks.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;
    private String taskUuid;
    private String text;
    private Boolean completed;
    private Long lastUpdateTs;
}