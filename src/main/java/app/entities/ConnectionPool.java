package app.entities;


import java.sql.*;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class ConnectionPool {

    //public static ResultSet rsPullB;


    private ConnectionPool(){
        //private constructor
    }

    private static ConnectionPool instance = null;

    public static ConnectionPool getInstance(){
        if (instance==null)
            instance = new ConnectionPool();
        return instance;
    }

    public Connection getConnection(String agent) throws SQLException {
        Context ctx;
        Connection c = null;
        Statement stmtPullB;
        try {
            ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/agent_"+agent);

            c = ds.getConnection();
        } catch (NamingException | SQLException e) {
            e.printStackTrace();
        }
         //
        //c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);




        return c;
    }
    public Connection getConnectionNQ(String Sap) throws SQLException {
        Context ctx;
        Connection cNQ = null;
        Statement stmtPullNQ;
        try {
            ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbcnq/"+Sap);

            cNQ = ds.getConnection();
        } catch (NamingException | SQLException e) {
            e.printStackTrace();
        }
        //
        //c.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        return cNQ;
    }

    public Connection getConnectionMerc() throws SQLException {
        Context ctx;
        Connection cMerc = null;
        Statement stmtPullMerc;
        try {
            ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/Merc_prod");
            cMerc = ds.getConnection();
        } catch (NamingException | SQLException e) {
            e.printStackTrace();
        }
        return cMerc;
    }

}
