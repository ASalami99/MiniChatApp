/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ca1_client;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.swing.JOptionPane;


public class SendingFileThread implements Runnable {
    
   Socket socket;
    DataOutputStream dos;
    Attachment AttachmentFrame; 
    String file;
    String receiver;
    String sender;
    
    private final int BUFFER_SIZE = 100;
    
    public SendingFileThread(Socket soc, String file, String receiver, String sender, Attachment frm){
        this.socket = soc;
        this.file = file;
        this.receiver = receiver;
        this.sender = sender;
        this.AttachmentFrame = frm;
    }
    

    @Override
    public void run() {
        try {
            AttachmentFrame.disableButtons(true);
            System.out.println("Sending File..!");
            dos = new DataOutputStream(socket.getOutputStream());
            
            //  Format: CMD_SENDFILE [Filename] [Size] [Recipient] [Sender]
            File filename = new File(file);
            int len = (int) filename.length();
            int filesize = (int)Math.ceil(len / BUFFER_SIZE); 
            String clean_filename = filename.getName();
            dos.writeUTF("CMD_SENDFILE "+ clean_filename.replace(" ", "_") +" "+ filesize +" "+ receiver +" "+ sender);
            System.out.println("From: "+ sender);
            System.out.println("To: "+ receiver);
            InputStream input = new FileInputStream(filename);
            OutputStream output = socket.getOutputStream();
            BufferedInputStream bis = new BufferedInputStream(input);
            
            byte[] buffer = new byte[BUFFER_SIZE];
            int count, percent = 0;
            while((count = bis.read(buffer)) > 0){
                percent = percent + count;
                int p = (percent / filesize);
                 AttachmentFrame.updateProgress(p);
                output.write(buffer, 0, count);
            }
            /* Update AttachmentForm GUI */
            AttachmentFrame.setMyTitle("File was sent.!");
            AttachmentFrame.updateAttachment(false); 
            JOptionPane.showMessageDialog(AttachmentFrame, 
                   "File successfully sent.!", "Sucess", JOptionPane.INFORMATION_MESSAGE);
            AttachmentFrame.closeThis();
           
            output.flush();
            output.close();
            System.out.println("File was sent..!");
        } catch (IOException e) {
            AttachmentFrame.updateAttachment(false);
            System.out.println(e.getMessage());
        }
    }
}
