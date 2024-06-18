ALTER TABLE notes
    ADD COLUMN userId UUID;

ALTER TABLE notes
    ADD CONSTRAINT fk_notes_userId
        FOREIGN KEY (userId) REFERENCES users(id);
