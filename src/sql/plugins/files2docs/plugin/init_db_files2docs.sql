-- files2docs_mapping
INSERT INTO files2docs_mapping(id_mapping,code_document_type,description,title,summary)
VALUES(1,'article','Mapping du type de document Article',NULL,NULL);

INSERT INTO files2docs_mapping(id_mapping,code_document_type,description,title,summary)
VALUES(2,'banner','Mapping du type de document Bannieres',NULL,NULL);

INSERT INTO files2docs_mapping(id_mapping,code_document_type,description,title,summary)
VALUES(3,'sound','Mapping du type de document Enregistrement sonore',NULL,NULL);

INSERT INTO files2docs_mapping(id_mapping,code_document_type,description,title,summary)
VALUES(4,'actor','Mapping du type de document Fiche acteur',NULL,NULL);

INSERT INTO files2docs_mapping(id_mapping,code_document_type,description,title,summary)
VALUES(5,'flash','Mapping du type de document Flash',NULL,NULL);

INSERT INTO files2docs_mapping(id_mapping,code_document_type,description,title,summary)
VALUES(6,'image','Mapping du type de document Image',NULL,NULL);

INSERT INTO files2docs_mapping(id_mapping,code_document_type,description,title,summary)
VALUES(7,'pdf','Mapping du type de document PDF',NULL,NULL);

INSERT INTO files2docs_mapping(id_mapping,code_document_type,description,title,summary)
VALUES(8,'video','Mapping du type de document Video',NULL,NULL);
 
-- files2docs_mapping_attribute 
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(1,1,12,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(2,2,52,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(3,2,53,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(4,3,50,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(5,4,30,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(6,4,31,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(7,5,54,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(8,5,55,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(9,5,56,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(10,5,57,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(11,5,58,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(12,5,59,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(13,5,60,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(14,5,61,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(15,5,62,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(16,6,43,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(17,6,44,'<user>');
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(18,7,48,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(19,8,39,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(20,8,40,NULL);
INSERT INTO files2docs_mapping_attribute(id_attribute,id_mapping,id_document_attribute,format) VALUES(21,6,63,'<filename>');
