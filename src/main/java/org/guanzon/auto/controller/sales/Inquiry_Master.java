/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.sales;

import java.sql.Connection;
import java.util.ArrayList;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GRecord;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.model.sales.Model_Inquiry_Master;
import org.guanzon.auto.model.sales.Model_Inquiry_Promo;
import org.guanzon.auto.model.sales.Model_Inquiry_VehiclePriority;
import org.json.simple.JSONObject;

/**
 *
 * @author MIS-PC
 */
public class Inquiry_Master implements GTransaction {
    final String XML = "Model_Inquiry_Master.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    int pnEditMode;
    String psMessagex;
    public JSONObject poJSON;
    
    Model_Inquiry_Master poMaster;
    
    public Inquiry_Master(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poGRider = foAppDrver;
        pbWtParent = fbWtParent;
        psBranchCd = fsBranchCd.isEmpty() ? foAppDrver.getBranchCode() : fsBranchCd;
    }

    @Override
    public int getEditMode() {
        return pnEditMode;     
    }
    
    @Override
    public Model_Inquiry_Master getMasterModel() {
        return poMaster;
    }

    @Override
    public JSONObject setMaster(int fnCol, Object foData) {
        JSONObject obj = new JSONObject();
        obj.put("pnEditMode", pnEditMode);
        if (pnEditMode != EditMode.UNKNOWN){
            // Don't allow specific fields to assign values
            if(!(fnCol == poMaster.getColumn("sTransNox") ||
                fnCol == poMaster.getColumn("cRecdStat") ||
                fnCol == poMaster.getColumn("sModified") ||
                fnCol == poMaster.getColumn("dModified"))){
                poMaster.setValue(fnCol, foData);
                obj.put(fnCol, pnEditMode);
            }
        }
        return obj;
    }

    @Override
    public JSONObject setMaster(String fsCol, Object foData) {
        return setMaster(poMaster.getColumn(fsCol), foData);
    }

    public Object getMaster(int fnCol) {
        if(pnEditMode == EditMode.UNKNOWN)
            return null;
        else 
            return poMaster.getValue(fnCol);
    }

    public Object getMaster(String fsCol) {
        return getMaster(poMaster.getColumn(fsCol));
    }
    
    public String getTransNox(){
        return poMaster.getTransNox();
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
    public JSONObject newTransaction() {
        poJSON = new JSONObject();
        try{
            pnEditMode = EditMode.ADDNEW;
            org.json.simple.JSONObject obj;

            poMaster = new Model_Inquiry_Master(poGRider);
            Connection loConn = null;
            loConn = setConnection();

            poMaster.setValue("sTransNox",MiscUtil.getNextCode(poMaster.getTable(), "sTransNox", true, loConn, psBranchCd));
            poMaster.newRecord();

            if (poMaster == null){
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

    @Override
    public JSONObject openTransaction(String fsValue) {
        pnEditMode = EditMode.READY;
        poJSON = new JSONObject();
        
        poMaster = new Model_Inquiry_Master(poGRider);
        poJSON = poMaster.openRecord(fsValue);
        
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
        pnEditMode = EditMode.UPDATE;
        poJSON.put("result", "success");
        poJSON.put("message", "Update mode success.");
        return poJSON;
    }

    @Override
    public JSONObject saveTransaction() {
        poJSON = new JSONObject();  
        //ValidatorInterface validator = ValidatorFactory.make(types,  ValidatorFactory.TYPE.Client_Master, poClient);
//        if (!validator.isEntryOkay()){
//            poJSON.put("result", "error");
//            poJSON.put("message", validator.getMessage());
//            return poJSON;
//        }
        
        if (!pbWtParent) poGRider.beginTrans();
        poJSON =  poMaster.saveRecord();
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        if (!pbWtParent) poGRider.commitTrans();
        
        return poJSON;
    }

    public JSONObject searchTransaction(String fsValue, boolean fbByCode) {
        String lsHeader = "Inquiry ID»Inquiry Date»Customer Name»";
        String lsColName = "sTransNox»dTransact»sCompnyNm";
        String lsColCrit = "a.sTransNox»sCompnyNm";
        String lsSQL =  "SELECT " +
                        " a.sTransNox"  + //1
                        ", a.sBranchCD" + //2
                        ", a.dTransact" + //3
                        ", a.sClientID" + //4
                        ",IFNULL(b.sCompnyNm,'') as sCompnyNm " +//29
                        ", IFNULL(CONCAT( IFNULL(CONCAT(dd.sHouseNox,' ') , ''), IFNULL(CONCAT(dd.sAddressx,' ') , ''), " +
                        " 	IFNULL(CONCAT(f.sBrgyName,' '), ''), " +
                        " 	IFNULL(CONCAT(e.sTownName, ', '),''), " +
                        " 	IFNULL(CONCAT(g.sProvName),'') )	, '') AS sAddressx " + //33
                    " FROM  " + poMaster.getTable() + " a " +
                    " LEFT JOIN client_master b ON b.sClientID = a.sClientID" +
                    " LEFT JOIN addresses dd ON dd.sAddrssID = d.sAddrssID" + 
                    " LEFT JOIN TownCity e ON e.sTownIDxx = dd.sTownIDxx" +
                    " LEFT JOIN Barangay f ON f.sBrgyIDxx = dd.sBrgyIDxx AND f.sTownIDxx = dd.sTownIDxx" + 
                    " LEFT JOIN Province g on g.sProvIDxx = e.sProvIDxx"  ;  
        
        if (fbByCode)
            lsSQL = MiscUtil.addCondition(lsSQL, "a.sClientID = " + SQLUtil.toSQL(fsValue));
        else
            lsSQL = MiscUtil.addCondition(lsSQL, "a.sCompnyNm LIKE " + SQLUtil.toSQL("%" + fsValue + "%"));
        
        JSONObject loJSON;
        String lsValue;
        
        System.out.println("lsSQL = " + lsSQL);
        loJSON = ShowDialogFX.Search(poGRider, 
                                        lsSQL, 
                                        fsValue, 
                                        lsHeader, 
                                        lsColName, 
                                        lsColCrit, 
                                        fbByCode ? 0 :1);
            
        System.out.println("loJSON = " + loJSON.toJSONString());
            
        if (loJSON != null && !"error".equals((String) loJSON.get("result"))) {
            System.out.println("sTransNox = " + (String) loJSON.get("sTransNox"));
            lsValue = (String) loJSON.get("sTransNox");
        }else {
            loJSON.put("result", "error");
            loJSON.put("message", "No client information found for: " + fsValue + ", Please check client type and client name details.");
            return loJSON;
        }
        return openTransaction(lsValue);
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

    
}
