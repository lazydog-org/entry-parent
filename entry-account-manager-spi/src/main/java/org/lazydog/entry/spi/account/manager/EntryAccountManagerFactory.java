package org.lazydog.entry.spi.account.manager;

import java.util.Properties;
import java.util.ServiceLoader;


/**
 * Entry account manager factory.
 *
 * @author  Ron Rickard
 */
public abstract class EntryAccountManagerFactory {

    /**
     * Protected constructor.
     */
    protected EntryAccountManagerFactory() {
        // Do nothing.
    }

    /**
     * Create the entry account manager.
     *
     * @param  environment  the environment.
     * 
     * @return  the entry account manager.
     */
    public abstract EntryAccountManager createEntryAccountManager(Properties environment);

    /**
     * Get an instance of the entry account manager factory.
     *
     * @return  the entry account manager factory.
     *
     * @throws  IllegalArgumentException   if not exactly one factory is found.
     * @throws  ServiceConfigurationError  if unable to create the factory due
     *                                     to a provider configuration error.
     */
    public static synchronized EntryAccountManagerFactory instance() {

        // Declare.
        EntryAccountManagerFactory factory;
        ServiceLoader<EntryAccountManagerFactory> factoryLoader;

        // Initialize.
        factory = null;
        factoryLoader = ServiceLoader.load(EntryAccountManagerFactory.class);

        // Loop through the services.
        for (EntryAccountManagerFactory loadedFactory : factoryLoader) {

            // Check if a factory has not been found.
            if (factory == null) {

                // Set the factory.
                factory = loadedFactory;
            }
            else {
                throw new IllegalArgumentException(
                    "More than one Entry account manager factory found.");
            }
        }

        // Check if a factory has not been found.
        if (factory == null) {
            throw new IllegalArgumentException(
                "No Entry account manager factory found.");
        }

        return factory;
    }
}
