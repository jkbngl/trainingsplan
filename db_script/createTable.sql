drop table tp_exercise;
drop table tp_day;
drop table tp_plan;
drop table tp_user;

create table tp_user(
       ID  SERIAL PRIMARY KEY,
       username    varchar(80)   NOT NULL UNIQUE,
       created timestamp DEFAULT now()
);


create table tp_plan(
        ID  SERIAL PRIMARY KEY,
        userid_FK   INT        NOT NULL    REFERENCES tp_user (id),
        name      varchar(80),
        created timestamp DEFAULT now()
);

create table tp_day(
        ID  SERIAL PRIMARY KEY,
        plan_FK     INT        NOT NULL    REFERENCES tp_plan(id)
);

create table tp_exercise(
        ID  SERIAL PRIMARY KEY,
        day_FK      INT        NOT NULL    REFERENCES tp_day(id),
        weight      varchar(80),
        reps        varchar(80),
        sets        varchar(80),
        name        varchar(80),
        pausetime   varchar(80),
        MAX_REP     varchar(80)
);