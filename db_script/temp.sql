select * from tp_user;
select * from tp_plan;
select * from tp_day order by id;
select * from tp_exercise order by id;

delete from tp_plan where id = 134;
delete from tp_day where id = 184;
delete from tp_exercise where id = 342;

update tp_plan set name = 'gemuetlich' where id = 147; 


select  distinct base_ex, e.name, u.username
  from  tp_exercise e 
  join  tp_day  d on d.id = day_fk
  join  tp_plan p on p.id = d.plan_fk
  join  tp_user u on u.id = p.userid_fk
 where  base_ex > 0
   and  username = 'jakob engl'
;

select  CAST(coalesce(weight, '1') AS integer) * CAST(coalesce(sets , '1') AS integer) * CAST(coalesce(reps , '1') AS integer) * CAST(coalesce(max_rep , '1') AS integer) as value
        , name
        , weight, reps, sets, max_rep, id
  from  tp_exercise
 where  base_ex = (select distinct base_ex from tp_exercise where name = 'lat' and base_ex > 0)
order by id
;

ALTER TABLE tp_exercise ALTER COLUMN max_rep
SET DEFAULT '0';

select  d.id 
  from  tp_day  d
  join  tp_plan p on p.id = d.plan_fk 
 where  p.id = 141
;

select  d.id 
	from  tp_day d 
	join  tp_plan p on p.id = d.plan_fk 
	where  p.id = 141
;

select   max(e.id), base_ex
  from  tp_exercise e
  join  tp_day d on e.day_fk = d.id
  join  tp_plan p on d.plan_fk = p.id
  join  tp_user u on p.userid_fk = u.id
 where  u.username = 'nathalie engl'
   and  e.name = ?
   and  e.weight = ?
   and  e.weight = ?
   and  e.sets = ?
   and  e.reps = ? 
   and  e.pausetime = ?
   and  e.deprecated = 0;
  


select  e.id 
  from  tp_exercise e 
 where  e.id = 463  
 and  e.name = 'brustpresse'   
 and  e.weight = '80'   
 and  e.reps = '10'   
 and  e.sets = '5'   
 and  e.pausetime = '3'  
 and  e.max_rep = '100'
;


select  e.id 
  from  tp_day d
  join  tp_plan p on p.id = d.plan_fk
  join  tp_exercise e on d.id = e.day_fk
  where p.id = 131
  order by 1
;

select  p.id 
  from  tp_plan p 
  join  tp_user u on u.id = p.userid_fk 
 where username = 'jakob engl' 
   and p.name = 'grunduebengen';

select d.id from tp_day d join tp_plan p on d.plan_fk = p.id join tp_exercise e on d.id = e.day_fk where e.id = 379;

select  e.day_fk
      , e.id 
	  , e.name
      , e.weight
	  , e.reps
	  , e.sets
	  , e.max_rep
	  , e.pausetime
  from tp_user u 
  join tp_plan p      on u.id = p.userid_fk 
  join tp_day d       on p.id = d.plan_fk
  join tp_exercise e  on d.id = e.day_fk 
  where p.id = 80
  and deprecated = 0;
  
update tp_exercise set deprecated = 0 where day_fk = 237; 



select p.id from tp_plan p join tp_user u on u.id = p.userid_fk where p.name = 'grunduebengen test after truncate' and username = 'jakob engl';

select  e.id 
  from  tp_exercise e 
  join  tp_day d on e.day_fk = d.id
  join  tp_plan p on d.plan_fk = p.id
  join  tp_user u on u.id = p.userid_fk
  where u.username = 'jakob engl'
    and d.plan_fk = 62
    and d.id = 129
    and e.name = 'Kreuzheben'
    and e.weight = '75'
    and e.reps = '12'
    and e.sets = '5'
    and e.pausetime = '3'
    and e.max_rep = '100'
;

update tp_plan set name = 'grunduebengen test after truncate' where id between 59 and 64; 
update tp_user set username = 'nathalie pirgstaller' where id = 4; 

delete from tp_plan where id = 150;
delete from tp_day where id >= 130;
delete from tp_exercise where id >= 125;


INSERT INTO tp_user(username) VALUES ('nathalie pirgstaller');
INSERT INTO tp_plan(userid_FK) VALUES (6);
INSERT INTO tp_exercise(day_fk, name, weight, reps, sets, max_rep, pausetime) VALUES ('11', 'test', 'test2', 'test3', 'test4', 'test5', 'test6');

/*
truncate table tp_user cascade;
truncate table tp_plan cascade;
truncate table tp_day cascade;
truncate table tp_exercise cascade;
*/

select * from tp_plan p
join tp_user u on p.userid_fk = u.id;

select max(id) from tp_plan where userid_fk = 6;

select count(1)
from tp_user u
join tp_plan p      on u.id = p.userid_fk
join tp_day d       on p.id = d.plan_fk
where username = 'jakob engl'
and p.id = (select max(p.id) from tp_user u join tp_plan p on u.id = p.userid_fk where username = 'jakob engl')
;

select distinct p.name , date(p.created), p.id
from tp_user u
join tp_plan p      on u.id = p.userid_fk
join tp_day d       on p.id = d.plan_fk
where username = 'jakob engl'
;

select *
from tp_user u
join tp_plan p      on u.id = p.userid_fk
join tp_day d       on p.id = d.plan_fk
where username = 'jakob engl'
and p.id = (select max(p.id) from tp_user u join tp_plan p on u.id = p.userid_fk where username = 'jakob engl')
;

select max(p.id) from tp_user u join tp_plan p on u.id = p.userid_fk where username = 'nani';


select  u.username
      , d.plan_fk
      , e.day_fk
      , e.name
      , e.weight
      , e.reps
      , e.sets
      , e.max_rep
      , e.pausetime
from tp_user u
join tp_plan p      on u.id = p.userid_fk
join tp_day d       on p.id = d.plan_fk
join tp_exercise e  on d.id = e.day_fk
where username = 'nani';

select  e.day_fk , e.name, e.weight, e.reps, e.sets, e.max_rep, e.pausetime from tp_user u join tp_plan p      on u.id = p.userid_fk join tp_day d       on p.id = d.plan_fk join tp_exercise e  on d.id = e.day_fk where username = ?and p.id = 'jakob engl'
;



ALTER TABLE tp_exercise
ADD COLUMN created timestamp without time zone DEFAULT now();

select distinct p.name, date(p.created), p.id 
				from tp_user u 
				join tp_plan p      on u.id = p.userid_fk 
				join tp_day d       on p.id = d.plan_fk 
				 where username = 'jakob engl';
