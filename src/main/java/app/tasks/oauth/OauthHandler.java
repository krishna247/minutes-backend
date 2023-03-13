//package app.tasks.oauth;
//
//import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
//import com.google.api.client.auth.oauth2.Credential;
//import com.google.api.client.auth.oauth2.TokenResponse;
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
//import jakarta.inject.Inject;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//import java.util.List;
//
//@RestController
//public class OauthHandler {
//
//    @Value("${auth.client_id}")
//    private String clientId; //= "823723763359-9u0ptkpe0bn9he01ukvohl6pse59pg7q.apps.googleusercontent.com";
//    String redirectUri = "http://localhost:8080/login-calendar";
//    List<String> scopes = List.of("https://www.googleapis.com/auth/calendar.readonly");
//    @Inject
//    AuthorizationCodeFlow flow;
//
//    @GetMapping("/testAuth")
//    public void test(HttpServletResponse response) throws IOException {
////        String userId = authService.isAuthenticated(sessionToken);
//        String userId = "token";
//        Credential credential = flow.loadCredential(userId);
//        if (credential != null) {
//            System.out.println(credential.getAccessToken());
//            return;
//        }
//        String url = new GoogleAuthorizationCodeRequestUrl(clientId, redirectUri, scopes)
//                .setAccessType("offline")
//                .setState(userId).build();
//        System.out.println(url);
//        response.sendRedirect(url);
//    }
//
//    @GetMapping("/login-calendar")
//    public void callback(@RequestParam String code, @RequestParam(value="state") String userId) throws IOException {
//        System.out.println(code);
//        TokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(redirectUri).execute();
//        flow.createAndStoreCredential(tokenResponse, userId);
//    }
//}
