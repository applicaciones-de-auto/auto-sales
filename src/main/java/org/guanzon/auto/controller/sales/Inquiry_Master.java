/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.sales;

import java.sql.Connection;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.general.SearchDialog;
import org.guanzon.auto.model.sales.Model_Inquiry_Master;
import org.guanzon.auto.validator.sales.ValidatorFactory;
import org.guanzon.auto.validator.sales.ValidatorInterface;
import org.json.simple.JSONObject;

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
    
    Model_Inquiry_Master poModel;
    
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
                fnCol == poModel.getColumn("cRecdStat") ||
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
        
        poModel.setLockedBy(poGRider.getUserID());
        poModel.setLockedDt(poGRider.getServerDate());
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
                        + "  , a.dTransact "                                                               
                        + "  , a.sEmployID "                                                               
                        + "  , a.sClientID "                                                               
                        + "  , a.sAddrssID "                                                               
                        + "  , a.sContctID "                                                               
                        + "  , a.sAgentIDx "                                                               
                        + "  , a.dTargetDt "                                                               
                        + "  , a.cTranStat "                                                               
                        + "  , CASE WHEN a.cTranStat = '0' THEN 'FOR FOLLOW-UP'"                           
                        + " 	WHEN a.cTranStat = '1' THEN 'ON PROCESS' "                                   
                        + " 	WHEN a.cTranStat = '2' THEN 'LOST SALE'  "                                   
                        + " 	WHEN a.cTranStat = '3' THEN 'VSP'        "                                   
                        + " 	WHEN a.cTranStat = '4' THEN 'SOLD'       "                                   
                        + " 	WHEN a.cTranStat = '5' THEN 'RETIRED'    "                                   
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
                    1);
            
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
    
    public Model_Inquiry_Master getModel() {
        return poModel;
    }
    
    public JSONObject searchSalesExecutive(String fsValue) {
        poJSON = new JSONObject();
        
        String lsSQL =   " SELECT "
                       + " a.sClientID "
                       + " , a.cRecdStat "
                       + " , b.sCompnyNm "
                       + " FROM sales_executive a "
                       + " LEFT JOIN GGC_ISysDBF.Client_Master b ON b.sClientID = a.sClientID " ;  
                
        lsSQL = MiscUtil.addCondition(lsSQL, "a.cRecdStat = '1' AND b.sCompnyNm LIKE " + SQLUtil.toSQL(fsValue + "%"));  

        System.out.println("SEARCH SALES EXECUTIVE: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    "Employee Name",
                    "sCompnyNm",
                    "b.sCompnyNm",
                0);

        if (poJSON != null) {
            if(!"error".equals((String) poJSON.get("result"))){
                poModel.setEmployID((String) poJSON.get("sClientID"));
                poModel.setSalesExe((String) poJSON.get("sCompnyNm"));
            } else {
                poModel.setEmployID("");
                poModel.setSalesExe("");
            }
        } else {
            poModel.setEmployID("");
            poModel.setSalesExe("");
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
        
        return poJSON;
    }
    
    public JSONObject searchReferralAgent(String fsValue) {
        poJSON = new JSONObject();
        
        String lsSQL =   " SELECT "
                       + " a.sClientID "
                       + " , a.cRecdStat "
                       + " , b.sCompnyNm "
                       + " FROM sales_agent a "
                       + " LEFT JOIN client_master b ON b.sClientID = a.sClientID " ;  
                
        lsSQL = MiscUtil.addCondition(lsSQL, "a.cRecdStat = '1' AND b.sCompnyNm LIKE " + SQLUtil.toSQL(fsValue + "%"));  

        System.out.println("SEARCH SALES AGENT: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    "Agent Name",
                    "sCompnyNm",
                    "b.sCompnyNm",
                0);

        if (poJSON != null) {
            if(!"error".equals((String) poJSON.get("result"))){
                poModel.setAgentID((String) poJSON.get("sClientID"));
                poModel.setSalesAgn((String) poJSON.get("sCompnyNm"));
            } else {
                poModel.setAgentID("");
                poModel.setSalesAgn("");
            }
        } else {
            poModel.setAgentID("");
            poModel.setSalesAgn("");
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
        
        return poJSON;
    }
    
    public JSONObject searchClient(String fsValue, boolean fbInqClient) {
        String lsHeader = "ID»Name»Address";
        String lsColName = "sClientID»sCompnyNm»xAddressx"; 
        String lsColCrit = "a.sClientID»a.sCompnyNm»CONCAT(bb.sHouseNox, ' ', bb.sAddressx, ', ', c.sTownName, ' ', d.sProvName)";
        String lsSQL =    "  SELECT  "                                                                                                
                        + "  a.sClientID "                                                                                             
                        + " , a.sCompnyNm sCompnyNm "                                                                                  
                        + " , CONCAT(c.sHouseNox, ' ', c.sAddressx,' ', d.sBrgyName, ', ', e.sTownName, ' ', f.sProvName)) sAddressx " 
                        + " , a.sLastName "                                                                                            
                        + " , a.sFrstName "                                                                                            
                        + " , a.sMiddName "                                                                                            
                        + " , a.sSuffixNm "                                                                                            
                        + " , a.cClientTp "                                                                                            
                        + " , g.sMobileNo "                                                                                            
                        + " , h.sEmailAdd "                                                                                            
                        + " , GROUP_CONCAT(DISTINCT i.sAccountx) AS sAccountx "                                                        
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
            lsSQL = MiscUtil.addCondition(lsSQL, " a.cRecdStat = '1' "
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
            if(!"error".equals((String) poJSON.get("result"))){
                if(fbInqClient){
                    poModel.setClientID((String) loJSON.get("sClientID"));
                    poModel.setClientNm((String) loJSON.get("sCompnyNm"));
                    poModel.setClientTp((String) loJSON.get("cClientTp"));
                    poModel.setMobileNo((String) loJSON.get("sMobileNo"));
                    poModel.setEmailAdd((String) loJSON.get("sEmailAdd"));
                    poModel.setAccount((String) loJSON.get("sAccountx"));
                } else {
                    poModel.setContctID((String) loJSON.get("sClientID"));
                    poModel.setContctNm((String) loJSON.get("sCompnyNm"));
                    poModel.setClientTp("");
                    poModel.setMobileNo("");
                    poModel.setEmailAdd("");
                    poModel.setAccount("");
                } 
            } else {
                if(fbInqClient){
                    poModel.setClientID("");
                    poModel.setClientNm("");
                    poModel.setClientTp("");
                    poModel.setMobileNo("");
                    poModel.setEmailAdd("");
                    poModel.setAccount("");
                } else {
                    poModel.setContctID("");
                    poModel.setContctNm("");
                    poModel.setClientTp("");
                    poModel.setMobileNo("");
                    poModel.setEmailAdd("");
                    poModel.setAccount("");
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
            } else {
                poModel.setContctID("");
                poModel.setContctNm("");
                poModel.setClientTp("");
                poModel.setMobileNo("");
                poModel.setEmailAdd("");
                poModel.setAccount("");
            }
            loJSON  = new JSONObject();  
            loJSON.put("result", "error");
            loJSON.put("message", "No information found");
            return loJSON;
        }
        return loJSON;
    }
    
    public JSONObject searchOnlinePlatform(String fsValue) {
        poJSON = new JSONObject();
        String lsSQL =  "  SELECT " 
                       + "   sTransNox " 
                       + " , sPlatform " 
                       + " , sWebSitex " 
                       + " FROM online_platforms " ;  
                
        lsSQL = MiscUtil.addCondition(lsSQL, "a.cRecdStat = '1' AND b.sPlatform LIKE " + SQLUtil.toSQL(fsValue + "%"));  

        System.out.println("SEARCH ONLINE PLATFORMS: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    "Platfrom",
                    "sPlatform",
                    "sPlatform",
                0);

        if (poJSON != null) {
            if(!"error".equals((String) poJSON.get("result"))){
                poModel.setSourceNo((String) poJSON.get("sTransNox"));
                poModel.setPlatform((String) poJSON.get("sPlatform"));
            } else {
                poModel.setSourceNo("");
                poModel.setPlatform("");
            }
        } else {
            poModel.setSourceNo("");
            poModel.setPlatform("");
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
        
        return poJSON;
    }
    
    public JSONObject searchActivity(String fsValue) {
        poJSON = new JSONObject();
        String lsHeader = "Actvitiy Date From»Actvitiy Date To»Activity ID»Activity No»Activity Title";
        String lsColName = "dDateFrom»dDateThru»sActvtyID»sActNoxxx»sActTitle"; 
        String lsCriteria = "dDateFrom»dDateThru»sActvtyID»sActNoxxx»sActTitle";
        
        String lsSQL =    " SELECT "                                                                   
                        + "   sActvtyID "                                                            
                        + " , sActNoxxx "                                                            
                        + " , sActTitle "                                                            
                        + " , dDateFrom "                                                            
                        + " , dDateThru "                                                           
                        + " , cTranStat "                                                             
                        + " FROM activity_master "   ;  
                
        lsSQL = MiscUtil.addCondition(lsSQL, " cTranStat = '3' AND sActTitle LIKE " + SQLUtil.toSQL(fsValue + "%")
                                                + " AND dDateFrom >=" + SQLUtil.toSQL(poModel.getTransactDte())
                                                + " AND dDateThru <=" + SQLUtil.toSQL(poModel.getTransactDte()));  

        System.out.println("SEARCH ACTIVITY: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    lsHeader,
                    lsColName,
                    lsCriteria,
                0);

        if (poJSON != null) {
            if(!"error".equals((String) poJSON.get("result"))){
                poModel.setActvtyID((String) poJSON.get("sActvtyID"));
                poModel.setActTitle((String) poJSON.get("sActTitle"));
            } else {
                poModel.setActvtyID("");
                poModel.setActTitle("");
            }
        } else {
            poModel.setActvtyID("");
            poModel.setActTitle("");
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
        
        return poJSON;
    }
    
}
