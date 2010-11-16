-- classtypes -- add a new sortorder column
ALTER TABLE classtypes
    ADD COLUMN sortorder INT8;

-- skatingclass -- add a new foreign key
ALTER TABLE skatingclass
    ADD CONSTRAINT type_fk FOREIGN KEY (classtype) REFERENCES classtypes;