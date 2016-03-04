package ftpFileDownloader;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.nio.file.*;

import ftpFileDownloader.util.*;

public class Tool {
	public static void error(int id){
		if(id == -1)
			System.out.println("Invalid use of program. Execute with flag -about for details\n"
				+ "or with -output <directory_path> if you want to download the FTP file.\n"
				+ "Please put the directory path within quotes if there are spaces within the directory path.\n");
		if(id == -2) 
			System.out.println("You do not have permissions to modify this directory."
    			+ "\nPlease try afresh with another directory.\n");
	}
	public static void about(){
		System.out.println("\u00A9 2016, Siddharth Dinesh, https://github.com/sid-dinesh94/FTPFileDownloader\n"
				+ "List of external library dependencies\n"
				+ "OpenCSV\n"
				+ "Apache Commons Net\n"
				+ "Format to execute:"
				+ "FTPFileDownloader.jar -output <directoryPath>"
				+ "directory Path needs to be enclosed in quotes if it is seperated by spaces");
		return;
	}
	
	public static Boolean isDir(Path path) {
		if (path == null || !Files.exists(path)) return false;
		else return Files.isDirectory(path);
	}
	
	public void output(String relativePath){
		InputStream credentialsFile;
		credentialsFile = getClass().getClassLoader().getResourceAsStream("Credentials.txt");
		
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(credentialsFile));
		String line;
		
		String [] credentials = new String[4];
		/*
		 * Reading from the credentials file and storing it into the credentials String array
		 * Contents of credentials[]:
		 * credentials[0] = hostname
		 * credentials[1] = username
		 * credentials[2] = password
		 * credentials[3] = filename
		 */
		int i = 0;
		try {
			while((line = bufferedReader.readLine()) != null){
				credentials[i] = line.split(":")[1];
				i++;
			}
		} catch (IOException e1) {
			System.out.println("Credentials file is corrupted.\n");
			e1.printStackTrace();
			return;
		}
		String fileName = credentials[3]; 
		String downloadPath = relativePath + "/backup/" + fileName + ".zip";
		File downloadFilePath = new File(relativePath+"/backup");
		if (!downloadFilePath.exists()) {
		    System.out.println("creating temporary backup directory: " + downloadFilePath);
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
		        System.out.println("Backup Directory created");  
		    }
		}
		try {
	           FileDownloader ftpDownloader =
	               new FileDownloader(credentials[0],credentials[1], credentials[2]);
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
            //Make logic to try extraction again
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
		if(args.length == 1 && args[0].equals("-about")){
			about();
			return;
		}
		else if(args.length==2 && args[0].equals("-output")){
			Tool tool = new Tool();
			File directory = new File(args[1]);
			Boolean pathExists = directory.isDirectory();
			if(pathExists)	tool.output(args[1]);
			else{
				System.out.println("The path that has been inputted is not a valid file path. Please try again.");
			}
		}else {
			error(-1);
		}
	}
}
