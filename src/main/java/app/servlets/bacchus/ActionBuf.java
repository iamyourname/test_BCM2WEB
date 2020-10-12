package app.servlets.bacchus;

import app.entities.ConnectionPool;
import app.entities.RcToAgent;
import app.servlets.NTLMUserFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//import static app.entities.ConnectionPool.stmtPullB;
import static app.entities.Logs.writeLogMain;
import static app.entities.Logs.writeLogParent;

public class ActionBuf{
    public static HttpURLConnection aCon;


    public static String actionRezerv(String aBuff, String aSap){

       //http://msk-dpro-app573:8080/bacchus/ship/util/0180/502237/cancel-rezerv
        String[] appsRezerv = {"резервы",
                "http://msk-dpro-app573:8080/bacchus/ship/util/",
                "http://msk-dpro-app621:8080/bacchus/ship/util/",
                "http://msk-dpro-app621:8080/bacchus/ship/util/",
                "http://msk-dpro-app621:8080/bacchus/ship/util/",
                "http://msk-dpro-app621:8080/bacchus/ship/util/",
                "http://msk-dpro-app621:8080/bacchus/ship/util/",
                "http://msk-dpro-app621:8080/bacchus/ship/util/",
                "http://msk-dpro-app621:8080/bacchus/ship/util/",
                "http://msk-dpro-app621:8080/bacchus/ship/util/",
                "http://msk-dpro-app731:8080/bacchus/ship/util/",
                "http://msk-dpro-app897:8080/bacchus/ship/util/",
                "http://msk-dpro-app900:8080/bacchus/ship/util/"};
        String action="";
        String agent = "";
        for(int i = 0; i < RcToAgent.rcAgent.length; i++){
            if (RcToAgent.rcAgent[i][0].equals(aSap)){
                agent = RcToAgent.rcAgent[i][2];
            }
        }

        if(Integer.parseInt(agent)<9)agent="0"+agent;

        String jsonOptions =
                        "{\"SAP\":\"" + aSap + "\"," +
                                "\"Web\":\"true\","+
                        "\"BUF\":\""+ aBuff +"\"}"; // multi
        try {
            writeLogMain(NTLMUserFilter.getUserName(),"BACCHUS","Отгрузка",
                    "Снятие резервов",
                    jsonOptions,"LOADING","");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String url = appsRezerv[Integer.parseInt(agent)] + aSap + "/" + aBuff + "/cancel-rezerv";
        URL obj = null;
        try {
            obj = new URL(url);
            aCon = (HttpURLConnection) obj.openConnection();
            //add reuqest header
            aCon.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(aCon.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();


            writeLogParent(NTLMUserFilter.getUserName(),"BACCHUS","Отгрузка",
                    "Снятие резервов",
                    jsonOptions,"OK","");
            action="good";
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            try {
                writeLogParent(NTLMUserFilter.getUserName(),"BACCHUS","Отгрузка",
                        "Снятие резервов",
                        jsonOptions,"ERROR",e.toString());
                action="bad";
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }


    return action;
    }


    public static String actionMark(String aBuff, String aSap) throws SQLException {
        String action="";

        String sqlQueryUpdateMark = "begin \n" +
                "for mark in (select BAMC_LID from b_amc where codv_id= (select codv_id from c_org_divisions where codv_code ='" +
                aSap + "') " +
                "AND bamc_status ='Z' AND bamc_lid in ( \n" +
                "select bamc_lid from b_boxpos_bamc_rel where bbp_id in (\n" +
                "select bbp_id from b_boxpos where efb_id in (\n" +
                "select efb_id from b_outgoing_details_fb where boud_id in (\n" +
                "select boud_id from b_outgoing_details where bout_id in (\n" +
                "select bout_id from b_outgoing where bout_transactionid = ('"+ aBuff+ "')))))))\n" +
                "loop\n" +
                "update b_amc set bamc_status = 'A' where bamc_lid = mark.bamc_lid; \n" +
                "end loop; \n" +
                "end; ";
        String agent="";
        for(int i = 0; i < RcToAgent.rcAgent.length; i++){
            if (RcToAgent.rcAgent[i][0].equals(aSap)){
                agent = RcToAgent.rcAgent[i][2];
            }
        }
        if(Integer.parseInt(agent)<9)agent="0"+agent;
        ConnectionPool.getInstance().getConnection(agent);
        String jsonOptions =
                "{\"BUF\":\"" + aBuff+ "\"," +
                        "\"Web\":\"true\","+
                        "\"SAP\":\""+ aSap +"\"}"; // multi
            try{
                writeLogMain(NTLMUserFilter.getUserName(),"BACCHUS","Отгрузка",
                        "Смена статуса марок",
                        jsonOptions,"LOADING","");
                Connection pullConn = ConnectionPool.getInstance().getConnection(agent);
                Statement stmtPullB = pullConn.createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                stmtPullB.executeUpdate(sqlQueryUpdateMark);
                pullConn.close();
                writeLogParent(NTLMUserFilter.getUserName(),"BACCHUS","Отгрузка",
                        "Смена статуса марок",
                        jsonOptions,"OK","");
                action="good";
                pullConn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                writeLogParent(NTLMUserFilter.getUserName(),"BACCHUS","Отгрузка",
                        "Смена статуса марок",
                        jsonOptions,"ERROR",e.toString());
                action="bad";

            }

        return action;
    }

    public static String actionChoise(String aBuff, String aSap, String status) throws SQLException {
        String action="";

        String sqlStringUpdate = "update B_OUTGOING\n" +
                "set DOC_STATUS = " + status +
                " where BOUT_TRANSACTIONID = '" + aBuff + "' and" +
                " codv_id = (select codv_id from c_org_divisions where codv_code ='"+ aSap + "')";
        String agent="";
        for(int i = 0; i < RcToAgent.rcAgent.length; i++){
            if (RcToAgent.rcAgent[i][0].equals(aSap)){
                agent = RcToAgent.rcAgent[i][2];
            }
        }
        if(Integer.parseInt(agent)<9)agent="0"+agent;
        ConnectionPool.getInstance().getConnection(agent);
        String jsonOptions =
                "{\"BUF\":\"" + aBuff+ "\"," +
                        "\"SAP\":\""+ aSap +"\","+
                        "\"Web\":\"true\","+
                "\"STATUS:\":\""+status+"\"}"; // multi
        try{
            writeLogMain(NTLMUserFilter.getUserName(),"BACCHUS","Отгрузка",
                    "Смена статуса буфера",
                    jsonOptions,"LOADING","");
            Connection pullConn = ConnectionPool.getInstance().getConnection(agent);
            Statement stmtPullB = pullConn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            stmtPullB.executeUpdate(sqlStringUpdate);
            writeLogParent(NTLMUserFilter.getUserName(),"BACCHUS","Отгрузка",
                    "Смена статуса буфера",
                    jsonOptions,"OK","");
            action="good";
            pullConn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            writeLogParent(NTLMUserFilter.getUserName(),"BACCHUS","Отгрузка",
                    "Смена статуса буфера",
                    jsonOptions,"ERROR",e.toString());
            action="bad";

        }

        return action;
    }





}
