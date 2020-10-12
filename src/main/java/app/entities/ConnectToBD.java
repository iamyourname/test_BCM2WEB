package app.entities;

import java.sql.*;
//import static app.entities.InitConnections.*;
import static app.entities.Logs.writeLogMain;

public class ConnectToBD {

    //---------------------БАХУС-------------------------
    final public static String driverNameB = "oracle.jdbc.driver.OracleDriver";
    public static String urlB;
    public static Connection connB;
    public static String serverB = "BACCHUS-AGENT";
    public static String portB = "1521";
    public static String servicenameB = "bacchus";
    public static String usernameB = "BAHU_CAGDB_PROD_X5_";
    public static String passwordB = "BAHU_CAGDB_PROD_X5_";
    public static ResultSet rsB;
    public static Statement stmtB;
    public static boolean isConnectedB;
    //--------------------------------------------------------

    //---------------------NQ-------------------------
    /*
    final public static String driverNameNQ = "oracle.jdbc.driver.OracleDriver";
    public static String urlNQ;
    public static Connection connNQ;
    public static String serverNQ = "BACCHUS-AGENT";
    public static String portNQ = "1521";
    public static String servicenameNQ = "bacchus";
    public static String usernameNQ = "BAHU_CAGDB_PROD_X5_";
    public static String passwordNQ = "BAHU_CAGDB_PROD_X5_";
    public static ResultSet rsNQ;
    public static Statement stmtNQ;
    public static boolean isConnectedNQ;
    */
    //--------------------------------------------------------
    //---------------------Логи-------------------------
    final public static String driverNamePostgres = "org.postgresql.Driver";
    public static String urlLog;
    public static Connection connLog;
    public static ResultSet rsLog;
    public static Statement stmtLog;
    public static boolean isConnectedLog;

    public static boolean connectB(String agent) throws SQLException {



            try {
                urlB = "jdbc:oracle:thin:@" + serverB + agent + "-DB" + ":" + portB + "/" + servicenameB;
                 //System.out.println(urlB);
                Class.forName(driverNameB);
                connB = DriverManager.getConnection(urlB, usernameB + agent, passwordB + agent + "-password");
                stmtB = connB.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                isConnectedB = true;
            } catch (ClassNotFoundException | SQLException e) {
                isConnectedB = false;
               // System.out.println(e.toString());
            }
        return isConnectedB;
        }

        /*
    public static boolean connectNQ(int SAP) throws SQLException {

            try {
                urlNQ = "jdbc:oracle:thin:@" + bdurls[SAP][1] + ":" + "PRD1";
                //System.out.println(urlNQ);
                Class.forName(driverNameNQ);
                connNQ = DriverManager.getConnection(urlNQ, "wh1", bdurls[SAP][2]);
                stmtNQ = connNQ.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

                if (connNQ.equals(null))
                    isConnectedNQ = false;
                else
                    isConnectedNQ = true;


            } catch (ClassNotFoundException | SQLException e) {

                isConnectedNQ = false;
            }
            return isConnectedNQ;
        }

        */

    public static boolean connectToLog() throws SQLException, ClassNotFoundException {

        try {
            urlLog = "jdbc:postgresql://msk-dpro-apl372:5432/postgres";
            //System.out.println(urlLog);
            Class.forName(driverNamePostgres);
            connLog = DriverManager.getConnection(urlLog, "bcm_writer", "q6GHn7uE");
            stmtLog = connLog.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            //System.out.println("connecting: " + url);
            if (connLog.equals(null))
                isConnectedLog = false;
            else
                isConnectedLog = true;
        } catch (SQLException e) {
            //err = "SQLException\n" + e.getMessage();
            isConnectedLog = false;
        }
        connLog.setAutoCommit(false);


        return isConnectedLog;

    }
}



