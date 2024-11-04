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
import org.guanzon.auto.main.cashiering.CashierReceivables;
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
//    public JSONObject poJSON;
    
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
        JSONObject loJSON = new JSONObject();
        try{
            loJSON = poController.newTransaction();
            
            if("success".equals(loJSON.get("result"))){
                pnEditMode = poController.getEditMode();
            } else {
                pnEditMode = EditMode.UNKNOWN;
            }
               
        }catch(NullPointerException e){
            loJSON.put("result", "error");
            loJSON.put("message", e.getMessage());
            pnEditMode = EditMode.UNKNOWN;
        }
        return loJSON;
    }
    
    @Override
    public JSONObject openTransaction(String fsValue) {
        JSONObject loJSON = new JSONObject();
        
        loJSON = poController.openTransaction(fsValue);
        if("success".equals(loJSON.get("result"))){
            pnEditMode = poController.getEditMode();
        } else {
            pnEditMode = EditMode.UNKNOWN;
        }
        
        loJSON = poVehiclePriority.openDetail(fsValue);
        if(!"success".equals(loJSON.get("result"))){
            pnEditMode = EditMode.UNKNOWN;
            return loJSON;
        }
        
        loJSON = poPromo.openDetail(fsValue);
        if(!"success".equals(loJSON.get("result"))){
            if(true == (boolean) loJSON.get("continue")){
                loJSON.put("result", "success");
                loJSON.put("message", "Record loaded succesfully.");
            } else {
                pnEditMode = EditMode.UNKNOWN;
                return loJSON;
            } 
        }
        
        loJSON = poRequirements.openDetail(fsValue);
        if(!"success".equals(loJSON.get("result"))){
            if(true == (boolean) loJSON.get("continue")){
                loJSON.put("result", "success");
                loJSON.put("message", "Record loaded succesfully.");
            } else {
                pnEditMode = EditMode.UNKNOWN;
                return loJSON;
            }
        }
        
        loJSON = poReservation.openDetail(fsValue,true, false);
        if(!"success".equals(loJSON.get("result"))){
            if(true == (boolean) loJSON.get("continue")){
                loJSON.put("result", "success");
                loJSON.put("message", "Record loaded succesfully.");
            } else {
                pnEditMode = EditMode.UNKNOWN;
                return loJSON;
            }
        }
        
        return loJSON;
    }

    @Override
    public JSONObject updateTransaction() {
        JSONObject loJSON = new JSONObject();  
        loJSON = poController.updateTransaction();
        if("error".equals(loJSON.get("result"))){
            return loJSON;
        }
        pnEditMode = poController.getEditMode();
        return loJSON;
    }

    @Override
    public JSONObject saveTransaction() {
        
        JSONObject loJSON = new JSONObject();  
        
        loJSON = validateEntry();
        if("error".equalsIgnoreCase((String)loJSON.get("result"))){
            return loJSON;
        }
        
        if (!pbWtParent) poGRider.beginTrans();
        
        loJSON =  poController.saveTransaction();
        if("error".equalsIgnoreCase((String)checkData(loJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(loJSON);
        }
        
        poVehiclePriority.setTargetBranchCd(poController.getMasterModel().getBranchCd());
        loJSON =  poVehiclePriority.saveDetail((String) poController.getMasterModel().getTransNo());
        if("error".equalsIgnoreCase((String)checkData(loJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(loJSON);
        }
        
        poPromo.setTargetBranchCd(poController.getMasterModel().getBranchCd());
        loJSON =  poPromo.saveDetail((String) poController.getMasterModel().getTransNo());
        if("error".equalsIgnoreCase((String)checkData(loJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(loJSON);
        }
        
        poRequirements.setTargetBranchCd(poController.getMasterModel().getBranchCd());
        loJSON =  poRequirements.saveDetail((String) poController.getMasterModel().getTransNo());
        if("error".equalsIgnoreCase((String)checkData(loJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(loJSON);
        }
        
        poReservation.setTargetBranchCd(poController.getMasterModel().getBranchCd());
        loJSON =  poReservation.saveDetail((String) poController.getMasterModel().getTransNo());
        if("error".equalsIgnoreCase((String)checkData(loJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(loJSON);
        }
        
        if (!pbWtParent) poGRider.commitTrans();
        
        return loJSON;
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
        JSONObject loJSON = new JSONObject();  
        loJSON = poController.searchTransaction(fsValue, fbByCode);
        if(!"error".equals(loJSON.get("result"))){
            loJSON = openTransaction((String) loJSON.get("sTransNox"));
        }
        return loJSON;
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
    
    public JSONObject searchAvlVhcl(String fsValue) {
        JSONObject loJSON = new JSONObject();
        JSONObject loJSONCheck = new JSONObject();
        
        loJSON = poController.searchAvlVhcl(fsValue);
        
        if(!"error".equals((String) loJSON.get("result"))){
            
            //Check Vehicle Availability
            loJSONCheck = poController.checkVhclAvailability((String) loJSON.get("sSerialID"));
            if(!"error".equals((String) loJSONCheck.get("result"))){
                poController.getMasterModel().setSerialID((String) loJSON.get("sSerialID"));
                poController.getMasterModel().setFrameNo((String) loJSON.get("sFrameNox"));
                poController.getMasterModel().setEngineNo((String) loJSON.get("sEngineNo"));
                poController.getMasterModel().setDescript((String) loJSON.get("sDescript"));
                
                if((String) loJSON.get("sCSNoxxxx") == null){
                    poController.getMasterModel().setCSNo("");
                } else {
                    poController.getMasterModel().setCSNo((String) loJSON.get("sCSNoxxxx"));
                }
                if((String) loJSON.get("sPlateNox") == null){
                    poController.getMasterModel().setPlateNo("");
                } else {
                    poController.getMasterModel().setPlateNo((String) loJSON.get("sPlateNox"));
                }
            } else {
                poController.getMasterModel().setSerialID("");
                poController.getMasterModel().setFrameNo("");
                poController.getMasterModel().setEngineNo("");
                poController.getMasterModel().setCSNo("");
                poController.getMasterModel().setPlateNo("");
                poController.getMasterModel().setDescript("");
                return loJSONCheck;
            }
        } else {
            poController.getMasterModel().setSerialID("");
            poController.getMasterModel().setFrameNo("");
            poController.getMasterModel().setEngineNo("");
            poController.getMasterModel().setCSNo("");
            poController.getMasterModel().setPlateNo("");
            poController.getMasterModel().setDescript("");
        }
        
        return loJSON ;
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
    
    public JSONObject searchEmployee(String fsRqrmtCde, String fsDescript, String fsRequired) {
        return poRequirements.searchEmployee(fsRqrmtCde,fsDescript, fsRequired); //,poController.getMasterModel().getTransNo()
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
    public Inquiry_Reservation getReservationModel(){return poReservation;} 
    
    public void setReservation(int fnRow, int fnIndex, Object foValue){ poReservation.setDetail(fnRow, fnIndex, foValue);}
    public void setReservation(int fnRow, String fsIndex, Object foValue){ poReservation.setDetail(fnRow, fsIndex, foValue);}
    public Object getReservation(int fnRow, int fnIndex){return poReservation.getDetail(fnRow, fnIndex);}
    public Object getReservation(int fnRow, String fsIndex){return poReservation.getDetail(fnRow, fsIndex);}
    
    public Object addReservation(){ return poReservation.addDetail("VINQ",poController.getMasterModel().getTransNo(),poController.getMasterModel().getClientID());}
    public Object removeReservation(int fnRow){ return poReservation.removeDetail(fnRow,true);}
    
    public JSONObject cancelReservation(int fnRow) {
        poReservation.setTargetBranchCd(poController.getMasterModel().getBranchCd());
        return poReservation.cancelReservation(fnRow);
    }
    
    public JSONObject loadReservationList() {
        JSONObject loJSON = new JSONObject();
        loJSON = poReservation.openDetail(poController.getMasterModel().getTransNo(),true,false);
        if(!"success".equals(loJSON.get("result"))){
            if(true == (boolean) loJSON.get("continue")){
                loJSON.put("result", "success");
                loJSON.put("message", "Record loaded succesfully.");
            }
        }
        return loJSON;
    }
    
    /**
     * Load for approval reservation
     * @return 
     */
    public JSONObject loadReservationForApproval(){
        return poReservation.loadForApproval();
    }
    
    /**
     * Reservation Approve
     * @param fnRow selected row of reservation to be approved
     * @return 
     */
    public JSONObject approveReservation(int fnRow){
        JSONObject loJSON = new JSONObject();
        if (!pbWtParent) poGRider.beginTrans();
        
        loJSON = poReservation.approveTransaction(fnRow);
        if("error".equalsIgnoreCase((String) loJSON.get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(loJSON);
        }
        
        if (!pbWtParent) poGRider.commitTrans();
        
         //Save Cashier Receivables
//        if(poController.getMasterModel().getTranStat().equals(TransactionStatus.STATE_CLOSED)){
        CashierReceivables loCAR = new CashierReceivables(poGRider, pbWtParent, psBranchCd);
        JSONObject loJSONCAR = loCAR.generateCAR("VSA", poReservation.getDetailModel(fnRow).getTransNo());
        if("error".equals((String) loJSONCAR.get("result"))){
            return loJSONCAR;
        }
        
        return loJSON;
    }
    
    public ArrayList getInquiryList(){return poController.getDetailList();}
    public Inquiry_Master getInquiryModel(){return poController;} 
    
    /**
     * Load for approval inquiry for VIP Clients
     * @return 
     */
    public JSONObject loadInquiryForApproval(){
        return poController.loadForApproval();
    }
    
    /**
     * VIP Inquiry Approve
     * @param fnRow selected row of Inquiry to be approved
     * @return 
     */
    public JSONObject approveInquiry(int fnRow){
        JSONObject loJSON = new JSONObject();
        if (!pbWtParent) poGRider.beginTrans();
        
        loJSON = poController.approveTransaction(fnRow);
        if("error".equalsIgnoreCase((String) loJSON.get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(loJSON);
        }
        
        if (!pbWtParent) poGRider.commitTrans();
        
        return loJSON;
    }
    
//    public JSONObject loadSelectedReservation(int lnRow) {
//        JSONObject loJSON = new JSONObject();
//        
//        for(int lnCtr = 0; fsTransNo.length() <= lnCtr; lnCtr++){
//            loJSON = poReservation.openDetail(poController.getMasterModel().getTransNo());
//            if(!"success".equals(poJSON.get("result"))){
//                if(true == (boolean) poJSON.get("continue")){
//                    loJSON.put("result", "success");
//                    loJSON.put("message", "Record loaded succesfully.");
//                }
//            }
//        }
//        return loJSON;
//    }
    
    public JSONObject loadBankApplicationList() {
        return poController.loadBankApplicationList();
    }
    
    public int getBankApplicationCount() throws SQLException{
        return poController.getBankApplicationCount();
    }
    
    public Object getBankApplicationDetail(int fnRow, int fnIndex) throws SQLException{
        return poController.getBankApplicationDetail(fnRow, fnIndex);
    }
    
    public Object getBankApplicationDetail(int fnRow, String fsIndex) throws SQLException{
        return poController.getBankApplicationDetail(fnRow, fsIndex);
    }
    
    public JSONObject loadFollowUpList() {
        return poController.loadFollowUpList();
    }
    
    public int getFollowUpCount() throws SQLException{
        return poController.getFollowUpCount();
    }
    
    public Object getFollowUpDetail(int fnRow, int fnIndex) throws SQLException{
        return poController.getFollowUpDetail(fnRow, fnIndex);
    }
    
    public Object getFollowUpDetail(int fnRow, String fsIndex) throws SQLException{
        return poController.getFollowUpDetail(fnRow, fsIndex);
    }
    
    public JSONObject validateEntry() {
        JSONObject loJSON = new JSONObject();
        
        //VALIDATE : Vehicle Priority
        if(poVehiclePriority.getDetailList() == null){
            loJSON.put("result", "error");
            loJSON.put("message", "No Vehicle Priority detected. Please encode vehicle priority.");
            return loJSON;
        }
        
        int lnSize = poVehiclePriority.getDetailList().size() -1;
        if (lnSize < 0){
            loJSON.put("result", "error");
            loJSON.put("message", "No Vehicle Priority detected. Please encode vehicle priority.");
            return loJSON;
        }
        
        boolean lbRqrdChk = false;
        for (int lnCtr = 0; lnCtr <= poRequirements.getRequirementsList().size()-1; lnCtr++){
            if(poRequirements.getRequirementsList().get(lnCtr).getRequired().equals("1")){
                if(poRequirements.getRequirementsList().get(lnCtr).getReceived() != null){
                    if(!poRequirements.getRequirementsList().get(lnCtr).getReceived().trim().isEmpty()){
                        if(poRequirements.getRequirementsList().get(lnCtr).getSubmittd().equals("1")){
                            lbRqrdChk = true;
                            break;
                        }
                    }
                }
            }
        }
        
        //validate atleast 1 required requirements must sent
        if(!poController.getMasterModel().getTranStat().equals("0") && !poController.getMasterModel().getTranStat().equals("6")){
            //Do not validate requirements when VIP Client
            if(poController.getMasterModel().getApprover() != null){
                if(!poController.getMasterModel().getApprover().isEmpty()) {
                    return loJSON;
                }
            } 
            
//            lnSize = poRequirements.getDetailList().size() -1;
//            if (lnSize < 0){
//                loJSON.put("result", "error");
//                loJSON.put("message", "Client must submit atleast 1 required requirement to proceed to on process.\nOtherwise inquiry must be approve for VIP clients.");
//                return loJSON;
//            }

            if(!lbRqrdChk){
                loJSON.put("result", "error");
                loJSON.put("continue", false);
                loJSON.put("message", "Client must submit atleast 1 required requirement to proceed to on process.\nOtherwise inquiry must be approve for VIP clients.");
                return loJSON;
            }
            
        }
        
        if(poController.getMasterModel().getTranStat().equals("6")){
            //If user edited and linked ID / added reservation, update the status into ON PROCESS
            if(lbRqrdChk){
                poController.getMasterModel().setTranStat("1");
                return loJSON;
            }
        }
        
        return loJSON;
    }
    
    public JSONObject checkExistingTransaction(boolean fbisClient) {
        return poController.checkExistingTransaction(fbisClient);
    }
}
