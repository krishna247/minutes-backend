package app.tasks.utils;

import app.tasks.model.SessionModel;
import app.tasks.repository.SessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class AuthUtils {

    public static void isAuthenticated(String sessionToken,SessionRepository sessionRepository){
       List<SessionModel> result = sessionRepository.findBySessionToken(sessionToken);
       if(result.size() > 0) {
           System.out.println("Authorized");
//           return result.size() > 0;
       }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }
}
