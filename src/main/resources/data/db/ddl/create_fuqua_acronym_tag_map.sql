create table
  public.fuqua_acronym_tag_map (
    id INTEGER   UNIQUE   GENERATED ALWAYS AS IDENTITY,
    acronym_id INTEGER NOT NULL,
    tag_id INTEGER NOT NULL,
    active BOOLEAN NOT NULL default TRUE,
    created_by VARCHAR(255) NOT NULL,
    created timestamp NOT NULL default NOW(),
    last_updated_by VARCHAR(255) NULL,
    last_updated TIMESTAMP NULL,
    
    PRIMARY KEY(id),
    CONSTRAINT fk_fuqua_acronyms_acronym_id   FOREIGN KEY(acronym_id) REFERENCES public.fuqua_acronyms(id),
    CONSTRAINT fk_fuqua_acronym_tags_tag_id   FOREIGN KEY(tag_id) REFERENCES public.fuqua_acronym_tags(id)
  );
