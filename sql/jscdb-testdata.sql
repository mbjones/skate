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

-- Default password is encrypted, original is 'sk8gr8t'
INSERT INTO people (surname, givenname, birthdate, password, role, email, home_phone, username) 
     VALUES ('Jones','Matt', '1967-02-09', '$2a$10$1JBlWZRkefGrhAVoxFo3UOflKZtzcW4LwiRIPjGCvSXUKrpWs2xLK', 
     3, 'mbjones.89@gmail.com', '907-789-0496', 'mbjones');
INSERT INTO people (surname, givenname, password, role, email, home_phone, username) 
     VALUES ('Anderson','Lauren', '$2a$10$1JBlWZRkefGrhAVoxFo3UOflKZtzcW4LwiRIPjGCvSXUKrpWs2xLK', 
     1, 'ldipenti@hotmail.com', '', 'landerson');
INSERT INTO people (surname, givenname, middlename, password, role, email, home_phone, username) 
     VALUES ('Siddon','Ebett', 'Calvert', '$2a$10$1JBlWZRkefGrhAVoxFo3UOflKZtzcW4LwiRIPjGCvSXUKrpWs2xLK', 
     1, 'ebett_calvert@hotmail.com', '', 'esiddon');
INSERT INTO people (surname, givenname, middlename, password, role, email, home_phone, username) 
     VALUES ('Dahlberg','Sigrid', '', '$2a$10$1JBlWZRkefGrhAVoxFo3UOflKZtzcW4LwiRIPjGCvSXUKrpWs2xLK', 
     1, 'sdahlberg@carsondorn.com', '', 'sdahlberg');
INSERT INTO people (surname, givenname, middlename, password, role, email, home_phone, username) 
     VALUES ('Green','Pam', '', '$2a$10$1JBlWZRkefGrhAVoxFo3UOflKZtzcW4LwiRIPjGCvSXUKrpWs2xLK', 
     1, 'pammyjo00@yahoo.com', '907-723-2031', 'pgreen');
INSERT INTO people (surname, givenname, middlename, password, role, email, home_phone, username) 
     VALUES ('Geissler','Karla', '', '$2a$10$1JBlWZRkefGrhAVoxFo3UOflKZtzcW4LwiRIPjGCvSXUKrpWs2xLK', 
     1, 'kmgeissler@gmail.com', '907-723-6743', 'kgeisler');
INSERT INTO people (surname, givenname, middlename, password, role, email, home_phone, username) 
     VALUES ('Siddon','Chris', '', '$2a$10$1JBlWZRkefGrhAVoxFo3UOflKZtzcW4LwiRIPjGCvSXUKrpWs2xLK', 
     1, 'chris_siddon@hotmail.com', '', 'csiddon');
INSERT INTO people (surname, givenname, middlename, password, role, email, home_phone, username) 
     VALUES ('Vuille','Wendy', '', '$2a$10$1JBlWZRkefGrhAVoxFo3UOflKZtzcW4LwiRIPjGCvSXUKrpWs2xLK', 
     1, 'wendy.vuille@alaska.gov', '', 'wvuille');
INSERT INTO people (surname, givenname, middlename, password, role, email, home_phone, username) 
     VALUES ('Mix','Kim', '', '$2a$10$1JBlWZRkefGrhAVoxFo3UOflKZtzcW4LwiRIPjGCvSXUKrpWs2xLK', 
     1, 'ak1tourist@hotmail.com', '', 'kmix');
INSERT INTO people (surname, givenname, middlename, password, role, email, home_phone, username) 
     VALUES ('Bishop','Kayla', '', '$2a$10$1JBlWZRkefGrhAVoxFo3UOflKZtzcW4LwiRIPjGCvSXUKrpWs2xLK', 
     1, 'anitab@eagle.ptialaska.net', '', 'kbishop');
INSERT INTO people (surname, givenname, middlename, password, role, email, home_phone, username) 
     VALUES ('Sargent','Alex', '', '$2a$10$1JBlWZRkefGrhAVoxFo3UOflKZtzcW4LwiRIPjGCvSXUKrpWs2xLK', 
     1, 'alaskangrl_87@hotmail.com', '', 'asargent');
     
INSERT INTO sessions (sessionname, season, startdate, enddate)
     VALUES ('1', '2009-2010', '2009-09-21', '2009-11-01');
INSERT INTO sessions (sessionname, season, startdate, enddate)
     VALUES ('2', '2009-2010', '2009-11-09', '2009-12-20');
INSERT INTO sessions (sessionname, season, startdate, enddate)
     VALUES ('3', '2009-2010', '2010-01-11', '2010-02-21');
INSERT INTO sessions (sessionname, season, startdate, enddate)
     VALUES ('4', '2009-2010', '2010-03-01', '2010-04-11');

INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'Snowplow Sam', 'Saturday', '11:45am-12:45pm', 1009, 80.00, 'Alex');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'Beginner Youth', 'Friday', '6:30-7:30pm', 1007, 80.00, 'Alex');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'Beginner Youth', 'Saturday', '11:45am-12:45pm', 1005, 80.00, 'Kim');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'Intermediate Youth', 'Friday', '6:30-7:30pm', 1010, 80.00, '');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'Intermediate Youth', 'Saturday', '11:45am-12:45pm', 1005, 80.00, '');    
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'Advanced Youth', 'Friday', '6:30-7:30pm', 1002, 80.00, '');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost,
    otherinstructors) VALUES
    (5000, 'Beginner Adult', 'Friday', '6:30-7:30pm', 1003, 80.00, '');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost,
    otherinstructors) VALUES
    (5000, 'Beginner Adult', 'Saturday', '11:45am-12:45pm', 1008, 80.00, '');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'Advanced Adult', 'Friday', '6:30-7:30pm', 1003, 80.00, '');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'Hockey Skating Skills', 'Sunday', '10:30-11:30am', 1006, 80.00, '');

INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'FS Moves in the Field', 'Monday', '5:15-6:15pm', 1005, 75.00, '');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'FS Artistry', 'Tuesday', '6:15-7:15am', 1002, 75.00, '');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'FS Club Ice', 'Wednesday', '5:15-6:15pm', 1004, 75.00, '');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'FS Club Ice', 'Friday', '5:15-6:15pm', 1004, 75.00, '');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'FS Synchro', 'Sunday', '9:15-10:15am', 1005, 75.00, '');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5000, 'FS Club Ice', 'Sunday', '9:15-10:15am', 1004, 75.00, '');

-- Session 2 classes
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5001, 'Snowplow Sam', 'Saturday', '11:45am-12:45pm', 1009, 80.00, 'Alex');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5001, 'Beginner Youth', 'Friday', '6:30-7:30pm', 1007, 80.00, 'Alex');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5001, 'Beginner Youth', 'Saturday', '11:45am-12:45pm', 1005, 80.00, 'Kim');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5001, 'Intermediate Youth', 'Friday', '6:30-7:30pm', 1010, 80.00, '');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5001, 'Intermediate Youth', 'Saturday', '11:45am-12:45pm', 1005, 80.00, '');    
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5001, 'Advanced Youth', 'Friday', '6:30-7:30pm', 1002, 80.00, '');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost,
    otherinstructors) VALUES
    (5001, 'Beginner Adult', 'Friday', '6:30-7:30pm', 1003, 80.00, '');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost,
    otherinstructors) VALUES
    (5001, 'Beginner Adult', 'Saturday', '11:45am-12:45pm', 1008, 80.00, '');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5001, 'Advanced Adult', 'Friday', '6:30-7:30pm', 1003, 80.00, '');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5001, 'Hockey Skating Skills', 'Sunday', '10:30-11:30am', 1006, 80.00, '');

INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5001, 'FS Moves in the Field', 'Monday', '5:15-6:15pm', 1005, 75.00, '');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5001, 'FS Artistry', 'Tuesday', '6:15-7:15am', 1002, 75.00, '');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5001, 'FS Club Ice', 'Wednesday', '5:15-6:15pm', 1004, 75.00, '');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5001, 'FS Club Ice', 'Friday', '5:15-6:15pm', 1004, 75.00, '');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5001, 'FS Synchro', 'Sunday', '9:15-10:15am', 1005, 75.00, '');
INSERT INTO skatingclass (sid, classtype, day, timeslot, instructorid, cost, 
    otherinstructors) VALUES
    (5001, 'FS Club Ice', 'Sunday', '9:15-10:15am', 1004, 75.00, '');
    