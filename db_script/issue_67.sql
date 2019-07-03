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
ALTER TABLE tp_bm_it RENAME COLUMN time_of_day TO tod;
ALTER TABLE tp_bm_it RENAME COLUMN base_bm_value TO base_bm_id;
ALTER TABLE tp_bm_it RENAME COLUMN referenced_bm_value TO referenced_bm_id;


select  userid_fk 
      , value_name 
      , uom 
      , time_of_day  
      , value 
      , note 
      , base_bm_id 
      , referenced_bm_id 
      , created 
      , changed 
      , id
from    tp_bm_it p 
--where   userid_fk = 17
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


--------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------------
--------------------------------------------------------------------------------





CREATE TABLE tp_uoms (
    id SERIAL NOT NULL PRIMARY KEY,
    uom_name character varying(100) NOT NULL,
    createdby character varying(20) NOT NULL,
    created timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    changed timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE EXTENSION moddatetime;


CREATE TRIGGER    tp_uoms_moddatetime
BEFORE UPDATE ON  tp_uoms
FOR EACH ROW
EXECUTE PROCEDURE moddatetime (changed)
;
/

select * from tp_uoms;
insert into tp_uoms (uom_name, createdby) values ('kg', 'jakob.engl');
update tp_uoms set createdby = 'test';
update tp_uoms set createdby = 'jakob.engl';

insert into tp_uoms (uom_name, createdby) values ('g', 'jakob.engl');
insert into tp_uoms (uom_name, createdby) values ('dg', 'jakob.engl');
insert into tp_uoms (uom_name, createdby) values ('kg', 'jakob.engl');
insert into tp_uoms (uom_name, createdby) values ('lb', 'jakob.engl');
insert into tp_uoms (uom_name, createdby) values ('mm', 'jakob.engl');
insert into tp_uoms (uom_name, createdby) values ('cm', 'jakob.engl');
insert into tp_uoms (uom_name, createdby) values ('m', 'jakob.engl');
insert into tp_uoms (uom_name, createdby) values ('pound', 'jakob.engl');
insert into tp_uoms (uom_name, createdby) values ('foot', 'jakob.engl');
insert into tp_uoms (uom_name, createdby) values ('feed', 'jakob.engl');
insert into tp_uoms (uom_name, createdby) values ('stone', 'jakob.engl');
insert into tp_uoms (uom_name, createdby) values ('oz', 'jakob.engl');


----------------------------------------------------------------------------------------------------------------------------------------------------------------
----------------------------------------------------------------------------------------------------------------------------------------------------------------

CREATE TABLE tp_tods (
    id SERIAL NOT NULL PRIMARY KEY,
    tod_name character varying(100) NOT NULL,
    createdby character varying(20) NOT NULL,
    created timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL,
    changed timestamp DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TRIGGER    tp_tods_moddatetime
BEFORE UPDATE ON  tp_tods
FOR EACH ROW
EXECUTE PROCEDURE moddatetime (changed)
;
/

select tod_name from tp_tods;


insert into tp_tods (tod_name, createdby) values ('after waking up', 'jakob.engl');
insert into tp_tods (tod_name, createdby) values ('after breakfast', 'jakob.engl');
insert into tp_tods (tod_name, createdby) values ('after training', 'jakob.engl');
insert into tp_tods (tod_name, createdby) values ('on empty stomach', 'jakob.engl');
insert into tp_tods (tod_name, createdby) values ('after lunch', 'jakob.engl');
insert into tp_tods (tod_name, createdby) values ('after dinner', 'jakob.engl');
insert into tp_tods (tod_name, createdby) values ('after snack', 'jakob.engl');

