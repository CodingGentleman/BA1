package at.fhj.persistence.adapter;

import at.fhj.persistence.CriteriaHint;

import javax.persistence.criteria.ParameterExpression;
import java.util.List;
import java.util.stream.Stream;

public interface TypedQueryAdapter<T> {
    TypedQueryAdapter<T> withHint(CriteriaHint hint);
    <U> TypedQueryAdapter<T> addParameter(ParameterExpression<U> parameterExpression, U parameterValue);
    List<T> getResultList();
    Stream<T> getResultStream();
    T getSingleResult();
}
