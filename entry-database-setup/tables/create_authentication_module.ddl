create table entry.authentication_module
	(id			int(10) unsigned not null auto_increment,
	 class_name             varchar(255) not null,
         create_time            datetime not null,
	 modify_time		datetime,
         primary key (id)
	) type = innodb;
