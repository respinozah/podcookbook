package podcookbook;

import server.*;
import action.*;

public class Manager {

    private Action action;

    Manager(){
        action = new Action();
    }

    public String[] getServersAddressList(){
        Servers servers = new Servers();
        return servers.getServersAddressList();
    }
    
    public String[][] getServersValues(){
        Servers servers = new Servers();
        return servers.getServersValues();
    }

    public void addServer(String addressp, String ownerp, String keyp){
        Servers servers = new Servers();
        servers.addServer(addressp, ownerp, keyp);
    }
    
    public void removeServer(String addressp){
        Servers servers = new Servers();
        servers.removeServer(addressp);
    }
    
    public String[] getAvailableActions(String typep){
        Action action = new Action();
        return action.getAvailableActions(typep);
    }

    public String executeAction(String serverAddressp, String actionp){
        return action.executeAction(serverAddressp, actionp);
    }

    public String getLastCommand(){
        return action.getLastCommand();
    }
}
