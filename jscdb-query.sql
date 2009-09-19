
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
   AND season = '2009-2010';