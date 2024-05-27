/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package ca1_client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;

/**
 *
 * @author New
 */
public class Attachment extends javax.swing.JFrame {

    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    String myusername;
    String host;
    int port;
    StringTokenizer st;
    String sendTo;
    String file;
    Client ClientFrame;

    /**
     * Creates new form Attachment1
     */
    public Attachment() {
        initComponents();
        jProgressBar1.setVisible(false);
    }

    public boolean prepare(String u, String h, int p, Client m) {
        this.host = h;
        this.myusername = u;
        this.port = p;
        this.ClientFrame = m;

        try {
            socket = new Socket(host, port);
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
            //  Format: CMD_SHARINGSOCKET [sender]
            String format = "CMD_SHARINGSOCKET " + myusername;
            dos.writeUTF(format);
            System.out.println(format);

            new Thread(new SendFileThread(this)).start();
            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    class SendFileThread implements Runnable {

        Attachment form;

        public SendFileThread(Attachment form) {
            this.form = form;
        }

        private void closeMe() {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println(e);
            }
            dispose();
        }

        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    String data = dis.readUTF();
                    st = new StringTokenizer(data);
                    String cmd = st.nextToken();
                    switch (cmd) {
                        case "CMD_RECEIVE_FILE_ERROR":
                            // Format: CMD_RECEIVE_FILE_ERROR [Message]
                            String msg = "";
                            while (st.hasMoreTokens()) {
                                msg = msg + " " + st.nextToken();
                            }
                            form.updateAttachment(false);
                            JOptionPane.showMessageDialog(Attachment.this, msg,
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            this.closeMe();
                            break;

                        case "CMD_RECEIVE_FILE_ACCEPT":
                            // Format: CMD_RECEIVE_FILE_ACCEPT [Message]
                            new Thread(new SendingFileThread(socket, file, sendTo, myusername, Attachment.this)).start();
                            break;

                        case "CMD_SENDFILEERROR":
                            String emsg = "";
                            while (st.hasMoreTokens()) {
                                emsg = emsg + " " + st.nextToken();
                            }
                            System.out.println(emsg);
                            JOptionPane.showMessageDialog(Attachment.this, emsg, "Error", JOptionPane.ERROR_MESSAGE);
                            form.updateAttachment(false);
                            form.disableButtons(false);
                            form.updateBtn("Send File");
                            break;

                        case "CMD_SENDFILERESPONSE":
                            /*
                            Format: CMD_SENDFILERESPONSE [username] [Message]
                             */
                            String rReceiver = st.nextToken();
                            String rMsg = "";
                            while (st.hasMoreTokens()) {
                                rMsg = rMsg + " " + st.nextToken();
                            }
                            form.updateAttachment(false);
                            JOptionPane.showMessageDialog(Attachment.this, rMsg, "Error", JOptionPane.ERROR_MESSAGE);
                            dispose();
                            break;
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

    }

    public void showOpenDialog() {
        int intval = jFileChooser1.showOpenDialog(this);
        if (intval == jFileChooser1.APPROVE_OPTION) {
            jTextField1.setText(jFileChooser1.getSelectedFile().toString());
        } else {
            jTextField1.setText("");
        }
    }

    public void disableButtons(boolean d) {
        if (d) {
            jTextField1.setEditable(false);
            jButton1.setEnabled(false);
            jButton2.setEnabled(false);
            jTextField2.setEditable(false);
            jProgressBar1.setVisible(true);
        } else {
            jTextField1.setEditable(true);
            jButton1.setEnabled(true);
            jButton2.setEnabled(true);
            jTextField2.setEditable(true);
            jProgressBar1.setVisible(false);
        }
    }

    public void setMyTitle(String s) {
        setTitle(s);
    }

    protected void closeThis() {
        dispose();
    }

    public String getThisFilename(String path) {
        File p = new File(path);
        String fname = p.getName();
        return fname.replace(" ", "_");
    }

    public void updateAttachment(boolean b) {
        ClientFrame.updateAttachment(b);
    }

    public void updateBtn(String str) {
        jButton2.setText(str);
    }

    public void updateProgress(int val) {
        jProgressBar1.setValue(val);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jLabel1.setText("Send Attachment");

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jLabel2.setText("Select File");

        jLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        jLabel3.setText("Reciever");

        jButton1.setText("Browse Files");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Send Private Attachment");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Send Group Attachment");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                                .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.LEADING))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(203, 203, 203)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(9, 9, 9)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 356, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(100, 100, 100))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        showOpenDialog();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        sendTo = jTextField2.getText();
        file = jTextField1.getText();

        if ((sendTo.length() > 0) && (file.length() > 0)) {
            try {
                // Format: CMD_SEND_FILE_XD [sender] [receiver] [filename]
                jTextField1.setText("");
                String fname = getThisFilename(file);
                String format = "CMD_SEND_FILE_XD " + myusername + " " + sendTo + " " + fname;
                dos.writeUTF(format);
                System.out.println(format);
                updateBtn("Sending...");
                jButton2.setEnabled(false);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Incomplete Form.!", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        ClientFrame.updateAttachment(false);
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosing

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        file = jTextField1.getText();

        if (file.length() > 0) {
            try {
                // Format: CMD_SHAREFILEALL [filename] [filesize] [sender]
                jTextField1.setText("");
                String fname = getThisFilename(file);
                //File fileToSend = new File(file);
                //long fileSize = fileToSend.length();
                String format = "CMD_SHAREFILEALL " + fname + " " + myusername;
                System.out.println("before dos " + format);
                dos.writeUTF(format);
                System.out.println("after dos " + format);
                updateBtn("Sending to All...");
                jButton3.setEnabled(false);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a file to send!", "Error", JOptionPane.ERROR_MESSAGE);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Attachment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Attachment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Attachment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Attachment.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Attachment().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
