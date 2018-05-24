package dev.gblpweb.backupmanagement;

import org.apache.lucene.index.IndexCommit;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.core.backup.repository.BackupRepository;
import org.apache.solr.core.backup.repository.LocalFileSystemRepository;
import org.apache.solr.handler.SnapShooter;

import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

/**
 * Created by gabriel.pereira on 23/05/18.
 */
public class BackupManager {

    private SolrCore core;

    private volatile NamedList<?> snapShootDetails;

    public BackupManager(SolrCore core) {
        this.core = core;
    }

    public void doBackupt(String backupLabel, Consumer<NamedList<?>> cb) {
        try {

            int numberBackupToKeep = 2;

            String location = core.getDataDir();

            IndexCommit indexCommit = core
                    .getDeletionPolicy()
                    .getLatestCommit();

            SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.ROOT);
            String backupName = backupLabel+"_"+String.valueOf(indexCommit.getGeneration())+"_"+fmt.format(new Date());
            String commitName = backupLabel+"_"+String.valueOf(indexCommit.getGeneration())+"_"+fmt.format(new Date());;

            Object repo = new LocalFileSystemRepository();

            URI locationUri = ((BackupRepository) repo).createURI(location);

            SnapShooter ss = new SnapShooter((BackupRepository) repo, core, locationUri, backupName, commitName);
            ss.validateCreateSnapshot();
            ss.createSnapAsync(indexCommit, numberBackupToKeep, (nl) -> {
                snapShootDetails = nl;
                cb.accept(snapShootDetails);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<BackupDirectory> getBackuptList() {

        List<BackupDirectory> listReturn = new ArrayList<>();

        try {

            String location = core.getDataDir();
            BackupRepository repo = new LocalFileSystemRepository();
            URI locationUri = repo.createURI(location);
            String[] filePaths = repo.listAll(locationUri);

            for (String f : filePaths) {
                BackupDirectory obd = new BackupDirectory(locationUri, f);
                if (obd.getTimestamp().isPresent()) {
                    listReturn.add(obd);
                }
            }

            Collections.sort(listReturn);

            if (listReturn.size() == 0) {
                throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "No backup name specified and none found in " + core.getDataDir());
            }

        } catch (IOException e) {

            e.printStackTrace();
        }

        return listReturn;
    }

    public void deleteOldBackups(int numberOfBackupsToKeep) {

        try {
            List<BackupDirectory> backups = getBackuptList();

            if (backups.size() > numberOfBackupsToKeep) {

                BackupRepository repo = new LocalFileSystemRepository();

                for (int i = numberOfBackupsToKeep; i < backups.size(); i++) {
                    BackupDirectory bd = backups.get(i);
                    repo.deleteDirectory(bd.getPath());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
