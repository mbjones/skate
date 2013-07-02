-- membership -- allow different membertypes in the same season
ALTER TABLE membership
    DROP CONSTRAINT membership_uk;
ALTER TABLE membership
	ADD CONSTRAINT membership_uk UNIQUE (pid,season,membertype);

-- membershiptype -- table to store the membershiptypes to be used in registration
CREATE TABLE membershiptype (
    typeName VARCHAR(20),       -- the unique name of the type (e.g., 'jsc_single')
    membertype VARCHAR(15),     -- the type of the membership, SINGLE, FAMILY, USFSA, USFSA_FAMILY
    description VARCHAR(20),    -- the label describing this membership class
    cost FLOAT8,                -- the cost for this membership type
	date_updated TIMESTAMP default CURRENT_TIMESTAMP, -- the date the record was last updated
   CONSTRAINT membershiptype_pk PRIMARY KEY (typeName)
);

-- version -- add a table reflecting the current applicaiton version
UPDATE version set version='1.6.0' WHERE vid=1;
