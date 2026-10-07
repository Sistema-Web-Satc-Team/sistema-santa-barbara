
CREATE TABLE IF NOT EXISTS file (
    id UUID,
    storage_key VARCHAR(256) NOT NULL,
    filename VARCHAR(128) NOT NULL,
    content_type VARCHAR(64) NOT NULL,

    CONSTRAINT pk_file PRIMARY key (id)
);

ALTER TABLE partitura
    ADD COLUMN partitura_file_id UUID;

ALTER TABLE membro
    ADD COLUMN avatar_file_id UUID;


ALTER TABLE partitura
    ADD CONSTRAINT fk_partitura_file
        FOREIGN KEY (partitura_file_id)
        REFERENCES file(id);

ALTER TABLE membro
    ADD CONSTRAINT fk_membro_avatar_file
        FOREIGN KEY (avatar_file_id)
        REFERENCES file(id);