package org.lazydog.entry.internal.account.manager;

import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lazydog.entry.spi.account.manager.EntryAccountManager;
import org.lazydog.entry.spi.account.manager.EntryAlreadyExistsException;
import org.lazydog.entry.spi.account.manager.NoSuchEntryException;


/**
 * Unit tests for EntryAccountManagerImpl class.
 *
 * @author  Ron Rickard
 */
public class EntryAccountManagerImplTest {

    private static EntryAccountManager accountManager;

    @BeforeClass
    public static void initialize() {

        // Get the Entry account manager.
        accountManager = new EntryAccountManagerWrapper();
    }

    @AfterClass
    public static void destroy() {

        try {
            accountManager.removeAccount("testaccount1");
        }
        catch(NoSuchEntryException e) {}

        try {
            accountManager.removeAccount("testaccount2");
        }
        catch(NoSuchEntryException e) {}

        try {
            accountManager.removeGroup("testgroup1");
        }
        catch(NoSuchEntryException e) {}

        try {
            accountManager.removeGroup("testgroup2");
        }
        catch(NoSuchEntryException e) {}
    }

    @Before
    public void beforeTest() {

        try {
            accountManager.removeAccount("testaccount1");
        }
        catch(NoSuchEntryException e) {}

        try {
            accountManager.removeAccount("testaccount2");
        }
        catch(NoSuchEntryException e) {}

        try {
            accountManager.removeGroup("testgroup1");
        }
        catch(NoSuchEntryException e) {}

        try {
            accountManager.removeGroup("testgroup2");
        }
        catch(NoSuchEntryException e) {}
    }

    @Test
    public void accountExists() {
        accountManager.createAccount("testaccount1", "test123");
        assertTrue(accountManager.accountExists("testaccount1"));
    }

    @Test
    public void accountExistsNot() {
        assertFalse(accountManager.accountExists("testaccount1"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void accountExistsEmptyAccountName() {
        accountManager.accountExists("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void accountExistsNullAccountName() {
        accountManager.accountExists(null);
    }

    @Test
    public void addMembers() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createAccount("testaccount2", "test123");
        accountManager.createGroup("testgroup1");
        accountManager.addMembers("testgroup1", accountNames);
        assertEquals(accountNames, accountManager.getMembers("testgroup1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void addMembersNoSuchGroup() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createAccount("testaccount2", "test123");
        assertFalse(accountManager.groupExists("testgroup1"));
        accountManager.addMembers("testgroup1", accountNames);
    }

    @Test(expected=IllegalArgumentException.class)
    public void addMembersEmptyGroupName() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createAccount("testaccount2", "test123");
        assertFalse(accountManager.groupExists("testgroup1"));
        accountManager.addMembers("", accountNames);
    }

    @Test(expected=IllegalArgumentException.class)
    public void addMembersNullGroupName() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createAccount("testaccount2", "test123");
        assertFalse(accountManager.groupExists("testgroup1"));
        accountManager.addMembers(null, accountNames);
    }

    @Test(expected=NoSuchEntryException.class)
    public void addMembersNoSuchAccount() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        accountManager.createGroup("testgroup1");
        accountManager.addMembers("testgroup1", accountNames);
    }

    @Test(expected=IllegalArgumentException.class)
    public void addMembersEmptyAccountName() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");
        accountNames.add("");

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createAccount("testaccount2", "test123");
        accountManager.createGroup("testgroup1");
        accountManager.addMembers("testgroup1", accountNames);
    }

    @Test(expected=IllegalArgumentException.class)
    public void addMembersNullAccountName() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");
        accountNames.add(null);

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createAccount("testaccount2", "test123");
        accountManager.createGroup("testgroup1");
        accountManager.addMembers("testgroup1", accountNames);
    }

    @Test(expected=IllegalArgumentException.class)
    public void addMemberEmptyAccountNames() {
        accountManager.createGroup("testgroup1");
        accountManager.addMembers("testgroup1", new HashSet<String>());
    }

    @Test(expected=IllegalArgumentException.class)
    public void addMembersNullAccountNames() {
        accountManager.createGroup("testgroup1");
        accountManager.addMembers("testgroup1", null);
    }

    @Test
    public void changePassword() {
        accountManager.createAccount("testaccount1", "test123");
        assertTrue(accountManager.isPassword("testaccount1", "test123"));
        accountManager.changePassword("testaccount1", "123test");
        assertTrue(accountManager.isPassword("testaccount1", "123test"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void changePasswordNoSuchAccount() {
        accountManager.changePassword("testaccount1", "test123");
    }

    @Test(expected=IllegalArgumentException.class)
    public void changePasswordEmptyAccountName() {
        accountManager.changePassword("", "test123");
    }

    @Test(expected=IllegalArgumentException.class)
    public void changePasswordNullAccountName() {
        accountManager.changePassword(null, "test123");
    }

    @Test(expected=IllegalArgumentException.class)
    public void changePasswordEmptyPassword() {
        accountManager.createAccount("testaccount1", "test123");
        accountManager.changePassword("testaccount1", "");
    }

    @Test(expected=IllegalArgumentException.class)
    public void changePasswordNullPassword() {
        accountManager.createAccount("testaccount1", "test123");
        accountManager.changePassword("testaccount1", null);
    }

    @Test
    public void createAccount() {
        accountManager.createAccount("testaccount1", "test123");
        assertTrue(accountManager.accountExists("testaccount1"));
    }

    @Test(expected=EntryAlreadyExistsException.class)
    public void createAccountAlreadyExists() {
        accountManager.createAccount("testaccount1", "test123");
        assertTrue(accountManager.accountExists("testaccount1"));
        accountManager.createAccount("testaccount1", "test123");
    }

    @Test(expected=IllegalArgumentException.class)
    public void createAccountEmptyAccountName() {
        accountManager.createAccount("", "test123");
    }

    @Test(expected=IllegalArgumentException.class)
    public void createAccountNullAccountName() {
        accountManager.createAccount(null, "test123");
    }

    @Test(expected=IllegalArgumentException.class)
    public void createAccountEmptyPassword() {
        accountManager.createAccount("testaccount1", "");
    }

    @Test(expected=IllegalArgumentException.class)
    public void createAccountNullPassword() {
        accountManager.createAccount("testaccount1", null);
    }

    @Test
    public void createGroup() {
        accountManager.createGroup("testgroup1");
        assertTrue(accountManager.groupExists("testgroup1"));
    }

    @Test(expected=EntryAlreadyExistsException.class)
    public void createGroupAlreadyExists() {
        accountManager.createGroup("testgroup1");
        assertTrue(accountManager.groupExists("testgroup1"));
        accountManager.createGroup("testgroup1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void createGroupEmptyGroupName() {
        accountManager.createGroup("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void createGroupNullGroupName() {
        accountManager.createGroup(null);
    }

    @Test
    public void getGroups() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        Set<String> groupNames = new HashSet<String>();
        groupNames.add("testgroup1");
        groupNames.add("testgroup2");

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createGroup("testgroup1");
        accountManager.createGroup("testgroup2");
        accountManager.addMembers("testgroup1", accountNames);
        accountManager.addMembers("testgroup2", accountNames);
        assertEquals(groupNames, accountManager.getGroups("testaccount1"));
    }

    @Test
    public void getGroupsNoGroup() {
        accountManager.createAccount("testaccount1", "test123");
        assertEquals(new HashSet<String>(), accountManager.getGroups("testaccount1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void getGroupsNoSuchAccount() {
        accountManager.getGroups("testaccount1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void getGroupsEmptyAccountName() {
        accountManager.getGroups("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void getGroupsNullAccountName() {
        accountManager.getGroups(null);
    }
    
    @Test
    public void getMembers() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createAccount("testaccount2", "test123");
        accountManager.createGroup("testgroup1");
        accountManager.addMembers("testgroup1", accountNames);
        assertEquals(accountNames, accountManager.getMembers("testgroup1"));
    }

    @Test
    public void getMembersNoMember() {
        accountManager.createGroup("testgroup1");
        assertEquals(new HashSet<String>(), accountManager.getMembers("testgroup1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void getMembersNoSuchGroup() {
        accountManager.getMembers("testgroup1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void getMembersEmptyGroupName() {
        accountManager.getMembers("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void getMembersNullGroupName() {
        accountManager.getMembers(null);
    }

    @Test
    public void groupExists() {
        accountManager.createGroup("testgroup1");
        assertTrue(accountManager.groupExists("testgroup1"));
    }

    @Test
    public void groupExistsNot() {
        assertFalse(accountManager.groupExists("testgroup1"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void groupExistsEmptyGroupName() {
        accountManager.groupExists("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void groupExistsNullGroupName() {
        accountManager.groupExists(null);
    }

    @Test
    public void isAccountLocked() {
        accountManager.createAccount("testaccount1", "test123");
        accountManager.lockAccount("testaccount1");
        assertTrue(accountManager.isAccountLocked("testaccount1"));
    }

    @Test
    public void isAccountLockedNot() {
        accountManager.createAccount("testaccount1", "test123");
        assertFalse(accountManager.isAccountLocked("testaccount1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void isAccountLockedNoSuchAccount() {
        accountManager.isAccountLocked("testaccount1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void isAccountLockedEmptyAccountName() {
        accountManager.isAccountLocked("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void isAccountLockedNullAccountName() {
        accountManager.isAccountLocked(null);
    }
    
    @Test
    public void isMember() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createGroup("testgroup1");
        accountManager.addMembers("testgroup1", accountNames);
        assertTrue(accountManager.isMember("testaccount1", "testgroup1"));
    }

    @Test
    public void isMemberNot() {
        accountManager.createAccount("testaccount1", "test123");
        accountManager.createGroup("testgroup1");
        assertFalse(accountManager.isMember("testaccount1", "testgroup1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void isMemberNoSuchAccount() {
        accountManager.createGroup("testgroup1");
        accountManager.isMember("testaccount1", "testgroup1");
    }

    @Test(expected=NoSuchEntryException.class)
    public void isMemberNoSuchGroup() {
        accountManager.createAccount("testaccount1", "test123");
        accountManager.isMember("testaccount1", "testgroup1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void isMemberEmptyAccountName() {
        accountManager.createGroup("testgroup1");
        accountManager.isMember("", "testgroup1");
    }
 
    @Test(expected=IllegalArgumentException.class)
    public void isMemberNullAccountName() {
        accountManager.createGroup("testgroup1");
        accountManager.isMember(null, "testgroup1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void isMemberEmptyGroupName() {
        accountManager.createAccount("testaccount1", "test123");
        accountManager.isMember("testaccount1", "");
    }

    @Test(expected=IllegalArgumentException.class)
    public void isMemberNullGroupName() {
        accountManager.createAccount("testaccount1", "test123");
        accountManager.isMember("testaccount1", null);
    }

    @Test
    public void isPassword() {
        accountManager.createAccount("testaccount1", "test123");
        assertTrue(accountManager.isPassword("testaccount1", "test123"));
    }

    @Test
    public void isPasswordNot() {
        accountManager.createAccount("testaccount1", "test123");
        assertFalse(accountManager.isPassword("testaccount1", "123test"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void isPasswordNoSuchAccount() {
        accountManager.isPassword("testaccount1", "test123");
    }

    @Test(expected=IllegalArgumentException.class)
    public void isPasswordEmptyAccountName() {
        accountManager.isPassword("", "test123");
    }

    @Test(expected=IllegalArgumentException.class)
    public void isPasswordNullAccountName() {
        accountManager.isPassword(null, "test123");
    }

    @Test(expected=IllegalArgumentException.class)
    public void isPaswordEmptyPassword() {
        accountManager.createAccount("testaccount1", "test123");
        accountManager.isPassword("testaccount1", "");
    }

    @Test(expected=IllegalArgumentException.class)
    public void isPaswordNullPassword() {
        accountManager.createAccount("testaccount1", "test123");
        accountManager.isPassword("testaccount1", null);
    }
    
    @Test
    public void lockAccount() {
        accountManager.createAccount("testaccount1", "test123");
        accountManager.lockAccount("testaccount1");
        assertTrue(accountManager.isAccountLocked("testaccount1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void lockAccountNoSuchAccount() {
        accountManager.lockAccount("testaccount1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void lockAccountEmptyAccountName() {
        accountManager.lockAccount("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void lockAccountNullAccountName() {
        accountManager.lockAccount(null);
    }

    @Test
    public void removeAccount() {
        accountManager.createAccount("testaccount1", "test123");
        accountManager.removeAccount("testaccount1");
        assertFalse(accountManager.accountExists("testaccount1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void removeAccountNoSuchAccount() {
        assertFalse(accountManager.accountExists("testaccount1"));
        accountManager.removeAccount("testaccount1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void removeAccountEmptyAccountName() {
        accountManager.removeAccount("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void removeAccountNullAccountName() {
        accountManager.removeAccount(null);
    }

    @Test
    public void removeGroup() {
        accountManager.createGroup("testgroup1");
        accountManager.removeGroup("testgroup1");
        assertFalse(accountManager.groupExists("testgroup1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void removeGroupNoSuchGroup() {
        assertFalse(accountManager.groupExists("testgroup1"));
        accountManager.removeGroup("testgroup1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void removeGroupEmptyGroupName() {
        accountManager.removeGroup("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void removeGroupNullGroupName() {
        accountManager.removeGroup(null);
    }

    @Test
    public void removeMembers() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createAccount("testaccount2", "test123");
        accountManager.createGroup("testgroup1");
        accountManager.addMembers("testgroup1", accountNames);
        assertEquals(accountNames, accountManager.getMembers("testgroup1"));
        accountManager.removeMembers("testgroup1", accountNames);
        assertEquals(new HashSet<String>(), accountManager.getMembers("testgroup1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void removeMembersNoSuchGroup() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createAccount("testaccount2", "test123");
        assertFalse(accountManager.groupExists("testgroup1"));
        accountManager.removeMembers("testgroup1", accountNames);
    }

    @Test(expected=IllegalArgumentException.class)
    public void removeMembersEmptyGroupName() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createAccount("testaccount2", "test123");
        accountManager.removeMembers("", accountNames);
    }

    @Test(expected=IllegalArgumentException.class)
    public void removeMembersNullGroupName() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createAccount("testaccount2", "test123");
        accountManager.removeMembers(null, accountNames);
    }

    @Test(expected=IllegalArgumentException.class)
    public void removeMembersEmptyAccountName() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createAccount("testaccount2", "test123");
        accountManager.createGroup("testgroup1");
        accountManager.addMembers("testgroup1", accountNames);
        assertEquals(accountNames, accountManager.getMembers("testgroup1"));
        accountNames.add("");
        accountManager.removeMembers("testgroup1", accountNames);
    }

    @Test(expected=IllegalArgumentException.class)
    public void removeMembersNullAccountName() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createAccount("testaccount2", "test123");
        accountManager.createGroup("testgroup1");
        accountManager.addMembers("testgroup1", accountNames);
        assertEquals(accountNames, accountManager.getMembers("testgroup1"));
        accountNames.add(null);
        accountManager.removeMembers("testgroup1", accountNames);
    }

    @Test(expected=IllegalArgumentException.class)
    public void removeMemberEmptyAccountNames() {
        accountManager.createGroup("testgroup1");
        accountManager.removeMembers("testgroup1", new HashSet<String>());
    }

    @Test(expected=IllegalArgumentException.class)
    public void removeMembersNullAccountNames() {
        Set<String> accountNames = new HashSet<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createAccount("testaccount2", "test123");
        accountManager.createGroup("testgroup1");
        accountManager.addMembers("testgroup1", accountNames);
        assertEquals(accountNames, accountManager.getMembers("testgroup1"));
        accountManager.removeMembers("testgroup1", null);
    }

    @Test
    public void unlockAccount() {
        accountManager.createAccount("testaccount1", "test123");
        accountManager.lockAccount("testaccount1");
        assertTrue(accountManager.isAccountLocked("testaccount1"));
        accountManager.unlockAccount("testaccount1");
        assertFalse(accountManager.isAccountLocked("testaccount1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void unlockAccountNoSuchAccount() {
        accountManager.unlockAccount("testaccount1");
    }

    @Test(expected=IllegalArgumentException.class)
    public void unlockAccountEmptyAccountName() {
        accountManager.unlockAccount("");
    }

    @Test(expected=IllegalArgumentException.class)
    public void unlockAccountNullAccountName() {
        accountManager.unlockAccount(null);
    }
}
