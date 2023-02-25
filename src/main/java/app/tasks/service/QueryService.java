package app.tasks.service;

import com.google.common.base.CaseFormat;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@SuppressWarnings(value = "unchecked")
public class QueryService {

    @PersistenceContext
    public EntityManager entityManager;

    @SuppressWarnings({"unchecked", "rawtypes", "deprecation"})
    public List<Map<String, Object>> executeQueryResponse(String queryString, Map<String, Object> params) {
        Query query = entityManager.createNativeQuery(queryString);
        NativeQueryImpl nativeQuery = (NativeQueryImpl) query;
        params.forEach(query::setParameter);
        nativeQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return AliasToCamelCase((List<Map<String, Object>>) nativeQuery.getResultList());
    }

    public List<Map<String, Object>> AliasToCamelCase(List<Map<String, Object>> inputs){
        List<Map<String, Object>> result = new ArrayList<>();
        for(Map<String, Object> input: inputs){
            Set<String> oldKeys = input.keySet();
            Map<String,Object> subResult = new HashMap<>();
            for(String key: oldKeys){
                String newKey = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, key);
                subResult.put(newKey,input.get(key));
            }
            result.add(subResult);
        }
        return result;
    }

    public <T> void persist(Object obj){
        T obj2 = (T) obj;
        entityManager.persist(obj2);
    }

    public <T> void update(Object obj){
        T obj2 = (T) obj;
        entityManager.merge(obj2);
    }

//    public <T> void persistAll(List<Object> objs){
//        for(Object obj:objs) {
//            T obj2 = (T) obj;
//            entityManager.persist(obj2);
//        }
//    }

}
