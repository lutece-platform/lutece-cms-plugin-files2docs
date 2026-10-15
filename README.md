![](https://dev.lutece.paris.fr/jenkins/buildStatus/icon?job=cms-plugin-files2docs-deploy)
[![Alerte](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-files2docs&metric=alert_status)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-files2docs)
[![Line of code](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-files2docs&metric=ncloc)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-files2docs)
[![Coverage](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-files2docs&metric=coverage)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-files2docs)

# Plugin files2docs

## Introduction

The files2docs plugin performs a bulk import of Lutece documents (plugin Document) from files (images, PDF, sounds, videos, ...) uploaded from the user's file system. Each uploaded file becomes a new document of the chosen type, created in the chosen document space.

Users no longer need to create each document one by one: the mandatory attributes of the selected document type are filled in automatically from a **mapping** defined per document type. A mapping gives a format for the title, the summary and each attribute of the document. A format can contain tags that are replaced by information on the imported file (name, MIME type, extension, import date).

The import is done in several steps:

 
* choice of the document type (only types with at least one file or image attribute, and at most one mandatory one, are proposed);
* choice of the target document space (a new space can be created on the fly);
* upload of the files, stored temporarily under the upload directory of the webapp;
* check and completion of the generated documents, then creation;
* display of the import result.

The plugin requires the Lutece core and the `plugin-document` plugin.

## Configuration

 **Properties** 

The settings are in `WEB-INF/conf/plugins/files2docs.properties` :

| Property| Default value| Description|
|-----------------|-----------------|-----------------|
|  `files2docs.itemsPerPage` |  `10` | Default number of rows displayed by the paginator in the admin screens.|
|  `files2docs.listAttributeTypeFile` |  `file,image` | Codes of the document attribute types holding a file.|
|  `files2docs.parentPath` |  `/plugins/files2docs/upload/` | Directory (relative to the webapp) where uploaded files are stored temporarily.|
|  `files2docs.swfupload.file_upload_limit` |  `0` | Maximum number of files to upload (0 = unlimited).|
|  `files2docs.swfupload.file_queue_limit` |  `0` | Maximum number of files in the upload queue (0 = unlimited).|
|  `files2docs.swfupload.requeue_on_error` |  `false` | Puts files in error back into the upload queue.|
|  `files2docs.swfupload.debug` |  `false` | Enables the debug mode of the upload component.|
|  `files2docs.requestSizeMax` |  `2097152` | Maximum size (in bytes) of a request, i.e. of an uploaded file. Must match the `requestSizeMax` value of the core upload filter.|
|  `files2docs.minInterval` |  `2000` | Minimum interval (in milliseconds) between two requests of the same client. Must match the `minInterval` value of the core filter.|
|  `files2docs.mapping.listTags` |  `filename,mimetype,extension,date` | List of the tags available in the mapping formats.|
|  `files2docs.mapping.tag.filename` |  `<filename>` | Tag replaced by the name of the imported file (without extension).|
|  `files2docs.mapping.tag.mimetype` |  `<mimetype>` | Tag replaced by the MIME type of the imported file.|
|  `files2docs.mapping.tag.extension` |  `<extension>` | Tag replaced by the extension of the imported file (without the leading dot).|
|  `files2docs.mapping.tag.date` |  `<date>` | Tag replaced by the import date.|
|  `daemon.autoCleaning.interval` |  `300` | Interval (in seconds) between two runs of the cleaning daemon.|
|  `daemon.autoCleaning.onstartup` |  `1` | Starts the cleaning daemon when the webapp starts (1) or not (0).|

The plugin requires a database pool ( `db-pool-required` ). A dedicated pool can be declared in `db.properties` with the `files2docs.*` keys.

 **Spring Beans** 

Declared in `WEB-INF/conf/plugins/files2docs_context.xml` :

| Bean ID| Class|
|-----------------|-----------------|
|  `files2docsMappingDAO` |  `fr.paris.lutece.plugins.files2docs.business.MappingDAO` |
|  `files2docsAttributeDAO` |  `fr.paris.lutece.plugins.files2docs.business.AttributeDAO` |

 **Daemons** 

| Daemon ID| Class| Description|
|-----------------|-----------------|-----------------|
|  `autoCleaning` |  `fr.paris.lutece.plugins.files2docs.service.AutoCleaningDaemon` | Deletes the temporary upload directories (under `files2docs.parentPath` ) last modified more than one day ago. It should be enabled to avoid filling the disk.|

## Usage

 **Admin Rights** 

| Right| Level| URL| Feature group| Description|
|-----------------|-----------------|-----------------|-----------------|-----------------|
|  `FILES2DOCS_MANAGEMENT` | 3|  `jsp/admin/plugins/files2docs/SelectFiles.jsp` |  `CONTENT` | Multiple documents upload|
|  `MAPPING_MANAGEMENT` | 1|  `jsp/admin/plugins/files2docs/ManageMapping.jsp` |  `CONTENT` | Management of the mappings between document types and imported files|

Document creation also checks the permissions of the Document plugin: the user must be allowed to create documents of the chosen type in the chosen space.

 **Mappings** 

The `MAPPING_MANAGEMENT` feature lists, creates, modifies and deletes mappings. A mapping is linked to one document type and defines the format of the title, of the summary and of each mandatory or file attribute of the type. The initialization script provides mappings for the standard types `article` , `banner` , `sound` , `actor` , `flash` , `image` , `pdf` and `video` .

 **Services** 

| Class| Description|
|-----------------|-----------------|
|  `Files2DocsLinkDocument` | Bridge to the Document plugin: document types eligible for import ( `getListDocumentTypeFile` ), mandatory attributes ( `getMandatoryAttributes` , `getMandatoryAttributeFileImage` ), mapped attributes ( `getAllAttributes` ), creation permission check ( `isAuthorizedAdminDocument` ), document creation ( `createDocument` ) and spaces browser ( `getSpacesBrowser` ).|
|  `AutoCleaningService` | Deletes the outdated temporary upload directories ( `processAutoCleaning` ).|


[Maven documentation and reports](https://dev.lutece.paris.fr/plugins/plugin-files2docs/)



 *generated by [xdoc2md](https://github.com/lutece-platform/tools-maven-xdoc2md-plugin) - do not edit directly.*