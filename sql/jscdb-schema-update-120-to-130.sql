-- membership -- add a column for tracking membership amounts
ALTER TABLE membership
    ADD COLUMN payment_amount FLOAT8;
    
-- downloads -- names and keys for temporary download files
CREATE TABLE downloads (
	randomkey INT8,          -- the key used to look up file names as needed
    filepath VARCHAR(200),   -- the absolute path of the file
	date_updated TIMESTAMP default CURRENT_TIMESTAMP, -- the date the record was last updated
   CONSTRAINT downloads_pk PRIMARY KEY (randomkey)
);