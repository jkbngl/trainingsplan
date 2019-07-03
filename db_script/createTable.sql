-- Get the data from linux command line, log onto container as postgres user and execute following command per table
-- docker exec -it --user postgres postgresTP bash
-- postgres@localhost:/$ pg_dump postgres -t public.tp_exercise --schema-only

drop table tp_exercise;
drop table tp_day;
drop table tp_plan;
drop table tp_user;

CREATE TABLE public.tp_user (
    id integer NOT NULL,
    username character varying(80) NOT NULL,
    created timestamp without time zone DEFAULT now()
);


CREATE TABLE public.tp_plan (
    id integer NOT NULL,
    userid_fk integer NOT NULL,
    name character varying(80),
    created timestamp without time zone DEFAULT now(),
    deleted integer DEFAULT 0,
    changed timestamp without time zone DEFAULT now()
);

CREATE TABLE public.tp_day (
    id integer NOT NULL,
    plan_fk integer NOT NULL
);

CREATE TABLE public.tp_exercise (
    id integer NOT NULL,
    day_fk integer NOT NULL,
    weight character varying(80) DEFAULT '0'::character varying,
    reps character varying(80) DEFAULT '0'::character varying,
    sets character varying(80) DEFAULT '0'::character varying,
    name character varying(80) DEFAULT '0'::character varying,
    pausetime character varying(80) DEFAULT '0'::character varying,
    max_rep character varying(80) DEFAULT '0'::character varying,
    deprecated integer DEFAULT 0,
    created timestamp without time zone DEFAULT now(),
    changed timestamp without time zone,
    base_ex integer DEFAULT 0,
    referenced_ex integer DEFAULT 0,
    note character varying(1000)
);

CREATE TABLE public.tp_preferences (
    userid_fk integer NOT NULL,
    mul_weight integer DEFAULT 1,
    mul_reps integer DEFAULT 1,
    mul_sets integer DEFAULT 1,
    mul_maxrep integer DEFAULT 1,
    check_weight boolean DEFAULT true NOT NULL,
    check_reps boolean DEFAULT true NOT NULL,
    check_sets boolean DEFAULT true NOT NULL,
    check_maxrep boolean DEFAULT false NOT NULL,
    created timestamp without time zone DEFAULT now(),
    changed timestamp without time zone DEFAULT now(),
    check_simple_view boolean DEFAULT false NOT NULL,
    check_chart_type boolean DEFAULT false NOT NULL,
    check_dialog_save boolean DEFAULT true NOT NULL
);

CREATE TABLE tp_bm_it (
    id SERIAL NOT NULL PRIMARY KEY,
    userid_fk integer NOT NULL,
    value_name character varying(100) NOT NULL,
    uom character varying(20) NOT NULL,
    tod character varying(20),
    value integer DEFAULT 1,
    note character varying(1000),
    base_bm_id integer DEFAULT 0,
    referenced_bm_id integer DEFAULT 0,
    created timestamp without time zone DEFAULT now(),
    changed timestamp without time zone DEFAULT now()
);

CREATE TABLE public.tp_uoms (
    id integer NOT NULL,
    uom_name character varying(100) NOT NULL,
    createdby character varying(20) NOT NULL,
    created timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    changed timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE public.tp_tods (
    id integer NOT NULL,
    tod_name character varying(100) NOT NULL,
    createdby character varying(20) NOT NULL,
    created timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    changed timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);