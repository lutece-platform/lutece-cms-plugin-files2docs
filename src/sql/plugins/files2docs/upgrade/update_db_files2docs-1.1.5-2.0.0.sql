-- liquibase formatted sql
-- changeset files2docs:update_db_files2docs-1.1.5-2.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN
UPDATE core_admin_right SET icon_url = 'ti ti-file-import' WHERE id_right = 'FILES2DOCS_MANAGEMENT';
UPDATE core_admin_right SET icon_url = 'ti ti-arrows-exchange' WHERE id_right = 'MAPPING_MANAGEMENT';
