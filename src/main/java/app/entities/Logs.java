package app.entities;

import org.postgresql.util.PGobject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static app.entities.ConnectToBD.connLog;

public class Logs {
    public static int parentID=0;
    public static int parentID31=0;

    public static void writeLogMain(
            String username,
            String systemName,
            String processName,
            String operationName,
            //String typeName,
            String options,
            String statusName,
            String error_log ) throws SQLException {
        //System.out.println(options);

        parentID=0;
        String LogStringMain = "insert into public.bcm_log  " +
                "(\"user\", " +
                "\"system\"," +
                " process," +
                " operation," +
                " \"type\"," +
                //" parent_id," +
                " \"options\"," +
                " status," +
                " error_log)\n values (?,?,?,?,?,?,?,?)";


        PreparedStatement pstmMain = connLog.prepareStatement(LogStringMain, Statement.RETURN_GENERATED_KEYS);

        PGobject jsonObject = new PGobject();
        jsonObject.setType("json");
        jsonObject.setValue(options);

        pstmMain.setString(1, username); //имя пользователя
        pstmMain.setString(2, systemName); // система
        pstmMain.setString(3,processName); // процесс
        pstmMain.setString(4,operationName); // операция
        pstmMain.setString(5,"REQUEST"); // тип операции
        //pstmMain.setInt(6,parrentForMain); // ID родительской операции, 0 - главная операция
        pstmMain.setObject(6, jsonObject); // Параметры операции
        pstmMain.setString(7,statusName); // Статус операции
        pstmMain.setString(8,error_log); // ошибки при выполнении

        pstmMain.execute();

        ResultSet rs = pstmMain.getGeneratedKeys();
        connLog.commit();


        if (rs.next()) {
            // Значение ID
            parentID = rs.getInt(1);
            parentID31 = rs.getInt(1);
        }
        //System.out.println("ID value: " + parentID);
    }


    public static void writeLogParent(
            String username,
            String systemName,
            String processName,
            String operationName,
            //String typeName,
            String options,
            String statusName,
            String error_log ) throws SQLException {

        String LogStringParent = "insert into public.bcm_log  " +
                "(\"user\", " +
                "\"system\"," +
                " process," +
                " operation," +
                " \"type\"," +
                " parent_id," +
                " \"options\"," +
                " status," +
                " error_log)\n values (?,?,?,?,?,?,?,?,?)";


        PreparedStatement pstmParent = connLog.prepareStatement(LogStringParent, Statement.RETURN_GENERATED_KEYS);

        PGobject jsonObject = new PGobject();
        jsonObject.setType("json");
        jsonObject.setValue(options);
        //jsonObject.setValue("{\"SAP\":\"0193\",\"docs\":\"124500\"}");
        //pstmt.setObject(11, jsonObject);

        pstmParent.setString(1, username); //имя пользователя
        pstmParent.setString(2, systemName); // система
        pstmParent.setString(3,processName); // процесс
        pstmParent.setString(4,operationName); // операция
        pstmParent.setString(5,"RESPONSE"); // тип операции
        pstmParent.setInt(6,parentID); // ID родительской операции
        pstmParent.setObject(7, jsonObject); // Параметры операции
        pstmParent.setString(8,statusName); // Статус операции
        pstmParent.setString(9,error_log); // ошибки при выполнении

        try {
            pstmParent.execute();
        }catch (SQLException ignored){}

        connLog.commit();
    }




}
