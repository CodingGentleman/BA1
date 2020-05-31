package at.fhj.entities;

public class VoucherDaoImpl extends AbstractBaseDao<Voucher> implements VoucherDao {
    @Override
    protected Class<Voucher> getBaseClass() {
        return Voucher.class;
    }
}
