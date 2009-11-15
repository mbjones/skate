-- levels -- add the new order column to track which is the highest level
ALTER TABLE levels
    ADD COLUMN levelorder INT8;
    
UPDATE levels SET levelorder = 0 WHERE levelcode = '0';    
UPDATE levels SET levelorder = 11 WHERE levelcode = 'SS1';
UPDATE levels SET levelorder = 21 WHERE levelcode = 'BS1';
UPDATE levels SET levelorder = 22 WHERE levelcode = 'BS2';
UPDATE levels SET levelorder = 23 WHERE levelcode = 'BS3';
UPDATE levels SET levelorder = 24 WHERE levelcode = 'BS4';
UPDATE levels SET levelorder = 25 WHERE levelcode = 'BS5';
UPDATE levels SET levelorder = 26 WHERE levelcode = 'BS6';
UPDATE levels SET levelorder = 27 WHERE levelcode = 'BS7';
UPDATE levels SET levelorder = 28 WHERE levelcode = 'BS8';
UPDATE levels SET levelorder = 31 WHERE levelcode = 'AD1';
UPDATE levels SET levelorder = 32 WHERE levelcode = 'AD2';
UPDATE levels SET levelorder = 33 WHERE levelcode = 'AD3';
UPDATE levels SET levelorder = 34 WHERE levelcode = 'AD4';
UPDATE levels SET levelorder = 41 WHERE levelcode = 'FS1';
UPDATE levels SET levelorder = 42 WHERE levelcode = 'FS2';
UPDATE levels SET levelorder = 43 WHERE levelcode = 'FS3';
UPDATE levels SET levelorder = 44 WHERE levelcode = 'FS4';
UPDATE levels SET levelorder = 45 WHERE levelcode = 'FS5';
UPDATE levels SET levelorder = 46 WHERE levelcode = 'FS6';

-- people -- add a new maxlevel column to track the highest level obtained
ALTER TABLE people
    ADD COLUMN maxlevel VARCHAR(60),
    ADD CONSTRAINT people_level_fk FOREIGN KEY (maxlevel) REFERENCES levels;
    
-- rosterpeople -- add the maxlevel field to the view
DROP VIEW rosterpeople;
CREATE OR REPLACE VIEW rosterpeople AS 
 SELECT r.rosterid, r.classid, r.pid, r.levelPassed, r.paymentid, 
        r.payment_amount, y.paypal_status, r.section, r.date_updated, 
        p.surname, p.givenname, p.maxlevel
   FROM roster r, people p, payment y
  WHERE r.pid = p.pid
    AND r.paymentid = y.paymentid
  ORDER BY r.section, r.classid, p.surname, p.givenname;

-- peoplelevel -- a view of people showing highest level passed 
CREATE OR REPLACE VIEW peoplelevel AS
 SELECT p.pid, l.levelcode, pl.levelorder
   FROM people p, levels l,
        (SELECT r.pid, max(l.levelorder) as levelorder
           FROM roster r, levels l
          WHERE r.levelpassed = l.levelcode
          GROUP BY r.pid) AS pl
  WHERE p.pid = pl.pid
    AND l.levelorder = pl.levelorder;
