package app.servlets.caduceus;

import app.entities.ConnectionPool;
import app.servlets.NTLMUserFilter;

import java.sql.*;

import static app.entities.Logs.writeLogMain;
import static app.entities.Logs.writeLogParent;


public class AutoGas {

    public static String newTime;

    public static String showTmp_uuid() throws SQLException {
         String tableTMP_UUID = "tmp_uuid";
         String showTmp_UUID = "select upper(get_uuid(t.uuid)) \"UUID\", " +
                "case  " +
                "  when m.MVDC_VETDSTATUS = '2' then 'Оформлен'  " +
                "  when m.MVDC_VETDSTATUS = '4' then 'Погашен'  " +
                "  when m.MVDC_VETDSTATUS is null then 'Сертификата нет в M_VETDOCUMENT'  " +
                "  end \"Статус\" from " + tableTMP_UUID + " t left join m_vetdocument m on m.mgen_uuid = get_uuid(t.uuid)";


        String jsonOptions =
                "{\"Table_name\":\"" + tableTMP_UUID + "\"}" ; // multi

        writeLogMain(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
                "Просмотр временной таблицы",
                jsonOptions,"LOADING","");

         String response = "";
        try{Connection pullConn = ConnectionPool.getInstance().getConnectionMerc();
            Statement stmtPullM = pullConn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rsPullM = stmtPullM.executeQuery(showTmp_UUID);


            ResultSetMetaData rsdataM = rsPullM.getMetaData();
            int siz = rsdataM.getColumnCount();
            rsPullM.last();
            int countrows = rsPullM.getRow();
            rsPullM.beforeFirst();
            Object[] colNames = new String[siz];
            //Object[][] data = new Object[countrows][siz];
            for (int i=0; i<siz; i++) {
                colNames[i] = rsdataM.getColumnName(i+1);
            }
            int id=0;
            while (rsPullM.next()){
                for (int iii=0;iii<siz;iii++) {
                    // не закрывать соединение!!!!!
                    if(rsPullM.getString((String) colNames[iii])==null){
                        //data[id][iii]="null";
                        response+="null";
                        response+="!";
                    }else{
                        //data[id][iii] = rsLog.getString((String) colNames[iii]);

                        response+=rsPullM.getString((String) colNames[iii]);
                        response+="!";
                    }
                }
                response+="&";
                id++;
            }
            stmtPullM.close();
            pullConn.close();
            writeLogParent(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
                    "Просмотр временной таблицы",
                    jsonOptions,"OK","");
        }catch (SQLException e){
            writeLogParent(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
                    "Просмотр временной таблицы",
                    jsonOptions,"ERROR",""+e.toString());
        }

    return response;
    }

    public static String trunAutoGas() throws SQLException {
        String response="";
        Connection pullConn = ConnectionPool.getInstance().getConnectionMerc();
        Statement stmtPullM = pullConn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        //ResultSet rsPullM = stmtPullM.executeQuery(showTmp_UUID);
        String jsonOptions =
                "{\"Table_name\":\"" + "tmp_uuid" + "\"}" ; // multi

        writeLogMain(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
                "Очистка временной таблицы",
                jsonOptions,"LOADING","");

        try{
            stmtPullM.executeUpdate("truncate table tmp_uuid");
            //pullConn.commit();
            pullConn.close();
            response="clear_done";
            writeLogParent(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
                    "Очистка временной таблицы",
                    jsonOptions,"OK","");
        }catch (SQLException e){
            response=e.toString();
            writeLogParent(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
                    "Очистка временной таблицы",
                    jsonOptions,"ERROR",""+e.toString());
        }


        return response;
    }

    public static String insToAutoGas(String certs) throws SQLException {
        String response="";
        int countCerts=0;
        String tableTMP_UUID = "tmp_uuid";
        String[] listOfCerts = certs.split("!");
        Connection pullConn = ConnectionPool.getInstance().getConnectionMerc();
        Statement stmtPullM = pullConn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        //ResultSet rsPullM = stmtPullM.executeQuery(showTmp_UUID);
        String jsonOptions =
                "{\"Table_name\":\"" + "tmp_uuid" + "\"}" ; // multi

        writeLogMain(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
                "Добавление сертификатов в tmp",
                jsonOptions,"LOADING","");
            try{
                for(int i=0; i < listOfCerts.length; i++){
                //System.out.println(listOfCerts[i] + "" + i);
                if(!listOfCerts[i].equals("")){
                    stmtPullM.executeUpdate("insert into " + tableTMP_UUID + " values ('"+listOfCerts[i]+"')");
                    countCerts++;
                }

            }
                //stmtPullM.executeUpdate("truncate table tmp_uuid");
                //pullConn.commit();
                pullConn.close();
                writeLogParent(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
                        "Добавление сертификатов в tmp",
                        jsonOptions,"OK","");
            }catch (SQLException e){
                response = ""+e.toString();writeLogParent(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
                    "Добавление сертификатов в tmp",
                    jsonOptions,"ERROR",""+e.toString());
            }

            if(listOfCerts.length>=1){response=""+countCerts;}else{response="empty";}




        return response;
    }

    public static void upNewTime() throws SQLException {
                String sqlNewTime = "select to_char((min(CAQ_DATE_UTILIZE)-1), 'YYYY-MM-DD HH24:MI:SS') min_date\n" +
                "                 from c_Autoconfirm_Queue\n" +
                "                 where CAQ_STATUS = 'NEW'";
        Connection pullConn = ConnectionPool.getInstance().getConnectionMerc();
        Statement stmtPullM = pullConn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet rsPullMT = stmtPullM.executeQuery(sqlNewTime);
        while(rsPullMT.next()){ newTime = rsPullMT.getString("MIN_DATE");}
        rsPullMT.close();

    }

    public static String insToAutoGas2(String certs) throws SQLException {

        String response="";
        int countCerts=0;int AllConfirmCerts=0;int countCertsInQueue=0;
        String tableTMP_UUID = "tmp_uuid";
        String certsToUpdate="";
        String sqlCounter = "select MGEN_UUID, MVDC_VETDSTATUS, CAQ_STATUS, CAQ_ERROR_DETAILS, CAQ_DATE_UTILIZE\n" +
                "from m_vetdocument mvd\n" +
                "left join c_Autoconfirm_Queue caq on caq.CAQ_VSD_UUID = mvd.MGEN_UUID \n";
        if(certs.length() == 4){
            System.out.println("SAP");

             String newSqlCounter = sqlCounter +  " where caq.caq_codv_id = ( select codv_id from c_org_divisions where codv_code = '" + certs + "') and mvd.mvdc_vetdstatus !='4'";
            Connection pullConn = ConnectionPool.getInstance().getConnectionMerc();
            Statement stmtPullM = pullConn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            System.out.println("SAP1");

            String jsonOptions =
                            "{\"SAP\":\""  + certs + "\"}" ; // multi

            writeLogMain(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
                    "Добавление сертификатов_web",
                    jsonOptions,"LOADING","");

            try{


                ResultSet rsPullM = stmtPullM.executeQuery(newSqlCounter);
                ResultSetMetaData rsdataM = rsPullM.getMetaData();

                System.out.println("certs in cell");

                int siz = rsdataM.getColumnCount();
                rsPullM.last();
                int countrows = rsPullM.getRow();
                response += "" + countrows + "!"+countrows+"!";
                rsPullM.beforeFirst();
                Object[] colNames = new String[siz];


                //Object[][] data = new Object[countrows][siz];
                for (int i=0; i<siz; i++) {
                    colNames[i] = rsdataM.getColumnName(i+1);
                }
                int id=0;
                while (rsPullM.next()){

                    //System.out.println("printing");

                    if(rsPullM.getString((String) colNames[1]).equals("4")){
                        AllConfirmCerts++;
                    }
                    if(!(rsPullM.getString((String) colNames[2]).equals("null"))
                            & !(rsPullM.getString((String) colNames[1]).equals("4"))){
                        countCertsInQueue++;

                        StringBuffer sb = new StringBuffer(rsPullM.getString((String) colNames[0]).replace("-",""));
                        sb.insert(4,"-");sb.insert(9,"-");sb.insert(14,"-");
                        sb.insert(19,"-");sb.insert(24,"-");sb.insert(29,"-");sb.insert(34,"-");


                        certsToUpdate += sb + "_";
                    }
                }
                response +=""+AllConfirmCerts+"!"+""+countCertsInQueue+"!"+certsToUpdate;

                stmtPullM.close();
                pullConn.close();
                rsPullM.close();
                upNewTime();
                writeLogParent(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
                        "Добавление сертификатов_web",
                        jsonOptions,"OK","");

            }catch (SQLException e){
                stmtPullM.close();
                pullConn.close();
                //rsPullM.close();
                response = ""+e.toString();
                writeLogParent(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
                        "Добавление сертификатов_web",
                        jsonOptions,"ERROR",""+e.toString());
            }

            //if(listOfCerts.length>=1){response=""+countCerts;}else{response="empty";}


            System.out.println(sqlCounter);
            //System.out.println(response);
            //System.out.println(certsToUpdate);
            return response;

            //------as usual

        }
        else {
            System.out.println("usual");
            sqlCounter += "where MGEN_UUID in (";
            String[] listOfCerts = certs.split("!");
            response += ""+listOfCerts.length +"!";
            Connection pullConn = ConnectionPool.getInstance().getConnectionMerc();
            Statement stmtPullM = pullConn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            //
            String jsonOptions =
                    "{\"SAP\":\"" + "list" + "\"}" ; // multi

            writeLogMain(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
                    "Добавление сертификатов_web",
                    jsonOptions,"LOADING","");

            try{

                for(int i=0; i < listOfCerts.length; i++) {
                    //System.out.println(listOfCerts[i] + "" + i);
                    if (!listOfCerts[i].equals("")) {
                        StringBuffer sb = new StringBuffer(listOfCerts[i].replace("-",""));
                        sb.insert(4,"-");sb.insert(9,"-");sb.insert(14,"-");
                        sb.insert(19,"-");sb.insert(24,"-");sb.insert(29,"-");sb.insert(34,"-");

                        sqlCounter += "get_uuid('" + sb + "'),";

                    }
                }
                sqlCounter+=")";sqlCounter=sqlCounter.replace("),)","))");
                ResultSet rsPullM = stmtPullM.executeQuery(sqlCounter);
                ResultSetMetaData rsdataM = rsPullM.getMetaData();
                int siz = rsdataM.getColumnCount();
                rsPullM.last();
                int countrows = rsPullM.getRow();
                response +=""+countrows+"!";
                rsPullM.beforeFirst();
                Object[] colNames = new String[siz];
                //Object[][] data = new Object[countrows][siz];
                for (int i=0; i<siz; i++) {
                    colNames[i] = rsdataM.getColumnName(i+1);
                }
                int id=0;
                while (rsPullM.next()){
                    if(rsPullM.getString((String) colNames[1]).equals("4")){
                        AllConfirmCerts++;
                    }
                    if(!(rsPullM.getString((String) colNames[2]).equals("null"))
                            & !(rsPullM.getString((String) colNames[1]).equals("4"))){
                        countCertsInQueue++;
                        StringBuffer sb = new StringBuffer(rsPullM.getString((String) colNames[0]).replace("-",""));
                        sb.insert(4,"-");sb.insert(9,"-");sb.insert(14,"-");
                        sb.insert(19,"-");sb.insert(24,"-");sb.insert(29,"-");sb.insert(34,"-");
                        certsToUpdate += sb +"_";
                    }
                }
                response +=""+AllConfirmCerts+"!"+""+countCertsInQueue+"!"+certsToUpdate;

                stmtPullM.close();
                pullConn.close();
                rsPullM.close();
                upNewTime();
                writeLogParent(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
                        "Добавление сертификатов_web",
                        jsonOptions,"OK","");

            }catch (SQLException e){
                stmtPullM.close();
                pullConn.close();
                //rsPullM.close();
                response = ""+e.toString();writeLogParent(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
                        "Добавление сертификатов",
                        jsonOptions,"ERROR",""+e.toString());
            }

            //System.out.println(sqlCounter);
            //System.out.println(response);
            //System.out.println(certsToUpdate);
            return response;
        }

    }


    public static String updateOrInsert(String certs) throws SQLException {
        //System.out.println(newTime);
        String response="";

        int countCerts=0;int AllConfirmCerts=0;int countCertsInQueue=0;
        String tableTMP_UUID = "tmp_uuid";
       String sqlNewTime = "select to_char((min(CAQ_DATE_UTILIZE)-1), 'DD.MM.YYYY HH24:MI:SS') min_date\n" +
               "                 from c_Autoconfirm_Queue\n" +
               "                 where CAQ_STATUS = 'NEW'";

        //String sqlCounter = "select MGEN_UUID, MVDC_VETDSTATUS, CAQ_STATUS, CAQ_ERROR_DETAILS, CAQ_DATE_UTILIZE" +
        //        " from m_vetdocument mvd" +
        //        " left join c_Autoconfirm_Queue caq on caq.CAQ_VSD_UUID = mvd.MGEN_UUID" +
        //        " where MGEN_UUID in ( ";
        Connection pullConn = ConnectionPool.getInstance().getConnectionMerc();
        Statement stmtPullM = pullConn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

        String jsonOptions =
                "{\"SAP\":\"" + "list" + "\"}" ; // multi

      //  writeLogMain(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
        //        "update_certs_web",
          //      jsonOptions,"LOADING","");
        try{
         /*   if(newTime.equals("1")){
                ResultSet rsPullMT = stmtPullM.executeQuery(sqlNewTime);
                while(rsPullMT.next()){ newTime = rsPullMT.getString("MIN_DATE");}
                rsPullMT.close();
            }
         */
            String[] listOfCerts = certs.split("_");

            String listUpdate = "update c_Autoconfirm_Queue set CAQ_DATE_UTILIZE = to_date('" + newTime  + "','YYYY-MM-DD HH24:MI:SS') " +
                    " where CAQ_VSD_UUID in (";
            for(int i =0; i < listOfCerts.length;i++){
                listUpdate += "get_uuid('" + listOfCerts[i] + "'),";
            }
            listUpdate +=")";listUpdate = listUpdate.replace("),)","))");
            System.out.println(listUpdate);
            stmtPullM.executeUpdate(listUpdate);
            pullConn.close();
            stmtPullM.close();

            //System.out.println(listUpdate);
           // writeLogParent(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
           //         "update_certs_web",
           //         jsonOptions,"OK","");
            response = "ok";
        }
        catch (SQLException e){
            pullConn.close();
            stmtPullM.close();

            response = newTime + " "+e.toString();
            //writeLogParent(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
            //        "update_certs_web",
            //        jsonOptions,"ERROR",""+e.toString());

        }

        //System.out.println(sqlCounter);
        System.out.println(response);
        return response;
    }


    public static String insToMainAutoGas() throws SQLException {
        String response="";
        String tableTMP_UUID = "tmp_uuid";
        String querySapCode = "select codv_code from c_org_divisions where ment_guid in (\n" +
                "select distinct(MVDC_CONS_CONSIGNEE_ENTERPRISE) " +
                "from m_vetdocument where MGEN_UUID in (select get_uuid (UUID) from " + tableTMP_UUID + "))";

        String  insToauto = "INSERT INTO C_AUTOCONFIRM_QUEUE (\n" +
                "                CAQ_ID,\n" +
                "                CAQ_CODV_ID,\n" +
                "                CAQ_VSD_UUID,\n" +
                "                CAQ_HOSTNAME,\n" +
                "                CAQ_STATUS,\n" +
                "                CAQ_DATE_CREATED,\n" +
                "                CAQ_DATE_UTILIZE)\n" +
                "                select SEQ_AUTOCONFIRM_QUEUE.NEXTVAL, a.* from (SELECT \n" +
                "                distinct \n" +
                "                codv.CODV_ID,\n" +
                "                m.MGEN_UUID,\n" +
                "                'msk-dpro-app456.x5.ru', \n" +
                "                'NEW',\n" +
                "                m.MVDC_STATUSCHANGE_DATE,\n" +
                "                sysdate+0.3/24 as CAQ_DATE_UTILIZE \n" +
                "                FROM\n" +
                "                m_vetdocument m\n" +
                "                join " + tableTMP_UUID+
                " u on get_uuid (u.uuid)=m.mgen_uuid\n" +
                "                left join c_org_divisions codv on m.mvdc_cons_consignee_enterprise = codv.ment_guid\n" +
                "                left join m_enterprise m1 on m.mvdc_cons_consignee_enterprise = m1.mgve_guid\n" +
                "                left join m_enterprise m2 on m.mvdc_cons_broker = m2.mgve_guid\n" +
                "                left join m_enterprise m3 on m.mvdc_cons_consignor_enterprise = m3.mgve_guid\n" +
                "                left join c_org_divisions codv2 on m.mvdc_cons_consignor_enterprise = codv2.ment_guid\n" +
                "                left join c_autoconfirm_queue cq on m.mgen_uuid = cq.caq_vsd_uuid\n" +
                "                left join c_incoming_details cind on cind.cind_vetdocument_uuid = m.mgen_uuid\n" +
                "                left join c_incoming cinc on cind.cind_incomingdoc_id = cinc.cinc_id\n" +
                "                WHERE\n" +
                "                m.MVDC_VETDSTATUS IN (1,2)\n" +
                "                AND m.MGEN_UUID NOT IN (SELECT CAQ_VSD_UUID FROM C_AUTOCONFIRM_QUEUE)\n" +
                "                AND codv.CODV_CODE = '";
        int countCerts=0;
        Connection pullConn = ConnectionPool.getInstance().getConnectionMerc();
        Statement stmtPullM = pullConn.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        String SapCode = ""; //  JOptionPane.showInputDialog(null,
        //       "Введите SAP Код","SAP",JOptionPane.QUESTION_MESSAGE);
        String jsonOptions =
                "{\"Table_name\":\"" + "tmp_uuid" + "\"}" ; // multi

        writeLogMain(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
                "Добавление сертификатов в main",
                jsonOptions,"LOADING","");
        try {
            ResultSet rsPullM = stmtPullM.executeQuery(querySapCode);
            while (rsPullM.next()){SapCode = rsPullM.getString(1);}

        } catch (SQLException e) {

            e.printStackTrace();
        }
        try{
            stmtPullM.executeUpdate(insToauto+SapCode+"') a");
            response="insert_done";
            writeLogParent(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
                    "Добавление сертификатов в main",
                    jsonOptions,"OK","");
        }catch (SQLException e){
            response="insert_fail"+e.toString();
            writeLogParent(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
                    "Добавление сертификатов в main",
                    jsonOptions,"ERROR",""+e.toString());
        }



        //ResultSet rsPullM = stmtPullM.executeQuery(showTmp_UUID);



        return response;
    }

    public static String showAutoGas() throws SQLException {
        String tableTMP_UUID = "tmp_uuid";
        String showTableAuto = "select * FROM c_Autoconfirm_Queue  a where a.CAQ_VSD_UUID in (select get_uuid (UUID) from " +
                tableTMP_UUID + ")";
        String response = "";
        String jsonOptions =
                "{\"Table_name\":\"" + tableTMP_UUID + "\"}" ; // multi

        writeLogMain(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
                "Просмотр main таблицы",
                jsonOptions,"LOADING","");
        try{

            Connection pullConn = ConnectionPool.getInstance().getConnectionMerc();
            Statement stmtPullM = pullConn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet rsPullM = stmtPullM.executeQuery(showTableAuto);


            ResultSetMetaData rsdataM = rsPullM.getMetaData();
            int siz = rsdataM.getColumnCount();
            rsPullM.last();
            int countrows = rsPullM.getRow();
            rsPullM.beforeFirst();
            Object[] colNames = new String[siz];
            //Object[][] data = new Object[countrows][siz];
            for (int i=0; i<siz; i++) {
                colNames[i] = rsdataM.getColumnName(i+1);
            }
            int id=0;
            while (rsPullM.next()){
                for (int iii=0;iii<siz;iii++) {
                    // не закрывать соединение!!!!!
                    if(rsPullM.getString((String) colNames[iii])==null){
                        //data[id][iii]="null";
                        response+="null";
                        response+="!";
                    }else{
                        //data[id][iii] = rsLog.getString((String) colNames[iii]);
                        //System.out.println(""+colNames[iii].toString());
                        response+=rsPullM.getString((String) colNames[iii]);
                        response+="!";
                    }
                }
                response+="&";
                id++;
            }
            stmtPullM.close();
            pullConn.close();
            writeLogParent(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
                    "Просмотр main таблицы",
                    jsonOptions,"OK","");
        }catch (SQLException e){
            writeLogMain(NTLMUserFilter.getUserName(),"CADUCEUS","Автогашение",
                    "Просмотр main таблицы",
                    jsonOptions,"ERROR",""+e.toString());
        }


        return response;
    }

}
