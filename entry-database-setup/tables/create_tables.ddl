create table entry.user_profile
	(id			int(10) unsigned not null auto_increment,
	 username		varchar(50) not null,
         uuid                   char(36) not null,
         activation_code        char(36),
         last_name              varchar(50) not null,
         first_name             varchar(50) not null,
         email_address          varchar(50) not null,
	 modify_time		datetime not null,
         register_time          datetime not null,
         primary key (id)
	) type = innodb;
