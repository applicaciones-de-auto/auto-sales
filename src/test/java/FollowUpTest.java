
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.auto.main.sales.FollowUp;
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
public class FollowUpTest {
    
    static FollowUp model;
    JSONObject json;
    boolean result;
    static GRider instance;
    public FollowUpTest(){}
    
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
        instance = new GRider("gRider");
        if (!instance.logUser("gRider", "M001000001")){
            System.err.println(instance.getMessage() + instance.getErrMsg());
            System.exit(1);
        }
        System.out.println("Connected");
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/");
        
        
        JSONObject json;
        
        System.out.println("sBranch code = " + instance.getBranchCode());
        model = new FollowUp(instance,false, instance.getBranchCode());
    }
    
    @AfterClass
    public static void tearDownClass() {
        
    }
    
    /**
     * COMMENTED TESTING TO CLEAN AND BUILD PROPERLY
     * WHEN YOU WANT TO CHECK KINDLY UNCOMMENT THE TESTING CASES (@Test).
     * ARSIELA
     */
    
//    @Test
//    public void test01NewRecord() throws SQLException{
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------NEW RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.newTransaction();
//        if ("success".equals((String) json.get("result"))){
//            json = model.setMaster("dTransact",instance.getServerDate());
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("dFollowUp",instance.getServerDate());
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sTransNox","M001IQ240001");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("tFollowUp",Time.valueOf("14:31:01"));
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
//            json = model.setMaster("sMessagex","TEST MESSAGE");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sMethodCd","M00124000001");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sMkeCmptr","TOYOTA");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sDlrCmptr","TOYOTA DAGUPAN");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            json = model.setMaster("sRspnseCd","3");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            json = model.setMaster("sEmployID","A00118000001");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
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
    
    
//    @Test
//    public void test02OpenRecord(){
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------RETRIEVAL--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.openTransaction("M001BA240001");
//        
//        if (!"success".equals((String) json.get("result"))){
//            result = false;
//        } else {
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("BANK APPLICATION");
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("sTransNox  :  " + model.getMaster("sTransNox"));
//            System.out.println("sApplicNo  :  " + model.getMaster("sApplicNo"));
//            System.out.println("dAppliedx  :  " + model.getMaster("dAppliedx"));
//            System.out.println("dApproved  :  " + model.getMaster("dApproved"));
//            System.out.println("cPayModex  :  " + model.getMaster("cPayModex"));
//            System.out.println("sSourceCD  :  " + model.getMaster("sSourceCD"));
//            System.out.println("sSourceNo  :  " + model.getMaster("sSourceNo"));
//            System.out.println("sBrBankID  :  " + model.getMaster("sBrBankID"));
//            System.out.println("sRemarksx  :  " + model.getMaster("sRemarksx"));
//            System.out.println("cTranStat  :  " + model.getMaster("cTranStat"));
//            System.out.println("sEntryByx  :  " + model.getMaster("sEntryByx"));
//            System.out.println("dEntryDte  :  " + model.getMaster("dEntryDte"));
//            System.out.println("sModified  :  " + model.getMaster("sModified"));
//            System.out.println("dModified  :  " + model.getMaster("dModified"));
//            System.out.println("sCancelld  :  " + model.getMaster("sCancelld"));
//            System.out.println("dCancelld  :  " + model.getMaster("dCancelld"));
//            System.out.println("sBrBankNm  :  " + model.getMaster("sBrBankNm"));
//            System.out.println("sBankIDxx  :  " + model.getMaster("sBankIDxx"));
//            System.out.println("sBankName  :  " + model.getMaster("sBankName"));
//            System.out.println("sTownName  :  " + model.getMaster("sTownName"));
//            System.out.println("sProvName  :  " + model.getMaster("sProvName"));
//            
//            
//            result = true;
//        }
//        assertTrue(result);
//        //assertFalse(result);
//    }
    
//    @Test
//    public void test03UpdateRecord(){
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------UPDATE RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.updateTransaction();
//        System.err.println((String) json.get("message"));
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            result = false;
//        } else {
//            result = true;
//        }
//        
//        json = model.setMaster("dApproved",instance.getServerDate());
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("cTranStat","2");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
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
}
