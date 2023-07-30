-- Participate

SET SEARCH_PATH TO parlgov;
drop table if exists q3 cascade;

-- You must not change this table definition.

create table q3(
        countryName varchar(50),
        year int,
        participationRatio real
);

-- You may find it convenient to do this for each of the views
-- that define your intermediate steps.  (But give them better names!)
DROP VIEW IF EXISTS Formatted CASCADE;
DROP VIEW IF EXISTS YearWanted CASCADE;
DROP VIEW IF EXISTS NotEligible CASCADE;
DROP VIEW IF EXISTS Eligible CASCADE;
DROP VIEW IF EXISTS Answer CASCADE;


-- Define views for your intermediate steps here.

CREATE VIEW Formatted AS(
	SELECT EXTRACT(YEAR FROM e_date) AS year, country_id,
       	       votes_cast / electorate :: float as ratio
	FROM election
	where votes_cast is not null and electorate is not null
);


CREATE VIEW YearWanted AS(
	SELECT year, country_id, avg(ratio) as ratio
	FROM Formatted 
	WHERE 2001 <= year AND 2016 >= year
	GROUP BY year, country_id
);

CREATE VIEW NotEligible AS(
	SELECT DISTINCT q1.country_id as country_id
	FROM YearWanted q1, YearWanted q2
	WHERE q1.year < q2.year AND q1.country_id = q2.country_id AND q1.ratio > q2.ratio 
);

CREATE VIEW Eligible AS(
	SELECT DISTINCT country_id
	FROM YearWanted
	WHERE country_id NOT IN (
		SELECT country_id 
		FROM NotEligible
	)
);
	
CREATE VIEW Answer AS(
	SELECT Eligible.country_id, year, ratio as participationRatio
	FROM Eligible, YearWanted 
	where Eligible.country_id = YearWanted.country_id
);

-- the answer to the query 
insert into q3(
	SELECT country.name as countryName, year, participationRatio
	FROM Answer, country
	WHERE Answer.country_id = country.id
); 

