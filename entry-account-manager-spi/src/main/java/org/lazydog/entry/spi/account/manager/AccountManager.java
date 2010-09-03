package org.lazydog.entry.spi.account.manager;

import java.util.Set;


/**
 * Account manager.
 *
 * @author  Ron Rickard
 */
public interface AccountManager {

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
    public boolean accountExists(String accountName);

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
    public void addMembers(String groupName, Set<String> accountNames);

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
    public void changePassword(String accountName, String password);

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
    public void createAccount(String accountName, String password);

    /**
     * Create the group.
     *
     * @param  groupName  the group name.
     *
     * @throws  AccountManagerException      if unable to create the group.
     * @throws  EntryAlreadyExistsException  if the group already exists.
     * @throws  IllegalArgumentException     if the group name is invalid.
     */
    public void createGroup(String groupName);

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
    public Set<String> getGroups(String accountName);

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
    public Set<String> getMembers(String groupName);

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
    public boolean groupExists(String groupName);

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
    public boolean isAccountLocked(String accountName);

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
    public boolean isMember(String accountName, String groupName);

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
    public boolean isPassword(String accountName, String password);

    /**
     * Lock the account.
     *
     * @param  accountName  the account name.
     *
     * @throws  AccountManagerException   if unable to lock the account.
     * @throws  IllegalArgumentException  if the account name is invalid.
     * @throws  NoSuchEntryException      if the account does not exist.
     */
    public void lockAccount(String accountName);

    /**
     * Remove the account.
     *
     * @param  accountName  the account name.
     *
     * @throws  AccountManagerException   if unable to remove the account.
     * @throws  IllegalArgumentException  if the account name is invalid.
     * @throws  NoSuchEntryException      if the account does not exist.
     */
    public void removeAccount(String accountName);

    /**
     * Remove the group.
     *
     * @param  groupName  the group name.
     *
     * @throws  AccountManagerException   if unable to remove the group.
     * @throws  IllegalArgumentException  if the group name is invalid.
     * @throws  NoSuchEntryException      if the group does not exist.
     */
    public void removeGroup(String groupName);

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
    public void removeMembers(String groupName, Set<String> accountNames);

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
    public void unlockAccount(String accountName);
}
