package at.fhj.persistence;

import javax.persistence.EntityManager;

public interface PersistenceApi {
    EntityManager getEntityManager();
}
