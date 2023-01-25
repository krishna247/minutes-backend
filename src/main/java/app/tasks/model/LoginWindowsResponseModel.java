package app.tasks.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;

@Getter
@Setter
@ToString
public class LoginWindowsResponseModel {


    String id_token;
    int expires_in;
    String scope;
    String token_type;
    String access_token;
    HashMap<String, String> error;
    String refresh_token;
}
