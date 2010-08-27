package org.lazydog.entry.internal.service;

import java.util.Date;
import org.lazydog.entry.EntryService;
import org.lazydog.entry.model.ApplicationUser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Unit tests for EntryServiceImpl class.
 *
 * @author  Ron Rickard
 */
public class EntryServiceImplTest {

    private static EntryService service;

    @BeforeClass
    public static void initialize() throws Exception {

        // Get the entry service.
        service = new EntryServiceWrapper();
    }

    @Before
    public void beforeTest() throws Exception {
        service = null;
        System.gc();
        Thread.sleep(1000);
        service = new EntryServiceWrapper();
    }

    private static double duration(Date startTime, Date endTime) {
        return (endTime.getTime() - startTime.getTime()) / 1000d;
    }

    @Test
    public void activate() throws Exception {

    }

    @Test
    public void deactivate() throws Exception {

    }

    @Test
    public void findTest() throws Exception {

    }

    @Test
    public void register() throws Exception {

    }
}
