package app.tasks.model.HTTPModels;

import app.tasks.enums.AccessType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SharePostInputModel {
    String taskId;
    String toUserId;
    AccessType accessType;
}
