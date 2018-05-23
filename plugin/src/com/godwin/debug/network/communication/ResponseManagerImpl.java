package com.godwin.debug.network.communication;

import com.godwin.debug.common.Common;
import com.godwin.debug.model.DApplication;
import com.godwin.debug.model.DDatabase;
import com.godwin.debug.model.DTable;
import com.godwin.debug.network.ClientSocket;
import com.godwin.debug.network.SocketPool;
import gherkin.deps.com.google.gson.JsonArray;
import gherkin.deps.com.google.gson.JsonElement;
import gherkin.deps.com.google.gson.JsonObject;
import gherkin.deps.com.google.gson.JsonParser;
import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Godwin on 5/7/2018 2:52 PM for plugin.
 *
 * @author : Godwin Joseph Kurinjikattu
 */
class ResponseManagerImpl implements ResponseManager {
    @Override
    public void processResponse(WebSocket socket, String response) {
        JsonParser parser = new JsonParser();
        JsonElement element = parser.parse(response);

        JsonObject object = element.getAsJsonObject();
        int responseCode = object.get(Common.RESPONSE_TYPE).getAsInt();

        switch (responseCode) {
            case Common.REQUEST_APP_DETAILS:
                processGetAppDetails(socket, object);
                break;
            case Common.REQUEST_DB:
                processDatabase(socket, object);
                break;
            case Common.REQUEST_TABLE_DETAILS:
                processTableDetails(socket, object);
                break;
        }
    }

    private void processDatabase(WebSocket socket, JsonObject object) {

        int responseCode = object.get(Common.RESPONSE_TYPE).getAsInt();

        JsonArray array = object.get(Common.KEY_DATA).getAsJsonArray();
        List<DDatabase> databases = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JsonObject dbObject = array.get(i).getAsJsonObject();
            String dbName = dbObject.get(Common.KEY_DB_NAME).getAsString();
            String dbPath = dbObject.get(Common.KEY_DB_PATH).getAsString();

            DDatabase database = new DDatabase();
            database.setName(dbName);
            database.setUri(dbPath);

            List<DTable> tables = new ArrayList<>();
            JsonArray tblArray = dbObject.get(Common.KEY_TABLES).getAsJsonArray();
            for (int j = 0; j < tblArray.size(); j++) {
                JsonObject tableObject = tblArray.get(j).getAsJsonObject();

                String tableName = tableObject.get(Common.KEY_TABLE_NAME).getAsString();
                DTable table = new DTable();
                table.setDatabaseName(dbName);
                table.setName(tableName);
                tables.add(table);
            }
            database.setTables(tables);
            databases.add(database);
        }
        DataObserver.getInstance().publish(databases);
    }

    private void processGetAppDetails(WebSocket socket, JsonObject object) {
        int responseCode = object.get(Common.RESPONSE_TYPE).getAsInt();

        String packageName = object.get(Common.KEY_PKG).getAsString();
        String name = object.get(Common.KEY_NAME).getAsString();
        String version = object.get(Common.KEY_VERSION).getAsString();
        String base64Icon = object.get(Common.KEY_ICON).getAsString();

        DApplication application = new DApplication();
        application.setPackageName(packageName);
        application.setAppName(name);
        application.setVersion(version);
        application.setIcon(base64Icon);

        ClientSocket clientSocket = SocketPool.getInstance().getClientSocket(socket.getAttachment());
        if (null != clientSocket) {
            clientSocket.setApplication(application);
        }
    }

    private void processTableDetails(WebSocket socket, JsonObject object) {
        int responseCode = object.get(Common.RESPONSE_TYPE).getAsInt();

        JsonArray array = object.get(Common.KEY_DATA).getAsJsonArray();
        List<String> header = new ArrayList<>();
        List<List<String>> table = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JsonObject rowObject = array.get(i).getAsJsonObject();
            JsonArray rowArray = rowObject.getAsJsonArray(Common.KEY_ROW);
            List<String> row = new ArrayList<>();
            for (int j = 0; j < rowArray.size(); j++) {
                String data = rowArray.get(j).getAsString();
                row.add(data);
            }
            table.add(row);
        }
        header = table.get(0);
        table.remove(0);
        DataObserver.getInstance().publishTable(table, header);
    }
}
