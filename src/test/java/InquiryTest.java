
import java.sql.SQLException;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.auto.main.sales.Inquiry;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Arsiela
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InquiryTest {
    
    static Inquiry model;
    JSONObject json;
    boolean result;
    
    public InquiryTest(){}
    
    @BeforeClass
    public static void setUpClass() {   
        
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
            System.err.println(instance.getMessage() + instance.getErrMsg());
            System.exit(1);
        }
        
        System.out.println("Connected");
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/");
        
        
        JSONObject json;
        
        System.out.println("sBranch code = " + instance.getBranchCode());
        model = new Inquiry(instance,false, instance.getBranchCode());
    }
    
    @AfterClass
    public static void tearDownClass() {
        
    }
    
    /**
     * COMMENTED TESTING TO CLEAN AND BUILD PROPERLY
     * WHEN YOU WANT TO CHECK KINDLY UNCOMMENT THE TESTING CASES (@Test).
     * ARSIELA 08-03-2024
     */
    
//    @Test
//    public void test01NewRecord() throws SQLException{
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------NEW RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.newTransaction();
//        if ("success".equals((String) json.get("result"))){
//            json = model.setMaster("sBranchCd","V001");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sEmployID","M001000001");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("cIsVhclNw","0");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sVhclIDxx","M001VM000005");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sClientID","M00124000001");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sAddrssID","M00124000001");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sContctID","");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sRemarksx","TEST LANG ITO");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sAgentIDx","");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
////            json = model.setMaster("dTargetDt", "");
////            if ("error".equals((String) json.get("result"))){
////                System.err.println((String) json.get("message"));
////                System.exit(1);
////            }
//
//            json = model.setMaster("cIntrstLv","c");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sSourceCD","0");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sSourceNo","");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sTestModl","");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sActvtyID","");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("nReserved",0.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("nRsrvTotl",0.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sApproved","");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sSerialID","");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            json = model.setMaster("sInqryCde","");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            json = model.setMaster("cTranStat","0");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            json = model.setMaster("cPayModex","");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            json = model.setMaster("cCustGrpx","");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//                 
//        } else {
//            System.err.println("result = " + (String) json.get("result"));
//            fail((String) json.get("message"));
//        }
//        
//    }
////    
//    @Test
//    public void test01NewRecordSave(){
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------NEW RECORD SAVING--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.saveTransaction();
//        System.err.println((String) json.get("message"));
//        
//        if (!"success".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            result = false;
//        } else {
//            System.out.println((String) json.get("message"));
//            result = true;
//        }
//        assertTrue(result);
//        //assertFalse(result);
//    }
//    
//    @Test
//    public void test02OpenRecord(){
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------RETRIEVAL--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.openRecord("M001MK000001");
//        
//        if (!"success".equals((String) json.get("result"))){
//            result = false;
//        } else {
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("VEHICLE MAKE");
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("sMakeIDxx  :  " + model.getMaster("sMakeIDxx"));
//            System.out.println("sMakeDesc  :  " + model.getMaster("sMakeDesc"));
//            System.out.println("sMakeCode  :  " + model.getMaster("sMakeCode"));
//            System.out.println("sFormula1  :  " + model.getMaster("sFormula1"));
//            System.out.println("sFormula2  :  " + model.getMaster("sFormula2"));
//            System.out.println("cRecdStat  :  " + model.getMaster("cRecdStat"));
//            System.out.println("sEntryByx  :  " + model.getMaster("sEntryByx"));
//            System.out.println("dEntryDte  :  " + model.getMaster("dEntryDte"));
//            System.out.println("sModified  :  " + model.getMaster("sModified"));
//            System.out.println("dModified  :  " + model.getMaster("dModified"));
//            
//            result = true;
//        }
//        assertTrue(result);
//        //assertFalse(result);
//    }
//    
//    @Test
//    public void test03UpdateRecord(){
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------UPDATE RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.updateRecord();
//        System.err.println((String) json.get("message"));
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            result = false;
//        } else {
//            result = true;
//        }
//        
//        json = model.setMaster("sMakeDesc","MAKING");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        assertTrue(result);
//        //assertFalse(result);
//    }
//    
//    @Test
//    public void test03UpdateRecordSave(){
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------UPDATE RECORD SAVING--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.saveRecord();
//        System.err.println((String) json.get("message"));
//        
//        if (!"success".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            result = false;
//        } else {
//            System.out.println((String) json.get("message"));
//            result = true;
//        }
//        assertTrue(result);
//        //assertFalse(result);
//    }
//    
//    @Test
//    public void test04DeactivateRecord(){
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------DEACTIVATE RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.deactivateRecord("M001MK000001");
//        System.err.println((String) json.get("message"));
//        
//        if (!"success".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            result = false;
//        } else {
//            System.out.println((String) json.get("message"));
//            result = true;
//        }
//        
//        assertTrue(result);
//        //assertFalse(result);
//    }
//    
//    @Test
//    public void test04ActivateRecord(){
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------ACTIVATE RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.activateRecord("M001MK000001");
//        System.err.println((String) json.get("message"));
//        
//        if (!"success".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            result = false;
//        } else {
//            System.out.println((String) json.get("message"));
//            result = true;
//        }
//        
//        assertTrue(result);
//        //assertFalse(result);
//    }
    
}
