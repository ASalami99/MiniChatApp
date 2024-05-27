
package ca1_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;

public class ServerThread implements Runnable {
    
    ServerSocket server;
    Server serverform;
    boolean StartServer = true;
    
    public ServerThread(int port, Server serverform){
        
        try {
            this.serverform = serverform;
            server = new ServerSocket(port);
            serverform.appendMessage("Server is running. !\n");
        } 
        catch (IOException e) { 
             System.out.println(e);
             }
    }

    @Override
    public void run() {
        try {
            while(StartServer){
                Socket socket = server.accept();
                 new Thread(new SocketThread(socket, serverform)).start();
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    
    
    public void stop(){
        try {
            server.close();
            StartServer = false;
            JOptionPane.showMessageDialog(null,"Server is now closed..!");
            
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
}
