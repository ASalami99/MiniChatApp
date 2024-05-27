

package ca1_client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitorInputStream;


public class ReceivingFileThread implements Runnable {
    
    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    Client ClientFrame;
    StringTokenizer token;
    final int BUFFER_SIZE = 100;
    
    public ReceivingFileThread(Socket soc, Client clientframe){
        this.socket = soc;
        this.ClientFrame = clientframe;
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            while(!Thread.currentThread().isInterrupted()){
                String data = dis.readUTF();
                token = new StringTokenizer(data);
                String CMD = token.nextToken();
                
                switch(CMD){
                    
                    case "CMD_SENDFILE":
                        String sender = null;
                            try {
                                String filename = token.nextToken();
                                int filesize = Integer.parseInt(token.nextToken());
                                sender = token.nextToken(); 
                                ClientFrame.setMyTitle("Downloading File....");
                                String path = ClientFrame.getMyDownloadFolder() + filename;                                
                                FileOutputStream fos = new FileOutputStream(path);
                                InputStream input = socket.getInputStream();                                
                                ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(ClientFrame, 
                                        "Downloading file please wait...", input);
                                BufferedInputStream bis = new BufferedInputStream(pmis);
                                byte[] buffer = new byte[BUFFER_SIZE];
                                int count, percent = 0;
                                while((count = bis.read(buffer)) != -1){
                                    percent = percent + count;
                                    int p = (percent / filesize);
                                    ClientFrame.setMyTitle("Downloading File  "+ p +"%");
                                    fos.write(buffer, 0, count);
                                }
                                fos.flush();
                                fos.close();
                                ClientFrame.setMyTitle("Welcome " + ClientFrame.getMyUsername());
                                JOptionPane.showMessageDialog(null, "File has been downloaded to \n'"+ path +"'");
                                
                            } catch (IOException e) {
                                                               DataOutputStream eDos = new DataOutputStream(socket.getOutputStream());
                                eDos.writeUTF("CMD_SENDFILERESPONSE "+ sender + " Connection was lost, please try again later.!");
                                
                                System.out.println(e.getMessage());
                                ClientFrame.setMyTitle("you are logged in as: " + ClientFrame.getMyUsername());
                                 socket.close();
                            }
                        break;

                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
