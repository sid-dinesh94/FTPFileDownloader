package ftpFileDownloader;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ftpFileDownloader.util.*;
public class Tool {
	public static void main(String[] args){
		System.out.println("Input file path\n");
		System.out.println(args[0]);
		String fileName = "FTP File Downloader and Processor example file"; 
		String downloadPath = args[0] + "/backup/" + fileName + ".zip";
		File downloadFilePath = new File(args[0]+"/backup");
		if (!downloadFilePath.exists()) {
		    System.out.println("creating directory: " + downloadFilePath);
		    boolean result = false;

		    try{
		        downloadFilePath.mkdir();
		        result = true;
		    } 
		    catch(SecurityException se){
		        //handle it
		    }        
		    if(result) {    
		        System.out.println("DIR created");  
		    }
		}
		try {
	           FileDownloader ftpDownloader =
	               new FileDownloader("beel.org", "f00b72a7", "WtdyYSkrvzT5hErd");
	           ftpDownloader.downloadFile(fileName+".zip", downloadPath);
	           System.out.println("FTP File downloaded successfully");
	           ftpDownloader.disconnect();
	       } catch (IOException e) {
	    	   System.out.println("There has been an issue in reconnecting to the server.\nDo you want to retry?");
	           e.printStackTrace();
	       }
        String destDirectory = args[0]+ "/backup/";
        Unzipper zipFile = new Unzipper();
        try {
            zipFile.unzip(downloadPath, destDirectory);
        } catch (IOException ex) {
            // some errors occurred
        	System.out.println("Could not extract file\nWant to try again?");
            ex.printStackTrace();
        }
        System.out.println("The unzipping was successful");
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        String finalDest = args[0] + "/" + timeStamp + ".csv";
        ProcessCSV csv = new ProcessCSV(destDirectory+fileName+".csv",finalDest, ';');
		try {
			csv.process();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in processing CSV file.\nWant to retry?");
			e.printStackTrace();
		}
	}

}
