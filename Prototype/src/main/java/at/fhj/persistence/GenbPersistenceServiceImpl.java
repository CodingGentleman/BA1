package at.fhj.persistence;

import at.fhj.genb.service.GenbPersistenceService;

import javax.enterprise.inject.spi.CDI;

public class GenbPersistenceServiceImpl implements GenbPersistenceService {
    @Override
    public void persist(Object entity) {
        CDI.current().select(PersistenceApi.class).get().getEntityManager().persist(entity);
    }
}
