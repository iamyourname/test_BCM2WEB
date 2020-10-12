package app.entities;

import app.servlets.NTLMUserFilter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static app.entities.Logs.writeLogMain;
import static app.entities.Logs.writeLogParent;


public class MassOut {
    public static String sqlErrorBuf="SELECT bh.id_header , ed.xml_data FROM alc.waybill_header wh LEFT JOIN sdd.bufheader bh ON wh.identity =  to_char(bh.id_header)  \n" +
            "LEFT JOIN alc.export_data ed ON ed.id=bh.id_header AND ed.data_type = 'OUT31'\n" +
            "WHERE wh.isoutdoc = 1 AND bac_status='invalid'\n" +
            "AND bh.id_header IS NOT NULL\n" +
            "AND ed.dt_created =  (SELECT MAX(ed22.dt_created) FROM alc.export_data ed22 WHERE ed22.data_type = 'OUT31' AND ed22.id = bh.id_header )\n" +
            "AND xml_data LIKE '%<TransactionID>'||to_char(bh.id_header)||'</TransactionID>%'";

    public static String getSqlErrorBufPost = "DECLARE \n" +
            "vRESULT_CLOB CLOB;\n" +
            "vNum NUMBER;\n" +
            "BEGIN\n" +
            "FOR ii IN \n" +
            "(SELECT bh.id_header , ed.xml_data FROM alc.waybill_header wh LEFT JOIN sdd.bufheader bh ON wh.identity =  to_char(bh.id_header)  \n" +
            "LEFT JOIN alc.export_data ed ON ed.id=bh.id_header AND ed.data_type = 'OUT31'\n" +
            "WHERE wh.isoutdoc = 1 AND bac_status='invalid'\n" +
            "AND bh.id_header IS NOT NULL\n" +
            "AND ed.dt_created =  (SELECT MAX(ed22.dt_created) FROM alc.export_data ed22 WHERE ed22.data_type = 'OUT31' AND ed22.id = bh.id_header )\n" +
            "AND xml_data LIKE '%<TransactionID>'||to_char(bh.id_header)||'</TransactionID>%'\n" +
            ")\n" +
            "LOOP\n" +
            "      vNUM:=NULL; \n" +
            "             alc.bac_utils.SEND(pREQUEST_MSG => ii.xml_data,\n" +
            "          pREPLY_MSG => vRESULT_CLOB,\n" +
            "         PINTEG_NAME => 'OUT31',\n" +
            "             pEXP_ID => ii.id_header,\n" +
            "         pRES_LINENO => vNUM);\n" +
            "END LOOP;\n" +
            "\n" +
            "END;";

    public static String searchErrorsFromNQ(String sap) throws SQLException {
        String response = "";
        try{
            Connection pullConnNq = ConnectionPool.getInstance().getConnectionNQ(sap);
            Statement stmtPullNq = pullConnNq.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            //ConnectToBD.connectNQ(Integer.parseInt(sap));

            ResultSet rsPullNq = stmtPullNq.executeQuery(sqlErrorBuf);
            rsPullNq.last();
            int countErrs = rsPullNq.getRow();
            rsPullNq.beforeFirst();
            if(countErrs==0){
                response="Ошибочные буферы не найдены";
                //response +="123,321,122,133";
            }else{
                while(rsPullNq.next()){
                    response+=rsPullNq.getString(1)+",";
                }


            }
            pullConnNq.close();
        }catch (SQLException e){
            System.out.println(e.toString());
        }

     return response;
    }

    public static boolean confrimMassSend(String sap) throws SQLException {
        boolean response = false;

        String jsonOptions =
                "{\"UserName\":\"" + NTLMUserFilter.getUserName() + "\"" +
                        "\"Web\":\"true\"," + "}"; // multi

        try{
            Connection pullConnNq = ConnectionPool.getInstance().getConnectionNQ(sap);
            Statement stmtPullNq = pullConnNq.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);


            writeLogMain(NTLMUserFilter.getUserName(),"BACCHUS","Отгрузка",
                    "MASS_ERROR",
                    jsonOptions,"LOADING","");

            stmtPullNq.execute(getSqlErrorBufPost);

            writeLogParent(NTLMUserFilter.getUserName(),"BACCHUS","Отгрузка",
                    "MASS_ERROR",
                    jsonOptions,"OK","");


            response = true;
            pullConnNq.close();
        }catch (SQLException e){

            writeLogParent(NTLMUserFilter.getUserName(),"BACCHUS","Отгрузка",
                    "MASS_ERROR",
                    jsonOptions,"OK","");

            System.out.println(e.toString());
        }

        return response;
    }

}
