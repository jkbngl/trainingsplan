CREATE TABLE tp_bm_it (
    id SERIAL NOT NULL PRIMARY KEY,
    userid_fk integer NOT NULL,
    value_name character varying(100) NOT NULL,
    uom character varying(20) NOT NULL,
    time_of_day character varying(20),
    value integer DEFAULT 1,
    note character varying(1000),
    base_bm_value integer DEFAULT 0,
    referenced_bm_value integer DEFAULT 0,
    created timestamp without time zone DEFAULT now(),
    changed timestamp without time zone DEFAULT now()
);

drop table tp_bm_it;

ALTER TABLE tp_bm_it RENAME COLUMN valuename TO value_name;
ALTER TABLE tp_bm_it RENAME COLUMN timeofday TO time_of_day;
ALTER TABLE tp_bm_it RENAME COLUMN base_bm_value TO base_bm_id;
ALTER TABLE tp_bm_it RENAME COLUMN referenced_bm_value TO referenced_bm_id;


select  userid_fk 
      , value_name 
      , uom 
      , time_of_day  
      , value 
      , note 
      , base_bm_value 
      , referenced_bm_value 
      , created 
      , changed 
from    tp_bm_it p 
where   userid_fk = 17
;

select * from tp_user;

insert into tp_bm_it (userid_fk, value_name, uom, time_of_day, value) values (15, 'oberarm', 'cm', 'after training', 35);
insert into tp_bm_it (userid_fk, value_name, uom, time_of_day, value) values (15, 'wade', 'cm', 'evening', 36);
insert into tp_bm_it (userid_fk, value_name, uom, time_of_day, value) values (15, 'linker oberarm', 'cm', 'after training', 34);

insert into tp_bm_it (userid_fk, value_name, uom, time_of_day, value, base_bm_value, referenced_bm_value) values (15, 'oberarm', 'cm', 'after training', 35);

select  *
from    tp_bm_it;

select  distinct base_bm_id
      , userid_fk 
      , value_name 
      , uom 
      , time_of_day  
      , value 
      , note 
      , referenced_bm_id 
      , created 
      , changed 
from    tp_bm_it p 
where   userid_fk = 15
--and base_bm_value > 0
;