-- membership -- add a new membershiptype column
ALTER TABLE membership
    ADD COLUMN membertype VARCHAR(15);

-- Set the memType to SINGLE for all existing records
UPDATE membership SET membertype = 'SINGLE';

-- memberstatus -- a view of membership reflecting payment status
DROP view memberstatus;
CREATE OR REPLACE VIEW memberstatus AS
 SELECT m.mid, m.pid, m.paymentid, m.season, py.paypal_status, m.membertype, p.surname, p.givenname
   FROM membership m, payment py, people p
  WHERE m.paymentid = py.paymentid
    AND m.pid = p.pid;

-- version -- add a table reflecting the current applicaiton version
CREATE table version (
	vid INT8,              -- the unique version id (pk)
	version VARCHAR(30),   -- the version of the application instaled
	CONSTRAINT version_pk PRIMARY KEY (vid)
);
INSERT into VERSION (vid, version) VALUES (1, '1.5.0');

