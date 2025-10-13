-- liquibase formatted sql
-- changeset files2docs:init_core_files2docs.sql
-- preconditions onFail:MARK_RAN onError:WARN
-- core_admin_right
INSERT INTO core_admin_right(id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url)
VALUES('FILES2DOCS_MANAGEMENT','files2docs.adminFeature.files2docs_management.name',3,'jsp/admin/plugins/files2docs/SelectFiles.jsp','files2docs.adminFeature.files2docs_management.description',0,'files2docs','CONTENT',NULL,NULL);

INSERT INTO core_admin_right(id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url)
VALUES('MAPPING_MANAGEMENT','files2docs.adminFeature.mapping_management.name',1,'jsp/admin/plugins/files2docs/ManageMapping.jsp','files2docs.adminFeature.mapping_management.description',0,'files2docs','CONTENT',NULL,NULL);

-- core_user_right
INSERT INTO core_user_right(id_right,id_user) VALUES('FILES2DOCS_MANAGEMENT',1);
INSERT INTO core_user_right(id_right,id_user) VALUES('FILES2DOCS_MANAGEMENT',2);
INSERT INTO core_user_right(id_right,id_user) VALUES('FILES2DOCS_MANAGEMENT',3);
INSERT INTO core_user_right(id_right,id_user) VALUES('FILES2DOCS_MANAGEMENT',4);

INSERT INTO core_user_right(id_right,id_user) VALUES('MAPPING_MANAGEMENT',1);
INSERT INTO core_user_right(id_right,id_user) VALUES('MAPPING_MANAGEMENT',2);
