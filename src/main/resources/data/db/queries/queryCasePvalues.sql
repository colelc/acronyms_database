select 
c.id, c.assignment_id, c.title, pv.sequence_number, pv.p_value
from 
rec_data_p_values pv, rec_case c
where
pv.case_id = c.id
order by c.assignment_id, pv.sequence_number;
