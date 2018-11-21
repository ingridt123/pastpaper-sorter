package address.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class DeprecatedFileUtil {

	/**
	 * convert sFile (pdf) to tFile (txt) using Zamzar API
	 * @param apiKey Zamzar API key
	 * @param sFile source file (pdf)
	 * @param tFile target file (txt)
	 * @throws Exception
	 */
	public static void convertFile(String apiKey, File sFile, File tFile) throws Exception {
		
		long jobID = formatFile(apiKey, sFile);
		
		long fileID = -1;
		while (fileID == -1) {
            fileID = checkJob(jobID, apiKey);
        }
		
		downloadFile(fileID, apiKey, tFile);
		
	}
	
	
	/**
	 * start conversion job
     * edited from Zamzar API Docs {@link https://developers.zamzar.com/docs}
	 * @param apiKey Zamzar API key
	 * @param sFile source file
	 * @return job ID
	 * @throws Exception
	 */
    private static long formatFile(String apiKey, File sFile) throws Exception {
        
        // test environment: https://sandbox.zamzar.com/v1/jobs
        // production environment: https://api.zamzar.com/v1/jobs
        String endpoint = "https://sandbox.zamzar.com/v1/jobs";
        
        String targetFormat = "txt";
        
        // create HTTP client and request object
        CloseableHttpClient httpClient = getHttpClient(apiKey);
        HttpEntity requestContent = MultipartEntityBuilder.create()
            .addPart("source_file", new FileBody(sFile))
            .addPart("target_format", new StringBody(targetFormat, ContentType.TEXT_PLAIN))
            .build();
        HttpPost request = new HttpPost(endpoint);
        request.setEntity(requestContent);

        // make request
        CloseableHttpResponse response = httpClient.execute(request);

        // extract body from response
        HttpEntity responseContent = response.getEntity();
        String result = EntityUtils.toString(responseContent, "UTF-8");

        // parse result as JSON
        JSONObject json = new JSONObject(result);

        // print result
//        System.out.println(json);
        
        // get ID from JSON object
        long jobID = json.getLong("id");
        
        // finalize response and client
        response.close();
        httpClient.close();
        
        return jobID;
        
    }
    
    /**
     * check whether job finished successfully
     * edited from Zamzar API Docs {@link https://developers.zamzar.com/docs}
     * @param jobID job ID
     * @param apiKey Zamzar API key
     * @return fileID (0 if job unsuccessful)
     * @throws Exception
     */
    private static long checkJob(long jobID, String apiKey) throws Exception {
        
        // test environment: https://sandbox.zamzar.com/v1/jobs
        // production environment: https://api.zamzar.com/v1/jobs
        String endpoint = "https://sandbox.zamzar.com/v1/jobs/" + jobID;

        // create HTTP client and request object
        CloseableHttpClient httpClient = getHttpClient(apiKey);
        HttpGet request = new HttpGet(endpoint);

        // make request
        CloseableHttpResponse response = httpClient.execute(request);

        // extract body from response
        HttpEntity responseContent = response.getEntity();
        String result = EntityUtils.toString(responseContent, "UTF-8");

        // parse result as JSON
        JSONObject json = new JSONObject(result);

        // print result
//        System.out.println(json);
        
        // finalize response and client
        response.close();
        httpClient.close();
        
        // check if job has been completed successfully
        if (! json.isNull("target_files")) {
            // get target file ID from JSON object
            long fileID = json.getJSONObject("target_files").getLong("id");
            return fileID;
        }
        return 0;
        
    }
    
    
    /**
     * download converted file
     * edited from Zamzar API Docs {@link https://developers.zamzar.com/docs}
     * @param fileID file ID
     * @param apiKey Zamzar API key
     * @param tFile target file
     * @throws Exception
     */
    private static void downloadFile(long fileID, String apiKey, File tFile) throws Exception {
        
        // test environment: https://sandbox.zamzar.com/v1/jobs
        // production environment: https://api.zamzar.com/v1/jobs
        String endpoint = "https://sandbox.zamzar.com/v1/files/" + fileID + "/content";

        // create HTTP client and request object
        CloseableHttpClient httpClient = getHttpClient(apiKey);
        HttpGet request = new HttpGet(endpoint);

        // make request
        CloseableHttpResponse response = httpClient.execute(request);

        // extract body from response
        HttpEntity responseContent = response.getEntity();

        // save response content to file on local disk (save to tFile)
        BufferedInputStream bis = new BufferedInputStream(responseContent.getContent());
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tFile));
        int inByte;
        while((inByte = bis.read()) != -1) {
            bos.write(inByte);
        }

        // print success message
//        System.out.println("File downloaded");

        // finalize response, client and streams
        response.close();
        httpClient.close();
        bos.close();
        bis.close();
        
    }
    
    /**
     * creates a HTTP client object that always makes requests that are signed
     * with the specified API key via Basic Auth
     * @param apiKey specified API key
     * @return HTTP client object
     */
    private static CloseableHttpClient getHttpClient(String apiKey) {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(apiKey, ""));

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultCredentialsProvider(credentialsProvider)
                .build();

        return httpClient;
    }
	
}
