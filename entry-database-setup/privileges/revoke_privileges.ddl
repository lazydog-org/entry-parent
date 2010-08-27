revoke alter,create,delete,drop,index,insert,select,update on entry.* from 'entryadmin';
revoke alter,create,delete,drop,index,insert,select,update on entry.* from 'entryadmin'@'localhost';
revoke delete,insert,select,update on entry.* from 'entryuser';
revoke delete,insert,select,update on entry.* from 'entryuser'@'localhost';
flush privileges;
