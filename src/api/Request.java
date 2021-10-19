package api;
//1
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
//
////2
//import java.io.IOException;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.util.Base64;

//3


public class Request {
    
    public String request4(){
        String retorno = "";
        try{
 
        }
        catch(Exception e){
            
        }
        finally{
            return retorno;
        }
    }
    
    public String httpGetRequest(){
        
        String value = "";
        
        try{
            //DO NOT REMOVE
            java.lang.System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
            
            
            URL urlForGetRequest = new URL("https://x.x.x.x:x/cluster/version");
            String readLine = null;
            HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
            conection.setRequestMethod("GET");
//            conection.setRequestProperty("user", "admin");
//            conection.setRequestProperty("password", "atlas");
            
            int responseCode = conection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(conection.getInputStream()));
                StringBuffer response = new StringBuffer();
                while ((readLine = in .readLine()) != null) {
                    response.append(readLine);
                } in.close();
                // print result
                System.out.println("JSON String Result " + response.toString());
                //GetAndPost.POSTRequest(response.toString());
                value = response.toString();
            } else {
                System.out.println("GET NOT WORKED");
            }   
        }
        catch(Exception e){
            value = "Error. Reason: " + e.getMessage();
        }
        finally{
            return value;
        }
    }
}
