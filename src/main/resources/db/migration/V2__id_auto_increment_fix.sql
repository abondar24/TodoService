ALTER TABLE item DROP COLUMN id;
ALTER TABLE item ADD COLUMN id IDENTITY PRIMARY KEY NOT NULL;