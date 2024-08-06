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
import org.guanzon.auto.controller.sales.Inquiry_Master;
import org.guanzon.auto.controller.sales.Inquiry_Promo;
import org.guanzon.auto.controller.sales.Inquiry_VehiclePriority;
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
        poVehiclePriority = new Inquiry_VehiclePriority(foAppDrver);
        poPromo = new Inquiry_Promo(foAppDrver);
        
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
        
        poJSON = poVehiclePriority.openDetail(fsValue);
        if(!"success".equals(poJSON.get("result"))){
            pnEditMode = EditMode.UNKNOWN;
        }
        
        poJSON = checkData(poPromo.openDetail(fsValue));
        
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
        
        poJSON =  poVehiclePriority.saveDetail((String) poController.getMasterModel().getTransNo());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        poJSON =  poPromo.saveDetail((String) poController.getMasterModel().getTransNo());
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
        return poController.lostSale(fsValue);
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
    public Inquiry_Master getMasterModel() {
        return poController;
    }

    @Override
    public void setTransactionStatus(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public JSONObject searchSalesExecutive(String fsValue) {
        return poController.searchSalesExecutive(fsValue);
    }
    
    public JSONObject searchReferralAgent(String fsValue) {
        return poController.searchReferralAgent(fsValue);
    }
    
    public JSONObject searchClient(String fsValue, boolean fbIsInqClient) {
        return poController.searchClient(fsValue, fbIsInqClient);
    }
    
    public JSONObject searchActivity(String fsValue) {
        return poController.searchActivity(fsValue);
    }
    
    public JSONObject searchOnlinePlatform(String fsValue) {
        return poController.searchOnlinePlatform(fsValue);
    }
    
    public JSONObject searchBranch(String fsValue) {
        return poController.searchBranch(fsValue);
    }
    
    public JSONObject searchVehicle() {
        return poVehiclePriority.searchVehicle(poController.getMasterModel().getTransNo());
    }
    
    public ArrayList getVehiclePriorityList(){return poVehiclePriority.getDetailList();}
    public void setVehiclePriorityList(ArrayList foObj){this.poVehiclePriority.setDetailList(foObj);}
    
    public void setVehiclePriority(int fnRow, int fnIndex, Object foValue){ poVehiclePriority.setDetail(fnRow, fnIndex, foValue);}
    public void setVehiclePriority(int fnRow, String fsIndex, Object foValue){ poVehiclePriority.setDetail(fnRow, fsIndex, foValue);}
    public Object getVehiclePriority(int fnRow, int fnIndex){return poVehiclePriority.getDetail(fnRow, fnIndex);}
    public Object getVehiclePriority(int fnRow, String fsIndex){return poVehiclePriority.getDetail(fnRow, fsIndex);}
    
    public Object addVehiclePriority(){ return poVehiclePriority.addDetail(poController.getMasterModel().getTransNo());}
    public Object removeVehiclePriority(int fnRow){ return poVehiclePriority.removeDetail(fnRow);}
    
    public JSONObject searchPromo() {
        return poPromo.searchPromo(poController.getMasterModel().getTransNo(),poController.getMasterModel().getTransactDte());
    }
    
    public ArrayList getPromoList(){return poPromo.getDetailList();}
    public void setPromoList(ArrayList foObj){this.poPromo.setDetailList(foObj);}
    
    public void setPromo(int fnRow, int fnIndex, Object foValue){ poPromo.setDetail(fnRow, fnIndex, foValue);}
    public void setPromo(int fnRow, String fsIndex, Object foValue){ poPromo.setDetail(fnRow, fsIndex, foValue);}
    public Object getPromo(int fnRow, int fnIndex){return poPromo.getDetail(fnRow, fnIndex);}
    public Object getPromo(int fnRow, String fsIndex){return poPromo.getDetail(fnRow, fsIndex);}
    
    public Object addPromo(){ return poPromo.addDetail(poController.getMasterModel().getTransNo());}
    public Object removePromo(int fnRow){ return poPromo.removeDetail(fnRow);}
    
    public JSONObject validateEntry() {
        JSONObject jObj = new JSONObject();
        
        //VALIDATE : Vehicle Priority
        if(poVehiclePriority.getDetailList() == null){
            jObj.put("result", "error");
            jObj.put("message", "No Vehicle Priority detected. Please encode vehicle priority.");
            return jObj;
        }
        
        int lnSize = poVehiclePriority.getDetailList().size() -1;
        if (lnSize < 0){
            jObj.put("result", "error");
            jObj.put("message", "No Vehicle Priority detected. Please encode vehicle priority.");
            return jObj;
        }
        
        return jObj;
    }
}
