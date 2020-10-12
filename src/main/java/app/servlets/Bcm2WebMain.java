package app.servlets;

import app.entities.*;
import app.model.ViewGisResp;
import app.model.ViewResult;
import app.model.ViewUserSettings;
import app.servlets.bacchus.ActionBuf;
import app.servlets.bacchus.In24;
import app.servlets.bacchus.Out31;
import app.servlets.caduceus.AutoGas;
import com.sun.net.httpserver.HttpContext;
import jcifs.ntlmssp.Type3Message;
//import oracle.net.ns.Message;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.Arrays;
import java.util.Base64;

import static app.entities.ConnectToBD.rsLog;
import static app.entities.ConnectToBD.stmtLog;
//import static app.entities.ConnectionPool.rsPullB;
import static app.entities.Logs.writeLogMain;
import static app.entities.Logs.writeLogParent;
import static app.entities.MassOut.searchErrorsFromNQ;
import static java.util.Base64.Decoder;
import static java.util.Base64.Encoder;

import static jcifs.util.Base64.encode;
import static jcifs.util.Base64.decode;




public  class Bcm2WebMain extends HttpServlet {







    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

         Object[] colUsers;
         Object[][] dataUser;



            try {
                //InitConnections.initConnections();
                if(ConnectToBD.connectToLog()) {
                   // System.out.println("get");
                }else{
                    ConnectToBD.connectToLog();
                }
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }


        try {

            rsLog = stmtLog.executeQuery("select * from bcm_users bu where upper(user_name) =upper(\'"+NTLMUserFilter.getUserName()+"\')");
            ResultSetMetaData rsCheck = rsLog.getMetaData();
            int siz = rsCheck.getColumnCount();
            rsLog.last();
            int countrows = rsLog.getRow();
            rsLog.beforeFirst();
            colUsers = new String[siz];
            dataUser = new Object[countrows][siz];
            for (int i=0; i<siz; i++) {
                colUsers[i] = rsCheck.getColumnName(i+1);
            }
            int id=0;
            while (rsLog.next()){
                for (int iii=0;iii<siz;iii++) {

                    if(rsLog.getString((String) colUsers[iii])==null){
                        dataUser[id][iii]="null";
                    }else{
                        dataUser[id][iii] = rsLog.getString((String) colUsers[iii]);
                    }
                }
                id++;
            }
            String jsonOptions =
                    "{\"UserName\":\"" + NTLMUserFilter.getUserName() + "\"}"; // multi
            writeLogMain(NTLMUserFilter.getUserName(),"BCM_web","logIn",
                    "BCM",
                    jsonOptions,"LOADING","");
            if(countrows!=0){

                ViewUserSettings viewUserSettings = new ViewUserSettings();

                viewUserSettings.initUserParams();

                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/index.jsp");
                requestDispatcher.forward(req, resp);
                writeLogParent(NTLMUserFilter.getUserName(),"BCM_web","logIn",
                        "logIn",
                        jsonOptions,"OK","");



                /*
                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/index.jsp");
                requestDispatcher.forward(req, resp);
                */
            }else
            {

                writeLogParent(NTLMUserFilter.getUserName(),"BCM_web","logIn",
                        "logIn",
                        jsonOptions,"ERROR","user not registered");

                RequestDispatcher requestDispatcher = req.getRequestDispatcher("/old_index.jsp");

                requestDispatcher.forward(req, resp);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        /*

       Connection pullConn = ConnectionPool.getInstance().getConnection();
        if(pullConn !=null){System.out.println("working");}else{System.out.println("working");}

        try {
            Statement pullStmt = pullConn.createStatement();
            ResultSet pullRS = pullStmt.executeQuery("select * from b_amc_statuses");
            while (pullRS.next()){System.out.println(pullRS.getString(1));}
        } catch (SQLException e) {
            e.printStackTrace();
        }

        */




    }
   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

       resp.setCharacterEncoding("UTF-8");
       //resp.setHeader();
       PrintWriter out = resp.getWriter();

       String toGo = req.getParameter("type");
       String system = req.getParameter("system");
       switch (system){
           case "bacchus" :
               if(toGo.equals("out31")){
                   String buffOut31 = req.getParameter("buff");
                   String SapOut31 = req.getParameter("SAP");
                   if(buffOut31.equals("all")){
                       //Переотправка всех ошибочных
                       System.out.println("ALL");
                       try {
                           out.append(searchErrorsFromNQ(SapOut31));
                       } catch (SQLException e) {
                           e.printStackTrace();
                       }
                       return;
                   }

                   if (buffOut31.contains(",")){
                       String listOut31[] = buffOut31.split(",");
                       for (int i=0; i < listOut31.length; i++){
                           out.append(Out31.searchOut31(SapOut31,listOut31[i]));
                       }
                   }else{
                       out.append(Out31.searchOut31(SapOut31, buffOut31));
                   }


               }else{
                   if(toGo.equals("in24")){
                       String buffIn24 = req.getParameter("buff");
                       String SapIn24 = req.getParameter("SAP");
                       if(buffIn24.contains(",")){
                           String listIn24[] = buffIn24.split(",");
                           for (int i=0; i < listIn24.length; i++){
                               out.append(In24.searchIn24(SapIn24, listIn24[i]));
                           }
                       }else{
                           out.append(In24.searchIn24(SapIn24, buffIn24));
                       }

                   }else{
                       if(toGo.equals("ActionRezerv")){
                           String actionBuf = req.getParameter("buff");
                           String actionSap = req.getParameter("SAP");
                           out.append(ActionBuf.actionRezerv(actionBuf,actionSap));
                       }
                       if(toGo.equals("ActionMark")){
                           String actionBuf = req.getParameter("buff");
                           String actionSap = req.getParameter("SAP");
                           try {
                               out.append(ActionBuf.actionMark(actionBuf,actionSap));
                           } catch (SQLException e) {
                               e.printStackTrace();
                               System.out.println(""+e.toString());
                           }
                       }
                       if(toGo.equals("ActionChoise")){
                           String actionBuf = req.getParameter("buff");
                           String actionSap = req.getParameter("SAP");
                           String toStatus = req.getParameter("toStatus");
                           try {
                               out.append(ActionBuf.actionChoise(actionBuf,actionSap,toStatus));
                           } catch (SQLException e) {
                               e.printStackTrace();
                               System.out.println(""+e.toString());
                           }
                       }else{
                           if(toGo.equals("FlowSearch")){
                               //System.out.println("Flow");
                               ViewResult viewResult2 = new ViewResult();
                               String searchBuf = req.getParameter("buff");
                               String searchSap = req.getParameter("SAP");
                               try {
                                   out.append(viewResult2.SearchFromNQ(searchBuf, searchSap));
                               } catch (SQLException e) {
                                   e.printStackTrace();
                               }
                           }else{
                               if(toGo.equals("markInfo")){
                                   //System.out.println("Flow");
                                   ViewResult viewResult2 = new ViewResult();
                                   String searchBuf = req.getParameter("buff");
                                   String searchSap = req.getParameter("SAP");
                                   try {
                                       out.append(viewResult2.ViewMarkInfo(searchBuf, searchSap));
                                   } catch (SQLException e) {
                                       e.printStackTrace();
                                   }
                               }


                           }


                       }
                   }
               }
               break;
           case "markus" :
               if(toGo.equals("queryGis")){
                   String cis = req.getParameter("cis");
                   String inn = req.getParameter("inn");
                   resp.setHeader("Content-Type","Application/json");
                   PrintWriter outM = resp.getWriter();

                   //out.append(cis+""+inn);
                   outM.append(ViewGisResp.getInstance().checkOwner(cis,inn));
               }
           case "caduceus":
               if(toGo.equals("autoGas")){
                   try {
                       out.append(AutoGas.showTmp_uuid());
                   } catch (SQLException e) {
                       e.printStackTrace();
                   }
               }
               if(toGo.equals("trunAutoGas")){
                   try {
                       out.append(AutoGas.trunAutoGas());
                   } catch (SQLException e) {
                       e.printStackTrace();
                   }
               }
               if(toGo.equals("showAutoGas")){
                   try {
                       out.append(AutoGas.showAutoGas());
                   } catch (SQLException e) {
                       e.printStackTrace();
                   }
               }//
               if(toGo.equals("insToMainAutoGas")){
                   try {
                       out.append(AutoGas.insToMainAutoGas());
                   } catch (SQLException e) {
                       e.printStackTrace();
                   }
               }
       }


        /*
       if(toGo.equals("out31")){
           String buffOut31 = req.getParameter("buff");
           String SapOut31 = req.getParameter("SAP");
            if(buffOut31.equals("all")){
                //Переотправка всех ошибочных
                System.out.println("ALL");
                try {
                    out.append(searchErrorsFromNQ(SapOut31));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return;
            }

           if (buffOut31.contains(",")){
               String listOut31[] = buffOut31.split(",");
               for (int i=0; i < listOut31.length; i++){
                   out.append(Out31.searchOut31(SapOut31,listOut31[i]));
               }
           }else{
               out.append(Out31.searchOut31(SapOut31, buffOut31));
           }


           }else{
           if(toGo.equals("in24")){
               String buffIn24 = req.getParameter("buff");
               String SapIn24 = req.getParameter("SAP");
               if(buffIn24.contains(",")){
                   String listIn24[] = buffIn24.split(",");
                   for (int i=0; i < listIn24.length; i++){
                       out.append(In24.searchIn24(SapIn24, listIn24[i]));
                   }
               }else{
                   out.append(In24.searchIn24(SapIn24, buffIn24));
               }

           }else{
               if(toGo.equals("ActionRezerv")){
                   String actionBuf = req.getParameter("buff");
                   String actionSap = req.getParameter("SAP");
                out.append(ActionBuf.actionRezerv(actionBuf,actionSap));
               }
               if(toGo.equals("ActionMark")){
                   String actionBuf = req.getParameter("buff");
                   String actionSap = req.getParameter("SAP");
                   try {
                       out.append(ActionBuf.actionMark(actionBuf,actionSap));
                   } catch (SQLException e) {
                       e.printStackTrace();
                       System.out.println(""+e.toString());
                   }
               }
               if(toGo.equals("ActionChoise")){
                   String actionBuf = req.getParameter("buff");
                   String actionSap = req.getParameter("SAP");
                   String toStatus = req.getParameter("toStatus");
                   try {
                       out.append(ActionBuf.actionChoise(actionBuf,actionSap,toStatus));
                   } catch (SQLException e) {
                       e.printStackTrace();
                       System.out.println(""+e.toString());
                   }
               }else{
                   if(toGo.equals("FlowSearch")){
                       //System.out.println("Flow");
                       ViewResult viewResult2 = new ViewResult();
                       String searchBuf = req.getParameter("buff");
                       String searchSap = req.getParameter("SAP");
                       try {
                           out.append(viewResult2.SearchFromNQ(searchBuf, searchSap));
                       } catch (SQLException e) {
                           e.printStackTrace();
                       }

                   }


               }
           }
       }
        */




       }

       //------Поиск Агента для РЦ






   }

