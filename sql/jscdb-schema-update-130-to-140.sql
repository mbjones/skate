-- rosterpeople -- add session information
DROP VIEW rosterpeople;
CREATE OR REPLACE VIEW rosterpeople AS 
 SELECT r.rosterid, r.classid, r.pid, r.levelPassed, r.paymentid, 
        r.payment_amount, y.paypal_status, r.section, r.date_updated, 
        p.surname, p.givenname, p.maxlevel, sc.season, sc.sessionname, sc.classtype
   FROM roster r, people p, payment y, sessionclasses sc
  WHERE r.pid = p.pid
    AND r.paymentid = y.paymentid
    AND r.classid = sc.classid
  ORDER BY r.section, r.classid, p.surname, p.givenname;
