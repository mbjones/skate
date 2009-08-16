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
INSERT INTO classtypes (classtype, classdescription, requireslevel) VALUES ('FS MITF','TBD','SS1');
INSERT INTO classtypes (classtype, classdescription, requireslevel) VALUES ('FS Artistry','TBD','SS1');
INSERT INTO classtypes (classtype, classdescription, requireslevel) VALUES ('FS Synchro','TBD','SS1');
INSERT INTO classtypes (classtype, classdescription, requireslevel) VALUES ('Hockey','TBD','SS1');

INSERT INTO sessions (sessionname, season, startdate, enddate, date_updated)
     VALUES ('1', '2009-2010', '2009-09-15', '2009-10-31', '2009-08-20');
INSERT INTO sessions (sessionname, season, startdate, enddate, date_updated)
     VALUES ('2', '2009-2010', '2009-09-15', '2009-10-31', '2009-08-20');
INSERT INTO sessions (sessionname, season, startdate, enddate, date_updated)
     VALUES ('3', '2009-2010', '2009-09-15', '2009-10-31', '2009-08-20');
INSERT INTO sessions (sessionname, season, startdate, enddate, date_updated)
     VALUES ('4', '2009-2010', '2009-09-15', '2009-10-31', '2009-08-20');

INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, 
    otherinstructors, date_updated) VALUES
    (1, 'Beginner Youth', 'Friday', '6-9pm', 1, 'Katie, Pam', '2009-08-20');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, 
    otherinstructors, date_updated) VALUES
    (1, 'Advanced Youth', 'Friday', '6-9pm', 1, 'Katie, Pam', '2009-08-20');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, 
    otherinstructors, date_updated) VALUES
    (1, 'Beginner Adult', 'Friday', '6-9pm', 1, 'Katie, Pam', '2009-08-20');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, 
    otherinstructors, date_updated) VALUES
    (1, 'Advanced Adult', 'Friday', '6-9pm', 1, 'Katie, Pam', '2009-08-20');
