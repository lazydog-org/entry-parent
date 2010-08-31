alter table entry.application_user
    drop key application_user__username__uk,
    drop key application_user__uuid__uk;