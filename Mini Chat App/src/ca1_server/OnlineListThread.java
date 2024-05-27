/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ca1_server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class OnlineListThread implements Runnable {
    
    Server serverform;
    
    public OnlineListThread(Server serverform){
        this.serverform = serverform;
    }

    @Override
    public void run() {
        try {
            while(!Thread.interrupted()){
                String msg = "";
                for(int x=0; x < serverform.clientList.size(); x++){
                    msg = msg+" "+ serverform.clientList.elementAt(x);
                }
                
                for(int x=0; x < serverform.socketList.size(); x++){
                    Socket tsoc = (Socket) serverform.socketList.elementAt(x);
                    DataOutputStream dos = new DataOutputStream(tsoc.getOutputStream());
                    /** CMD_ONLINE [user1] [user2] [user3] **/
                    if(msg.length() > 0){
                        dos.writeUTF("CMD_ONLINE "+ msg);
                    }
                }
                
                Thread.sleep(1900);
            }
        } catch(InterruptedException e){
            serverform.appendMessage("[InterruptedException]: "+ e.getMessage());
        } catch (IOException e) {
            serverform.appendMessage("[IOException]: "+ e.getMessage());
        }
    }
    
    
}
