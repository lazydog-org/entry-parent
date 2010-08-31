package org.lazydog.entry.internal.account.manager;

import org.lazydog.entry.account.manager.AccountAlreadyExistsException;
import org.lazydog.entry.account.manager.AccountManager;
import org.lazydog.entry.account.manager.GroupAlreadyExistsException;
import org.lazydog.entry.account.manager.NoSuchAccountException;
import org.lazydog.entry.account.manager.NoSuchGroupException;

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
     * Add members (accounts) to the group.
     *
     * @param  groupName     the group name.
     * @param  accountNames  the account names.
     *
     * @throws  NoSuchGroupException  if the group does not exist.
     * @throws  NullPointerException  if the group name is null.
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
            throw new NoSuchGroupException(groupName, "Group " + groupName + " does not exist.", e);
        }
        catch(NamingException e) {
            // TODO: handle.
            e.printStackTrace();
        }
    }

    /**
     * Change the password for the account.
     *
     * @param  accountName  the account name.
     * @param  password     the password.
     *
     * @throws  NoSuchAccountException  if the account does not exist.
     * @throws  NullPointerException    if the account name is null.
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
            throw new NoSuchAccountException(accountName, "Account " + accountName + " does not exist.", e);
        }
        catch(NamingException e) {
            // TODO: handle.
            e.printStackTrace();
        }
    }

    /**
     * Create the account.
     *
     * @param  accountName  the account name.
     * @param  password     the password.
     *
     * @throws  AccountAlreadyExistsException  if the account already exists.
     * @throws  NullPointerException           if the account name is null.
     */
    public void createAccount(String accountName, String password) {
        this.createAccount(accountName, password, null);
    }

    /**
     * Create the container.
     * 
     * @param  containerName  the container name.
     * 
     * @throws NamingException  if unable to create the container.
     */
    private void createContainer(String containerName) throws NamingException {

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

    /**
     * Create the account.
     *
     * @param  accountName  the account name.
     * @param  password     the password.
     * @param  groupNames   the group names.
     *
     * @throws  AccountAlreadyExistsException  if the account already exists.
     * @throws  NullPointerException           if the account name is null.
     */
    public void createAccount(String accountName, String password, List<String> groupNames) {

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
            attributes.put(new BasicAttribute("userPassword", EncryptPassword.encrypt(password)));

            createContainer(ACCOUNTS_CONTAINER_NAME);

            // Create the account.
            dirContext.createSubcontext(accountDN, attributes);
        }
        catch(NameAlreadyBoundException e) {
            throw new AccountAlreadyExistsException(accountName, "Account " + accountName + " already exists.", e);
        }
        catch(NamingException e) {
            // TODO: handle.
            e.printStackTrace();
        }
    }

    /**
     * Create the group.
     *
     * @param  groupName  the group name.
     *
     * @throws  GroupAlreadyExistsException  if the group already exists.
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

            createContainer(GROUPS_CONTAINER_NAME);

            // Create the group.
            dirContext.createSubcontext(groupDN, attributes);
        }
        catch(NameAlreadyBoundException e) {
            throw new GroupAlreadyExistsException(groupName, "Group " + groupName + " already exists.", e);
        }
        catch(NamingException e) {
            // TODO: handle.
            e.printStackTrace();
        }
    }

    /**
     * Get the account DN.
     * 
     * @param  accountName  the account name.
     * 
     * @return  the account DN.
     * 
     * @throws  InvalidNameException  if unable to get the account DN.
     */
    private static Name getAccountDN(String accountName) throws InvalidNameException {
 
        // Declare.
        Name accountDN;
        List<Rdn> accountRDNs;

        // Get the account DN.
        accountRDNs = new ArrayList<Rdn>();
        accountRDNs.add(new Rdn("uid", accountName));
        accountDN = new LdapName(accountRDNs);
        accountDN.addAll(0, getContainerDN(ACCOUNTS_CONTAINER_NAME));

        return accountDN;
    }

    /**
     * Get the container DN.
     *
     * @param  containerName  the container name.
     *
     * @return  the container DN.
     *
     * @throws  InvalidNameException  if unable to get the container DN.
     */
    private static Name getContainerDN(String containerName) throws InvalidNameException {

        // Declare.
        Name containerDN;
        List<Rdn> containerRDNs;

        // Get the container DN.
        containerRDNs = new ArrayList<Rdn>();
        containerRDNs.add(new Rdn("ou", containerName));
        containerDN = new LdapName(containerRDNs);

        return containerDN;
    }

    /**
     * Get the group DN.
     *
     * @param  groupName  the group name.
     *
     * @return  the group DN.
     *
     * @throws  InvalidNameException  if unable to get the group DN.
     */
    private static Name getGroupDN(String groupName) throws InvalidNameException {

        // Declare.
        Name groupDN;
        List<Rdn> groupRDNs;

        // Get the group DN.
        groupRDNs = new ArrayList<Rdn>();
        groupRDNs.add(new Rdn("cn", groupName));
        groupDN = new LdapName(groupRDNs);
        groupDN.addAll(0, getContainerDN(GROUPS_CONTAINER_NAME));

        return groupDN;
    }
    
    /**
     * Get the groups of the account.
     *
     * @param  accountName  the account name.
     *
     * @return  the groups.
     *
     * @throws  NoSuchAccountException  if the account does not exist.
     * @throws  NullPointerException    if the account name is null.
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
     * @throws  NoSuchGroupException  if the group does not exist.
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
     * Check if the account is locked.
     *
     * @param  accountName  the account name.
     *
     * @return  true if the account is locked, otherwise false.
     *
     * @throws  NoSuchAccountException  if the account does not exist.
     * @throws  NullPointerException    if the account name is null.
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
            throw new NoSuchAccountException(accountName, "Account " + accountName + " does not exist.", e);
        }
        catch(NamingException e) {
            // TODO: handle.
            e.printStackTrace();
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
     * @throws  NoSuchAccountException  if the account does not exist.
     * @throws  NoSuchGroupException    if the group does not exist.
     * @throws  NullPointerException    if the account or group name is null.
     */
    public boolean isMember(String accountName, String groupName) {
        return false;
    }

    /**
     * Lock the account.
     *
     * @param  accountName  the account name.
     *
     * @throws  NoSuchAccountException  if the account does not exist.
     * @throws  NullPointerException    if the account name is null.
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
            throw new NoSuchAccountException(accountName, "Account " + accountName + " does not exist.", e);
        }
        catch(NamingException e) {
            // TODO: handle.
            e.printStackTrace();
        }
    }

    /**
     * Remove the account.
     *
     * @param  accountName  the account name.
     *
     * @throws  NoSuchAccountException  if the account does not exist.
     * @throws  NullPointerException    if the account name is null.
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
            throw new NoSuchAccountException(accountName, "Account " + accountName + " does not exist.", e);
        }
        catch(NamingException e) {
            // TODO: handle.
            e.printStackTrace();
        }
    }

    /**
     * Remove the group.
     *
     * @param  groupName  the group name.
     *
     * @throws  NoSuchGroupException  if the group does not exist.
     * @throws  NullPointerException  if the group name is null.
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
            throw new NoSuchGroupException(groupName, "Group " + groupName + " does not exist.", e);
        }
        catch(NamingException e) {
            // TODO: handle.
            e.printStackTrace();
        }
    }

    /**
     * Remove the members (accounts) from the group.
     *
     * @param  groupName     the group name.
     * @param  accountNames  the account names.
     *
     * @throws  NoSuchGroupException  if the group does not exist.
     * @throws  NullPointerException  if the group name is null.
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
            throw new NoSuchGroupException(groupName, "Group " + groupName + " does not exist.", e);
        }
        catch(NamingException e) {
            // TODO: handle.
            e.printStackTrace();
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
     * @throws  NoSuchAccountException  if the account does not exist.
     * @throws  NullPointerException    if the account name is null.
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
            throw new NoSuchAccountException(accountName, "Account " + accountName + " does not exist.", e);
        }
        catch(NamingException e) {
            // TODO: handle.
            e.printStackTrace();
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
        manager.createAccount("rjrjr", "tek.book", null);
    }
}
