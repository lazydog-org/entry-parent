package org.lazydog.entry.internal.service;

import java.util.Properties;
import org.lazydog.entry.internal.account.manager.EntryAccountManagerImpl;
import org.lazydog.entry.spi.account.manager.EntryAccountManager;


/**
 * Entry account manager wrapper.
 *
 * @author  Ron Rickard
 */
public class EntryAccountManagerWrapper extends EntryAccountManagerImpl {

    /**
     * Constructor.
     */
    public EntryAccountManagerWrapper () {

        // Declare.
        Properties environment;

        // Get the environment.
        environment = new Properties();
        environment.put(EntryAccountManager.PROVIDER_URL, "ldap://localhost:389/dc=lazydog,dc=org ldap://ldap1:389/dc=lazydog,dc=org ldap://ldap2:389/dc=lazydog,dc=org");
        environment.put(EntryAccountManager.SECURITY_CREDENTIALS, "@dm1n");
        environment.put(EntryAccountManager.SECURITY_PRINCIPAL, "cn=admin,dc=lazydog,dc=org");

        // Inject the environment.
        this.setEnvironment(environment);
    }
}
