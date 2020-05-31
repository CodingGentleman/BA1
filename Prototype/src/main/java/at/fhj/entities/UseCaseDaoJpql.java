package at.fhj.entities;

import at.fhj.persistence.CriteriaApi;
import at.fhj.persistence.JpqlApi;
import at.fhj.service.OrderLineRestrictionDto;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
@Named("jpql")
public class UseCaseDaoJpql implements UseCaseDao {

    @Inject
    private JpqlApi jpqlApi;

    @Override
    public List<Address> getByFirstName(String name) {
        var hql = String.format(
                "select a from %s a where a.firstName = '"+name+"'", Address.class.getSimpleName());
        return jpqlApi.getResultList(Address.class, hql);
    }

    @Override
    public List<Long> coalesceSingleTable(OrderType orderType, Address invoiceAddress) {
        var hql = String.format(
                "select coalesce(orders.invoiceAmount, orders.orderAmount, 0) " +
                "from %s orders " +
                "where orders.type=?1 " +
                "and orders.invoiceAddress=?2", Order.class.getSimpleName());
        return jpqlApi.getResultList(Long.class, hql, orderType, invoiceAddress);
    }

    @Override
    public long coalesceSingleTableById(OrderType orderType, long invoiceAddress) {
        var hql = String.format("select coalesce(orders.invoiceAmount, orders.orderAmount) " +
                "from %s orders where orders.type=?1 and orders.invoiceAddress.id=?2", Order.class.getSimpleName());
        return jpqlApi.getSingleResult(Long.class, hql, orderType, invoiceAddress);
    }

    @Override
    public long countWithCast(int wholeNumberValue) {
        var jpql = "select count(v.id) from "+Voucher.class.getSimpleName()+" v where cast(v.value as integer) = ?1";
        return jpqlApi.getSingleResult(Long.class, jpql, wholeNumberValue);
    }

    @Override
    public List<OrderLine> getRestrictedOrderLines(Order order, OrderLineRestrictionDto orderLineRestrictionDto) {
        var params = new ArrayList<Serializable>();
        params.add(order);

        var hql = "select ol from "+OrderLine.class.getSimpleName()+" ol where ol.order = ?1";
        var paramIndex = 1;
        if (!orderLineRestrictionDto.isSkipLotCheck()) {
            hql = String.format("%s AND ol.lotNumber = ?%d ", hql, ++paramIndex);
            params.add(orderLineRestrictionDto.getLotNumber());
        }
        if (!orderLineRestrictionDto.isSkipTollCheck()) {
            hql = String.format("%s AND ol.tollLocked = ?%d ", hql, ++paramIndex);
            params.add(orderLineRestrictionDto.isTollLocked());
        }
        return jpqlApi.getResultList(OrderLine.class, hql, params.toArray());
    }

    @Override
    public List<Order> getOrdersOfDate(Date date) {
        return jpqlApi.getResultList(Order.class, "select o from "+Order.class.getSimpleName()+" o where trunc(invoiceTimestamp) = trunc(?1)", date);
    }

    @Override
    public List<Address> getAddressesWithAboveAverageOrderVouchers() {
        return jpqlApi.getResultList(Address.class,
                "select distinct(orders.invoiceAddress) " +
                        "from "+Order.class.getSimpleName()+" orders " +
                        "join orders.vouchers voucher " +
                        "where voucher.value > ( select avg(v.value) from Voucher v ) ");
    }
}
