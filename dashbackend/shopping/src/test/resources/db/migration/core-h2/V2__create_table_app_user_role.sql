CREATE TABLE public.app_user_role (
    user_id UUID NOT NULL,
    "role" varchar(255) NOT NULL,
    CONSTRAINT app_user_role_pkey PRIMARY KEY (user_id, role),
    CONSTRAINT app_user_role_role_check CHECK ("role" IN ('USER', 'ADMIN')),
    CONSTRAINT user_user_role FOREIGN KEY (user_id) REFERENCES public.app_user(id)
);
