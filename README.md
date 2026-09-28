# lutece-cms-plugin-files2docs

Plugin Lutèce d'import en masse de documents.

## Description

Le plugin Files2Docs permet d'importer en masse certains types de documents Lutèce,
à partir de ressources (images, fichiers) présentes sur le système de fichiers de
l'utilisateur.

Il évite aux utilisateurs la création individuelle de chaque document : les champs
obligatoires du type de document choisi sont renseignés automatiquement à partir d'un
mapping configurable.

## Fonctionnalités

- Configuration de mappings entre un type de fichier et un type de document Lutèce
- Upload multiple de fichiers vers un espace documentaire
- Création automatique des documents à partir des fichiers importés

## Documentation

La documentation fonctionnelle est publiée avec le site Maven du composant
(`src/site/fr/xdoc`).

## Build

```sh
mvn clean install
```

## Licence

Licence BSD — voir l'en-tête des fichiers sources.
