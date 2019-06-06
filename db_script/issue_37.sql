ALTER TABLE tp_exercise
ADD COLUMN note character  varying(1000);

update tp_exercise set deprecated = 0 where changed > NOW() - INTERVAL '1 DAY' and deprecated = 2;

update tp_exercise set note = '' where note is null;

select * from tp_exercise order by id desc;