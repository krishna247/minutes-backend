package app.tasks.controller.http;

import app.tasks.aws.S3Upload;
import app.tasks.repository.SessionRepository;
import app.tasks.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class S3Handler {
    @Autowired
    SessionRepository sessionRepository;
    @Autowired
    S3Upload s3Upload;
    @Autowired
    AuthService authService;
    @Autowired Environment env;

    @Operation(security = {@SecurityRequirement(name = "Authorization")})
    @GetMapping("/upload")
    public Map<String, String> getPreSignedURL(@RequestParam String key, @RequestParam String contentType,
                                               @RequestHeader("Authorization") String sessionToken) {
        authService.isAuthenticated(sessionToken);
        return Map.of("url", s3Upload.getPresignedUrl(key, contentType));
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
