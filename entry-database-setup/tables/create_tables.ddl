create table entry.application_user
	(id			int(10) unsigned not null auto_increment,
	 username		varchar(50) not null,
         last_name              varchar(50) not null,
         first_name             varchar(50) not null,
         email_address          varchar(50) not null,
	 create_time		datetime not null,
	 modify_time		datetime,
	 primary key (id),
	 unique key (username)
	) type = innodb;
