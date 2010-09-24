alter table entry.authentication_module
    add constraint authentication_module__class_name__uk
            unique (class_name);