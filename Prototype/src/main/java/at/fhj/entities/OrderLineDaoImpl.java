package at.fhj.entities;

public class OrderLineDaoImpl extends AbstractBaseDao<OrderLine> implements OrderLineDao {
    @Override
    protected Class<OrderLine> getBaseClass() {
        return OrderLine.class;
    }
}
