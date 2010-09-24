alter table entry.application_profile
    add constraint application_profile__application_id__uk
            unique (application_id),
    add index (application_server_profile_id),
    add constraint application_profile__application_server_profile_id__fk
            foreign key (application_server_profile_id)
            references application_server_profile (id),
    add index (authentication_module_id),
    add constraint application_profile__authentication_module_id__fk
            foreign key (authentication_module_id)
            references authentication_module (id);