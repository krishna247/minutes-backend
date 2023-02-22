package app.tasks.model.websocket;

import lombok.AllArgsConstructor;

import java.security.Principal;
//@Getter
//@Setter
@AllArgsConstructor
public
class StompPrincipal implements Principal {
    String userId;
    String sessionToken;

    @Override
    public String getName() {
        return userId+"-"+sessionToken;
    }
}