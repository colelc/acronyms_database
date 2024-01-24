create table
  public.fuqua_acronym_tags (
    id INTEGER UNIQUE GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL default TRUE,
    created_by VARCHAR(255) NOT NULL,
    created TIMESTAMP NOT NULL default NOW(),
    deleted_by VARCHAR(255) NULL,
    deleted TIMESTAMP NULL
  );
