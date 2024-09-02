
import java.math.BigDecimal;
import java.sql.SQLException;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.auto.main.sales.VehicleSalesProposal;
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
public class VehicleSalesProposalTest {
    static VehicleSalesProposal model;
    JSONObject json;
    boolean result;
    static GRider instance;
    public VehicleSalesProposalTest(){}
    
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
        model = new VehicleSalesProposal(instance,false, instance.getBranchCode());
    }
    
    @AfterClass
    public static void tearDownClass() {
        
    }
    
    /**
     * COMMENTED TESTING TO CLEAN AND BUILD PROPERLY
     * WHEN YOU WANT TO CHECK KINDLY UNCOMMENT THE TESTING CASES (@Test).
     * ARSIELA 
     */
//    
//    @Test
//    public void test01NewRecord() throws SQLException{
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------NEW RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.newTransaction();
//        if ("success".equals((String) json.get("result"))){
//
//            json = model.setMaster("dDelvryDt", instance.getServerDate());
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            json = model.setMaster("sInqryIDx","M001IQ240001");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sClientID","M00124000028");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sCoCltIDx","M00124000003");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sSerialID","M001VS240005");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("nUnitPrce",1005000.00);
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
//            json = model.setMaster("nAdvDwPmt",200000.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sOthrDesc","TEST");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nOthrChrg",12000.65);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("nLaborAmt",12000.65);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("nAccesAmt",50555.66);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("nInsurAmt",3200.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("nTPLAmtxx",6500.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("nCompAmtx",12000.50);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("nLTOAmtxx",0.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("nChmoAmtx",0.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            json = model.setMaster("sChmoStat","2");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            json = model.setMaster("sTPLStatx","2");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            json = model.setMaster("sCompStat","2");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            json = model.setMaster("sLTOStatx","0");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            json = model.setMaster("sInsurTyp","0");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nInsurYrx",1);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("sInsTplCd","M001IN240001");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("sInsCodex","M001IN240002");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nPromoDsc",5000.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nFleetDsc",10000.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nSPFltDsc",5000.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nBndleDsc",5000.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nAddlDscx",12000.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            
//            json = model.setMaster("nDealrInc",12000.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("cPayModex","2");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("sBnkAppCD","M001BA240001");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nTranTotl",500000.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nResrvFee",0.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nDownPaym",50000.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nNetTTotl",5000.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nAmtPaidx",6000.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nFrgtChrg",85000.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nDue2Supx",10000.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nDue2Dlrx",10000.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nSPFD2Sup",10.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nSPFD2Dlr",40.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nPrmD2Sup",10000.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nPrmD2Dlr",10000.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            
//            json = model.setMaster("sEndPlate","655");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("sBranchCD","M001");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nDealrRte",5.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nDealrAmt",5000.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nSlsInRte",6000.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("nSlsInAmt",5000.00);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("cIsVhclNw","0");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("cIsVIPxxx","0");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("sDcStatCd","0");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("dDcStatDt",instance.getServerDate());
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            json = model.setMaster("cPrintedx","0");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//            
//            model.addVSPFinance();
//            System.out.println("VSP Finance size = " + model.getVSPFinanceList().size());
//            //for(int lnctr = 0; lnctr < model.getVSPFinanceList().size(); lnctr++){
//                model.getVSPFinanceModel().getVSPFinanceModel().setFinPromo("0"); //setVSPFinance(lnctr, "cFinPromo", "0");
//                model.getVSPFinanceModel().getVSPFinanceModel().setBankID("M001BK240001"); //setVSPFinance(lnctr, "sBankIDxx", "M001BK240001");
//                model.getVSPFinanceModel().getVSPFinanceModel().setBankname("BANKO DE ORO - PEREZ"); //setVSPFinance(lnctr, "sBankname", "BANKO DE ORO - PEREZ");
//                model.getVSPFinanceModel().getVSPFinanceModel().setFinAmt(new BigDecimal("5000.00"));//setVSPFinance(lnctr, "nFinAmtxx", 5000.00);
//                model.getVSPFinanceModel().getVSPFinanceModel().setAcctTerm(24); //setVSPFinance(lnctr, "nAcctTerm", 0.01);
//                model.getVSPFinanceModel().getVSPFinanceModel().setAcctRate(6.40); //setVSPFinance(lnctr, "nAcctRate", 06.40);
//                model.getVSPFinanceModel().getVSPFinanceModel().setRebates(new BigDecimal("6.00")); //setVSPFinance(lnctr, "nRebatesx", 06.00);
//                //model.getVSPFinanceModel().getVSPFinanceModel().setMonAmort(new BigDecimal("650.00")); //setVSPFinance(lnctr, "nMonAmort", 650.00);
//                //model.getVSPFinanceModel().getVSPFinanceModel().setsetVSPFinance(lnctr, "nPNValuex", 600.00);
//                model.getVSPFinanceModel().getVSPFinanceModel().setBnkPaid(new BigDecimal("4000.00")); //setVSPFinance(lnctr, "nBnkPaidx", 4000.00);
//                //model.getVSPFinanceModel().getVSPFinanceModel().setGrsMonth(50.00); //setVSPFinance(lnctr, "nGrsMonth", 50.00);
//                model.getVSPFinanceModel().getVSPFinanceModel().setNtDwnPmt(new BigDecimal("5.00")); //setVSPFinance(lnctr, "nNtDwnPmt", 5.50);
//                model.getVSPFinanceModel().getVSPFinanceModel().setDiscount(new BigDecimal("10000.00")) ; //setVSPFinance(lnctr, "nDiscount", 10000.00);
//            //}
//            
//            model.addVSPLabor();
//            System.out.println("VSP Labor size = " + model.getVSPLaborList().size());
//            for(int lnctr = 0; lnctr < model.getVSPLaborList().size(); lnctr++){
//                model.setVSPLabor(lnctr, "sLaborCde", "M00124000001");
//                model.setVSPLabor(lnctr, "nLaborAmt", 1500.00);
//                model.setVSPLabor(lnctr, "sChrgeTyp", "1");
//                model.setVSPLabor(lnctr, "sLaborDsc", "TINT");
//                model.setVSPLabor(lnctr, "nLaborDsc", 1000.00);
//                model.setVSPLabor(lnctr, "sRemarksx", "TEST");
//                model.setVSPLabor(lnctr, "cAddtlxxx", "0");
////                model.setVSPLabor(lnctr, "dAddDatex", instance.getServerDate());
////                model.setVSPLabor(lnctr, "sAddByxxx", instance.getUserID());
//            }
//            
//            model.addVSPParts();
//            System.out.println("VSP Parts size = " + model.getVSPPartsList().size());
//            for(int lnctr = 0; lnctr < model.getVSPPartsList().size(); lnctr++){
//                model.setVSPParts(lnctr, "sStockIDx", "");
//                model.setVSPParts(lnctr, "nUnitPrce", 5000.00);
//                model.setVSPParts(lnctr, "nSelPrice", 5000.00);
//                model.setVSPParts(lnctr, "nQuantity", 1);
//                model.setVSPParts(lnctr, "nReleased", 0);
//                model.setVSPParts(lnctr, "sChrgeTyp", "1");
//                model.setVSPParts(lnctr, "sPartStat", "0");
//                model.setVSPParts(lnctr, "sDescript", "SEAT COVER");
//                model.setVSPParts(lnctr, "nPartsDsc", 500.00);
////                model.setVSPParts(lnctr, "dAddDatex", instance.getServerDate());
////                model.setVSPParts(lnctr, "sAddByxxx", instance.getUserID());
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
//            System.out.println("VSP MASTER");
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("sBranchNm  :  " + model.getMaster("sBranchNm"));
//            
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("VSP FINANCE");
//            System.out.println("--------------------------------------------------------------------");
//            for(int lnCtr = 0;lnCtr <= model.getVSPFinanceList().;lnCtr++){
//                System.out.println("sTransNox  :  " +); 
//            }
//            
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("VSP LABOR");
//            System.out.println("--------------------------------------------------------------------");
//            for(int lnCtr = 0;lnCtr <= model.getVSPLaborList().size()-1; lnCtr++){
//                System.out.println("sTransNox  :  " +); 
//            }
//            
//            System.out.println("--------------------------------------------------------------------");
//            System.out.println("VSP PARTS");
//            System.out.println("--------------------------------------------------------------------");
//            for(int lnCtr = 0;lnCtr <= model.getVSPPartsList().size()-1; lnCtr++){
//                System.out.println("sTransNox  :  " +); 
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
