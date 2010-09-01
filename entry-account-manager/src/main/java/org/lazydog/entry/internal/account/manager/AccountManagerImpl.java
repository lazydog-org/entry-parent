package org.lazydog.entry.internal.account.manager;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.naming.Context;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.ldap.Rdn;
import javax.naming.ldap.LdapName;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.lazydog.entry.account.manager.AccountManager;
import org.lazydog.entry.account.manager.AccountManagerException;
import org.lazydog.entry.account.manager.EntryAlreadyExistsException;
import org.lazydog.entry.account.manager.NoSuchEntryException;


/**
 * Account manager implementation.
 *
 * @author  Ron Rickard
 */
public class AccountManagerImpl implements AccountManager {

    private static final String ACCOUNT_LOCK_TIME = "000001010000Z";    
    private static final String ACCOUNTS_CONTAINER_NAME = "Accounts";
    private static final String GROUPS_CONTAINER_NAME = "Groups";
    private DirContext dirContext;

    /**
     * Check if the account exists.
     * 
     * @param  accountName  the account name.
     *
     * @return  true if the account exists, otherwise false.
     *
     * @throws  AccountManagerException  if unable to check if the account exists.
     * @throws  NullPointerException     if the account name is null.
     */
    public boolean accountExists(String accountName) {

        if (accountName == null) {
            throw new NullPointerException("The account name is null.");
        }

        return this.entryExists(getAccountDN(accountName));
    }

    /**
     * Add members (accounts) to the group.
     *
     * @param  groupName     the group name.
     * @param  accountNames  the account names.
     *
     * @throws  AccountManagerException  if unable to add members to the group.
     * @throws  NoSuchEntryException     if the group does not exist.
     * @throws  NullPointerException     if the group name is null.
     */
    public void addMembers(String groupName, List<String> accountNames) {

        if (groupName == null) {
            throw new NullPointerException("The group name is null.");
        }

        try {

            // Declare.
            Name groupDN;
            Attributes attributes;

            // Get the group DN.
            groupDN = getGroupDN(groupName);

            // Set the attributes.
            attributes = new BasicAttributes();
            attributes.put(getUniqueMemberAttribute(accountNames.toArray(new String[accountNames.size()])));

            // Add members to the group.
            dirContext.modifyAttributes(groupDN, DirContext.ADD_ATTRIBUTE, attributes);
        }
        catch(NameNotFoundException e) {
            throw new NoSuchEntryException(
                    groupName, "Group " + groupName + " does not exist.", e);
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to add the members " + accountNames + " to the group " + groupName + ".", e);
        }
    }

    /**
     * Change the password for the account.
     *
     * @param  accountName  the account name.
     * @param  password     the password.
     *
     * @throws  AccountManagerException  if unable to change the password for the account.
     * @throws  NoSuchEntryException     if the account does not exist.
     * @throws  NullPointerException     if the account name is null.
     */
    public void changePassword(String accountName, String password) {


        if (accountName == null) {
            throw new NullPointerException("The account name is null.");
        }

        try {

            // Declare.
            Name accountDN;
            Attributes attributes;

            // Get the account DN.
            accountDN = getAccountDN(accountName);

            // Set the attributes.
            attributes = new BasicAttributes();
            attributes.put(new BasicAttribute("userPassword", EncryptPassword.encrypt(password)));

            // Change the password for the account.
            dirContext.modifyAttributes(accountDN, DirContext.REPLACE_ATTRIBUTE, attributes);
        }
        catch(NameNotFoundException e) {
            throw new NoSuchEntryException(
                    accountName, "Account " + accountName + " does not exist.", e);
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to change the password for the account " + accountName + ".", e);
        }
    }

    /**
     * Check if the entry exists.
     *
     * @param  dn  the DN.
     *
     * @return  true if the container exists, otherwise false.
     *
     * @throws  AccountManagerException  if unable to check if the entry exists.
     */
    private boolean entryExists(Name dn) {

        // Declare.
        boolean entryExists;

        // Initialize.
        entryExists = true;

        try {

            // Get the attributes.
            dirContext.getAttributes(dn);
        }
        catch(NameNotFoundException e) {
            entryExists = false;
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to check if the entry " + dn.toString() + " exists.", e);
        }

        return entryExists;
    }

    /**
     * Check if the container exists.
     *
     * @param  containerName  the container name.
     *
     * @return  true if the container exists, otherwise false.
     *
     * @throws  AccountManagerException  if unable to check if the container exists.
     * @throws  NullPointerException     if the container name is null.
     */
    private boolean containerExists(String containerName) {

        if (containerName == null) {
            throw new NullPointerException("The container name is null.");
        }

        return this.entryExists(getContainerDN(containerName));
    }

    /**
     * Create the account.
     *
     * @param  accountName  the account name.
     * @param  password     the password.
     *
     * @throws  AccountManagerException      if unable to create the account.
     * @throws  EntryAlreadyExistsException  if the account already exists.
     * @throws  NullPointerException         if the account name is null.
     */
    public void createAccount(String accountName, String password) {

        if (accountName == null) {
            throw new NullPointerException("The account name is null.");
        }

        try {

            // Declare.
            Name accountDN;
            Attributes attributes;

            // Get the account DN.
            accountDN = getAccountDN(accountName);

            // Set the attributes.
            attributes = new BasicAttributes();
            attributes.put(getObjectClassAttribute("top", "account", "simpleSecurityObject"));
            attributes.put(new BasicAttribute("uid", accountName));
            //attributes.put(new BasicAttribute("userPassword", EncryptPassword.encrypt(password)));
            attributes.put(new BasicAttribute("userPassword", password));

            // Check if the accounts container does not exist.
            if (!containerExists(ACCOUNTS_CONTAINER_NAME)) {

                // Create the accounts container.
                createContainer(ACCOUNTS_CONTAINER_NAME);
            }

            // Create the account.
            dirContext.createSubcontext(accountDN, attributes);
        }
        catch(NameAlreadyBoundException e) {
            throw new EntryAlreadyExistsException(
                    accountName, "Account " + accountName + " already exists.", e);
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to create the account " + accountName + ".", e);
        }
    }

    /**
     * Create the container.
     * 
     * @param  containerName  the container name.
     *
     * @throws  AccountManagerException      if unable to create the container.
     * @throws  EntryAlreadyExistsException  if the container already exists.
     * @throws  NullPointerException         if the container name is null.
     */
    private void createContainer(String containerName) {

        if (containerName == null) {
            throw new NullPointerException("The container name is null.");
        }

        try {

            // Declare.
            Attributes attributes;
            Name containerDN;

            // Get the container DN.
            containerDN = getContainerDN(containerName);

            // Set the attributes.
            attributes = new BasicAttributes();
            attributes.put(getObjectClassAttribute("top", "organizationalUnit"));
            attributes.put(new BasicAttribute("ou", containerName));

            // Create the container.
            dirContext.createSubcontext(containerDN, attributes);
        }
        catch(NameAlreadyBoundException e) {
            throw new EntryAlreadyExistsException(
                    containerName, "Container " + containerName + " already exists.", e);
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to create the container " + containerName + ".", e);
        }
    }

    /**
     * Create the group.
     *
     * @param  groupName  the group name.
     *
     * @throws  AccountManagerException      if unable to create the group.
     * @throws  EntryAlreadyExistsException  if the group already exists.
     * @throws  NullPointerException         if the group name is null.
     */
    public void createGroup(String groupName) {

        if (groupName == null) {
            throw new NullPointerException("The group name is null.");
        }

        try {

            // Declare.
            Attributes attributes;
            Name groupDN;
            
            // Get the group DN.
            groupDN = getGroupDN(groupName);

            // Set the attributes.
            attributes = new BasicAttributes();
            attributes.put(getObjectClassAttribute("top", "groupOfUniqueNames"));
            attributes.put(new BasicAttribute("cn", groupName));

            // Check if the groups container does not exist.
            if (!containerExists(GROUPS_CONTAINER_NAME)) {

                // Create the groups container.
                createContainer(GROUPS_CONTAINER_NAME);
            }

            // Create the group.
            dirContext.createSubcontext(groupDN, attributes);
        }
        catch(NameAlreadyBoundException e) {
            throw new EntryAlreadyExistsException(
                    groupName, "Group " + groupName + " already exists.", e);
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to create the group " + groupName + ".", e);
        }
    }

    /**
     * Get the account DN.
     * 
     * @param  accountName  the account name.
     * 
     * @return  the account DN.
     * 
     * @throws  AccountManagerException  if unable to get the account DN.
     */
    private static Name getAccountDN(String accountName) {
 
        // Declare.
        Name accountDN;

        try {

            // Declare.
            List<Rdn> accountRDNs;

            // Get the account DN.
            accountRDNs = new ArrayList<Rdn>();
            accountRDNs.add(new Rdn("uid", accountName));
            accountDN = new LdapName(accountRDNs);
            accountDN.addAll(0, getContainerDN(ACCOUNTS_CONTAINER_NAME));
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to get the account DN for " + accountName + ".", e);
        }

        return accountDN;
    }

    /**
     * Get the container DN.
     *
     * @param  containerName  the container name.
     *
     * @return  the container DN.
     *
     * @throws  AccountManagerException  if unable to get the container DN.
     */
    private static Name getContainerDN(String containerName) {

        // Declare.
        Name containerDN;

        try {

            // Declare.
            List<Rdn> containerRDNs;

            // Get the container DN.
            containerRDNs = new ArrayList<Rdn>();
            containerRDNs.add(new Rdn("ou", containerName));
            containerDN = new LdapName(containerRDNs);
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to get the container DN for " + containerName + ".", e);
        }

        return containerDN;
    }

    /**
     * Get the group DN.
     *
     * @param  groupName  the group name.
     *
     * @return  the group DN.
     *
     * @throws  AccountManagerException  if unable to get the group DN.
     */
    private static Name getGroupDN(String groupName) {

        // Declare.
        Name groupDN;

        try {

            // Declare.
            List<Rdn> groupRDNs;

            // Get the group DN.
            groupRDNs = new ArrayList<Rdn>();
            groupRDNs.add(new Rdn("cn", groupName));
            groupDN = new LdapName(groupRDNs);
            groupDN.addAll(0, getContainerDN(GROUPS_CONTAINER_NAME));
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to get the group DN for " + groupName + ".", e);
        }

        return groupDN;
    }
    
    /**
     * Get the groups of the account.
     *
     * @param  accountName  the account name.
     *
     * @return  the groups.
     *
     * @throws  NoSuchEntryException  if the account does not exist.
     * @throws  NullPointerException  if the account name is null.
     */
    public List<String> getGroups(String accountName) {
        return null;
    }

    /**
     * Get the members (accounts) of the group.
     *
     * @param  groupName  the group name.
     *
     * @return  the members.
     *
     * @throws  NoSuchEntryException  if the group does not exist.
     * @throws  NullPointerException  if the group name is null.
     */
    public List<String> getMembers(String groupName) {
        return null;
    }

    /**
     * Get the object class attribute.
     *
     * @param  objectClassValues  the object class values.
     *
     * @return  the object class attribute.
     */
    private static Attribute getObjectClassAttribute(String... objectClassValues) {

        // Declare.
        Attribute objectClassAttribute;

        // Initialize.
        objectClassAttribute = new BasicAttribute("objectClass");

        // Loop through the object class values.
        for (String objectClassValue : objectClassValues) {

            // Add the object class value to the attribute.
            objectClassAttribute.add(objectClassValue);
        }

        return objectClassAttribute;
    }

    /**
     * Get the unique member attribute.
     *
     * @param  uniqueMemberValues  the unique member values.
     *
     * @return  the unique member values.
     *
     * @throws  InvalidNameException  if unable to get the unique member attribute.
     */
    private static Attribute getUniqueMemberAttribute(String... uniqueMemberValues) throws InvalidNameException {

        // Declare.
        Attribute uniqueMemberAttribute;

        // Initialize.
        uniqueMemberAttribute = new BasicAttribute("uniqueMember");

        // Loop through the unique member values.
        for (String uniqueMemberValue : uniqueMemberValues) {

            // Add the unique member value to the attribute.
            uniqueMemberAttribute.add(getAccountDN(uniqueMemberValue).toString());
        }

        return uniqueMemberAttribute;
    }

    /**
     * Check if the group exists.
     *
     * @param  groupName  the group name.
     *
     * @return  true if the group exists, otherwise false.
     *
     * @throws  AccountManagerException  if unable to check if the group exists.
     * @throws  NullPointerException     if the group name is null.
     */
    public boolean groupExists(String groupName) {

        if (groupName == null) {
            throw new NullPointerException("The account name is null.");
        }

        return this.entryExists(getGroupDN(groupName));
    }

    /**
     * Check if the account is locked.
     *
     * @param  accountName  the account name.
     *
     * @return  true if the account is locked, otherwise false.
     *
     * @throws  AccountManagerException  if unable to check if the account is locked.
     * @throws  NoSuchEntryException     if the account does not exist.
     * @throws  NullPointerException     if the account name is null.
     */
    public boolean isAccountLocked(String accountName) {

        // Declare.
        boolean isAccountLocked;

        // Initialize.
        isAccountLocked = true;

        if (accountName == null) {
            throw new NullPointerException("The account name is null.");
        }

        try {

            // Declare.
            Attributes attributes;
            Name accountDN;

            // Get the account DN.
            accountDN = getAccountDN(accountName);

            // Get the attributes.
            attributes = dirContext.getAttributes(accountDN, new String[] {"pwdAccountLockedTime"});

            // Check if the account is not locked.
            if (attributes.get("pwdAccountLockedTime") == null) {
                isAccountLocked = false;
            }
        }
        catch(NameNotFoundException e) {
            throw new NoSuchEntryException(
                    accountName, "Account " + accountName + " does not exist.", e);
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to check if the account " + accountName + " is locked.", e);
        }

        return isAccountLocked;
    }

    /**
     * Check if the account is a member of the group.
     *
     * @param  accountName  the account name.
     * @param  groupName    the group name.
     *
     * @return  true if the account is a member, otherwise false.
     *
     * @throws  AccountManagerException  if unable to check if the account is a member of the group.
     * @throws  NoSuchEntryException     if the account and/or group does not exist.
     * @throws  NullPointerException     if the account and/or group name is null.
     */
    public boolean isMember(String accountName, String groupName) {
        return false;
    }

    /**
     * Lock the account.
     *
     * @param  accountName  the account name.
     *
     * @throws  AccountManagerException  if unable to lock the account.
     * @throws  NoSuchEntryException     if the account does not exist.
     * @throws  NullPointerException     if the account name is null.
     */
    public void lockAccount(String accountName) {

        if (accountName == null) {
            throw new NullPointerException("The account name is null.");
        }

        try {

            // Declare.
            Name accountDN;
            Attributes attributes;

            // Get the account DN.
            accountDN = getAccountDN(accountName);

            // Set the attributes.
            attributes = new BasicAttributes();
            attributes.put(new BasicAttribute("pwdAccountLockedTime", ACCOUNT_LOCK_TIME));
            
            // Lock the account.
            dirContext.modifyAttributes(accountDN, DirContext.ADD_ATTRIBUTE, attributes);
        }
        catch(NameNotFoundException e) {
            throw new NoSuchEntryException(
                    accountName, "Account " + accountName + " does not exist.", e);
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to lock the account " + accountName + ".", e);
        }
    }

    /**
     * Remove the account.
     *
     * @param  accountName  the account name.
     *
     * @throws  AccountManagerException  if unable to remove the account.
     * @throws  NoSuchEntryException     if the account does not exist.
     * @throws  NullPointerException     if the account name is null.
     */
    public void removeAccount(String accountName) {

        if (accountName == null) {
            throw new NullPointerException("The account name is null.");
        }

        try {

            // Declare.
            Name accountDN;

            // Get the account DN.
            accountDN = getAccountDN(accountName);

            // Remove the account.
            dirContext.destroySubcontext(accountDN);
        }
        catch(NameNotFoundException e) {
            throw new NoSuchEntryException(
                    accountName, "Account " + accountName + " does not exist.", e);
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to remove the account " + accountName + ".", e);
        }
    }

    /**
     * Remove the group.
     *
     * @param  groupName  the group name.
     *
     * @throws  AccountManagerException  if unable to remove the group.
     * @throws  NoSuchEntryException     if the group does not exist.
     * @throws  NullPointerException     if the group name is null.
     */
    public void removeGroup(String groupName) {

        if (groupName == null) {
            throw new NullPointerException("The group name is null.");
        }

        try {

            // Declare.
            Name groupDN;

            // Get the group DN.
            groupDN = getGroupDN(groupName);

            // Remove the group.
            dirContext.destroySubcontext(groupDN);
        }
        catch(NameNotFoundException e) {
            throw new NoSuchEntryException(
                    groupName, "Group " + groupName + " does not exist.", e);
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to remove the group " + groupName + ".", e);
        }
    }

    /**
     * Remove the members (accounts) from the group.
     *
     * @param  groupName     the group name.
     * @param  accountNames  the account names.
     *
     * @throws  AccountManagerException  if unable to remove the members from the group.
     * @throws  NoSuchEntryException     if the group does not exist.
     * @throws  NullPointerException     if the group name is null.
     */
    public void removeMembers(String groupName, List<String> accountNames) {

        if (groupName == null) {
            throw new NullPointerException("The group name is null.");
        }

        try {

            // Declare.
            Name groupDN;
            Attributes attributes;

            // Get the group DN.
            groupDN = getGroupDN(groupName);

            // Set the attributes.
            attributes = new BasicAttributes();
            attributes.put(getUniqueMemberAttribute(accountNames.toArray(new String[accountNames.size()])));

            // Add members to the group.
            dirContext.modifyAttributes(groupDN, DirContext.REMOVE_ATTRIBUTE, attributes);
        }
        catch(NameNotFoundException e) {
            throw new NoSuchEntryException(
                    groupName, "Group " + groupName + " does not exist.", e);
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to remove the members " + accountNames + " from the group " + groupName + ".", e);
        }
    }

    /**
     * Set the directory context.
     * 
     * @param  dirContext  the directory context.
     */
    public void setDirContext(DirContext dirContext) {
        this.dirContext = dirContext;
    }

    /**
     * Unlock the account.
     *
     * @param  accountName  the account name.
     *
     * @throws  AccountManagerException  if unable to unlock the account.
     * @throws  NoSuchEntryException     if the account does not exist.
     * @throws  NullPointerException     if the account name is null.
     */
    public void unlockAccount(String accountName) {

        if (accountName == null) {
            throw new NullPointerException("The account name is null.");
        }
        
        try {

            // Declare.
            Name accountDN;
            Attributes attributes;

            // Get the account DN.
            accountDN = getAccountDN(accountName);

            // Set the attributes.
            attributes = new BasicAttributes();
            attributes.put(new BasicAttribute("pwdAccountLockedTime", ACCOUNT_LOCK_TIME));

            // Unlock the account.
            dirContext.modifyAttributes(accountDN, DirContext.REMOVE_ATTRIBUTE, attributes);
        }
        catch(NameNotFoundException e) {
            throw new NoSuchEntryException(
                    accountName, "Account " + accountName + " does not exist.", e);
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to unlock the account " + accountName + ".", e);
        }
    }

    

    
    public static void main(String[] args) throws Exception {

        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_CREDENTIALS, "@dm1n");
        env.put(Context.SECURITY_PRINCIPAL, "cn=admin,dc=lazydog,dc=org");
        env.put(Context.PROVIDER_URL, "ldap://localhost/dc=lazydog,dc=org ldap://ldap1/dc=lazydog,dc=org ldap://ldap2/dc=lazydog,dc=org");

        DirContext context = new InitialDirContext(env);

        AccountManagerImpl manager = new AccountManagerImpl();
        manager.setDirContext(context);
        //manager.removeAccount("rjrjr");
        manager.createAccount("rjrjr", "tek.book");
System.out.println(manager.accountExists("rjrjr"));
    }
}
