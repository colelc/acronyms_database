select
rdv.id, rc.assignment_id, rdt.case_id, rdv.template_id, rc.title, rdt.column_number, rdt.column_name, rdv.data_value, rdv.data_type
from rec_case rc, rec_data_template rdt, rec_data_values rdv
where
rdt.case_id = rc.id
and rdv.template_id = rdt.id
and rdt.case_id = 22
order by rc.assignment_id, rdv.id, rdt.case_id, rdv.template_id, rdt.column_number, rdt.column_name