-- Left-right

SET SEARCH_PATH TO parlgov;
drop table if exists q4 cascade;

-- You must not change this table definition.


CREATE TABLE q4(
        countryName VARCHAR(50),
        r0_2 INT,
        r2_4 INT,
        r4_6 INT,
        r6_8 INT,
        r8_10 INT
);

-- You may find it convenient to do this for each of the views
-- that define your intermediate steps.  (But give them better names!)
drop view if exists intermediate_step_0_2 CASCADE;
drop view if exists intermediate_step_2_4 CASCADE;
drop view if exists intermediate_step_4_6 CASCADE;
drop view if exists intermediate_step_6_8 CASCADE;
drop view if exists intermediate_step_8_10 CASCADE;
drop view if exists result_0_4 CASCADE;
drop view if exists result_0_6 CASCADE;
drop view if exists result_0_8 CASCADE;
drop view if exists result_0_10 CASCADE;
drop view if exists nonTrivialCountries CASCADE;
drop view if exists TrivialCountries CASCADE;
-- Define views for your intermediate steps here.

create view intermediate_step_0_2 as 
select country_id, count(distinct party_id) as count_0_2
from party_position, party, country
where party_position.party_id = party.id and party.country_id = country.id and 0<=left_right and left_right<2--now want to eliminate left_right is null case
group by country_id;

create view intermediate_step_2_4 as 
select country_id, count(distinct party_id) as count_2_4
from party_position, party, country
where party_position.party_id = party.id and party.country_id = country.id and 2<=left_right and left_right<4
group by country_id;

create view intermediate_step_4_6 as 
select country_id, count(distinct party_id) as count_4_6
from party_position, party, country
where party_position.party_id = party.id and party.country_id = country.id and 4<=left_right and left_right<6
group by country_id;

create view intermediate_step_6_8 as 
select country_id, count(distinct party_id) as count_6_8
from party_position, party, country
where party_position.party_id = party.id and party.country_id = country.id and 6<=left_right and left_right<8
group by country_id;

create view intermediate_step_8_10 as 
select country_id, count(distinct party_id) as count_8_10
from party_position, party, country
where party_position.party_id = party.id and party.country_id = country.id and 8<=left_right and left_right<=10
group by country_id;
-------------------------------------------------------------------
create view result_0_4 as
select country_id, count_0_2, count_2_4
from intermediate_step_0_2  NATURAL FULL JOIN intermediate_step_2_4;
-------------------------------------------------------------------
create view result_0_6 as
select country_id, count_0_2, count_2_4, count_4_6
from result_0_4  NATURAL FULL JOIN intermediate_step_4_6;
-------------------------------------------------------------------
create view result_0_8 as
select country_id, count_0_2, count_2_4, count_4_6, count_6_8
from result_0_6  NATURAL FULL JOIN intermediate_step_6_8;
-------------------------------------------------------------------
create view result_0_10 as
select country_id, count_0_2, count_2_4, count_4_6, count_6_8, count_8_10
from result_0_8  NATURAL FULL JOIN intermediate_step_8_10;
-------------------------------------------------------------------

create view nonTrivialCountries as
select country_id, case when count_0_2 is not null then count_0_2 end as r0_2, case when count_2_4 is not null then count_2_4 end as r2_4, case when count_4_6 is not null then count_4_6 end as r4_6, case when count_6_8 is not null then count_6_8 end as r6_8, case when count_8_10 is not null then count_8_10 end as r8_10
from result_0_10;

create view TrivialCountries as
select country.id as country_id, 0 as r0_2, 0 as r2_4, 0 as r4_6, 0 as r6_8, 0 as r8_10
from country
where country.id not in (
    select country_id 
    from nonTrivialCountries
);

-- the answer to the query 
INSERT INTO q4 
select country.name as countryName, r0_2, r2_4, r4_6, r6_8, r8_10
from country, nonTrivialCountries
where country.id = nonTrivialCountries.country_id;

INSERT INTO q4 
select country.name as countryName, r0_2, r2_4, r4_6, r6_8, r8_10
from country, TrivialCountries
where country.id = TrivialCountries.country_id;


