create table
  public.fuqua_acronyms (
    id INTEGER UNIQUE GENERATED ALWAYS AS IDENTITY,
    acronym VARCHAR(255) NOT NULL,
    refers_to VARCHAR(4096) NOT NUlL,
    definition VARCHAR(65535) NOT NULL,
    area_key VARCHAR(4096) NOT NULL,
    active BOOLEAN NOT NULL default TRUE,
    created_by VARCHAR(255) NOT NULL,
    created timestamp NOT NULL default NOW(),
    deleted_by VARCHAR(255) NULL,
    deleted TIMESTAMP NULL
  );
