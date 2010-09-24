create table entry.application_profile
	(id                             int(10) unsigned not null auto_increment,
	 application_id                 varchar(255) not null,
         application_server_profile_id  int(10) unsigned not null,
         authentication_module_id       int(10) unsigned not null,
         default_group_name             varchar(255),
         registration_url               varchar(255),
         create_time                    datetime not null,
	 modify_time                    datetime,
         primary key (id)
	) type = innodb;
