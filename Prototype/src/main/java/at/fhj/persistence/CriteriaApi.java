package at.fhj.persistence;

import at.fhj.persistence.adapter.TypedQueryAdapter;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;

public interface CriteriaApi {
	CriteriaBuilder getCriteriaBuilder();

	<T> TypedQueryAdapter<T> query(CriteriaQuery<T> criteriaQuery);

	<T> int delete(CriteriaDelete<T> criteriaDelete);

	<T> int update(CriteriaUpdate<T> criteriaUpdate);
}
