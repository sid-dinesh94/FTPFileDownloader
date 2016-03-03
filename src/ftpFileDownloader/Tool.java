package ftpFileDownloader;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import ftpFileDownloader.util.*;

public class Tool {
	public static void error(int id){
		if(id == -1)
			System.out.println("Invalid use of program. Execute with flag -about for details\n"
				+ "or with -output <directory_path> if you want to download the FTP file.\n");
		if(id == -2) 
			System.out.println("You do not have permissions to modify this directory."
    			+ "\nPlease try afresh with another directory.");
	}
	public static void about(){
		System.out.println("\u00A9 2016, Siddharth Dinesh, https://github.com/sid-dinesh94/FTPFileDownloader\n"
				+ "List of external library dependencies\n"
				+ "OpenCSV\n"
				+ "Apache Commons Net\n");
		return;
	}
	public static void output(String relativePath){
		String fileName = "FTP File Downloader and Processor example file"; 
		String downloadPath = relativePath + "/backup/" + fileName + ".zip";
		File downloadFilePath = new File(relativePath+"/backup");
		if (!downloadFilePath.exists()) {
		    System.out.println("creating directory: " + downloadFilePath);
		    boolean result = false;

		    try{
		        downloadFilePath.mkdir();
		        result = true;
		    } 
		    catch(SecurityException se){
		    	error(-2);
		    	return;
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
        String destDirectory = relativePath+ "/backup/";
        Unzipper zipFile = new Unzipper();
        try {
            zipFile.unzip(downloadPath, destDirectory);
        } catch (IOException ex) {
            // some errors occurred
        	System.out.println("Could not extract file\nWant to try again?");
            ex.printStackTrace();
        }
        System.out.println("The unzipping was successful");
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-mm-ss").format(Calendar.getInstance().getTime());
        String finalDest = relativePath + "/" + timeStamp + ".csv";
        ProcessCSV csv = new ProcessCSV(destDirectory+fileName+".csv",finalDest, ';');
		try {
			csv.process();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in processing CSV file.\nWant to retry?");
			e.printStackTrace();
		}
		
		File zip = new File(downloadPath);
		zip.delete();
		File spreadsheet = new File(destDirectory+fileName+".csv");
		spreadsheet.delete();
		File backup = new File(destDirectory);
		backup.delete();
		System.out.println("Deletion was successful");
	}
	
	public static void main(String[] args){
		if(args.length < 1){
			error(-1);
			return;
		}
		if(args[0].equals("-about")){
			about();
			return;
		}
		else if(args.length==2 && args[0].equals("-output")){
			output(args[1]);
		}
	}
}
