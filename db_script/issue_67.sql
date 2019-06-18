CREATE TABLE tp_bm_it (
    id SERIAL NOT NULL PRIMARY KEY,
    userid_fk integer NOT NULL,
    valuename character varying(100) NOT NULL,
    uom character varying(20) NOT NULL,
    timeofday character varying(20),
    value integer DEFAULT 1,
    note character varying(1000),
    base_stat integer DEFAULT 0,
    referenced_stat integer DEFAULT 0,
    created timestamp without time zone DEFAULT now(),
    changed timestamp without time zone DEFAULT now()
);

drop table tp_bm_it;

ALTER TABLE tp_bm_it RENAME COLUMN valuename TO value_name;
ALTER TABLE tp_bm_it RENAME COLUMN timeofday TO time_of_day;
ALTER TABLE tp_bm_it RENAME COLUMN base_stat TO base_bm_value;
ALTER TABLE tp_bm_it RENAME COLUMN referenced_stat TO referenced_bm_value;
