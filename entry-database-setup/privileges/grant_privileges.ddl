grant alter,create,delete,drop,index,insert,select,update on entry.* to 'entryadmin';
grant alter,create,delete,drop,index,insert,select,update on entry.* to 'entryadmin'@'localhost';
grant delete,insert,select,update on entry.* to 'entryuser';
grant delete,insert,select,update on entry.* to 'entryuser'@'localhost';
flush privileges;