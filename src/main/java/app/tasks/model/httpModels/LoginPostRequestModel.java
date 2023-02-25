package app.tasks.model.httpModels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginPostRequestModel {
    private String idToken;
    private String deviceId;
    private String deviceInfo;
}
