-- Departments
INSERT INTO department (id, name, acronym, active, created_at)
SELECT 10, 'Financeiro', 'FIN', TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM department d WHERE d.id = 10);

INSERT INTO department (id, name, acronym, active, created_at)
SELECT 11, 'Recursos Humanos', 'RH', TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM department d WHERE d.id = 11);

INSERT INTO department (id, name, acronym, active, created_at)
SELECT 12, 'Compliance', 'COMP', TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM department d WHERE d.id = 12);

INSERT INTO department (id, name, acronym, active, created_at)
SELECT 13, 'Operações', 'OPS', TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM department d WHERE d.id = 13);

INSERT INTO department (id, name, acronym, active, created_at)
SELECT 14, 'Jurídico', 'JUR', TRUE, NOW()
WHERE NOT EXISTS (SELECT 1 FROM department d WHERE d.id = 14);

SELECT setval(pg_get_serial_sequence('department', 'id'), COALESCE((SELECT MAX(id) FROM department), 1));
SELECT setval(pg_get_serial_sequence('user_info', 'id'), COALESCE((SELECT MAX(id) FROM public.user_info), 1));

-- Users
INSERT INTO public.user_info (active, created_at, updated_at, email, name, username, password, role, department_id)
SELECT
    TRUE,
    NOW(),
    NULL,
    'reclamante@teste.com',
    'Usuário Reclamante',
    'reclamante',
    '$2a$10$VLNo0bK7iyPRY8myjbqJMuZoBO/82pCqzEUfXdLiJWFEYqndVmtya',
    'REMONSTRANT',
    11
WHERE NOT EXISTS (SELECT 1 FROM public.user_info u WHERE u.username = 'reclamante');

INSERT INTO public.user_info (active, created_at, updated_at, email, name, username, password, role, department_id)
SELECT
    TRUE,
    NOW(),
    NULL,
    'ouvidor@teste.com',
    'Ouvidor',
    'ouvidor',
    '$2a$10$VLNo0bK7iyPRY8myjbqJMuZoBO/82pCqzEUfXdLiJWFEYqndVmtya',
    'LISTENER',
    12
WHERE NOT EXISTS (SELECT 1 FROM public.user_info u WHERE u.username = 'ouvidor');

INSERT INTO public.user_info (active, created_at, updated_at, email, name, username, password, role, department_id)
SELECT
    TRUE,
    NOW(),
    NULL,
    'gestor@teste.com',
    'Gestor',
    'gestor',
    '$2a$10$VLNo0bK7iyPRY8myjbqJMuZoBO/82pCqzEUfXdLiJWFEYqndVmtya',
    'MANAGER',
    10
WHERE NOT EXISTS (SELECT 1 FROM public.user_info u WHERE u.username = 'gestor');

-- Reports
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public' AND table_name = 'report' AND column_name = 'status'
    ) THEN
        EXECUTE $q$
            INSERT INTO report (active, created_at, updated_at, title, description, status, date_of_occurrence, user_info_id)
            SELECT
                TRUE,
                NOW(),
                NULL,
                r.title,
                r.description,
                'PENDING',
                r.date,
                u.id
            FROM (
                     VALUES
                         ('Suspeita de favorecimento em contratação',
                          'Observei possível favorecimento em um processo de contratação de fornecedor...',
                          DATE '2026-01-15'),
                         ('Reclamação sobre demora em atendimento do RH',
                          'Tenho aguardado retorno sobre uma solicitação há semanas...',
                          DATE '2026-02-03'),
                         ('Sugestão de melhoria no canal de denúncias',
                          'Seria útil receber um status de acompanhamento por etapas...',
                          DATE '2026-03-10'),
                         ('Elogio ao atendimento da área de Compliance',
                          'Fui orientado com clareza e rapidez...',
                          DATE '2026-03-18'),
                         ('Solicitação de orientações sobre política de brindes',
                          'Gostaria de confirmar limites e procedimentos...',
                          DATE '2026-04-01')
                 ) AS r(title, description, date)
                     JOIN public.user_info u ON u.username = 'reclamante'
            WHERE NOT EXISTS (
                SELECT 1 FROM report rep
                WHERE rep.title = r.title AND rep.user_info_id = u.id
            )
        $q$;
    ELSE
        EXECUTE $q$
            INSERT INTO report (active, created_at, updated_at, title, description, date_of_occurrence, user_info_id)
            SELECT
                TRUE,
                NOW(),
                NULL,
                r.title,
                r.description,
                r.date,
                u.id
            FROM (
                     VALUES
                         ('Suspeita de favorecimento em contratação',
                          'Observei possível favorecimento em um processo de contratação de fornecedor...',
                          DATE '2026-01-15'),
                         ('Reclamação sobre demora em atendimento do RH',
                          'Tenho aguardado retorno sobre uma solicitação há semanas...',
                          DATE '2026-02-03'),
                         ('Sugestão de melhoria no canal de denúncias',
                          'Seria útil receber um status de acompanhamento por etapas...',
                          DATE '2026-03-10'),
                         ('Elogio ao atendimento da área de Compliance',
                          'Fui orientado com clareza e rapidez...',
                          DATE '2026-03-18'),
                         ('Solicitação de orientações sobre política de brindes',
                          'Gostaria de confirmar limites e procedimentos...',
                          DATE '2026-04-01')
                 ) AS r(title, description, date)
                     JOIN public.user_info u ON u.username = 'reclamante'
            WHERE NOT EXISTS (
                SELECT 1 FROM report rep
                WHERE rep.title = r.title AND rep.user_info_id = u.id
            )
        $q$;
    END IF;
END $$;

-- Protocol
UPDATE report
SET protocol_number = 'PM' || id
WHERE protocol_number IS NULL;

-- Processed reports
INSERT INTO report_processed (active, created_at, updated_at, title_anonymized, description_anonymized, category, risk, status, has_conflict, report_id)
SELECT
    TRUE,
    NOW(),
    NULL,
    r.title,
    r.description,
    CASE r.title
        WHEN 'Suspeita de favorecimento em contratação' THEN 'DENUNCIATION'
        WHEN 'Reclamação sobre demora em atendimento do RH' THEN 'COMPLAINT'
        WHEN 'Sugestão de melhoria no canal de denúncias' THEN 'SUGGESTION'
        WHEN 'Elogio ao atendimento da área de Compliance' THEN 'COMPLIMENT'
        ELSE 'REQUEST'
        END,
    CASE r.title
        WHEN 'Suspeita de favorecimento em contratação' THEN 'HIGH'
        WHEN 'Reclamação sobre demora em atendimento do RH' THEN 'MEDIUM'
        ELSE 'LOW'
        END,
    'PENDING',
    FALSE,
    r.id
FROM report r
         JOIN public.user_info u ON r.user_info_id = u.id
WHERE u.username = 'reclamante'
  AND NOT EXISTS (
    SELECT 1 FROM report_processed rp WHERE rp.report_id = r.id
);

-- Sequences
SELECT setval(pg_get_serial_sequence('department', 'id'), COALESCE(MAX(id), 1)) FROM department;
SELECT setval(pg_get_serial_sequence('user_info', 'id'), COALESCE(MAX(id), 1)) FROM public.user_info;
SELECT setval(pg_get_serial_sequence('report', 'id'), COALESCE(MAX(id), 1)) FROM report;
SELECT setval(pg_get_serial_sequence('report_processed', 'id'), COALESCE(MAX(id), 1)) FROM report_processed;
