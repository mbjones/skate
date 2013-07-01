-- membership -- allow different membertypes in the same season
ALTER TABLE membership
    DROP CONSTRAINT membership_uk;
ALTER TABLE membership
	ADD CONSTRAINT membership_uk UNIQUE (pid,season,membertype);

-- version -- add a table reflecting the current applicaiton version
UPDATE version set version='1.6.0' WHERE vid=1;
