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
import org.guanzon.appdriver.constant.TransactionStatus;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.general.LockTransaction;
import org.guanzon.auto.general.SearchDialog;
import org.guanzon.auto.model.sales.Model_Inquiry_Master;
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
        
        poLockTrans.saveLockTransaction(poModel.getTable(),"sTransNox", poModel.getTransNo());
        
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
        
        return poJSON;
    }

    @Override
    public JSONObject cancelTransaction(String fsTransNox) {
        poJSON = new JSONObject();

        if (poModel.getEditMode() == EditMode.READY
                || poModel.getEditMode() == EditMode.UPDATE) {
            poJSON = poModel.setTranStat(TransactionStatus.STATE_CANCELLED);

            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }

            poJSON = poModel.saveRecord();
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded to update.");
        }
        return poJSON;
    }
    
    public JSONObject searchTransaction(String fsValue, boolean fbByCode) {
        String lsHeader = "VSP Date»VSP No»Customer»CS No»Plate No»Cancelled";
        String lsColName = "dTransact»sVSPNOxxx»sCompnyNm»sCSNoxxxx»sPlateNox»cTrStatus";
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
}
