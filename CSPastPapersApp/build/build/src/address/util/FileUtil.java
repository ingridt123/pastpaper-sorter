package address.util;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import technology.tabula.CommandLineApp;

public class FileUtil {

	/**
	 * gets relative path of file relative to current path
	 * @param f file
	 * @return relative path of file
	 */
	public static String getRelativePath(File f) {
		
		Path curPath = Paths.get(new File(".").getAbsolutePath());
		Path path = Paths.get(f.getAbsolutePath());
		Path relativePath = curPath.relativize(path);
		
		return relativePath.toString();
		
	}
	
	/**
	 * convert sFile (pdf) to tFile (txt) using Tabex PDF API
	 * edited from http://pdfextractoronline.com/tabex-pdf-api/
	 * @param name file name
	 * @param path relative path of file
	 * @throws Exception
	 */
	public static void convertToTXT(String name, String path) throws Exception {
		
        String uri = "http://api2.pdfextractoronline.com:8089/tab2ex2/api";
        MultiValueMap<String, Object> multipartMap = new LinkedMultiValueMap<String, Object>();
        Resource pdfFile = new FileSystemResource(path);
        multipartMap.add("tab2exkey", "581.3FBA36650178C890");
        multipartMap.add("fileName", name);
        multipartMap.add("recognitionMethod", "auto");
        multipartMap.add("outputFormat", "TXT");
        multipartMap.add("file", pdfFile);
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("multipart", "form-data"));
        HttpEntity<Object> request = new HttpEntity<Object>(multipartMap, headers);
        ResponseEntity<byte[]> httpResponse = template.exchange(uri, HttpMethod.POST, request, byte[].class);
        if (httpResponse.getStatusCode().equals(HttpStatus.OK)) {
            FileOutputStream output = new FileOutputStream(new File("files/import-files/" + name + ".txt"));
            IOUtils.write(httpResponse.getBody(), output);
        }
		
	}
	
	/**
	 * parses table of PDF file using Tabula
	 * @param sFilePath source file path
	 * @param tFilePath target file path
	 * @param coord coordinates of table (top left bottom right)
	 * @param tFileType target file type
	 */
	public static void parseTable(String sFilePath, String tFilePath, String coord, String tFileType) {
		
		CommandLineParser parser = new DefaultParser();
        String[] arguments = new String[] {
        		sFilePath, 
        		"-a", coord,
        		"-l", 
        		"-p", "all", 
        		"-f", tFileType, 
        		"-o", tFilePath
        };
        
        try {
        	
        		CommandLine cmd = parser.parse(CommandLineApp.buildOptions(), arguments);
            StringBuilder stringBuilder = new StringBuilder();
            new CommandLineApp(stringBuilder, cmd).extractTables(cmd);
        	
        }
        catch (Exception e) {
        		e.printStackTrace();
        }
		
	}
	
}
