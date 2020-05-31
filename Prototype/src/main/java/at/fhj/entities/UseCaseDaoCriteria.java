package at.fhj.entities;

import at.fhj.persistence.CriteriaApi;
import at.fhj.service.OrderLineRestrictionDto;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
@Named("criteria")
public class UseCaseDaoCriteria implements UseCaseDao {

	@Inject
	private CriteriaApi criteriaApi;

	@Override
	public List<Address> getByFirstName(String name) {
		var cb = criteriaApi.getCriteriaBuilder();
		var query = cb.createQuery(Address.class);
		var qAddress = query.from(Address.class);
		query.where(cb.equal(qAddress.get(Address_.firstName), name));
		return criteriaApi.query(query).getResultList();
	}

	@Override
	public List<Long> coalesceSingleTable(OrderType orderType, Address invoiceAddress) {
		var cb = criteriaApi.getCriteriaBuilder();
		var query = cb.createQuery(Long.class);
		var qOrder = query.from(Order.class);
		query.where(orderTypeEquals(qOrder, orderType),
				cb.equal(qOrder.get(Order_.invoiceAddress), invoiceAddress));
		query.select(currentAmount(qOrder));
		return criteriaApi.query(query).getResultList();
	}

	private Predicate orderTypeEquals(Root<Order> qOrder, OrderType orderType) {
		return criteriaApi.getCriteriaBuilder().equal(qOrder.get(Order_.TYPE), orderType);
	}

	private Expression<Long> currentNullableAmount(Root<Order> qOrder) {
		return criteriaApi.getCriteriaBuilder().coalesce(qOrder.get(Order_.invoiceAmount), qOrder.get(Order_.orderAmount));
	}

	private Expression<Long> currentAmount(Root<Order> qOrder) {
		CriteriaBuilder.Coalesce<Long> coalesce = criteriaApi.getCriteriaBuilder().coalesce();
		return coalesce.value(qOrder.get(Order_.invoiceAmount)).value(qOrder.get(Order_.orderAmount)).value(0L);
	}

	public void update() {
		var cb = criteriaApi.getCriteriaBuilder();
		var query = cb.createCriteriaUpdate(Order.class);
		var qOrder = query.from(Order.class);
		query.set(Order_.invoiceAmount, qOrder.get(Order_.orderAmount));
		criteriaApi.update(query);
	}

	@Override
	public long countWithCast(int wholeNumberValue) {
		var cb = criteriaApi.getCriteriaBuilder();
		var query = cb.createQuery(Long.class);
		var root = query.from(Voucher.class);
		query.where(cb.equal(root.get(Voucher_.value).as(Integer.TYPE), wholeNumberValue));
		query.select(cb.count(root.get(Voucher_.id)));
		return criteriaApi.query(query).getSingleResult();
	}

	@Override
	public long coalesceSingleTableById(OrderType orderType, long invoiceAddress) {
		var cb = criteriaApi.getCriteriaBuilder();
		var query = cb.createQuery(Long.class);
		var qOrder = query.from(Order.class);
		query.where(orderTypeEquals(qOrder, orderType),
				cb.equal(qOrder.get(Order_.invoiceAddress).get(Address_.id), invoiceAddress));
		query.select(currentAmount(qOrder));
		return criteriaApi.query(query).getSingleResult();
	}

	@Override
	public List<OrderLine> getRestrictedOrderLines(Order order, OrderLineRestrictionDto orderLineRestrictionDto) {
		var cb = criteriaApi.getCriteriaBuilder();
		var query = cb.createQuery(OrderLine.class);
		var qOrderLine = query.from(OrderLine.class);
		var clauses = new ArrayList<Predicate>();
		clauses.add(cb.equal(qOrderLine.get(OrderLine_.order), order));
		clauses.addAll(createOrderLineRestrictions(qOrderLine, orderLineRestrictionDto));
		query.where(clauses.toArray(new Predicate[0]));
		return criteriaApi.query(query).getResultList();
	}

	private List<Predicate> createOrderLineRestrictions(Root<OrderLine> qOrderLine, OrderLineRestrictionDto orderLineRestrictionDto) {
		var cb = criteriaApi.getCriteriaBuilder();
		var clauses = new ArrayList<Predicate>();
		if (!orderLineRestrictionDto.isSkipLotCheck()) {
			clauses.add(cb.equal(qOrderLine.get(OrderLine_.lotNumber), orderLineRestrictionDto.getLotNumber()));
		}
		if (!orderLineRestrictionDto.isSkipTollCheck()) {
			clauses.add(cb.equal(qOrderLine.get(OrderLine_.tollLocked), orderLineRestrictionDto.isTollLocked()));
		}
		return clauses;
	}

	@Override
	public List<Order> getOrdersOfDate(Date date) {
		var cb = criteriaApi.getCriteriaBuilder();
		var query = cb.createQuery(Order.class);
		var qOrder = query.from(Order.class);
		var dateParam = cb.parameter(Date.class);
		query.where(invoiceDateEquals(qOrder, dateParam));
		return criteriaApi.query(query)
				.addParameter(dateParam, date)
				.getResultList();
	}

	private Predicate invoiceDateEquals(Root<Order> qOrder, ParameterExpression<Date> dateParam) {
		var cb = criteriaApi.getCriteriaBuilder();
		return cb.equal(
				cb.function("TRUNC", Date.class, qOrder.get(Order_.invoiceTimestamp)),
				cb.function("TRUNC", Date.class, dateParam));
	}

	@Override
	public List<Address> getAddressesWithAboveAverageOrderVouchers() {
		var cb = criteriaApi.getCriteriaBuilder();
		var query = cb.createQuery(Address.class);
		var qOrder = query.from(Order.class);
		var qVoucher = qOrder.join(Order_.vouchers);

		var subQuery = query.subquery(Double.class);
		var subQVoucher = subQuery.from(Voucher.class);
		subQuery.select(cb.avg(subQVoucher.get(Voucher_.value)));

		query.select(qOrder.get(Order_.invoiceAddress))
				.distinct(true)
				.where(cb.gt(qVoucher.get(Voucher_.value), subQuery));
		return criteriaApi.query(query).getResultList();
	}
}
