package dev.gblpweb.eventlisteners;

import dev.gblpweb.backupmanagement.BackupManager;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.dataimport.Context;
import org.apache.solr.handler.dataimport.EventListener;

public class CoreAutoBackupEventListener implements EventListener {

    private volatile NamedList<?> snapShootDetails;

    private static String LISTENER_LABEL = "autobackup";

    private static int NUMBER_BACKUPS_TOKEEP = 2;

    public void onEvent(Context context) {

        doBackup(context.getSolrCore());
    }

    private void doBackup(SolrCore core) {
        BackupManager bm = new BackupManager(core);
        bm.doBackupt(LISTENER_LABEL, (nl) -> {
            bm.deleteOldBackups(NUMBER_BACKUPS_TOKEEP);
        });
    }
}
