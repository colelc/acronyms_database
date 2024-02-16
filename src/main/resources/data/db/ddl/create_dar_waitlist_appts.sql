create table
  public.dar_waitlist_appts (
    id INTEGER UNIQUE GENERATED ALWAYS AS IDENTITY,
    
    board_id  INTEGER NOT NULL,
    student_id INTEGER NOT NULL,
    appt_id INTEGER NOT NULL,
    wait_timestamp TIMESTAMP NOT NULL default NOW(),
    wait_status BOOLEAN,
    
    created_by VARCHAR(255) NOT NULL,
    created timestamp NOT NULL default NOW(),
    deleted_by VARCHAR(255) NULL,
    deleted timestamp NULL,
    last_updated_by VARCHAR(255)  NULL,
    last_updated TIMESTAMP NULL,
    
    PRIMARY KEY(id),
    CONSTRAINT fk_dar_board_members_board_id   FOREIGN KEY(board_id) REFERENCES public.dar_board_members(id),
    CONSTRAINT fk_dar_available_appts_appt_id   FOREIGN KEY(appt_id) REFERENCES public.dar_available_appts(id)
    /*CONSTRAINT fk_dar_students_student_id   FOREIGN KEY(student_id) REFERENCES public.dar_students(id)*/
  );
