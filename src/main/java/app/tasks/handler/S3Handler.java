//package app.tasks.handler;
//
//import app.tasks.aws.S3Upload;
//import app.tasks.repository.SessionRepository;
//import app.tasks.utils.AuthUtils;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestHeader;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
//@RestController
//public class S3Handler {
//    @Autowired
//    SessionRepository sessionRepository;
//    @Autowired
//    S3Upload s3Upload;
//    @Autowired
//    AuthUtils authUtils;
//
//    @Operation(security = {@SecurityRequirement(name = "Authorization")})
//    @GetMapping("/upload")
//    public Map<String, String> getPreSignedURL(@RequestParam String key, @RequestParam String contentType,
//                                               @RequestHeader("Authorization") String sessionToken) {
//        authUtils.isAuthenticated(sessionToken, sessionRepository);
//        return Map.of("url", s3Upload.getPresignedUrl(key, contentType));
//    }
//}
