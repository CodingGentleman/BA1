package at.fhj.persistence;

import static java.util.Optional.ofNullable;
import javax.persistence.TypedQuery;

public class CriteriaHintImpl implements CriteriaHint {
	
	private Integer maxResults;
	private Integer firstResult;
	private Boolean readOnly;
	private Boolean cacheQuery;
	private Long fetchSize;

	@Override
	public <T> void apply(TypedQuery<T> query)
	{
		ofNullable(maxResults).ifPresent(query::setMaxResults);
		ofNullable(firstResult).ifPresent(query::setFirstResult);
		ofNullable(readOnly).ifPresent(v -> query.setHint("org.hibernate.readOnly", v));
		ofNullable(cacheQuery).ifPresent(v -> query.setHint("org.hibernate.cacheable", v));
		ofNullable(fetchSize).ifPresent(v -> query.setHint("org.hibernate.fetchSize", v));
	}

	@Override
	public CriteriaHint withMaxResults(int value)
	{
		maxResults = value;
		return this;
	}

	@Override
	public CriteriaHint withFirstResult(int value)
	{
		firstResult = value;
		return this;
	}

	@Override
	public CriteriaHint withReadOnly()
	{
		readOnly = true;
		return this;
	}

	@Override
	public CriteriaHint withCacheQuery()
	{
		cacheQuery = true;
		return this;
	}

	@Override
	public CriteriaHint withFetchSize(long value)
	{
		fetchSize = value;
		return this;
	}
}
