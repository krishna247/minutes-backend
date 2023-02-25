package app.tasks.controller.http;

import app.tasks.aws.S3Upload;
import app.tasks.repository.SessionRepository;
import app.tasks.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
public class S3Handler {
    final
    SessionRepository sessionRepository;
    final
    S3Upload s3Upload;
    final
    AuthService authService;
    final Environment env;

    public S3Handler(SessionRepository sessionRepository, S3Upload s3Upload, AuthService authService, Environment env) {
        this.sessionRepository = sessionRepository;
        this.s3Upload = s3Upload;
        this.authService = authService;
        this.env = env;
    }

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping("/upload")
    public Map<String, String> getPreSignedURL(@RequestParam String key, @RequestParam String contentType,
                                               @RequestHeader("Authorization") String sessionToken) {
        String userId = authService.isAuthenticated(sessionToken);
        if(key.startsWith(userId+"/")) {
            return Map.of("url", s3Upload.getPresignedUrl(key, contentType));
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"The S3 key provided must start with <userID>/");
    }

//    @Operation(security = {@SecurityRequirement(name = "Authorization")})
//    @GetMapping("/listS3")
//    public void listS3files(@RequestParam String prefix){
//        S3Client client = S3Client.builder().region(Region.EU_WEST_1).build();
//        ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(env.getProperty("aws.s3.resource_bucket")).prefix(prefix).build();
//        ListObjectsV2Iterable response = client.listObjectsV2Paginator(request);
//
//        for (ListObjectsV2Response page : response) {
//            page.contents().forEach((S3Object object) -> {
//                System.out.println(object.key());
//            });
//        }
//    }
}
