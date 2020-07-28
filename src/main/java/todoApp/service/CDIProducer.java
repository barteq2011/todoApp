package todoApp.service;

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

// Class that will be used for injecting EntityManager to other
public class CDIProducer {

    @Produces
    @PersistenceContext
    EntityManager entityManager;
}
