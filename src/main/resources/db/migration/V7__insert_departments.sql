UPDATE department
SET acronym = 'IT'
WHERE id = 1 AND (acronym IS NULL OR acronym = '');

INSERT INTO department (id, name, acronym, active, created_at)
VALUES (2, 'Instituto Metrópole Digital', 'IMD', TRUE, NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO department (id, name, acronym, active, created_at)
VALUES (3, 'Reitoria', 'REI', TRUE, NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO department (id, name, acronym, active, created_at)
VALUES (4, 'Superintendência de Informática', 'SINFO', TRUE, NOW())
ON CONFLICT (id) DO NOTHING;
