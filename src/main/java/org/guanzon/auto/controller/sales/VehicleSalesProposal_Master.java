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
import java.time.format.DateTimeFormatter;
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
import org.guanzon.auto.general.CancelForm;
import org.guanzon.auto.general.LockTransaction;
import org.guanzon.auto.general.SearchDialog;
import org.guanzon.auto.model.sales.Model_VehicleSalesProposal_Master;
import org.guanzon.auto.validator.sales.ValidatorFactory;
import org.guanzon.auto.validator.sales.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class VehicleSalesProposal_Master implements GTransaction{
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    int pnEditMode;
    String psTransStat;
    
    String psMessagex;
    public JSONObject poJSON;

    Model_VehicleSalesProposal_Master poModel;
    LockTransaction poLockTrans;
    
    CachedRowSet poReservation;

    public VehicleSalesProposal_Master(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;

        poModel = new Model_VehicleSalesProposal_Master(foGRider);
        pnEditMode = EditMode.UNKNOWN;
    }

    @Override
    public int getEditMode() {
        return pnEditMode;
    }
    
    @Override
    public Model_VehicleSalesProposal_Master getMasterModel() {
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

            poModel = new Model_VehicleSalesProposal_Master(poGRider);
            Connection loConn = null;
            loConn = setConnection();

            poModel.setTransNo(MiscUtil.getNextCode(poModel.getTable(), "sTransNox", true, poGRider.getConnection(), poGRider.getBranchCode()+"VSP"));
            poModel.setVSPNO(MiscUtil.getNextCode(poModel.getTable(), "sVSPNOxxx", true, poGRider.getConnection(), poGRider.getBranchCode()));
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
        
        poModel = new Model_VehicleSalesProposal_Master(poGRider);
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
        
        poLockTrans.saveLockTransaction(poModel.getTable(),"sTransNox", poModel.getTransNo(),poModel.getBranchCD());
        
        pnEditMode = EditMode.UPDATE;
        poJSON.put("result", "success");
        poJSON.put("message", "Update mode success.");
        return poJSON;
    }

    @Override
    public JSONObject saveTransaction() {
        poJSON = new JSONObject();  
        
        ValidatorInterface validator = ValidatorFactory.make( ValidatorFactory.TYPE.VehicleSalesProposal_Master, poModel);
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
        //Update Inquiry and Vehicle Serial
        JSONObject loJSON = poModel.updateTables();
        if("error".equalsIgnoreCase((String) loJSON.get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return loJSON;
        }
        
        return poJSON;
    }

    @Override
    public JSONObject cancelTransaction(String fsTransNox) {
        poJSON = new JSONObject();

        if (poModel.getEditMode() == EditMode.READY
                || poModel.getEditMode() == EditMode.UPDATE) {
            try {
                poJSON = poModel.setTranStat(TransactionStatus.STATE_CANCELLED);
                if ("error".equals((String) poJSON.get("result"))) {
                    return poJSON;
                }
                
                ValidatorInterface validator = ValidatorFactory.make( ValidatorFactory.TYPE.VehicleSalesProposal_Master, poModel);
                validator.setGRider(poGRider);
                if (!validator.isEntryOkay()){
                    poJSON.put("result", "error");
                    poJSON.put("message", validator.getMessage());
                    return poJSON;
                }
                
                CancelForm cancelform = new CancelForm();
                if (!cancelform.loadCancelWindow(poGRider, poModel.getTransNo(), poModel.getVSPNO(), "VSP")) {
                    poJSON.put("result", "error");
                    poJSON.put("message", "Cancellation failed.");
                    return poJSON;
                }
                
                poJSON = poModel.saveRecord();
            } catch (SQLException ex) {
                Logger.getLogger(VehicleSalesProposal_Master.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded to update.");
        }
        return poJSON;
    }
    
    public JSONObject searchTransaction(String fsValue, boolean fbByCode) {
        String lsHeader = "VSP Date»VSP No»Customer»CS No»Plate No»Cancelled";
        String lsColName = "dTransact»sVSPNOxxx»sCompnyNm»sCSNoxxxx»sPlateNox»sTrStatus";
        String lsSQL = poModel.getSQL();
        System.out.println(lsSQL);
        JSONObject loJSON = SearchDialog.jsonSearch(
                    poGRider,
                    lsSQL,
                    "",
                    lsHeader,
                    lsColName,
                "0.1D»0.2D»0.3D»0.2D»0.2D»0.3D", 
                    "VSP",
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
    public JSONObject searchWithCondition(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject searchTransaction(String fsColumn, String string, boolean bln) {
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
    

    @Override
    public JSONObject closeTransaction(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject deleteTransaction(String string) {
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
    
    public JSONObject searchInquiry(String fsValue){
        JSONObject loJSON = new JSONObject(); //Inquiry Date» dTransact» Customer Address» sAddressx»
        String lsHeader = "Inquiry ID»Customer Name»Branch»Inquiry Status";
        String lsColName = "sInqryIDx»sCompnyNm»sBranchNm»sTranStat";
        String lsCriteria = "a.dTransact»a.sInqryIDx»b.sCompnyNm»"
                            + "IFNULL(CONCAT( IFNULL(CONCAT(d.sHouseNox,' ') , ''), "                      
                            + " 	IFNULL(CONCAT(d.sAddressx,' ') , ''),  "                                     
                            + " 	IFNULL(CONCAT(e.sBrgyName,' '), ''),   "                                     
                            + " 	IFNULL(CONCAT(f.sTownName, ', '),''),  "                                     
                            + " 	IFNULL(CONCAT(g.sProvName),'') )	, '')"
                            + "»p.sBranchNm»cTranStat";
        
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
                        + "  , a.cIsVhclNw "                                                               
                        + "  , a.sSourceCD "                                                               
                        + "  , a.sSourceNo "                                                            
                        + "  , a.cPayModex "                                                               
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
                        + "  , k.sCompnyNm AS sContctNm "                                                 
                        + "  , p.sBranchNm  "                                              
                        + "  , q.sPlatform  "
                        + "  , SUM(r.nAmountxx) AS nAmountxx"                                                              
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
                        + " LEFT JOIN online_platforms q ON q.sTransNox = a.sTransNox "
                        + " LEFT JOIN customer_inquiry_reservation r ON r.sSourceNo = a.sTransNox AND r.cTranStat = '2' "   ; 
        
        lsSQL = MiscUtil.addCondition(lsSQL,  " a.cTranStat = '1' "
                                                + " AND b.sCompnyNm LIKE " + SQLUtil.toSQL(fsValue + "%")
                                                + " GROUP BY a.sTransNox ");
        System.out.println("SEARCH INQUIRY: " + lsSQL);
        loJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    lsHeader,
                    lsColName,
                    lsCriteria,
                1);

        if (loJSON != null) {
        } else {
            loJSON = new JSONObject();
            loJSON.put("result", "error");
            loJSON.put("message", "No record loaded.");
            return loJSON;
        }
        return loJSON;
    }
    
    public JSONObject searchClient(String fsValue, boolean fbBuyClient) {
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
        
        if(fbBuyClient){
            lsSQL = MiscUtil.addCondition(lsSQL, " a.cRecdStat = '1' "
                                                    + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getContctID())
                                                    + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getAgentID())
                                                    + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getEmployID())
                                                    + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getCoCltID())
                                                    + " AND a.sCompnyNm LIKE " + SQLUtil.toSQL(fsValue + "%")) ;
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL, " a.cRecdStat = '1' AND a.cClientTp = '0' "
                                                    + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getClientID())
                                                    + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getAgentID())
                                                    + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getEmployID())
                                                    + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getContctID())
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
        }else {
            loJSON  = new JSONObject();  
            loJSON.put("result", "error");
            loJSON.put("message", "No information found");
            return loJSON;
        }
        return loJSON;
    }
    
    public JSONObject searchAvlVhcl(String fsValue, String fsFindBy) {
        JSONObject loJSON = new JSONObject();
        
        if(poModel.getClientID() == null){
            loJSON.put("result", "error");
            loJSON.put("message", "Buying Customer is not set.");
        } else {
            if(poModel.getClientID().trim().isEmpty()){
                loJSON.put("result", "error");
                loJSON.put("message", "Buying Customer is not set.");
            }
        }
        
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
        int lnSort = 0;
        String lsSQL =    " SELECT "                                                               
                        + "    a.sSerialID "                                                       
                        + "  , a.sFrameNox "                                                       
                        + "  , a.sEngineNo "                                                       
                        + "  , a.sCSNoxxxx "                                                        
                        + "  , a.cVhclNewx "                                                          
                        + "  , a.sKeyNoxxx "                                                     
                        + "  , a.cSoldStat "                                                      
                        + "  , b.sPlateNox "                                                       
                        + "  , c.sDescript "                                                       
                        + " FROM vehicle_serial a "                                                
                        + " LEFT JOIN vehicle_serial_registration b ON b.sSerialID = a.sSerialID " 
                        + " LEFT JOIN vehicle_master c ON c.sVhclIDxx = a.sVhclIDxx"
                        + " WHERE a.cSoldStat <> '0' "
                        + " AND a.cVhclNewx = " + SQLUtil.toSQL(poModel.getIsVhclNw())  ;
                       // + " AND ( a.sCSNoxxxx LIKE " + SQLUtil.toSQL(fsValue + "%") 
                       //+ " OR b.sPlateNox LIKE " + SQLUtil.toSQL(fsValue + "%") + " ) " ;
        
        switch(fsFindBy){
            case "CS" :
                lnSort = 1;
                break;
            case "PLT" :
                lnSort = 2;
                break;
            case "ENG" :
                lnSort = 3;
                break;
            case "FRM" :
                lnSort = 4;
                break;
        }
        
        System.out.println("SEARCH AVAILABLE VEHICLE: " + lsSQL);
        loJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    lsHeader,
                    lsColName,
                    lsColCrit,
                     lnSort);

        if (loJSON != null) {
        } else {
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
            Logger.getLogger(VehicleSalesProposal_Master.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return loJSON;
    }
    
    public JSONObject searchBankApp(String fsValue){
        JSONObject loJSON = new JSONObject();
        String lsHeader = "Applied Date»Application No»Bank Name»Bank Branch";
        String lsColName = "dAppliedx»sApplicNo»sBankName»sBrBankNm";
        String lsCriteria = "a.dAppliedx»a.sApplicNo»c.sBankName»b.sBrBankNm";
        
        String lsSQL =   " SELECT "                                                 
                        + "    a.sTransNox "                                         
                        + "  , a.sApplicNo "                                         
                        + "  , a.dAppliedx "                                         
                        + "  , a.dApproved "                                         
                        + "  , a.cPayModex "                                         
                        + "  , a.sSourceCD "                                         
                        + "  , a.sSourceNo "                                         
                        + "  , a.sBrBankID "                                          
                        + "  , a.cTranStat "                                          
                        + "  , b.sBrBankNm "                                         
                        + "  , c.sBankIDxx "                                         
                        + "  , c.sBankName "   
                        + "  , c.sBankType " 
                        + " FROM bank_application a "                                
                        + " LEFT JOIN banks_branches b ON b.sBrBankID = a.sBrBankID "
                        + " LEFT JOIN banks c ON c.sBankIDxx = b.sBankIDxx          "; 
        
        lsSQL = MiscUtil.addCondition(lsSQL,  " a.cTranStat = '2' "
                                                + " AND a.sSourceNo = " + SQLUtil.toSQL(poModel.getInqryID())
                                                + " AND a.cPayModex = " + SQLUtil.toSQL(poModel.getPayMode())
                                                + " AND c.sBankName LIKE " + SQLUtil.toSQL(fsValue + "%"));
        System.out.println("SEARCH BANK APPLICATION: " + lsSQL);
        loJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    lsHeader,
                    lsColName,
                    lsCriteria,
                1);

        if (loJSON != null) {
        } else {
            loJSON = new JSONObject();
            loJSON.put("result", "error");
            loJSON.put("message", "No record loaded.");
            return loJSON;
        }
        return loJSON;
    }
    
    public JSONObject searchInsurance(String fsValue){
        JSONObject loJSON = new JSONObject();
        String lsHeader = "Branch ID»Insurance Name»Insurance Branch";
        String lsColName = "sBrInsIDx»sInsurNme»sBrInsNme";
        String lsCriteria = "a.sBrInsIDx»b.sInsurNme»a.sBrInsNme";
        
        String lsSQL =   " SELECT "                                                    
                        + "    a.sBrInsIDx "                                            
                        + "  , a.sBrInsNme "                                            
                        + "  , a.sBrInsCde "                                            
                        + "  , a.sCompnyTp "                                            
                        + "  , a.sInsurIDx "                                            
                        + "  , a.cRecdStat "                                             
                        + "  , b.sInsurNme "                                            
                        + " FROM insurance_company_branches a "                         
                        + " LEFT JOIN insurance_company b ON b.sInsurIDx = a.sInsurIDx "; 
        
        lsSQL = MiscUtil.addCondition(lsSQL,  " a.cRecdStat = '1' "
                                                + " AND b.sInsurNme LIKE " + SQLUtil.toSQL(fsValue + "%"));
        System.out.println("SEARCH INSURANCE: " + lsSQL);
        loJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    lsHeader,
                    lsColName,
                    lsCriteria,
                1);

        if (loJSON != null) {
        } else {
            loJSON = new JSONObject();
            loJSON.put("result", "error");
            loJSON.put("message", "No record loaded.");
            return loJSON;
        }
        return loJSON;
    }
    
    public JSONObject checkExistingRecord(){
        JSONObject loJSON = new JSONObject();
        try {
            //Check Existing VSP of Inquiry
            String lsTransNo = "";
            String lsID = "";
            String lsDesc = "";
            String lsSQL = "";
                lsSQL =  " SELECT "          
                        + "   sTransNox "     
                        + " , dTransact "     
                        + " , sVSPNOxxx "     
                        + " , dDelvryDt "     
                        + " , sInqryIDx "     
                        + " , sClientID "     
                        + " , sCoCltIDx "     
                        + " , sSerialID "     
                        + " FROM vsp_master " ;

                lsSQL = MiscUtil.addCondition(lsSQL, " cTranStat <> '0' "
                                                        + " AND sInqryIDx = " + SQLUtil.toSQL(poModel.getInqryID()) 
                                                        );
                System.out.println("EXISTING VSP OF INQUIRY CHECK: " + lsSQL);
                ResultSet loRS = poGRider.executeQuery(lsSQL);

                if (MiscUtil.RecordCount(loRS) > 0){
                while(loRS.next()){
                    lsTransNo = loRS.getString("sTransNox");
                    lsID = loRS.getString("sVSPNOxxx");
                    lsDesc = xsDateShort(loRS.getDate("dTransact"));
                }
                
                MiscUtil.close(loRS);
                loJSON.put("result", "error");
                loJSON.put("sTransNox", lsTransNo);
                loJSON.put("message", "Found an existing vsp record with the same client inquiry."
                                        + "\n\n<VSP No:" + lsID + ">"
                                        + "\n<VSP Date:" + lsDesc + ">"
                                        + "\n\n Do you want to view the record?");
                return loJSON;

            } 
        } catch (SQLException ex) {
            Logger.getLogger(VehicleSalesProposal_Master.class.getName()).log(Level.SEVERE, null, ex);
        }   
        
        return loJSON;
    }
    
//    public JSONObject loadOthReservation(){
//        JSONObject loJSON = new JSONObject();
//        try {
//            String lsSQL =    " SELECT "                                                                  
//                            + "   a.sTransNox "                                                           
//                            + " , a.sBranchCd "                                                           
//                            + " , a.dTransact "                                                           
//                            + " , a.cDocTypex "                                                           
//                            + " , a.sReferNox "                                                           
//                            + " , a.sClientID "                                                           
//                            + " , a.nTranTotl "                                                           
//                            + " , a.nDiscount "                                                           
//                            + " , a.nVatSales "                                                           
//                            + " , a.nVatAmtxx "                                                           
//                            + " , a.nNonVATSl "                                                           
//                            + " , a.nZroVATSl "                                                           
//                            + " , a.cWTRatexx "                                                           
//                            + " , a.nCWTAmtxx "                                                           
//                            + " , a.nAdvPaymx "                                                           
//                            + " , a.nNetTotal "                                                           
//                            + " , a.nCashAmtx "                                                           
//                            + " , a.nChckAmtx "                                                           
//                            + " , a.nCardAmtx "                                                           
//                            + " , a.nOthrAmtx "                                                           
//                            + " , a.nGiftAmtx "                                                           
//                            + " , a.nAmtPaidx "                                                           
//                            + " , a.cTranStat "                                                           
//                            + " , b.sReferNox "                                                       
//                            + " , c.sTransNox AS sRsvTrnNo "                                                         
//                            + " , c.sReferNox AS sRsvSlpNo "                                              
//                            + " , d.sCompnyNm AS sClientNm "                                              
//                            + " FROM si_master a  "                                                       
//                            + " INNER JOIN si_master_source b ON b.sTransNox = a.sTransNox "              
//                            + " INNER JOIN customer_inquiry_reservation c ON c.sTransNox = b.sReferNox "  
//                            + " LEFT JOIN client_master d ON d.sClientID = a.sClientID " ;
//            lsSQL = MiscUtil.addCondition(lsSQL,  " a.cTranStat = '1' "
//                                                    + " AND a.sClientID <> " + SQLUtil.toSQL(poModel.getInqCltID()) 
//                                                    + " GROUP BY a.sTransNox ORDER BY a.dTransact DESC ");
//            
//            System.out.println("LOAD OTHER RESERVATION "+ lsSQL);
//            RowSetFactory factory = RowSetProvider.newFactory();
//            ResultSet loRS = poGRider.executeQuery(lsSQL);
//            try {
//                poReservation = factory.createCachedRowSet();
//                poReservation.populate(loRS);
//                MiscUtil.close(loRS);
//                loJSON.put("result", "success");
//                loJSON.put("message", "Other reservation load successfully.");
//            } catch (SQLException e) {
//                loJSON.put("result", "error");
//                loJSON.put("message", e.getMessage());
//            }
//            
//        } catch (SQLException ex) {
//            Logger.getLogger(VehicleSalesProposal_Master.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return loJSON;
//        
//    }
//     
//    public int getOthReservationCount() throws SQLException{
//        if (poReservation != null){
//            poReservation.last();
//            return poReservation.getRow();
//        }else{
//            return 0;
//        }
//    }
//    
//    public Object getOthReservationDetail(int fnRow, int fnIndex) throws SQLException{
//        if (fnIndex == 0) return null;
//        
//        poReservation.absolute(fnRow);
//        return poReservation.getObject(fnIndex);
//    }
//    
//    public Object getOthReservationDetail(int fnRow, String fsIndex) throws SQLException{
//        return getOthReservationDetail(fnRow, MiscUtil.getColumnIndex(poReservation, fsIndex));
//    } 
    
    private static String xsDateShort(Date fdValue) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(fdValue);
        return date;
    }

    private static String xsDateShort(String fsValue) throws org.json.simple.parser.ParseException, java.text.ParseException {
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
    
}
