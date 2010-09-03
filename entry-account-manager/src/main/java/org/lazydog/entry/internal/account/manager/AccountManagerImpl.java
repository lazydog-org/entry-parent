package org.lazydog.entry.internal.account.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Properties;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.ldap.Rdn;
import javax.naming.ldap.LdapName;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.lazydog.entry.spi.account.manager.AccountManager;
import org.lazydog.entry.spi.account.manager.AccountManagerException;
import org.lazydog.entry.spi.account.manager.EntryAlreadyExistsException;
import org.lazydog.entry.spi.account.manager.NoSuchEntryException;


/**
 * Account manager implementation.
 *
 * @author  Ron Rickard
 */
public class AccountManagerImpl implements AccountManager {

    private static final String ACCOUNT_LOCK_TIME = "000001010000Z";    
    private static final String ACCOUNTS_CONTAINER_NAME = "Accounts";
    private static final String GROUPS_CONTAINER_NAME = "Groups";
    private static final String DUMMY_ACCOUNT_NAME = "dummy";
    private static final String DUMMY_ACCOUNT_PASSWORD = "dummy";
    private DirContext dirContext;

    /**
     * Constructor.
     * 
     * @param  dirContext  the directory context.
     */
    public AccountManagerImpl(DirContext dirContext) {
        this.dirContext = dirContext;
    }

    /**
     * Check if the account exists.
     * 
     * @param  accountName  the account name.
     *
     * @return  true if the account exists, otherwise false.
     *
     * @throws  AccountManagerException   if unable to check if the account exists.
     * @throws  IllegalArgumentException  if the account name is invalid.
     */
    @Override
    public boolean accountExists(String accountName) {

        // Declare.
        boolean accountExists;

        checkEmpty(accountName, "The account name is invalid.");

        try {

            // Check if the account exists.
            accountExists = entryExists(getAccountRDN(accountName));
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to check if the account " + accountName + " exists.", e);
        }

        return accountExists;
    }

    /**
     * Add members (accounts) to the group.
     *
     * @param  groupName     the group name.
     * @param  accountNames  the account names.
     *
     * @throws  AccountManagerException   if unable to add members to the group.
     * @throws  IllegalArgumentException  if the group name and/or the account names is invalid.
     * @throws  NoSuchEntryException      if the group and/or one or more of the accounts do not exist.
     */
    @Override
    public void addMembers(String groupName, Set<String> accountNames) {

        checkEmpty(groupName, "The group name is invalid.");
        checkEmpty(accountNames, "The account names is invalid.");
        checkNoSuchGroup(groupName, "The group " + groupName + " does not exist.");
        checkNoSuchAccount(accountNames, "One or more of the accounts " + accountNames + " do not exist.");

        try {

            // Declare.
            Attributes attributes;

            // Set the attributes.
            attributes = new BasicAttributes();
            attributes.put(getUniqueMemberAttribute(getUniqueMemberValues(accountNames)));

            // Add members to the group.
            dirContext.modifyAttributes(getGroupRDN(groupName), DirContext.ADD_ATTRIBUTE, attributes);
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
     * @throws  AccountManagerException   if unable to change the password for the account.
     * @throws  IllegalArgumentException  if the account name and/or password is invalid.
     * @throws  NoSuchEntryException      if the account does not exist.
     * 
     */
    @Override
    public void changePassword(String accountName, String password) {

        checkEmpty(accountName, "The account name is invalid.");
        checkEmpty(password, "The password is invalid.");
        checkNoSuchAccount(accountName, "The account " + accountName + " does not exist.");

        try {

            // Declare.
            Attributes attributes;

            // Set the attributes.
            attributes = new BasicAttributes();
            attributes.put(new BasicAttribute("userPassword", password));
//attributes.put(new BasicAttribute("userPassword", EncryptPassword.encrypt(password)));

            // Change the password for the account.
            dirContext.modifyAttributes(getAccountRDN(accountName), DirContext.REPLACE_ATTRIBUTE, attributes);
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to change the password for the account " + accountName + ".", e);
        }
    }

    /**
     * Check if the value is empty.
     * 
     * @param  value    the value.
     * @param  message  the message to throw if the value is empty.
     * 
     * @throws  IllegalArgumentException  if the value is empty.
     */
    private static void checkEmpty(String value, String message) {

        // Check if the value is empty.
        if (value == null || "".equals(value)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Check if one or more of the values are empty.
     * 
     * @param  values   the values.
     * @param  message  the message to throw if the values are empty.
     * 
     * @throws  IllegalArgumentException  if one or more of the values are empty.
     */
    private static void checkEmpty(Set<String> values, String message) {

        // Check if the values are empty.
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        else {

            // Loop through the values.
            for (String value : values) {

                // Check if the value is empty.
                checkEmpty(value, message);
            }
        }
    }

    /**
     * Check if the entry does not exist.
     *
     * @param  entryName    the entry name.
     * @param  entryExists  true if the entry exists, otherwise false.
     * @param  message      the message to throw if the entry does not exist.
     *
     * @throws  NoSuchEntryException  if the entry does not exist.
     */
    private void checkNoSuchEntry(String entryName, boolean entryExists, String message) {

        // Check if the account does not exist.
        if (!entryExists) {
            throw new NoSuchEntryException(entryName, message);
        }
    }

    /**
     * Check if the account does not exist.
     * 
     * @param  accountName  the account name.
     * @param  message      the message to throw if the account does not exist.
     * 
     * @throws  NoSuchEntryException  if the account does not exist.
     */
    private void checkNoSuchAccount(String accountName, String message) {
        checkNoSuchEntry(accountName, accountExists(accountName), message);
    }

    /**
     * Check if one or more of the accounts do not exist.
     *
     * @param  accountNames  the account names.
     * @param  message       the message to throw if one or more of the accounts do not exist.
     *
     * @throws  NoSuchEntryException  if one or more of the accounts do not exist.
     */
    private void checkNoSuchAccount(Set<String> accountNames, String message) {

        // Loop through the account names.
        for(String accountName : accountNames) {

            // Check if the account does not exist.
            checkNoSuchAccount(accountName, message);
        }
    }

    /**
     * Check if the group does not exist.
     *
     * @param  groupName  the group name.
     * @param  message    the message to throw if the group does not exist.
     *
     * @throws  NoSuchEntryException  if the group does not exist.
     */
    private void checkNoSuchGroup(String groupName, String message) {
        checkNoSuchEntry(groupName, groupExists(groupName), message);
    }

    /**
     * Check if the entry exists.
     *
     * @param  rdn  the RDN.
     *
     * @return  true if the container exists, otherwise false.
     *
     * @throws  NamingException  if unable to check if the entry exists.
     */
    private boolean entryExists(Name rdn) throws NamingException {

        // Declare.
        boolean entryExists;

        // Initialize.
        entryExists = true;

        try {

            // Get the attributes.
            dirContext.getAttributes(rdn);
        }
        catch(NameNotFoundException e) {
            entryExists = false;
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
     * @throws  NamingException  if unable to check if the container exists.
     */
    private boolean containerExists(String containerName) throws NamingException {
        return entryExists(getContainerRDN(containerName));
    }

    /**
     * Create the account.
     *
     * @param  accountName  the account name.
     * @param  password     the password.
     *
     * @throws  AccountManagerException      if unable to create the account.
     * @throws  EntryAlreadyExistsException  if the account already exists.
     * @throws  IllegalArgumentException     if the account name and/or password is invalid.
     */
    @Override
    public void createAccount(String accountName, String password) {

        checkEmpty(accountName, "The account name is invalid.");
        checkEmpty(password, "The password is invalid.");

        try {

            // Declare.
            Attributes attributes;

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
            dirContext.createSubcontext(getAccountRDN(accountName), attributes);
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
     * @throws  NamingException  if unable to create the container.
     */
    private void createContainer(String containerName) throws NamingException {

        // Declare.
        Attributes attributes;

        // Set the attributes.
        attributes = new BasicAttributes();
        attributes.put(getObjectClassAttribute("top", "organizationalUnit"));
        attributes.put(new BasicAttribute("ou", containerName));

        // Create the container.
        dirContext.createSubcontext(getContainerRDN(containerName), attributes);
    }

    /**
     * Create the group.
     *
     * @param  groupName  the group name.
     *
     * @throws  AccountManagerException      if unable to create the group.
     * @throws  EntryAlreadyExistsException  if the group already exists.
     * @throws  IllegalArgumentException     if the group name is invalid.
     */
    @Override
    public void createGroup(String groupName) {

        checkEmpty(groupName, "The group name is invalid.");

        try {

            // Declare.
            Attributes attributes;

            // Set the attributes.
            attributes = new BasicAttributes();
            attributes.put(getObjectClassAttribute("top", "groupOfUniqueNames"));
            attributes.put(new BasicAttribute("cn", groupName));
            attributes.put(new BasicAttribute("uniqueMember", getDummyAccountDN().toString()));

            // Check if the groups container does not exist.
            if (!containerExists(GROUPS_CONTAINER_NAME)) {

                // Create the groups container.
                createContainer(GROUPS_CONTAINER_NAME);
            }

            // Check if the dummy account does not exist.
            if (!accountExists(DUMMY_ACCOUNT_NAME)) {

                // Create the dummy account.
                createAccount(DUMMY_ACCOUNT_NAME, DUMMY_ACCOUNT_PASSWORD);
            }

            // Create the group.
            dirContext.createSubcontext(getGroupRDN(groupName), attributes);
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
     * @throws  NamingException  if unable to get the account DN.
     */
    private Name getAccountDN(String accountName) throws NamingException {
        return getAccountRDN(accountName).addAll(0, getBaseDN());
    }

    /**
     * Get the account names.
     * 
     * @param  uniqueMemberAttribute  the unique member attribute.
     * 
     * @return  the account names.
     * 
     * @throws  NamingException  if unable to get the account names.
     */
    private static Set<String> getAccountNames(Attribute uniqueMemberAttribute) throws NamingException {

        // Declare.
        Set<String> accountNames;

        // Initialize.
        accountNames = new HashSet<String>();
        
        // Check if the unique member attribute exists.
        if (uniqueMemberAttribute != null) {

            // Loop through the unique member values.
            for (int x = 0; x < uniqueMemberAttribute.size(); x++) {

                // Declare.
                LdapName accountRDN;

                // Get the account RDN (unique member value).
                accountRDN = new LdapName((String)uniqueMemberAttribute.get(x));

                // Loop through RDNs for the account RDN.
                for (Rdn rdn : accountRDN.getRdns()) {

                    // Check if the RDN attribute ID is uid.
                    if (rdn.getType().equals("uid")) {

                        // Add the account name (RDN attribute value) to the set.
                        accountNames.add((String)rdn.getValue());
                        break;
                    }
                }
            }
        }

        return accountNames;
    }

    /**
     * Get the account RDN.
     *
     * @param  accountName  the account name.
     *
     * @return  the account RDN.
     *
     * @throws  NamingException  if unable to get the account RDN.
     */
    private static Name getAccountRDN(String accountName) throws NamingException {

        // Declare.
        Name accountRDN;
        List<Rdn> accountRDNs;

        // Get the account RDN.
        accountRDNs = new ArrayList<Rdn>();
        accountRDNs.add(new Rdn("uid", accountName));
        accountRDN = new LdapName(accountRDNs);
        accountRDN.addAll(0, getContainerRDN(ACCOUNTS_CONTAINER_NAME));

        return accountRDN;
    }

    /**
     * Get the base DN.
     *
     * @return  the base DN.
     *
     * @throws  NamingException  if unable to get the base DN.
     */
    private Name getBaseDN() throws NamingException {
        return new LdapName(dirContext.getNameInNamespace());
    }

    /**
     * Get the container RDN.
     *
     * @param  containerName  the container name.
     *
     * @return  the container RDN.
     *
     * @throws  NamingException  if unable to get the container RDN.
     */
    private static Name getContainerRDN(String containerName) throws NamingException {

        // Declare.
        Name containerRDN;
        List<Rdn> containerRDNs;

        // Get the container RDN.
        containerRDNs = new ArrayList<Rdn>();
        containerRDNs.add(new Rdn("ou", containerName));
        containerRDN = new LdapName(containerRDNs);

        return containerRDN;
    }

    /**
     * Get the dummy account DN.
     * 
     * @return  the dummy account DN.
     * 
     * @throws  NamingException  if unable to get the dummy account DN.
     */
    private Name getDummyAccountDN() throws NamingException {
        return getAccountDN(DUMMY_ACCOUNT_NAME);
    }

    /**
     * Get the group names.
     *
     * @param  memberOfAttribute  the member of attribute.
     *
     * @return  the group names.
     *
     * @throws  NamingException  if unable to get the group names.
     */
    private static Set<String> getGroupNames(Attribute memberOfAttribute) throws NamingException {

        // Declare.
        Set<String> groupNames;

        // Initialize.
        groupNames = new HashSet<String>();

        // Check if the unique member attribute exists.
        if (memberOfAttribute != null) {

            // Loop through the member of values.
            for (int x = 0; x < memberOfAttribute.size(); x++) {

                // Declare.
                LdapName groupRDN;

                // Get the group RDN (member of value).
                groupRDN = new LdapName((String)memberOfAttribute.get(x));

                // Loop through RDNs for the group RDN.
                for (Rdn rdn : groupRDN.getRdns()) {

                    // Check if the RDN attribute ID is cn.
                    if (rdn.getType().equals("cn")) {

                        // Add the group name (RDN attribute value) to the set.
                        groupNames.add((String)rdn.getValue());
                        break;
                    }
                }
            }
        }

        return groupNames;
    }

    /**
     * Get the group RDN.
     *
     * @param  groupName  the group name.
     *
     * @return  the group RDN.
     *
     * @throws  NamingException  if unable to get the group RDN.
     */
    private static Name getGroupRDN(String groupName) throws NamingException {

        // Declare.
        Name groupRDN;
        List<Rdn> groupRDNs;

        // Get the group RDN.
        groupRDNs = new ArrayList<Rdn>();
        groupRDNs.add(new Rdn("cn", groupName));
        groupRDN = new LdapName(groupRDNs);
        groupRDN.addAll(0, getContainerRDN(GROUPS_CONTAINER_NAME));

        return groupRDN;
    }

    /**
     * Get the groups of the account.
     *
     * @param  accountName  the account name.
     *
     * @return  the groups.
     *
     * @throws  AccountManagerException   if unable to get the groups of the account.
     * @throws  IllegalArgumentException  if the account name is invalid.
     * @throws  NoSuchEntryException      if the account does not exist.
     */
    @Override
    public Set<String> getGroups(String accountName) {

        // Declare.
        Set<String> groupNames;

        checkEmpty(accountName, "The account name is invalid.");
        checkNoSuchAccount(accountName, "The account " + accountName + " does not exist.");

        try {

            // Declare.
            Attributes attributes;

            // Get the member of attribute for the account.
            attributes = dirContext.getAttributes(getAccountRDN(accountName), new String[]{"memberOf"});

            // Get the group names.
            groupNames = getGroupNames(attributes.get("memberOf"));
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to get the groups of the account " + accountName + ".", e);
        }

        return groupNames;
    }

    /**
     * Get the members (accounts) of the group.
     *
     * @param  groupName  the group name.
     *
     * @return  the members.
     *
     * @throws  AccountManagerException   if unable to get the members of the group.
     * @throws  IllegalArgumentException  if the group name is invalid.
     * @throws  NoSuchEntryException      if the group does not exist.
     */
    @Override
    public Set<String> getMembers(String groupName) {

        // Declare.
        Set<String> accountNames;

        checkEmpty(groupName, "The group name is invalid.");
        checkNoSuchGroup(groupName, "The group " + groupName + " does not exist.");

        try {

            // Declare.
            Attributes attributes;

            // Get the unique member attribute for the group.
            attributes = dirContext.getAttributes(getGroupRDN(groupName), new String[]{"uniqueMember"});

            // Get the account names.
            accountNames = getAccountNames(attributes.get("uniqueMember"));

            // Remove the dummy account name from the set.
            accountNames.remove(DUMMY_ACCOUNT_NAME);
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to get the members of the group " + groupName + ".", e);
        }

        return accountNames;
    }

    /**
     * Get the multi-value attribute.
     * 
     * @param  attributeId      the attribute ID.
     * @param  attributeValues  the attribute values.
     * 
     * @return  the multi-value attribute.
     */
    private static Attribute getMultiValueAttribute(String attributeId, String... attributeValues) {

        // Declare.
        Attribute attribute;

        // Initialize.
        attribute = new BasicAttribute(attributeId);

        // Loop through the attribute values.
        for (String attributeValue : attributeValues) {

            // Add the attribute value to the attribute.
            attribute.add(attributeValue);
        }

        return attribute;
    }

    /**
     * Get the object class attribute.
     *
     * @param  objectClassValues  the object class values.
     *
     * @return  the object class attribute.
     */
    private static Attribute getObjectClassAttribute(String... objectClassValues) {
        return getMultiValueAttribute("objectClass", objectClassValues);
    }

    /**
     * Get the unique member attribute.
     *
     * @param  uniqueMemberValues  the unique member values.
     *
     * @return  the unique member values.
     */
    private static Attribute getUniqueMemberAttribute(String... uniqueMemberValues) {
        return getMultiValueAttribute("uniqueMember", uniqueMemberValues);
    }

    /**
     * Get the unique member values.
     * 
     * @param  accountNames  the account names.
     * 
     * @return  the unique member values.
     *
     * @throws  NamingException  if unable to get the unique member values.
     */
    private String[] getUniqueMemberValues(Set<String> accountNames) throws NamingException {

        // Declare.
        int count;
        String[] uniqueMemberValues;

        // Initialize.
        count = 0;
        uniqueMemberValues = new String[accountNames.size()];

        // Loop through the account names.
        for (String accountName : accountNames) {

            // Add the unique member value to the array.
            uniqueMemberValues[count++] = getAccountDN(accountName).toString();
        }

        return uniqueMemberValues;
    }

    /**
     * Check if the group exists.
     *
     * @param  groupName  the group name.
     *
     * @return  true if the group exists, otherwise false.
     *
     * @throws  AccountManagerException   if unable to check if the group exists.
     * @throws  IllegalArgumentException  if the group name is invalid.
     */
    @Override
    public boolean groupExists(String groupName) {

        // Declare.
        boolean groupExists;

        checkEmpty(groupName, "The group name is invalid.");

        try {
        
            // Check if the group exists.
            groupExists = entryExists(getGroupRDN(groupName));
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to check if the group " + groupName + " exists.", e);
        }

        return groupExists;
    }

    /**
     * Check if the account is locked.
     *
     * @param  accountName  the account name.
     *
     * @return  true if the account is locked, otherwise false.
     *
     * @throws  AccountManagerException   if unable to check if the account is locked.
     * @throws  IllegalArgumentException  if the account name is invalid.
     * @throws  NoSuchEntryException      if the account does not exist.
     */
    @Override
    public boolean isAccountLocked(String accountName) {

        // Declare.
        boolean isAccountLocked;

        // Initialize.
        isAccountLocked = true;

        checkEmpty(accountName, "The account name is invalid.");
        checkNoSuchAccount(accountName, "The account " + accountName + " does not exist.");

        try {

            // Declare.
            Attributes attributes;

            // Get the attributes.
            attributes = dirContext.getAttributes(getAccountRDN(accountName), new String[] {"pwdAccountLockedTime"});

            // Check if the account is not locked.
            if (attributes.get("pwdAccountLockedTime") == null) {
                isAccountLocked = false;
            }
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
     * @throws  AccountManagerException   if unable to check if the account is a member of the group.
     * @throws  IllegalArgumentException  if the account and/or group name is invalid.
     * @throws  NoSuchEntryException      if the account and/or group does not exist.
     */
    @Override
    public boolean isMember(String accountName, String groupName) {

        checkEmpty(accountName, "The account name is invalid.");
        checkEmpty(groupName, "The group name is invalid.");
        checkNoSuchAccount(accountName, "The account " + accountName + " does not exist.");
        checkNoSuchGroup(groupName, "The group " + groupName + " does not exist.");

        // Check if the account is a member of the group.
        return getMembers(groupName).contains(accountName);
    }

    /**
     * Check if the password is the password for the account.
     *
     * @param  accountName  the account name.
     * @param  password     the password to verify.
     *
     * @return  true if the password is the password for the account, otherwise false.
     *
     * @throws  AccountManagerException   if unable to check if the password is the password for the account.
     * @throws  IllegalArgumentException  if the account name and/or password is invalid.
     * @throws  NoSuchEntryException      if the account does not exist.
     */
    @Override
    public boolean isPassword(String accountName, String password) {

        // Declare.
        boolean isPassword;

        // Initialize.
        isPassword = true;

        checkEmpty(accountName, "The account name is invalid.");
        checkEmpty(password, "The password is invalid.");
        checkNoSuchAccount(accountName, "The account " + accountName + " does not exist.");

        try {

            // Declare.
            Properties tempEnvironment;
            Properties environment;
            DirContext tempDirContext;

            // Get the environment for the directory context.
            environment = (Properties)dirContext.getEnvironment();

            // Set the temporary directory context.
            tempEnvironment = new Properties();
            tempEnvironment.put(Context.INITIAL_CONTEXT_FACTORY, environment.getProperty(Context.INITIAL_CONTEXT_FACTORY));
            tempEnvironment.put(Context.SECURITY_AUTHENTICATION, environment.getProperty(Context.SECURITY_AUTHENTICATION));
            tempEnvironment.put(Context.SECURITY_CREDENTIALS, password);
            tempEnvironment.put(Context.SECURITY_PRINCIPAL, getAccountDN(accountName).toString());
            tempEnvironment.put(Context.PROVIDER_URL, environment.getProperty(Context.PROVIDER_URL));
            tempDirContext = new InitialDirContext(tempEnvironment);

            // Check if the password is the password for the account.
            tempDirContext.getAttributes(getAccountRDN(accountName));
        }
        catch(AuthenticationException e) {
            isPassword = false;
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to check if the password is the password for the account " + accountName + ".", e);
        }

        return isPassword;
    }

    /**
     * Lock the account.
     *
     * @param  accountName  the account name.
     *
     * @throws  AccountManagerException   if unable to lock the account.
     * @throws  IllegalArgumentException  if the account name is invalid.
     * @throws  NoSuchEntryException      if the account does not exist.
     */
    @Override
    public void lockAccount(String accountName) {

        checkEmpty(accountName, "The account name is invalid.");
        checkNoSuchAccount(accountName, "The account " + accountName + " does not exist.");

        try {

            // Declare.
            Attributes attributes;

            // Set the attributes.
            attributes = new BasicAttributes();
            attributes.put(new BasicAttribute("pwdAccountLockedTime", ACCOUNT_LOCK_TIME));
            
            // Lock the account.
            dirContext.modifyAttributes(getAccountRDN(accountName), DirContext.ADD_ATTRIBUTE, attributes);
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
     * @throws  AccountManagerException   if unable to remove the account.
     * @throws  IllegalArgumentException  if the account name is invalid.
     * @throws  NoSuchEntryException      if the account does not exist.
     */
    @Override
    public void removeAccount(String accountName) {

        checkEmpty(accountName, "The account name is invalid.");
        checkNoSuchAccount(accountName, "The account " + accountName + " does not exist.");

        try {

            // Declare.
            Set<String> accountNames;

            // Add this account to a set of account names.
            accountNames = new HashSet<String>();
            accountNames.add(accountName);

            // Loop through the group names of the account.
            for (String groupName : getGroups(accountName)) {

                // Remove this account (member) from the group.
                removeMembers(groupName, accountNames);
            }

            // Remove the account.
            dirContext.destroySubcontext(getAccountRDN(accountName));
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
     * @throws  AccountManagerException   if unable to remove the group.
     * @throws  IllegalArgumentException  if the group name is invalid.
     * @throws  NoSuchEntryException      if the group does not exist.
     */
    @Override
    public void removeGroup(String groupName) {

        checkEmpty(groupName, "The group name is invalid.");
        checkNoSuchGroup(groupName, "The group " + groupName + " does not exist.");

        try {

            // Remove the group.
            dirContext.destroySubcontext(getGroupRDN(groupName));
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
     * @throws  AccountManagerException   if unable to remove the members from the group.
     * @throws  IllegalArgumentException  if the group name and/or one or more of the account names is invalid.
     * @throws  NoSuchEntryException      if the group does not exist.
     */
    @Override
    public void removeMembers(String groupName, Set<String> accountNames) {

        checkEmpty(groupName, "The group name is invalid.");
        checkEmpty(accountNames, "The account names is invalid.");
        checkNoSuchGroup(groupName, "The group " + groupName + " does not exist.");
        checkNoSuchAccount(accountNames, "One or more of the accounts " + accountNames + " do not exist.");

        try {

            // Declare.
            Attributes attributes;

            // Set the attributes.
            attributes = new BasicAttributes();
            attributes.put(getUniqueMemberAttribute(getUniqueMemberValues(accountNames)));

            // Add members to the group.
            dirContext.modifyAttributes(getGroupRDN(groupName), DirContext.REMOVE_ATTRIBUTE, attributes);
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
     * @throws  AccountManagerException   if unable to unlock the account.
     * @throws  IllegalArgumentException  if the account name is invalid.
     * @throws  NoSuchEntryException      if the account does not exist.
     * 
     */
    @Override
    public void unlockAccount(String accountName) {

        checkEmpty(accountName, "The account name is invalid.");
        checkNoSuchAccount(accountName, "The account " + accountName + " does not exist.");
        
        try {

            // Declare.
            Attributes attributes;

            // Set the attributes.
            attributes = new BasicAttributes();
            attributes.put(new BasicAttribute("pwdAccountLockedTime", ACCOUNT_LOCK_TIME));

            // Unlock the account.
            dirContext.modifyAttributes(getAccountRDN(accountName), DirContext.REMOVE_ATTRIBUTE, attributes);
        }
        catch(NamingException e) {
            throw new AccountManagerException(
                    "Unable to unlock the account " + accountName + ".", e);
        }
    }
}
