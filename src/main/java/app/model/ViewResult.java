package app.model;

import app.entities.ConnectionPool;
import app.entities.RcToAgent;
import app.servlets.NTLMUserFilter;

import java.sql.*;

//import static app.entities.ConnectionPool.rsPullB;
//import static app.entities.ConnectionPool.stmtPullB;
import static app.entities.Logs.writeLogMain;
import static app.entities.Logs.writeLogParent;

public class ViewResult {
    private static ViewResult viewResult = new ViewResult();



    public static ViewResult getInstance() {
        return viewResult;
    }


    public Object[][] ViewBuffGodOutFromBD(String godbuff, String godSAP, String godagent) throws SQLException {


        String result="";
        String sqlstring = "select \n" +
                "bo.bout_transactionid \"Номер буфера\" \n" +
                ", sd.sdss_name || ' (' || bo.doc_status || ')'  \"Статус буфера\"\n" +
                ", bo.bout_waybilldate \"Дата\"\n" +
                ", bo.bout_waybillnumber \"Номер АП\" \n" +
                ", ew.ewbh_wbregid \"ТТН ЕГАИС\"\n" +
                ", ews.ewbs_name \"Статус ТТН ЕГАИС в Бахус\"\n" +
                ", ew.ewbh_egais_response \"Ошибка\""+
                "from b_outgoing bo\n" +
                "left join c_org_divisions co on co.codv_id = bo.codv_id\n" +
                "left join e_waybill ew on ew.ewbh_id = bo.ewbh_id\n" +
                "left join e_waybill_statuses ews on ews.ewbs_code = ew.ewbh_status\n" +
                "left join s_docstatuses sd on sd.sdss_id = bo.doc_status\n" +
                "where 1=1 \n" +
                "and bo.doc_adddate > sysdate -10 " +
                "and bo.bout_transactionid = '" + godbuff + "'\n" +
                "and co.codv_code = '" + godSAP + "'\n";


        Connection pullConn = ConnectionPool.getInstance().getConnection(godagent);
        Statement stmtPullB = pullConn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet rsPullB = stmtPullB.executeQuery(sqlstring);
        ConnectionPool.getInstance().getConnection(godagent).close();
        //System.out.println("Find");
        ResultSetMetaData rsdata = rsPullB.getMetaData();
        int siz = rsdata.getColumnCount();
        rsPullB.last();
        int countrows = rsPullB.getRow();
        Object[] colNames = new String[siz];
        Object[][] data = new Object[countrows][siz+1];
        if(countrows == 0){data[0][0]="empty";return data;} // если не отгрузка
        rsPullB.beforeFirst();

        for (int i=0; i<siz; i++) {
            colNames[i] = rsdata.getColumnName(i+1);
        }
        int id=0;
        while (rsPullB.next()){
            for (int iii=0;iii<siz;iii++) {

                if(rsPullB.getString((String) colNames[iii])==null){
                    data[id][iii]="null";
                }else{
                    data[id][iii] = rsPullB.getString((String) colNames[iii]);
                }
            }
            id++;
        }
        if(data[0][6].toString().contains("необеспеченный")){
            String posFix = data[0][6].toString().replace("  имеют необеспеченный расход.","");
            String[] posFixList = posFix.split("позиции ");
            //System.out.println(posFixList[1]);
            data[0][7]=ViewFixOfNeob(godbuff,godSAP,godagent,posFixList[1]);
        }
        pullConn.close();
        return data;
    }

    public String ViewFixOfNeob(String godbuff, String godSAP, String godagent, String pos) throws SQLException {


        String result="";
        String sqlstring = "select \n" +
                "  bo.bout_transactionid \n" +
                " , ef.efb_f2regid \n" +
                " , ewp.ewbp_party \n" +
                " , ewp.ewbp_quantity \n" +
                " , bs.bste_quantity \n" +
                "from e_waybill_positions ewp \n" +
                "left join b_outgoing bo on bo.ewbh_id = ewp.ewbh_id \n" +
                "left join c_org_divisions co on co.codv_id = ewp.codv_id \n" +
                "left join e_fb ef on ef.efb_id = ewp.ewbp_outgoing_efb_id \n" +
                "left join b_stockentry bs on bs.efb_id = ewp.ewbp_outgoing_efb_id "+
                "where 1=1 \n" +
                "and bo.doc_adddate > sysdate -10 " +
                "and bo.bout_transactionid = '" + godbuff + "'\n" +
                "and co.codv_code = '" + godSAP + "'\n" +
                "and ewp.ewbp_identity = '" + pos + "'\n"
                ;

        System.out.println(sqlstring);
        Connection pullConn = ConnectionPool.getInstance().getConnection(godagent);
        Statement stmtPullB = pullConn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet rsPullB = stmtPullB.executeQuery(sqlstring);
        ConnectionPool.getInstance().getConnection(godagent).close();
        //System.out.println("Find");
        ResultSetMetaData rsdata = rsPullB.getMetaData();
        int siz = rsdata.getColumnCount();
        rsPullB.last();
        int countrows = rsPullB.getRow();
        Object[] colNames = new String[siz];
        Object[][] data = new Object[countrows][siz];
        //if(countrows == 0){data[0][0]="empty";return data;} // если не отгрузка
        rsPullB.beforeFirst();

        /*
Добрый день!
В буфере 2945788 имеется необеспеченный расход по справке FB-000002642000696 для PLU 3696357.
Отгружаете 20 шт, доступно для отгрузки 3 шт.
Просьба запросить у ГУТЗ корректные остатки и отгружать согласно количеству продукции на вашем РЦ.
Вы также можете отгрузить изъяв данную продукцию из отгрузки.
*/



        for (int i=0; i<siz; i++) {
            colNames[i] = rsdata.getColumnName(i+1);

        }

        StringBuilder startMes = new StringBuilder("Добрый день!\n" +
                "В буфере " + godbuff + " имеется необеспеченный расход.\n");

        //System.out.println(startMes);

        int id=0;
        while (rsPullB.next()){

            startMes.append("По справке ")
                    .append(rsPullB.getString((String) colNames[1]))
                    .append(" для PLU ")
                    .append(rsPullB.getString((String) colNames[2]))
                    .append(". В документе содержится ")
                    .append(rsPullB.getString((String) colNames[3]))
                    .append("шт. данной АП, но доступно для отгрузки: ")
                    .append(rsPullB.getString((String) colNames[4]))
                    .append(" шт. \n");
        }
        startMes.append("Просьба запросить у ГУТЗ корректные остатки и отгружать согласно количеству продукции на вашем РЦ.\n" + "Вы также можете отгрузить изъяв данную продукцию из отгрузки.");
        pullConn.close();
        System.out.println("mes" + startMes.toString());
        return startMes.toString();
    }



    public Object[][] ViewTasksFromBD(String godbuff, String godSAP, String godagent) throws SQLException {


        String result="";
        String sqlstring = "select ats.* from a_tasks ats\n" +
                "left join b_outgoing bo on bo.bout_id = ats.c_doc_id \n" +
                "left join c_org_divisions co on co.codv_id = bo.codv_id\n" +
                "where 1=1 \n" +
                "and bo.doc_adddate > sysdate -10 \n"+
                "and bo.bout_transactionid = '" + godbuff + "'\n" +
                "and co.codv_code = '" + godSAP + "'\n" +
                "order by 1 ";


        Connection pullConn = ConnectionPool.getInstance().getConnection(godagent);
        Statement stmtPullB = pullConn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet rsPullB = stmtPullB.executeQuery(sqlstring);
        ConnectionPool.getInstance().getConnection(godagent).close();
        //System.out.println("Find");
        ResultSetMetaData rsdata = rsPullB.getMetaData();
        int siz = rsdata.getColumnCount();
        rsPullB.last();
        int countrows = rsPullB.getRow();
        Object[] colNames = new String[siz];
        Object[][] data = new Object[countrows][siz];
        if(countrows == 0){data[0][0]="empty";return data;} // если не отгрузка
        rsPullB.beforeFirst();

        for (int i=0; i<siz; i++) {
            colNames[i] = rsdata.getColumnName(i+1);
        }
        int id=0;
        while (rsPullB.next()){
            for (int iii=0;iii<siz;iii++) {

                if(rsPullB.getString((String) colNames[iii])==null){
                    data[id][iii]="null$$";
                }else{
                    data[id][iii] = rsPullB.getString((String) colNames[iii]) + "$$";
                }
            }
            id++;
        }
        pullConn.close();
        return data;
    }

    public Object[][] ViewUTMFromBD(String godbuff, String godSAP, String godagent) throws SQLException {


        String result="";
        String sqlstring = "select bu.* from b_outgoing bo\n" +
                "left join c_org_divisions co on co.codv_id = bo.codv_id\n" +
                "left join e_waybill ew on ew.ewbh_id = bo.ewbh_id\n" +
                "left join b_utmdocs bu on bu.bud_utm_reply_id = ew.ewbh_utmwaybill_replyid \n"+
                "where 1=1 \n" +
                "and bo.doc_adddate > sysdate -10 \n"+
                "and bo.bout_transactionid = '" + godbuff + "'\n" +
                "and co.codv_code = '" + godSAP + "'\n" +
                "order by 1 ";


        Connection pullConn = ConnectionPool.getInstance().getConnection(godagent);
        Statement stmtPullB = pullConn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet rsPullB = stmtPullB.executeQuery(sqlstring);
        ConnectionPool.getInstance().getConnection(godagent).close();
        //System.out.println("Find");
        ResultSetMetaData rsdata = rsPullB.getMetaData();
        int siz = rsdata.getColumnCount();
        rsPullB.last();
        int countrows = rsPullB.getRow();
        Object[] colNames = new String[siz];
        Object[][] data = new Object[countrows][siz];
        if(countrows == 0){data[0][0]="empty";return data;} // если не отгрузка
        rsPullB.beforeFirst();

        for (int i=0; i<siz; i++) {
            colNames[i] = rsdata.getColumnName(i+1);
        }
        int id=0;
        while (rsPullB.next()){
            for (int iii=0;iii<siz;iii++) {

                if(rsPullB.getString((String) colNames[iii])==null){
                    data[id][iii]="null$$";
                }else{
                    data[id][iii] = rsPullB.getString((String) colNames[iii]) + "$$";
                }
            }
            id++;
        }
        pullConn.close();
        return data;
    }


    public Object[][] ViewBuffOutFromBD(String buff, String SAP, String agent) throws SQLException {
            String result="";
            String sqlstring = "select bo.bout_transactionid \"Буфер\"," +
                " bo.bout_transactiondate \"Дата\", bo.bout_waybillnumber \"Номер ТТН\", sd.sdss_name \"Статус\" " +
                "from b_outgoing  bo "+
                "join s_docstatuses sd on sd.sdss_id = bo.doc_status "+
                "where codv_id = (select codv_id from c_org_divisions where codv_code = '";


        Connection pullConn = ConnectionPool.getInstance().getConnection(agent);
        Statement stmtPullB = pullConn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet rsPullB = stmtPullB.executeQuery(sqlstring + SAP +
                      "') and  doc_adddate > sysdate -5 and bout_transactionid ='" + buff+"'");
        ConnectionPool.getInstance().getConnection(agent).close();
        //System.out.println("Find");
        ResultSetMetaData rsdata = rsPullB.getMetaData();
        int siz = rsdata.getColumnCount();
        rsPullB.last();
        int countrows = rsPullB.getRow();
        rsPullB.beforeFirst();
        Object[] colNames = new String[siz];
        Object[][] data = new Object[countrows][siz];
        for (int i=0; i<siz; i++) {
            colNames[i] = rsdata.getColumnName(i+1);
        }
        int id=0;
        while (rsPullB.next()){
            for (int iii=0;iii<siz;iii++) {

                if(rsPullB.getString((String) colNames[iii])==null){
                    data[id][iii]="null";
                }else{
                    data[id][iii] = rsPullB.getString((String) colNames[iii]);
                }
            }
            id++;
        }
    pullConn.close();
    return data;
    }

    public Object[][] ViewBuffInFromBD(String buff, String SAP, String agent) throws SQLException {
        String result="";
        String sqlstring = "select bi.binc_transactionid \"Буфер\"," +
                " bi.binc_transactiondate \"Дата\", bi.binc_waybillnumber \"Номер ТТН\", sd.sdss_name \"Статус\" " +
                "from b_incoming  bi "+
                "join s_docstatuses sd on sd.sdss_id = bi.doc_status "+
                "where codv_id = (select codv_id from c_org_divisions where codv_code = '";

        //rsB = stmtB.executeQuery(sqlstring + SAP +
        //      "') and  doc_adddate > sysdate -5 and bout_transactionid ='" + buff+"'");
        Connection pullConn = ConnectionPool.getInstance().getConnection(agent);
        Statement stmtPullB = pullConn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
       ResultSet rsPullB = stmtPullB.executeQuery(sqlstring + SAP +
                "') and  doc_adddate > sysdate -5 and binc_transactionid ='" + buff+"'");

        //System.out.println("Find");
        ResultSetMetaData rsdata = rsPullB.getMetaData();
        int siz = rsdata.getColumnCount();
        rsPullB.last();
        int countrows = rsPullB.getRow();
        rsPullB.beforeFirst();
        Object[] colNames = new String[siz];
        Object[][] data = new Object[countrows][siz];
        for (int i=0; i<siz; i++) {
            colNames[i] = rsdata.getColumnName(i+1);
        }
        int id=0;
        while (rsPullB.next()){
            for (int iii=0;iii<siz;iii++) {

                if(rsPullB.getString((String) colNames[iii])==null){
                    data[id][iii]="null";
                }else{
                    data[id][iii] = rsPullB.getString((String) colNames[iii]);
                }
            }
            id++;
        }
        pullConn.close();
        return data;
    }

    public String ViewBuffOutFromNQ(String buff, String sap) throws SQLException {
        String result="";
        Connection pullConnNq = ConnectionPool.getInstance().getConnectionNQ(sap);
        Statement stmtPullNq = pullConnNq.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        String getSqlstringNQ ="select xml_data from alc.export_data e where dt_created > trunc(sysdate-13) and e.data_type='OUT31' " +
                "and id = '";
        ResultSet rsNQ = stmtPullNq.executeQuery(getSqlstringNQ + buff + "'");
        while (rsNQ.next()) {
            for (int i = 0; i < 1; i++) {
                result = (rsNQ.getString("XML_DATA"));
            }
        }
        pullConnNq.close();
        return result;
    }

    public String ViewBuffInFromNQ(String buff, String sap) throws SQLException {
        String result="";
        Connection pullConnNq = ConnectionPool.getInstance().getConnectionNQ(sap);
        Statement stmtPullNq = pullConnNq.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        String getSqlstringNQ ="select xml_data from alc.export_data e where dt_created > trunc(sysdate-13) and e.data_type='IN24' " +
                "and id = '";
        ResultSet rsNQ = stmtPullNq.executeQuery(getSqlstringNQ + buff + "'");
        while (rsNQ.next()) {
            for (int i = 0; i < 1; i++) {
                result = (rsNQ.getString("XML_DATA"));
            }
        }
        pullConnNq.close();
        return result;
    }


    public String ViewMarkInfo(String spravka, String sap) throws SQLException {
        String result="";
        String agent="";
        for(int i = 0; i < RcToAgent.rcAgent.length; i++){
            if (RcToAgent.rcAgent[i][0].equals(sap)){
                agent = RcToAgent.rcAgent[i][2];
            }
        }
        if(Integer.parseInt(agent)<9)agent="0"+agent;

        String getMarkInfo ="select ba.bamc_lid, ba.bamc_amc, ba.bamc_status from b_amc ba\n" +
                "join c_org_divisions co on co.codv_id = ba.codv_id\n" +
                "join b_stockentry_amc bsa on bsa.bamc_lid = ba.bamc_lid \n" +
                "join b_stockentry bs on bs.bste_id = bsa.bste_id\n" +
                "join e_fb ef on ef.efb_id = bs.efb_id\n" +
                "where co.codv_code = '" + sap + "' and ef.efb_f2regid = '" + spravka + "'";
        //ba.bamc_status = 'O' and
        Connection pullConn = ConnectionPool.getInstance().getConnection(agent);
        Statement stmtPullB = pullConn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet rsPullB = stmtPullB.executeQuery(getMarkInfo);


        ResultSetMetaData rsdata = rsPullB.getMetaData();
        int siz = rsdata.getColumnCount();
        rsPullB.last();
        int countrows = rsPullB.getRow();
        rsPullB.beforeFirst();
        Object[] colNames = new String[siz];
        String[][] data = new String[countrows][siz];
        for (int i=0; i<siz; i++) {
            colNames[i] = rsdata.getColumnName(i+1);
        }
        int id=0;
        while (rsPullB.next()){
            for (int iii=0;iii<siz;iii++) {

                if(rsPullB.getString((String) colNames[iii])==null){
                    result +="null&";
                    //data[id][iii]="null";
                }else{

                    result += rsPullB.getString((String) colNames[iii]);
                    result += "&";
                }

            }
            result += ";";
            id++;
        }
        rsPullB.close();
        pullConn.close();
        return result;
    }



    public String SearchFromNQ(String buff, String Sap) throws SQLException {
        String result="";
        //ConnectToBD.connectNQ(Integer.parseInt(Sap));
        Connection pullConnNq = ConnectionPool.getInstance().getConnectionNQ(Sap);
        Statement stmtPullNq = pullConnNq.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        String sqlSearchFlow = "select e.data_type \"Потоки\",e.xml_data \"REQUEST\", e.http_replytext \"RESPONSE\" from alc.export_data e where dt_created > trunc(sysdate-45) \n" +
                "and id = '";

        String jsonOptions =
                "{\"SAP\":\"" + Sap + "\"," +
                        "\"Web\":\"true\","+
                        "\"BUF\":\""+ buff +"\"}"; // multi

            writeLogMain(NTLMUserFilter.getUserName(),"BACCHUS","Поиск потоков",
                    "Поиск потоков",
                    jsonOptions,"LOADING","");
        try {
            ResultSet rsNQ = stmtPullNq.executeQuery(sqlSearchFlow + buff + "'");
            rsNQ.last();
            int countrowsnqS = rsNQ.getRow();

            rsNQ.beforeFirst();
            ResultSetMetaData rsdatanqS = rsNQ.getMetaData();
            int siznqS = rsdatanqS.getColumnCount();

            while (rsNQ.next()) {
                for (int i = 0; i < countrowsnqS; i++) {
                    result += (rsNQ.getString("Потоки"));
                    result +="!";
                    result += (rsNQ.getString("REQUEST"));
                    result +="!";
                    result += (rsNQ.getString("RESPONSE"));

                }
                result +="#";
                //System.out.println(""+result);

            }
        }
        catch (SQLException e) {

            writeLogParent(NTLMUserFilter.getUserName(),"BACCHUS","Поиск потоков",
                    "Поиск потоков",
                    jsonOptions,"ERROR",""+e.toString());
        }

        writeLogParent(NTLMUserFilter.getUserName(),"BACCHUS","Поиск потоков",
                "Поиск потоков",
                jsonOptions,"OK","");
        pullConnNq.close();
        return result;
    }



}
