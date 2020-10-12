package app.model;

import app.servlets.NTLMUserFilter;


import java.sql.ResultSetMetaData;
import java.sql.SQLException;



import static app.entities.ConnectToBD.rsLog;
import static app.entities.ConnectToBD.stmtLog;

public class ViewUserSettings {
    public static ViewUserSettings viewUserSettings = new ViewUserSettings();

    public static ViewUserSettings getInstance(){return viewUserSettings;}

    private static String[][] userParams = new String[10][2];
    private static String[][] allUsersTableAuto = new String[23][2];

    public void initUserParams() throws SQLException {
        String sqlTableName = "select user_name, settings -> 'Таблица автогашения' \"use_table\" from public.bcm_users\n" +
                "where settings ->>'Таблица автогашения' !=  ''";
        rsLog = stmtLog.executeQuery("select bu.settings from bcm_users bu where upper(user_name) =upper(\'"
                + NTLMUserFilter.getUserName()+"\')");
        String[] jsonParams = new String[10];
        // Считываем json

        while(rsLog.next()){
            jsonParams = (rsLog.getString("settings").replaceAll("[{-}]","")).split(",");
        }

        for(int i=0; i < jsonParams.length; i++){
            String[] sKeyParam = jsonParams[i].split(":");
            userParams[i][0] = sKeyParam[0].replace("\"","");
            userParams[i][1] = sKeyParam[1].replace("\"","");
            System.out.println(userParams[i][0]+" " + userParams[i][1]);
        }

        //------------------------------------------


        rsLog = stmtLog.executeQuery(sqlTableName);
        int ii=1;
        while(rsLog.next()){
            //jsonParams = (rsLog.getString("settings").replaceAll("[{-}]","")).split(",");
            allUsersTableAuto[ii][0] = rsLog.getString("user_name");
            allUsersTableAuto[ii][1] = rsLog.getString("use_table");
            ii++;
            //System.out.println(userParams[i][0]+" " + userParams[i][1])
        }

    }

    public  String getTableAutoGas(){
        return userParams[1][1];
    }

    public  String[][] getAllUsersTableAuto(){
        return allUsersTableAuto;
    }






}
