package ca1_client;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.JOptionPane;


public class ClientThread implements Runnable{
    
    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    Client clientFrame;
    StringTokenizer token;
     MessageAlert alert;
    
    
    public ClientThread(Socket socket, Client ClientFrame){
        this.clientFrame = ClientFrame;
        this.socket = socket;
        try {
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            
        }
    }

    @Override
    public void run() {
        try {
            while(!Thread.currentThread().isInterrupted()){
                String data = dis.readUTF();
                token = new StringTokenizer(data);
                /** Get Message CMD **/
                String CMD = token.nextToken();
                
                switch(CMD){
                    case "CMD_CHATALL":
                       
                            String msg = "";
                        String frm = token.nextToken();
                        while(token.hasMoreTokens()){
                            msg = msg +" "+ token.nextToken();
                        }
                       alert=new MessageAlert();
                       alert.TextMsg();
                        clientFrame.appendMessage(frm+": "+msg+"\n" );
                        break;
                        
                   case "CMD_CHAT":
                       
                        String msgs = "";
                        String from = token.nextToken();
                        while(token.hasMoreTokens()){
                            msgs = msgs +" "+ token.nextToken();
                        }
                       alert=new MessageAlert();
                       alert.PrivateMsg();
                        clientFrame.appendMessage(from+": "+msgs+"\n" );
                        break;     
             
                        
                    case "CMD_ONLINE":
                        Vector online = new Vector();
                        while(token.hasMoreTokens()){
                            String list = token.nextToken();
                            if(!list.equalsIgnoreCase(clientFrame.username)){
                                online.add(list);
                            }
                        }
                        clientFrame.appendOnlineList(online);
                        break;
                    
                        
                    //  This will inform the client that there's a file receive, Accept or Reject the file  
                    case "CMD_FILE_XD":  // Format:  CMD_FILE_XD [sender] [receiver] [filename]
                        String sender = token.nextToken();
                        String receiver = token.nextToken();
                        String fname = token.nextToken();
                        
                        System.out.println("Sender: " + sender + " Receiver: " + receiver + " filename: " + fname);
                        int confirm = JOptionPane.showConfirmDialog(clientFrame,
                                "From: "+sender+"\nFilename: "+fname+"\nWould you like to Accept.?");
                        alert=new MessageAlert();
                        alert.FileMsg();
                        if(confirm == 0){ 
                           
                            clientFrame.openFolder();
                            try {
                                dos = new DataOutputStream(socket.getOutputStream());
                                // Format:  CMD_SEND_FILE_ACCEPT [ToSender] [Message]
                                 String format = "CMD_SEND_FILE_ACCEPT_ALL "+sender+"  accepted";
                                System.out.println("The CMD_SEND_DILE_ACCEPT format is: " + format);
                                dos.writeUTF(format);
                                
                                /*  this will create a filesharing socket to handle incoming file
                                and this socket will automatically closed when it's done.  */
                                Socket fSoc = new Socket(clientFrame.getMyHost(), clientFrame.getMyPort());
                                DataOutputStream fdos = new DataOutputStream(fSoc.getOutputStream());
                                fdos.writeUTF("CMD_SHARINGSOCKET "+ clientFrame.getMyUsername());
                                /*  Run Thread for this   */
                                new Thread(new ReceivingFileThread(fSoc, clientFrame)).start();
                            } catch (IOException e) {
                                System.out.println("[CMD_FILE_XD]: "+e.getMessage());
                            }
                        } else { // client rejected the request, then send back result to sender
                            try {
                                dos = new DataOutputStream(socket.getOutputStream());
                                // Format:  CMD_SEND_FILE_ERROR [ToSender] [Message]
                                String format = "CMD_SEND_FILE_ERROR "+sender+" Client rejected your request or connection was lost.!";
                                dos.writeUTF(format);
                            } catch (IOException e) {
                                System.out.println("[CMD_FILE_XD]: "+e.getMessage());
                            }
                        }                       
                        break;   
                        
                        case "CMD_FILE_XD_ALL":  // Format:  CMD_FILE_XD [sender] [receiver] [filename]
                        String senderAll = token.nextToken();
                        String receiverAll = token.nextToken();
                        String fnameAll = token.nextToken();
                        
                        System.out.println("Sender: " + senderAll + " Receiver: " + receiverAll + " filename: " + fnameAll);
                        int confirmAll = JOptionPane.showConfirmDialog(clientFrame,
                                "From: "+senderAll+"\nFilename: "+fnameAll+"\nwould you like to Accept.?");
                        alert=new MessageAlert();
                        alert.FileMsg();
                        if(confirmAll == 0){ 
                           
                            clientFrame.openFolder();
                            try {
                                dos = new DataOutputStream(socket.getOutputStream());
                                // Format:  CMD_SEND_FILE_ACCEPT [Sender] [ToReceiver] [filename] [Message]
                                String format = "CMD_SEND_FILE_ACCEPT_ALL "+fnameAll+ " " + receiverAll + " " + senderAll + "  accepted";
                                System.out.println("The CMD_SEND_DILE_ACCEPT format is: " + format);
                                dos.writeUTF(format);
                                
                                /*  this will create a filesharing socket to handle incoming file
                                and this socket will automatically closed when it's done.  */
                                Socket fSocAll = new Socket(clientFrame.getMyHost(), clientFrame.getMyPort());
                                DataOutputStream fdos = new DataOutputStream(fSocAll.getOutputStream());
                                fdos.writeUTF("CMD_SHARINGSOCKET "+ clientFrame.getMyUsername());
                                /*  Run Thread for this   */
                                new Thread(new ReceivingFileThread(fSocAll, clientFrame)).start();
                            } catch (IOException e) {
                                System.out.println("[CMD_FILE_XD]: "+e.getMessage());
                            }
                        } else { // client rejected the request, then send back result to sender
                            try {
                                dos = new DataOutputStream(socket.getOutputStream());
                                // Format:  CMD_SEND_FILE_ERROR [ToSender] [Message]
                                String format = "CMD_SEND_FILE_ERROR "+senderAll+" Client rejected your request or connection was lost.!";
                                dos.writeUTF(format);
                            } catch (IOException e) {
                                System.out.println("[CMD_FILE_XD]: "+e.getMessage());
                            }
                        }                       
                        break;
                        
                        
                    default: 
                        clientFrame.appendMessage("[CMDException]: Unknown Command "+ CMD);
                    break;
                }
            }
        } catch(IOException e){
            System.err.println(e);
        }
    }
}
