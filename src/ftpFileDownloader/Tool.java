package ftpFileDownloader;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;
import java.nio.file.*;

import ftpFileDownloader.util.*;
/**<h1> Tool Class </h1>
 * This is the class in the project that links all the utility classes as well 
 * as bothers with parsing the command line arguments
 * @author siddharth dinesh
 * @version 1.0
 *
 */
public class Tool {
	/** The error handling method for the program
	 * 
	 * @param id used to convey the error message
	 */
	public static void error(int id){
		if(id == -1)
			System.out.println("Invalid use of program. Execute with flag -about for details\n"
				+ "or with -output <directory_path> if you want to download the FTP file.\n"
				+ "Please put the directory path within quotes if there are spaces within the directory path.\n");
		if(id == -2) 
			System.out.println("You do not have permissions to modify this directory."
    			+ "\nPlease try afresh with another directory.\n");
	}
	
	/**This method deals with the about flag in the command line argument
	 * 
	 */
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
	
	/**
	 * This method checks to see if the inputed Absolute Directory Path exists in 
	 * the file system
	 * @param path An instance of the Path class which carries the directory path
	 * fed as input to the program
	 * @return True if path is valid, false if not
	 */
	public static Boolean isDir(Path path) {
		if (path == null || !Files.exists(path)) return false;
		else return Files.isDirectory(path);
	}
	
	/**This method deals with the flag -output in the execution of the program
	 * 
	 * 
	 * @param directoryPath contains the directoryPath fed as input to the program
	 */
	public void output(String directoryPath){
		InputStream credentialsFile;
		credentialsFile = getClass().getClassLoader().getResourceAsStream("config.txt");
		Scanner scan = new Scanner(System.in);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(credentialsFile));
		String line;
		
		String [] credentials = new String[4];
		/*
		 * Reading from the config file and storing it into the credentials String array
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
			System.out.println("Config file is corrupted.");
			e1.printStackTrace();
			return;
		}
		String fileName = credentials[3]; 
		String downloadPath = directoryPath + "/backup/" + fileName + ".zip";
		
		/*
		 * Create the backup directory in the directoryPath provided
		 */
		
		File downloadFilePath = new File(directoryPath+"/backup");
		if (!downloadFilePath.exists()) {
		    System.out.println("creating temporary backup directory: " + downloadFilePath);
		    boolean result = false;

		    try{
		        downloadFilePath.mkdir();
		        result = true;
		    } 
		    catch(SecurityException se){
		    	error(-2);
		    	scan.close();
		    	return;
		    }        
		    if(result) {    
		        System.out.println("Backup Directory created");  
		    }
		}
		/*
		 * Attempting to download the zip file from the FTP server by calling an instance of the FileDownloader class.
		 * Three attempts are allowed. With the user getting the ability to override this feature at every retry.
		 */
		
		Boolean downloadFlag = true;
		int downloadAttempt = 0;
		while(downloadFlag){
			try {
		           downloadAttempt++;
				   FileDownloader ftpDownloader =
		               new FileDownloader(credentials[0],credentials[1], credentials[2]);
		           ftpDownloader.downloadFile(fileName+".zip", downloadPath);
		           System.out.println("FTP File downloaded successfully");
		           ftpDownloader.disconnect();
		           downloadFlag = false;
		       } catch (IOException e) {
		    	   if(downloadAttempt > 3){
		    		   System.out.println("Maximum download tries exceeded. Exiting program.");
		    		   scan.close();
		    		   return;
		    	   }
		    	   System.out.println("There has been an issue in reconnecting to the server."
		    	   		+ "Do you want to retry? y or (n)");
		    	   String input = scan.nextLine();
		    	   if(input.equals("\n") || input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")){
		    		   System.out.println("Quitting the download process");
		    		   scan.close();
		    		   return;
		    	   }else if(input.equalsIgnoreCase("y") || input.equalsIgnoreCase("Yes")){
		    		   System.out.println("Retrying download");
		    	   }else{
		    		   System.out.println("Invalid input. Exiting program assuming default NO option.");
		    		   scan.close();
		    		   return;
		    	   }
		       }
		}
		
		/*
		 * The following code is to unzip the zip file which has been downloaded 
		 * It is done by calling the Unzipper class.
		 * Two retries are allowed for this step. User gets the ability to override the retry. 
		 */
		
		
        String destDirectory = directoryPath+ "/backup/";
        Unzipper zipFile = new Unzipper();
        Boolean unzipperFlag = true;
        int unzipperCount = 0;
        while(unzipperFlag && unzipperCount < 2){
	        try {
	            unzipperCount ++;
	            zipFile.unzip(downloadPath, destDirectory);
	            unzipperFlag = false;
	            
	        } catch (IOException ex) {
	            //Make logic to try extraction again
	        	System.out.println("Could not extract file\nWant to try again: y or (n)?");
	        	String input = scan.nextLine();
	        	if(input.equals("\n") || input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")){
	    		   System.out.println("Quitting the unzipping process");
	    		   scan.close();
	    		   return;
	    	   }else if(input.equalsIgnoreCase("y") || input.equalsIgnoreCase("Yes")){
	    		   System.out.println("Retrying unzipping");
	    	   }else{
	    		   System.out.println("Invalid input. Exiting program assuming default NO option.");
	    		   scan.close();
	    		   return;
	    	   }
	        }
        }
        if( unzipperCount >= 2){
        	System.out.println("Extraction was not successful. Exiting gracefully.");
        	scan.close();
        	return;
        }
        
        System.out.println("The unzipping was successful");
        
        /*
         * This code block is to process the CSV file and save it with the timestamp.
         * Two retries are allowed
         * The ProcessCSV class is called in this code block and the process method of that element is used. 
         */
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
        String finalDest = directoryPath + "/" + timeStamp + ".csv";
        ProcessCSV csv = new ProcessCSV(destDirectory+fileName+".csv",finalDest, ';');
		Boolean processFlag = true;
		int processCount = 0;
        while(processFlag && processCount < 3){
	        try {
	        	processCount ++;
				csv.process();
				processFlag = false;
			} catch (IOException e) {
				System.out.println("Error in processing CSV file. Want to retry: y or (n)?");
			 	String input = scan.nextLine();
	        	if(input.equals("\n") || input.equalsIgnoreCase("n") || input.equalsIgnoreCase("no")){
	    		   System.out.println("Quitting the processing step");
	    		   scan.close();
	    		   return;
	    	    }else if(input.equalsIgnoreCase("y") || input.equalsIgnoreCase("Yes")){
	    		   System.out.println("Retrying processing");
	    	    }else{
	    		   System.out.println("Invalid input. Exiting program assuming default NO option.");
	    		   scan.close();
	    		   return;
	    	    }
			}
		}
        scan.close();
		File zip = new File(downloadPath);
		zip.delete();
		File spreadsheet = new File(destDirectory+fileName+".csv");
		spreadsheet.delete();
		File backup = new File(destDirectory);
		backup.delete();
		System.out.println("Deletion was successful");
	}
	/**Called by the program upon execution. 
	 * 
	 * @param args Command line arguments
	 */
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
			File directory = new File(args[1].trim());
			Boolean pathExists = directory.isDirectory();
			if(pathExists)	tool.output(args[1].trim());
			else{
				System.out.println("The path that has been inputted is not a valid file path. Please try again.");
			}
		}else {
			error(-1);
		}
	}
}
