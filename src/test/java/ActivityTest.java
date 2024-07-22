
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.auto.main.sales.Activity;
import org.json.simple.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Arsiela
 */
public class ActivityTest {
    public static void main(String [] args){
        String path;
        if(System.getProperty("os.name").toLowerCase().contains("win")){
            path = "D:/GGC_Maven_Systems";
        }
        else{
            path = "/srv/GGC_Maven_Systems";
        }
        System.setProperty("sys.default.path.config", path);

        GRider instance = new GRider("gRider");
        if (!instance.logUser("gRider", "M001000001")){
            System.err.println(instance.getErrMsg());
            System.exit(1);
        }

        System.out.println("Connected");
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/");
        
        JSONObject json;
        
        System.out.println("sBranch code = " + instance.getBranchCode());
        Activity model = new Activity(instance, false, instance.getBranchCode());
        
        System.out.println("--------------------------------------------------------------------");
        System.out.println("------------------------------NEW RECORD--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        
        json = model.newTransaction();
        if (!"success".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        System.err.println("result = " + (String) json.get("result"));
        
        json = model.setMaster("sActTitle","Activity Title");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        json = model.setMaster("sActDescx","Activity Description");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
    
        
        json = model.setMaster("sActTypID","M001ACTP0001");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
    
        
        json = model.setMaster("sActSrcex","TEST");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
    
        json = model.setMaster("sProvIDxx","01");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        String lsdate = "2024-08-24";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(lsdate);
        } catch (ParseException e) {
            System.err.println("Error parsing date: " + e.getMessage());
        }
        
        json = model.setMaster("dDateFrom",date);
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        lsdate = "2024-08-30";
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        date = null;
        try {
            date = sdf.parse(lsdate);
        } catch (ParseException e) {
            System.err.println("Error parsing date: " + e.getMessage());
        }
        
        json = model.setMaster("dDateThru", date);
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
    
        json = model.setMaster("sLocation","V001");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
    
        json = model.setMaster("sCompnynx","GEELY - PANGASINAN");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
    
        json = model.setMaster("nTrgtClnt",50);
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
    
        json = model.setMaster("sEmployID","M001000001");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
    
        json = model.setMaster("sDeptIDxx","M001000001");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
    
        json = model.setMaster("sLogRemrk","SAMPLE");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
    
        json = model.setMaster("sRemarksx","TEST");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        model.addActTown();
        System.out.println("town size = " + model.getActTownList().size());
        for(int lnctr = 0; lnctr < model.getActTownList().size(); lnctr++){
            model.setActTown(lnctr, "sTownIDxx", "0335");
            model.setActTown(lnctr, "sAddressx", "MALASIQUI");
        }
        
        model.addActMember();
        System.out.println("member size = " + model.getActMemberList().size());
        for(int lnctr = 0; lnctr < model.getActMemberList().size(); lnctr++){
            model.setActMember(lnctr, "sEmployID", "M001000001");
            model.setActMember(lnctr, "cOriginal", "1");
        }
        
        model.addActVehicle();
        System.out.println("vehicle size = " + model.getActVehicleList().size());
        for(int lnctr = 0; lnctr < model.getActVehicleList().size(); lnctr++){
            model.setActVehicle(lnctr, "sSerialID", "M001VS240001");
        }

        System.out.println("--------------------------------------------------------------------");
        System.out.println("------------------------------NEW RECORD SAVING--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        
        json = model.saveTransaction();
        System.err.println((String) json.get("message"));
        
        if (!"success".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        } else {
            System.out.println((String) json.get("message"));
            System.exit(0);
        }
    }
    
    
}
