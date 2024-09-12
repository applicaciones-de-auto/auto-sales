
import java.math.BigDecimal;
import java.sql.SQLException;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.auto.main.sales.VehicleDeliveryReceipt;
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
public class VehicleDeliveryReceiptTest {
    static VehicleDeliveryReceipt model;
    JSONObject json;
    boolean result;
    static GRider instance;
    
    public VehicleDeliveryReceiptTest(){}
    
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
        model = new VehicleDeliveryReceipt(instance,false, instance.getBranchCode());
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
//            json = model.getMasterModel().getMasterModel().setSourceNo("M001VSP24006");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.getMasterModel().getMasterModel().setClientID("M00124000031");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.getMasterModel().getMasterModel().setSerialID("M001VS240015");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.getMasterModel().getMasterModel().setInqTran("M001IQ240011");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.getMasterModel().getMasterModel().setBranchCD("M057");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.getMasterModel().getMasterModel().setGrossAmt(new BigDecimal("1500000.00"));
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.getMasterModel().getMasterModel().setDiscount(new BigDecimal("500000.00"));
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.getMasterModel().getMasterModel().setTranTotl(new BigDecimal("1000000.00"));
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
//        json = model.openTransaction("M001VDR24001");
//        
//        if (!"success".equals((String) json.get("result"))){
//            result = false;
//        } else {
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("VEHICLE DELIVERY RECEIPT");
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("getAddress  :  " + model.getMasterModel().getMasterModel().getAddress());
//            System.out.println("getApproved  :  " + model.getMasterModel().getMasterModel().getApproved());
//            System.out.println("getBranchCD  :  " + model.getMasterModel().getMasterModel().getBranchCD());
//            System.out.println("getBranchNm  :  " + model.getMasterModel().getMasterModel().getBranchNm());
//            System.out.println("getBuyCltNm  :  " + model.getMasterModel().getMasterModel().getBuyCltNm());
//            System.out.println("getCSNo  :  " + model.getMasterModel().getMasterModel().getCSNo());
//            System.out.println("getCallStat  :  " + model.getMasterModel().getMasterModel().getCallStat());
//            System.out.println("getClientID  :  " + model.getMasterModel().getMasterModel().getClientID());
//            System.out.println("getClientTp  :  " + model.getMasterModel().getMasterModel().getClientTp());
//            System.out.println("getCoCltNm  :  " + model.getMasterModel().getMasterModel().getCoCltNm());
//            System.out.println("getCustType  :  " + model.getMasterModel().getMasterModel().getCustType());
//            System.out.println("getEngineNo  :  " + model.getMasterModel().getMasterModel().getEngineNo());
//            System.out.println("getEntryBy  :  " + model.getMasterModel().getMasterModel().getEntryBy());
//            System.out.println("getEntryDte  :  " + model.getMasterModel().getMasterModel().getEntryDte());
//            System.out.println("getFrameNo  :  " + model.getMasterModel().getMasterModel().getFrameNo());
//            System.out.println("getInqTran  :  " + model.getMasterModel().getMasterModel().getInqTran());
//            System.out.println("getIsVhclNw  :  " + model.getMasterModel().getMasterModel().getIsVhclNw());
//            System.out.println("getKeyNo  :  " + model.getMasterModel().getMasterModel().getKeyNo());
//            System.out.println("getModifiedBy  :  " + model.getMasterModel().getMasterModel().getModifiedBy());
//            System.out.println("getPONo  :  " + model.getMasterModel().getMasterModel().getPONo());
//            System.out.println("getPlateNo  :  " + model.getMasterModel().getMasterModel().getPlateNo());
//            System.out.println("getPrepared  :  " + model.getMasterModel().getMasterModel().getPrepared());
//            System.out.println("getPrinted  :  " + model.getMasterModel().getMasterModel().getPrinted());
//            System.out.println("getReferNo  :  " + model.getMasterModel().getMasterModel().getReferNo());
//            System.out.println("getRemarks  :  " + model.getMasterModel().getMasterModel().getRemarks());
//            System.out.println("getSerialID  :  " + model.getMasterModel().getMasterModel().getSerialID());
//            System.out.println("getSourceCd  :  " + model.getMasterModel().getMasterModel().getSourceCd());
//            System.out.println("getSourceNo  :  " + model.getMasterModel().getMasterModel().getSourceNo());
//            System.out.println("getTranStat  :  " + model.getMasterModel().getMasterModel().getTranStat());
//            System.out.println("getTransNo  :  " + model.getMasterModel().getMasterModel().getTransNo());
//            System.out.println("getTranTotl  :  " + model.getMasterModel().getMasterModel().getTranTotl());
//            System.out.println("getTransactDte  :  " + model.getMasterModel().getMasterModel().getTransactDte());
//            System.out.println("getVSPNO  :  " + model.getMasterModel().getMasterModel().getVSPNO());
//            System.out.println("getVSPTrans  :  " + model.getMasterModel().getMasterModel().getVSPTrans());
//            System.out.println("getVhclDesc  :  " + model.getMasterModel().getMasterModel().getVhclDesc());
//            System.out.println("getVSPDate  :  " + model.getMasterModel().getMasterModel().getVSPDate());
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
//        json =  model.getMasterModel().getMasterModel().setRemarks("TESTING LANG ITO");
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
