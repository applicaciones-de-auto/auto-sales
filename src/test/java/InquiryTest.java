
import org.guanzon.appdriver.base.GRider;
import org.guanzon.auto.main.sales.Inquiry;
import org.json.simple.JSONObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author MIS-PC
 */
public class InquiryTest {
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
        Inquiry model = new Inquiry(instance, false, instance.getBranchCode());
        
        json = model.newTransaction();
        if (!"success".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        System.err.println("result = " + (String) json.get("result"));
        
        json = model.setMaster("sEmployID","00001");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        json = model.setMaster("cIsVhclNw","0");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
    
        
        json = model.setMaster("sVhclIDxx","Jonathan");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
    
        
        json = model.setMaster("sClientID","00001");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
    
        
        json = model.setMaster("sRemarksx","TEST");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        json = model.setMaster("dTargetDt","2024-05-24");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        json = model.setMaster("cIntrstLv", "0");
        if ("error".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        }
        
        System.out.println("promo size = " + model.getPromoList().size());
        for(int lnctr = 0; lnctr < model.getPromoList().size(); lnctr++){
            model.setPromo(lnctr, "sTransNox", "");
            model.setPromo(lnctr, "sPromoIDx", "");
            model.setPromo(lnctr, "sActTitle", "");
            model.setPromo(lnctr, "dDateFrom", "2024-03-29");
            model.setPromo(lnctr, "dDateThru", "2024-03-29");
        }
        
        for(int lnctr = 0; lnctr < model.getVehiclePriorityList().size(); lnctr++){
            model.setVehiclePriority(lnctr, "sTransNox", "");
            model.setVehiclePriority(lnctr, "nPriority", "1");
            model.setVehiclePriority(lnctr, "sVhclIDxx", "");
           
        }
        json = model.saveTransaction();
//        1200145 - barangay id
//        0335 - town id
        if (!"success".equals((String) json.get("result"))){
            System.err.println((String) json.get("message"));
            System.exit(1);
        } else {
            System.out.println((String) json.get("message"));
            System.exit(0);
        }
    }
    
}
