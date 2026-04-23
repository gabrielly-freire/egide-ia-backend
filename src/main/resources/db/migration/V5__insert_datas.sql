INSERT INTO department (id, name)
SELECT 1, 'IT'
WHERE NOT EXISTS (SELECT 1 FROM department d WHERE d.id = 1);

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM public.user_info u WHERE u.username = 'admin') THEN
        RETURN;
    END IF;

    IF EXISTS (SELECT 1 FROM public.user_info u WHERE u.id = 1) THEN
        INSERT INTO public.user_info (active, created_at, updated_at, email, name, username, password, role, department_id)
        VALUES (TRUE, NOW(), NULL, 'admin@teste.com', 'Administrador', 'admin', '$2a$10$VLNo0bK7iyPRY8myjbqJMuZoBO/82pCqzEUfXdLiJWFEYqndVmtya', 'ADMIN', 1);
    ELSE
        INSERT INTO public.user_info (id, active, created_at, updated_at, email, name, username, password, role, department_id)
        VALUES (1, TRUE, NOW(), NULL, 'admin@teste.com', 'Administrador', 'admin', '$2a$10$VLNo0bK7iyPRY8myjbqJMuZoBO/82pCqzEUfXdLiJWFEYqndVmtya', 'ADMIN', 1);
    END IF;
END $$;
