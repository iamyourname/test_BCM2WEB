package app.servlets.bacchus;

import app.entities.ConnectToBD;
import app.entities.ConnectionPool;
import app.entities.RcToAgent;
import app.model.ViewResult;

import java.sql.SQLException;
import java.util.Arrays;

public class In24 {

    public static  Object[][] in24Data;


    public static String searchIn24(String SapIn24, String buffIn24){

        String outResponse="";
        String agent = "";
        for(int i = 0; i < RcToAgent.rcAgent.length; i++){
            if (RcToAgent.rcAgent[i][0].equals(SapIn24)){
                agent = RcToAgent.rcAgent[i][2];
            }
        }
        //------------Соединение с БД БАХУС
        if(Integer.parseInt(agent)<9)agent="0"+agent;


        ViewResult viewResult = ViewResult.getInstance();

        //------------Поиск потока в БД БАХУС
        try {
            in24Data = viewResult.ViewBuffInFromBD(buffIn24,SapIn24, agent);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (Object[] in24Datum : in24Data) {
            outResponse += Arrays.toString(in24Datum);
            //out.append(Arrays.toString(out31Datum));
        }
        //outResponse += "$";
        //ConnectToBD.connectNQ(Integer.parseInt(SapIn24));
        // System.out.println("OK");
        //------------Поиск потока в БД РЦ
        try {
            String resultFromNq = viewResult.ViewBuffInFromNQ(buffIn24, SapIn24);
           // System.out.println(resultFromNq);
            outResponse += resultFromNq;
            outResponse += "$$";
            //out.append(resultFromNq);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return outResponse;
    }

}
