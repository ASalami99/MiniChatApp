/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca1_server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

public class SocketThread implements Runnable {

    Socket socket;
    Server serverform;
    DataInputStream dis;
    StringTokenizer st;
    String client, filesharing_username;

    final int BUFFER_SIZE = 100;

    public SocketThread(Socket socket, Server serverform) {
        this.serverform = serverform;
        this.socket = socket;

        try {
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createConnection(String receiver, String sender, String filename) {
        try {

            Socket s = serverform.getClientList(receiver);
            if (s != null) {
                DataOutputStream dosS = new DataOutputStream(s.getOutputStream());
                // Format:  CMD_FILE_XD [sender] [receiver] [filename]
                String format = "CMD_FILE_XD " + sender + " " + receiver + " " + filename;
                dosS.writeUTF(format);
                System.out.println("This is from createConnection() function in the form sender, receiver, filename" + format);
                //The line above is testing something. Remember to remove it.                  
            } else {

                serverform.appendMessage("Client was not found '" + receiver + "'");
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("CMD_SENDFILEERROR " + "Client '" + receiver + "' was not found in the list, make sure it is on the online list.!");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void createConnectionAll(String receiver, String sender, String filename) {
        try {

            Socket s = serverform.getClientList(receiver);
            if (s != null) {
                DataOutputStream dosS = new DataOutputStream(s.getOutputStream());
                // Format:  CMD_FILE_XD [sender] [receiver] [filename]
                String format = "CMD_FILE_XD_ALL " + sender + " " + receiver + " " + filename;
                dosS.writeUTF(format);
                System.out.println("This is from createConnection() function in the form sender, receiver, filename" + format);

            } else {

                serverform.appendMessage("Client was not found '" + receiver + "'");
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("CMD_SENDFILEERROR " + "Client '" + receiver + "' was not found in the list, make sure it is on the online list.!");
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String data = dis.readUTF();
                st = new StringTokenizer(data);
                String CMD = st.nextToken();
                /**
                 * Check COMMAND *
                 */
                switch (CMD) {
                    case "CMD_JOIN":
                        /**
                         * CMD_JOIN [clientUsername] *
                         */
                        String clientUsername = st.nextToken();
                        client = clientUsername;
                        serverform.setClientList(clientUsername);
                        serverform.setSocketList(socket);
                        serverform.appendMessage("[Client]: " + clientUsername + " joins the chatroom.!");
                        break;

                    case "CMD_CHAT":

                        /**
                         * CMD_CHAT [from] [sendTo] [message] *
                         */
                        String from = st.nextToken();
                        String sendTo = st.nextToken();
                        String msg = "";
                        while (st.hasMoreTokens()) {
                            msg = msg + " " + st.nextToken();
                        }
                        Socket tsoc = serverform.getClientList(sendTo);
                        try {
                            DataOutputStream dos = new DataOutputStream(tsoc.getOutputStream());
                            String content = from + ": " + msg;
                            dos.writeUTF("CMD_CHAT " + content);
                            serverform.appendMessage("Private Message: From " + from + " To " + sendTo + " : " + msg);
                        } catch (IOException e) {

                        }
                        break;

                    case "CMD_CHATALL":

                        /**
                         * CMD_CHATALL [from] [message] *
                         */
                        String chatall_from = st.nextToken();
                        String chatall_msg = "";
                        while (st.hasMoreTokens()) {
                            chatall_msg = chatall_msg + " " + st.nextToken();
                        }
                        String chatall_content = chatall_from + " " + chatall_msg;
                        for (int x = 0; x < serverform.clientList.size(); x++) {
                            if (!serverform.clientList.elementAt(x).equals(chatall_from)) {
                                try {
                                    Socket tsoc2 = (Socket) serverform.socketList.elementAt(x);
                                    DataOutputStream dos2 = new DataOutputStream(tsoc2.getOutputStream());
                                    dos2.writeUTF("CMD_CHATALL " + chatall_content);
                                } catch (IOException e) {

                                }
                            }
                        }
                        serverform.appendMessage("CMD_CHATALL " + chatall_content);
                        break;

                    case "CMD_SHARINGSOCKET":
                        serverform.appendMessage("CMD_SHARINGSOCKET : Client stablish a socket connection for file sharing...");
                        String file_sharing_username = st.nextToken();
                        filesharing_username = file_sharing_username;
                        serverform.setClientFileSharingUsername(file_sharing_username);
                        serverform.setClientFileSharingSocket(socket);
                        serverform.appendMessage("CMD_SHARINGSOCKET : Username: " + file_sharing_username);

                        break;

                    case "CMD_SENDFILE":
                        serverform.appendMessage("CMD_SENDFILE : Client sending a file...");
                        /*
                        Format: CMD_SENDFILE [Filename] [Size] [Recipient] [Sender]  from: Sender Format
                        Format: CMD_SENDFILE [Filename] [Size] [Sender] to Receiver Format
                         */
                        String file_name = st.nextToken();
                        String filesize = st.nextToken();
                        String sendto = st.nextToken();
                        String Sender = st.nextToken();
                        serverform.appendMessage("CMD_SENDFILE : From: " + Sender);
                        serverform.appendMessage("CMD_SENDFILE : To: " + sendto);
                        serverform.appendMessage("CMD_SENDFILE : preparing connections..");
                        Socket cSock = serverform.getClientFileSharingSocket(sendto);

                        if (cSock != null) {
                            try {
                                DataOutputStream cDos = new DataOutputStream(cSock.getOutputStream());
                                cDos.writeUTF("CMD_SENDFILE " + file_name + " " + filesize + " " + Sender);
                                InputStream input = socket.getInputStream();
                                OutputStream sendFile = cSock.getOutputStream();
                                byte[] buffer = new byte[BUFFER_SIZE];
                                int cnt;
                                while ((cnt = input.read(buffer)) > 0) {
                                    sendFile.write(buffer, 0, cnt);
                                }
                                sendFile.flush();
                                sendFile.close();
                                serverform.removeClientFileSharing(sendto);
                                serverform.removeClientFileSharing(Sender);

                            } catch (IOException e) {
                                System.err.println(e.getMessage());
                            }
                        } else {
                            /*   FORMAT: CMD_SENDFILEERROR  */
                            serverform.removeClientFileSharing(Sender);
                            serverform.appendMessage("CMD_SENDFILE : Client '" + sendto + "' was not found.!");
                            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                            dos.writeUTF("CMD_SENDFILEERROR " + "Client '" + sendto + "' was not found, File Sharing will exit.");
                        }
                        break;

                    case "CMD_SHAREFILEALL":
                        /*
     * Format: CMD_SHAREFILEALL [Filename] [Size] [Sender]
                         */
                        serverform.appendMessage("CMD_SHAREFILEALL : Client sending a file to all users...");

                        if (st.countTokens() < 2) {
                            serverform.appendMessage("CMD_SHAREFILEALL : Error - Incomplete command received.");
                            break;
                        }

                        String file_nameall = st.nextToken();
                        String sender = st.nextToken();
                        serverform.appendMessage("CMD_SHAREFILEALL : From: " + sender);
                        serverform.appendMessage("CMD_SHAREFILEALL : Filename: " + file_nameall);
                        System.out.println("Sender: " + sender);

                        for (int x = 0; x < serverform.clientList.size(); x++) {
                            if (!serverform.clientList.elementAt(x).equals(sender)) {
                                String user = serverform.clientList.elementAt(x).toString();
                                createConnectionAll(user, sender, file_nameall);
                                // Check for null user before retrieving socket
                                if (user != null) {
                                    Socket tsoc2 = (Socket) serverform.getClientFileSharingSocket(user);
                                    if (tsoc2 != null) { // Check for null socket as well
                                        try {
                                            DataOutputStream cDos = new DataOutputStream(tsoc2.getOutputStream());
                                            String format = "CMD_SHAREFILEALL " + file_nameall + " " + sender;
                                            cDos.writeUTF(format);
                                            System.out.println(format);

                                            // Sending the actual file data
                                            InputStream input = socket.getInputStream();
                                            OutputStream sendFile = tsoc2.getOutputStream();
                                            byte[] buffer = new byte[BUFFER_SIZE];
                                            int cnt;
                                            while ((cnt = input.read(buffer)) > 0) {
                                                sendFile.write(buffer, 0, cnt);
                                            }
                                            sendFile.flush();
                                            sendFile.close();
                                            serverform.removeClientFileSharing(user);
                                            System.out.println("Successful sharing to: " + user);
                                        } catch (IOException e) {
                                            serverform.appendMessage("CMD_SHAREFILEALL : Error sending file to user at index " + x + " - " + e.getMessage());
                                        }
                                    } else {
                                        System.out.println("No file sharing socket found for user: " + user);
                                        // Handle the case where user has no socket (e.g., log a message)
                                    }
                                } else {
                                    System.out.println("Null user found in client list at index: " + x);
                                    // Handle the case where user is null (e.g., log an error)
                                }
                            }
                        }
                        break;

                    case "CMD_SENDFILERESPONSE":
                        /*
                        Format: CMD_SENDFILERESPONSE [username] [Message]
                         */
                        String receiver = st.nextToken();
                        String rMsg = "";
                        serverform.appendMessage("[CMD_SENDFILERESPONSE]: username: " + receiver);
                        while (st.hasMoreTokens()) {
                            rMsg = rMsg + " " + st.nextToken();
                        }
                        try {
                            Socket rSock = (Socket) serverform.getClientFileSharingSocket(receiver);
                            DataOutputStream rDos = new DataOutputStream(rSock.getOutputStream());
                            rDos.writeUTF("CMD_SENDFILERESPONSE" + " " + receiver + " " + rMsg);
                        } catch (IOException e) {
                            System.err.println(e.getMessage());
                        }
                        break;

                    case "CMD_SEND_FILE_XD":  // Format: CMD_SEND_FILE_XD [sender] [receiver]                        
                        try {
                        String send_sender = st.nextToken();
                        String send_receiver = st.nextToken();
                        String send_filename = st.nextToken();
                        serverform.appendMessage("CMD_SEND_FILE_XD Host: " + send_sender);
                        this.createConnection(send_receiver, send_sender, send_filename);
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                    }
                    break;
                    
                     case "CMD_SEND_FILE_XD_ALL":  // Format: CMD_SEND_FILE_XD [sender] [receiver]                        
                        try {
                            String send_sender = st.nextToken();
                            String send_receiver = st.nextToken();
                            String send_filename = st.nextToken();
                            serverform.appendMessage("CMD_SEND_FILE_XD Host: "+ send_sender);
                            this.createConnection(send_receiver, send_sender, send_filename);
                        } catch (Exception e) {
                             System.err.println(e.getMessage());
                        }
                        break;

                    case "CMD_SEND_FILE_ERROR":
                        // Format:  CMD_SEND_FILE_ERROR [receiver] [Message]
                        String eReceiver = st.nextToken();
                        String eMsg = "";
                        while (st.hasMoreTokens()) {
                            eMsg = eMsg + " " + st.nextToken();
                        }
                        try {

                            Socket eSock = serverform.getClientFileSharingSocket(eReceiver);
                            DataOutputStream eDos = new DataOutputStream(eSock.getOutputStream());
                            //  Format:  CMD_RECEIVE_FILE_ERROR [Message]
                            eDos.writeUTF("CMD_RECEIVE_FILE_ERROR " + eMsg);
                        } catch (IOException e) {
                            System.out.println(e);
                        }
                        break;

                    case "CMD_SEND_FILE_ACCEPT": // Format:  CMD_SEND_FILE_ACCEPT [receiver] [Message]
                        String aReceiver = st.nextToken();
                        String aMsg = "";
                        while (st.hasMoreTokens()) {
                            aMsg = aMsg + " " + st.nextToken();
                        }
                        try {
                            /*  Send Error to the File Sharing host  */
                            Socket aSock = serverform.getClientFileSharingSocket(aReceiver); // get the file sharing host socket for connection
                            DataOutputStream aDos = new DataOutputStream(aSock.getOutputStream());
                            //  Format:  CMD_RECEIVE_FILE_ACCEPT [Message]
                            aDos.writeUTF("CMD_RECEIVE_FILE_ACCEPT " + aMsg);
                        } catch (IOException e) {
                            serverform.appendMessage("[CMD_RECEIVE_FILE_ERROR]: " + e.getMessage());
                        }
                        break;
                        
                        case "CMD_SEND_FILE_ACCEPT_ALL": // Format:  CMD_SEND_FILE_ACCEPT [filename] [receiver] [sender] [Message]
                        String aMsgAll = "";
                        while(st.hasMoreTokens()){
                            aMsgAll = aMsgAll+" "+st.nextToken();
                        }
                        
                        System.out.println("This is aMsgAll before splitting. " + aMsgAll);
                        String[] parts = aMsgAll.split(" ");
                        String aReceiverAll = parts[1];
                        System.out.println("this is aMsg: " + aMsgAll);
                        try {
                            /*  Send Error to the File Sharing host  */
                            Socket aSock = serverform.getClientFileSharingSocket(aReceiverAll); // get the file sharing host socket for connection
                            DataOutputStream aDos = new DataOutputStream(aSock.getOutputStream());
                            //  Format:  CMD_RECEIVE_FILE_ACCEPT [Message]
                            aDos.writeUTF("CMD_RECEIVE_FILE_ACCEPT "+ aMsgAll);
                        } catch (IOException e) {
                            serverform.appendMessage("[CMD_RECEIVE_FILE_ERROR]: "+ e.getMessage());
                        }
                        break;

                    default:
                        serverform.appendMessage("Unknown Command " + CMD);
                        break;
                }
            }
        } catch (IOException e) {

            serverform.removeFromTheList(client);
            if (filesharing_username != null) {
                serverform.removeClientFileSharing(filesharing_username);
            }
            serverform.appendMessage("Connection closed..!");
        }
    }

}
