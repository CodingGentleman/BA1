package at.fhj.genb.service;

import java.util.NoSuchElementException;
import java.util.ServiceLoader;

public class GenbPersistenceProvider {
    private static GenbPersistenceProvider provider;
    private final ServiceLoader<GenbPersistenceService> loader;
    private GenbPersistenceProvider() {
        loader = ServiceLoader.load(GenbPersistenceService.class);
    }
    public static GenbPersistenceProvider getInstance() {
        if(provider == null) {
            provider = new GenbPersistenceProvider();
        }
        return provider;
    }
    public GenbPersistenceService loadService() {
        GenbPersistenceService service = loader.iterator().next();
        if(service != null) {
            return service;
        } else {
            throw new NoSuchElementException("No implementation for GreetingsProvider");
        }
    }
}
