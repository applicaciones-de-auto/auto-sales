/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.main.sales;

import java.sql.SQLException;
import java.util.ArrayList;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.controller.sales.Inquiry_Master;
import org.guanzon.auto.controller.sales.Inquiry_Promo;
import org.guanzon.auto.controller.sales.Inquiry_Requirements;
import org.guanzon.auto.controller.sales.Inquiry_Reservation;
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
    Inquiry_Requirements poRequirements;
    Inquiry_Reservation poReservation;
    
    public Inquiry(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poController = new Inquiry_Master(foAppDrver,fbWtParent,fsBranchCd);
        poVehiclePriority = new Inquiry_VehiclePriority(foAppDrver);
        poPromo = new Inquiry_Promo(foAppDrver);
        poRequirements = new Inquiry_Requirements(foAppDrver);
        poReservation = new Inquiry_Reservation(foAppDrver);
        
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
            return poJSON;
        }
        
        poJSON = poPromo.openDetail(fsValue);
        if(!"success".equals(poJSON.get("result"))){
            if(true == (boolean) poJSON.get("continue")){
                poJSON.put("result", "success");
                poJSON.put("message", "Record loaded succesfully.");
            } else {
                pnEditMode = EditMode.UNKNOWN;
                return poJSON;
            } 
        }
        
        poJSON = poRequirements.openDetail(fsValue);
        if(!"success".equals(poJSON.get("result"))){
            if(true == (boolean) poJSON.get("continue")){
                poJSON.put("result", "success");
                poJSON.put("message", "Record loaded succesfully.");
            } else {
                pnEditMode = EditMode.UNKNOWN;
                return poJSON;
            }
        }
        
        poJSON = poReservation.openDetail(fsValue);
        if(!"success".equals(poJSON.get("result"))){
            if(true == (boolean) poJSON.get("continue")){
                poJSON.put("result", "success");
                poJSON.put("message", "Record loaded succesfully.");
            } else {
                pnEditMode = EditMode.UNKNOWN;
                return poJSON;
            }
        }
        
        return poJSON;
    }

    @Override
    public JSONObject updateTransaction() {
        poJSON = new JSONObject();  
        poJSON = poController.updateTransaction();
        if("error".equals(poJSON.get("result"))){
            return poJSON;
        }
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
        
        poJSON =  poRequirements.saveDetail((String) poController.getMasterModel().getTransNo());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        poJSON =  poReservation.saveDetail((String) poController.getMasterModel().getTransNo());
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
    
    public JSONObject loadTestModel() {
        return poController.loadTestModel();
    }
    
    public int getTestModelCount() throws SQLException{
        return poController.getTestModelCount();
    }
    
    public Object getTestModelDetail(int fnRow, int fnIndex) throws SQLException{
        return poController.getTestModelDetail(fnRow, fnIndex);
    }
    
    public Object getTestModelDetail(int fnRow, String fsIndex) throws SQLException{
        return poController.getTestModelDetail(fnRow, fsIndex);
    }
    
    public JSONObject searchVehicle() {
        return poVehiclePriority.searchVehicle();
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
        return poPromo.searchPromo(poController.getMasterModel().getTransactDte());
    }
    
    public ArrayList getPromoList(){return poPromo.getDetailList();}
    public void setPromoList(ArrayList foObj){this.poPromo.setDetailList(foObj);}
    
    public void setPromo(int fnRow, int fnIndex, Object foValue){ poPromo.setDetail(fnRow, fnIndex, foValue);}
    public void setPromo(int fnRow, String fsIndex, Object foValue){ poPromo.setDetail(fnRow, fsIndex, foValue);}
    public Object getPromo(int fnRow, int fnIndex){return poPromo.getDetail(fnRow, fnIndex);}
    public Object getPromo(int fnRow, String fsIndex){return poPromo.getDetail(fnRow, fsIndex);}
    
    public Object addPromo(){ return poPromo.addDetail(poController.getMasterModel().getTransNo());}
    public Object removePromo(int fnRow){ return poPromo.removeDetail(fnRow);}
    
    public JSONObject loadRequirements() {
        return poRequirements.loadRequirements(poController.getMasterModel().getTransNo(), poController.getMasterModel().getPayMode(), poController.getMasterModel().getCustGrp());
    }
    
    public ArrayList getRequirementList(){return poRequirements.getRequirementsList();}
    public void setRequirementList(ArrayList foObj){this.poRequirements.setRequirementsList(foObj);}
    
    public void setRequirement(int fnRow, int fnIndex, Object foValue){ poRequirements.setRequirements(fnRow, fnIndex, foValue);}
    public void setRequirement(int fnRow, String fsIndex, Object foValue){ poRequirements.setRequirements(fnRow, fsIndex, foValue);}
    public Object getRequirement(int fnRow, int fnIndex){return poRequirements.getRequirements(fnRow, fnIndex);}
    public Object getRequirement(int fnRow, String fsIndex){return poRequirements.getRequirements(fnRow, fsIndex);}
    
    
//    public ArrayList getSubRequirementList(){return poRequirements.getDetailList();}
//    public void setSubRequirementList(ArrayList foObj){this.poRequirements.setDetailList(foObj);}
    
//    public void setSubRequirement(int fnRow, int fnIndex, Object foValue){ poRequirements.setDetail(fnRow, fnIndex, foValue);}
//    public void setSubRequirement(int fnRow, String fsIndex, Object foValue){ poRequirements.setDetail(fnRow, fsIndex, foValue);}
//    public Object getSubRequirement(int fnRow, int fnIndex){return poRequirements.getDetail(fnRow, fnIndex);}
//    public Object getSubRequirement(int fnRow, String fsIndex){return poRequirements.getDetail(fnRow, fsIndex);}
    
//    public Object addRequirements(){ return poRequirements.addDetail();} //poController.getMasterModel().getTransNo()
    //public Object removeRequirements(int fnRow){ return poRequirements.removeDetail(fnRow);}
    
    public JSONObject searchEmployee(String fsRqrmtCde, String fsDescript) {
        return poRequirements.searchEmployee(fsRqrmtCde,fsDescript); //,poController.getMasterModel().getTransNo()
    }
    
    public void removeEmployee(String fsRqrmtCde) {
        poRequirements.removeEmployee(fsRqrmtCde);
        
//        JSONObject lObj = new JSONObject();
//        
//        boolean lbExist = false;
//        int lnRow = 0;
//        
//        for (int lnCtr = 0; lnCtr <= poRequirements.getDetailList().size()-1;lnCtr++){
//            if(poRequirements.getDetailList().get(lnCtr).getRqrmtCde().equals(fsRqrmtCde )){
//               lbExist = true;
//               lnRow = lnCtr;
//               break;
//            }
//        }
//
//        if(lbExist){
//            if(((String) poRequirements.getDetail(lnRow, "sTransNox")).isEmpty()){
//                poRequirements.setDetail(lnRow,"sReceived", "");
//                poRequirements.setDetail(lnRow,"sCompnyNm", "");
//                poRequirements.setDetail(lnRow,"dReceived", "");
//                poRequirements.setDetail(lnRow,"cSubmittd", "0");
//            }
//        } 
//        
//        poRequirements.setRequirements(fnRow,"sReceived", "");
//        poRequirements.setRequirements(fnRow,"sCompnyNm", "");
//        poRequirements.setRequirements(fnRow,"dReceived", "");
//        poRequirements.setRequirements(fnRow,"cSubmittd", "0");
//        
        
        
        
    }
    
    public ArrayList getReservationList(){return poReservation.getDetailList();}
    public void setReservationList(ArrayList foObj){this.poReservation.setDetailList(foObj);}
    
    public void setReservation(int fnRow, int fnIndex, Object foValue){ poReservation.setDetail(fnRow, fnIndex, foValue);}
    public void setReservation(int fnRow, String fsIndex, Object foValue){ poReservation.setDetail(fnRow, fsIndex, foValue);}
    public Object getReservation(int fnRow, int fnIndex){return poReservation.getDetail(fnRow, fnIndex);}
    public Object getReservation(int fnRow, String fsIndex){return poReservation.getDetail(fnRow, fsIndex);}
    
    public Object addReservation(){ return poReservation.addDetail("VINQ",poController.getMasterModel().getTransNo(),poController.getMasterModel().getClientID());}
    public Object removeReservation(int fnRow){ return poReservation.removeDetail(fnRow);}
    
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
