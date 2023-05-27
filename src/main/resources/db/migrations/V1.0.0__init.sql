create table file
(
    created   timestamp(6) with time zone,
    id        bigint generated by default as identity,
    modified  timestamp(6) with time zone,
    name      varchar(255),
    directory clob,
    path      clob,
    primary key (id)
);

create table video
(
    created  timestamp(6) with time zone,
    file_id  bigint unique,
    id       bigint generated by default as identity,
    modified timestamp(6) with time zone,
    name     varchar(255),
    primary key (id)
);

create index IDX_VIDEO__NAME
    on video (name);

alter table video
    add constraint FK_VIDEO__FILE_ID
    foreign key (file_id)
    references file;
