CREATE TABLE file (
    id UUID,
    storage_key VARCHAR(256) NOT NULL,
    filename VARCHAR(128) NOT NULL,
    content_type VARCHAR(64) NOT NULL,

    CONSTRAINT pk_file PRIMARY KEY (id)
);

