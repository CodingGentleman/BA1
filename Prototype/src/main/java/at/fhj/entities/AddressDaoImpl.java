package at.fhj.entities;

public class AddressDaoImpl extends AbstractBaseDao<Address> implements AddressDao {
    @Override
    protected Class<Address> getBaseClass() {
        return Address.class;
    }
}
