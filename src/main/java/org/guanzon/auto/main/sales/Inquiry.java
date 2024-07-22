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
 * @author MIS-PC
 */
public class Inquiry implements GTransaction{
    final String XML = "Model_Inquiry_Master.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    int pnEditMode;
    String psMessagex;
    public JSONObject poJSON;
    
    Inquiry_Master poMaster;
    Inquiry_VehiclePriority poVehiclePriority;
    Inquiry_Promo poPromo;
    
    public Inquiry(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poGRider = foAppDrver;
        pbWtParent = fbWtParent;
        psBranchCd = fsBranchCd.isEmpty() ? foAppDrver.getBranchCode() : fsBranchCd;
    }

    @Override
    public int getEditMode() {
        return pnEditMode;     
    }
    
    @Override
    public JSONObject setMaster(int fnCol, Object foData) {
        return poMaster.setMaster(fnCol, foData);
    }

    @Override
    public JSONObject setMaster(String fsCol, Object foData) {
        return poMaster.setMaster(fsCol, foData);
    }

    public Object getMaster(int fnCol) {
        return poMaster.getMaster(fnCol);
    }

    public Object getMaster(String fsCol) {
        return poMaster.getMaster(fsCol);
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
                poJSON.put("message", "initialized new record failed.");
                return poJSON;
            }else{
                addPromo();
                addVehiclePriority();
                        
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
    
    public JSONObject addPromo(){
        return poPromo.addPromo(poMaster.getTransNox());
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
    
    public JSONObject addVehiclePriority(){
        return poPromo.addPromo(poMaster.getTransNox());
    }
    
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
    
    @Override
    public JSONObject openTransaction(String fsValue) {
        poJSON = poMaster.openTransaction(fsValue);
        poJSON = poPromo.checkData(poPromo.openPromo(poMaster.getTransNox()));
        poJSON = poVehiclePriority.checkData(poPromo.openPromo(poMaster.getTransNox()));
        return poJSON;
    }

    @Override
    public JSONObject updateTransaction() {
        return poMaster.updateTransaction();
    }

    @Override
    public JSONObject saveTransaction() {
        poJSON = poMaster.saveTransaction();
        
        poJSON =  poPromo.savePromo(poMaster.getTransNox());
        if("error".equalsIgnoreCase((String)poPromo.checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return poPromo.checkData(poJSON);
        }
        
        poJSON =  poVehiclePriority.saveVehiclePriority(poMaster.getTransNox());
        if("error".equalsIgnoreCase((String)poVehiclePriority.checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return poVehiclePriority.checkData(poJSON);
        }
        
        return poJSON;
    }
    
    public JSONObject searchTransaction(String fsValue, boolean fbByCode) {
        return searchInquiry(fsValue, fbByCode);
    }
    
    public JSONObject searchInquiry(String fsValue, boolean fbByCode){
        pnEditMode = EditMode.READY;
        poJSON = new JSONObject();
        String lsValue;
        JSONObject loJSON = poMaster.searchTransaction(fsValue, fbByCode);
        
        System.out.println("loJSON = " + loJSON.toJSONString());
            
        if (poJSON != null && !"error".equals((String) poJSON.get("result"))) {
            System.out.println("sTransNox = " + (String) loJSON.get("sTransNox"));
            lsValue = (String) loJSON.get("sTransNox");
        }else {
            loJSON.put("result", "error");
            loJSON.put("message", "No Inquiry found for: " + fsValue + ".");
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
    public Object getMasterModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setTransactionStatus(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
