package app.tasks.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubTask {

    private String text;
    private boolean isDone;
}
