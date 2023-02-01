package app.tasks.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class QueryService {

    @PersistenceContext
    public EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> exceuteQueryResponse(String queryString, Map<String, Object> params) {
//        Session session = entityManager.unwrap(Session.class);
//        List result = session.createNativeQuery(queryString).list();
        Query query = entityManager.createNativeQuery(queryString);
        NativeQueryImpl nativeQuery = (NativeQueryImpl) query;
        params.forEach(query::setParameter);
        nativeQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
        return (List<Map<String, Object>>) nativeQuery.getResultList();
    }

}
