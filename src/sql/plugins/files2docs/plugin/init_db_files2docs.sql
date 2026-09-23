-- liquibase formatted sql
-- changeset files2docs:init_db_files2docs.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- files2docs_mapping
INSERT INTO files2docs_mapping(id_mapping,code_document_type,description,title,summary)
VALUES(1,'article','Mapping du type de document Article',NULL,NULL);

INSERT INTO files2docs_mapping(id_mapping,code_document_type,description,title,summary)
VALUES(4,'actor','Mapping du type de document Fiche acteur',NULL,NULL);

INSERT INTO files2docs_mapping(id_mapping,code_document_type,description,title,summary)
VALUES(6,'image','Mapping du type de document Image',NULL,NULL);

INSERT INTO files2docs_mapping(id_mapping,code_document_type,description,title,summary)
VALUES(7,'pdf','Mapping du type de document PDF',NULL,NULL);

INSERT INTO files2docs_mapping(id_mapping,code_document_type,description,title,summary)
VALUES(8,'video','Mapping du type de document Video',NULL,NULL);
 
-- files2docs_mapping_attribute 
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(1,1,12,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(5,4,30,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(6,4,31,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(16,6,43,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(17,6,44,'<user>');
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(18,7,48,NULL);
