/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.main.sales;

import java.util.ArrayList;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GRecord;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.controller.sales.Inquiry_Master;
import org.guanzon.auto.controller.sales.Inquiry_Promo;
import org.guanzon.auto.controller.sales.Inquiry_VehiclePriority;
import org.guanzon.auto.model.sales.Model_Inquiry_Promo;
import org.guanzon.auto.model.sales.Model_Inquiry_VehiclePriority;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Inquiry implements GTransaction{
    final String XML = "Model_Inquiry_Master.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    int pnEditMode;
    String psTransStat;
    String psMessagex;
    public JSONObject poJSON;
    
    Inquiry_Master poController;
    Inquiry_VehiclePriority poVehiclePriority;
    Inquiry_Promo poPromo;
    
    public Inquiry(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poController = new Inquiry_Master(foAppDrver,fbWtParent,fsBranchCd);
        
        poGRider = foAppDrver;
        pbWtParent = fbWtParent;
        psBranchCd = fsBranchCd.isEmpty() ? foAppDrver.getBranchCode() : fsBranchCd;
    }

    @Override
    public int getEditMode() {
        pnEditMode = poController.getEditMode();
        return pnEditMode;
    }
    
    @Override
    public JSONObject setMaster(int fnCol, Object foData) {
        return poController.setMaster(fnCol, foData);
    }

    @Override
    public JSONObject setMaster(String fsCol, Object foData) {
        return poController.setMaster(fsCol, foData);
    }

    public Object getMaster(int fnCol) {
        if(pnEditMode == EditMode.UNKNOWN)
            return null;
        else 
            return poController.getMaster(fnCol);
    }

    public Object getMaster(String fsCol) {
        return poController.getMaster(fsCol);
    }
    
    @Override
    public JSONObject newTransaction() {
        poJSON = new JSONObject();
        try{
            poJSON = poController.newTransaction();
            
            if("success".equals(poJSON.get("result"))){
                pnEditMode = poController.getEditMode();
            } else {
                pnEditMode = EditMode.UNKNOWN;
            }
               
        }catch(NullPointerException e){
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
            pnEditMode = EditMode.UNKNOWN;
        }
        return poJSON;
    }
    
//    public JSONObject addPromo(){
//        return poPromo.addPromo(poController.getTransNox());
//    }
    @Override
    public JSONObject openTransaction(String fsValue) {
        poJSON = new JSONObject();
        
        poJSON = poController.openTransaction(fsValue);
        if("success".equals(poJSON.get("result"))){
            pnEditMode = poController.getEditMode();
        } else {
            pnEditMode = EditMode.UNKNOWN;
        }
        
        return poJSON;
    }

    @Override
    public JSONObject updateTransaction() {
        poJSON = new JSONObject();  
        poJSON = poController.updateTransaction();
        pnEditMode = poController.getEditMode();
        return poJSON;
    }

    @Override
    public JSONObject saveTransaction() {
        
        poJSON = new JSONObject();  
        
//        poJSON = validateEntry();
//        if("error".equalsIgnoreCase((String)poJSON.get("result"))){
//            return poJSON;
//        }
        
        if (!pbWtParent) poGRider.beginTrans();
        
        poJSON =  poController.saveTransaction();
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        if (!pbWtParent) poGRider.commitTrans();
        
        return poJSON;
    }
    
    private JSONObject checkData(JSONObject joValue){
        if(pnEditMode == EditMode.ADDNEW ||pnEditMode == EditMode.READY || pnEditMode == EditMode.UPDATE){
            if(joValue.containsKey("continue")){
                if(true == (boolean)joValue.get("continue")){
                    joValue.put("result", "success");
                    joValue.put("message", "Record saved successfully.");
                }
            }
        }
        return joValue;
    }
    
    public JSONObject searchTransaction(String fsValue, boolean fbByCode) {
        poJSON = new JSONObject();  
        poJSON = poController.searchTransaction(fsValue, fbByCode);
        if(!"error".equals(poJSON.get("result"))){
            poJSON = openTransaction((String) poJSON.get("sTransNox"));
        }
        return poJSON;
    }
    
    public JSONObject lostSale(String fsValue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public void setTransactionStatus(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Model_Inquiry_Promo getPromo(int fnIndex){
        return poPromo.getPromo(fnIndex);
    }
    
    public ArrayList<Model_Inquiry_Promo> getPromoList(){
        return poPromo.getPromoList();
    }
    
    public void setPromo(int fnRow, int fnIndex, Object foValue){ poPromo.setPromo(fnRow, fnIndex, foValue); }
    public void setPromo(int fnRow, String fsIndex, Object foValue){  poPromo.setPromo(fnRow, fsIndex, foValue);}
    public Object getPromo(int fnRow, int fnIndex){return poPromo.getPromo( fnRow, fnIndex);}
    public Object getPromo(int fnRow, String fsIndex){return poPromo.getPromo( fnRow, fsIndex);}
    
//    public JSONObject addVehiclePriority(){
//        return poPromo.addPromo(poController.getTransNox());
//    }
    
    public Model_Inquiry_VehiclePriority getVehiclePriority(int fnIndex){
        return poVehiclePriority.getVehiclePriority(fnIndex);
    }
    
    public ArrayList<Model_Inquiry_VehiclePriority> getVehiclePriorityList(){
        return poVehiclePriority.getVehiclePriorityList();
    }

    public void setVehiclePriority(int fnRow, int fnIndex, Object foValue){ poVehiclePriority.setVehiclePriority(fnRow, fnIndex, foValue); }
    public void setVehiclePriority(int fnRow, String fsIndex, Object foValue){  poVehiclePriority.setVehiclePriority(fnRow, fsIndex, foValue);}
    public Object getVehiclePriority(int fnRow, int fnIndex){return poVehiclePriority.getVehiclePriority( fnRow, fnIndex);}
    public Object getVehiclePriority(int fnRow, String fsIndex){return poVehiclePriority.getVehiclePriority( fnRow, fsIndex);}
    
    
}
