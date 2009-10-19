--  '$RCSfile$'
--  Copyright: 2008 Matthew B. Jones
--
--   '$Author: jones $'
--     '$Date: 2008-07-06 20:25:34 -0800 (Sun, 06 Jul 2008) $'
-- '$Revision: 4080 $'

-- People -- table to store data about each person in JSC
CREATE SEQUENCE person_id_seq START 1000;
CREATE TABLE people (
	pid INT8 default nextval('person_id_seq'), -- the unique node id (pk)
	username VARCHAR(250),   -- the unique username for this account
	surname VARCHAR(250),	 -- the surname of this person
    givenname VARCHAR(250),  -- the givenname of this person
    middlename VARCHAR(250), -- the middle name of this person
	birthdate DATE,          -- the person's birthday
    password VARCHAR(250),   -- the password for this user
    role INT2,               -- coded role, 1 is coach, 2 is admin, 3 is both
	email VARCHAR(250),	     -- the email of this person
    home_phone VARCHAR(250), -- the phone of this person
    cell_phone VARCHAR(250), -- the phone of this person
    work_phone VARCHAR(250), -- the phone of this person
    street1 VARCHAR(250),    -- street address
    street2 VARCHAR(250),    -- continued street address
    city VARCHAR(250),       -- city
    state VARCHAR(250),      -- state FIPS code, 2 chars
    zipcode VARCHAR(250),    -- zip code
    usfsaid VARCHAR(250),    -- identifier for USFSA
    parentSurname VARCHAR(250), -- Last name of the parent
    parentFirstname VARCHAR(250), -- First name of the parent
    parentEmail VARCHAR(250), -- email of the parent
	date_updated TIMESTAMP default CURRENT_TIMESTAMP, -- the date the record was last updated
   CONSTRAINT people_pk PRIMARY KEY (pid),
   CONSTRAINT username_uk UNIQUE (username)
);

-- Index of surname
CREATE INDEX people_idx1 ON people (surname);

-- Sessions -- table to store the list of sessions held each year
CREATE SEQUENCE session_id_seq START 5000;
CREATE TABLE sessions (
	sid INT8 default nextval('session_id_seq'), -- the unique node id (pk)
    sessionname VARCHAR(20),  -- the label for this session (e.g., 'Session 2')
    season VARCHAR(20),       -- the name of the season (e.g., '2008-2009')
	startdate DATE,           -- the date the session starts
	enddate DATE,             -- the date the session ends
	activesession BOOLEAN,    -- flag indicating whether this is the active session for registration
   CONSTRAINT session_pk PRIMARY KEY (sid)
);

-- Indices of sessions and season
CREATE INDEX session_idx1 ON sessions (sessionname);
CREATE INDEX session_idx2 ON sessions (season);

-- ClassTypes -- the skatingclass types that can be registered
CREATE TABLE classtypes (
	classtype VARCHAR(60),        -- the name for this class
    classdescription VARCHAR(60), -- the description for this class
    requireslevel VARCHAR(60),    -- the level needed to enroll in class
   CONSTRAINT classtypes_pk PRIMARY KEY (classtype)
);

-- Classes -- table to store the list of Classes in each session
CREATE SEQUENCE class_id_seq START 7000;
CREATE TABLE skatingclass (
	classid INT8 default nextval('class_id_seq'), -- the unique node id (pk)
    sid INT8,                -- the id of the session for this class
    classtype VARCHAR(40),   -- the name for this class (e.g., 'Youth Basic')
    day VARCHAR(40),   -- the day on which the class is held
    timeslot VARCHAR(40),   -- the time slot during which the class is held
    instructorid INT8,       -- the id of the instructor for this class
    cost FLOAT8,             -- the cost of registering for this class
    otherinstructors VARCHAR(40),   -- list of surnames of other instructors
	date_updated TIMESTAMP default CURRENT_TIMESTAMP,       -- the date the record was last updated
   CONSTRAINT class_pk PRIMARY KEY (classid),
   CONSTRAINT class_session_fk FOREIGN KEY (sid) REFERENCES sessions,
   CONSTRAINT class_instructor_fk FOREIGN KEY (instructorid) REFERENCES people
);

-- Sessionclasses -- a view over the session and skatingclass tables joined
CREATE OR REPLACE VIEW sessionclasses AS 
 SELECT s.sid, s.sessionname, s.season, s.startdate, s.enddate, s.activesession, c.classid, 
        c.classtype, c.day, c.timeslot, c.instructorid, c.cost, 
        c.otherinstructors, p.surname, p.givenname
   FROM sessions s, skatingclass c, people p
  WHERE c.sid = s.sid
    AND p.pid = c.instructorid;
  
-- Levels -- the ASFS levels that can be passed
CREATE TABLE levels (
	levelcode VARCHAR(60),    -- the code for this level
    levelname VARCHAR(60),    -- the name for this level
   CONSTRAINT levels_pk PRIMARY KEY (levelcode)
);

-- payment -- table to store the list of payments made for transactions
CREATE SEQUENCE payment_id_seq START 50000;
CREATE TABLE payment (
	paymentid INT8 default nextval('payment_id_seq'), -- the identifier of this payment
	payment_date DATE,         -- the date the payment was made
    paypal_tx_id VARCHAR(20),  -- the transaction id from paypal
    paypal_gross FLOAT8,       -- the gross amount actually paid at paypal
    discount FLOAT8,           -- the discount amount above gross that we waived
    paypal_fee FLOAT8,         -- the amount of the fee at paypal
    paypal_net FLOAT8,         -- the net amount to the organization (gross-fee=net amount to us)
    paypal_status VARCHAR(20), -- the status of the payment at PayPal
    payer_email VARCHAR(60),   -- the email address of the payer
    payer_id VARCHAR(60),      -- the PayPal ID of the payer
	date_updated TIMESTAMP default CURRENT_TIMESTAMP, -- the date the record was last updated
   CONSTRAINT payment_pk PRIMARY KEY (paymentid)
);

-- Rosters -- table to store the list of students enrolled in a class
CREATE SEQUENCE roster_id_seq START 10000;
CREATE TABLE roster (
	rosterid INT8 default nextval('roster_id_seq'), -- the identifier of this entry in the roster
	classid INT8,            -- the id of the class for this roster
    pid INT8,                -- the id of the person enrolled
    paymentid INT8,          -- the id of the payment for this roster entry
    payment_amount FLOAT8,   -- the amount paid for this single roster entry, excluding discounts
    section VARCHAR(8),      -- the section designator for this entry
    levelPassed VARCHAR(20), -- the ASFS Level passed during testing
   	date_updated TIMESTAMP default CURRENT_TIMESTAMP, -- the date the record was last updated
   CONSTRAINT roster_pk PRIMARY KEY (rosterid),
   CONSTRAINT roster_uk UNIQUE (classid,pid),
   CONSTRAINT roster_class_fk FOREIGN KEY (classid) REFERENCES skatingclass,
   CONSTRAINT roster_student_fk FOREIGN KEY (pid) REFERENCES people,
   CONSTRAINT roster_payment_fk FOREIGN KEY (paymentid) REFERENCES payment,
   CONSTRAINT roster_level_fk FOREIGN KEY (levelPassed) REFERENCES levels
);

-- rosterpeople -- a view over the roster and person tables joined showing selected fields
CREATE OR REPLACE VIEW rosterpeople AS 
 SELECT r.rosterid, r.classid, r.pid, r.levelPassed, r.paymentid, 
        r.payment_amount, y.paypal_status, r.section, r.date_updated, 
        p.surname, p.givenname
   FROM roster r, people p, payment y
  WHERE r.pid = p.pid
    AND r.paymentid = y.paymentid
  ORDER BY r.section, r.classid, p.surname, p.givenname;

-- membership -- table to store the memberships of students each season
CREATE SEQUENCE membership_id_seq START 50000;
CREATE TABLE membership (
	mid INT8 default nextval('membership_id_seq'), -- the identifier of this membership
	pid INT8,                -- the id of the person who is the member
    paymentid INT8,          -- the id of the payment for this membership
    season VARCHAR(20),      -- the name of the season (e.g., '2008-2009')
	date_updated TIMESTAMP default CURRENT_TIMESTAMP, -- the date the record was last updated
   CONSTRAINT membership_pk PRIMARY KEY (mid),
   CONSTRAINT membership_uk UNIQUE (pid,season),
   CONSTRAINT membership_payment_fk FOREIGN KEY (paymentid) REFERENCES payment
);

-- memberstatus -- a view of membership reflecting payment status
CREATE OR REPLACE VIEW memberstatus AS
 SELECT m.mid, m.pid, m.paymentid, m.season, p.paypal_status
   FROM membership m, payment p
  WHERE m.paymentid = p.paymentid;
