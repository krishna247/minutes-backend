package app.tasks.handler;

import app.tasks.model.LoginWindowsResponseModel;
import app.tasks.model.SessionModel;
import app.tasks.repository.SessionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@RestController
public class AuthWindowsHandler {
    @Autowired
    private Environment env;
    private final SessionRepository sessionRepository;

    public AuthWindowsHandler(SessionRepository sessionRepository){
        this.sessionRepository = sessionRepository;
    }


    @GetMapping("/login-windows-google")
    public void loginWindows(@RequestParam String code, @RequestParam String state ) throws Exception {
        // return session token and userId(firebase)
        HttpResponse response = Request.Post("https://oauth2.googleapis.com/token").bodyForm(
                Form.form()
                        .add("client_id",env.getProperty("auth.client_id"))
                        .add("client_secret", env.getProperty("auth.client_secret"))
                        .add("redirect_uri",env.getProperty("auth.redirect_uri"))
                        .add("code",code)
                        .add("grant_type","authorization_code")
                        .build()).execute().returnResponse();
        System.out.println(new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8));
        ObjectMapper objectMapper = new ObjectMapper();
//
        LoginWindowsResponseModel responseObj = objectMapper.readValue(response.getEntity().getContent(), LoginWindowsResponseModel.class);
//        System.out.println(responseObj.toString());
        String reqString = String.format("""
                {
                    "postBody":"id_token=%s&providerId=google.com",
                        "requestUri":"%s",
                        "returnSecureToken":true,
                        "returnIdpCredential":true
                }
                                
                """,responseObj.getId_token(),env.getProperty("auth.redirect_uri"));
        Response firebaseResponse = Request.Post("https://identitytoolkit.googleapis.com/v1/accounts:signInWithIdp?key=AIzaSyAki3Of0s4BqRHzTyng20ZshxLMZT01nB8")
                .bodyString(reqString, ContentType.APPLICATION_JSON).execute();
        String firebaseResponseStr = firebaseResponse.returnContent().toString();
        System.out.println(firebaseResponseStr);

        Map<String, Object> mapFromString = new HashMap<>();
        mapFromString = objectMapper.readValue(firebaseResponseStr, new TypeReference<Map<String, Object>>() {
        });
        System.out.println(mapFromString.toString());

        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken((String)(mapFromString.get("idToken")),true);
        String sessionToken = UUID.randomUUID().toString();
        sessionRepository.save(new SessionModel(decodedToken.getUid(),state,"Windows", decodedToken.getEmail(),
                sessionToken , new Date().toInstant().toEpochMilli()));

//        loginWindowsRepository.save(new LoginWindowsModel(state, (String) mapFromString.get("idToken"), new Date().toInstant().toEpochMilli()));
    }
}
