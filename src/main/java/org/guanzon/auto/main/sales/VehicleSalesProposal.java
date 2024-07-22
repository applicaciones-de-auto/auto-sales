/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.main.sales;

import java.util.ArrayList;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.controller.sales.VehicleSalesProposal_Finance;
import org.guanzon.auto.controller.sales.VehicleSalesProposal_Labor;
import org.guanzon.auto.controller.sales.VehicleSalesProposal_Master;
import org.guanzon.auto.controller.sales.VehicleSalesProposal_Parts;
import org.guanzon.auto.model.sales.Model_VehicleSalesProposal_Finance;
import org.guanzon.auto.model.sales.Model_VehicleSalesProposal_Labor;
import org.guanzon.auto.model.sales.Model_VehicleSalesProposal_Parts;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class VehicleSalesProposal implements GTransaction{

    GRider poGRider;
    boolean pbWthParent;
    int pnEditMode;
    String psTranStatus;

    VehicleSalesProposal_Master poMaster;
    VehicleSalesProposal_Finance poVSPFinance;
    VehicleSalesProposal_Labor poVSPLabor;
    VehicleSalesProposal_Parts poVSPParts;
    JSONObject poJSON;
    
    public VehicleSalesProposal(GRider foGRider, boolean fbWthParent) {
        poGRider = foGRider;
        pbWthParent = fbWthParent;

        poMaster = new VehicleSalesProposal_Master(foGRider, fbWthParent);
        pnEditMode = EditMode.UNKNOWN;
    }

    @Override
    public JSONObject newTransaction() {
       poJSON = new JSONObject();
        try{
            pnEditMode = EditMode.ADDNEW;
            org.json.simple.JSONObject obj;

            poMaster.newTransaction();
            
            if (poMaster == null){
                poJSON.put("result", "error");
                poJSON.put("message", "initialized new transaction failed.");
                return poJSON;
            }else{
                addVSPFinance();
                addVSPLabor();
                addVSPParts();
                        
                poJSON.put("result", "success");
                poJSON.put("message", "initialized new transaction.");
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
        
        try {
            poJSON = poMaster.openTransaction(fsValue);
            poJSON = poVSPFinance.openVSPFinance(fsValue);
            poJSON = poVSPLabor.openVSPLabor(fsValue);
            poJSON = poVSPParts.openVSPParts(fsValue);
        }catch(NullPointerException e){
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        
        return poJSON;
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
//        ValidatorInterface validator = ValidatorFactory.make(types,  ValidatorFactory.TYPE.Client_Master, poClient);
//        if (!validator.isEntryOkay()){
//            poJSON.put("result", "error");
//            poJSON.put("message", validator.getMessage());
//            return poJSON;
//        }
        
        if (!pbWthParent) poGRider.beginTrans();
        poJSON =  poMaster.saveTransaction();
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWthParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        poJSON = poVSPFinance.saveVSPFinance(poMaster.getTransNox());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWthParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        poJSON = poVSPLabor.saveVSPLabor(poMaster.getTransNox());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWthParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        poJSON = poVSPParts.saveVSPParts(poMaster.getTransNox());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWthParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        if (!pbWthParent) poGRider.commitTrans();
        
        return poJSON;
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
    public Object getMasterModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject setMaster(int i, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject setMaster(String string, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getEditMode() {
        return pnEditMode;
    }

    @Override
    public void setTransactionStatus(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public JSONObject addVSPFinance(){
        return poVSPFinance.addVSPFinance(poMaster.getTransNox());
    }
    
    public Model_VehicleSalesProposal_Finance getVSPFinance(int fnIndex){
        return poVSPFinance.getVSPFinance(fnIndex);
    }
    
    public ArrayList<Model_VehicleSalesProposal_Finance> getVSPFinanceList(){
        return poVSPFinance.getVSPFinanceList();
    }
    
    public void setVSPFinance(int fnRow, int fnIndex, Object foValue){ poVSPFinance.setVSPFinance(fnRow, fnIndex, foValue); }
    public void setVSPFinance(int fnRow, String fsIndex, Object foValue){  poVSPFinance.setVSPFinance(fnRow, fsIndex, foValue);}
    public Object getVSPFinance(int fnRow, int fnIndex){return poVSPFinance.getVSPFinance( fnRow, fnIndex);}
    public Object getVSPFinance(int fnRow, String fsIndex){return poVSPFinance.getVSPFinance( fnRow, fsIndex);}
    
    public JSONObject addVSPLabor(){
        return poVSPLabor.addVSPLabor(poMaster.getTransNox());
    }
    
    public Model_VehicleSalesProposal_Labor getVSPLabor(int fnIndex){
        return poVSPLabor.getVSPLabor(fnIndex);
    }
    
    public ArrayList<Model_VehicleSalesProposal_Labor> getVSPLaborList(){
        return poVSPLabor.getVSPLaborList();
    }

    public void setVSPLabor(int fnRow, int fnIndex, Object foValue){ poVSPLabor.setVSPLabor(fnRow, fnIndex, foValue); }
    public void setVSPLabor(int fnRow, String fsIndex, Object foValue){  poVSPLabor.setVSPLabor(fnRow, fsIndex, foValue);}
    public Object getVSPLabor(int fnRow, int fnIndex){return poVSPLabor.getVSPLabor( fnRow, fnIndex);}
    public Object getVSPLabor(int fnRow, String fsIndex){return poVSPLabor.getVSPLabor( fnRow, fsIndex);}
    
    public JSONObject addVSPParts(){
        return poVSPParts.addVSPParts(poMaster.getTransNox());
    }
    
    public Model_VehicleSalesProposal_Parts getVSPParts(int fnIndex){
        return poVSPParts.getVSPParts(fnIndex);
    }
    
    public ArrayList<Model_VehicleSalesProposal_Parts> getVSPPartsList(){
        return poVSPParts.getVSPPartsList();
    }

    public void setVSPParts(int fnRow, int fnIndex, Object foValue){ poVSPParts.setVSPParts(fnRow, fnIndex, foValue); }
    public void setVSPParts(int fnRow, String fsIndex, Object foValue){  poVSPParts.setVSPParts(fnRow, fsIndex, foValue);}
    public Object getVSPParts(int fnRow, int fnIndex){return poVSPParts.getVSPParts( fnRow, fnIndex);}
    public Object getVSPParts(int fnRow, String fsIndex){return poVSPParts.getVSPParts( fnRow, fsIndex);}
    
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
}
