package app.servlets.bacchus;

import app.entities.FlowSend;
//import app.entities.InitConnections;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;

import static app.entities.MassOut.confrimMassSend;
import static app.entities.MassOut.searchErrorsFromNQ;

public class confirmSend extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        //System.out.println("get");


    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Content-Type","Application/xml");
        PrintWriter out31 = resp.getWriter();
        BufferedReader respik = new BufferedReader(
                new InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8));
      //BufferedReader respik =  new BufferedReader(req.getReader(), StandardCharsets.UTF_8);
        String respic="";
        StringBuffer responseList = new StringBuffer();
        while((respic=respik.readLine()) !=null){
            responseList.append(respic);
        }
        String[] params = responseList.toString().split("!");
        //System.out.println(Arrays.toString(params[0].split("=")));
        String[] confirmSend = params[0].split("#");
        String[] type = params[1].split("#");
        String[] buff = params[2].split("#");
        String[] selfBuf = params[3].split("#");
        String[] Sap = params[4].split("#");

        //String[] params = responseList.toString().split("!");
        //System.out.println(Arrays.toString(params[0].split("=")));
        //String[] confirmSend24 = params[0].split("#");
        //String[] type24 = params[1].split("#");
       // String[] buffOut24 = params[2].split("#");
       // String[] selfBuf24 = params[3].split("#");
       // String[] SapOut24 = params[4].split("#");


        System.out.println(selfBuf[1]);

        try{
            //String confirmSend31 = req.getParameter("confirmSend");
            if(confirmSend[1].equals("yes")){
                if(type[1].equals("out31")){
                    //String buffOut31 = req.getParameter("buff");
                    //String SapOut31 = req.getParameter("SAP");
                    if(buff[1].equals("all")){
                        //Переотправка всех ошибочных
                        System.out.println("AllConfirm");
                        //
                        if(confrimMassSend(Sap[1])){out31.append("Буферы успешно отправлены!");}else{out31.append("Ошибка");}
                        return;
                    }
                    if(buff[1].equals("afterEdit")){
                        //После редактирования
                        // System.out.println("afterEdit");
                        //String selfBuf = req.getParameter("selfBuf");
                        //System.out.println(selfBuf);
                        //if(confrimMassSend(SapOut31)){out31.append("Буферы успешно отправлены!");}else{out31.append("Ошибка");}
                        out31.append(FlowSend.SendAfterEditFromRC(Sap[1],selfBuf[1]));
                        out31.append("$$");
                        //System.out.println(out31);
                        return;
                    }
                    if(buff[1].contains("%2C")){
                        System.out.println("Multi");
                        String[] buffList = buff[1].split("%2C");
                        for (String s : buffList) {
                            System.out.println("Test "+s);
                            out31.append(FlowSend.SendFlowFromRC(Sap[1],s));
                            out31.append("$$");
                        }
                    }else{
                        //System.out.println("Single Test "+buffOut31[1]);
                        out31.append(FlowSend.SendFlowFromRC(Sap[1],buff[1]));
                        out31.append("$$");
                    }
                }else{
                    if(type[1].equals("in24")){
                        System.out.println("in24");
                        if(buff[1].equals("afterEdit")){
                            //После редактирования
                            // System.out.println("afterEdit");
                            //String selfBuf = req.getParameter("selfBuf");
                            //System.out.println(selfBuf);
                            //if(confrimMassSend(SapOut31)){out31.append("Буферы успешно отправлены!");}else{out31.append("Ошибка");}
                            out31.append(FlowSend.Send24AfterEditFromRC(Sap[1],selfBuf[1]));
                            out31.append("$$");
                            //System.out.println(out31);
                            return;
                        }
                        if(buff[1].contains("%2C")){
                            System.out.println("Multi");
                            String[] buffList = buff[1].split("%2C");
                            for (String s : buffList) {
                                System.out.println("Test "+s);
                                out31.append(FlowSend.SendInFlowFromRC(Sap[1],s));
                                out31.append("$$");
                            }
                        }else{
                            System.out.println("Single Test "+buff[1]);
                            out31.append(FlowSend.SendInFlowFromRC(Sap[1],buff[1]));
                            out31.append("$$");
                        }



                    }

                }


            }





        }catch (NullPointerException | SQLException n){
            //System.out.println("null");
        }



    }



}
