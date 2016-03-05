# FTPFileDownloader
This is a software tool that downloads a zipped CSV file from an FTP server, unzips the file, and processes it. 


The tool should be started with the parameter -output that specifies the folder in which output files will be
stored, e.g. “assignment.jar -output c:\temp\”
This retrieves the data as soon as the tool is started:
a) Download the file using the credentials listed in the config.txt
b) Store the downloaded zip file to <output_folder>\backup\<original_filename>
e.g. c:\temp\backup\FTP File Downloader and Processor example file.zip
c) Unzip the zip file to <output_folder>\backup\<original_filename>
e.g. c:\temp\backup\FTP File Downloader and Processor example file.csv

Process the data by deleting all lines with set_id < 6500000 and store the new data as <output_folder>\<date>.csv
e.g. c:\temp\2016-02-28.csv

The tool offers “about” information. For instance, when you start the tool like “assignment.jar –about”
the following information will be shown:
 <year_of_copyright>, <my_name>, <url_to_this_repository>
+ List of external libraries that you used
