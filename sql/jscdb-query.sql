
-- Query to find the current list of registrants in the whole database
select rp.surname, rp.givenname, sc.classtype, sc.day, rp.paypal_status 
  from rosterpeople rp, sessionclasses sc 
 where rp.classid = sc.classid 
   and sc.season = '2009-2010'
   and sc.sessionname = '1'
 order by classtype, surname, givenname;

-- Report of the number of people in each class
SELECT c.season, c.sessionname as session, c.classtype as class, c.day, count(*) as count
  FROM people p, roster r, sessionclasses c, payment py
 WHERE p.pid = r.pid
   AND r.classid = c.classid
   AND r.paymentid = py.paymentid
   AND py.paypal_status NOT IN ('Pending', 'Refunded')
 GROUP BY c.season, c.sessionname, c.classtype, c.day
 ORDER BY c.season, c.sessionname, c.classtype, c.day;
 
-- Query to find the list of memberships
SELECT ppl.surname, ppl.givenname, m.season, p.paypal_status   
  FROM membership m, payment p, people ppl  
 WHERE m.paymentid = p.paymentid 
   AND m.pid = ppl.pid
   AND season = '2009-2010'
 ORDER BY p.paypal_status, ppl.surname, ppl.givenname;

 --SELECT p.surname, p.givenname, p.email, sc.classtype, sc.day, y.paypal_status
 SELECT p.surname||','|| p.givenname||','||p.email||','||sc.classtype||','||sc.day||','||y.paypal_status
   FROM roster r, people p, payment y, sessionclasses sc
  WHERE r.pid = p.pid 
    AND r.paymentid = y.paymentid
    AND r.classid = sc.classid
    AND sc.season = '2009-2010'
    AND sc.sessionname = '3'
  ORDER BY y.paypal_status, sc.classtype, p.surname, p.givenname;

   SELECT p.surname||','|| p.givenname||','||p.levelpassed||','||
          sc.classtype||','||sc.day||','||y.paypal_status||','||
          y.paypal_tx_id||','||y.paypal_gross||','||y.discount||','||
          y.paypal_fee||','||y.paypal_net||','||y.payment_date
   FROM roster r, peoplelevel p, payment y, sessionclasses sc
  WHERE r.pid = p.pid 
    AND r.paymentid = y.paymentid
    AND r.classid = sc.classid
    AND sc.season = '2009-2010'
    AND sc.sessionname = '3'
  ORDER BY y.paypal_status, sc.classtype, p.surname, p.givenname;
  
 -- Query for USFSA report
 SELECT p.pid||','|| p.surname||','||p.givenname||','||sc.classtype||','||sc.day||','||y.paypal_status||',2009-2010,3'
   FROM roster r, people p, payment y, sessionclasses sc
  WHERE r.pid = p.pid
    AND r.paymentid = y.paymentid
    AND r.classid = sc.classid
    AND sc.season = '2009-2010'
    AND sc.sessionname = '3'
  ORDER BY y.paypal_status, sc.classtype, p.surname, p.givenname;

select DISTINCT p.pid, p.givenname, p.surname, p.birthdate, p.usfsaid, p.street1,
       p.city, p.state, p.zipcode
  from people p, rosterpeople r, sessionclasses c
 where p.pid = r.pid
   and r.classid = c.classid
   and (c.sid = 5002 or c.sid = 5003)
   and r.paypal_status NOT IN ('Pending', 'Refunded')
   and c.classtype NOT LIKE 'FS%'
 EXCEPT
 (select DISTINCT p.pid, p.givenname, p.surname, p.birthdate, p.usfsaid, p.street1,
       p.city, p.state, p.zipcode
  from people p, rosterpeople r, sessionclasses c
 where p.pid = r.pid
   and r.classid = c.classid
   and (c.sid = 5000 or c.sid = 5001)
   and r.paypal_status NOT IN ('Pending', 'Refunded'));
 
select DISTINCT p.pid, p.givenname, p.surname, p.birthdate, p.usfsaid, p.street1,
       p.city, p.state, p.zipcode
  from people p, rosterpeople r, sessionclasses c
 where p.pid = r.pid
   and r.classid = c.classid
   and (c.sid = 5000 or c.sid = 5001)
   and r.paypal_status NOT IN ('Pending', 'Refunded')
   and c.classtype NOT LIKE 'FS%'
 order by p.givenname, p.surname;

 
 -- Roster & Payment Dump for Wendy
 SELECT sc.season,sc.sessionname,p.pid, p.surname,
        p.givenname, sc.classtype, sc.day, y.paypal_status,
        y.paypal_tx_id, y.paypal_gross, y.discount,
        y.paypal_fee, y.paypal_net, y.date_updated
   FROM roster r, people p, payment y, sessionclasses sc
  WHERE r.pid = p.pid
    AND r.paymentid = y.paymentid
    AND r.classid = sc.classid
  ORDER BY sc.season,sc.sessionname, p.date_updated;
  
-- New payment dump for wendy -- one row per transaction
SELECT DISTINCT sc.season,sc.sessionname as session, p.surname,
        p.givenname, y.paypal_status,
        y.paypal_tx_id, y.paypal_gross, y.discount,
        y.paypal_fee, y.paypal_net
   FROM roster r, people p, payment y, sessionclasses sc
  WHERE r.pid = p.pid
    AND r.paymentid = y.paymentid
    AND r.classid = sc.classid
    AND sc.season = '2010-2011'
    AND sc.sessionname = '1'
  ORDER BY sc.season,sc.sessionname, p.surname, p.givenname;

-- Class enrollment summary count for a session
SELECT sc.season,sc.sessionname as session,
        sc.classtype, sc.day, count(*) as count
   FROM roster r, people p, payment y, sessionclasses sc
  WHERE r.pid = p.pid
    AND r.paymentid = y.paymentid
    AND r.classid = sc.classid
    AND sc.season = '2010-2011'
    AND sc.sessionname = '1'
    AND y.paypal_status IN ('Completed', 'Paid Cash', 'Paid Check')
  GROUP BY sc.season, session, sc.classtype, sc.day
  ORDER BY sc.season,sc.sessionname, sc.classtype, sc.day;
  
-- Class enrollment listing for a session
SELECT sc.season,sc.sessionname as session,
        sc.classtype, sc.day, p.surname, p.givenname, y.paypal_status
   FROM roster r, people p, payment y, sessionclasses sc
  WHERE r.pid = p.pid
    AND r.paymentid = y.paymentid
    AND r.classid = sc.classid
    AND sc.season = '2010-2011'
    AND sc.sessionname = '1'
  ORDER BY sc.season,sc.sessionname, sc.classtype, sc.day, p.surname, p.givenname;

-- Query to create a mailing list
SELECT givenname|| ' ' || surname || ' <' || email || '>' from people
UNION
SELECT parentfirstname|| ' ' || parentsurname || ' <' || parentemail || '>' from people;

-- Query to create a mailing list, but for a specific session
SELECT givenname|| ' ' || surname || ' <' || email || '>' from people 
 WHERE (pid in 
       (select r.pid from roster r, skatingclass s 
         where r.classid = s.classid and s.sid = 5002))
UNION
SELECT parentfirstname|| ' ' || parentsurname || ' <' || parentemail || '>' from people
 WHERE (pid in 
       (select r.pid from roster r, skatingclass s 
         where r.classid = s.classid and s.sid = 5003));

-- Update query to batch correct the maxlevel field for people based on the hishest level
-- they have passed in any of their classes
UPDATE people
  SET maxlevel =
      (SELECT levelcode
         FROM peoplelevel
        WHERE peoplelevel.pid = people.pid)
  WHERE EXISTS
      (SELECT peoplelevel.pid
         FROM peoplelevel
        WHERE peoplelevel.pid = people.pid
          AND peoplelevel.levelorder > 0);

-- Find all of the registrants for a given session for sending to Sigrid
SELECT p.pid, p.givenname, p.surname, p.birthdate, p.usfsaid 
  FROM people p, roster r, sessionclasses c 
 WHERE p.pid = r.pid 
   AND r.classid = c.classid 
   AND (c.sid = 5002 or c.sid = 5003) 
 ORDER BY p.pid, c.sid, c.classtype;
 
 -- Select registrants for USFSA membership registration
 SELECT DISTINCT p.usfsaid, p.givenname, p.surname, p.birthdate, p.birthdate, p.street1, p.city, p.state, p.zipcode 
  FROM people p, roster r, sessionclasses c 
 WHERE p.pid = r.pid 
   AND r.classid = c.classid 
   AND (c.sid = 5004) 
 ORDER BY p.usfsaid, p.surname, p.givenname;

 
 -- All FS and Advanced Youth skaters for given session
 SELECT DISTINCT p.givenname, p.surname, p.email, p.parentemail, p.home_phone, p.cell_phone
  FROM people p, roster r, sessionclasses c
 WHERE p.pid = r.pid
   AND r.classid = c.classid
   AND c.sid = 5004
   AND (c.classid in (SELECT classid from sessionclasses where classtype like 'FS%'or classtype like 'Advanced Youth'))
ORDER BY p.surname, p.givenname;

-- A mechanism to update the data in people.usfsaid from a column in another table
UPDATE people
  SET usfsaid = t2.usfsaid
 FROM usfsaid as t2
WHERE people.pid = t2.pid;




