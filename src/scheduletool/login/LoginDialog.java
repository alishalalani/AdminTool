/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package scheduletool.login;

import gsutils.Debug;
import java.awt.Color;
import scheduletool.schedule_client.ScheduleClient;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.prefs.Preferences;

/**
 *
 * @author samla
 */
public class LoginDialog extends javax.swing.JDialog
    {
    static public String response = null;
    //---------------------------------------------------------------------------------------------
    public boolean development = false;
    //---------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------
    public LoginDialog (java.awt.Frame parent, boolean modal, boolean development)
        {
        super (parent, modal);
        initComponents ();

        this.development  = development;
        Preferences prefs = Preferences.userNodeForPackage (LoginDialog.class);
        String username   = prefs.get ("username", "");
        String password   = prefs.get ("password", "");
        username_TextField.setText (username);
        password_Field    .setText (password);

        display_server_label ();
        }
    //---------------------------------------------------------------------------------------------
    private void display_server_label ()
        {
        if (development)
            {
            Debug.print ("development");
            set_server_label ("development", Color.green);
            }
        else
            {
            Debug.print ("PRODUCTION");
            set_server_label ("PRODUCTION", Color.red);
            }
        }
    //---------------------------------------------------------------------------------------------
    private void login ()
        {
        String username = username_TextField.getText ();
        String password = new String (password_Field.getPassword ());
        Preferences prefs = Preferences.userNodeForPackage (LoginDialog.class);
        prefs.put ("username", username);
        prefs.put ("password", password);
        try
            {
            String post_string = "development=" + (development ? "1" : "0") + "&username=" + username + "&password=" + password;

            URL url = new URL ((development
                                 ? "http://"  + ScheduleClient.host_dev  + "/OLtest"
                                 : "https://" + ScheduleClient.host_prod + "/OddsLogic")
                               + "/sources/php/tools_login.php");

            HttpURLConnection url_connection = (HttpURLConnection) url.openConnection ();
            url_connection.setRequestMethod   ("POST");
            url_connection.setRequestProperty ("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            url_connection.setRequestProperty ("Content-Length", String.valueOf (post_string.length ()));
            url_connection.setDoOutput (true);

            url_connection.getOutputStream ().write (post_string.getBytes (StandardCharsets.UTF_8));
            //Debug.print (post_string);
            Reader in = new BufferedReader (new InputStreamReader (url_connection.getInputStream (), "UTF-8"));

            response = "";
            for (int c; (c = in.read ()) >= 0;)
                response += ((char) c);
            //Debug.print (response);
            setVisible (false);
            }
        catch (Exception e)
            {
            Debug.print ("Exception (" + e + ")");
            e.printStackTrace ();
            }
        }
    //---------------------------------------------------------------------------------------------
    static public String [] parse_response ()
        {
        int index = response.indexOf (',');
        //development = (LoginDialog.response.charAt (i-1) == '1');
        String next = LoginDialog.response.substring (index+1);
        String db_value [] = new String [5];
        for (int i = 0; i < 5; i++)
            {
            index = next.indexOf (',');
            db_value [i] = LoginDialog.convert_from_hex (next.substring (0, index));
            next = next.substring (index+1);
            }
        return db_value;
        }
    //---------------------------------------------------------------------------------------------
    static private String convert_from_hex (String hex)
        {
        String string = "";
        int length = hex.length ();
        for (int i = 0; i < length; i += 2)
            {
            String hex_value = "" + hex.charAt(i) + hex.charAt(i+1);
            string += (char) Integer.parseInt (hex_value, 16);
            }
        return (string);
        }
    //---------------------------------------------------------------------------------------------
    private void set_server_label (String string, Color color)
        {
        java.awt.EventQueue.invokeLater (() ->
            {
            server_Label.setOpaque (true);
            server_Label.setText       (string);
            server_Label.setForeground (color);
            });
        }
    //---------------------------------------------------------------------------------------------
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        username_Label = new javax.swing.JLabel();
        username_TextField = new javax.swing.JTextField();
        password_Label = new javax.swing.JLabel();
        password_Field = new javax.swing.JPasswordField();
        login_Button = new javax.swing.JButton();
        server_Label = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Tools Login");

        username_Label.setText("Username:");

        username_TextField.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                username_TextFieldActionPerformed(evt);
            }
        });

        password_Label.setText("Password:");

        password_Field.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                password_FieldActionPerformed(evt);
            }
        });

        login_Button.setText("Login");
        login_Button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                login_ButtonActionPerformed(evt);
            }
        });

        server_Label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        server_Label.setText("serverLabel");
        server_Label.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                server_LabelMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(username_Label)
                            .addComponent(password_Label))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(username_TextField)
                            .addComponent(password_Field, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(server_Label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(login_Button)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(username_Label)
                    .addComponent(username_TextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(password_Label)
                    .addComponent(password_Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(login_Button)
                    .addComponent(server_Label))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void login_ButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_login_ButtonActionPerformed
    {//GEN-HEADEREND:event_login_ButtonActionPerformed
        login ();
    }//GEN-LAST:event_login_ButtonActionPerformed

    private void password_FieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_password_FieldActionPerformed
    {//GEN-HEADEREND:event_password_FieldActionPerformed
        login ();
    }//GEN-LAST:event_password_FieldActionPerformed

    private void username_TextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_username_TextFieldActionPerformed
    {//GEN-HEADEREND:event_username_TextFieldActionPerformed
        password_Field.requestFocus ();
    }//GEN-LAST:event_username_TextFieldActionPerformed

    private void server_LabelMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_server_LabelMouseClicked
    {//GEN-HEADEREND:event_server_LabelMouseClicked
        development = !development;
        display_server_label ();
    }//GEN-LAST:event_server_LabelMouseClicked

    public static LoginDialog main (boolean development)
        {
        LoginDialog dialog = new LoginDialog (new javax.swing.JFrame (), true, development);
        dialog.addWindowListener (new java.awt.event.WindowAdapter ()
            {
            @Override
            public void windowClosing (java.awt.event.WindowEvent e)
                {
                System.exit (0);
                }
            });
        dialog.setVisible (true);
        // The dialog box will be up until closed or Login is clicked
        if (response == null || response.length () == 0)
            System.exit (0);
        return (dialog);
        }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton login_Button;
    private javax.swing.JPasswordField password_Field;
    private javax.swing.JLabel password_Label;
    private javax.swing.JLabel server_Label;
    private javax.swing.JLabel username_Label;
    private javax.swing.JTextField username_TextField;
    // End of variables declaration//GEN-END:variables
    }
