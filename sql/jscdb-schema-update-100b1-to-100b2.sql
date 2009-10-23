-- roster -- add the new section column to the roster table
ALTER TABLE roster
    ADD COLUMN section VARCHAR(8),
    ALTER COLUMN levelpassed SET DEFAULT '0',
    ADD CONSTRAINT roster_level_fk FOREIGN KEY (levelPassed) REFERENCES levels;

-- Add section to the rosterpeople view
DROP VIEW rosterpeople;
CREATE OR REPLACE VIEW rosterpeople AS 
 SELECT r.rosterid, r.classid, r.pid, r.levelPassed, r.paymentid, 
        r.payment_amount, y.paypal_status, r.section, r.date_updated, 
        p.surname, p.givenname
   FROM roster r, people p, payment y
  WHERE r.pid = p.pid
    AND r.paymentid = y.paymentid
  ORDER BY r.section, r.classid, p.surname, p.givenname;

-- session -- add a new activeSession column
ALTER TABLE sessions
    ADD COLUMN activesession BOOLEAN,
    ADD COLUMN discountdate DATE;
    
-- Update the view of classes to include activeSession
DROP VIEW sessionclasses;
CREATE OR REPLACE VIEW sessionclasses AS 
 SELECT s.sid, s.sessionname, s.season, s.startdate, s.enddate, s.activesession, c.classid, 
        c.classtype, c.day, c.timeslot, c.instructorid, c.cost, 
        c.otherinstructors, p.surname, p.givenname, s.discountdate
   FROM sessions s, skatingclass c, people p
  WHERE c.sid = s.sid
    AND p.pid = c.instructorid;
    
