select * from tp_exercise order by id desc;
select * from tp_day order by id desc;
select * from tp_plan order by id desc;

select     *
from       tp_day  d
left join  tp_exercise e on d.id = e.day_fk
left join  tp_plan p on d.plan_fk = p.id
where      e.id is null
order by d.id desc
;

select  max(e.id)
      , avg(e.deprecated)
from    tp_plan p
join    tp_day d on p.id = d.plan_fk
join    tp_exercise e on e.day_fk = d.id
where   p.id = 149
and     e.deprecated < 2
group by base_ex
;

--delete from tp_exercise where id >= 852;
--delete from tp_day where id in (248, 195, 194, 180, 179);

update tp_exercise set deprecated = 0 where id in (820, 536, 812,  847,  676,  807,  845,  844,  833,  810,  706,  825,  841,  819,  682,  827,  535,  817,  843,  816,  808,  814,  824,  818,  829,
811,  684,  794,  826,  851,  848,  804,  846,  775);