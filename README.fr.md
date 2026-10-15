![](https://dev.lutece.paris.fr/jenkins/buildStatus/icon?job=cms-plugin-files2docs-deploy)
[![Alerte](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-files2docs&metric=alert_status)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-files2docs)
[![Line of code](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-files2docs&metric=ncloc)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-files2docs)
[![Coverage](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-files2docs&metric=coverage)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-files2docs)

# Plugin files2docs

## Introduction

Le plugin files2docs permet d'importer en masse des documents Lutèce (plugin Document) à partir de fichiers (images, PDF, sons, vidéos, ...) présents sur le système de fichiers de l'utilisateur. Chaque fichier téléversé devient un nouveau document du type choisi, créé dans l'espace documentaire choisi.

Les utilisateurs n'ont plus à créer chaque document un par un : les attributs obligatoires du type de document sont renseignés automatiquement à partir d'un **mapping** défini par type de document. Un mapping donne un format pour le titre, le résumé et chaque attribut du document. Un format peut contenir des tags remplacés par des informations sur le fichier importé (nom, type MIME, extension, date d'import).

L'import se déroule en plusieurs étapes :

 
* choix du type de document (seuls les types ayant au moins un attribut fichier ou image, dont un seul au plus obligatoire, sont proposés) ;
* choix de l'espace documentaire cible (un nouvel espace peut être créé à la volée) ;
* téléversement des fichiers, stockés temporairement dans le répertoire d'upload de la webapp ;
* vérification et complément des documents générés, puis création ;
* affichage du résultat de l'import.

Le plugin nécessite le coeur de Lutèce et le plugin `plugin-document` .

## Configuration

 **Propriétés** 

Les réglages se trouvent dans `WEB-INF/conf/plugins/files2docs.properties` :

| Propriété| Valeur par défaut| Description|
|-----------------|-----------------|-----------------|
|  `files2docs.itemsPerPage` |  `10` | Nombre de lignes affichées par défaut par le paginateur dans les écrans d'administration.|
|  `files2docs.listAttributeTypeFile` |  `file,image` | Codes des types d'attributs de document contenant un fichier.|
|  `files2docs.parentPath` |  `/plugins/files2docs/upload/` | Répertoire (relatif à la webapp) où les fichiers téléversés sont stockés temporairement.|
|  `files2docs.swfupload.file_upload_limit` |  `0` | Nombre maximum de fichiers à téléverser (0 = illimité).|
|  `files2docs.swfupload.file_queue_limit` |  `0` | Nombre maximum de fichiers dans la file d'attente (0 = illimité).|
|  `files2docs.swfupload.requeue_on_error` |  `false` | Replace les fichiers en erreur dans la file d'attente.|
|  `files2docs.swfupload.debug` |  `false` | Active le mode debug du composant de téléversement.|
|  `files2docs.requestSizeMax` |  `2097152` | Taille maximale (en octets) d'une requête, c'est-à-dire d'un fichier téléversé. Doit correspondre à la valeur `requestSizeMax` du filtre d'upload du coeur.|
|  `files2docs.minInterval` |  `2000` | Intervalle minimum (en millisecondes) entre deux requêtes d'un même client. Doit correspondre à la valeur `minInterval` du filtre du coeur.|
|  `files2docs.mapping.listTags` |  `filename,mimetype,extension,date` | Liste des tags utilisables dans les formats des mappings.|
|  `files2docs.mapping.tag.filename` |  `<filename>` | Tag remplacé par le nom du fichier importé (sans extension).|
|  `files2docs.mapping.tag.mimetype` |  `<mimetype>` | Tag remplacé par le type MIME du fichier importé.|
|  `files2docs.mapping.tag.extension` |  `<extension>` | Tag remplacé par l'extension du fichier importé (sans le point).|
|  `files2docs.mapping.tag.date` |  `<date>` | Tag remplacé par la date de l'import.|
|  `daemon.autoCleaning.interval` |  `300` | Intervalle (en secondes) entre deux passages du daemon de nettoyage.|
|  `daemon.autoCleaning.onstartup` |  `1` | Démarre le daemon de nettoyage au lancement de la webapp (1) ou non (0).|

Le plugin nécessite un pool de base de données ( `db-pool-required` ). Un pool dédié peut être déclaré dans `db.properties` avec les clés `files2docs.*` .

 **Beans Spring** 

Déclarés dans `WEB-INF/conf/plugins/files2docs_context.xml` :

| ID du bean| Classe|
|-----------------|-----------------|
|  `files2docsMappingDAO` |  `fr.paris.lutece.plugins.files2docs.business.MappingDAO` |
|  `files2docsAttributeDAO` |  `fr.paris.lutece.plugins.files2docs.business.AttributeDAO` |

 **Daemons** 

| ID du daemon| Classe| Description|
|-----------------|-----------------|-----------------|
|  `autoCleaning` |  `fr.paris.lutece.plugins.files2docs.service.AutoCleaningDaemon` | Supprime les répertoires temporaires d'upload (sous `files2docs.parentPath` ) dont la dernière modification date de plus d'un jour. Il est conseillé de l'activer pour ne pas saturer le disque.|

## Usage

 **Droits d'administration** 

| Droit| Niveau| URL| Groupe de fonctionnalités| Description|
|-----------------|-----------------|-----------------|-----------------|-----------------|
|  `FILES2DOCS_MANAGEMENT` | 3|  `jsp/admin/plugins/files2docs/SelectFiles.jsp` |  `CONTENT` | Import multiple de documents|
|  `MAPPING_MANAGEMENT` | 1|  `jsp/admin/plugins/files2docs/ManageMapping.jsp` |  `CONTENT` | Gestion des mappings entre types de document et fichiers importés|

La création des documents vérifie aussi les permissions du plugin Document : l'utilisateur doit être autorisé à créer des documents du type choisi dans l'espace choisi.

 **Mappings** 

La fonctionnalité `MAPPING_MANAGEMENT` permet de lister, créer, modifier et supprimer les mappings. Un mapping est lié à un type de document et définit le format du titre, du résumé et de chaque attribut obligatoire ou fichier du type. Le script d'initialisation fournit des mappings pour les types standards `article` , `banner` , `sound` , `actor` , `flash` , `image` , `pdf` et `video` .

 **Services** 

| Classe| Description|
|-----------------|-----------------|
|  `Files2DocsLinkDocument` | Pont vers le plugin Document : types de document éligibles à l'import ( `getListDocumentTypeFile` ), attributs obligatoires ( `getMandatoryAttributes` , `getMandatoryAttributeFileImage` ), attributs mappés ( `getAllAttributes` ), contrôle du droit de création ( `isAuthorizedAdminDocument` ), création de document ( `createDocument` ) et navigateur d'espaces ( `getSpacesBrowser` ).|
|  `AutoCleaningService` | Supprime les répertoires temporaires d'upload périmés ( `processAutoCleaning` ).|


[Maven documentation and reports](https://dev.lutece.paris.fr/plugins/plugin-files2docs/)



 *generated by [xdoc2md](https://github.com/lutece-platform/tools-maven-xdoc2md-plugin) - do not edit directly.*