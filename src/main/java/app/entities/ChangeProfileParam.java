package app.entities;

import app.servlets.NTLMUserFilter;

import java.sql.SQLException;

import static app.entities.ConnectToBD.*;

public class ChangeProfileParam {

    public static String changeParam(String param, String value) throws SQLException, ClassNotFoundException {
        String response="";

        switch (param){
            case "tableAutoGas":
                connectToLog();
                stmtLog.executeUpdate("update bcm_users  set \n" +
                        "settings = settings::jsonb || '{\"Таблица автогашения\":\""+value+"\"}'::jsonb\n" +
                        "where user_name = '"+ NTLMUserFilter.getUserName() + "'");
                System.out.println("update bcm_users  set \n" +
                        "settings = settings::jsonb || '{\"Таблица автогашения\":\""+value+"\"}'::jsonb\n" +
                        "where user_name = '"+ NTLMUserFilter.getUserName() + "'");
                connLog.commit();
                response ="table_changed";
                break;


        }

        return response;
    }

}
