-- TransitIQ seed data. Run AFTER schema.sql.
-- Safe to re-run: existing rows are left untouched.

INSERT INTO trains (id, type, speed, base_fare, express_stops) VALUES
    ('L101', 'LOCAL',   60, 10.00, NULL),
    ('E201', 'EXPRESS', 100, 20.00, '2,7,9,10,15')
ON CONFLICT (id) DO NOTHING;
