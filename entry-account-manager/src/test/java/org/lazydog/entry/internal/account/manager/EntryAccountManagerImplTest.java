package org.lazydog.entry.internal.account.manager;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lazydog.entry.spi.account.manager.EntryAccountManager;
import org.lazydog.entry.spi.account.manager.EntryAccountManagerFactory;
import org.lazydog.entry.spi.account.manager.EntryAlreadyExistsException;
import org.lazydog.entry.spi.account.manager.NoSuchEntryException;


/**
 * Unit tests for EntryAccountManagerImpl class.
 *
 * @author  Ron Rickard
 */
public class EntryAccountManagerImplTest {

    private static EntryAccountManager entryAccountManager;

    @BeforeClass
    public static void initialize() throws Exception {

        // Declare.
        Properties environment;

        // Set the environment.
        environment = new Properties();
        environment.put(EntryAccountManager.PROVIDER_URL, "ldap://localhost:389/dc=lazydog,dc=org ldap://ldap1:389/dc=lazydog,dc=org ldap://ldap2:389/dc=lazydog,dc=org");
        environment.put(EntryAccountManager.SECURITY_CREDENTIALS, "@dm1n");
        environment.put(EntryAccountManager.SECURITY_PRINCIPAL, "cn=admin,dc=lazydog,dc=org");

        // Get the entry account manager.
        entryAccountManager = EntryAccountManagerFactory.instance().createEntryAccountManager(environment);
    }

    @AfterClass
    public static void destroy() {

        try {
            entryAccountManager.removeAccount("testaccount1");
        }
        catch(NoSuchEntryException e) {}

        try {
            entryAccountManager.removeAccount("testaccount2");
        }
        catch(NoSuchEntryException e) {}

        try {
            entryAccountManager.removeGroup("testgroup1");
        }
        catch(NoSuchEntryException e) {}

        try {
            entryAccountManager.removeGroup("testgroup2");
        }
        catch(NoSuchEntryException e) {}
    }

    @Before
    public void beforeTest() {

        try {
            entryAccountManager.removeAccount("testaccount1");
        }
        catch(NoSuchEntryException e) {}

        try {
            entryAccountManager.removeAccount("testaccount2");
        }
        catch(NoSuchEntryException e) {}

        try {
            entryAccountManager.removeGroup("testgroup1");
        }
        catch(NoSuchEntryException e) {}

        try {
            entryAccountManager.removeGroup("testgroup2");
        }
        catch(NoSuchEntryException e) {}
    }

    @Test
    public void accountExists() {
        entryAccountManager.createAccount("testaccount1", "test123");
        assertTrue(entryAccountManager.accountExists("testaccount1"));
    }

    @Test
    public void accountExistsNot() {
        assertFalse(entryAccountManager.accountExists("testaccount1"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void accountExistsEmptyAccountName() {
        entryAccountManager.accountExists("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void accountExistsNullAccountName() {
        entryAccountManager.accountExists(null);
    }

    @Test
    public void addMembers() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.createAccount("testaccount2", "test123");
        entryAccountManager.createGroup("testgroup1");
        entryAccountManager.addMembers("testgroup1", accountNames);
        assertEquals(accountNames, entryAccountManager.getMembers("testgroup1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void addMembersNoSuchGroup() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.createAccount("testaccount2", "test123");
        assertFalse(entryAccountManager.groupExists("testgroup1"));
        entryAccountManager.addMembers("testgroup1", accountNames);
    }

    @Test(expected=IllegalArgumentException.class)
    public void addMembersEmptyGroupName() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.createAccount("testaccount2", "test123");
        assertFalse(entryAccountManager.groupExists("testgroup1"));
        entryAccountManager.addMembers("", accountNames);
    }

    @Test(expected=IllegalArgumentException.class)
    public void addMembersNullGroupName() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.createAccount("testaccount2", "test123");
        assertFalse(entryAccountManager.groupExists("testgroup1"));
        entryAccountManager.addMembers(null, accountNames);
    }

    @Test(expected=NoSuchEntryException.class)
    public void addMembersNoSuchAccount() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        entryAccountManager.createGroup("testgroup1");
        entryAccountManager.addMembers("testgroup1", accountNames);
    }

    @Test(expected=IllegalArgumentException.class)
    public void addMembersEmptyAccountName() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");
        accountNames.add("");

        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.createAccount("testaccount2", "test123");
        entryAccountManager.createGroup("testgroup1");
        entryAccountManager.addMembers("testgroup1", accountNames);
    }

    @Test(expected=IllegalArgumentException.class)
    public void addMembersNullAccountName() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");
        accountNames.add(null);

        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.createAccount("testaccount2", "test123");
        entryAccountManager.createGroup("testgroup1");
        entryAccountManager.addMembers("testgroup1", accountNames);
    }

    @Test(expected=IllegalArgumentException.class)
    public void addMemberEmptyAccountNames() {
        entryAccountManager.createGroup("testgroup1");
        entryAccountManager.addMembers("testgroup1", new HashSet<String>());
    }

    @Test(expected=IllegalArgumentException.class)
    public void addMembersNullAccountNames() {
        entryAccountManager.createGroup("testgroup1");
        entryAccountManager.addMembers("testgroup1", null);
    }

    @Test
    public void changePassword() {
        entryAccountManager.createAccount("testaccount1", "test123");
        assertTrue(entryAccountManager.isPassword("testaccount1", "test123"));
        entryAccountManager.changePassword("testaccount1", "123test");
        assertTrue(entryAccountManager.isPassword("testaccount1", "123test"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void changePasswordNoSuchAccount() {
        entryAccountManager.changePassword("testaccount1", "test123");
    }

    @Test(expected=IllegalArgumentException.class)
    public void changePasswordEmptyAccountName() {
        entryAccountManager.changePassword("", "test123");
    }

    @Test(expected=IllegalArgumentException.class)
    public void changePasswordNullAccountName() {
        entryAccountManager.changePassword(null, "test123");
    }

    @Test(expected=IllegalArgumentException.class)
    public void changePasswordEmptyPassword() {
        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.changePassword("testaccount1", "");
    }

    @Test(expected=IllegalArgumentException.class)
    public void changePasswordNullPassword() {
        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.changePassword("testaccount1", null);
    }

    @Test
    public void createAccount() {
        entryAccountManager.createAccount("testaccount1", "test123");
        assertTrue(entryAccountManager.accountExists("testaccount1"));
    }

    @Test(expected=EntryAlreadyExistsException.class)
    public void createAccountAlreadyExists() {
        entryAccountManager.createAccount("testaccount1", "test123");
        assertTrue(entryAccountManager.accountExists("testaccount1"));
        entryAccountManager.createAccount("testaccount1", "test123");
    }

    @Test(expected=IllegalArgumentException.class)
    public void createAccountEmptyAccountName() {
        entryAccountManager.createAccount("", "test123");
    }

    @Test(expected=IllegalArgumentException.class)
    public void createAccountNullAccountName() {
        entryAccountManager.createAccount(null, "test123");
    }

    @Test(expected=IllegalArgumentException.class)
    public void createAccountEmptyPassword() {
        entryAccountManager.createAccount("testaccount1", "");
    }

    @Test(expected=IllegalArgumentException.class)
    public void createAccountNullPassword() {
        entryAccountManager.createAccount("testaccount1", null);
    }

    @Test
    public void createGroup() {
        entryAccountManager.createGroup("testgroup1");
        assertTrue(entryAccountManager.groupExists("testgroup1"));
    }

    @Test(expected=EntryAlreadyExistsException.class)
    public void createGroupAlreadyExists() {
        entryAccountManager.createGroup("testgroup1");
        assertTrue(entryAccountManager.groupExists("testgroup1"));
        entryAccountManager.createGroup("testgroup1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void createGroupEmptyGroupName() {
        entryAccountManager.createGroup("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void createGroupNullGroupName() {
        entryAccountManager.createGroup(null);
    }

    @Test
    public void getGroups() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        Set<String> groupNames = new HashSet<String>();
        groupNames.add("testgroup1");
        groupNames.add("testgroup2");

        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.createGroup("testgroup1");
        entryAccountManager.createGroup("testgroup2");
        entryAccountManager.addMembers("testgroup1", accountNames);
        entryAccountManager.addMembers("testgroup2", accountNames);
        assertEquals(groupNames, entryAccountManager.getGroups("testaccount1"));
    }

    @Test
    public void getGroupsNoGroup() {
        entryAccountManager.createAccount("testaccount1", "test123");
        assertEquals(new HashSet<String>(), entryAccountManager.getGroups("testaccount1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void getGroupsNoSuchAccount() {
        entryAccountManager.getGroups("testaccount1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void getGroupsEmptyAccountName() {
        entryAccountManager.getGroups("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void getGroupsNullAccountName() {
        entryAccountManager.getGroups(null);
    }
    
    @Test
    public void getMembers() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.createAccount("testaccount2", "test123");
        entryAccountManager.createGroup("testgroup1");
        entryAccountManager.addMembers("testgroup1", accountNames);
        assertEquals(accountNames, entryAccountManager.getMembers("testgroup1"));
    }

    @Test
    public void getMembersNoMember() {
        entryAccountManager.createGroup("testgroup1");
        assertEquals(new HashSet<String>(), entryAccountManager.getMembers("testgroup1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void getMembersNoSuchGroup() {
        entryAccountManager.getMembers("testgroup1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void getMembersEmptyGroupName() {
        entryAccountManager.getMembers("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void getMembersNullGroupName() {
        entryAccountManager.getMembers(null);
    }

    @Test
    public void groupExists() {
        entryAccountManager.createGroup("testgroup1");
        assertTrue(entryAccountManager.groupExists("testgroup1"));
    }

    @Test
    public void groupExistsNot() {
        assertFalse(entryAccountManager.groupExists("testgroup1"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void groupExistsEmptyGroupName() {
        entryAccountManager.groupExists("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void groupExistsNullGroupName() {
        entryAccountManager.groupExists(null);
    }

    @Test
    public void isAccountLocked() {
        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.lockAccount("testaccount1");
        assertTrue(entryAccountManager.isAccountLocked("testaccount1"));
    }

    @Test
    public void isAccountLockedNot() {
        entryAccountManager.createAccount("testaccount1", "test123");
        assertFalse(entryAccountManager.isAccountLocked("testaccount1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void isAccountLockedNoSuchAccount() {
        entryAccountManager.isAccountLocked("testaccount1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void isAccountLockedEmptyAccountName() {
        entryAccountManager.isAccountLocked("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void isAccountLockedNullAccountName() {
        entryAccountManager.isAccountLocked(null);
    }
    
    @Test
    public void isMember() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");

        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.createGroup("testgroup1");
        entryAccountManager.addMembers("testgroup1", accountNames);
        assertTrue(entryAccountManager.isMember("testaccount1", "testgroup1"));
    }

    @Test
    public void isMemberNot() {
        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.createGroup("testgroup1");
        assertFalse(entryAccountManager.isMember("testaccount1", "testgroup1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void isMemberNoSuchAccount() {
        entryAccountManager.createGroup("testgroup1");
        entryAccountManager.isMember("testaccount1", "testgroup1");
    }

    @Test(expected=NoSuchEntryException.class)
    public void isMemberNoSuchGroup() {
        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.isMember("testaccount1", "testgroup1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void isMemberEmptyAccountName() {
        entryAccountManager.createGroup("testgroup1");
        entryAccountManager.isMember("", "testgroup1");
    }
 
    @Test(expected=IllegalArgumentException.class)
    public void isMemberNullAccountName() {
        entryAccountManager.createGroup("testgroup1");
        entryAccountManager.isMember(null, "testgroup1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void isMemberEmptyGroupName() {
        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.isMember("testaccount1", "");
    }

    @Test(expected=IllegalArgumentException.class)
    public void isMemberNullGroupName() {
        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.isMember("testaccount1", null);
    }

    @Test
    public void isPassword() {
        entryAccountManager.createAccount("testaccount1", "test123");
        assertTrue(entryAccountManager.isPassword("testaccount1", "test123"));
    }

    @Test
    public void isPasswordNot() {
        entryAccountManager.createAccount("testaccount1", "test123");
        assertFalse(entryAccountManager.isPassword("testaccount1", "123test"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void isPasswordNoSuchAccount() {
        entryAccountManager.isPassword("testaccount1", "test123");
    }

    @Test(expected=IllegalArgumentException.class)
    public void isPasswordEmptyAccountName() {
        entryAccountManager.isPassword("", "test123");
    }

    @Test(expected=IllegalArgumentException.class)
    public void isPasswordNullAccountName() {
        entryAccountManager.isPassword(null, "test123");
    }

    @Test(expected=IllegalArgumentException.class)
    public void isPaswordEmptyPassword() {
        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.isPassword("testaccount1", "");
    }

    @Test(expected=IllegalArgumentException.class)
    public void isPaswordNullPassword() {
        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.isPassword("testaccount1", null);
    }
    
    @Test
    public void lockAccount() {
        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.lockAccount("testaccount1");
        assertTrue(entryAccountManager.isAccountLocked("testaccount1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void lockAccountNoSuchAccount() {
        entryAccountManager.lockAccount("testaccount1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void lockAccountEmptyAccountName() {
        entryAccountManager.lockAccount("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void lockAccountNullAccountName() {
        entryAccountManager.lockAccount(null);
    }

    @Test
    public void removeAccount() {
        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.removeAccount("testaccount1");
        assertFalse(entryAccountManager.accountExists("testaccount1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void removeAccountNoSuchAccount() {
        assertFalse(entryAccountManager.accountExists("testaccount1"));
        entryAccountManager.removeAccount("testaccount1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void removeAccountEmptyAccountName() {
        entryAccountManager.removeAccount("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void removeAccountNullAccountName() {
        entryAccountManager.removeAccount(null);
    }

    @Test
    public void removeGroup() {
        entryAccountManager.createGroup("testgroup1");
        entryAccountManager.removeGroup("testgroup1");
        assertFalse(entryAccountManager.groupExists("testgroup1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void removeGroupNoSuchGroup() {
        assertFalse(entryAccountManager.groupExists("testgroup1"));
        entryAccountManager.removeGroup("testgroup1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void removeGroupEmptyGroupName() {
        entryAccountManager.removeGroup("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void removeGroupNullGroupName() {
        entryAccountManager.removeGroup(null);
    }

    @Test
    public void removeMembers() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.createAccount("testaccount2", "test123");
        entryAccountManager.createGroup("testgroup1");
        entryAccountManager.addMembers("testgroup1", accountNames);
        assertEquals(accountNames, entryAccountManager.getMembers("testgroup1"));
        entryAccountManager.removeMembers("testgroup1", accountNames);
        assertEquals(new HashSet<String>(), entryAccountManager.getMembers("testgroup1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void removeMembersNoSuchGroup() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.createAccount("testaccount2", "test123");
        assertFalse(entryAccountManager.groupExists("testgroup1"));
        entryAccountManager.removeMembers("testgroup1", accountNames);
    }

    @Test(expected=IllegalArgumentException.class)
    public void removeMembersEmptyGroupName() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.createAccount("testaccount2", "test123");
        entryAccountManager.removeMembers("", accountNames);
    }

    @Test(expected=IllegalArgumentException.class)
    public void removeMembersNullGroupName() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.createAccount("testaccount2", "test123");
        entryAccountManager.removeMembers(null, accountNames);
    }

    @Test(expected=IllegalArgumentException.class)
    public void removeMembersEmptyAccountName() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.createAccount("testaccount2", "test123");
        entryAccountManager.createGroup("testgroup1");
        entryAccountManager.addMembers("testgroup1", accountNames);
        assertEquals(accountNames, entryAccountManager.getMembers("testgroup1"));
        accountNames.add("");
        entryAccountManager.removeMembers("testgroup1", accountNames);
    }

    @Test(expected=IllegalArgumentException.class)
    public void removeMembersNullAccountName() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.createAccount("testaccount2", "test123");
        entryAccountManager.createGroup("testgroup1");
        entryAccountManager.addMembers("testgroup1", accountNames);
        assertEquals(accountNames, entryAccountManager.getMembers("testgroup1"));
        accountNames.add(null);
        entryAccountManager.removeMembers("testgroup1", accountNames);
    }

    @Test(expected=IllegalArgumentException.class)
    public void removeMemberEmptyAccountNames() {
        entryAccountManager.createGroup("testgroup1");
        entryAccountManager.removeMembers("testgroup1", new HashSet<String>());
    }

    @Test(expected=IllegalArgumentException.class)
    public void removeMembersNullAccountNames() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.createAccount("testaccount2", "test123");
        entryAccountManager.createGroup("testgroup1");
        entryAccountManager.addMembers("testgroup1", accountNames);
        assertEquals(accountNames, entryAccountManager.getMembers("testgroup1"));
        entryAccountManager.removeMembers("testgroup1", null);
    }

    @Test
    public void unlockAccount() {
        entryAccountManager.createAccount("testaccount1", "test123");
        entryAccountManager.lockAccount("testaccount1");
        assertTrue(entryAccountManager.isAccountLocked("testaccount1"));
        entryAccountManager.unlockAccount("testaccount1");
        assertFalse(entryAccountManager.isAccountLocked("testaccount1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void unlockAccountNoSuchAccount() {
        entryAccountManager.unlockAccount("testaccount1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void unlockAccountEmptyAccountName() {
        entryAccountManager.unlockAccount("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void unlockAccountNullAccountName() {
        entryAccountManager.unlockAccount(null);
    }
}
