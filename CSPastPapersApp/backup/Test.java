package address;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.util.Arrays;
//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.Entity;
//import javax.ws.rs.client.Invocation;
////import javax.ws.rs.client.WebTarget;
//import javax.ws.rs.core.Response;
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

public class Test {

	public static void main (String[] args) throws Exception {
		
		File sFile = new File("files/import-files/M16/P1_Markscheme.pdf");
		File tFile = new File("files/import-files/M16/P1_Markscheme.txt");
		String apiKey = "";
		
		String uri = "http://api2.pdfextractoronline.com:8089/tab2ex2/api?tab2exkey=" + apiKey;
//		byte[] inputBytes = read(sFile);
		MultiValueMap<String, Object> multipartMap = new LinkedMultiValueMap<String, Object>();
		Resource pdfFile = new FileSystemResource(sFile.getName());
		multipartMap.add("tab2exkey", "ABCDEFG");
		multipartMap.add("fileName", sFile.getName());
		multipartMap.add("recognitionMethod", "auto");
		multipartMap.add("outputFormat", "TXT");
		multipartMap.add("file", pdfFile);
		RestTemplate template = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(new MediaType("multipart", "form-data"));
		HttpEntity<Object> request = new HttpEntity<>(multipartMap, headers);
      	ResponseEntity<byte[]> httpResponse = template.exchange(uri, HttpMethod.POST, request, byte[].class);
      	if (httpResponse.getStatusCode().equals(HttpStatus.OK)) {
      		FileOutputStream output = new FileOutputStream(tFile);
      		IOUtils.write(httpResponse.getBody(), output);
      	}
      	System.out.println(httpResponse.getHeaders().toString());
        
	}
	
	public static byte[] read(File file) throws IOException {

        ByteArrayOutputStream ous = null;
        InputStream ios = null;
        try {
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(file);
            int read = 0;
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
        } finally {
            try {
                if (ous != null) {
                    ous.close();
                }
            } catch (IOException e) {
            }

            try {
                if (ios != null) {
                    ios.close();
                }
            } catch (IOException e) {
            }
        }
        return ous.toByteArray();
    }
	
}
