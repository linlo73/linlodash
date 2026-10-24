CREATE TABLE shopping.object (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 name VARCHAR(100) UNIQUE NOT NULL,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 created_by UUID,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 updated_by UUID
);

CREATE TABLE shopping.list (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 name VARCHAR(100) UNIQUE NOT NULL,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 created_by UUID,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 updated_by UUID
);

CREATE TABLE shopping.list_object (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 list_id UUID NOT NULL,
                                 object_id UUID NOT NULL,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 created_by UUID,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 updated_by UUID,
                                 CONSTRAINT fk_list FOREIGN KEY (list_id) REFERENCES shopping.list(id) ON DELETE CASCADE,
                                 CONSTRAINT fk_object FOREIGN KEY (object_id) REFERENCES shopping.object(id) ON DELETE CASCADE
);

CREATE TABLE shopping.list_user (
                                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                      list_id UUID NOT NULL,
                                      user_id UUID NOT NULL,
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      created_by UUID,
                                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      updated_by UUID,
                                      CONSTRAINT fk_list FOREIGN KEY (list_id) REFERENCES shopping.list(id) ON DELETE CASCADE,
                                      CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES public.app_user(id) ON DELETE CASCADE
);