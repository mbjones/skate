-- membership -- add a column for tracking membership amounts
ALTER TABLE membership
    ADD COLUMN payment_amount FLOAT8;