package org.lazydog.entry.internal.account.manager;

import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.interceptor.Interceptors;
import org.lazydog.ejbmonitor.interceptor.EJBMonitor;
import org.lazydog.entry.spi.account.manager.EntryAccountManager;


/**
 * Entry account manager Enterprise Java Bean.
 *
 * @author  Ron Rickard
 */
@Singleton(name="ejb/EntryAccountManager")
@Local(EntryAccountManager.class)
@Interceptors(EJBMonitor.class)
public class EntryAccountManagerEJB extends EntryAccountManagerImpl implements EntryAccountManager {

    @Resource(name="EntryAccountManagerEnvironment")
    private Properties environment;

    @PostConstruct
    protected void initialize() {
        this.setEnvironment(this.environment);
    }
}
