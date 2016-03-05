package ftpFileDownloader.util;


import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

/**This class deals with the FTP connections in order to download the zip file from the ftp server.
 * 
 * @author siddharth dinesh
 *
 */
public class FileDownloader {

   FTPClient ftp = null;

   /** It takes in parameters of credentials to the FTP site and checks if the credentials work on the server after making the connection
    * 
    * @param host host website. ex. beel.org
    * @param user username for the FTP server
    * @param pwd password for the corresponding username
    * @throws IOException Connection error
    */
   public FileDownloader(String host, String user, String pwd) throws IOException {
       ftp = new FTPClient();
       int reply;
       ftp.connect(host);
       reply = ftp.getReplyCode();
       if (!FTPReply.isPositiveCompletion(reply)) {
           ftp.disconnect();
           throw new IOException("Exception in connecting to FTP Server");
       }
       ftp.login(user, pwd);
       ftp.setFileType(FTP.BINARY_FILE_TYPE);
       ftp.enterLocalPassiveMode();
   }

   /** Downloads the file given the FilePath on the server and the destination FilePath where it is to be saved
    * 
    * @param remoteFilePath The path in the FTP server where the file to be downloaded exists
    * @param localFilePath The path where it is to be saved in the local once it is downloaded
    */
   public void downloadFile(String remoteFilePath, String localFilePath) {
       try (FileOutputStream fos = new FileOutputStream(localFilePath)) {
           this.ftp.retrieveFile(remoteFilePath, fos);
       } catch (IOException e) {
           e.printStackTrace();
       }
   }

   /** This method is to disconnect from the server once the download is completed. 
    * 
    */
   public void disconnect() {
       if (this.ftp.isConnected()) {
           try {
               this.ftp.logout();
               this.ftp.disconnect();
           } catch (IOException f) {
               // do nothing as file is already downloaded from FTP server
           }
       }
   }

   /** Test method to check if the class functions as required.
    * 
    * @param args
    */
   public static void main(String[] args) {
       try {
           FileDownloader ftpDownloader =
               new FileDownloader("beel.org", "f00b72a7", "WtdyYSkrvzT5hErd");
           ftpDownloader.downloadFile("FTP File Downloader and Processor example file.zip", "E:/Sem 4-1/excel.zip");
           System.out.println("FTP File downloaded successfully");
           ftpDownloader.disconnect();
       } catch (Exception e) {
           e.printStackTrace();
       }
   }

}