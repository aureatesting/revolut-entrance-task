package com.revolut.entrancetask.alexeyz.persistence;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Helps build entity manager for persistence
 *
 * @author alexey.zakharchenko@gmail.com
 */
public class PersistenceUtil {
    private static EntityManagerFactory factory;

    public static EntityManagerFactory getEntityManagerFactory() {
        if (factory == null) {
            factory = Persistence.createEntityManagerFactory("default");
        }

        return factory;
    }
}
