create table
  public.dar_registration_appts (
    id INTEGER UNIQUE GENERATED ALWAYS AS IDENTITY,
    
    board_id  INTEGER NOT NULL,
    
    appt_date_text TEXT NOT NULL,
    appt_date DATE NOT NULL,
    
    appt_start_time_text TEXT NOT NULL,
    appt_start_time TIMESTAMP NOT NULL,
    
    appt_end_time_text TEXT NOT NULL,
    appt_end_time TIMESTAMP NOT NULL,
    
    appt_duration INTEGER NOT NULL,
    appt_count INTEGER NOT NULL,
    
    appt_status TEXT NOT NULL,
    
    appt_active BOOLEAN NOT NULL default TRUE,
    
    created_by VARCHAR(255) NOT NULL,
    created timestamp NOT NULL default NOW(),
    deleted_by VARCHAR(255) NULL,
    deleted timestamp NULL,
    last_updated_by VARCHAR(255)  NULL,
    last_updated TIMESTAMP NULL,
    
    PRIMARY KEY(id),
    CONSTRAINT fk_dar_board_members_board_id   FOREIGN KEY(board_id) REFERENCES public.dar_board_members(id)
  );
