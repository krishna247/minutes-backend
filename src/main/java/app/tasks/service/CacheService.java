package app.tasks.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

@Component
public class CacheService {

    @CacheEvict("maxUpdateTs")
    public void evictCacheMaxUpdateTs(String userId){
        System.out.println("Evict cache: "+userId);
    }
}
