create table
  public.dar_board_members (
    id INTEGER UNIQUE GENERATED ALWAYS AS IDENTITY,
    entity_id TEXT NOT NULL, 
    board_fname TEXT NOT NULL,
    board_lname TEXT NOT NULL,
    board_preferred TEXT NOT NULL DEFAULT '',
    program TEXT NOT NULL DEFAULT '',
    board_class TEXT NOT NULL,
    hsm_cert TEXT NULL DEFAULT '',
    other_duke_degree TEXT NULL DEFAULT '',
    employer TEXT NULL DEFAULT '',
    job_title TEXT NULL DEFAULT '',
    linked_in TEXT NULL DEFAULT '',
    cur_serve_on TEXT NULL DEFAULT '',
    board_photo TEXT NULL DEFAULT '',
    board_email text NULL DEFAULT '',
    board_active BOOLEAN NOT NULL default TRUE,
    created_by VARCHAR(255) NOT NULL,
    created timestamp NOT NULL default NOW(),
    deleted_by VARCHAR(255) NULL,
    deleted timestamp NULL,
    last_updated_by VARCHAR(255)  NULL,
    last_updated TIMESTAMP NULL
  );