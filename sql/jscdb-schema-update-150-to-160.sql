-- membership -- allow different membertypes in the same season
ALTER TABLE membership
    DROP CONSTRAINT membership_uk;
ALTER TABLE membership
	ADD CONSTRAINT membership_uk UNIQUE (pid,season,membertype);

-- membershiptype -- table to store the membershiptypes to be used in registration
CREATE TABLE membershiptype (
    typeName VARCHAR(20),       -- the unique name of the type (e.g., 'jsc_single')
    membertype VARCHAR(15),     -- the type of the membership, SINGLE, FAMILY, USFSA, USFSA_FAMILY
    description VARCHAR(50),    -- the label describing this membership class
    cost FLOAT8,                -- the cost for this membership type
	date_updated TIMESTAMP default CURRENT_TIMESTAMP, -- the date the record was last updated
   CONSTRAINT membershiptype_pk PRIMARY KEY (typeName)
);

INSERT INTO membershiptype (typeName, membertype, description, cost) VALUES ('jsc_single', 'SINGLE', 'JSC Individual Member', 20.00);
INSERT INTO membershiptype (typeName, membertype, description, cost) VALUES ('jsc_fam1', 'FAMILY1', 'JSC Additional Family Member #1', 10.00);
INSERT INTO membershiptype (typeName, membertype, description, cost) VALUES ('jsc_fam2', 'FAMILY2', 'JSC Additional Family Member #2', 10.00);
INSERT INTO membershiptype (typeName, membertype, description, cost) VALUES ('jsc_fam3', 'FAMILY3', 'JSC Additional Family Member #3', 10.00);
INSERT INTO membershiptype (typeName, membertype, description, cost) VALUES ('usfsa_single', 'USFSA', 'USFSA Individual Member', 50.00);
INSERT INTO membershiptype (typeName, membertype, description, cost) VALUES ('usfsa_fam1', 'USFSA_FAMILY1', 'USFSA Additional Family Member #1', 20.00);
INSERT INTO membershiptype (typeName, membertype, description, cost) VALUES ('usfsa_fam2', 'USFSA_FAMILY2', 'USFSA Additional Family Member #2', 20.00);
INSERT INTO membershiptype (typeName, membertype, description, cost) VALUES ('usfsa_fam3', 'USFSA_FAMILY3', 'USFSA Additional Family Member #3', 20.00);
INSERT INTO membershiptype (typeName, membertype, description, cost) VALUES ('usfsa_latefee', 'USFSA_LATEFEE', 'USFSA Late Fee', 8.00);

-- memberstatus -- a view of membership reflecting payment status
DROP VIEW memberstatus;
CREATE OR REPLACE VIEW memberstatus AS
 SELECT m.mid, m.pid, m.paymentid, m.season, py.paypal_status, m.payment_amount, m.membertype, p.surname, p.givenname
   FROM membership m, payment py, people p
  WHERE m.paymentid = py.paymentid
    AND m.pid = p.pid;
    
-- version -- add a table reflecting the current applicaiton version
UPDATE version set version='1.6.0' WHERE vid=1;
