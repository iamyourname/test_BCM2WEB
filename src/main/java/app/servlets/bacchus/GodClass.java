package app.servlets.bacchus;

import app.servlets.caduceus.AutoGas;

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

public class GodClass extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws  IOException {

    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws  IOException, ServletException {

        resp.setCharacterEncoding("UTF-8");
        //resp.setHeader();
        PrintWriter out = resp.getWriter();

        String GodBuf = req.getParameter("GodBuf");
        String GodSap = req.getParameter("GodSap");
        try {
            System.out.println("GodClass");
            out.append(FindLikeGod.godFind(GodBuf, GodSap));
        }catch (Exception sq){
            System.out.println(""+sq.toString());
        }


    }

}