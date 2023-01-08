package app.tasks.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;

@Component
public class FirebaseApp {

    @PostConstruct
    public void setup() throws IOException {
        System.out.println(System.getProperty("user.dir"));
        FileInputStream firebaseCredsStream = new FileInputStream(System.getProperty("user.dir") + "\\project42-cb520-firebase-adminsdk-pn4s4-15cca3eb33.json");
        FirebaseOptions options = (new FirebaseOptions.Builder()).setCredentials(GoogleCredentials.fromStream(firebaseCredsStream)).build();
        com.google.firebase.FirebaseApp.initializeApp(options);
    }
}
