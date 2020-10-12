package app.servlets.bacchus;

import app.entities.ConnectToBD;
import app.entities.ConnectionPool;
import app.entities.RcToAgent;
import app.model.ViewResult;

import java.sql.SQLException;
import java.util.Arrays;

public class Out31 {

    public static  Object[][] out31Data;

    public static String searchOut31(String SapOut31, String buffOut31){

        String outResponse="";
        String agent = "";
        for(int i = 0; i < RcToAgent.rcAgent.length; i++){
            if (RcToAgent.rcAgent[i][0].equals(SapOut31)){
                agent = RcToAgent.rcAgent[i][2];
            }
        }
        //------------Соединение с БД БАХУС
        if(Integer.parseInt(agent)<9)agent="0"+agent;


        ViewResult viewResult = ViewResult.getInstance();

        //------------Поиск потока в БД БАХУС
        try {
            out31Data = viewResult.ViewBuffOutFromBD(buffOut31,SapOut31, agent);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (Object[] out31Datum : out31Data) {
            outResponse += Arrays.toString(out31Datum);
            //out.append(Arrays.toString(out31Datum));
        }
        //outResponse += "$";
        //ConnectToBD.connectNQ(Integer.parseInt(SapOut31));
        // System.out.println("OK");
        //------------Поиск потока в БД РЦ
        try {
            String resultFromNq = viewResult.ViewBuffOutFromNQ(buffOut31, SapOut31);
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
