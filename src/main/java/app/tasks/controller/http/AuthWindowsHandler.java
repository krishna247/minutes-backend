package app.tasks.controller.http;

import app.tasks.model.LoginWindowsResponseModel;
import app.tasks.model.SessionModel;
import app.tasks.model.User;
import app.tasks.repository.SessionRepository;
import app.tasks.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import io.swagger.v3.oas.annotations.Operation;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@RestController
public class AuthWindowsHandler {
    private final Environment env;
    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public AuthWindowsHandler(SessionRepository sessionRepository,
                              UserRepository userRepository, Environment env) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.env = env;
    }


    @Operation(hidden = true)
    @GetMapping("/login-windows-google")
    public void loginWindows(@RequestParam String code, @RequestParam String state) throws Exception {
        // return session token and userId(firebase)
        ObjectMapper objectMapper = new ObjectMapper();

        HttpResponse response = Request.Post("https://oauth2.googleapis.com/token").bodyForm(
                Form.form()
                        .add("client_id", env.getProperty("auth.client_id"))
                        .add("client_secret", env.getProperty("auth.client_secret"))
                        .add("redirect_uri", env.getProperty("auth.redirect_uri"))
                        .add("code", code)
                        .add("grant_type", "authorization_code")
                        .build()).execute().returnResponse();

        LoginWindowsResponseModel responseObj = objectMapper.readValue(response.getEntity().getContent(), LoginWindowsResponseModel.class);
        String reqString = String.format("""
                {
                    "postBody":"id_token=%s&providerId=google.com",
                        "requestUri":"%s",
                        "returnSecureToken":true,
                        "returnIdpCredential":true
                }
                """, responseObj.getId_token(), env.getProperty("auth.redirect_uri"));
        Response firebaseResponse = Request.Post("https://identitytoolkit.googleapis.com/v1/accounts:signInWithIdp?key=AIzaSyAki3Of0s4BqRHzTyng20ZshxLMZT01nB8")
                .bodyString(reqString, ContentType.APPLICATION_JSON).execute();
        String firebaseResponseStr = firebaseResponse.returnContent().toString();

        HashMap<String, Object> mapFromString = (HashMap<String, Object>) objectMapper.readValue(firebaseResponseStr, new TypeReference<Map<String, Object>>() {
        });

        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken((String) (mapFromString.get("idToken")), true);

        // TODO split state on | to get deviceId|deviceInfo
        String[] stateSplit = state.split("\\|");
        String loginState = stateSplit[0];
        String deviceId = stateSplit[1];
        String deviceInfo = stateSplit[2];
        sessionRepository.save(new SessionModel(decodedToken.getUid(), deviceId, deviceInfo ,decodedToken.getEmail(),
                UUID.randomUUID().toString(), new Date().toInstant().toEpochMilli(),loginState));

        if (!userRepository.existsById(decodedToken.getUid())) {
            userRepository.save(new User(decodedToken.getUid(), decodedToken.getName(), decodedToken.getPicture(), null));
        }
    }
}
