create table
  public.fuqua_acronym_permissions (
    id INTEGER UNIQUE GENERATED ALWAYS AS IDENTITY,
    duke_id VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL default TRUE,
    created_by VARCHAR(255) NOT NULL,
    created timestamp NOT NULL default NOW(),
    last_updated_by VARCHAR(255) NOT NULL,
    last_updated TIMESTAMP NOT NULL default NOW()
  );
