create type t_site_status as enum ('INDEXING', 'INDEXED', 'FAILED');

create table t_field
(
    id bigserial not null primary key,
    name varchar(255) not null,
    selector varchar(255) not null unique,
    weight numeric(3,2) not null
);

create table t_site
(
    id bigserial not null primary key,
    status t_site_status not null,
    status_time timestamp not null,
    last_error text,
    url varchar(255) not null unique,
    name varchar(255) not null
);

create index on t_site (url);

create table t_page
(
    id bigserial not null primary key,
    site_id bigint not null references t_site (id),
    path varchar(255) not null,
    code_response smallint not null,
    content text,
    constraint site_page_path unique (site_id, path)
);

create index on t_page (site_id);
create index on t_page (path);

create table t_lemma_frequency
(
    id bigserial not null primary key,
    lemma varchar(255) not null unique,
    frequency smallint not null
);

create table t_index
(
    id bigserial not null primary key,
    page_id bigint not null references t_page (id) on delete cascade,
    field_id bigint not null references t_field (id) on delete cascade,
    lemma_id bigint not null references t_lemma_frequency (id) on delete cascade,
    index_rank numeric(5,2) not null,
    constraint index_page_lemma_field unique (page_id, lemma_id, field_id)
);

create index on t_index (page_id, lemma_id);
