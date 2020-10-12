package app.model;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static app.entities.ConnectToBD.rsLog;
import static app.entities.ConnectToBD.stmtLog;


public class ViewLog {
    private static ViewLog viewLog = new ViewLog();



    public static ViewLog getInstance() {
        return viewLog;
    }

    public String getQueryFromLog(String usr, String sys, String stat, String per) throws SQLException {
        String response="";
        String queryToShow="select * from  bcm_log bl where 1=1 ";
        if(!usr.equals("def") && !usr.equals("all")){

            queryToShow += "and bl.user ='"+usr + "'";
        }
        if(!sys.equals("def") && !sys.equals("all")){
            queryToShow += "and bl.system ='"+sys + "'";
        }
        if(!stat.equals("def") && !stat.equals("all")){
            queryToShow += "and bl.status ='"+stat + "'";
        }

        if(!per.equals("def")){

            switch (per){
                case "all":
                    queryToShow +="";
                    break;
                case "day" :
                    queryToShow += "and bl.dt_created > (NOW() - INTERVAL '1 DAY')";
                    break;
                case "3day" :
                    queryToShow += "and bl.dt_created > (NOW() - INTERVAL '3 DAY')";
                    break;
                default:

                    String[] fromTO = per.split("\\^");
                    queryToShow += "and bl.dt_created >= '" + fromTO[0] +
                            "' and bl.dt_created <= '" + fromTO[1] + "'";

            }

            }



        queryToShow += " order by id";
        System.out.println(queryToShow);


            rsLog = stmtLog.executeQuery(queryToShow);
            ResultSetMetaData rsdata = rsLog.getMetaData();
            int siz = rsdata.getColumnCount();
            rsLog.last();
            int countrows = rsLog.getRow();
            rsLog.beforeFirst();
            Object[] colNames = new String[siz];
            //Object[][] data = new Object[countrows][siz];
            for (int i=0; i<siz; i++) {
                colNames[i] = rsdata.getColumnName(i+1);
            }
            int id=0;
            while (rsLog.next()){
                for (int iii=0;iii<siz;iii++) {
    // не закрывать соединение!!!!!
                    if(rsLog.getString((String) colNames[iii])==null){
                        //data[id][iii]="null";
                        response+="null";
                        response+="!";
                    }else{
                        //data[id][iii] = rsLog.getString((String) colNames[iii]);
                        response+=rsLog.getString((String) colNames[iii]);
                        response+="!";
                    }
                }
                response+="&";
                id++;
            }
            return response;


    }

}
