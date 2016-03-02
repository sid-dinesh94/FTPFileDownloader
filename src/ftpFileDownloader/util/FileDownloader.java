package ftpFileDownloader.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

//import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class FileDownloader {
	/*
	 * FTPClient variable
	 */
	FTPClient ftp = null;
	
	public FileDownloader(String host, String user, String pwd) throws Exception {
		ftp = new FTPClient();
		ftp.connect(host);
		int reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            throw new Exception("Exception in connecting to FTP Server");
        }
		ftp.login(user, pwd);
        ftp.setFileType(FTP.BINARY_FILE_TYPE);
        ftp.enterLocalPassiveMode();
	}
	
	public void downloadFile(String remoteFilePath, String localFilePath) {
        try (FileOutputStream fos = new FileOutputStream(localFilePath)) {
            this.ftp.retrieveFile(remoteFilePath, fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
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
	 
	 public static void main(String[] args) {
	        try {
	            FileDownloader ftpDownloader =
	                new FileDownloader("beel.org", "f00b72a7", "WtdyYSkrvzT5hErd");
	            ftpDownloader.downloadFile("FTP File Downloader and Processor example file.zip", "E:/temp.zip");
	            System.out.println("FTP File downloaded successfully");
	            ftpDownloader.disconnect();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	 
}
