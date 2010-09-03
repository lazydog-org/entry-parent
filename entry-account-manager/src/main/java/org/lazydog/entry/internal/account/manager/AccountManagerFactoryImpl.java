package org.lazydog.entry.internal.account.manager;

import javax.naming.directory.DirContext;
import org.lazydog.entry.spi.account.manager.AccountManager;
import org.lazydog.entry.spi.account.manager.AccountManagerFactory;


/**
 * Account manager factory implementation.
 *
 * @author  Ron Rickard
 */
public class AccountManagerFactoryImpl extends AccountManagerFactory {

    /**
     * Create the account manager.
     *
     * @param  context  the context.
     *
     * @return  the account manager.
     */
    @Override
    public AccountManager createAccountManager(Object context) {
        return new AccountManagerImpl((DirContext)context);
    }

}
