package ftpFileDownloader.util;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

/** Process CSV class is to deal with the processing of a CSV file based on the set_id parameter.
 * The filter value is hardcoded in at 650000.
 * The class has a process method that reads a csv file and then writes out the filtered new CSV file with the timestamp as the file name.
 * @author siddharth dinesh
 *
 */
public class ProcessCSV {
	String filePath;
	String writePath;
	char seperator;
	/**This is the constructor. Takes in the path to the CSV file, the destination directory where you want to save it and the seperator in the CSV file
	 * 
	 * @param path Path to CSV file. given as String
	 * @param dest Path to destination folder. Given as String
	 * @param seperator Value seperator in the CSV file which is fed as input
	 */
	public ProcessCSV(String path, String dest, char seperator){
		this.filePath = path;
		this.writePath = dest;
		this.seperator = seperator;
	}
	/** Reads in the CSV file with a CSV Reader line by line, then using CSVWriter it only writes down those lines for which set_id > 6500000.
	 * 
	 * @throws IOException when filePath or writePath are corrupted
	 */
	public void process() throws IOException{
		CSVReader reader = new CSVReader(new FileReader(filePath), seperator);
		CSVWriter writer = new CSVWriter(new FileWriter(writePath));
	    String[] nextLine; 
	    while ((nextLine = reader.readNext()) != null) {
	       // nextLine[] is an array of values from the line
	    	if(nextLine[0].equals("set_id")){
	    		writer.writeNext(nextLine);
	    
	    	}else if(Integer.parseInt(nextLine[0])>6500000){
	    		writer.writeNext(nextLine);
	    
	    	}
	    	
	    }
    	writer.close();
	    reader.close();
	}
	/**Test method to try out the class.
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		ProcessCSV csv = new ProcessCSV("E:/Sem 4-2/FTP File Downloader and Processor example file.csv","E:/Sem 4-2/Processed.csv", ';');
		try {
			csv.process();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
