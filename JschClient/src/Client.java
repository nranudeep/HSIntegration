import com.jcraft.jsch.*;

import java.io.*;
import java.util.Properties;

public class Client {
    public static void main(String[] args) throws JSchException, IOException {
        Client client = new Client();
        try {
            //Creating a ssh FTP connection to the server
            Session session = client.connectSession();
            // Uploading the file to the required folder
            client.upload(session,"C:/Users/tungala/Desktop/unable to add users.mp4","/home/nani/JschRx");
            //Disconnecting the session
            // Can remove this and sustain connection by just repeating the client upload frequently
            session.disconnect();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    /*\
    This method uploads the file on local system to the specified location on remote server.
    @params
     session       -  SFTP channel
     localPath     -  Path of the file on local system
     remotePath    -  Path to store the file on remote system
     */
    public void upload(Session session,String localPath,String remotePath) throws IOException
    {
        // create a channel for transmission of file over sftp session
        ChannelSftp channelSftp = getChannelToSftpServer(session);
        if(channelSftp != null)
        {
            try
            {
                //change the default working folder to the specified path - remote path
                changeWorkingDirectory(channelSftp,remotePath);

                File f = new File(localPath);

                //Uploading the file from local system to the remote system
                channelSftp.put(new FileInputStream(f), f.getName());
            } catch (SftpException e) {
                throw new IOException(e.getStackTrace()+"");
            }
            finally
            {
                //Disconnect the SFTPChannel after the transmission or any exceptions
                disconnectChanneltoSftpServer(channelSftp);
            }
        }
    }

    /*
    This method changes the working directory on the remote server to the specified location
    @Params
    channelsftp - SFTPChannel over SFTP session
    path        - Specified path to store the file
     */
    public void changeWorkingDirectory(ChannelSftp channelSftp,String path)
            throws IOException
    {
        try{
            //passing cd command to change the working directory of the channel
            channelSftp.cd(path);
        }
        catch (SftpException e)
        {
            throw new IOException(e.getMessage());
        }
    }

    public Session connectSession() throws IOException {
        //Instantiating secure channel object of JCraft
        JSch jsch = new JSch();
        Session session = null;
        try {
            //Get the private key of RSA key pair
            File file = new File("C:/Users/tungala/Desktop/Mobile Sensing/SSHKeys/priv.ppk");

            //Create a session by passing username and the host name
            session = jsch.getSession
                    ("nani", "192.168.0.16");
            //Adding the private key to the Jsch channel
            jsch.addIdentity(file.getAbsolutePath());
            Properties hash = new Properties();
            hash.put("StrictHostKeyChecking", "no");
            session.setConfig(hash);
            session.setPort(22);
            session.setTimeout(10000);
            //Connect to the server with the given attributes
            session.connect();
        }
        catch (JSchException ex) {
            throw new IOException("Error while creating session >> " + ex);
        }
        return session;
    }

    /*
     To check if the session is still connected
     with remote server.
     */
    public boolean isSessionConnected(Session session)
    {
        return (session != null) && session.isConnected();
    }

    /*
     Get a sftp channel from the session
     */
    public ChannelSftp getChannelToSftpServer(Session session)
            throws IOException
    {
        ChannelSftp channelSftp = null;
        if(isSessionConnected(session))
        {
            try
            {
                Channel channel = session.openChannel("sftp");
                channel.connect();
                channelSftp = (ChannelSftp)channel;
            }
            catch (JSchException e) {
                throw new IOException("Falied to create channel " + e.getMessage());
            }
        }
        return channelSftp;
    }

    /*
    Disconnect the sftp channel over SFTP connection
     */

    public void disconnectChanneltoSftpServer(ChannelSftp channelSftp)
    {
        if (channelSftp != null)
        {
            channelSftp.disconnect();
        }
    }

}