drop table tp_exercise;
drop table tp_day;
drop table tp_plan;
drop table tp_user;

create table tp_user(
       ID  SERIAL PRIMARY KEY,
       username    varchar(80)   NOT NULL UNIQUE
);


create table tp_plan(
        ID  SERIAL PRIMARY KEY,
        userid_FK   INT        NOT NULL    REFERENCES tp_user (id)
);

create table tp_day(
        ID  SERIAL PRIMARY KEY,
        plan_FK     INT        NOT NULL    REFERENCES tp_plan(id)
);

create table tp_exercise(
        ID  SERIAL PRIMARY KEY,
        day_FK      INT        NOT NULL    REFERENCES tp_day(id),
        weight      INT,
        reps        INT,
        sets        INT,
        name        varchar(80),
        pausetime   INT,
        MAX_REP     INT
);

create table tp_preferences 
(
     id    SERIAL PRIMARY KEY ,
     userid_FK   INT        NOT NULL    REFERENCES tp_user (id),
     mul_weight INT default 1,
     mul_reps INT default 1,
     mul_sets INT default 1,
     mul_maxrep INT default 1,
     check_weight  BOOLEAN DEFAULT TRUE NOT NULL ,
     check_reps  BOOLEAN DEFAULT TRUE NOT NULL ,
     check_sets  BOOLEAN DEFAULT TRUE NOT NULL ,
     check_max_rep BOOLEAN DEFAULT FALSE NOT NULL ,
     created timestamp DEFAULT now(),
     changes timestamp DEFAULT now()
);