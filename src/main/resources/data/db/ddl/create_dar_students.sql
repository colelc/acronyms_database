/*
create table
  public.dar_students (
    id INTEGER UNIQUE GENERATED ALWAYS AS IDENTITY,
    
    net_id TEXT NOT NULL,
    
    stu_fname TEXT NOT NULL,
    stu_lname TEXT NOT NULL,
    duke_id TEXT NOT NULL,
    stu_email TEXT NOT NULL,
    stu_degree TEXT NOT NULL,
    stu_class TEXT NOT NULL,
    stu_fuqua_email TEXT NOT NULL, 
    stu_active BOOLEAN NOT NULL default TRUE,
    stu_photo TEXT NULL,
   
    created_by VARCHAR(255) NOT NULL,
    created timestamp NOT NULL default NOW(),
    deleted_by VARCHAR(255) NULL,
    deleted timestamp NULL,
    last_updated_by VARCHAR(255)  NULL,
    last_updated TIMESTAMP NULL
  
  );
*/