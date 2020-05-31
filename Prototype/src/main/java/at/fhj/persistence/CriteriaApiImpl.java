package at.fhj.persistence;

import at.fhj.persistence.adapter.TypedQueryAdapter;
import at.fhj.persistence.adapter.TypedQueryAdapterImpl;
import org.hibernate.query.QueryParameter;
import org.jboss.logging.Logger;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Stateless
public class CriteriaApiImpl implements CriteriaApi {
    private static final Logger LOG = Logger.getLogger(CriteriaApiImpl.class);

    @Inject
    private PersistenceApi persistenceApi;

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return persistenceApi.getEntityManager().getCriteriaBuilder();
    }

    @Override
    public <T> TypedQueryAdapter<T> query(CriteriaQuery<T> criteriaQuery) {
        var query = persistenceApi.getEntityManager().createQuery(criteriaQuery);
        log(query);
        return new TypedQueryAdapterImpl<>(query);
    }

    @Override
    public <T> int delete(CriteriaDelete<T> criteriaDelete) {
        var query = persistenceApi.getEntityManager().createQuery(criteriaDelete);
        log(query);
        return query.executeUpdate();
    }

    @Override
    public <T> int update(CriteriaUpdate<T> criteriaUpdate) {
        var query = persistenceApi.getEntityManager().createQuery(criteriaUpdate);
        log(query);
        return query.executeUpdate();
    }

    private void log(Query unwrappedQuery) {
        if (LOG.isDebugEnabled()) {
            var query = unwrappedQuery.unwrap(org.hibernate.query.Query.class);
            var params = query.getParameterMetadata().getNamedParameters().stream()
                    .filter(query::isBound)
                    .map(QueryParameter::getName)
                    .map(paramName -> paramName + ": " + query.getParameterValue(paramName))
                    .collect(Collectors.joining(", "));
            LOG.debug(String.format("query: '%s' params: [%s]", query.getQueryString(), params));
        }
    }
}
