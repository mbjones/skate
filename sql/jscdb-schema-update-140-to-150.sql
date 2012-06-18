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