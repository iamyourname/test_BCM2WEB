package app.entities;

import app.model.ViewResult;
import app.servlets.NTLMUserFilter;
import app.servlets.bacchus.Out31;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import static app.entities.Logs.writeLogMain;
import static app.entities.Logs.writeLogParent;

public class FlowSend {
    public static String[] apps31 = {"31поток",
            "http://msk-dpro-app573:8080/bacchus/ship/confirm",
            "http://msk-dpro-app621:8080/bacchus/ship/confirm",
            "http://msk-dpro-app621:8080/bacchus/ship/confirm",
            "http://msk-dpro-app621:8080/bacchus/ship/confirm",
            "http://msk-dpro-app621:8080/bacchus/ship/confirm",
            "http://msk-dpro-app621:8080/bacchus/ship/confirm",
            "http://msk-dpro-app621:8080/bacchus/ship/confirm",
            "http://msk-dpro-app621:8080/bacchus/ship/confirm",
            "http://msk-dpro-app621:8080/bacchus/ship/confirm",
            "http://msk-dpro-app731:8080/bacchus/ship/confirm",
            "http://msk-dpro-app897:8080/bacchus/ship/confirm",
            "http://msk-dpro-app900:8080/bacchus/ship/confirm"};
    public static String[] apps24 = {"24поток",
            "http://msk-dpro-app573:8080/bacchus/supply/receipt/confirm",
            "http://msk-dpro-app621:8080/bacchus/supply/receipt/confirm",
            "http://msk-dpro-app621:8080/bacchus/supply/receipt/confirm",
            "http://msk-dpro-app621:8080/bacchus/supply/receipt/confirm",
            "http://msk-dpro-app621:8080/bacchus/supply/receipt/confirm",
            "http://msk-dpro-app621:8080/bacchus/supply/receipt/confirm",
            "http://msk-dpro-app621:8080/bacchus/supply/receipt/confirm",
            "http://msk-dpro-app621:8080/bacchus/supply/receipt/confirm",
            "http://msk-dpro-app621:8080/bacchus/supply/receipt/confirm",
            "http://msk-dpro-app731:8080/bacchus/supply/receipt/confirm",
            "http://msk-dpro-app897:8080/bacchus/supply/receipt/confirm",
            "http://msk-dpro-app900:8080/bacchus/supply/receipt/confirm"};
    public static HttpURLConnection con;
    public static HttpURLConnection con24;

    public static String SendFlowFromRC(String Sap, String buff) throws IOException, SQLException {

        ViewResult viewResult = new ViewResult();

        String toPost = viewResult.ViewBuffOutFromNQ(buff, Sap);

        String agent="";

        byte[] out;

        for(int i = 0; i < RcToAgent.rcAgent.length; i++){
            if (RcToAgent.rcAgent[i][0].equals(Sap)){
                agent = RcToAgent.rcAgent[i][2];
            }
        }

        String jsonOptions =
                "{\"BUFF\":\"" + buff + "\"," +
                        "\"CODV_CODE\":\""+ Sap +"\"," +
                        "\"TYPE\":\"SINGLE\","+
                        "\"FROM\":\"WEB\"}"; // multi

        writeLogMain(NTLMUserFilter.getUserName(),"BACCHUS","Отгрузка",
                "Переотправка 31",
                jsonOptions,"LOADING","");

        String url = apps31[Integer.parseInt(agent)];
        URL obj = new URL(url);
        con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "Application/xml");
        System.out.println(url);


        //con.setRequestProperty("StandardCharsets","UTF-8");


        //Send post request
        out = toPost.getBytes(StandardCharsets.UTF_8);
        con.setDoOutput(true);


        try (OutputStream os = con.getOutputStream()) {

            os.write(out);
            os.flush();
          int  response = con.getResponseCode();
            writeLogParent(NTLMUserFilter.getUserName(),"BACCHUS","Отгрузка",
                    "Переотправка 31",
                    jsonOptions,"OK","");
        }catch (IOException e){
            writeLogParent(NTLMUserFilter.getUserName(),"BACCHUS","Отгрузка",
                    "Переотправка 31",
                    jsonOptions,"ERROR",e.toString());
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));


        String inputLine = "";
        StringBuffer responseList = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            responseList.append(inputLine + "\n");
        }

        in.close();
        inputLine = String.valueOf(responseList);
        inputLine = inputLine.replace("><", ">\n<");
        con.disconnect();

    return inputLine;
    }


    public static String SendInFlowFromRC(String Sap, String buff) throws IOException, SQLException {

        ViewResult viewResult = new ViewResult();

        String toPost = viewResult.ViewBuffInFromNQ(buff, Sap);

        String agent="";

        byte[] out;

        for(int i = 0; i < RcToAgent.rcAgent.length; i++){
            if (RcToAgent.rcAgent[i][0].equals(Sap)){
                agent = RcToAgent.rcAgent[i][2];
            }
        }

        String jsonOptions =
                "{\"BUFF\":\"" + buff + "\"," +
                        "\"CODV_CODE\":\""+ Sap +"\"," +
                        "\"TYPE\":\"SINGLE\","+
                        "\"FROM\":\"WEB\"}"; // multi

        writeLogMain(NTLMUserFilter.getUserName(),"BACCHUS","Приемка",
                "Переотправка 24",
                jsonOptions,"LOADING","");

        String url = apps24[Integer.parseInt(agent)];
        URL obj = new URL(url);
        con24 = (HttpURLConnection) obj.openConnection();
        con24.setRequestMethod("POST");
        con24.setRequestProperty("Content-Type", "Application/xml");
        System.out.println(url);


        //con.setRequestProperty("StandardCharsets","UTF-8");


        //Send post request
        out = toPost.getBytes(StandardCharsets.UTF_8);
        con24.setDoOutput(true);


        try (OutputStream os = con24.getOutputStream()) {

            os.write(out);
            os.flush();
            int  response = con24.getResponseCode();
            writeLogParent(NTLMUserFilter.getUserName(),"BACCHUS","Приемка",
                    "Переотправка 24",
                    jsonOptions,"OK","");
        }catch (IOException e){
            writeLogParent(NTLMUserFilter.getUserName(),"BACCHUS","Приемка",
                    "Переотправка 24",
                    jsonOptions,"ERROR",e.toString());
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con24.getInputStream(), StandardCharsets.UTF_8));


        String inputLine = "";
        StringBuffer responseList = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            responseList.append(inputLine + "\n");
        }

        in.close();
        inputLine = String.valueOf(responseList);
        inputLine = inputLine.replace("><", ">\n<");
        con24.disconnect();

        return inputLine;
    }


    public static String SendAfterEditFromRC(String Sap, String buff) throws IOException, SQLException {



       // ViewResult viewResult = new ViewResult();

       // String toPost = buff;

        String agent="";

        //System.out.println("SendAfterEditFromRC русский"+buff);

        byte[] out;

        for(int i = 0; i < RcToAgent.rcAgent.length; i++){
            if (RcToAgent.rcAgent[i][0].equals(Sap)){
                agent = RcToAgent.rcAgent[i][2];
            }
        }

/*
        String jsonOptions =
                "{\"BUFF\":\"" + buff + "\"," +
                        "\"CODV_CODE\":\""+ Sap +"\"," +
                        "\"TYPE\":\"EDIT\","+
                        "\"FROM\":\"WEB\"}"; // multi

        writeLogMain(NTLMUserFilter.userName,"BACCHUS","Отгрузка",
                "Переотправка 31",
                jsonOptions,"LOADING","");
    */

        String url = apps31[Integer.parseInt(agent)];
        URL obj = new URL(url);
        con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/xml");
      //  System.out.println(url);


        //con.setRequestProperty("StandardCharsets","UTF-8");


        //Send post request
        out = buff.getBytes(StandardCharsets.UTF_8);
        con.setDoOutput(true);


        try (OutputStream os = con.getOutputStream()) {

            os.write(out);
            os.flush();
            int  response = con.getResponseCode();
      /*
            writeLogParent(NTLMUserFilter.userName,"BACCHUS","Отгрузка",
                    "Переотправка 31",
                    jsonOptions,"OK","");
    */
        }catch (IOException e){
           System.out.println(e.toString());
           /*
            writeLogMain(NTLMUserFilter.userName,"BACCHUS","Отгрузка",
                    "Переотправка 31",
                    jsonOptions,"ERROR","");

            */
        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));


        String inputLine = "";
        StringBuffer responseList = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            responseList.append(inputLine + "\n");
        }

        in.close();
        inputLine = String.valueOf(responseList);
        inputLine = inputLine.replace("><", ">\n<");
        con.disconnect();

        return inputLine;
    }

    public static String Send24AfterEditFromRC(String Sap, String buff) throws IOException, SQLException {



        // ViewResult viewResult = new ViewResult();

        // String toPost = buff;

        String agent="";

        //System.out.println("SendAfterEditFromRC русский"+buff);

        byte[] out;

        for(int i = 0; i < RcToAgent.rcAgent.length; i++){
            if (RcToAgent.rcAgent[i][0].equals(Sap)){
                agent = RcToAgent.rcAgent[i][2];
            }
        }





        String url = apps24[Integer.parseInt(agent)];
        URL obj = new URL(url);
        con24 = (HttpURLConnection) obj.openConnection();
        con24.setRequestMethod("POST");
        con24.setRequestProperty("Content-Type", "application/xml");
        //  System.out.println(url);


        //con.setRequestProperty("StandardCharsets","UTF-8");


        //Send post request
        out = buff.getBytes(StandardCharsets.UTF_8);
        con24.setDoOutput(true);


        try (OutputStream os = con24.getOutputStream()) {

            os.write(out);
            os.flush();
            int  response = con24.getResponseCode();

        }catch (IOException e){
            System.out.println(e.toString());

        }

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con24.getInputStream(), StandardCharsets.UTF_8));


        String inputLine = "";
        StringBuffer responseList = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            responseList.append(inputLine + "\n");
        }

        in.close();
        inputLine = String.valueOf(responseList);
        inputLine = inputLine.replace("><", ">\n<");
        con24.disconnect();

        return inputLine;
    }

}
