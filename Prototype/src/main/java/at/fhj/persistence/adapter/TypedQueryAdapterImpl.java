package at.fhj.persistence.adapter;

import at.fhj.persistence.CriteriaHint;
import org.jboss.logging.Logger;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.ParameterExpression;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

public class TypedQueryAdapterImpl<T> implements TypedQueryAdapter<T> {
    private static final Logger LOG = Logger.getLogger(TypedQueryAdapterImpl.class);

    private final TypedQuery<T> typedQuery;

    public TypedQueryAdapterImpl(TypedQuery<T> typedQuery) {
        this.typedQuery = typedQuery;
    }

    @Override
    public TypedQueryAdapter<T> withHint(CriteriaHint hint) {
        ofNullable(hint).ifPresent(h -> h.apply(typedQuery));
        return this;
    }

    @Override
    public <U> TypedQueryAdapter<T> addParameter(ParameterExpression<U> parameterExpression, U parameterValue) {
        LOG.debug("param name:" + parameterExpression.getName() + ", position:" + parameterExpression.getPosition() + ", value:" + parameterValue);
        typedQuery.setParameter(parameterExpression, parameterValue);
        return this;
    }

    @Override
    public List<T> getResultList() {
        return typedQuery.getResultList();
    }

    @Override
    public Stream<T> getResultStream() {
        return typedQuery.getResultStream();
    }

    @Override
    public T getSingleResult() {
        return typedQuery.getSingleResult();
    }
}
