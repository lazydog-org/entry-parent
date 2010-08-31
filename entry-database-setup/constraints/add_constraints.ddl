alter table entry.application_user
    add constraint application_user__username__uk 
            unique (username),
    add constraint application_user__uuid__uk 
            unique (uuid);