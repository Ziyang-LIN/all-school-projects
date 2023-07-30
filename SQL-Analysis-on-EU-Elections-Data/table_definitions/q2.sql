-- Winners

SET SEARCH_PATH TO parlgov;
drop table if exists q2 cascade;

-- You must not change this table definition.

create table q2(
countryName VARCHaR(100),
partyName VARCHaR(100),
partyFamily VARCHaR(100),
wonElections INT,
mostRecentlyWonElectionId INT,
mostRecentlyWonElectionYear INT
);


-- You may find it convenient to do this for each of the views
-- that define your intermediate steps.  (But give them better names!)


DROP VIEW IF EXISTS electionsWon CASCADE;
DROP VIEW IF EXISTS partyTimesWon CASCADE;
DROP TABLE IF EXISTS allPartyTimesWon CASCADE;
DROP VIEW IF EXISTS countryTimesWon CASCADE;
DROP VIEW IF EXISTS countryPartyNum CASCADE;
DROP VIEW IF EXISTS countryAvgPartyTimesWon CASCADE;
DROP VIEW IF EXISTS qualifiedParties CASCADE;
DROP VIEW IF EXISTS countryAverage CASCADE;
DROP VIEW IF EXISTS qualifyingParties CASCADE;
DROP VIEW IF EXISTS mostRecentlyWon CASCADE;
DROP VIEW IF EXISTS result_1 CASCADE;
DROP VIEW IF EXISTS result_2 CASCADE;

-- Define views for your intermediate steps here.

create view electionsWon as --checked
	select p.id as party_id, e_result.election_id as election_id, c.id as country_id, e_date
	from election_result e_result, election e, party p, country c
	where e_result.election_id = e.id and e_result.party_id = p.id and p.country_id = c.id --didn't check that election happen in the same country as the party's country. And we don't have to, either (potentially a party can compete in another country.
	and e_result.votes IS NOT NULL and not exists(
		select *
		from election_result e_result_sub, election e_sub, party p_sub, country c_sub
		where e_result_sub.party_id = p_sub.id and e_result.election_id=e_result_sub.election_id and p.id != p_sub.id and e_result_sub.votes > e_result.votes
	);

create view partyTimesWon as --could there be redundant election_id for the same party? No, because a party can only join an election once! CHECK THIS!!!! I CAN't REALLY REASON NOW. checked.
	select country_id, party_id, count(distinct election_id) as timesWonCount
	from electionsWon
	group by country_id, party_id
	;
---------------------------------------------------------------------------------------------------------------------

create table allPartyTimesWon (country_id INT, party_id INT PRIMARY KEY, timesWonCount INT);

insert into allPartyTimesWon --insert the sum of party that have won at least one election.  checked.
	select * from partyTimesWon;


--SHOULDN'T USE THIS FOR QUALIFIED PARTIES
insert into allPartyTimesWon --insert the party that have never won any election, assgin them a velue of 0. checked.  *** NOTE THAT WE ONLY FILLED IN THE PARTIES THAT HAVEN'T WON ANY ELECTIONS YET, BUT THERE COULD BE COUNTRY THAT DOESN'T HAVE ANY PARTIES AT ALL, SO IN WHICH CASE, THE COUNTRY SHOULDN'T HAVE ANY QUALIFIED PARTY SO WE DON'T EVEN BOTHER ADDING THEM.
	select party.country_id, party.id as party_id, 0 as timesWonCount  --WARNING: is the format ok?
	from country, party
	where party.country_id = country.id and party.id not in ( -- DOES 'NOT IN' REQUIRE THE COLUMN NAMES TO MATCH?
		select party_id
		from partyTimesWon
		)
	group by party.country_id, party.id;
---------------------------------------------------------------------------------------------------------------------

create view countryTimesWon as --checked. ONLY COUNTRIES THAT HAVE A PARTY AT ALL IS INCLUDED IN allPartyTimesWon.
	select country_id, sum(timesWonCount) as total_won--HOW DOES *3 WORK? HAS TO BE IN A SUBQUERY (XXX * 3) ????
	from allPartyTimesWon
	group by country_id
	;

create view countryPartyNum as --checked
	select country.id as country_id, count(party.id) as partyNumCount
	from country, party 
	where country.id = party.country_id
	group by country.id
	;

create view countryAvgPartyTimesWon as --BECAUSE THE COUNTRY MUST AT LEAST HAVE A PARTY TO BE INCLUDED IN COUNTRYTIMESWON, THEN COUNTRYPARTYNUM CAN'T BE 0 OTHERWISE WE HAVE A GLITCH OR BUG. checked.
	select countryPartyNum.country_id, cast(countryTimesWon.total_won as decimal)/countryPartyNum.partyNumCount as avgTimesWonInDecimal --DECIMAL VS FLOAT???
	from countryTimesWon, countryPartyNum
	where countryTimesWon.country_id = countryPartyNum.country_id;

create view qualifiedParties as --checked.
	select partyTimesWon.country_id, party_id, timesWonCount
	from partyTimesWon, countryAvgPartyTimesWon
	where partyTimesWon.country_id = countryAvgPartyTimesWon.country_id and partyTimesWon.timesWonCount > 3*avgTimesWonInDecimal;
------------------------------------------------------------------------------------------------------------------------------------------------
CREATE VIEW mostRecentlyWon AS --QQQQQQQQQQ: COULD A PARTY WIN TWO ELECTIONS ON THE SAME DATE?, JUST GROUP BY THEN. checked.
	select party_id, election_id, extract (year from e_date) as mostRecentlyWonElectionYear
	from electionsWon e1
	where not exists(
	    select *
	    from electionsWon e2
	    where e1.party_id = e2.party_id and e1.e_date < e2.e_date
	    )
	group by party_id, election_id, e_date;

------------------------------------------------------------------------------------------------------------------------------------------------
--countries that have a party family
create view result_1 as
	select country.name as countryName, party.name as partyName, party_family.family as partyFamily, timesWonCount as wonElections, mostRecentlyWon.election_id as mostRecentlyWonElectionId, mostRecentlyWonElectionYear, qualifiedParties.party_id
	from qualifiedParties, mostRecentlyWon, country, party, party_family--, party_family
	where qualifiedParties.country_id = country.id and qualifiedParties.party_id = party.id and party.id = mostRecentlyWon.party_id and party.id = party_family.party_id;


--countries that don't have any party family
create view result_2 as
	select country.name as countryName, party.name as partyName, NULL as partyFamily, timesWonCount as wonElections, mostRecentlyWon.election_id as mostRecentlyWonElectionId, mostRecentlyWonElectionYear
	from qualifiedParties, mostRecentlyWon, country, party
	where qualifiedParties.country_id = country.id and qualifiedParties.party_id = party.id and party.id = mostRecentlyWon.party_id and qualifiedParties.party_id not in (
	    select party_id
	    from result_1	
	);

insert into q2 
    select countryName, partyName, partyFamily, wonElections, mostRecentlyWonElectionId, mostRecentlyWonElectionYear
    from result_1;

insert into q2 
    select countryName, partyName, partyFamily, wonElections, mostRecentlyWonElectionId, mostRecentlyWonElectionYear
    from result_2;


