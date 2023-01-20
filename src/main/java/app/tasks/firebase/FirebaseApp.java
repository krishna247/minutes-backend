package app.tasks.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FirebaseApp {

    @PostConstruct
    public void setup() throws IOException {
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();

        System.out.println(System.getProperty(s));
        FileInputStream firebaseCredsStream = new FileInputStream("project42-cb520-firebase-adminsdk-pn4s4-15cca3eb33.json");
        FirebaseOptions options = (new FirebaseOptions.Builder()).setCredentials(GoogleCredentials.fromStream(firebaseCredsStream)).build();
        com.google.firebase.FirebaseApp.initializeApp(options);
    }
}
