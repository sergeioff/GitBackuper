# GitBackuper

Program that provides functionality for backup remote repositories from GitHub to local disk without _git_ client.
Makes incremental backups, that are keeping changes between backups.

## Get:

If you wouldn't like to build jar from sources, you can download _jar_ from [release page](https://github.com/sergeioff/GitBackuper/releases).

## Build:

Build executable _jar_ from sources:

execute _buildJar.sh_ 

```
./buildJar.sh
```

or:

```
mvn clean compile assembly:single clean
```

## Run

To run program use command:

```
java -jar GitBackuper.jar
```

Program can use GitHub OAuth token. 
If you'd like to use token, you need to create file _.token_ and put your token there. 

## Task description:

Write a Java program that will backup and restore Github repository content.

1. The backup should be done to local storage
2. The backup should be composed of an initial backup (backup all data) and an incremental backup (that backup all new updates/content since the latest backup we did).
3. Use OAUTH to connect to Github repository.
4. The restore should enable a “full snapshot” restore according to a give date in the past (backup date). 
