package server;

import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Servers {

    private Server[] serversList;
    private String[] serversAddressList;

    public Servers() {
        loadServers();
        loadServersAddressList();
    }

    private void loadServers(){
        try{
            String content = new String(Files.readAllBytes(Paths.get("servers.json")));
            Gson gson=new Gson();
            serversList = gson.fromJson(content, Server[].class);
        }
        catch(Exception e){
            System.out.println("Error loading servers from configuration. Error: " + e.getMessage() + ".");
        }
    }

    private void loadServersAddressList(){
        try {
            serversAddressList = new String[serversList.length];
            if (serversList.length > 0) {
                for (int i = 0; i < serversList.length; i++) {
                    serversAddressList[i] = serversList[i].getAddress();
                }
            } else {
                serversAddressList = new String[1];
                serversAddressList[0] = "There are no configured servers";
            }
        }
        catch (Exception e){
            System.out.println("Error on Servers when loading servers address list. Error: " + e.getMessage());
        }
    }

    public void addServer(String addressp, String ownerp, String keyp){
        Server newServer = new Server(addressp, ownerp, keyp);
        Server[] serversListAux = new Server[serversList.length + 1];
        
        try{
            for(int i = 0; i < serversList.length; i++){
               serversListAux[i] = serversList[i];
            }
            serversListAux[serversList.length] = newServer;
            serversList = serversListAux;
            updateServersJsonFile();
        }
        catch(Exception e){
            System.out.println("Error adding new server. Error: " + e.getMessage() + e.getClass() + ".");
        }     
    }
    
    public void removeServer(String addressp){
        Server removedServer = new Server();
        removedServer.setAddress(addressp);
        Server[] serversListAux = new Server[serversList.length - 1];
        
        try{
            for(int i = 0; i < serversList.length; i++){
                if(!serversList[i].getAddress().equals(addressp)){
                    serversListAux[i] = serversList[i];
                }
            }
            serversList = serversListAux;
            updateServersJsonFile();
        }
        catch(Exception e){
            System.out.println("Error removing server. Error: " + e.getMessage() + e.getClass() + ".");
        }
    }
    
    private void updateServersJsonFile(){
        try{
            String content = "[";
            for(int i = 0; i < serversList.length; i++){
                content = content + "{\"address\": \"" + serversList[i].getAddress() + "\",";
                content = content + "\"owner\": \"" + serversList[i].getOwner() + "\",";
                content = content + "\"key\": \"" + serversList[i].getKey() + "\"}";
                if(i != serversList.length - 1){
                    content = content + ",";
                }
            }
            content = content + "]";

            BufferedWriter writer = new BufferedWriter(new FileWriter("servers.json"));
            writer.write(content);
            writer.close();
        }
        catch(Exception e){
            
        }
    }
    
    public String[][] getServersValues(){
        String[][] serversValues = new String[serversList.length][3];
        for(int i = 0; i < serversList.length; i++){
            serversValues[i][0] = serversList[i].getAddress();
            serversValues[i][1] = serversList[i].getOwner();
            serversValues[i][2] = serversList[i].getKey();
        }
        return serversValues;
    }
    
    private Server[] getServersList() {
        return serversList;
    }

    public String[] getServersAddressList() {
        return serversAddressList;
    }
}
