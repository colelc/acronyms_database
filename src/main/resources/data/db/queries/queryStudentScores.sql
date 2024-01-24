select 
c.id as case_id,
c.assignment_id, 
c.title,
s.id as user_id, s.uid, s.first_name, s.last_name,
ss.id as student_score_id, ss.score

from
rec_students s,  rec_student_scores ss, rec_case c
where
s.id = ss.user_id
and ss.case_id = c.id
order by c.id, s.uid
;