alter table entry.user_profile
    add constraint user_profile__username__uk
            unique (username),
    add constraint user_profile__uuid__uk
            unique (uuid);