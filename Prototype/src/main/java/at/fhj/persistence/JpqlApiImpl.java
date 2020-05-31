package at.fhj.persistence;

import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class JpqlApiImpl implements JpqlApi {
    private static final Logger LOG = Logger.getLogger(JpqlApiImpl.class);

    @Inject
    private PersistenceApi persistenceApi;

    @Override
    public <T> List<T> getResultList(Class<T> clazz, String jpql, Object... params) {
        return createQuery(clazz, jpql, params).getResultList();
    }

    @Override
    public <T> T getSingleResult(Class<T> clazz, String jpql, Object... params) {
        return createQuery(clazz, jpql, params).getSingleResult();
    }

    public <T> TypedQuery<T> createQuery(Class<T> clazz, String jpql, Object[] params) {
        var query = persistenceApi.getEntityManager().createQuery(jpql, clazz);
        LOG.info("jpql query:'"+jpql+"', params:"+Arrays.stream(params).map(Object::toString).collect(Collectors.joining(",")));
        bindParams(query, params);
        return query;
    }

    private <T> void bindParams(TypedQuery<T> query, Object[] params) {
        if(params == null) {
            return;
        }
        for(int i = 1; i <= params.length; i++) {
            query.setParameter(i, params[i-1]);
        }
    }

}
