/*create table
  public.fuqua_acronym_tags (
    id INTEGER UNIQUE GENERATED ALWAYS AS IDENTITY,
    name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL default TRUE,
    created_by VARCHAR(255) NOT NULL,
    created timestamp NOT NULL default NOW(),
    last_updated_by VARCHAR(255) NULL,
    last_updated TIMESTAMP NULL 
  );
*/