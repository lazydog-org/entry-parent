alter table entry.application_server_profile
    add constraint application_server_profile__application_server_id__uk
            unique (application_server_id);