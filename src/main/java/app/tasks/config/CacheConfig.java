//package app.tasks.config;
//
//import com.google.common.cache.CacheBuilder;
//import org.springframework.cache.CacheManager;
//import org.springframework.cache.annotation.CachingConfigurerSupport;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@EnableCaching
//public class CacheConfig extends CachingConfigurerSupport {
//
//    @Override
//    @Bean
//    public CacheManager cacheManager() {
//        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
//        return cacheManager;
//    }
//
//    @Bean
//    public CacheManager timeoutCacheManager() {
//        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
//        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder()
//                .maximumSize(100)
//                .expireAfterWrite(5, TimeUnit.SECONDS);
//        cacheManager.setCacheBuilder(cacheBuilder);
//        return cacheManager;
//    }
//
//}
