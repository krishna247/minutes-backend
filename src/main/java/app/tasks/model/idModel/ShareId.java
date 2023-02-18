package app.tasks.model.idModel;


import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ShareId implements Serializable {
    String userId;
    String taskId;
}
