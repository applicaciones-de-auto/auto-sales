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
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.general.LockTransaction;
import org.guanzon.auto.general.SearchDialog;
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
        
        poLockTrans.saveLockTransaction(poModel.getTable(),"sTransNox", poModel.getTransNo());
        
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
        String lsColName = "dTransact»sTransNox»sCompnyNm»sAddressx»sTranStat»sBranchNm";
        String lsSQL =    " SELECT "                                                                       
                        + "    a.sTransNox "                                                               
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
                        + " 	WHEN a.cTranStat = '3' THEN 'VSP'        "                                   
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
                
        lsSQL = MiscUtil.addCondition(lsSQL, "a.cRecdStat = '1' AND b.sCompnyNm LIKE " + SQLUtil.toSQL(fsValue + "%"));  

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
                
        lsSQL = MiscUtil.addCondition(lsSQL, "a.cRecdStat = '1' AND b.sCompnyNm LIKE " + SQLUtil.toSQL(fsValue + "%"));  

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
        String lsColCrit = "a.sClientID»a.sCompnyNm»CONCAT(c.sHouseNox, ' ', c.sAddressx,' ', d.sBrgyName, ', ', e.sTownName, ' ', f.sProvName)";
        String lsSQL =    "  SELECT  "                                                                                                
                        + "  a.sClientID "                                                                                             
                        + " , a.sCompnyNm "                                                                                  
                        + " , CONCAT(c.sHouseNox, ' ', c.sAddressx,' ', d.sBrgyName, ', ', e.sTownName, ' ', f.sProvName) AS sAddressx " 
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
                                                    + " AND a.sCompnyNm LIKE " + SQLUtil.toSQL(fsValue + "%")) ;
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL, " a.cRecdStat = '1' AND a.cClientTp = '0' "
                                                    + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getClientID())
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
                    poModel.setAddress((String) loJSON.get("sAddressx"));
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
    
    public JSONObject searchAvlVhcl(String fsValue, boolean fbIsBrandNew) {
        JSONObject loJSON = new JSONObject();
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
                        + " WHERE a.cSoldStat = '1' ";
        
        if(fbIsBrandNew){
            lsSQL = lsSQL + " AND a.cVhclNewx = '0' ";
        } else {
            lsSQL = lsSQL + " AND a.cVhclNewx = '1' ";
        }
        
        lsSQL = lsSQL +  " AND ( a.sCSNoxxxx LIKE " + SQLUtil.toSQL(fsValue + "%") 
                      + " OR b.sPlateNox LIKE " + SQLUtil.toSQL(fsValue + "%") + " ) " ;
        
        System.out.println("SEARCH AVAILABLE VEHICLE: " + lsSQL);
        loJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    lsHeader,
                    lsColName,
                    lsColCrit,
                     1);

        if (loJSON != null) {
            if(!"error".equals((String) loJSON.get("result"))){
                poModel.setSerialID((String) loJSON.get("sSerialID"));
                poModel.setFrameNo((String) loJSON.get("sFrameNox"));
                poModel.setEngineNo((String) loJSON.get("sEngineNo"));
                poModel.setCSNo((String) loJSON.get("sCSNoxxxx"));
                poModel.setPlateNo((String) loJSON.get("sPlateNox"));
                poModel.setDescript((String) loJSON.get("sDescript"));
            } else {
                poModel.setSerialID("");
                poModel.setFrameNo("");
                poModel.setEngineNo("");
                poModel.setCSNo("");
                poModel.setPlateNo("");
                poModel.setDescript("");
            }
        } else {
            poModel.setSerialID("");
            poModel.setFrameNo("");
            poModel.setEngineNo("");
            poModel.setCSNo("");
            poModel.setPlateNo("");
            poModel.setDescript("");
            loJSON = new JSONObject();
            loJSON.put("result", "error");
            loJSON.put("message", "No record loaded.");
            return loJSON;
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
                lsSQL = MiscUtil.addCondition(lsSQL,  " a.cRecdStat = '1' "
                                                        + " AND b.sMakeDesc = (SELECT sValuexxx FROM xxxstandard_sets WHERE sDescript = 'mainproduct') "
                                                        + " GROUP BY a.sModelDsc ORDER BY a.sModelDsc DESC ");
            } else {
                lsSQL = MiscUtil.addCondition(lsSQL,  " a.cRecdStat = '1' "
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
                            + "  , d.sDisValue "                                            
                            + " FROM customer_inquiry_followup  a "                         
                            + " LEFT JOIN online_platforms b ON b.sTransNox = a.sSclMedia "
                            + " LEFT JOIN GGC_ISysDBF.Client_Master c ON c.sClientID = a.sEmployID "
                            + " LEFT JOIN xxxform_typelist d ON d.sDataValx = a.sRspnseCd ";
            
            lsSQL = MiscUtil.addCondition(lsSQL,  " a.sTransNox = " + SQLUtil.toSQL(poModel.getTransNo()) 
                                                    + " ORDER BY a.dTransact ASC ");
            System.out.println("LOAD LOAD FOLLOW UPS "+ lsSQL);
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
    
    
}
