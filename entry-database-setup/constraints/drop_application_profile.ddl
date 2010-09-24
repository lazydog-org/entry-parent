alter table entry.application_profile
    drop key application_profile__application_id__uk,
    drop foreign key application_profile__application_server_profile_id__fk,
    drop foreign key application_profile__authentication_module_id__fk;