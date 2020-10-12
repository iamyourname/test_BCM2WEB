package app.servlets;



import app.entities.ChangeProfileParam;
import app.model.ViewLog;
import app.model.ViewUserSettings;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import static app.entities.ConnectToBD.*;
import static app.entities.Logs.writeLogMain;
import static app.entities.Logs.writeLogParent;

public class EditProfile extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ViewUserSettings viewUserSettings = new ViewUserSettings();

        try {
            viewUserSettings.initUserParams();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        RequestDispatcher requestDispatcher = req.getRequestDispatcher("/profile/profile.jsp");
        requestDispatcher.forward(req, resp);


    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        Object[] colUsers;
        Object[][] dataUser;
        BufferedReader respik = new BufferedReader(
                new InputStreamReader(req.getInputStream(), StandardCharsets.UTF_8));
        //BufferedReader respik =  new BufferedReader(req.getReader(), StandardCharsets.UTF_8);
        String respic="";
        StringBuffer responseList = new StringBuffer();
        while((respic=respik.readLine()) !=null){
            responseList.append(respic);
        }
        String[] fromEdit = responseList.toString().split("!");

        switch (fromEdit[0]){
            case "reqUsers#yes":
                try {
                    rsLog = stmtLog.executeQuery("select bu.user_name from bcm_users bu  order by id");
                    while(rsLog.next()){
                        out.append(rsLog.getString("user_name"));
                        out.append("&");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            break;
            case "confirmSend#yes" :
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
                    //String toResponce=dataUser[0][3].toString().replace("{","");
                    // out.append(toResponce.replace("}",""));
                    out.append(dataUser[0][3].toString());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case "reqData#yes" :
                String[] queryParams = fromEdit[1].split("&");
               System.out.println(queryParams[3]);
                try {
                    connectToLog();
                   out.append( ViewLog.getInstance().getQueryFromLog(queryParams[0],queryParams[1],
                            queryParams[2],queryParams[3]));
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case "queryTableAuto#yes" :
                //String[] queryNameTable = fromEdit[0].split("&");
                //System.out.println(queryParams[3]);
                ViewUserSettings viewUserSettings = new ViewUserSettings();
                try {
                    viewUserSettings.initUserParams();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                try {
                    connectToLog();
                    //ViewUserSettings viewUserSettings = new ViewUserSettings();
                    String[][] allUsersTableAuto =  viewUserSettings.getAllUsersTableAuto();
                    out.append(NTLMUserFilter.getUserName() + "!");
                    for(int i=1; i < allUsersTableAuto.length; i++ ){
                            out.append(allUsersTableAuto[i][0]+"&"+
                                    allUsersTableAuto[i][1].replace("\"","")+"!");
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case "changeParam#yes" :
                String[] ChangeParamValue = fromEdit[1].split("#");
                //System.out.println(queryParams[3]);
                try {
                    out.append(ChangeProfileParam.changeParam(ChangeParamValue[0],ChangeParamValue[1]));
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }


                break;

        }



    }
}
