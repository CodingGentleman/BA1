package at.fhj.entities;

public class OrderDaoImpl extends AbstractBaseDao<Order> implements OrderDao {
    @Override
    protected Class<Order> getBaseClass() {
        return Order.class;
    }
}
