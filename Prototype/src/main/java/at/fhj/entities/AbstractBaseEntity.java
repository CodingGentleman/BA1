package at.fhj.entities;

abstract class AbstractBaseEntity implements BaseEntity {
    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"="+getId();
    }
}
