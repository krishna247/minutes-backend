//package app.tasks.oauth;
//
//import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
//import com.google.api.client.auth.oauth2.StoredCredential;
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.client.util.store.DataStore;
//import com.google.api.client.util.store.FileDataStoreFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//
//@Configuration
//public class GoogleCredDataStore {
//    private static final String DATA_STORE_DIR = ".store/google_client_sample";
//    @Value("${auth.client_id}")
//    private String clientId;
//
//    @Value("${auth.client_secret}")
//    private String clientSecret;
//    List<String> scopes = List.of("https://www.googleapis.com/auth/calendar.readonly");
//
//    @Bean
//    public DataStore<StoredCredential> credentialDataStore() {
//        DataStore<StoredCredential> credentialDataStore = null;
//        try {
//            File dataStoreDir = new File(System.getProperty("user.home"), DATA_STORE_DIR);
//            credentialDataStore = StoredCredential.getDefaultDataStore(new FileDataStoreFactory(dataStoreDir));
//        } catch (IOException e) {
//            System.out.println("Error in datastore");
//        }
//
//        return credentialDataStore;
//    }
//
//    @Bean
//    public AuthorizationCodeFlow initializeFlow() throws IOException {
//        return new GoogleAuthorizationCodeFlow(
//                new NetHttpTransport(),
//                new JacksonFactory(),
//                clientId,
//                clientSecret,
//                scopes
//        );
//    }
//}