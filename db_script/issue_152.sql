select  e.id
      , e.name
      , weight, reps, sets, max_rep, pausetime, note
      , e.created
      , e.changed
      , base_ex
      , deprecated 
from    tp_exercise e
join    tp_day d on e.day_fk = d.id
join    tp_plan p on d.plan_fk = p.id
where   p.id = 149
and     base_ex = 748
and     e.created < now()
order by e.created desc
fetch first row only
;

select * from tp_exercise where id = 748;
select * from tp_day where id = 307;
select * from tp_plan where id = 149;