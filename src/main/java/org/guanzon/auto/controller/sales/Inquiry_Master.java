/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.sales;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.TransactionStatus;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.general.LockTransaction;
import org.guanzon.auto.general.SearchDialog;
import org.guanzon.auto.general.TransactionStatusHistory;
import org.guanzon.auto.model.sales.Model_Inquiry_Master;
import org.guanzon.auto.validator.sales.ValidatorFactory;
import org.guanzon.auto.validator.sales.ValidatorInterface;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Arsiela
 */
public class Inquiry_Master implements GTransaction {
    final String XML = "Model_Inquiry_Master.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    int pnEditMode;
    String psTransStat;
    
    String psMessagex;
    public JSONObject poJSON;
    CachedRowSet poTestModel;
    CachedRowSet poBankApp;
    CachedRowSet poFollowUp;
    
    Model_Inquiry_Master poModel;
    ArrayList<Model_Inquiry_Master> paDetail;
    
    LockTransaction poLockTrans;
    
    public Inquiry_Master(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;
        
        poModel = new Model_Inquiry_Master(foGRider);
        pnEditMode = EditMode.UNKNOWN;
    }

    @Override
    public int getEditMode() {
        return pnEditMode;
    }
    
    @Override
    public Model_Inquiry_Master getMasterModel() {
        return poModel;
    }

    @Override
    public JSONObject setMaster(int fnCol, Object foData) {
        JSONObject obj = new JSONObject();
        obj.put("pnEditMode", pnEditMode);
        if (pnEditMode != EditMode.UNKNOWN){
            // Don't allow specific fields to assign values
            if(!(fnCol == poModel.getColumn("sTransNox") ||
                fnCol == poModel.getColumn("cTranStat") ||
                fnCol == poModel.getColumn("sEntryByx") ||
                fnCol == poModel.getColumn("dEntryDte") ||
                fnCol == poModel.getColumn("sModified") ||
                fnCol == poModel.getColumn("dModified"))){
                poModel.setValue(fnCol, foData);
                obj.put(fnCol, pnEditMode);
            }
        }
        return obj;
    }

    @Override
    public JSONObject setMaster(String fsCol, Object foData) {
        return setMaster(poModel.getColumn(fsCol), foData);
    }
    
    public Object getMaster(int fnCol) {
        if(pnEditMode == EditMode.UNKNOWN)
            return null;
        else 
            return poModel.getValue(fnCol);
    }

    public Object getMaster(String fsCol) {
        return getMaster(poModel.getColumn(fsCol));
    }

    @Override
    public JSONObject newTransaction() {
        poJSON = new JSONObject();
        try{
            pnEditMode = EditMode.ADDNEW;
            org.json.simple.JSONObject obj;

            poModel = new Model_Inquiry_Master(poGRider);
            Connection loConn = null;
            loConn = setConnection();

            poModel.setTransNo(MiscUtil.getNextCode(poModel.getTable(), "sTransNox", true, poGRider.getConnection(), poGRider.getBranchCode()+"IQ"));
            poModel.setInqryID(MiscUtil.getNextCode(poModel.getTable(), "sInqryIDx", true, poGRider.getConnection(), poGRider.getBranchCode()));
            poModel.newRecord();
            
            if (poModel == null){
                poJSON.put("result", "error");
                poJSON.put("message", "initialized new record failed.");
                return poJSON;
            }else{
                poJSON.put("result", "success");
                poJSON.put("message", "initialized new record.");
                pnEditMode = EditMode.ADDNEW;
            }
        }catch(NullPointerException e){
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        
        return poJSON;
    }
    
    private Connection setConnection(){
        Connection foConn;
        if (pbWtParent){
            foConn = (Connection) poGRider.getConnection();
            if (foConn == null) foConn = (Connection) poGRider.doConnect();
        }else foConn = (Connection) poGRider.doConnect();
        return foConn;
    }

    @Override
    public JSONObject openTransaction(String fsValue) {
        pnEditMode = EditMode.READY;
        poJSON = new JSONObject();
        
        poModel = new Model_Inquiry_Master(poGRider);
        poJSON = poModel.openRecord(fsValue);
        
        return poJSON;
    }
    
    private JSONObject checkData(JSONObject joValue){
        if(pnEditMode == EditMode.READY || pnEditMode == EditMode.UPDATE){
            if(joValue.containsKey("continue")){
                if(true == (boolean)joValue.get("continue")){
                    joValue.put("result", "success");
                    joValue.put("message", "Record saved successfully.");
                }
            }
        }
        return joValue;
    }
    
    @Override
    public JSONObject updateTransaction() {
        poJSON = new JSONObject();
        if (pnEditMode != EditMode.READY && pnEditMode != EditMode.UPDATE){
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid edit mode.");
            return poJSON;
        }
        
        poLockTrans = new LockTransaction(poGRider);
        
        if(!poLockTrans.checkLockTransaction(poModel.getTable(), "sTransNox", poModel.getTransNo())){
            poJSON.put("result", "error");
            poJSON.put("message", poLockTrans.getMessage());
            return poJSON;
        } 
        
        poLockTrans.saveLockTransaction(poModel.getTable(),"sTransNox", poModel.getTransNo(), poModel.getBranchCd());
        
        pnEditMode = EditMode.UPDATE;
        poJSON.put("result", "success");
        poJSON.put("message", "Update mode success.");
        return poJSON;
    }

    @Override
    public JSONObject saveTransaction() {
        poJSON = new JSONObject();  
        
        ValidatorInterface validator = ValidatorFactory.make( ValidatorFactory.TYPE.Inquiry_Master, poModel);
        validator.setGRider(poGRider);
        if (!validator.isEntryOkay()){
            poJSON.put("result", "error");
            poJSON.put("message", validator.getMessage());
            return poJSON;
        }
        
        poJSON =  poModel.saveRecord();
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        return poJSON;
    }

    public JSONObject searchTransaction(String fsValue, boolean fbByCode) {
        String lsHeader = "Inquiry Date»Inquiry ID»Customer Name»Customer Address»Inquiry Status»Branch";
        String lsColName = "dTransact»sInqryIDx»sCompnyNm»sAddressx»sTranStat»sBranchNm";
        String lsSQL =    " SELECT "                                                                       
                        + "    a.sTransNox "                                                               
                        + "  , a.sInqryIDx "                                                                
                        + "  , a.sBranchCd "                                                               
                        + "  , DATE(a.dTransact) AS dTransact"                                                               
                        + "  , a.sEmployID "                                                               
                        + "  , a.sClientID "                                                                
                        + "  , a.sContctID "                                                               
                        + "  , a.sAgentIDx "                                                               
                        + "  , a.dTargetDt "                                                               
                        + "  , a.cTranStat "                                                               
                        + "  , CASE WHEN a.cTranStat = '0' THEN 'FOR FOLLOW-UP'"                           
                        + " 	WHEN a.cTranStat = '1' THEN 'ON PROCESS' "                                   
                        + " 	WHEN a.cTranStat = '2' THEN 'LOST SALE'  "                                   
                        + " 	WHEN a.cTranStat = '3' THEN 'WITH VSP'   "                                   
                        + " 	WHEN a.cTranStat = '4' THEN 'SOLD'       "                                     
                        + " 	ELSE 'CANCELLED'  "                                                          
                        + "    END AS sTranStat "                                                          
                        + "  , b.sCompnyNm      "                                                          
                        + "  , b.cClientTp      "                                                          
                        + "  , IFNULL(CONCAT( IFNULL(CONCAT(d.sHouseNox,' ') , ''), "                      
                        + " 	IFNULL(CONCAT(d.sAddressx,' ') , ''),  "                                     
                        + " 	IFNULL(CONCAT(e.sBrgyName,' '), ''),   "                                     
                        + " 	IFNULL(CONCAT(f.sTownName, ', '),''),  "                                     
                        + " 	IFNULL(CONCAT(g.sProvName),'') )	, '') AS sAddressx "                       
                        + "  , l.sCompnyNm AS sSalesExe "                                                  
                        + "  , m.sCompnyNm AS sSalesAgn "                                                  
                        + "  , p.sBranchNm  "                                                              
                        + " FROM customer_inquiry a "                                                      
                        + " LEFT JOIN client_master b ON a.sClientID = b.sClientID "                       
                        + " LEFT JOIN client_address c ON c.sClientID = a.sClientID AND c.cPrimaryx = 1  " 
                        + " LEFT JOIN addresses d ON d.sAddrssID = c.sAddrssID "                           
                        + " LEFT JOIN barangay e ON e.sBrgyIDxx = d.sBrgyIDxx  "                           
                        + " LEFT JOIN towncity f ON f.sTownIDxx = d.sTownIDxx  "                           
                        + " LEFT JOIN province g ON g.sProvIDxx = f.sProvIDxx   "                           
                        + " LEFT JOIN client_master k ON k.sClientID = a.sContctID  "                       
                        + " LEFT JOIN ggc_isysdbf.client_master l ON l.sClientID = a.sEmployID "            
                        + " LEFT JOIN client_master m ON m.sClientID = a.sAgentIDx "                        
                        + " LEFT JOIN branch p ON p.sBranchCd = a.sBranchCd "                        
                        + " WHERE a.cTranStat <> '6' " ;  
        
        System.out.println(lsSQL);
        JSONObject loJSON = SearchDialog.jsonSearch(
                    poGRider,
                    lsSQL,
                    "",
                    lsHeader,
                    lsColName,
                "0.1D»0.2D»0.3D»0.4D»0.2D»0.3D", 
                    "INQUIRY",
                    0);
            
        if (loJSON != null && !"error".equals((String) loJSON.get("result"))) {
        }else {
            loJSON = new JSONObject();
            loJSON.put("result", "error");
            loJSON.put("message", "No Transaction loaded.");
            return loJSON;
        }
        return loJSON;
    }

    @Override
    public JSONObject deleteTransaction(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject closeTransaction(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject postTransaction(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject voidTransaction(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject cancelTransaction(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject searchWithCondition(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject searchTransaction(String string, String string1, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject searchMaster(String string, String string1, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject searchMaster(int i, String string, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTransactionStatus(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public JSONObject lostSale(String fsValue) {
        JSONObject loJSON = new JSONObject();
        
        loJSON = poModel.lostSale(fsValue);
        if ("success".equals((String) loJSON.get("result"))) {
            loJSON.put("result", "success");
            loJSON.put("message", "Lost Sale success.");
        } else {
            loJSON.put("result", "error");
            loJSON.put("message", "Lost Sale failed.");
            }
        
        return loJSON;
    }
    
    public JSONObject searchSalesExecutive(String fsValue) {
        JSONObject loJSON = new JSONObject();
        
        String lsSQL =   " SELECT "
                       + " a.sClientID "
                       + " , a.cRecdStat "
                       + " , b.sCompnyNm "
                       + " FROM sales_executive a "
                       + " LEFT JOIN GGC_ISysDBF.Client_Master b ON b.sClientID = a.sClientID " ;  
                
        lsSQL = MiscUtil.addCondition(lsSQL, "a.cRecdStat = '1' " 
                                                + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getAgentID())
                                                + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getClientID())
                                                + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getContctID())
                                                + " AND b.sCompnyNm LIKE " + SQLUtil.toSQL(fsValue + "%"));  

        System.out.println("SEARCH SALES EXECUTIVE: " + lsSQL);
        loJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    "Employee Name",
                    "sCompnyNm",
                    "b.sCompnyNm",
                0);

        if (loJSON != null) {
            if(!"error".equals((String) loJSON.get("result"))){
                poModel.setEmployID((String) loJSON.get("sClientID"));
                poModel.setSalesExe((String) loJSON.get("sCompnyNm"));
            } else {
                poModel.setEmployID("");
                poModel.setSalesExe("");
            }
        } else {
            poModel.setEmployID("");
            poModel.setSalesExe("");
            loJSON = new JSONObject();
            loJSON.put("result", "error");
            loJSON.put("message", "No record loaded.");
            return loJSON;
        }
        
        return loJSON;
    }
    
    public JSONObject searchReferralAgent(String fsValue) {
        JSONObject loJSON = new JSONObject();
        
        if(!poModel.getSourceCD().equals("3")){
            loJSON.put("result", "error");
            loJSON.put("message", "Invalid Inquiry type.");
            return loJSON;
        }
        
        String lsSQL =   " SELECT "
                       + " a.sClientID "
                       + " , a.cRecdStat "
                       + " , b.sCompnyNm "
                       + " FROM sales_agent a "
                       + " LEFT JOIN client_master b ON b.sClientID = a.sClientID " ;  
                
        lsSQL = MiscUtil.addCondition(lsSQL, " a.cRecdStat = '1' "
                                                + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getContctID())
                                                + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getClientID())
                                                + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getEmployID())
                                                + " AND b.sCompnyNm LIKE " + SQLUtil.toSQL(fsValue + "%"));  

        System.out.println("SEARCH SALES AGENT: " + lsSQL);
        loJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    "Agent Name",
                    "sCompnyNm",
                    "b.sCompnyNm",
                0);

        if (loJSON != null) {
            if(!"error".equals((String) loJSON.get("result"))){
                poModel.setAgentID((String) loJSON.get("sClientID"));
                poModel.setSalesAgn((String) loJSON.get("sCompnyNm"));
            } else {
                poModel.setAgentID("");
                poModel.setSalesAgn("");
            }
        } else {
            poModel.setAgentID("");
            poModel.setSalesAgn("");
            loJSON = new JSONObject();
            loJSON.put("result", "error");
            loJSON.put("message", "No record loaded.");
            return loJSON;
        }
        
        return loJSON;
    }
    
    public JSONObject searchClient(String fsValue, boolean fbInqClient) {
        String lsHeader = "ID»Name»Address";
        String lsColName = "sClientID»sCompnyNm»sAddressx"; 
        String lsColCrit = "a.sClientID»a.sCompnyNm»TRIM(CONCAT(c.sHouseNox, ' ', c.sAddressx,' ', d.sBrgyName, ' ', e.sTownName, ', ', f.sProvName))";
        String lsSQL =    "  SELECT  "                                                                                                
                        + "  a.sClientID "                                                                                             
                        + " , a.sCompnyNm "                                                                                  
                        + " , CONCAT(c.sHouseNox, ' ', c.sAddressx,' ', d.sBrgyName, ' ', e.sTownName, ', ', f.sProvName) AS sAddressx " 
                        + " , a.sLastName "                                                                                            
                        + " , a.sFrstName "                                                                                            
                        + " , a.sMiddName "                                                                                            
                        + " , a.sSuffixNm "                                                                                            
                        + " , a.cClientTp "                                                                                            
                        + " , g.sMobileNo "                                                                                            
                        + " , IFNULL(h.sEmailAdd,'') AS sEmailAdd"                                                                                            
                        + " , IFNULL(GROUP_CONCAT(DISTINCT i.sAccountx),'') AS sAccountx "                                                        
                        + " FROM client_master a  "                                                                                    
                        + " LEFT JOIN client_address b ON b.sClientID = a.sClientID AND b.cPrimaryx = 1 "                              
                        + " LEFT JOIN addresses c ON c.sAddrssID = b.sAddrssID "                                                       
                        + " LEFT JOIN barangay d ON d.sBrgyIDxx = c.sBrgyIDxx  "                                                       
                        + " LEFT JOIN TownCity e ON e.sTownIDxx = c.sTownIDxx  "                                                       
                        + " LEFT JOIN Province f ON f.sProvIDxx = e.sProvIDxx  "                                                       
                        + " LEFT JOIN client_mobile g ON g.sClientID = a.sClientID AND g.cPrimaryx = 1  "                              
                        + " LEFT JOIN client_email_address h ON h.sClientID = a.sClientID AND h.cPrimaryx = 1 "                        
                        + " LEFT JOIN client_social_media i ON i.sClientID = a.sClientID AND i.cRecdStat = 1  "  ;
        
        if(fbInqClient){
            lsSQL = MiscUtil.addCondition(lsSQL, " a.cRecdStat = '1' "
                                                    + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getContctID())
                                                    + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getAgentID())
                                                    + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getEmployID())
                                                    + " AND a.sCompnyNm LIKE " + SQLUtil.toSQL(fsValue + "%")) ;
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL, " a.cRecdStat = '1' AND a.cClientTp = '0' "
                                                    + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getClientID())
                                                    + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getAgentID())
                                                    + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getEmployID())
                                                    + " AND a.sCompnyNm LIKE " + SQLUtil.toSQL(fsValue + "%")) ;
        }
        
        lsSQL = lsSQL + " GROUP BY a.sClientID ";
        JSONObject loJSON;
        System.out.println(lsSQL);
        loJSON = ShowDialogFX.Search(poGRider, 
                                        lsSQL, 
                                        fsValue, 
                                        lsHeader, 
                                        lsColName, 
                                        lsColCrit, 
                                        1);
        
        if (loJSON != null) {
            if(!"error".equals((String) loJSON.get("result"))){
                if(fbInqClient){
                    poModel.setClientID((String) loJSON.get("sClientID"));
                    poModel.setClientNm((String) loJSON.get("sCompnyNm"));
                    poModel.setClientTp((String) loJSON.get("cClientTp"));
                    poModel.setMobileNo((String) loJSON.get("sMobileNo"));
                    poModel.setEmailAdd((String) loJSON.get("sEmailAdd"));
                    poModel.setAccount((String) loJSON.get("sAccountx"));
                    poModel.setAddress(((String) loJSON.get("sAddressx")).trim());
                } else {
                    poModel.setContctID((String) loJSON.get("sClientID"));
                    poModel.setContctNm((String) loJSON.get("sCompnyNm"));
                } 
            } else {
                if(fbInqClient){
                    poModel.setClientID("");
                    poModel.setClientNm("");
                    poModel.setClientTp("");
                    poModel.setMobileNo("");
                    poModel.setEmailAdd("");
                    poModel.setAccount("");
                    poModel.setAddress("");
                } else {
                    poModel.setContctID("");
                    poModel.setContctNm("");
                }
            }
        }else {
            if(fbInqClient){
                poModel.setClientID("");
                poModel.setClientNm("");
                poModel.setClientTp("");
                poModel.setMobileNo("");
                poModel.setEmailAdd("");
                poModel.setAccount("");
                poModel.setAddress("");
            } else {
                poModel.setContctID("");
                poModel.setContctNm("");
            }
            loJSON  = new JSONObject();  
            loJSON.put("result", "error");
            loJSON.put("message", "No information found");
            return loJSON;
        }
        return loJSON;
    }
    
    public JSONObject checkExistingTransaction(boolean fbisClient){
        JSONObject loJSON = new JSONObject();
        
        try {
            String lsTransNo = "";
            String lsID = "";
            String lsDesc = "";
            String lsStat = "";
            String lsEmpl = "";
            String lsSQL = "";
            String lsWhere = "";
            String lsInqdate = "1900-01-01";
            String lsInqRsv = "";
            String lsEmpID = "";
            ResultSet loRS;
            
            if(poModel.getClientID() == null){
                return loJSON;
            }
            
            if(poModel.getEmployID()!= null){
                if(!poModel.getEmployID().trim().isEmpty()){
                    lsEmpID = poModel.getEmployID();
                }
            }
            
            if(getEditMode() == EditMode.ADDNEW){
                lsSQL =   " SELECT "                                                                       
                        + "    a.sTransNox "                                                               
                        + "  , a.sBranchCd "                                                               
                        + "  , a.dTransact "                                                               
                        + "  , a.sInqryIDx "                                                               
                        + "  , a.sEmployID "                                                               
                        + "  , a.sClientID "                                                               
                        + "  , a.sContctID "                                                               
                        + "  , a.sAgentIDx "                                                               
                        + "  , a.dTargetDt "                                                               
                        + "  , a.cTranStat "                                                               
                        + "  , CASE "
                        + "     WHEN a.cTranStat = '0' THEN 'FOR FOLLOW-UP'"                           
                        + " 	WHEN a.cTranStat = '1' THEN 'ON PROCESS' "                                   
                        + " 	WHEN a.cTranStat = '2' THEN 'LOST SALE'  "                                   
                        + " 	WHEN a.cTranStat = '3' THEN 'WITH VSP'   "                                   
                        + " 	WHEN a.cTranStat = '4' THEN 'SOLD'       "                                     
                        + " 	ELSE 'CANCELLED'  "                                                          
                        + "    END AS sTranStat "                                                          
                        + "  , b.sCompnyNm      "                                                          
                        + "  , b.cClientTp      "                                                          
                        + "  , IFNULL(CONCAT( IFNULL(CONCAT(d.sHouseNox,' ') , ''), "                      
                        + " 	IFNULL(CONCAT(d.sAddressx,' ') , ''),  "                                     
                        + " 	IFNULL(CONCAT(e.sBrgyName,' '), ''),   "                                     
                        + " 	IFNULL(CONCAT(f.sTownName, ', '),''),  "                                     
                        + " 	IFNULL(CONCAT(g.sProvName),'') )	, '') AS sAddressx "                       
                        + "  , l.sCompnyNm AS sSalesExe "                                                  
                        + "  , m.sCompnyNm AS sSalesAgn "                                                  
                        + "  , p.sBranchNm  "                                                              
                        + " FROM customer_inquiry a "                                                      
                        + " LEFT JOIN client_master b ON a.sClientID = b.sClientID "                       
                        + " LEFT JOIN client_address c ON c.sClientID = a.sClientID AND c.cPrimaryx = 1  " 
                        + " LEFT JOIN addresses d ON d.sAddrssID = c.sAddrssID "                           
                        + " LEFT JOIN barangay e ON e.sBrgyIDxx = d.sBrgyIDxx  "                           
                        + " LEFT JOIN towncity f ON f.sTownIDxx = d.sTownIDxx  "                           
                        + " LEFT JOIN province g ON g.sProvIDxx = f.sProvIDxx   "                           
                        + " LEFT JOIN client_master k ON k.sClientID = a.sContctID  "                       
                        + " LEFT JOIN ggc_isysdbf.client_master l ON l.sClientID = a.sEmployID "            
                        + " LEFT JOIN client_master m ON m.sClientID = a.sAgentIDx "                        
                        + " LEFT JOIN branch p ON p.sBranchCd = a.sBranchCd "                        
                        + " WHERE a.cTranStat <> '5' " ;
                
                //Check for Client existing Inquiry
                if(fbisClient){
                    lsWhere = MiscUtil.addCondition(lsSQL, " a.sClientID = " + SQLUtil.toSQL(poModel.getClientID())
                                                            + " AND a.sTransNox <> " + SQLUtil.toSQL(poModel.getTransNo()) 
                                                            + " AND (a.cTranStat = '0' OR a.cTranStat = '1' OR a.cTranStat = '3')"  
                                                            );

                    System.out.println("EXISTING CUSTOMER CHECK: " + lsSQL);
                    loRS = poGRider.executeQuery(lsWhere);
                    if (MiscUtil.RecordCount(loRS) > 0){
                        while(loRS.next()){
                            lsID = loRS.getString("sInqryIDx");
                            lsTransNo = loRS.getString("sTransNox");
                            lsInqdate = xsDateShort(loRS.getDate("dTransact"));
                            lsStat = loRS.getString("sTranStat");
                        }
                        MiscUtil.close(loRS);

                        loJSON.put("result", "error");
                        loJSON.put("sTransNox",lsTransNo);
                        loJSON.put("message", "An existing inquiry with the same customer."
                                                + "\n\n<Inquiry ID : " + lsID + ">"
                                                + "\n<Inquiry Date: " + lsInqdate + ">"
                                                + "\n<Inquiry Status: " + lsStat + ">"
                                                + "\n\nDo you want to open Record?");
                        return loJSON;

                    } 
                } else {
                    if(!lsEmpID.trim().isEmpty()){
                        /* 1.
                        * Will not allow SE to create new inquiry for client with an existing for-followup, on process and with vsp inquiry with the same SE.
                        */
                        lsWhere = MiscUtil.addCondition(lsSQL, " a.sClientID = " + SQLUtil.toSQL(poModel.getClientID())
                                                                + " AND a.sEmployID = " + SQLUtil.toSQL(lsEmpID) 
                                                                + " AND a.sTransNox <> " + SQLUtil.toSQL(poModel.getTransNo()) 
                                                                + " AND (a.cTranStat = '0' OR a.cTranStat = '1' OR a.cTranStat = '3')"  
                                                                );

                        System.out.println("EXISTING CUSTOMER WITH THE SAME SE CHECK: " + lsSQL);
                        loRS = poGRider.executeQuery(lsWhere);
                        //Check for existing inquiry with same SE
                        if (MiscUtil.RecordCount(loRS) > 0){
                            while(loRS.next()){
                                lsID = loRS.getString("sInqryIDx");
                                lsTransNo = loRS.getString("sTransNox");
                                lsInqdate = xsDateShort(loRS.getDate("dTransact"));
                                lsStat = loRS.getString("sTranStat");
                            }
                            MiscUtil.close(loRS);

                            loJSON.put("result", "error");
                            loJSON.put("sTransNox",lsTransNo);
                            loJSON.put("message", "An existing inquiry with the same customer."
                                                    + "\nPlease update this one instead of creating a new inquiry record."
                                                    + "\n\n<Inquiry ID : " + lsID + ">"
                                                    + "\n<Inquiry Date: " + lsInqdate + ">"
                                                    + "\n<Inquiry Status: " + lsStat + ">"
                                                    + "\n\nDo you want to open Record?");
                            return loJSON;

                        } 

                        // 2.
                        //if same cust code is to be used by another SE, validate the status of the existing inquiry (check reservations, etc). 
                        //Allow to be re-used if status is for follow up and Lost/Cancelled  only
                        lsWhere = MiscUtil.addCondition(lsSQL, " a.sClientID = " + SQLUtil.toSQL(poModel.getClientID()) 
                                                            + " AND a.sEmployID <> " + SQLUtil.toSQL(lsEmpID) 
                                                            + " AND a.sTransNox <> " + SQLUtil.toSQL(poModel.getTransNo()) 
                                                            + " AND (a.cTranStat = '1' OR a.cTranStat = '3')" 
                                                            );

                        System.out.println("EXISTING INQUIRY ON PROCESS AND WITH VSP WITH SAME CUSTOMER CHECK: " + lsWhere);
                        loRS = poGRider.executeQuery(lsWhere);
                        //Check for existing inquiry with same SE
                        if (MiscUtil.RecordCount(loRS) > 0){
                            while(loRS.next()){
                                lsID = loRS.getString("sInqryIDx");
                                lsTransNo =  loRS.getString("sTransNox");
                                lsInqdate = xsDateShort(loRS.getDate("dTransact"));
                                lsDesc = loRS.getString("sCompnyNm");
                                lsEmpl = loRS.getString("sSalesExe");
                                lsStat = loRS.getString("sTranStat");
                            }
                            MiscUtil.close(loRS);

                            //Check for existing reservation
                            lsInqRsv = " SELECT "
                                    + "   sTransNox "
                                    + " , dTransact "
                                    + " , sReferNox "
                                    + " , sClientID "
                                    + " , nAmountxx "
                                    + " , sSourceNo "
                                    + " FROM customer_inquiry_reservation"
                                    + " WHERE cTranStat = '2' "
                                    + " AND sSourceNo = " + SQLUtil.toSQL(lsTransNo) ;
                            System.out.println("EXISTING INQUIRY RESERVATION CHECK: " + lsInqRsv);
                            loRS = poGRider.executeQuery(lsInqRsv);
                            if (MiscUtil.RecordCount(loRS) > 0){
                                MiscUtil.close(loRS);

                                loJSON.put("result", "error");
                                loJSON.put("sTransNox",lsTransNo);
                                loJSON.put("message", "Found an existing inquiry record for " + lsDesc.toUpperCase() + " with vehicle reservation."
                                                        + "\n\n<Sales Executive: " + lsEmpl.toUpperCase() + ">"
                                                        + "\n<Inquiry ID: " + lsID + ">"
                                                        + "\n<Inquiry Date: " + lsInqdate + ">"
                                                        + "\n<Inquiry Status: " + lsStat + ">"
                                                        + "\n\nDo you want to open Record?");
                                return loJSON;
                            }

                            loJSON.put("result", "error");
                            loJSON.put("sTransNox",lsTransNo);
                            loJSON.put("message", "Found an existing inquiry record for " + lsDesc.toUpperCase() 
                                                    + "\n\n<Sales Executive: " + lsEmpl.toUpperCase() + ">"
                                                    + "\n<Inquiry ID: " + lsID + ">"
                                                    + "\n<Inquiry Date: " + lsInqdate + ">"
                                                    + "\n<Inquiry Status: " + lsStat + ">"
                                                    + "\n\nDo you want to open Record?");
                            return loJSON;
                        } 
                    }
                }
            }
        
        } catch (SQLException ex) {
            Logger.getLogger(Inquiry_Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return loJSON;
    }
    
    public JSONObject searchOnlinePlatform(String fsValue) {
        JSONObject loJSON = new JSONObject();
        
        if(!poModel.getSourceCD().equals("1")){
            loJSON.put("result", "error");
            loJSON.put("message", "Invalid Inquiry type.");
            return loJSON;
        }
        
        String lsSQL =  "  SELECT " 
                       + "   sTransNox " 
                       + " , sPlatform " 
                       + " , sWebSitex " 
                       + " FROM online_platforms " ;  
                
        lsSQL = MiscUtil.addCondition(lsSQL, "sPlatform LIKE " + SQLUtil.toSQL(fsValue + "%"));  

        System.out.println("SEARCH ONLINE PLATFORMS: " + lsSQL);
        loJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    "Platfrom",
                    "sPlatform",
                    "sPlatform",
                0);

        if (loJSON != null) {
            if(!"error".equals((String) loJSON.get("result"))){
                poModel.setSourceNo((String) loJSON.get("sTransNox"));
                poModel.setPlatform((String) loJSON.get("sPlatform"));
            } else {
                poModel.setSourceNo("");
                poModel.setPlatform("");
            }
        } else {
            poModel.setSourceNo("");
            poModel.setPlatform("");
            loJSON = new JSONObject();
            loJSON.put("result", "error");
            loJSON.put("message", "No record loaded.");
            return loJSON;
        }
        
        return loJSON;
    }
    
    public JSONObject searchActivity(String fsValue) {
        JSONObject loJSON = new JSONObject();
        String lsHeader = "Actvitiy Date From»Actvitiy Date To»Activity ID»Activity No»Activity Title";
        String lsColName = "dDateFrom»dDateThru»sActvtyID»sActNoxxx»sActTitle"; 
        String lsCriteria = "dDateFrom»dDateThru»sActvtyID»sActNoxxx»sActTitle";
        
        String lsEventType = "";
        
        switch(poModel.getSourceCD()){
            case "4":
                lsEventType = "sal";
                break;
            case "5":
                lsEventType = "eve";
                break;
        }
        
        if(lsEventType.trim().isEmpty()){
            loJSON.put("result", "error");
            loJSON.put("message", "Invalid Inquiry type.");
            return loJSON;
        }
        
        String lsSQL =    " SELECT "                                                                   
                        + "   a.sActvtyID "                                                            
                        + " , a.sActNoxxx "                                                            
                        + " , a.sActTitle "                                                           
                        + " , a.sActTypID "                                                           
                        + " , a.dDateFrom "                                                            
                        + " , a.dDateThru "                                                           
                        + " , a.cTranStat "                                                             
                        + " , b.sEventTyp "                                                            
                        + " FROM activity_master a"                                                       
                        + " LEFT JOIN event_type b ON b.sActTypID = a.sActTypID "   ;  
                
        lsSQL = MiscUtil.addCondition(lsSQL, " a.cTranStat = '3' AND a.sActTitle LIKE " + SQLUtil.toSQL(fsValue + "%")
                                                + " AND b.sEventTyp =" + SQLUtil.toSQL(lsEventType)
                                                + " AND a.dDateFrom <=" + SQLUtil.toSQL(xsDateShort(poModel.getTransactDte()))
                                                + " AND a.dDateThru >=" + SQLUtil.toSQL(xsDateShort(poModel.getTransactDte())));  

        System.out.println("SEARCH ACTIVITY: " + lsSQL);
        loJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    lsHeader,
                    lsColName,
                    lsCriteria,
                0);

        if (loJSON != null) {
            if(!"error".equals((String) loJSON.get("result"))){
                poModel.setActvtyID((String) loJSON.get("sActvtyID"));
                poModel.setActTitle((String) loJSON.get("sActTitle"));
            } else {
                poModel.setActvtyID("");
                poModel.setActTitle("");
            }
        } else {
            poModel.setActvtyID("");
            poModel.setActTitle("");
            loJSON = new JSONObject();
            loJSON.put("result", "error");
            loJSON.put("message", "No record loaded.");
            return loJSON;
        }
        
        return loJSON;
    }
    
    public static String xsDateShort(Date fdValue) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(fdValue);
        return date;
    }

    public static String xsDateShort(String fsValue) throws ParseException, java.text.ParseException {
        SimpleDateFormat fromUser = new SimpleDateFormat("MMMM dd, yyyy");
        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");
        String lsResult = "";
        lsResult = myFormat.format(fromUser.parse(fsValue));
        return lsResult;
    }
    
    /*Convert Date to String*/
    private LocalDate strToDate(String val) {
        DateTimeFormatter date_formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(val, date_formatter);
        return localDate;
    }
    
    public JSONObject searchBranch(String fsValue) {
        JSONObject loJSON = new JSONObject();
        
        String lsSQL =   " SELECT "
                + " IFNULL(a.sBranchCd, '') sBranchCd "
                + " , IFNULL(a.sBranchNm, '') sBranchNm "
                + " , IFNULL(b.cDivision, '') cDivision "
                + " FROM branch a "
                + " LEFT JOIN branch_others b ON a.sBranchCd = b.sBranchCd  "
                + " WHERE a.cRecdStat = '1'  "
                + " AND b.cDivision = (SELECT cDivision FROM branch_others WHERE sBranchCd = " + SQLUtil.toSQL(psBranchCd) + ")"
                + " AND sBranchNm LIKE " + SQLUtil.toSQL(fsValue + "%");

        System.out.println("SEARCH BRANCH: " + lsSQL);
        loJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    "Branch Code»Branch Name",
                    "sBranchCd»sBranchNm",
                    "sBranchCd»sBranchNm",
                1);

        if (loJSON != null) {
            if(!"error".equals((String) loJSON.get("result"))){
                poModel.setBranchCd((String) loJSON.get("sBranchCd"));
                poModel.setBranchNm((String) loJSON.get("sBranchNm"));
            } else {
                poModel.setBranchCd("");
                poModel.setBranchNm("");
            }
        } else {
            poModel.setBranchCd("");
            poModel.setBranchNm("");
            loJSON = new JSONObject();
            loJSON.put("result", "error");
            loJSON.put("message", "No record loaded.");
            return loJSON;
        }
        
        return loJSON;
    }
    
    public JSONObject searchAvlVhcl(String fsValue) {
        JSONObject loJSON = new JSONObject();
        
        if(poModel.getIsVhclNw() == null){
            loJSON.put("result", "error");
            loJSON.put("message", "Vehicle Category is not set.");
        } else {
            if(poModel.getIsVhclNw().trim().isEmpty()){
                loJSON.put("result", "error");
                loJSON.put("message", "Vehicle Category is not set.");
            }
        }
        
        String lsHeader = "Vehicle Serial ID»CS No.»Plate No.»Engine No»Frame No»Vehicle Description";
        String lsColName = "sSerialID»sCSNoxxxx»sPlateNox»sEngineNo»sFrameNox»sDescript"; 
        String lsColCrit = "a.sSerialID»a.sCSNoxxxx»b.sPlateNox»a.sEngineNo»a.sFrameNox»c.sDescript";
        
        String lsSQL =    " SELECT "                                                               
                        + "    a.sSerialID "                                                       
                        + "  , a.sFrameNox "                                                       
                        + "  , a.sEngineNo "                                                       
                        + "  , a.sCSNoxxxx "                                                        
                        + "  , a.cVhclNewx "                                                        
                        + "  , a.cSoldStat "                                                      
                        + "  , b.sPlateNox "                                                       
                        + "  , c.sDescript "                                                       
                        + " FROM vehicle_serial a "                                                
                        + " LEFT JOIN vehicle_serial_registration b ON b.sSerialID = a.sSerialID " 
                        + " LEFT JOIN vehicle_master c ON c.sVhclIDxx = a.sVhclIDxx"
                        + " WHERE a.cSoldStat <> '0' "
                        + " AND a.cVhclNewx = " + SQLUtil.toSQL(poModel.getIsVhclNw())  //(TRIM(a.sClientID) = '' OR a.sClientID = NULL) AND 
                        + " AND ( a.sCSNoxxxx LIKE " + SQLUtil.toSQL(fsValue + "%") 
                        + " OR b.sPlateNox LIKE " + SQLUtil.toSQL(fsValue + "%") + " ) " ;
//        if(fbIsBrandNew){
//            lsSQL = lsSQL + " AND a.cVhclNewx = '0' " ;
//        } else {
//            lsSQL = lsSQL + " AND a.cVhclNewx = '1' ";
//        }
        
//        lsSQL = lsSQL +  " AND ( a.sCSNoxxxx LIKE " + SQLUtil.toSQL(fsValue + "%") 
//                      + " OR b.sPlateNox LIKE " + SQLUtil.toSQL(fsValue + "%") + " ) " ;
        
        System.out.println("SEARCH AVAILABLE VEHICLE: " + lsSQL);
        loJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    lsHeader,
                    lsColName,
                    lsColCrit,
                     1);

        if (loJSON != null) {
//            if(!"error".equals((String) loJSON.get("result"))){
//                poModel.setSerialID((String) loJSON.get("sSerialID"));
//                poModel.setFrameNo((String) loJSON.get("sFrameNox"));
//                poModel.setEngineNo((String) loJSON.get("sEngineNo"));
//                poModel.setCSNo((String) loJSON.get("sCSNoxxxx"));
//                poModel.setPlateNo((String) loJSON.get("sPlateNox"));
//                poModel.setDescript((String) loJSON.get("sDescript"));
//            } else {
//                poModel.setSerialID("");
//                poModel.setFrameNo("");
//                poModel.setEngineNo("");
//                poModel.setCSNo("");
//                poModel.setPlateNo("");
//                poModel.setDescript("");
//            }
        } else {
//            poModel.setSerialID("");
//            poModel.setFrameNo("");
//            poModel.setEngineNo("");
//            poModel.setCSNo("");
//            poModel.setPlateNo("");
//            poModel.setDescript("");
            loJSON = new JSONObject();
            loJSON.put("result", "error");
            loJSON.put("message", "No record loaded.");
            return loJSON;
        }
        
        return loJSON;
    }
    
    public JSONObject checkVhclAvailability(String fsValue){
        JSONObject loJSON = new JSONObject();
        try {
            //TODO
            //1. CHECK FOR: DR EXIST
            String lsCSPlateNo = "";
            String lsID = "";
            String lsDesc = "";
            String lsSQL =    " SELECT "                                                              
                            + "   a.sTransNox "                                                       
                            + " , a.dTransact "                                                       
                            + " , a.sClientID "                                                       
                            + " , a.sSerialID "                                                       
                            + " , a.sReferNox "                                                       
                            + " , b.sCompnyNm "                                                       
                            + " , c.sCSNoxxxx "                                                       
                            + " , c.sFrameNox "                                                       
                            + " , c.sEngineNo "                                                       
                            + " , d.sPlateNox "                                                       
                            + " FROM udr_master a "                                                   
                            + " LEFT JOIN client_master b ON b.sClientID = a.sClientID  "             
                            + " LEFT JOIN vehicle_serial c ON c.sSerialID = a.sSerialID "             
                            + " LEFT JOIN vehicle_serial_registration d ON d.sSerialID = a.sSerialID "
                            + " WHERE a.cTranStat <> '0' "
                            + " AND a.sSerialID = " + SQLUtil.toSQL(fsValue);
            System.out.println("CHECK FOR: DR EXIST: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);
            if (MiscUtil.RecordCount(loRS) > 0){
                while(loRS.next()){
                    lsDesc = loRS.getString("sCompnyNm");
                    lsID = loRS.getString("sReferNox");

                    if(loRS.getString("sPlateNox") != null){
                        lsCSPlateNo = loRS.getString("sPlateNox");
                    } else {
                        lsCSPlateNo = loRS.getString("sCSNoxxxx");
                    }

                }
                loJSON.put("result", "error");
                loJSON.put("message", "Plate No./CS No. " + lsCSPlateNo + " has been SOLD already. See Unit Delivery Receipt No. " + lsID + ".");
                return loJSON;
            }
            
            //2. CHECK FOR: VSP EXIST
            lsCSPlateNo = "";
            lsID = "";
            lsDesc = "";
            lsSQL =    " SELECT "                                                              
                        + "   a.sTransNox "                                                       
                        + " , a.dTransact "                                                       
                        + " , a.sClientID "                                                       
                        + " , a.sSerialID "                                                       
                        + " , a.sVSPNOxxx "                                                         
                        + " , a.cIsVhclNw "                                                      
                        + " , b.sCompnyNm "                                                       
                        + " , c.sCSNoxxxx "                                                       
                        + " , c.sFrameNox "                                                       
                        + " , c.sEngineNo "                                                       
                        + " , d.sPlateNox "                                                      
                        + " FROM vsp_master a "                                                   
                        + " LEFT JOIN client_master b ON b.sClientID = a.sClientID  "             
                        + " LEFT JOIN vehicle_serial c ON c.sSerialID = a.sSerialID "             
                        + " LEFT JOIN vehicle_serial_registration d ON d.sSerialID = a.sSerialID "
                        + " WHERE a.cTranStat <> '0' "
                        + " AND a.sSerialID = " + SQLUtil.toSQL(fsValue)
                        + " AND a.cIsVhclNw = " + SQLUtil.toSQL(poModel.getIsVhclNw()) ;
            System.out.println("CHECK FOR: VSP EXIST: " + lsSQL);
            loRS = poGRider.executeQuery(lsSQL);
            if (MiscUtil.RecordCount(loRS) > 0){
                while(loRS.next()){
                    lsDesc = loRS.getString("sCompnyNm");
                    lsID = loRS.getString("sVSPNOxxx");

                    if(loRS.getString("sPlateNox") != null){
                        lsCSPlateNo = loRS.getString("sPlateNox");
                    } else {
                        lsCSPlateNo = loRS.getString("sCSNoxxxx");
                    }

                }
                loJSON.put("result", "error");
                loJSON.put("message", "Plate No./CS No. " + lsCSPlateNo + " has been SOLD already. SEE VSP No. " + lsID + ".");
                return loJSON;
            }
            
            //3. CHECK FOR: RESERVE UNIT
            lsCSPlateNo = "";
            lsID = "";
            lsDesc = "";
            lsSQL =    " SELECT "                                                              
                        + "   a.sTransNox "                                                       
                        + " , a.dTransact "                                                       
                        + " , a.sClientID "                                                       
                        + " , a.sSerialID "                                                       
                        + " , a.sInqryIDx "                                                         
                        + " , a.cIsVhclNw "                                                      
                        + " , b.sCompnyNm "                                                       
                        + " , c.sCSNoxxxx "                                                       
                        + " , c.sFrameNox "                                                       
                        + " , c.sEngineNo "                                                       
                        + " , d.sPlateNox "                                                      
                        + " FROM customer_inquiry a "                                                   
                        + " LEFT JOIN client_master b ON b.sClientID = a.sClientID  "             
                        + " LEFT JOIN vehicle_serial c ON c.sSerialID = a.sSerialID "             
                        + " LEFT JOIN vehicle_serial_registration d ON d.sSerialID = a.sSerialID "
                        + " WHERE a.sSerialID = " + SQLUtil.toSQL(fsValue)
                        + " AND a.cIsVhclNw = " + SQLUtil.toSQL(poModel.getIsVhclNw())
                        + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getClientID()) 
                        + " AND a.cTranStat <> '5' AND a.cTranStat <> '2' AND a.cTranStat <> '4' " ;
            System.out.println("CHECK FOR: RESERVATION EXIST: " + lsSQL);
            loRS = poGRider.executeQuery(lsSQL);
            if (MiscUtil.RecordCount(loRS) > 0){
                while(loRS.next()){
                    lsDesc = loRS.getString("sCompnyNm");

                    if(loRS.getString("sPlateNox") != null){
                        lsCSPlateNo = loRS.getString("sPlateNox");
                    } else {
                        lsCSPlateNo = loRS.getString("sCSNoxxxx");
                    }

                }
                loJSON.put("result", "error");
                loJSON.put("message",  "Plate No./CS No. " + lsCSPlateNo+ " is already RESERVED for Customer " + lsDesc + ".");
                return loJSON;
            }
            
            //4. CHECK FOR: VEHICLE CUSTOMER OWNERSHIP
            lsCSPlateNo = "";
            lsID = "";
            lsDesc = "";
            lsSQL =       " SELECT "                                                              
                        + "   a.sClientID "                                                      
                        + " , a.sCompnyNm "                                                        
                        + " , b.sSerialID "                                                       
                        + " , b.sCSNoxxxx "                                                       
                        + " , b.sFrameNox "                                                       
                        + " , b.sEngineNo "                                                       
                        + " , c.sPlateNox "                                                       
                        + " FROM client_master a "                                               
                        + " LEFT JOIN vehicle_serial b ON b.sClientID = a.sClientID "              
                        + " LEFT JOIN vehicle_serial_registration c ON c.sSerialID = b.sSerialID "
                        + " WHERE b.sSerialID = " + SQLUtil.toSQL(fsValue)
                        + " AND b.sClientID <> " + SQLUtil.toSQL(poModel.getClientID()) ;
            System.out.println("CHECK FOR: VEHICLE CUSTOMER OWNERSHIP: " + lsSQL);
            loRS = poGRider.executeQuery(lsSQL);
            if (MiscUtil.RecordCount(loRS) > 0){
                while(loRS.next()){
                    lsDesc = loRS.getString("sCompnyNm");

                    if(loRS.getString("sPlateNox") != null){
                        lsCSPlateNo = loRS.getString("sPlateNox");
                    } else {
                        lsCSPlateNo = loRS.getString("sCSNoxxxx");
                    }

                }
                loJSON.put("result", "error");
                loJSON.put("message",  "Plate No./CS No. " + lsCSPlateNo+" has been named after " + lsDesc + ". Please verify.");
                return loJSON;
            }
            
            //5. CHECK FOR: AVAILABILITY BY VEHICLE STATUS
//            lsCSPlateNo = "";
//            lsID = "";
//            lsDesc = "";
//            lsSQL =   " SELECT "                                                              
//                    + "   a.sClientID "                                                       
//                    + " , a.sSerialID "                                                       
//                    + " , a.sCSNoxxxx "                                                       
//                    + " , a.sFrameNox "                                                       
//                    + " , a.sEngineNo "                                                        
//                    + " , a.cSoldStat "                                                      
//                    + " , b.sPlateNox "                                                       
//                    + " FROM vehicle_serial a "                                               
//                    + " LEFT JOIN vehicle_serial_registration b ON b.sSerialID = a.sSerialID "
//                    + " WHERE a.sSerialID = " + SQLUtil.toSQL(fsValue);
//            System.out.println("CHECK FOR: AVAILABILITY BY VEHICLE STATUS: " + lsSQL);
//            loRS = poGRider.executeQuery(lsSQL);
//            if (MiscUtil.RecordCount(loRS) > 0){
//                while(loRS.next()){
//                    lsID = loRS.getString("cSoldStat");
//                    
//                    if(loRS.getString("sPlateNox") != null){
//                        lsCSPlateNo = loRS.getString("sPlateNox");
//                    } else {
//                        lsCSPlateNo = loRS.getString("sCSNoxxxx");
//                    }
//
//                }
//                loJSON.put("result", "error");
//                loJSON.put("message",  "Plate No./CS No. " + lsCSPlateNo+" has been named after " + lsDesc + ". Please verify.");
//                return loJSON;
//            }
        
        } catch (SQLException ex) {
            Logger.getLogger(Inquiry_Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return loJSON;
    }
//    public JSONObject loadTestModel() {
//        poJSON = new JSONObject();
//        
//        String lsSQL =    " SELECT "             
//                        + "   sVhclIDxx "        
//                        + " , sDescript "        
//                        + " , cRecdStat "        
//                        + " FROM vehicle_master " ;
//        lsSQL = MiscUtil.addCondition(lsSQL, " cRecdStat = '1' AND sDescript LIKE " + SQLUtil.toSQL(fsValue + "%"));
//        System.out.println("SEARCH TEST DRIVE: " + lsSQL);
//        poJSON = ShowDialogFX.Search(poGRider,
//                lsSQL,
//                fsValue,
//                    "ID»Description",
//                    "sVhclIDxx»sDescript",
//                    "sVhclIDxx»sDescript",
//                1);
//
//        if (poJSON != null) {
//            if(!"error".equals((String) poJSON.get("result"))){
//                poModel.setVhclID((String) poJSON.get("sVhclIDxx"));
//                poModel.setTestModl((String) poJSON.get("sDescript"));
//            } else {
//                poModel.setVhclID("");
//                poModel.setTestModl("");
//            }
//        } else {
//            poModel.setVhclID("");
//            poModel.setTestModl("");
//            poJSON = new JSONObject();
//            poJSON.put("result", "error");
//            poJSON.put("message", "No record loaded.");
//            return poJSON;
//        }
//        
//        return poJSON;
//    }
    
    public JSONObject loadTestModel(){
        JSONObject loJSON = new JSONObject();
        try {
//            String lsSQL =   " SELECT "
//                           + "  sDescript "
//                           + " , sValuexxx "
//                           + " FROM xxxstandard_sets ";
//            lsSQL = MiscUtil.addCondition(lsSQL, " (sDescript = 'affiliated_make' OR sDescript = 'mainproduct') ");
//            System.out.println("AFFILIATED MAKE AND MAIN PRODUCT CHECK: " + lsSQL);
//            ResultSet loRS = poGRider.executeQuery(lsSQL);
//
//            if (MiscUtil.RecordCount(loRS) == 0){
//                poJSON.put("result", "error");
//                poJSON.put("message", "Please notify System Administrator to config `affiliated_make` or `mainproduct`.");
//            }
            
            String lsSQL =   " SELECT "
                    + "  sDescript "
                    + " , sValuexxx "
                    + " FROM xxxstandard_sets "
                    + " WHERE sDescript = 'mainproduct' ";
            System.out.println("MAIN PRODUCT CHECK: " + lsSQL);
            ResultSet loRS = poGRider.executeQuery(lsSQL);
            
            lsSQL =   " SELECT "                                               
                    + "   a.sModelIDx "                                        
                    + " , a.sModelDsc "                                        
                    + " , a.sMakeIDxx "                                        
                    + " , a.cRecdStat "                                        
                    + " , b.sMakeDesc "                                        
                    + " FROM vehicle_model a "                                 
                    + " LEFT JOIN vehicle_make b ON b.sMakeIDxx = a.sMakeIDxx ";
            if (MiscUtil.RecordCount(loRS) > 0){
                lsSQL = MiscUtil.addCondition(lsSQL,  " a.cRecdStat = '1' AND (a.sBodyType <> '4' AND a.sBodyType <> '5') "  
                                                        + " AND b.sMakeDesc = (SELECT sValuexxx FROM xxxstandard_sets WHERE sDescript = 'mainproduct') "
                                                        + " GROUP BY a.sModelDsc ORDER BY a.sModelDsc DESC ");
            } else {
                lsSQL = MiscUtil.addCondition(lsSQL,  " a.cRecdStat = '1' AND (a.sBodyType <> '4' AND a.sBodyType <> '5') "
                                                        //+ " AND b.sMakeDesc = (SELECT sValuexxx FROM xxxstandard_sets WHERE sDescript = 'affiliated_make') "
                                                        + " GROUP BY a.sModelDsc ORDER BY a.sModelDsc DESC ");
            }
            
            System.out.println("LOAD TEST MODEL "+ lsSQL);
            RowSetFactory factory = RowSetProvider.newFactory();
            loRS = poGRider.executeQuery(lsSQL);
            try {
                poTestModel = factory.createCachedRowSet();
                poTestModel.populate(loRS);
                MiscUtil.close(loRS);
                loJSON.put("result", "success");
                loJSON.put("message", "Test Model load successfully.");
            } catch (SQLException e) {
                loJSON.put("result", "error");
                loJSON.put("message", e.getMessage());
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Inquiry_Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        return loJSON;
    }
    
    public int getTestModelCount() throws SQLException{
        if (poTestModel != null){
            poTestModel.last();
            return poTestModel.getRow();
        }else{
            return 0;
        }
    }
    
    public Object getTestModelDetail(int fnRow, int fnIndex) throws SQLException{
        if (fnIndex == 0) return null;
        
        poTestModel.absolute(fnRow);
        return poTestModel.getObject(fnIndex);
    }
    
    public Object getTestModelDetail(int fnRow, String fsIndex) throws SQLException{
        return getTestModelDetail(fnRow, MiscUtil.getColumnIndex(poTestModel, fsIndex));
    }
    
    public JSONObject loadBankApplicationList(){
        JSONObject loJSON = new JSONObject();
        try {
            String lsSQL =    " SELECT "                                                 
                            + "    a.sTransNox "                                         
                            + "  , a.sApplicNo "                                         
                            + "  , a.dAppliedx "                                         
                            + "  , a.dApproved "                                         
                            + "  , a.cPayModex "                                         
                            + "  , a.sSourceCD "                                         
                            + "  , a.sSourceNo "                                         
                            + "  , a.sBrBankID "                                         
                            + "  , a.sRemarksx "                                         
                            + "  , a.cTranStat "                                         
                            + "  , a.sCancelld "                                         
                            + "  , a.dCancelld "                                         
                            + "  , b.sBrBankNm "                                         
                            + "  , c.sBankIDxx "                                         
                            + "  , c.sBankName "                                          
                            + "  , c.sBankType "                                          
                            + " FROM bank_application a "                                
                            + " LEFT JOIN banks_branches b ON b.sBrBankID = a.sBrBankID "
                            + " LEFT JOIN banks c ON c.sBankIDxx = b.sBankIDxx ";
            
            lsSQL = MiscUtil.addCondition(lsSQL,  " a.sSourceNo = " + SQLUtil.toSQL(poModel.getTransNo()) 
                                                    + " ORDER BY a.sTransNox DESC ");
            System.out.println("LOAD BANK APPLICATIONS "+ lsSQL);
            RowSetFactory factory = RowSetProvider.newFactory();
            ResultSet loRS = poGRider.executeQuery(lsSQL);
            try {
                poBankApp = factory.createCachedRowSet();
                poBankApp.populate(loRS);
                MiscUtil.close(loRS);
                loJSON.put("result", "success");
                loJSON.put("message", "Bank Applications load successfully.");
            } catch (SQLException e) {
                loJSON.put("result", "error");
                loJSON.put("message", e.getMessage());
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Inquiry_Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        return loJSON;
    }
    
    public int getBankApplicationCount() throws SQLException{
        if (poBankApp != null){
            poBankApp.last();
            return poBankApp.getRow();
        }else{
            return 0;
        }
    }
    
    public Object getBankApplicationDetail(int fnRow, int fnIndex) throws SQLException{
        if (fnIndex == 0) return null;
        
        poBankApp.absolute(fnRow);
        return poBankApp.getObject(fnIndex);
    }
    
    public Object getBankApplicationDetail(int fnRow, String fsIndex) throws SQLException{
        return getBankApplicationDetail(fnRow, MiscUtil.getColumnIndex(poBankApp, fsIndex));
    }
    
    public JSONObject loadFollowUpList(){
        JSONObject loJSON = new JSONObject();
        try {
            String lsSQL =    " SELECT "                                                    
                            + "    a.sTransNox "                                            
                            + "  , a.sReferNox "                                            
                            + "  , a.dTransact "                                            
                            + "  , a.sRemarksx "                                            
                            + "  , a.sMessagex "                                            
                            + "  , a.sMethodCd "                                            
                            + "  , a.sSclMedia "                                            
                            + "  , a.dFollowUp "                                            
                            + "  , a.tFollowUp "                                            
                            + "  , a.sGdsCmptr "                                            
                            + "  , a.sMkeCmptr "                                            
                            + "  , a.sDlrCmptr "                                            
                            + "  , a.sRspnseCd "                                            
                            + "  , a.sEmployID "                                              
                            + "  , b.sPlatform "
                            + "  , c.sCompnyNm "   
                            //+ "  , d.sDisValue "                                            
                            + " FROM customer_inquiry_followup  a "                         
                            + " LEFT JOIN online_platforms b ON b.sTransNox = a.sSclMedia "
                            + " LEFT JOIN GGC_ISysDBF.Client_Master c ON c.sClientID = a.sEmployID ";
                            //+ " LEFT JOIN xxxform_typelist d ON d.sDataValx = a.sRspnseCd ";
            
            lsSQL = MiscUtil.addCondition(lsSQL,  " a.sTransNox = " + SQLUtil.toSQL(poModel.getTransNo()) 
                                                    + " ORDER BY a.dTransact ASC ");
            System.out.println("LOAD FOLLOW UPS "+ lsSQL);
            RowSetFactory factory = RowSetProvider.newFactory();
            ResultSet loRS = poGRider.executeQuery(lsSQL);
            try {
                poFollowUp = factory.createCachedRowSet();
                poFollowUp.populate(loRS);
                MiscUtil.close(loRS);
                loJSON.put("result", "success");
                loJSON.put("message", "Follow Ups load successfully.");
            } catch (SQLException e) {
                loJSON.put("result", "error");
                loJSON.put("message", e.getMessage());
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Inquiry_Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        return loJSON;
    }
    
    public int getFollowUpCount() throws SQLException{
        if (poFollowUp != null){
            poFollowUp.last();
            return poFollowUp.getRow();
        }else{
            return 0;
        }
    }
    
    public Object getFollowUpDetail(int fnRow, int fnIndex) throws SQLException{
        if (fnIndex == 0) return null;
        
        poFollowUp.absolute(fnRow);
        return poFollowUp.getObject(fnIndex);
    }
    
    public Object getFollowUpDetail(int fnRow, String fsIndex) throws SQLException{
        return getFollowUpDetail(fnRow, MiscUtil.getColumnIndex(poFollowUp, fsIndex));
    }
    
    public ArrayList<Model_Inquiry_Master> getDetailList(){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        return paDetail;
    }
    public void setDetailList(ArrayList<Model_Inquiry_Master> foObj){this.paDetail = foObj;}
    
    public void setDetail(int fnRow, int fnIndex, Object foValue){ paDetail.get(fnRow).setValue(fnIndex, foValue);}
    public void setDetail(int fnRow, String fsIndex, Object foValue){ paDetail.get(fnRow).setValue(fsIndex, foValue);}
    public Object getDetail(int fnRow, int fnIndex){return paDetail.get(fnRow).getValue(fnIndex);}
    public Object getDetail(int fnRow, String fsIndex){return paDetail.get(fnRow).getValue(fsIndex);}
    
    public Model_Inquiry_Master getDetailModel(int fnRow) {
        return paDetail.get(fnRow);
    }
    
    public JSONObject loadForApproval(){
        /*
        -cTranStat	0	For Follow-up
        -cTranStat	1	On Process
        -cTranStat	2	Lost Sale
        -cTranStat	3	VSP
        -cTranStat	4	Sold
        -cTranStat	5	Cancelled
        */
        paDetail = new ArrayList<>();
        poJSON = new JSONObject();
        Model_Inquiry_Master loEntity = new Model_Inquiry_Master(poGRider);
        String lsSQL = MiscUtil.addCondition(loEntity.getSQL(), " a.cTranStat = '0' "); //Load for follow up inquiries
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        System.out.println(lsSQL);
       try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                while(loRS.next()){
                        paDetail.add(new Model_Inquiry_Master(poGRider));
                        paDetail.get(paDetail.size() - 1).openRecord(loRS.getString("sTransNox"));
                        
                        pnEditMode = EditMode.UPDATE;
                        lnctr++;
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record loaded successfully.");
                    } 
                
            }else{
//                paDetail = new ArrayList<>();
//                addDetail(fsValue);
                poJSON.put("result", "error");
                poJSON.put("continue", true);
                poJSON.put("message", "No record selected.");
            }
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        return poJSON;
    }
    
    public JSONObject approveTransaction(int fnRow){
        JSONObject loJSON = new JSONObject();
        paDetail.get(fnRow).setTranStat(TransactionStatus.STATE_CLOSED); //Set to ON PROCESS for VIP Clients
        loJSON = paDetail.get(fnRow).saveRecord();
        if(!"error".equals((String) loJSON.get("result"))){
            TransactionStatusHistory loEntity = new TransactionStatusHistory(poGRider);
            loJSON = loEntity.newTransaction();
            if(!"error".equals((String) loJSON.get("result"))){
                loEntity.getMasterModel().setApproved(poGRider.getUserID());
                loEntity.getMasterModel().setApprovedDte(poGRider.getServerDate());
                loEntity.getMasterModel().setSourceNo(paDetail.get(fnRow).getTransNo());
                loEntity.getMasterModel().setTableNme(paDetail.get(fnRow).getTable());
                loEntity.getMasterModel().setRefrStat(paDetail.get(fnRow).getTranStat());
                
                loJSON = loEntity.saveTransaction();
                if("error".equals((String) loJSON.get("result"))){
                    return loJSON;
                }
                
            }
        
        }
        return loJSON;
    }
}
