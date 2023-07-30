SET SEARCH_PATH TO parlgov;
drop table if exists q1 cascade;

-- You must not change this table definition.

create table q1(
year INT,
countryName VARCHAR(50),
voteRange VARCHAR(20),
partyName VARCHAR(100)
);


-- You may find it convenient to do this for each of the views
-- that define your intermediate steps.  (But give them better names!)
DROP VIEW IF EXISTS intermediate_step CASCADE;
drop view if exists elect_result cascade;
drop view if exists elect_result_year_filtered cascade;
drop view if exists by_year_country_party cascade;
drop view if exists next_1 cascade; 
drop view if exists result cascade;

-- Define views for your intermediate steps here.

--------------------------------
--INTERMEDIATE 1


CREATE VIEW elect_result AS
select extract (year from election.e_date) as year,election.country_id, party.id, party.name,election_result.votes, election.votes_valid, cast(election_result.votes as decimal)/election.votes_valid as percentage
from election, party, election_result
where election.votes_valid is not null and election_result.votes is not null and election.id = election_result.election_id AND party.id = election_result.party_id
order by year, election.country_id
;

--------------------------------

CREATE VIEW elect_result_year_filtered AS
select * from elect_result
where 1996<=year and year<= 2016;



--INTERMETIDIATE 2


CREATE VIEW by_year_country_party AS
select year, country_id, id as party_id, avg(percentage) as avg_percentage 
from elect_result_year_filtered
GROUP BY year, country_id, id
ORDER BY year, country_id, party_id;



CREATE VIEW next_1 AS 
SELECT year, country_id, party_id, 
	CASE WHEN 0<avg_percentage and avg_percentage<=0.05 THEN '(0-5]'
		 WHEN 0.05<avg_percentage and avg_percentage<=0.10 THEN '(5-10]'
		 WHEN 0.10<avg_percentage and avg_percentage<=0.20 THEN '(10-20]'
		 WHEN 0.20<avg_percentage and avg_percentage<=0.30 THEN '(20-30]'
		 WHEN 0.30<avg_percentage and avg_percentage<=0.40 THEN '(30-40]'
		 WHEN 0.40<avg_percentage and avg_percentage<=100 THEN '(40-100]'
		 ELSE 'OTHER' --WARNING: Does this case ever happen? Well, yeah, with NULL. BUT CAN WE JUST IGNORE IT THIS WAY?
	END
FROM by_year_country_party;

----------------------------
--result

CREATE VIEW RESULT AS
SELECT year, country.name as countryName, next_1.case as voteRange, party.name_short as partyName
FROM next_1, country, party
WHERE next_1.country_id = country.id AND next_1.party_id = party.id and next_1.case != 'OTHER' --IMPORTANT!!! WARNING!!! check this later. 
ORDER BY year, country.name, next_1.case, party.name_short
;
-- the answer to the query 
insert into q1  --WARNING does the type of result view like VARCHAR (100) has to be the same as q1's table definition??? WARNING!!! check this later.
select * from RESULT;


