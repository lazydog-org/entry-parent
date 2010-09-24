create table entry.application_server_profile
	(id                             int(10) unsigned not null auto_increment,
	 application_server_id          varchar(255) not null,
         jmx_host                       varchar(255) not null,
         jmx_port                       int(5) unsigned not null,
         jmx_login                      varchar(255) not null,
         jmx_password                   varchar(255) not null,
         create_time                    datetime not null,
	 modify_time                    datetime,
         primary key (id)
	) type = innodb;
