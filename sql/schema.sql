-- TransitIQ database schema.
-- 1. In pgAdmin, create a database named: transitiq
-- 2. Open the Query Tool on that database and run this script first,
--    then run seed.sql.

CREATE TABLE IF NOT EXISTS trains (
    id            VARCHAR(20) PRIMARY KEY,
    type          VARCHAR(10) NOT NULL CHECK (type IN ('LOCAL', 'EXPRESS')),
    speed         INTEGER NOT NULL,
    base_fare     NUMERIC(6,2) NOT NULL,
    express_stops VARCHAR(100)  -- comma-separated station indexes, e.g. '2,7,9'; NULL for local trains
);
