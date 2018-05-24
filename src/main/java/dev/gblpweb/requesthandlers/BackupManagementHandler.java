package dev.gblpweb.requesthandlers;

import dev.gblpweb.backupmanagement.BackupDirectory;
import dev.gblpweb.backupmanagement.BackupManager;

import org.apache.solr.common.SolrException;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.core.SolrCore;
import org.apache.solr.handler.RequestHandlerBase;
import org.apache.solr.request.SolrQueryRequest;
import org.apache.solr.response.SolrQueryResponse;
import org.apache.solr.util.plugin.SolrCoreAware;


import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by gabriel.pereira on 22/05/18.
 */
public class BackupManagementHandler extends RequestHandlerBase implements SolrCoreAware {

    private SolrCore core;

    @Override
    public void handleRequestBody(SolrQueryRequest req, SolrQueryResponse rsp) throws Exception {

        rsp.setHttpCaching(false);
        final SolrParams solrParams = req.getParams();
        String command = solrParams.get(CMD);

        if (command == null) {
            rsp.add("status", "OK");
            rsp.add("message", "No command");
            return;
        }

        if (command.equals(CMD_BACKUP_LIST)) {
            BackupManager bm = new BackupManager(core);
            List<BackupDirectory> backups = bm.getBackuptList();
            rsp.add("backups", formatBackupList(backups));
        }
    }


    private List<NamedList> formatBackupList(List<BackupDirectory> backups) {

        List<NamedList> listReturn = new ArrayList<>();

        try {

            if (backups.size() == 0) {
                throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "No backup name specified and none found in " + core.getDataDir());
            }

            for (BackupDirectory bd : backups) {

                NamedList nl = new NamedList();
                nl.add("path", bd.getPath().getPath());
                nl.add("name", bd.getDirName().split("\\.")[1]);
                SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                nl.add("timestamp", dt.format(bd.getTimestamp().get()));
                nl.add("generation", String.valueOf(bd.getGenereation()));
                listReturn.add(nl);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return listReturn;
    }

    @Override
    public String getDescription() {
        return "BackupManagementHandler provides the options to manage backups made automatically.";
    }

    @Override
    public void inform(SolrCore core) {
        this.core = core;
    }

    public static final String CMD = "command";

    public static final String CMD_BACKUP_LIST = "list";
}
