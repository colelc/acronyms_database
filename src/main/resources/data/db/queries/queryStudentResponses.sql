// individual student
select 
s.uid, s.first_name, s.last_name,
c.assignment_id, c.title,
ss.score,
sr.submitted_value_seq_number, sr.submitted_value
from
rec_students s, rec_student_responses sr, rec_student_scores ss, rec_case c
where
s.uid = 'jz362'
and s.id = sr.user_id
and sr.case_id = c.id
and ss.user_id = sr.user_id
and ss.case_id = c.id
order by c.id, sr.submitted_value_seq_number
;

// for a case
select 
c.id as case_id,
s.uid, s.first_name, s.last_name,
c.assignment_id, 
sr.submitted_value_seq_number, sr.submitted_value
from
rec_students s, rec_student_responses sr,  rec_case c
where
s.id = sr.user_id
and sr.case_id = c.id
and s.id = sr.user_id
order by c.id, s.uid, sr.submitted_value_seq_number
;