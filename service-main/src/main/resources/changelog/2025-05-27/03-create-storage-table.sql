-- CREATE STORAGE TABLE
CREATE TABLE storage (
                         id BIGSERIAL PRIMARY KEY,
                         beer VARCHAR(100) NOT NULL,
                         store VARCHAR(100) NOT NULL,
                         count BIGINT NOT NULL,

                         CONSTRAINT fk_storage_beer FOREIGN KEY (beer) REFERENCES BEER(id),
                         CONSTRAINT fk_storage_store FOREIGN KEY (store) REFERENCES STORE(id)
);