CREATE TABLE IF NOT EXISTS item(
   id BIGINT NOT NULL,
   description VARCHAR(255) NOT NULL,
   status VARCHAR(10) NOT NULL,
   created_date TIMESTAMP NOT NULL,
   due_date TIMESTAMP NOT NULL,
   completed_date TIMESTAMP
);
