-- files2docs_mapping
DROP TABLE IF EXISTS files2docs_mapping;
CREATE TABLE files2docs_mapping (
	id_mapping int default '0' NOT NULL,
	code_document_type varchar(30) default '' NOT NULL,
	description varchar(255) default '' NOT NULL,
	title varchar(255) default NULL,
	summary varchar(255) default NULL,
	PRIMARY KEY (id_mapping),
	UNIQUE (code_document_type)
);

-- files2docs_mapping_attribute
DROP TABLE IF EXISTS files2docs_mapping_attribute;
CREATE TABLE files2docs_mapping_attribute (
	id_attribute int default '0' NOT NULL,
	id_mapping int default '0' NOT NULL,
	id_document_attribute int default '0' NOT NULL,
	format varchar(255) default NULL,
	PRIMARY KEY  (id_attribute)
);
