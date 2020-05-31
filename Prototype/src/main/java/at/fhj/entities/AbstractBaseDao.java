package at.fhj.entities;

import at.fhj.persistence.CriteriaApi;
import at.fhj.persistence.PersistenceApi;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.function.BiFunction;

import static java.util.Optional.ofNullable;

abstract class AbstractBaseDao<T extends BaseEntity> implements BaseDao<T> {
    @Inject
    private PersistenceApi persistenceApi;
    @Inject
    protected CriteriaApi criteriaApi;

    @Override
    public int delete(BiFunction<CriteriaBuilder, Root<T>, Predicate> restrictions) {
        var cb = criteriaApi.getCriteriaBuilder();
        var query = cb.createCriteriaDelete(getBaseClass());
        var qEntity = query.from(getBaseClass());
        query.where(restrictions.apply(cb, qEntity));
        return criteriaApi.delete(query);
    }

    @Override
    public T getById(long id) {
        return ofNullable(persistenceApi.getEntityManager().find(getBaseClass(), id))
                .orElseThrow(() -> new NoResultException("No "+getBaseClass().getSimpleName()+" found with id "+id));
    }

    protected abstract Class<T> getBaseClass();

}
