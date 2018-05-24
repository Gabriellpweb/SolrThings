SolrThings Plugin
=

Build
-
To generate plugin .jar file use:

```bash
you@/path/to/project/root$ mvn clean package
```
The SolrThings-\<version\>-SNAPSHOT.jar will be created in the target folder inside project.

Install
-

Copy the jar file to server@[solr/installation/folder/]contrib/dataimporthandler/lib/.

On server@[core/data/folder/conf/]solrconfig.xml insert the configuration bellow:
```xml
...
<requestHandler name="/managebackups" class="dev.gblpweb.requesthandlers.BackupManagementHandler">
</requestHandler>
...
```
Next on server@[core/data/folder/conf/]data-config.xml make the configurations as bellow:

```xml
<dataConfig>
    ...
    <document name="yourcore" onImportStart="dev.gblpweb.eventlisteners.CoreAutoBackupEventListener">
        ...
    </document>
    ...
</dataConfig>
```

Then restart solr to apply all the configs.
