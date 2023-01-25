package app.tasks.rough;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileInputStream;
import java.util.Date;

public class FirebaseTests {

    public static void main(String[] args) {
        try {

            FileInputStream firebaseCredsStream = new FileInputStream("<>>");
            FirebaseOptions options = (new FirebaseOptions.Builder()).setCredentials(GoogleCredentials.fromStream(firebaseCredsStream)).build();
            FirebaseApp.initializeApp(options);

            String cookie = "<>";
            long a = new Date().toInstant().toEpochMilli();
            System.out.println(FirebaseAuth.getInstance().verifySessionCookie(cookie, true).getEmail());
            long b = new Date().toInstant().toEpochMilli();
            System.out.println((b - a) / 1000.0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
