package action;

import server.Server;

public class Action {

    private Server server;

    public Action() {
        server = new Server();
    }

    public String getLastCommand(){
        return server.getLastCommand();
    }

    public String executeAction(String serverAddressp, String actionp){
        server.setAddress(serverAddressp);
        String console = "";
        try{
            switch (actionp){
                case "Get namespace":
                    console = server.getNamespace();
                    break;
                case "Get pods":
                    console = server.getPods();
                    break;
                case "Get Kafka manager IP":
                    console = server.getKafkaManagerIP();
                    break;
                case "Is FIPS enabled?":
                    console = server.isFIPSEnabled();
                    break;
                case "Is CA enabled?":
                    console = server.isCAEnabled();
                    break;
                case "Is TLS enabled?":
                    console = server.isTLSEnabled();
                    break;
                case "Is plain text allowed?":
                    console = server.isPlainTextAllowed();
                    break;
                case "Restart Kafka pods":
                    if(server.restartKafkaPods()){
                        console = "Kafka pods restarted, please wait for new instances to initiate. You can ask the PodCookbook for the latest pods status on the Kubernetes tab using the get Arcsight pods command.";
                    }else {
                        console = "Unable to restart Kafka pods.";
                    }
                    break;
                case "Get nodes":
                    console = server.getNodes();
                    break;
                case "Restart nodes":
                    if(server.restartNodes()){
                        console = "Nodes were commanded to reboot. Please wait for them to initiate. You can ask the PodCookbook for nodes status.";
                    }else {
                        console = "Unable to restart nodes.";
                    }
                    break;
                case "Is OS on FIPS?":
                    console = server.isOSFips();
                    break;
                case "Get OS version":
                    console = server.getOSVersion();
                    break;
                case "Get licensed EPS":
                    console = server.getLicensedEPS();
                    break;
                case "Get Arcsight Pods":
                    console = server.getArcsightPods();
                    break;
                case "Get cert value":
                    console = server.getCertValue();
                    break;
                case "Get version":
                    console = server.getTHVersion();
                    break;
                default:
                    console = "";
                    break;
            }
            return console;
        }
        catch (Exception ex){
            return "";
        }
    }
    
    public String[] getAvailableActions(String typep) {
        String[] actions = new String[1];
        try {
            switch (typep){
                case "Kubernetes":
                    actions = new String[5];
                    actions[0] = "Get namespace";
                    actions[1] = "Get pods";
                    actions[2] = "Get Arcsight Pods";
                    actions[3] = "Get nodes";
                    actions[4] = "Get version";
                    break;
                case "Security":
                    actions = new String[6];
                    actions[0] = "Is FIPS enabled?";
                    actions[1] = "Is CA enabled?";
                    actions[2] = "Is TLS enabled?";
                    actions[3] = "Is plain text allowed?";
                    actions[4] = "Is OS on FIPS?";
                    actions[5] = "Get cert value";
                    break;
                case "Kafka":
                    actions = new String[3];
                    actions[0] = "Get Kafka manager IP";
                    actions[1] = "Restart Kafka pods";
                    actions[2] = "Get licensed EPS";
                    break;
                case "Nodes":
                    actions = new String[2];
                    actions[0] = "Restart nodes";
                    actions[1] = "Get OS version";
                    break;
                default:
                    break;
            }
            return actions;
        }
        catch (Exception e){
            actions = new String[1];
            actions[0] = "No actions can be executed.";
            return actions;
        }
    }
}
