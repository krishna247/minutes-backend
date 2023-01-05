package app.tasks.handler;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class OAuthHandlers {

    @GetMapping("/getUser")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        Collections Collections;
        System.out.println(principal.toString());
        return principal.getAttributes();
//		return java.util.Collections.singletonMap("name", principal.getAttribute("name"));
    }

}
