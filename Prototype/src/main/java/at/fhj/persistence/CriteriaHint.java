package at.fhj.persistence;

import javax.persistence.TypedQuery;

/**
 * Property container to alter behaviour of Hibernate or JDBC driver.
 * Used in combination with {@link CriteriaApi}
 */
public interface CriteriaHint {
	/**
	 * Factory method to create an instance of {@link CriteriaHint}
	 * @return an instance of {@link CriteriaHint}
	 */
	static CriteriaHint createInstance() {
		return new CriteriaHintImpl();
	}
	<T> void apply(TypedQuery<T> query);

	/**
	 * This is equivalent to LIMIT in SQL. It sets the maximum number of rows you want returned.
	 *
	 * @param value amount of results
	 * @return a reference to this object
	 */
	CriteriaHint withMaxResults(int value);

	/**
	 * Set the position of the first result to retrieve. Can be combined with
	 * {@link #withMaxResults(int)} for pagination
	 *
	 * @param value position number of the first row
	 * @return a reference to this object
	 */
	CriteriaHint withFirstResult(int value);

	/**
	 * If you will not apply any changes to the selected entities, you can set the org.hibernate.readOnly hint to true.
	 * This allows Hibernate to deactivate dirty checking for these entities and can provide a performance benefit.
	 *
	 * @return a reference to this object
	 */
	CriteriaHint withReadOnly();

	/**
	 * Enable Hibernate query cache
	 *
	 * @return a reference to this object
	 */
	CriteriaHint withCacheQuery();

	/**
	 * Hibernate provides the value of this hint to the JDBC driver to define the number of rows the driver shall
	 * receive in one batch. This can improve the communication between the JDBC driver and the database.
	 *
	 * @param value amount of rows for each batch
	 * @return a reference to this object
	 */
	CriteriaHint withFetchSize(long value);
}
