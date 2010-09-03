package org.lazydog.entry.spi.account.manager;

import java.util.ServiceLoader;


/**
 * Account manager factory.
 *
 * @author  Ron Rickard
 */
public abstract class AccountManagerFactory {

    /**
     * Protected constructor.
     */
    protected AccountManagerFactory() {
        // Do nothing.
    }

    /**
     * Create the account manager.
     *
     * @param  context  the context.
     * 
     * @return  the account manager.
     */
    public abstract AccountManager createAccountManager(Object context);

    /**
     * Get an instance of the account manager factory.
     *
     * @return  the account manager factory.
     *
     * @throws  IllegalArgumentException   if not exactly one factory is found.
     * @throws  ServiceConfigurationError  if unable to create the factory due
     *                                     to a provider configuration error.
     */
    public static synchronized AccountManagerFactory instance() {

        // Declare.
        AccountManagerFactory factory;
        ServiceLoader<AccountManagerFactory> factoryLoader;

        // Initialize.
        factory = null;
        factoryLoader = ServiceLoader.load(AccountManagerFactory.class);

        // Loop through the services.
        for (AccountManagerFactory loadedFactory : factoryLoader) {

            // Check if a factory has not been found.
            if (factory == null) {

                // Set the factory.
                factory = loadedFactory;
            }
            else {
                throw new IllegalArgumentException(
                    "More than one AccountManagerFactory found.");
            }
        }

        // Check if a factory has not been found.
        if (factory == null) {
            throw new IllegalArgumentException(
                "No AccountManagerFactory found.");
        }

        return factory;
    }
}
