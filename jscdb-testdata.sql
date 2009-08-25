INSERT INTO levels (levelcode, levelname) VALUES ('Snowplow Sam', 'SS1');
INSERT INTO levels (levelcode, levelname) VALUES ('Basic Skills 1', 'BS1');
INSERT INTO levels (levelcode, levelname) VALUES ('Basic Skills 2', 'BS2');
INSERT INTO levels (levelcode, levelname) VALUES ('Basic Skills 3', 'BS3');
INSERT INTO levels (levelcode, levelname) VALUES ('Basic Skills 4', 'BS4');
INSERT INTO levels (levelcode, levelname) VALUES ('Basic Skills 5', 'BS5');
INSERT INTO levels (levelcode, levelname) VALUES ('Basic Skills 6', 'BS6');
INSERT INTO levels (levelcode, levelname) VALUES ('Basic Skills 7', 'BS7');
INSERT INTO levels (levelcode, levelname) VALUES ('Basic Skills 8', 'BS8');
INSERT INTO levels (levelcode, levelname) VALUES ('Adult 1', 'AD1');
INSERT INTO levels (levelcode, levelname) VALUES ('Adult 2', 'AD2');
INSERT INTO levels (levelcode, levelname) VALUES ('Adult 3', 'AD3');
INSERT INTO levels (levelcode, levelname) VALUES ('Adult 4', 'AD4');
INSERT INTO levels (levelcode, levelname) VALUES ('Free Skate 1', 'FS1');
INSERT INTO levels (levelcode, levelname) VALUES ('Free Skate 2', 'FS2');
INSERT INTO levels (levelcode, levelname) VALUES ('Free Skate 3', 'FS3');
INSERT INTO levels (levelcode, levelname) VALUES ('Free Skate 4', 'FS4');
INSERT INTO levels (levelcode, levelname) VALUES ('Free Skate 5', 'FS5');
INSERT INTO levels (levelcode, levelname) VALUES ('Free Skate 6', 'FS6');

INSERT INTO classtypes (classtype, classdescription, requireslevel) VALUES ('Beginner Youth','TBD','SS1');
INSERT INTO classtypes (classtype, classdescription, requireslevel) VALUES ('Intermediate Youth','TBD','SS1');
INSERT INTO classtypes (classtype, classdescription, requireslevel) VALUES ('Advanced Youth','TBD','SS1');
INSERT INTO classtypes (classtype, classdescription, requireslevel) VALUES ('Beginner Adult','TBD','SS1');
INSERT INTO classtypes (classtype, classdescription, requireslevel) VALUES ('Advanced Adult','TBD','SS1');
INSERT INTO classtypes (classtype, classdescription, requireslevel) VALUES ('FS Club Ice','TBD','SS1');
INSERT INTO classtypes (classtype, classdescription, requireslevel) VALUES ('FS Moves in the Field','TBD','SS1');
INSERT INTO classtypes (classtype, classdescription, requireslevel) VALUES ('FS Power and Artistry','TBD','SS1');
INSERT INTO classtypes (classtype, classdescription, requireslevel) VALUES ('FS Synchro','TBD','SS1');
INSERT INTO classtypes (classtype, classdescription, requireslevel) VALUES ('Hockey','TBD','SS1');
    
--INSERT INTO people (surname, givenname, middlename, birthdate, password, role,)
--     email, home_phone, cell_phone, work_phone, street1, street2, city, state,
--     zipcode, usafsaid, parentSurname, parentFirstname, parentEmail) VALUES
      
INSERT INTO people (surname, givenname, birthdate, password, role,
     email, home_phone) VALUES ('Anderson','Lauren', '1988-10-08', 'foo', 
     1, 'lauren@jsc.org', '907-789-0000');
INSERT INTO people (surname, givenname, birthdate, password, role,
     email, home_phone) VALUES ('Jones','Matt', '1988-10-08', 'foo', 
     3, 'mbjones.89@gmail.com', '907-789-0000');
     
INSERT INTO sessions (sessionname, season, startdate, enddate)
     VALUES ('1', '2009-2010', '2009-09-15', '2009-10-31');
INSERT INTO sessions (sessionname, season, startdate, enddate)
     VALUES ('2', '2009-2010', '2009-11-01', '2009-12-31');
INSERT INTO sessions (sessionname, season, startdate, enddate)
     VALUES ('3', '2009-2010', '2010-01-07', '2010-02-15');
INSERT INTO sessions (sessionname, season, startdate, enddate)
     VALUES ('4', '2009-2010', '2010-02-16', '2010-04-15');

INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'Snowplow Sam', 'Saturday', '11:45am-12:45pm', 1000, 80.00, 'Sigrid');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'Beginner Youth', 'Friday', '6:30-7:30pm', 1000, 80.00, 'Katie, Pam');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'Beginner Youth', 'Saturday', '11:45am-12:45pm', 1000, 80.00, 'Katie, Pam');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'Intermediate Youth', 'Friday', '6:30-7:30pm', 1000, 80.00, 'Katie, Pam');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'Intermediate Youth', 'Saturday', '11:45am-12:45pm', 1000, 80.00, 'Katie, Pam');    
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'Advanced Youth', 'Friday', '6:30-7:30pm', 1000, 80.00, 'Katie, Pam');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'Advanced Youth', 'Saturday', '11:45am-12:45pm', 1000, 80.00, 'Katie, Pam');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost,
    otherinstructors) VALUES
    (5000, 'Beginner Adult', 'Friday', '6:30-7:30pm', 1000, 80.00, 'Katie, Pam');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost,
    otherinstructors) VALUES
    (5000, 'Beginner Adult', 'Saturday', '11:45am-12:45pm', 1000, 80.00, 'Katie, Pam');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'Advanced Adult', 'Friday', '6:30-7:30pm', 1000, 80.00, 'Katie, Pam');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'Advanced Adult', 'Saturday', '11:45am-12:45pm', 1000, 80.00, 'Katie, Pam');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'Hockey Skating Skills', 'Sunday', '10:30-11:30am', 1000, 80.00, 'Chris');

INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'FS Moves in the Field', 'Monday', '5:15-6:15pm', 1000, 75.00, 'Ebett');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'FS Artistry', 'Tuesday', '6:15-7:15am', 1000, 75.00, 'Ebett');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'FS Club Ice', 'Wednesday', '5:15-6:15pm', 1000, 75.00, 'Ebett');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'FS Club Ice', 'Friday', '5:15-6:15pm', 1000, 75.00, 'Ebett');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'FS Synchro', 'Sunday', '9:15-10:15am', 1000, 75.00, 'Ebett');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'FS Club Ice', 'Sunday', '9:15-10:15am', 1000, 75.00, 'Ebett');