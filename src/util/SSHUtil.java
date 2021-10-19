
        /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import com.jcraft.jsch.*;

/**
 *
 * @author espiraul
 */
public class SSHUtil {

    private String host;
    private JSch jsch;
    private Session session;
    private Channel channel;
    private InputStream input;
    private OutputStream output;

    public SSHUtil() {
    }

    public String runSSHCommandSimple(String serverp, String userp, String passwordp, String commandp) {
        String methodResult = "";
        try {
            if(openConnection(serverp, userp, passwordp)) {
                channel = (ChannelExec) session.openChannel("exec");
                ((ChannelExec)channel).setCommand(commandp);
                input = channel.getInputStream();
                output = channel.getOutputStream();
                channel.connect();

                BufferedReader br = new BufferedReader(new InputStreamReader(input));
                methodResult = br.readLine();
                closeConnection();
            }
        } catch (Exception e) {
            methodResult = "";
        }
        return methodResult;
    }

    public String runSSHCommand(String serverp, String userp, String passwordp, String commandp) {
        String methodResult = "";

        try {
            if(openConnection(serverp, userp, passwordp)){
                Channel channel=session.openChannel("exec");
                ((ChannelExec)channel).setCommand(commandp);
                ((ChannelExec) channel).setPty(true);
                InputStream in=channel.getInputStream();
                OutputStream out=channel.getOutputStream();
                ((ChannelExec)channel).setErrStream(System.err);
                channel.connect();
                out.write(("\n").getBytes());
                out.flush();
                byte[] tmp=new byte[102400];
                while(true){
                    while(in.available()>0){
                        int i=in.read(tmp, 0, 102400);
                        if(i<0)break;
                        methodResult = methodResult + new String(tmp, 0, i);
                    }
                    if(channel.isClosed()){
                        break;
                    }
                    try{Thread.sleep(1000);}catch(Exception ee){
                        ee.printStackTrace();
                        System.out.println(ee.getMessage());
                    }
                }
                closeConnection();
            }
        } catch (Exception e) {
            methodResult = "";
            System.out.println("Error en runSSHCommand2. Error: " + e.getMessage());
        }
        return methodResult;
    }


    private boolean openConnection(String serverp, String userp, String passwordp) {
        boolean openConnection = false;
        try {
            host = serverp;
            jsch = new JSch();
            session = jsch.getSession(userp, serverp, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
            session.setPassword(passwordp);
            session.connect();
            openConnection = true;
        } catch (Exception e) {
            openConnection = false;
        }
        return openConnection;

    }

    private void closeConnection() {
        try {
            if(session != null){
                session.disconnect();
            }
            if(channel != null) {
                channel.isClosed();
            }
            if(input != null) {
                input.close();
            }
            if(output != null) {
                output.close();
            }
            jsch = null;
        } catch (Exception e) {
        }
    }

    public boolean isSSHEnabled(String serverp, String userp, String passwordp){
        boolean enabled = false;
        try {
            if(openConnection(serverp, userp, passwordp)) {
                enabled = true;
                closeConnection();
            }
        } catch (Exception e) {
            enabled = false;
        }
        return enabled;
    }

    public boolean isDirectory(String serverp, String userp, String passwordp, String commandp){
        boolean isDirectory = false;
        try {

        } catch (Exception e) {

        }
        return isDirectory;
    }

    public boolean pingServer(String addressp) {
        boolean pingResult = false;
        try {
            InetAddress inet = InetAddress.getByName(addressp);
            if(inet.isReachable(2500)) {
                pingResult = true;
            }else {
                pingResult = false;
            }
        } catch (Exception e) {
            pingResult = false;
        }
        return pingResult;
    }
}