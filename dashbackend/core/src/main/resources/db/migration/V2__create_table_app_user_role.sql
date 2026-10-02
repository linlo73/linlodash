-- public.app_user_roles definition

-- Drop table

-- DROP TABLE public.app_user_roles;

CREATE TABLE public.app_user_role (
    user_id UUID NOT NULL,
    "role" varchar(255) NOT NULL,
    CONSTRAINT app_user_role_pkey PRIMARY KEY (user_id, role),
    CONSTRAINT app_user_role_role_check CHECK (((role)::text = ANY ((ARRAY['USER'::character varying, 'ADMIN'::character varying])::text[])))
);


-- public.app_user_roles foreign keys

ALTER TABLE public.app_user_role ADD CONSTRAINT user_user_role FOREIGN KEY (user_id) REFERENCES public.app_user(id);