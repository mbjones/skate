
-- Query to find the current list of registrants in the whole database
select rp.surname, rp.givenname, sc.classtype, sc.day, rp.paypal_status 
  from rosterpeople rp, sessionclasses sc 
 where rp.classid = sc.classid 
   and sc.season = '2009-2010'
   and sc.sessionname = '1'
 order by classtype, surname, givenname;

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
    AND sc.sessionname = '1'
  ORDER BY y.paypal_status, sc.classtype, p.surname, p.givenname;

   SELECT p.surname||','|| p.givenname||','||p.levelpassed||','||sc.classtype||','||sc.day||','||y.paypal_status
   FROM roster r, peoplelevel p, payment y, sessionclasses sc
  WHERE r.pid = p.pid 
    AND r.paymentid = y.paymentid
    AND r.classid = sc.classid
    AND sc.season = '2009-2010'
    AND sc.sessionname = '1'
  ORDER BY y.paypal_status, sc.classtype, p.surname, p.givenname;
  
-- Query to create a mailing list
SELECT givenname|| ' ' || surname || ' <' || email || '>' from people
UNION
SELECT parentfirstname|| ' ' || parentsurname || ' <' || parentemail || '>' from people;

