DROP TABLE organization IF EXISTS;

CREATE TABLE organization  (
    org_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    org_name VARCHAR(20),
    org_address VARCHAR(20)
);
