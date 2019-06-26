select * from tp_user;
select * from tp_plan;

delete from tp_user where username = '';
delete from tp_plan where name = '';


update tp_user set username = 'jakob.engl' where username = 'jakob engl';
update tp_user set username = 'nani' where username = 'nathalie engl';
update tp_user set username = 'seppl' where username = 'josef engl';
update tp_user set username = 'edith' where username = 'edith hauser';
update tp_user set username = 'mrjon' where username = 'jonas engl';
update tp_user set username = 'julian' where username = 'julian schmalzl';