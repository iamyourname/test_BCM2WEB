package app.servlets;

import java.io.IOException;
import java.io.PrintStream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import jcifs.ntlmssp.Type3Message;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;

import java.util.Base64.Encoder;



public class NTLMUserFilter implements Filter{
    private FilterConfig filterConfig = null;
    private String userDomain = null;
    private static String userName = "";

    public static String getUserName(){
        return userName;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }
    public void doFilter(ServletRequest req, ServletResponse res,
                         FilterChain chain) {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String useragent = request.getHeader("user-agent");
            try{
                String auth = request.getHeader("Authorization");
                response.setContentType("text/html");
                if (auth == null) {
                    response.setContentLength(0);
                    response.setStatus(response.SC_UNAUTHORIZED);
                    response.setHeader("WWW-Authenticate", "NTLM");
                    return;
                }
                if (auth.startsWith("NTLM ")) {
                    byte[] msg =  jcifs.util.Base64.decode(auth.substring(5));
                    int off = 0, length, offset;
                    String d,m,u;
                    if (msg[8] == 1) {
                        off = 18;
                        byte z = 0;
                        byte[] msg1 = {(byte)'N', (byte)'T', (byte)'L', (byte)'M', (byte)'S', (byte)'S', (byte)'P', z,(byte)2, z, z, z, z, z, z, z,(byte)40, z, z, z, (byte)1, (byte)130, z, z,z, (byte)2, (byte)2, (byte)2, z, z, z, z, z, z, z, z, z, z, z, z};
                        System.out.println(" ");
                        response.setContentLength(0);
                        response.setStatus(response.SC_UNAUTHORIZED);
                        response.setHeader("WWW-Authenticate", "NTLM " + jcifs.util.Base64.encode(msg1));

                        return;
                    } else
                    if (msg[8] == 3) {
                        off = 30;
                        length = msg[off+17]*256 + msg[off+16];
                        offset = msg[off+19]*256 + msg[off+18];
                        m = new String(msg, offset, length);
                    } else {
                        return;
                    }

                    Type3Message type3Message = new Type3Message(msg);
                    userName = type3Message.getUser();

                }
            }catch(Exception e){
                System.out.println(e.toString()) ;
            }

        try {
            chain.doFilter(req, res);
        } catch (IOException | ServletException e) {
            System.out.println(e);
        }
    }
    public void destroy()
    {
        this.filterConfig = null;
    }
}
