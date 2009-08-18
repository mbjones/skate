--  '$RCSfile$'
--  Copyright: 2008 Matthew B. Jones
--
--   '$Author: jones $'
--     '$Date: 2008-07-06 20:25:34 -0800 (Sun, 06 Jul 2008) $'
-- '$Revision: 4080 $'

-- People -- table to store data about each person in JSC
CREATE SEQUENCE person_id_seq;
CREATE TABLE people (
	pid INT8 default nextval('person_id_seq'), -- the unique node id (pk)
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
   CONSTRAINT people_pk PRIMARY KEY (pid)
);

-- Index of surname
CREATE INDEX people_idx1 ON people (surname);

-- Sessions -- table to store the list of sessions held each year
CREATE SEQUENCE session_id_seq;
CREATE TABLE sessions (
	sid INT8 default nextval('session_id_seq'), -- the unique node id (pk)
    sessionname VARCHAR(20),  -- the label for this session (e.g., 'Session 2')
    season VARCHAR(20),       -- the name of the season (e.g., 'Fall 2008')
	startdate DATE,           -- the date the session starts
	enddate DATE,             -- the date the session ends
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
CREATE SEQUENCE class_id_seq;
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
 SELECT s.sid, s.sessionname, s.season, s.startdate, s.enddate, c.classid, c.classtype, c.day, c.timeslot, c.instructorid
   FROM sessions s, skatingclass c
  WHERE c.sid = s.sid;
  
-- Levels -- the ASFS levels that can be passed
CREATE TABLE levels (
	levelcode VARCHAR(60),    -- the code for this level
    levelname VARCHAR(60),    -- the name for this level
   CONSTRAINT levels_pk PRIMARY KEY (levelcode)
);

-- Rosters -- table to store the list of students enrolled in a class
CREATE SEQUENCE roster_id_seq;
CREATE TABLE roster (
	rosterid INT8 default nextval('roster_id_seq'), -- the identifier of this entry in the roster
	classid INT8,            -- the id of the class for this roster
    pid INT8,                -- the id of the person enrolled
    levelPassed VARCHAR(20), -- the ASFS Level passed during testing
    payment_amount FLOAT8,   -- the amount to be paid
	payment_date DATE,       -- the date the payment was made
    paypal_tx_id VARCHAR(20), -- the transaction id from paypal
    paypal_gross FLOAT8,     -- the gross amount actually paid at paypal
    paypal_fee FLOAT8,       -- the amount of the fee at paypal (gross-fee=net amount to JSC)
    paypal_status VARCHAR(20), -- the status of the payment at PayPal
	date_updated TIMESTAMP default CURRENT_TIMESTAMP, -- the date the record was last updated
   CONSTRAINT roster_pk PRIMARY KEY (rosterid),
   CONSTRAINT roster_uk UNIQUE (classid,pid),
   CONSTRAINT roster_class_fk FOREIGN KEY (classid) REFERENCES skatingclass,
   CONSTRAINT roster_student_fk FOREIGN KEY (pid) REFERENCES people
);

-- Query to select the people in a class
-- SELECT p.*, r.levelpassed, c.classname from roster r, people p, skatingclass c WHERE r.pid = p.pid AND r.classid = c.classid;
