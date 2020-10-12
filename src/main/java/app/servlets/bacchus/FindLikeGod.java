package app.servlets.bacchus;

import app.entities.RcToAgent;
import app.model.ViewResult;

import java.sql.SQLException;
import java.util.Arrays;

public class FindLikeGod {


    public static String godFind(String GodBuf, String GodSAP){
        String GodResponse="";

        //нашли агент
        String agent = "";
        for(int i = 0; i < RcToAgent.rcAgent.length; i++){
            if (RcToAgent.rcAgent[i][0].equals(GodSAP)){
                agent = RcToAgent.rcAgent[i][2];
            }
        }
        //------------

        if(Integer.parseInt(agent)<9)agent="0"+agent;

        ViewResult viewResult = ViewResult.getInstance();

        try {
            System.out.println("FindLikeGod");
           Object[][] out31Data = viewResult.ViewBuffGodOutFromBD(GodBuf,GodSAP, agent); // ошибка
           Object[][] out31Data2 = viewResult.ViewTasksFromBD(GodBuf,GodSAP, agent); // таски
            Object[][] out31Data3 = viewResult.ViewUTMFromBD(GodBuf,GodSAP, agent); // утмдокс

            for (Object[] out31Datum : out31Data) {
                GodResponse += Arrays.toString(out31Datum);
                //out.append(Arrays.toString(out31Datum));
            }

            GodResponse+="&&";

            for (Object[] out31Datum : out31Data2) {
                GodResponse += Arrays.toString(out31Datum);
                //out.append(Arrays.toString(out31Datum));
            }

            //ViewUTMFromBD

            GodResponse+="&&";

            for (Object[] out31Datum : out31Data3) {
                GodResponse += Arrays.toString(out31Datum);
                //out.append(Arrays.toString(out31Datum));
            }

        } catch (SQLException  e) {
            System.out.println("findlike"+e.toString());
            e.printStackTrace();
        }





        return GodResponse;
    }


}
