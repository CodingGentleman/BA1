package at.fhj.entities;

import at.fhj.service.OrderLineRestrictionDto;

import java.util.Date;
import java.util.List;

public interface UseCaseDao {
    List<Address> getByFirstName(String name);
    List<Long> coalesceSingleTable(OrderType orderType, Address invoiceAddress);
    long coalesceSingleTableById(OrderType orderType, long invoiceAddressId);
    long countWithCast(int wholeNumberValue);
    List<OrderLine> getRestrictedOrderLines(Order order, OrderLineRestrictionDto orderLineRestrictionDto);
    List<Order> getOrdersOfDate(Date date);
    List<Address> getAddressesWithAboveAverageOrderVouchers();
}
