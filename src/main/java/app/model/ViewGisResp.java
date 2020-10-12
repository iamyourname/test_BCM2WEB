package app.model;

import app.servlets.NTLMUserFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static app.entities.Logs.writeLogParent;

public class ViewGisResp {
    public static HttpURLConnection conGis;

    private static ViewGisResp viewGisResp = new ViewGisResp();



    public static ViewGisResp getInstance() {
        return viewGisResp;
    }


    public String checkOwner(String cis, String inn) throws IOException {
        String respose="";
        String toSend = "{\"cis\": \""+cis+"\",\"userInn\": \""+inn+"\"}";
        String url = "http://motpsender.prod.os-pub.x5.ru/statusCisIn";
        byte[] out;
        URL obj = new URL(url);
        conGis = (HttpURLConnection) obj.openConnection();
        conGis.setRequestMethod("POST");
        conGis.setRequestProperty("Content-Type", "Application/json");
        out = toSend.getBytes(StandardCharsets.UTF_8);
        conGis.setDoOutput(true);


        OutputStream os = conGis.getOutputStream();

            os.write(out);
            os.flush();
            int  response = conGis.getResponseCode();

            if(response != 200){
                System.out.println(response + "" + conGis.getErrorStream().toString());
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conGis.getErrorStream(), StandardCharsets.UTF_8));
                StringBuffer responseList = new StringBuffer();
                while ((respose = in.readLine()) != null) {
                    responseList.append(respose + "\n");
                }
                in.close();
                respose = String.valueOf(responseList);
                conGis.disconnect();
            }else{
                System.out.println(response);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conGis.getInputStream(), StandardCharsets.UTF_8));
                StringBuffer responseList = new StringBuffer();
                while ((respose = in.readLine()) != null) {
                    responseList.append(respose + "\n");
                }
                in.close();
                respose = String.valueOf(responseList);
                conGis.disconnect();
            }




        return respose;
    }


}
