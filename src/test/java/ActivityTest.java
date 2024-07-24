
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.auto.main.sales.Activity;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import static org.junit.Assert.assertFalse;
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
public class ActivityTest {
    
    static Activity model;
    JSONObject json;
    boolean result;
    
    public ActivityTest(){}
    
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
        model = new Activity(instance,false, instance.getBranchCode());
    }
    
    @AfterClass
    public static void tearDownClass() {
        
    }
    
//    @Test
//    public void test01NewRecord() {
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------NEW RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.newTransaction();
//        if ("success".equals((String) json.get("result"))){
//            json = model.setMaster("sActTitle","Activity Title");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sActDescx","Activity Description");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//
//            json = model.setMaster("sActTypID","M001ACTP0001");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//
//            json = model.setMaster("sActSrcex","TEST");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sProvIDxx","01");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            String lsdate = "2024-08-24";
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            Date date = null;
//            try {
//                date = sdf.parse(lsdate);
//            } catch (ParseException e) {
//                System.err.println("Error parsing date: " + e.getMessage());
//            }
//
//            json = model.setMaster("dDateFrom",date);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            lsdate = "2024-08-30";
//            sdf = new SimpleDateFormat("yyyy-MM-dd");
//            date = null;
//            try {
//                date = sdf.parse(lsdate);
//            } catch (ParseException e) {
//                System.err.println("Error parsing date: " + e.getMessage());
//            }
//
//            json = model.setMaster("dDateThru", date);
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sLocation","V001");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sCompnynx","GEELY - PANGASINAN");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("nTrgtClnt",50);
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
//            json = model.setMaster("sDeptIDxx","M001000001");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sLogRemrk","SAMPLE");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            json = model.setMaster("sRemarksx","TEST");
//            if ("error".equals((String) json.get("result"))){
//                System.err.println((String) json.get("message"));
//                System.exit(1);
//            }
//
//            model.loadDepartment();
//            System.out.println("department size = " + model.getDepartmentList().size());
//            for(int lnctr = 0; lnctr < model.getDepartmentList().size(); lnctr++){
//                System.out.println("Department ID : " + model.getDepartmentID(lnctr, lnctr));
//                System.out.println("Department Nn : " + model.getDepartmentNm(lnctr, lnctr));
//            }
//
//            model.loadEmployee("026");
//            System.out.println("employee size = " + model.getEmployeeList().size());
//            for(int lnctr = 0; lnctr < model.getEmployeeList().size(); lnctr++){
//                System.out.println("Employee ID : " + model.getEmployeeID(lnctr, lnctr));
//                System.out.println("Employee Nn : " + model.getEmployeeNm(lnctr, lnctr));
//            }
//
//            model.loadVehicle();
//            System.out.println("vehicle size = " + model.getVehicleList().size());
//            for(int lnctr = 0; lnctr < model.getVehicleList().size(); lnctr++){
//                System.out.println("Vehicle Serial ID : " + model.getSerialID(lnctr, lnctr));
//                System.out.println("Vehicle Description  : " + model.getVehicleDesc(lnctr, lnctr));
//            }
//
//
//
//            model.addActLocation();
//            System.out.println("town size = " + model.getActLocationList().size());
//            for(int lnctr = 0; lnctr < model.getActLocationList().size(); lnctr++){
//                model.setActLocation(lnctr, "sProvIDxx", "01");
//                model.setActLocation(lnctr, "sTownIDxx", "0335");
//                model.setActLocation(lnctr, "sAddressx", "MALASIQUI");
//            }
//
//            model.addActMember();
//            System.out.println("member size = " + model.getActMemberList().size());
//            for(int lnctr = 0; lnctr < model.getActMemberList().size(); lnctr++){
//                model.setActMember(lnctr, "sEmployID", "M001000001");
//                model.setActMember(lnctr, "cOriginal", "1");
//            }
//
//            model.addActVehicle();
//            System.out.println("vehicle size = " + model.getActVehicleList().size());
//            for(int lnctr = 0; lnctr < model.getActVehicleList().size(); lnctr++){
//                model.setActVehicle(lnctr, "sSerialID", "M001VS240001");
//            }
//                 
//        } else {
//            System.err.println("result = " + (String) json.get("result"));
//            fail((String) json.get("message"));
//        }
//        
//    }
//    
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
//    }
    
    @Test
    public void test02OpenRecord(){
        System.out.println("--------------------------------------------------------------------");
        System.out.println("------------------------------RETRIEVAL--------------------------------------");
        System.out.println("--------------------------------------------------------------------");
        
        json = model.openTransaction("M00124000001");
        
        if (!"success".equals((String) json.get("result"))){
            result = false;
        } else {
            System.out.println("--------------------------------------------------------------------");
            System.out.println("ACTVITIY MASTER");
            System.out.println("--------------------------------------------------------------------");
            System.out.println("sActvtyID  :  " + model.getMaster("sActvtyID"));
            System.out.println("sActNoxxx  :  " + model.getMaster("sActNoxxx"));
            System.out.println("sActTitle  :  " + model.getMaster("sActTitle"));
            System.out.println("sActDescx  :  " + model.getMaster("sActDescx"));
            System.out.println("sActTypID  :  " + model.getMaster("sActTypID"));
            System.out.println("sActSrcex  :  " + model.getMaster("sActSrcex"));
            System.out.println("dDateFrom  :  " + model.getMaster("dDateFrom"));
            System.out.println("dDateThru  :  " + model.getMaster("dDateThru"));
            System.out.println("sLocation  :  " + model.getMaster("sLocation"));
            System.out.println("nPropBdgt  :  " + model.getMaster("nPropBdgt"));
            System.out.println("nRcvdBdgt  :  " + model.getMaster("nRcvdBdgt"));
            System.out.println("nTrgtClnt  :  " + model.getMaster("nTrgtClnt"));
            System.out.println("sEmployID  :  " + model.getMaster("sEmployID"));
            System.out.println("sDeptIDxx  :  " + model.getMaster("sDeptIDxx"));
            System.out.println("sLogRemrk  :  " + model.getMaster("sLogRemrk"));
            System.out.println("sRemarksx  :  " + model.getMaster("sRemarksx"));
            System.out.println("cTranStat  :  " + model.getMaster("cTranStat"));
            System.out.println("sEntryByx  :  " + model.getMaster("sEntryByx"));
            System.out.println("dEntryDte  :  " + model.getMaster("dEntryDte"));
            System.out.println("sApproved  :  " + model.getMaster("sApproved"));
            System.out.println("dApproved  :  " + model.getMaster("dApproved"));
            System.out.println("sModified  :  " + model.getMaster("sModified"));
            System.out.println("dModified  :  " + model.getMaster("dModified"));
            System.out.println("sDeptName  :  " + model.getMaster("sDeptName"));
            System.out.println("sCompnyNm  :  " + model.getMaster("sCompnyNm"));
            System.out.println("sBranchNm  :  " + model.getMaster("sBranchNm"));
            System.out.println("sEventTyp  :  " + model.getMaster("sEventTyp"));
            System.out.println("sActTypDs  :  " + model.getMaster("sActTypDs"));
            
            System.out.println("--------------------------------------------------------------------");
            System.out.println("ACTIVITY LOCATION");
            System.out.println("--------------------------------------------------------------------");
            for(int lnCtr = 0;lnCtr <= model.getActLocationList().size()-1; lnCtr++){
                System.out.println("nEntryNox  :  " + model.getActLocation(lnCtr,"nEntryNox")); 
                System.out.println("sAddressx  :  " + model.getActLocation(lnCtr,"sAddressx")); 
                System.out.println("sBrgyIDxx  :  " + model.getActLocation(lnCtr,"sBrgyIDxx"));
                System.out.println("sTownIDxx  :  " + model.getActLocation(lnCtr,"sTownIDxx")); 
                System.out.println("sCompnynx  :  " + model.getActLocation(lnCtr,"sCompnynx")); 
                System.out.println("sBrgyName  :  " + model.getActLocation(lnCtr,"sBrgyName")); 
                System.out.println("sBrgyIDxx  :  " + model.getActLocation(lnCtr,"sBrgyIDxx")); 
                System.out.println("sTownName  :  " + model.getActLocation(lnCtr,"sTownName")); 
                System.out.println("sZippCode  :  " + model.getActLocation(lnCtr,"sZippCode")); 
                System.out.println("sProvIDxx  :  " + model.getActLocation(lnCtr,"sProvIDxx")); 
                System.out.println("sProvName  :  " + model.getActLocation(lnCtr,"sProvName")); 
            }
            
            System.out.println("--------------------------------------------------------------------");
            System.out.println("ACTIVITY MEMBER");
            System.out.println("--------------------------------------------------------------------");
            for(int lnCtr = 0;lnCtr <= model.getActMemberList().size()-1; lnCtr++){
                System.out.println("sTransNox  :  " + model.getActMember(lnCtr,"sTransNox")); 
                System.out.println("nEntryNox  :  " + model.getActMember(lnCtr,"nEntryNox")); 
                System.out.println("sEmployID  :  " + model.getActMember(lnCtr,"sEmployID")); 
                System.out.println("cOriginal  :  " + model.getActMember(lnCtr,"cOriginal")); 
                System.out.println("sEntryByx  :  " + model.getActMember(lnCtr,"sEntryByx")); 
                System.out.println("dEntryDte  :  " + model.getActMember(lnCtr,"dEntryDte")); 
                System.out.println("sCompnyNm  :  " + model.getActMember(lnCtr,"sCompnyNm")); 
                System.out.println("sDeptName  :  " + model.getActMember(lnCtr,"sDeptName")); 
            }
            
            System.out.println("--------------------------------------------------------------------");
            System.out.println("ACTIVITY VEHICLE");
            System.out.println("--------------------------------------------------------------------");
            for(int lnCtr = 0;lnCtr <= model.getActVehicleList().size()-1; lnCtr++){
                System.out.println("sTransNox  :  " + model.getActVehicle(lnCtr,"sTransNox")); 
                System.out.println("nEntryNox  :  " + model.getActVehicle(lnCtr,"nEntryNox")); 
                System.out.println("sSerialID  :  " + model.getActVehicle(lnCtr,"sSerialID")); 
                System.out.println("sDescript  :  " + model.getActVehicle(lnCtr,"sDescript")); 
                System.out.println("sCSNoxxxx  :  " + model.getActVehicle(lnCtr,"sCSNoxxxx")); 
            }
            
            result = true;
        }
        assertTrue(result);
//        assertFalse(result);
    }
    
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
//        json = model.setMaster("sColorDsc","COLOR");
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
//    
//    @Test
//    public void test04CancelTransaction(){
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------DEACTIVATE RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.cancelTransaction("M001CL000001");
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
    
//    @Test
//    public void test04ApproveTransaction(){
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------ACTIVATE RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.approveTransaction("M001CL000001");
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
    
    
    
//    public static void main(String [] args){
//        String path;
//        if(System.getProperty("os.name").toLowerCase().contains("win")){
//            path = "D:/GGC_Maven_Systems";
//        }
//        else{
//            path = "/srv/GGC_Maven_Systems";
//        }
//        System.setProperty("sys.default.path.config", path);
//
//        GRider instance = new GRider("gRider");
//        if (!instance.logUser("gRider", "M001000001")){
//            System.err.println(instance.getErrMsg());
//            System.exit(1);
//        }
//
//        System.out.println("Connected");
//        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/");
//        
//        JSONObject json;
//        
//        System.out.println("sBranch code = " + instance.getBranchCode());
//        Activity model = new Activity(instance, false, instance.getBranchCode());
//        
//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------NEW RECORD--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.newTransaction();
//        if (!"success".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        System.err.println("result = " + (String) json.get("result"));
//        
//        json = model.setMaster("sActTitle","Activity Title");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        json = model.setMaster("sActDescx","Activity Description");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//    
//        
//        json = model.setMaster("sActTypID","M001ACTP0001");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//    
//        
//        json = model.setMaster("sActSrcex","TEST");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//    
//        json = model.setMaster("sProvIDxx","01");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        String lsdate = "2024-08-24";
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Date date = null;
//        try {
//            date = sdf.parse(lsdate);
//        } catch (ParseException e) {
//            System.err.println("Error parsing date: " + e.getMessage());
//        }
//        
//        json = model.setMaster("dDateFrom",date);
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        lsdate = "2024-08-30";
//        sdf = new SimpleDateFormat("yyyy-MM-dd");
//        date = null;
//        try {
//            date = sdf.parse(lsdate);
//        } catch (ParseException e) {
//            System.err.println("Error parsing date: " + e.getMessage());
//        }
//        
//        json = model.setMaster("dDateThru", date);
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//    
//        json = model.setMaster("sLocation","V001");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//    
//        json = model.setMaster("sCompnynx","GEELY - PANGASINAN");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//    
//        json = model.setMaster("nTrgtClnt",50);
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//    
//        json = model.setMaster("sEmployID","M001000001");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//    
//        json = model.setMaster("sDeptIDxx","M001000001");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//    
//        json = model.setMaster("sLogRemrk","SAMPLE");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//    
//        json = model.setMaster("sRemarksx","TEST");
//        if ("error".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        }
//        
//        model.loadDepartment();
//        System.out.println("department size = " + model.getDepartmentList().size());
//        for(int lnctr = 0; lnctr < model.getDepartmentList().size(); lnctr++){
//            System.out.println("Department ID : " + model.getDepartmentID(lnctr, lnctr));
//            System.out.println("Department Nn : " + model.getDepartmentNm(lnctr, lnctr));
//        }
//        
//        model.loadEmployee("026");
//        System.out.println("employee size = " + model.getEmployeeList().size());
//        for(int lnctr = 0; lnctr < model.getEmployeeList().size(); lnctr++){
//            System.out.println("Employee ID : " + model.getEmployeeID(lnctr, lnctr));
//            System.out.println("Employee Nn : " + model.getEmployeeNm(lnctr, lnctr));
//        }
//        
//        model.loadVehicle();
//        System.out.println("vehicle size = " + model.getVehicleList().size());
//        for(int lnctr = 0; lnctr < model.getVehicleList().size(); lnctr++){
//            System.out.println("Vehicle Serial ID : " + model.getSerialID(lnctr, lnctr));
//            System.out.println("Vehicle Description  : " + model.getVehicleDesc(lnctr, lnctr));
//        }
//        
//        
//        
//        model.addActLocation();
//        System.out.println("town size = " + model.getActLocationList().size());
//        for(int lnctr = 0; lnctr < model.getActLocationList().size(); lnctr++){
//            model.setActLocation(lnctr, "sProvIDxx", "01");
//            model.setActLocation(lnctr, "sTownIDxx", "0335");
//            model.setActLocation(lnctr, "sAddressx", "MALASIQUI");
//        }
//        
//        model.addActMember();
//        System.out.println("member size = " + model.getActMemberList().size());
//        for(int lnctr = 0; lnctr < model.getActMemberList().size(); lnctr++){
//            model.setActMember(lnctr, "sEmployID", "M001000001");
//            model.setActMember(lnctr, "cOriginal", "1");
//        }
//        
//        model.addActVehicle();
//        System.out.println("vehicle size = " + model.getActVehicleList().size());
//        for(int lnctr = 0; lnctr < model.getActVehicleList().size(); lnctr++){
//            model.setActVehicle(lnctr, "sSerialID", "M001VS240001");
//        }

//        System.out.println("--------------------------------------------------------------------");
//        System.out.println("------------------------------NEW RECORD SAVING--------------------------------------");
//        System.out.println("--------------------------------------------------------------------");
//        
//        json = model.saveTransaction();
//        System.err.println((String) json.get("message"));
//        
//        if (!"success".equals((String) json.get("result"))){
//            System.err.println((String) json.get("message"));
//            System.exit(1);
//        } else {
//            System.out.println((String) json.get("message"));
//            System.exit(0);
//        }
//    }
    
    
}
