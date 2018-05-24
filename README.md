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

Using
-
To see backups use http://solr.host:8983/solr/mycore/managebackups?command=list
```json
{
  "responseHeader": {
    "status": 0,
    "QTime": 3
  },
  "backups": [
    [
      "path",
      "/path/solr/core/data/snapshot.autobackup_10_20180524194735509",
      "name",
      "autobackup_10_20180523194735509",
      "timestamp",
      "2018-05-23 07:47:35",
      "generation",
      "10"
    ]
  ]
}
```
