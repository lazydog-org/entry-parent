package org.lazydog.entry.internal.repository;

import java.util.Date;
import java.util.List;
import org.lazydog.entry.EntryRepository;
import org.lazydog.entry.model.ApplicationUser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Unit tests for EntryRepositoryImpl class.
 *
 * @author  Ron Rickard
 */
public class EntryRepositoryImplTest {

    private static EntryRepository repository;

    @BeforeClass
    public static void initialize() throws Exception {

        // Get the entry repository.
        repository = new EntryRepositoryWrapper();
    }

    @Before
    public void beforeTest() throws Exception {
        repository = null;
        System.gc();
        Thread.sleep(1000);
        repository = new EntryRepositoryWrapper();
    }

    private static double duration(Date startTime, Date endTime) {
        return (endTime.getTime() - startTime.getTime()) / 1000d;
    }

    private static <T> void findList(Class<T> entityClass) {

        List<T> entities;
        Date endTime;
        Date startTime;

        startTime = new Date();
        entities = repository.findList(entityClass);
        endTime = new Date();
        System.out.println(entities.size() + " " + entityClass.getSimpleName()
                + "s retrieved in " + duration(startTime, endTime)
                + " seconds");
    }

    @Test
    public void findListApplicationUser() throws Exception {
        findList(ApplicationUser.class);
    }
}
