package at.fhj.entities;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.function.BiFunction;

interface BaseDao<T extends BaseEntity> {
	int delete(BiFunction<CriteriaBuilder, Root<T>, Predicate> pred);
	T getById(long id);
}
