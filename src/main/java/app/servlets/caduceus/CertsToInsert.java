package app.servlets.caduceus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import static app.servlets.caduceus.AutoGas.newTime;

public class CertsToInsert extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        //System.out.println("get");


    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Content-Type","Application/xml");
        PrintWriter outCerts = resp.getWriter();
        BufferedReader respik = new BufferedReader(
                new InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8));
        //BufferedReader respik =  new BufferedReader(req.getReader(), StandardCharsets.UTF_8);
        String respic="";
        StringBuffer responseList = new StringBuffer();
        while((respic=respik.readLine()) !=null){
            responseList.append(respic);
        }
        String[] params = responseList.toString().split("#");
            if(params[0].equals("listOfCerts")){

                try {
                    newTime = "";
                    outCerts.append(AutoGas.insToAutoGas2(params[1]));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    outCerts.append(AutoGas.updateOrInsert(params[1]));
                } catch (SQLException e) {
                    e.printStackTrace();
                }


            }



    }



}
