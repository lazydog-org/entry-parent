alter table entry.user_profile
    drop key user_profile__username__uk,
    drop key user_profile__uuid__uk;