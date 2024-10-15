
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
    static GRider instance;
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
        instance = new GRider("gRider");
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
//    public void test00loadForApproval(){
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------LOAD FOR APPROVAL--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.loadInquiryForApproval();
//        if(!"error".equals((String) json.get("result"))){
//            for(int lnCtr=0;lnCtr <= model.getInquiryList().size()-1;lnCtr++){
//                System.out.println("sTransNox  :  " + model.getInquiryModel().getDetailModel(lnCtr).getTransNo());
//            }
//        } 
//        
//        assertTrue(true);
//    
//    }
    
//        @Test 
//    public void test00loadReservationForApproval(){
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------LOAD FOR APPROVAL--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.loadReservationForApproval();
//        if(!"error".equals((String) json.get("result"))){
//            for(int lnCtr=0;lnCtr <= model.getReservationList().size()-1;lnCtr++){
//                System.out.println("sTransNox  :  " + model.getReservationModel().getDetailModel(lnCtr).getTransNo());
//            }
//        } 
//        
//        assertTrue(true);
//    
//    }
    
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
//            json = model.setMaster("sClientID","M00124000002");
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
//            json = model.setMaster("cPayModex","2");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            json = model.setMaster("cCustGrpx","1");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            //Add Vehicle Priority
//            model.addVehiclePriority();
//            System.out.println("vehicle priority size = " + model.getVehiclePriorityList().size());
//            for(int lnctr = 0; lnctr < model.getVehiclePriorityList().size(); lnctr++){
//                model.setVehiclePriority(lnctr, "nPriority", 1);
//                model.setVehiclePriority(lnctr, "sVhclIDxx", "M001VM000005");
//            }
//
//            json = model.setMaster("sVhclIDxx","M001VM000005");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            model.addPromo();
//            System.out.println("promo size = " + model.getPromoList().size());
//            for(int lnctr = 0; lnctr < model.getPromoList().size(); lnctr++){
//                model.setPromo(lnctr, "sPromoIDx", "M001AC240004");
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
    
//    @Test
//    public void test01loadRequirements(){
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------LOAD REQUIREMENTS--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.loadRequirements();
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
//        System.out.println("requirements size = " + model.getRequirementList().size());
//            for(int lnctr = 0; lnctr < model.getRequirementList().size(); lnctr++){
//                System.out.println("sRqrmtCde : " + model.getRequirement(lnctr, "sRqrmtCde"));
//                System.out.println("sDescript : " + model.getRequirement(lnctr, "sDescript"));
//            }
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
//        json = model.openTransaction("M001IQ240004");
//        
//        if (!"success".equals((String) json.get("result"))){
//            result = false;
//        } else {
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("INQUIRY MASTER");
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("sTransNox  :  " + model.getMaster("sTransNox"));
//            System.out.println("sInqryIDx  :  " + model.getMaster("sInqryIDx"));
//            System.out.println("sBranchCd  :  " + model.getMaster("sBranchCd"));
//            System.out.println("dTransact  :  " + model.getMaster("dTransact"));
//            System.out.println("sEmployID  :  " + model.getMaster("sEmployID"));
//            System.out.println("cIsVhclNw  :  " + model.getMaster("cIsVhclNw"));
//            System.out.println("sVhclIDxx  :  " + model.getMaster("sVhclIDxx"));
//            System.out.println("sClientID  :  " + model.getMaster("sClientID"));
//            System.out.println("sContctID  :  " + model.getMaster("sContctID"));
//            System.out.println("sRemarksx  :  " + model.getMaster("sRemarksx"));
//            System.out.println("sAgentIDx  :  " + model.getMaster("sAgentIDx"));
//            System.out.println("dTargetDt  :  " + model.getMaster("dTargetDt"));
//            System.out.println("cIntrstLv  :  " + model.getMaster("cIntrstLv"));
//            System.out.println("sSourceCD  :  " + model.getMaster("sSourceCD"));
//            System.out.println("sSourceNo  :  " + model.getMaster("sSourceNo"));
//            System.out.println("sTestModl  :  " + model.getMaster("sTestModl"));
//            System.out.println("sActvtyID  :  " + model.getMaster("sActvtyID"));
//            System.out.println("dLastUpdt  :  " + model.getMaster("dLastUpdt"));
//            System.out.println("sLockedBy  :  " + model.getMaster("sLockedBy"));
//            System.out.println("dLockedDt  :  " + model.getMaster("dLockedDt"));
//            System.out.println("sApproved  :  " + model.getMaster("sApproved"));
//            System.out.println("sSerialID  :  " + model.getMaster("sSerialID"));
//            System.out.println("sInqryCde  :  " + model.getMaster("sInqryCde"));
//            System.out.println("cPayModex  :  " + model.getMaster("cPayModex"));
//            System.out.println("cCustGrpx  :  " + model.getMaster("cCustGrpx"));
//            System.out.println("cTranStat  :  " + model.getMaster("cTranStat"));
//            System.out.println("sEntryByx  :  " + model.getMaster("sEntryByx"));
//            System.out.println("dEntryDte  :  " + model.getMaster("dEntryDte"));
//            System.out.println("sModified  :  " + model.getMaster("sModified"));
//            System.out.println("dModified  :  " + model.getMaster("dModified"));         
//            System.out.println("sClientNm  :  " + model.getMaster("sClientNm"));
//            System.out.println("cClientTp  :  " + model.getMaster("cClientTp"));
//            System.out.println("sAddressx  :  " + model.getMaster("sAddressx"));
//            System.out.println("sMobileNo  :  " + model.getMaster("sMobileNo"));
//            System.out.println("sEmailAdd  :  " + model.getMaster("sEmailAdd"));
//            System.out.println("sAccountx  :  " + model.getMaster("sAccountx"));
//            System.out.println("sContctNm  :  " + model.getMaster("sContctNm"));
//            System.out.println("sSalesExe  :  " + model.getMaster("sSalesExe"));
//            System.out.println("sSalesAgn  :  " + model.getMaster("sSalesAgn"));
//            System.out.println("sPlatform  :  " + model.getMaster("sPlatform"));
//            System.out.println("sActTitle  :  " + model.getMaster("sActTitle"));
//            System.out.println("sBranchNm  :  " + model.getMaster("sBranchNm"));
//            
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("INQUIRY VEHICLE PRIORITY");
//            System.out.println("--------------------------------------------------------------------");
//            for(int lnCtr = 0;lnCtr <= model.getVehiclePriorityList().size()-1; lnCtr++){
//                System.out.println("sTransNox  :  " + model.getVehiclePriority(lnCtr,"sTransNox")); 
//                System.out.println("nPriority  :  " + model.getVehiclePriority(lnCtr,"nPriority")); 
//                System.out.println("sVhclIDxx  :  " + model.getVehiclePriority(lnCtr,"sVhclIDxx"));
//                System.out.println("sEntryByx  :  " + model.getVehiclePriority(lnCtr,"sEntryByx")); 
//                System.out.println("dEntryDte  :  " + model.getVehiclePriority(lnCtr,"dEntryDte")); 
//                System.out.println("sDescript  :  " + model.getVehiclePriority(lnCtr,"sDescript")); 
//            }
//            
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("INQUIRY INQUIRY PROMO");
//            System.out.println("--------------------------------------------------------------------");
//            for(int lnCtr = 0;lnCtr <= model.getPromoList().size()-1; lnCtr++){
//                System.out.println("sTransNox  :  " + model.getPromo(lnCtr,"sTransNox")); 
//                System.out.println("sPromoIDx  :  " + model.getPromo(lnCtr,"sPromoIDx"));
//                System.out.println("sEntryByx  :  " + model.getPromo(lnCtr,"sEntryByx")); 
//                System.out.println("dEntryDte  :  " + model.getPromo(lnCtr,"dEntryDte"));  
//                System.out.println("sActNoxxx  :  " + model.getPromo(lnCtr,"sActNoxxx")); 
//                System.out.println("sActTitle  :  " + model.getPromo(lnCtr,"sActTitle")); 
//                System.out.println("dDateFrom  :  " + model.getPromo(lnCtr,"dDateFrom"));
//                System.out.println("dDateThru  :  " + model.getPromo(lnCtr,"dDateThru")); 
//            }
//            
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("INQUIRY INQUIRY REQUIREMENTS");
//            System.out.println("--------------------------------------------------------------------");
//            for(int lnCtr = 0;lnCtr <= model.getRequirementList().size()-1; lnCtr++){
//                System.out.println("sTransNox  :  " + model.getRequirement(lnCtr,"sTransNox")); 
//                System.out.println("nEntryNox  :  " + model.getRequirement(lnCtr,"nEntryNox"));
//                System.out.println("sRqrmtCde  :  " + model.getRequirement(lnCtr,"sRqrmtCde")); 
//                System.out.println("cRequired  :  " + model.getRequirement(lnCtr,"cRequired"));  
//                System.out.println("cSubmittd  :  " + model.getRequirement(lnCtr,"cSubmittd")); 
//                System.out.println("sReceived  :  " + model.getRequirement(lnCtr,"sReceived")); 
//                System.out.println("dReceived  :  " + model.getRequirement(lnCtr,"dReceived"));
//                System.out.println("sDescript  :  " + model.getRequirement(lnCtr,"sDescript")); 
//                System.out.println("cPayModex  :  " + model.getRequirement(lnCtr,"cPayModex")); 
//                System.out.println("cCustGrpx  :  " + model.getRequirement(lnCtr,"cCustGrpx")); 
//                System.out.println("sCompnyNm  :  " + model.getRequirement(lnCtr,"sCompnyNm"));
//            }
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
//        json = model.updateTransaction();
//        System.err.println((String) json.get("message"));
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            result = false;
//        } else {
//            result = true;
//        }
//        
//        json = model.setMaster("cCustGrpx","1");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("cPayModex","2");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
        
        //Add Requirements
//        model.loadRequirements();
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("INQUIRY INQUIRY REQUIREMENTS");
//        System.out.println("--------------------------------------------------------------------");
//        for(int lnCtr = 0;lnCtr <= model.getRequirementList().size()-1; lnCtr++){
//            System.out.println("sTransNox  :  " + model.getRequirement(lnCtr,"sTransNox")); 
//            System.out.println("nEntryNox  :  " + model.getRequirement(lnCtr,"nEntryNox"));
//            System.out.println("sRqrmtCde  :  " + model.getRequirement(lnCtr,"sRqrmtCde")); 
//            System.out.println("cRequired  :  " + model.getRequirement(lnCtr,"cRequired"));  
//            System.out.println("cSubmittd  :  " + model.getRequirement(lnCtr,"cSubmittd")); 
//            System.out.println("sReceived  :  " + model.getRequirement(lnCtr,"sReceived")); 
//            System.out.println("dReceived  :  " + model.getRequirement(lnCtr,"dReceived"));
//            System.out.println("sDescript  :  " + model.getRequirement(lnCtr,"sDescript")); 
//            System.out.println("cPayModex  :  " + model.getRequirement(lnCtr,"cPayModex")); 
//            System.out.println("cCustGrpx  :  " + model.getRequirement(lnCtr,"cCustGrpx")); 
//            System.out.println("sCompnyNm  :  " + model.getRequirement(lnCtr,"sCompnyNm"));
//        }
//        
//        System.out.println("requirement size = " + model.getRequirementList().size());
//        for(int lnCtr = 0; lnCtr < model.getRequirementList().size(); lnCtr++){
//            if(lnCtr == 1 || lnCtr == 3 || lnCtr == 4){
//                model.setRequirement(lnCtr, "sReceived", "A00118000010");
//                model.setRequirement(lnCtr, "dReceived", instance.getServerDate());
//            }
//        }
//        
//        
//        //Add Reservation
//        model.addReservation();
//        System.out.println("reservation size = " + model.getReservationList().size());
//        for(int lnctr = 0; lnctr < model.getReservationList().size(); lnctr++){
//            model.setReservation(lnctr, "nAmountxx", 10000.00);
//            model.setReservation(lnctr, "cResrvTyp", "0");
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
//    
//    @Test
//    public void test04OpenRecord(){
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------RETRIEVAL--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.openTransaction("M001IQ240004");
//        
//        if (!"success".equals((String) json.get("result"))){
//            result = false;
//        } else {
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("INQUIRY MASTER");
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("sTransNox  :  " + model.getMaster("sTransNox"));
//            System.out.println("sInqryIDx  :  " + model.getMaster("sInqryIDx"));
//            System.out.println("sBranchCd  :  " + model.getMaster("sBranchCd"));
//            System.out.println("dTransact  :  " + model.getMaster("dTransact"));
//            System.out.println("sEmployID  :  " + model.getMaster("sEmployID"));
//            System.out.println("cIsVhclNw  :  " + model.getMaster("cIsVhclNw"));
//            System.out.println("sVhclIDxx  :  " + model.getMaster("sVhclIDxx"));
//            System.out.println("sClientID  :  " + model.getMaster("sClientID"));
//            System.out.println("sContctID  :  " + model.getMaster("sContctID"));
//            System.out.println("sRemarksx  :  " + model.getMaster("sRemarksx"));
//            System.out.println("sAgentIDx  :  " + model.getMaster("sAgentIDx"));
//            System.out.println("dTargetDt  :  " + model.getMaster("dTargetDt"));
//            System.out.println("cIntrstLv  :  " + model.getMaster("cIntrstLv"));
//            System.out.println("sSourceCD  :  " + model.getMaster("sSourceCD"));
//            System.out.println("sSourceNo  :  " + model.getMaster("sSourceNo"));
//            System.out.println("sTestModl  :  " + model.getMaster("sTestModl"));
//            System.out.println("sActvtyID  :  " + model.getMaster("sActvtyID"));
//            System.out.println("dLastUpdt  :  " + model.getMaster("dLastUpdt"));
//            System.out.println("sLockedBy  :  " + model.getMaster("sLockedBy"));
//            System.out.println("dLockedDt  :  " + model.getMaster("dLockedDt"));
//            System.out.println("sApproved  :  " + model.getMaster("sApproved"));
//            System.out.println("sSerialID  :  " + model.getMaster("sSerialID"));
//            System.out.println("sInqryCde  :  " + model.getMaster("sInqryCde"));
//            System.out.println("cPayModex  :  " + model.getMaster("cPayModex"));
//            System.out.println("cCustGrpx  :  " + model.getMaster("cCustGrpx"));
//            System.out.println("cTranStat  :  " + model.getMaster("cTranStat"));
//            System.out.println("sEntryByx  :  " + model.getMaster("sEntryByx"));
//            System.out.println("dEntryDte  :  " + model.getMaster("dEntryDte"));
//            System.out.println("sModified  :  " + model.getMaster("sModified"));
//            System.out.println("dModified  :  " + model.getMaster("dModified"));         
//            System.out.println("sClientNm  :  " + model.getMaster("sClientNm"));
//            System.out.println("cClientTp  :  " + model.getMaster("cClientTp"));
//            System.out.println("sAddressx  :  " + model.getMaster("sAddressx"));
//            System.out.println("sMobileNo  :  " + model.getMaster("sMobileNo"));
//            System.out.println("sEmailAdd  :  " + model.getMaster("sEmailAdd"));
//            System.out.println("sAccountx  :  " + model.getMaster("sAccountx"));
//            System.out.println("sContctNm  :  " + model.getMaster("sContctNm"));
//            System.out.println("sSalesExe  :  " + model.getMaster("sSalesExe"));
//            System.out.println("sSalesAgn  :  " + model.getMaster("sSalesAgn"));
//            System.out.println("sPlatform  :  " + model.getMaster("sPlatform"));
//            System.out.println("sActTitle  :  " + model.getMaster("sActTitle"));
//            System.out.println("sBranchNm  :  " + model.getMaster("sBranchNm"));
//            
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("INQUIRY VEHICLE PRIORITY");
//            System.out.println("--------------------------------------------------------------------");
//            for(int lnCtr = 0;lnCtr <= model.getVehiclePriorityList().size()-1; lnCtr++){
//                System.out.println("sTransNox  :  " + model.getVehiclePriority(lnCtr,"sTransNox")); 
//                System.out.println("nPriority  :  " + model.getVehiclePriority(lnCtr,"nPriority")); 
//                System.out.println("sVhclIDxx  :  " + model.getVehiclePriority(lnCtr,"sVhclIDxx"));
//                System.out.println("sEntryByx  :  " + model.getVehiclePriority(lnCtr,"sEntryByx")); 
//                System.out.println("dEntryDte  :  " + model.getVehiclePriority(lnCtr,"dEntryDte")); 
//                System.out.println("sDescript  :  " + model.getVehiclePriority(lnCtr,"sDescript")); 
//            }
//            
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("INQUIRY INQUIRY PROMO");
//            System.out.println("--------------------------------------------------------------------");
//            for(int lnCtr = 0;lnCtr <= model.getPromoList().size()-1; lnCtr++){
//                System.out.println("sTransNox  :  " + model.getPromo(lnCtr,"sTransNox")); 
//                System.out.println("sPromoIDx  :  " + model.getPromo(lnCtr,"sPromoIDx"));
//                System.out.println("sEntryByx  :  " + model.getPromo(lnCtr,"sEntryByx")); 
//                System.out.println("dEntryDte  :  " + model.getPromo(lnCtr,"dEntryDte"));  
//                System.out.println("sActNoxxx  :  " + model.getPromo(lnCtr,"sActNoxxx")); 
//                System.out.println("sActTitle  :  " + model.getPromo(lnCtr,"sActTitle")); 
//                System.out.println("dDateFrom  :  " + model.getPromo(lnCtr,"dDateFrom"));
//                System.out.println("dDateThru  :  " + model.getPromo(lnCtr,"dDateThru")); 
//            }
//            
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("INQUIRY INQUIRY REQUIREMENTS");
//            System.out.println("--------------------------------------------------------------------");
//            for(int lnCtr = 0;lnCtr <= model.getRequirementList().size()-1; lnCtr++){
//                System.out.println("sTransNox  :  " + model.getRequirement(lnCtr,"sTransNox")); 
//                System.out.println("nEntryNox  :  " + model.getRequirement(lnCtr,"nEntryNox"));
//                System.out.println("sRqrmtCde  :  " + model.getRequirement(lnCtr,"sRqrmtCde")); 
//                System.out.println("cRequired  :  " + model.getRequirement(lnCtr,"cRequired"));  
//                System.out.println("cSubmittd  :  " + model.getRequirement(lnCtr,"cSubmittd")); 
//                System.out.println("sReceived  :  " + model.getRequirement(lnCtr,"sReceived")); 
//                System.out.println("dReceived  :  " + model.getRequirement(lnCtr,"dReceived"));
//                System.out.println("sDescript  :  " + model.getRequirement(lnCtr,"sDescript")); 
//                System.out.println("cPayModex  :  " + model.getRequirement(lnCtr,"cPayModex")); 
//                System.out.println("cCustGrpx  :  " + model.getRequirement(lnCtr,"cCustGrpx")); 
//                System.out.println("sCompnyNm  :  " + model.getRequirement(lnCtr,"sCompnyNm"));
//            }
//            
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
