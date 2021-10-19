package server;

import api.Request;
import util.SSHUtil;

public class Server {
    private String address;
    private String username;
    private String password;
    private String owner;
    private String key;
    private SSHUtil sshUtil;
    private String lastCommand;

    public Server() {
        username = "XXX";
        password = "XXX";
        sshUtil = new SSHUtil();
        lastCommand = "No command has been executed.";
    }
    
    public Server(String addressp, String ownerp, String keyp) {
        address = addressp;
        owner = ownerp;
        key = keyp;
        username = "XXX";
        password = "XXX";
        sshUtil = new SSHUtil();
        lastCommand = "No command has been executed.";
    }

    private String executeCommand(String commandp){
        try{
            return sshUtil.runSSHCommand(address, username, password, commandp);
        }
        catch (Exception e){
            return "Command execution failed.";
        }
    }

    private String executeCommandSimple(String commandp){
        try{
            return sshUtil.runSSHCommandSimple(address, username, password, commandp);
        }
        catch (Exception e){
            return "Command execution failed.";
        }
    }

    public String getNamespace() {
        try{
            lastCommand = "kubectl get pods --all-namespaces -o wide | grep xxx-installer* | head -n 1 | cut -f 1 -d \" \"";
            return executeCommandSimple(lastCommand);
        }
        catch (Exception e){
            return "";
        }
    }

    public String getPods() {
        try{
            lastCommand = "kubectl get pods --all-namespaces";
            return executeCommand(lastCommand).replaceAll(" ", "..");
        }
        catch (Exception e){
            return "Error retrieving pods status.";
        }
    }

    public String getxxxPods() {
        try{
            lastCommand = "kubectl get pods -n " + getNamespace();
            return executeCommand(lastCommand).replaceAll(" ", "..");
        }
        catch (Exception e){
            return "Error retrieving xxx pods status.";
        }
    }

    public String getKafkaManagerIP() {
        try{
            lastCommand = "kubectl get pods -n $( kubectl get namespaces | grep xxx | cut -d ' ' -f1 ) -o wide | grep th-kafka-manager* | awk '{print $6}'";
            return executeCommandSimple(lastCommand);
        }catch (Exception e){
            return "";
        }
    }

    public String isFIPSEnabled() {
        try{
            String console;
            lastCommand = "kubectl describe pod th-kafka-0 -n " + getNamespace() + " | grep -i 'FIPS_MODE'";
            console = executeCommandSimple(lastCommand);
            if(console == null){
                console = "FIPS disabled";
            }
            return console;
        }
        catch (Exception e){
            return e.getMessage();
        }
    }

    public String isCAEnabled() {
        try{
            String console;
            lastCommand = "kubectl describe pod th-kafka-0 -n " + getNamespace() + " | grep -i 'SSL_CLIENT_AUTH_ENABLED'";
            console = executeCommandSimple(lastCommand);
            if(console == null){
                console = "Client Authentication disabled";
            }
            return console;
        }
        catch (Exception e){
            return "";
        }
    }

    public String isTLSEnabled() {
        try{
            String console;
            lastCommand = "kubectl describe pod th-kafka-0 -n " + getNamespace() + " | grep -i 'SSL_CLIENT_AUTH_ENABLED'";
            console = executeCommandSimple(lastCommand);
            if(console == null){
                console = "TLS disabled";
            }
            return console;
        }
        catch (Exception e){
            return "";
        }
    }

    public String getNodes() {
        try{
            String console;
            String nodes = "";
            lastCommand = "kubectl get nodes | grep xxx* | cut -f 1 -d \" \"";
            console = executeCommand(lastCommand);

            if(console != null){
                String[] tempNodesArray = console.split("\n");
                for(int i = 1; i < tempNodesArray.length; i++){
                    nodes = nodes + tempNodesArray[i] + " (" + getRole(tempNodesArray[i].trim()) + ")\n";
                }
                console = nodes;
            }
            else{
                console = "This master as 0 nodes.";
            }

            if(console == null){
                console = "This master as 0 nodes.";
            }
            return console;
        }
        catch (Exception e){
            return "";
        }
    }

    public String isPlainTextAllowed() {
        try{
            lastCommand = "kubectl describe pod th-kafka-0 -n " + getNamespace() + " | grep -i 'TH_KAFKA_ALLOW_PLAINTEXT:'";
            return executeCommandSimple(lastCommand);
        }
        catch (Exception e){
            return "";
        }
    }

    public boolean restartKafkaPods(){
        String tempCommand= "";
        try{
            for(int i = 0; i < Integer.parseInt(executeCommandSimple("kubectl get pods --all-namespaces | grep \"th-kafka-*.[0-9]\" | wc -l")); i++){
                tempCommand = tempCommand + "kubectl delete pod th-kafka-"+i+" -n " + getNamespace() + "\n";
                executeCommandSimple("kubectl delete pod th-kafka-"+i+" -n " + getNamespace());
            }
            lastCommand = tempCommand;
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public String getLicensedEPS(){
        try{
            String result = "";
            String tempCommand = "";

            for(int i = 0; i < Integer.parseInt(executeCommandSimple("kubectl get pods --all-namespaces | grep \"th-kafka-*.[0-9]\" | wc -l")); i++){
                tempCommand = tempCommand + "kubectl logs th-kafka-"+i+" -n " + getNamespace() + " | grep license \n";
                result = result + executeCommand("kubectl logs th-kafka-"+i+" -n " + getNamespace() + " | grep license") + "\n";
            }
            lastCommand = tempCommand;
            return result;
        }
        catch (Exception e){
            return "Unable to get licensed EPS.";
        }
    }

    public boolean restartNodes(){
        try{
            String[] nodes = getNodes().split("\\r?\\n");
            lastCommand = "";
            for(int i = 0; i < nodes.length; i++){
                if(!nodes[i].isEmpty()){
                    sshUtil.runSSHCommandSimple(getIPfromFQDN(nodes[i]), username, password, "reboot");
                    lastCommand = lastCommand + getIPfromFQDN(nodes[i]) + ": reboot \n";
                }
            }
            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    public String isOSFips() {
        String result = "";
        try {
            String[] nodes = getNodes().split("\\r?\\n");
            for(int i = 0; i < nodes.length; i++){
                if(!nodes[i].isEmpty()){
                    result = result + "Node: " + nodes[i] + "\n";// + "------------------------------" + "\n";
                    result = result + " cat /proc/sys/crypto/fips_enabled = " + executeCommand("cat /proc/sys/crypto/fips_enabled").trim() + "\n";
                    result = result + " sysctl crypto.fips_enabled = " + executeCommand("sysctl crypto.fips_enabled").trim() + "\n\n";
                }
            }
            lastCommand = "cat /proc/sys/crypto/fips_enabled" + "\n" + "sysctl crypto.fips_enabled";
        }
        catch (Exception e){
            result = "Unable to verify if FIPS is enabled the OS";
        }
        finally {
            return result;
        }
    }

    public String getOSVersion() {
        String result = "";
        try {
            String[] nodes = getNodes().split("\\r?\\n");

            for(int i = 0; i < nodes.length; i++){
                if(!nodes[i].isEmpty()){
                    result = result + nodes[i] + ": " + (String)sshUtil.runSSHCommandSimple(getIPfromFQDN(nodes[i]), username, password, "cat /etc/redhat-release") + "\n";
                }
            }
            lastCommand = "cat /etc/redhat-release";
        }
        catch (Exception e){
            result = "Unable to verify if FIPS is enabled the OS";
        }
        finally {
            return result;
        }
    }

    public String getCertValue() {
        try{
            lastCommand = "xxx";
            return executeCommand(lastCommand).replaceAll("\\\\[0m", "");
        }
        catch (Exception e){
            return "Error: Unable to retrieve certificate value.";
        }
    }
    
    private String getRole(String addressp){
        String role = "Without role";
        boolean isMaster = false;
        boolean isWorker = false;
        try{
            if(!executeCommand("kubectl get nodes | grep \"" + addressp + "\" | head -n 1 | grep \"master\"").trim().equals("")){
                role = "Master";
                isMaster = true;
            }
            else if(!executeCommand("kubectl get nodes | grep \"" + addressp + "\" | head -n 1 | grep \"worker\"").trim().equals("")){
                role = "Worker";
                isWorker = true;
            }
            else{
                role = "undefined";
            }
            
            if(isMaster && isWorker){
                role = "Master High Availability";
            }
        }
        catch(Exception e){
            role = "Error";
        }
        return role;
    }

    private String getIPfromFQDN(String fqdnp){
        fqdnp = fqdnp.replace("n","");
        fqdnp = fqdnp.replace("-h",".");
        fqdnp = fqdnp.replace("-",".");
        fqdnp = fqdnp.replace(".xxx.com","");
        return fqdnp;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() { return address; }
    
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setKey(String key) {
        this.key = key;
    }
    
    public String getKey() {
        return key;
    }

    public String getLastCommand() { return lastCommand; }

    public String getTHVersion() {
        Request request = new Request();
        return request.httpGetRequest();
    }
}
