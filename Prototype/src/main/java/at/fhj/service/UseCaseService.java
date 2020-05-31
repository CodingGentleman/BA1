package at.fhj.service;

import at.fhj.entities.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Path("/api")
@Stateless
public class UseCaseService {

    @Inject
    @Named("jpql")
    private UseCaseDao jpqlDao;

    @Inject
    @Named("criteria")
    private UseCaseDao criteriaDao;

    @Inject
    private AddressDao addressDao;
    @Inject
    private OrderDao orderDao;
    @Inject
    private OrderLineDao orderLineDao;
    @Inject
    private VoucherDao voucherDao;

    @GET
    @Path("jpql")
    @Produces({"application/json"})
    public long jpql(@QueryParam("type") OrderType orderType, @QueryParam("address") long addressId) {
        return jpqlDao.coalesceSingleTableById(orderType, addressId);
    }

    @GET
    @Path("criteria")
    @Produces({"application/json"})
    public long criteria(@QueryParam("type") OrderType orderType, @QueryParam("address") long addressId) {
        return criteriaDao.coalesceSingleTableById(orderType, addressId);
    }

    @GET
    @Path("inject")
    @Produces({"application/json"})
    public UseCaseResultDto inject(@QueryParam("name") String name) {
        var result = new UseCaseResultDto();
        result.setJpql(jpqlDao.getByFirstName(name).size());
        result.setCriteria(criteriaDao.getByFirstName(name).size());
        return result;
    }

    @GET
    @Path("coalesceSingleTable")
    @Produces({"application/json"})
    public UseCaseResultDto coalesceSingleTable(@QueryParam("type") OrderType orderType, @QueryParam("address") long addressId) {
        var address = addressDao.getById(addressId);
        return time(
                () -> jpqlDao.coalesceSingleTable(orderType, address),
                () -> criteriaDao.coalesceSingleTable(orderType, address));
    }

    @GET
    @Path("coalesceSingleTableById")
    @Produces({"application/json"})
    public UseCaseResultDto coalesceSingleTableById(@QueryParam("type") OrderType orderType, @QueryParam("address") long addressId) {
        return time(
                () -> jpqlDao.coalesceSingleTableById(orderType, addressId),
                () -> criteriaDao.coalesceSingleTableById(orderType, addressId));
    }

    @GET
    @Path("getRestrictedOrderLines")
    @Produces({"application/json"})
    public UseCaseResultDto getRestrictedOrderLines(@QueryParam("order") long orderId,
                                                    @QueryParam("lotNumber") String lotNumber,
                                                    @QueryParam("tollLocked") boolean tollLocked,
                                                    @QueryParam("skipLotCheck") boolean skipLotCheck,
                                                    @QueryParam("skipTollCheck") boolean skipTollCheck
                                                    ) {
        var order = orderDao.getById(orderId);
        var orderLineRestrictionDto = new OrderLineRestrictionDto();
        orderLineRestrictionDto.setLotNumber(lotNumber);
        orderLineRestrictionDto.setTollLocked(tollLocked);
        orderLineRestrictionDto.setSkipLotCheck(skipLotCheck);
        orderLineRestrictionDto.setSkipTollCheck(skipTollCheck);
        return time(
                () -> jpqlDao.getRestrictedOrderLines(order, orderLineRestrictionDto),
                () -> criteriaDao.getRestrictedOrderLines(order, orderLineRestrictionDto));
    }

    @GET
    @Path("getOrdersOfToday")
    @Produces({"application/json"})
    public UseCaseResultDto getOrdersOfToday() {
        var today = new Date();
        return time(
                () -> jpqlDao.getOrdersOfDate(today),
                () -> criteriaDao.getOrdersOfDate(today));
    }

    @GET
    @Path("getAddressesAboveAvgVoucher")
    @Produces({"application/json"})
    public UseCaseResultDto getAddressesAboveAvgVoucher() {
        return time(
                () -> jpqlDao.getAddressesWithAboveAverageOrderVouchers(),
                () -> criteriaDao.getAddressesWithAboveAverageOrderVouchers());
    }

    @GET
    @Path("benchmark")
    @Produces({"application/json"})
    public List<UseCaseResultDto> benchmark(
            @QueryParam("jpqlFirst") boolean jpqlFirst,
            @QueryParam("address") long addressId,
            @QueryParam("order") long orderId,
            @QueryParam("lotNumber") String lotNumber,
            @QueryParam("tollLocked") boolean tollLocked,
            @QueryParam("skipLotCheck") boolean skipLotCheck,
            @QueryParam("skipTollCheck") boolean skipTollCheck) {
        var address = addressDao.getById(addressId);
        var order = orderDao.getById(orderId);
        var orderLineRestrictionDto = new OrderLineRestrictionDto();
        orderLineRestrictionDto.setLotNumber(lotNumber);
        orderLineRestrictionDto.setTollLocked(tollLocked);
        orderLineRestrictionDto.setSkipLotCheck(skipLotCheck);
        orderLineRestrictionDto.setSkipTollCheck(skipTollCheck);
        if(jpqlFirst) {
            return timeIt(address, order, orderLineRestrictionDto, jpqlDao, criteriaDao);
        }
        return timeIt(address, order, orderLineRestrictionDto, criteriaDao, jpqlDao);
    }

    public List<UseCaseResultDto> timeIt(Address address, Order order, OrderLineRestrictionDto orderLineRestrictionDto, UseCaseDao useCaseDao1, UseCaseDao useCaseDao2) {
        var result = new ArrayList<UseCaseResultDto>();
        var today = new Date();
        result.add(time("coalesceSingleTable",
                () -> useCaseDao1.coalesceSingleTable(OrderType.B2C, address),
                () -> useCaseDao2.coalesceSingleTable(OrderType.B2C, address)));
        result.add(time("getRestrictedOrderLines",
                () -> useCaseDao1.getRestrictedOrderLines(order, orderLineRestrictionDto),
                () -> useCaseDao2.getRestrictedOrderLines(order, orderLineRestrictionDto)));
        result.add(time("getAddressesWithAboveAverageOrderVouchers",
                useCaseDao1::getAddressesWithAboveAverageOrderVouchers,
                useCaseDao2::getAddressesWithAboveAverageOrderVouchers));
        result.add(time("getOrdersOfDate",
                () -> useCaseDao1.getOrdersOfDate(today),
                () -> useCaseDao2.getOrdersOfDate(today)));
        return result;
    }

    @GET
    @Path("setup")
    @Produces({"application/text"})
    public String setup(@QueryParam("massSize") Integer massSize) {
        DataSetup.setup(Optional.ofNullable(massSize).orElse(0));
        return "DONE";
    }

    @DELETE
    @Path("reset")
    public void reset() {
        orderLineDao.delete((cb, q) -> cb.equal(cb.literal(1), 1));
        orderDao.delete((cb, q) -> cb.equal(cb.literal(1), 1));
        addressDao.delete((cb, q) -> cb.equal(cb.literal(1), 1));
        voucherDao.delete((cb, q) -> cb.equal(cb.literal(1), 1));
    }

    private UseCaseResultDto time(Runnable jpqlRunnable, Runnable criteriaRunnable) {
        return time(null, jpqlRunnable, criteriaRunnable);
    }

    private UseCaseResultDto time(String name, Runnable jpqlRunnable, Runnable criteriaRunnable) {
        var dto = new UseCaseResultDto();
        dto.setName(name);
        dto.setJpql(time(jpqlRunnable));
        dto.setCriteria(time(criteriaRunnable));
        return dto;
    }

    private long time(Runnable runnable) {
        var startTime = System.currentTimeMillis();
        runnable.run();
        var endTime = System.currentTimeMillis();
        return endTime-startTime;
    }
}
