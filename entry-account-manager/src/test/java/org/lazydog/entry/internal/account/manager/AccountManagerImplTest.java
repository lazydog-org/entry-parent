package org.lazydog.entry.internal.account.manager;

import java.util.Properties;
import java.util.ArrayList;
import java.util.List;
import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import static org.junit.Assert.*;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lazydog.entry.account.manager.AccountManagerException;
import org.lazydog.entry.account.manager.EntryAlreadyExistsException;
import org.lazydog.entry.account.manager.NoSuchEntryException;


/**
 * Unit tests for AccountManagerImpl class.
 *
 * @author  Ron Rickard
 */
public class AccountManagerImplTest {

    private static AccountManagerImpl accountManager;

    @BeforeClass
    public static void initialize() throws Exception {

        // Declare.
        DirContext dirContext;
        Properties env;

        // Set the directory context.
        env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_CREDENTIALS, "@dm1n");
        env.put(Context.SECURITY_PRINCIPAL, "cn=admin,dc=lazydog,dc=org");
        env.put(Context.PROVIDER_URL, "ldap://localhost/dc=lazydog,dc=org ldap://ldap1/dc=lazydog,dc=org ldap://ldap2/dc=lazydog,dc=org");
        dirContext = new InitialDirContext(env);

        // Get the account manager.
        accountManager = new AccountManagerImpl();
        accountManager.setDirContext(dirContext);
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

    @Test(expected=NullPointerException.class)
    public void accountExistsNullAccountName() {
        accountManager.accountExists(null);
    }

    @Test
    public void addMembers() {
        List<String> accountNames = new ArrayList<String>();
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
        List<String> accountNames = new ArrayList<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createAccount("testaccount2", "test123");
        assertFalse(accountManager.groupExists("testgroup1"));
        accountManager.addMembers("testgroup1", accountNames);
    }

    @Test(expected=NullPointerException.class)
    public void addMembersNullGroupName() {
        List<String> accountNames = new ArrayList<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createAccount("testaccount2", "test123");
        assertFalse(accountManager.groupExists("testgroup1"));
        accountManager.addMembers(null, accountNames);
    }

    @Test(expected=NoSuchEntryException.class)
    public void addMembersNoSuchAccount() {
        List<String> accountNames = new ArrayList<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        accountManager.createGroup("testgroup1");
        accountManager.addMembers("testgroup1", accountNames);
    }

    @Test(expected=NullPointerException.class)
    public void addMembersNullAccountName() {
        List<String> accountNames = new ArrayList<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");
        accountNames.add(null);

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createAccount("testaccount2", "test123");
        accountManager.createGroup("testgroup1");
        accountManager.addMembers("testgroup1", accountNames);
    }

    @Test(expected=NullPointerException.class)
    public void addMembersNullAccountNames() {
        accountManager.createGroup("testgroup1");
        accountManager.addMembers("testgroup1", null);
    }

    @Test(expected=AccountManagerException.class)
    public void addMemberEmptyAccountNames() {
        accountManager.createGroup("testgroup1");
        accountManager.addMembers("testgroup1", new ArrayList<String>());
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

    @Test(expected=NullPointerException.class)
    public void createAccountNullAccountName() {
        accountManager.createAccount(null, "test123");
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

    @Test(expected=NullPointerException.class)
    public void createGroupNullGroupName() {
        accountManager.createGroup(null);
    }

    @Test
    public void getGroups() {
        List<String> accountNames = new ArrayList<String>();
        accountNames.add("testaccount1");
        List<String> groupNames = new ArrayList<String>();
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
        assertEquals(new ArrayList<String>(), accountManager.getGroups("testaccount1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void getGroupsNoSuchAccount() {
        accountManager.getGroups("testaccount1");
    }

    @Test(expected=NullPointerException.class)
    public void getGroupsNullAccountName() {
        accountManager.getGroups(null);
    }
    
    @Test
    public void getMembers() {
        List<String> accountNames = new ArrayList<String>();
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
        assertEquals(new ArrayList<String>(), accountManager.getMembers("testgroup1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void getMembersNoSuchGroup() {
        accountManager.getMembers("testgroup1");
    }

    @Test(expected=NullPointerException.class)
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

    @Test(expected=NullPointerException.class)
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

    @Test(expected=NullPointerException.class)
    public void isAccountLockedNullAccountName() {
        accountManager.isAccountLocked(null);
    }
    
    @Test
    public void isMember() {
        List<String> accountNames = new ArrayList<String>();
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
    
    @Test(expected=NullPointerException.class)
    public void isMemberNullAccountName() {
        accountManager.createGroup("testgroup1");
        accountManager.isMember(null, "testgroup1");
    }

    @Test(expected=NullPointerException.class)
    public void isMemberNullGroupName() {
        accountManager.createAccount("testaccount1", "test123");
        accountManager.isMember("testaccount1", null);
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

    @Test(expected=NullPointerException.class)
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

    @Test(expected=NullPointerException.class)
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

    @Test(expected=NullPointerException.class)
    public void removeGroupNullGroupName() {
        accountManager.removeGroup(null);
    }

    @Test
    public void removeMembers() {
        List<String> accountNames = new ArrayList<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createAccount("testaccount2", "test123");
        accountManager.createGroup("testgroup1");
        accountManager.addMembers("testgroup1", accountNames);
        assertEquals(accountNames, accountManager.getMembers("testgroup1"));
        accountManager.removeMembers("testgroup1", accountNames);
        assertEquals(new ArrayList<String>(), accountManager.getMembers("testgroup1"));
    }

    @Test(expected=NoSuchEntryException.class)
    public void removeMembersNoSuchGroup() {
        List<String> accountNames = new ArrayList<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createAccount("testaccount2", "test123");
        assertFalse(accountManager.groupExists("testgroup1"));
        accountManager.removeMembers("testgroup1", accountNames);
    }

    @Test(expected=NullPointerException.class)
    public void removeMembersNullGroupName() {
        List<String> accountNames = new ArrayList<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createAccount("testaccount2", "test123");
        accountManager.removeMembers(null, accountNames);
    }

    @Test(expected=NullPointerException.class)
    public void removeMembersNullAccountName() {
        List<String> accountNames = new ArrayList<String>();
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

    @Test(expected=NullPointerException.class)
    public void removeMembersNullAccountNames() {
        List<String> accountNames = new ArrayList<String>();
        accountNames.add("testaccount1");
        accountNames.add("testaccount2");

        accountManager.createAccount("testaccount1", "test123");
        accountManager.createAccount("testaccount2", "test123");
        accountManager.createGroup("testgroup1");
        accountManager.addMembers("testgroup1", accountNames);
        assertEquals(accountNames, accountManager.getMembers("testgroup1"));
        accountManager.removeMembers("testgroup1", null);
    }

    @Test(expected=AccountManagerException.class)
    public void removeMemberEmptyAccountNames() {
        accountManager.createGroup("testgroup1");
        accountManager.removeMembers("testgroup1", new ArrayList<String>());
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

    @Test(expected=NullPointerException.class)
    public void unlockAccountNullAccountName() {
        accountManager.unlockAccount(null);
    }
}
