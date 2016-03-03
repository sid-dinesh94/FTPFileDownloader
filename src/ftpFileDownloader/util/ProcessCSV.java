package ftpFileDownloader.util;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
public class ProcessCSV {
	String filePath;
	String writePath;
	char seperator;
	public ProcessCSV(String path, String dest, char seperator){
		this.filePath = path;
		this.writePath = dest;
		this.seperator = seperator;
	}
	public void process() throws IOException{
		CSVReader reader = new CSVReader(new FileReader(filePath), seperator);
		CSVWriter writer = new CSVWriter(new FileWriter(writePath));
	    String[] nextLine; 
	    //int count =0;
	    while ((nextLine = reader.readNext()) != null) {
	       // nextLine[] is an array of values from the line
	    	if(nextLine[0].equals("set_id")){
	    		writer.writeNext(nextLine);
	    		/*for(String item : nextLine){
	    			System.out.print(item + "\t");
	    		}
	    		System.out.println();*/
	    	}else if(Integer.parseInt(nextLine[0])>6500000){
	    		writer.writeNext(nextLine);
	    		/*for(String item : nextLine){
	    			System.out.print(item + "\t");
	    		}
	    		System.out.println();*/
    			//count ++;

	    	}
	    	
	    }
    	//System.out.println(count);
    	writer.close();
	    reader.close();
	}
	
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
