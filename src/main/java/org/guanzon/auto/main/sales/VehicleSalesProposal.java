/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.main.sales;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.CachedRowSet;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.constant.TransactionStatus;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.controller.sales.Inquiry_Reservation;
import org.guanzon.auto.controller.sales.VehicleSalesProposal_Finance;
import org.guanzon.auto.controller.sales.VehicleSalesProposal_Labor;
import org.guanzon.auto.controller.sales.VehicleSalesProposal_Master;
import org.guanzon.auto.controller.sales.VehicleSalesProposal_Parts;
import org.guanzon.auto.validator.sales.ValidatorFactory;
import org.guanzon.auto.validator.sales.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class VehicleSalesProposal implements GTransaction{
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    int pnEditMode;
    String psTransStat;
    String psMessagex;
    public JSONObject poJSON;

    VehicleSalesProposal_Master poController;
    VehicleSalesProposal_Finance poVSPFinance;
    VehicleSalesProposal_Labor poVSPLabor;
    VehicleSalesProposal_Parts poVSPParts;
    Inquiry_Reservation poVSPReservation;
    Inquiry_Reservation poOTHReservation;
    
    CachedRowSet poPaymentHstry;
    
    public VehicleSalesProposal(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poController = new VehicleSalesProposal_Master(foAppDrver,fbWtParent,fsBranchCd);
        poVSPFinance = new VehicleSalesProposal_Finance(foAppDrver);
        poVSPLabor = new VehicleSalesProposal_Labor(foAppDrver);
        poVSPParts = new VehicleSalesProposal_Parts(foAppDrver);
        poVSPReservation = new Inquiry_Reservation(foAppDrver);
        poOTHReservation = new Inquiry_Reservation(foAppDrver);
        
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
        
        poJSON = poVSPFinance.openDetail(fsValue);
        if(!"success".equals((String) checkData(poJSON).get("result"))){
            pnEditMode = EditMode.UNKNOWN;
            return poJSON;
        }
        
        poJSON = poVSPLabor.openDetail(fsValue);
        if(!"success".equals((String) checkData(poJSON).get("result"))){
            pnEditMode = EditMode.UNKNOWN;
            return poJSON;
        }
        
        poJSON = poVSPParts.openDetail(fsValue);
        if(!"success".equals((String) checkData(poJSON).get("result"))){
            pnEditMode = EditMode.UNKNOWN;
            return poJSON;
        }
        
        poJSON = poVSPReservation.openDetail(fsValue, false, false);
        if(!"success".equals((String) checkData(poJSON).get("result"))){
            pnEditMode = EditMode.UNKNOWN;
            return poJSON;
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
        
        poJSON = computeAmount();
        if("error".equalsIgnoreCase((String)poJSON.get("result"))){
            return poJSON;
        }
        
        poJSON = validateEntry();
        if("error".equalsIgnoreCase((String)poJSON.get("result"))){
            return poJSON;
        }
        
        if (!pbWtParent) poGRider.beginTrans();
        
        poJSON =  poController.saveTransaction();
        if("error".equalsIgnoreCase((String) poJSON.get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        poVSPFinance.setTargetBranchCd(poController.getMasterModel().getBranchCD());
        poJSON =  poVSPFinance.saveDetail((String) poController.getMasterModel().getTransNo());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        poVSPLabor.setTargetBranchCd(poController.getMasterModel().getBranchCD());
        poJSON =  poVSPLabor.saveDetail((String) poController.getMasterModel().getTransNo());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        poVSPParts.setTargetBranchCd(poController.getMasterModel().getBranchCD());
        poJSON =  poVSPParts.saveDetail((String) poController.getMasterModel().getTransNo());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        for(int lnCtr = 0; lnCtr <= getVSPReservationList().size()-1; lnCtr++){
            poVSPReservation.getDetailModel(lnCtr).setTransID(poController.getMasterModel().getTransNo());
        }
        
        poVSPReservation.setTargetBranchCd(poController.getMasterModel().getBranchCD());
        poJSON =  poVSPReservation.saveDetail("");
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        //Update Inquiry and Vehicle Serial
        JSONObject loJSON = poController.getMasterModel().updateTables();
        if("error".equalsIgnoreCase((String) checkData(loJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return loJSON;
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
    
    /**
     * SEARCH Vehicle Sales Proposal
     * @param fsValue handle the sTranNox when fbByCode is true.
     * @param fbByCode set true when retrieving the actual data else false when browsing only.
     * @param fbUpdate set true when browsing thru Parts Update else false when browsing only.
     * @return 
     */
    public JSONObject searchTransaction(String fsValue, boolean fbByCode, boolean fbUpdate) {
        poJSON = new JSONObject();  
        poJSON = poController.searchTransaction(fsValue, fbByCode, fbUpdate);
        if(!"error".equals(poJSON.get("result"))){
            poJSON = openTransaction((String) poJSON.get("sTransNox"));
        }
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
    public JSONObject cancelTransaction(String fsValue) {
        poJSON =  poController.cancelTransaction(fsValue);
        if(!"error".equals(poJSON.get("result"))){
            //Check inquiry reservation linked
            for(int lnCtr = 0; lnCtr <= getVSPReservationList().size()-1; lnCtr++){
                poVSPReservation.getDetailModel(lnCtr).setTransID("");
            }
            
            poVSPReservation.setTargetBranchCd(poController.getMasterModel().getBranchCD());
            poJSON =  poVSPReservation.saveDetail("");
            if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
                if (!pbWtParent) poGRider.rollbackTrans();
                return checkData(poJSON);
            }
        }
        
        return poJSON;
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
    public VehicleSalesProposal_Master getMasterModel() {
        return poController;
    }

    @Override
    public void setTransactionStatus(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public VehicleSalesProposal_Finance getVSPFinanceModel(){return poVSPFinance;} 
    public ArrayList getVSPFinanceList(){return poVSPFinance.getDetailList();}
    public Object addVSPFinance(){ return poVSPFinance.addDetail(poController.getMasterModel().getTransNo());}
    public Object removeVSPFinance(int fnRow){ return poVSPFinance.removeDetail(fnRow);}
    
    public VehicleSalesProposal_Labor getVSPLaborModel(){return poVSPLabor;} 
    public ArrayList getVSPLaborList(){return poVSPLabor.getDetailList();}
    public Object addVSPLabor(){ return poVSPLabor.addDetail(poController.getMasterModel().getTransNo());}
    public Object removeVSPLabor(int fnRow){ return poVSPLabor.removeDetail(fnRow);}
    
    public VehicleSalesProposal_Parts getVSPPartsModel(){return poVSPParts;} 
    public ArrayList getVSPPartsList(){return poVSPParts.getDetailList();}
    public Object addVSPParts(){ return poVSPParts.addDetail(poController.getMasterModel().getTransNo());}
    public Object removeVSPParts(int fnRow){ return poVSPParts.removeDetail(fnRow);}
    
    /**
     * Load linked reservation into VSP
     * @return 
     */
    public JSONObject loadVSPReservationList() {
        JSONObject loJSON = new JSONObject();
        loJSON = poVSPReservation.openDetail(poController.getMasterModel().getTransNo(),false, false);
        if(!"success".equals(poJSON.get("result"))){
            if(true == (boolean) poJSON.get("continue")){
                loJSON.put("result", "success");
                loJSON.put("message", "Record loaded succesfully.");
            }
        }
        return loJSON;
    }
    
    public Inquiry_Reservation getVSPReservationModel(){return poVSPReservation;} 
    public ArrayList getVSPReservationList(){return poVSPReservation.getDetailList();}
    public JSONObject removeVSPReservation(int fnRow){ 
        JSONObject loJSON = new JSONObject();
        if(poVSPReservation.getDetailModel(fnRow).getSourceNo().equals(poController.getMasterModel().getInqTran())){
            loJSON.put("result", "error");
            loJSON.put("message", "You are not allowed to remove its actual inquiry reservation.");
            return loJSON;
        }

        loJSON = poVSPReservation.removeDetail(fnRow,false);
        computeAmount();
        return loJSON;
    
    }
    
    /**
     * Add other reservation to VSP Other Reservation
     * @param fsRsvTrnNo
     * @param fsTransID
     * @return 
     */
    public JSONObject addToVSPReservation(String fsRsvTrnNo, String fsTransID){ 
        JSONObject loJSON = new JSONObject();
        //Check if reservation already exist
        for(int lnCtr = 0; lnCtr <= getVSPReservationList().size() - 1;lnCtr++){
            if(((String) poVSPReservation.getDetailModel(lnCtr).getTransNo()).equals(fsRsvTrnNo)){
                loJSON.put("result", "error");
                loJSON.put("message", "Reservation already exist.");
                return loJSON;
            }
        }
         
        if(fsTransID != null){
            if(!fsTransID.trim().isEmpty()){
                if(!(fsTransID).equals(poController.getMasterModel().getTransNo())){
                    loJSON.put("result", "error");
                    loJSON.put("message", "Reservation already in use. Add aborted.");
                    return loJSON;
                }
            }
        }
        
        loJSON = poVSPReservation.openRecord(fsRsvTrnNo);
        if("error".equals((String) loJSON.get("result"))){
            removeVSPReservation(getVSPReservationList().size()-1);
            loJSON.put("result", "error");
            loJSON.put("message", "Cannot add other reservation.");
        } else {
            poVSPReservation.getDetailModel(getVSPReservationList().size()-1).setTransID(poController.getMasterModel().getTransNo());
        }
        return loJSON;
    }
    
    /**
     * Load linked reservation into VSP
     * @return 
     */
    public JSONObject loadOTHReservationList() {
        JSONObject loJSON = new JSONObject();
        if(poController.getMasterModel().getInqTran() == null) {
            loJSON.put("result","error");
            loJSON.put("message", "Select Inquiry first.");
            return loJSON;
        } else {
            if(poController.getMasterModel().getInqTran().trim().isEmpty()){
                loJSON.put("result","error");
                loJSON.put("message", "Select Inquiry first.");
                return loJSON;
            }
        }
        
        loJSON = poOTHReservation.openDetail(poController.getMasterModel().getInqTran(),true, true);
        if(!"success".equals(poJSON.get("result"))){
            if(true == (boolean) poJSON.get("continue")){
                loJSON.put("result", "success");
                loJSON.put("message", "Record loaded succesfully.");
            }
        }
        return loJSON;
    }
    
    public Inquiry_Reservation getOTHReservationModel(){return poOTHReservation;} 
    public ArrayList getOTHReservationList(){return poOTHReservation.getDetailList();}
    
    public JSONObject searchInquiry(String fsValue, boolean fbByCode){
        JSONObject loJSON = new JSONObject();
        JSONObject loJSONRsv = new JSONObject();
        loJSON = poController.searchInquiry(fsValue, fbByCode);
        if(!"error".equals((String) loJSON.get("result"))){
            //Buying Customer Default         
            poController.getMasterModel().setInqTran((String) loJSON.get("sTransNox"));          
            poController.getMasterModel().setInqryID((String) loJSON.get("sInqryIDx"));                                                        
            poController.getMasterModel().setInqryDte(SQLUtil.toDate((String) loJSON.get("dTransact"), SQLUtil.FORMAT_SHORT_DATE));            
            System.out.println(getMasterModel().getMasterModel().getInqryDte()); 
            
            poController.getMasterModel().setClientID((String) loJSON.get("sClientID"));                                                        
            poController.getMasterModel().setBuyCltNm((String) loJSON.get("sCompnyNm"));                                                        
            poController.getMasterModel().setAddress(((String) loJSON.get("sAddressx")).trim());                                                         
            poController.getMasterModel().setPayMode((String) loJSON.get("cPayModex"));                                                         
            poController.getMasterModel().setIsVhclNw((String) loJSON.get("cIsVhclNw"));  
            
            //Clear reservation details
            poOTHReservation.resetDetail();
            poVSPReservation.resetDetail();
            
            if((String) loJSON.get("nAmountxx") == null){                                                                                      
                poController.getMasterModel().setResrvFee(new BigDecimal("0.00"));                                                             
            } else {   
                //Automatically add reservation to VSP reservation list
                loJSONRsv = poOTHReservation.openDetail(poController.getMasterModel().getInqTran(),true, false);
                if(!"success".equals(loJSONRsv.get("result"))){
                    if(true == (boolean) loJSONRsv.get("continue")){
                        loJSONRsv.put("result", "success");
                        loJSONRsv.put("message", "Record loaded succesfully.");
                    }
                }
                
                String lsTransID = "";
                for(int lnCtr = 0; lnCtr <= poOTHReservation.getDetailList().size() - 1; lnCtr++){
                    //check for approval
                    if(poOTHReservation.getDetailModel(lnCtr).getTranStat().equals("2")){
                        if(poOTHReservation.getDetailModel(lnCtr).getTransID() != null){
                            lsTransID = poOTHReservation.getDetailModel(lnCtr).getTransID();
                        }
                        //check for payment
                        if(lsTransID.isEmpty()){
                            if(poOTHReservation.getDetailModel(lnCtr).getSINo() != null){
                                if(!poOTHReservation.getDetailModel(lnCtr).getSINo().trim().isEmpty()){
                                    addToVSPReservation(poOTHReservation.getDetailModel(lnCtr).getTransNo(),poOTHReservation.getDetailModel(lnCtr).getTransID());
                                    computeAmount();
                                }
                            }
                        }
                        
                    }
                }
            }  
            
            //Automatically add row for vsp finance when payment mode is not cash
            if(!poController.getMasterModel().getPayMode().equals("0")){
                addVSPFinance();
            }
                                                                                                                                               
            //Inquiring Customer                                                                                      
            poController.getMasterModel().setInqCltID((String) loJSON.get("sClientID"));                                                       
            poController.getMasterModel().setInqCltNm((String) loJSON.get("sCompnyNm"));                                                       
            poController.getMasterModel().setInqCltTp((String) loJSON.get("cClientTp"));                                                       
            poController.getMasterModel().setSourceCD((String) loJSON.get("sSourceCD"));                                                       
            poController.getMasterModel().setSourceNo((String) loJSON.get("sSourceNo"));                                                       
            poController.getMasterModel().setPlatform((String) loJSON.get("sPlatform"));                                                       
            poController.getMasterModel().setAgentID((String) loJSON.get("sAgentIDx"));                                                        
            poController.getMasterModel().setAgentNm((String) loJSON.get("sSalesAgn"));                                                        
            poController.getMasterModel().setEmployID((String) loJSON.get("sEmployID"));                                                       
            poController.getMasterModel().setSEName((String) loJSON.get("sSalesExe"));                                                         
            poController.getMasterModel().setContctNm((String) loJSON.get("sContctNm"));                                                       
            poController.getMasterModel().setBranchCD((String) loJSON.get("sBranchCd"));                                                       
            poController.getMasterModel().setBranchNm((String) loJSON.get("sBranchNm"));       
            
        } else {                                                                                                                               
            //Buying Customer Default                                                                                                          
            poController.getMasterModel().setClientID("");                                                                                     
            poController.getMasterModel().setBuyCltNm("");                                                                                     
            poController.getMasterModel().setAddress("");                                                                                      
            poController.getMasterModel().setPayMode("");                                                                                      
            poController.getMasterModel().setIsVhclNw("");                                                                                     
            poController.getMasterModel().setResrvFee(new BigDecimal("0.00"));                                                                 
                                                                                                                                               
            //Inquiring Customer                                                                                                               
            poController.getMasterModel().setInqryID("");                                                                                      
            poController.getMasterModel().setInqryDte(SQLUtil.toDate("1900-01-01", SQLUtil.FORMAT_SHORT_DATE));                                
            System.out.println(poController.getMasterModel().getInqryDte());                                                               
            poController.getMasterModel().setInqCltID("");                                                                                     
            poController.getMasterModel().setInqCltNm("");                                                                                     
            poController.getMasterModel().setInqCltTp("");                                                                                     
            poController.getMasterModel().setSourceCD("");                                                                                     
            poController.getMasterModel().setSourceNo("");                                                                                     
            poController.getMasterModel().setPlatform("");                                                                                     
            poController.getMasterModel().setAgentID("");                                                                                      
            poController.getMasterModel().setAgentNm("");                                                                                      
            poController.getMasterModel().setEmployID("");                                                                                     
            poController.getMasterModel().setSEName("");                                                                                       
            poController.getMasterModel().setContctNm("");                                                                                     
            poController.getMasterModel().setBranchCD("");                                                                                     
            poController.getMasterModel().setBranchNm("");      
        }
        
        
        return loJSON;
    }
    
    public JSONObject searchClient(String fsValue, boolean fbBuyClient){
        JSONObject loJSON = new JSONObject();
        loJSON = poController.searchClient(fsValue,fbBuyClient);
        if(!"error".equals((String) loJSON.get("result"))){
            if(fbBuyClient){
                poController.getMasterModel().setClientID((String) loJSON.get("sClientID"));
                poController.getMasterModel().setBuyCltNm((String) loJSON.get("sCompnyNm"));
                poController.getMasterModel().setClientTp((String) loJSON.get("cClientTp"));
                poController.getMasterModel().setAddress((String) loJSON.get("sAddressx"));
            } else {
                poController.getMasterModel().setCoCltID((String) loJSON.get("sClientID"));
                poController.getMasterModel().setCoCltNm((String) loJSON.get("sCompnyNm"));
            }
        } else {
            if(fbBuyClient){
                poController.getMasterModel().setClientID("");
                poController.getMasterModel().setBuyCltNm("");
                poController.getMasterModel().setClientTp("");
                poController.getMasterModel().setAddress("");
            } else {
                poController.getMasterModel().setCoCltID("");
                poController.getMasterModel().setCoCltNm("");
            }
        }
        return loJSON;
    }
    
    /**
     * Search Available Vehicle
     * @param fsValue the value of the field
     * @param fsFindBy set CS when search thru CSNO, set PLT when search thru PLATE NO, set ENG when search thru ENGINE NO, set FRM when search thru FRAME NO,
     * @return 
     */
    public JSONObject searchAvlVhcl(String fsValue, String fsFindBy) {
        JSONObject loJSON = new JSONObject();
        JSONObject loJSONCheck = new JSONObject();
        
        loJSON = poController.searchAvlVhcl(fsValue, fsFindBy);
        if(!"error".equals((String) loJSON.get("result"))){
            //Check Vehicle Availability
            loJSONCheck = poController.checkVhclAvailability((String) loJSON.get("sSerialID"));
            if(!"error".equals((String) loJSONCheck.get("result"))){
                poController.getMasterModel().setSerialID((String) loJSON.get("sSerialID"));
                poController.getMasterModel().setFrameNo((String) loJSON.get("sFrameNox"));
                poController.getMasterModel().setEngineNo((String) loJSON.get("sEngineNo"));
                poController.getMasterModel().setVhclDesc((String) loJSON.get("sDescript"));
                poController.getMasterModel().setKeyNo((String) loJSON.get("sKeyNoxxx"));
    //            poController.getMasterModel().setCSNo((String) loJSON.get("sCSNoxxxx"));
    //            poController.getMasterModel().setPlateNo((String) loJSON.get("sPlateNox"));

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
                poController.getMasterModel().setVhclDesc("");
                poController.getMasterModel().setKeyNo("");
                return loJSONCheck;
            }
        } else {
            poController.getMasterModel().setSerialID("");
            poController.getMasterModel().setFrameNo("");
            poController.getMasterModel().setEngineNo("");
            poController.getMasterModel().setCSNo("");
            poController.getMasterModel().setPlateNo("");
            poController.getMasterModel().setVhclDesc("");
            poController.getMasterModel().setKeyNo("");
        }
        
        return loJSON;
    }
    
    public JSONObject searchBankApp(String fsValue){
        JSONObject loJSON = new JSONObject();
        loJSON = poController.searchBankApp(fsValue);
        if(!"error".equals((String) loJSON.get("result"))){
            poController.getMasterModel().setBnkAppCD((String) loJSON.get("sTransNox"));
            poController.getMasterModel().setBankName((String) loJSON.get("sBankName"));
            poController.getMasterModel().setBrBankNm((String) loJSON.get("sBrBankNm"));
            
            poVSPFinance.getVSPFinanceModel().setBankID((String) loJSON.get("sBrBankID"));
            poVSPFinance.getVSPFinanceModel().setBankname((String) loJSON.get("sBankName") + " " + (String) loJSON.get("sBrBankNm"));
        } else {
            poController.getMasterModel().setBnkAppCD("");
            poController.getMasterModel().setBankName("");
            poController.getMasterModel().setBrBankNm("");
        }
        return loJSON;
    }
    
    public JSONObject searchInsurance(String fsValue, boolean fbisTPL){
        JSONObject loJSON = new JSONObject();
        loJSON = poController.searchInsurance(fsValue);
        if(!"error".equals((String) loJSON.get("result"))){
            if(fbisTPL){
                poController.getMasterModel().setInsTplCd((String) loJSON.get("sBrInsIDx"));
                poController.getMasterModel().setTPLInsNm((String) loJSON.get("sInsurNme"));
                poController.getMasterModel().setTPLBrIns((String) loJSON.get("sBrInsNme"));
            } else {
                poController.getMasterModel().setInsCode((String) loJSON.get("sBrInsIDx"));
                poController.getMasterModel().setCOMInsNm((String) loJSON.get("sInsurNme"));
                poController.getMasterModel().setCOMBrIns((String) loJSON.get("sBrInsNme"));
            }
            
//            System.out.println("Insurance Compre Main : "+ poController.getMasterModel().getCOMInsNm() );
//            System.out.println("Insurance Compre Branch : "+ poController.getMasterModel().getCOMBrIns() );
        } else {
            if(fbisTPL){
                poController.getMasterModel().setInsTplCd("");
                poController.getMasterModel().setTPLInsNm("");
                poController.getMasterModel().setTPLBrIns("");
            } else {
                poController.getMasterModel().setInsCode("");
                poController.getMasterModel().setCOMInsNm("");
                poController.getMasterModel().setCOMBrIns("");
            }
        }
        return loJSON;
    }
    
    public JSONObject searchLabor(String fsValue, int fnRow, boolean withUI){
        JSONObject loJSON = new JSONObject();
        loJSON = poVSPLabor.searchLabor(fsValue, withUI);
        if(!"error".equals((String) loJSON.get("result"))){
            //check exisiting labor
            for(int lnCtr = 0; lnCtr <= getVSPLaborList().size()-1; lnCtr++){
                if(((String) loJSON.get("sLaborCde")).equals(poVSPLabor.getDetailModel(lnCtr).getLaborCde())){
                    loJSON.put("result", "error");
                    loJSON.put("message", "Labor " + (String) loJSON.get("sLaborDsc") + " already exist. Add labor aborted.");
                    return loJSON;
                }
            }
            
            poVSPLabor.getDetailModel(fnRow).setLaborCde((String) loJSON.get("sLaborCde"));
            poVSPLabor.getDetailModel(fnRow).setLaborDsc((String) loJSON.get("sLaborDsc"));
        } else {
            poVSPLabor.getDetailModel(fnRow).setLaborCde("");
            poVSPLabor.getDetailModel(fnRow).setLaborDsc("");
        }
        
        System.out.println("CLASS LABOR CODE : " + getVSPLaborModel().getDetailModel(fnRow).getLaborCde());
        return loJSON;
    }
    
    public JSONObject searchParts(String fsValue, int fnRow, boolean withUI){
        JSONObject loJSON = new JSONObject();
        loJSON = poVSPParts.searchParts(fsValue);
        if(!"error".equals((String) loJSON.get("result"))){
            //check exisiting part
            for(int lnCtr = 0; lnCtr <= getVSPPartsList().size()-1; lnCtr++){
                if(((String) loJSON.get("sStockIDx")).equals(poVSPParts.getDetailModel(lnCtr).getStockID())){
                    loJSON.put("result", "error");
                    loJSON.put("message", "Part No. " + (String) loJSON.get("sBarCodex") + " already exist. Part number update aborted.");
                    return loJSON;
                }
            }
            
            poVSPParts.getDetailModel(fnRow).setStockID((String) loJSON.get("sStockIDx"));
            poVSPParts.getDetailModel(fnRow).setBarCode((String) loJSON.get("sBarCodex"));
            poVSPParts.getDetailModel(fnRow).setPartDesc((String) loJSON.get("sDescript"));
            poVSPParts.getDetailModel(fnRow).setUnitPrce(new BigDecimal((String) loJSON.get("nUnitPrce")));
        } else {
            poVSPParts.getDetailModel(fnRow).setStockID("");
            poVSPParts.getDetailModel(fnRow).setBarCode("");
            poVSPParts.getDetailModel(fnRow).setPartDesc("");
            poVSPParts.getDetailModel(fnRow).setUnitPrce(new BigDecimal("0.00"));
        }
        return loJSON;
    }
    
    /**
     * Check VSP Parts Quantity linked to JO
     * @param fsValue parts Stock ID
     * @param fnInputQty parts quantity to be input
     * @param fnRow
    */
    public JSONObject checkVSPJOParts(String fsValue, int fnInputQty, int fnRow) {
        return poVSPParts.checkVSPJOParts(fsValue, fnInputQty, fnRow);
    }
    
    /**
     * Compute amounts on VSP Transaction.
     * This method performs the computation of amount that has been input to the VSP Record.
     * 
    */
    public JSONObject computeAmount() {
        JSONObject loJSON = new JSONObject();
        String lsPayModex = (String) getMaster("cPayModex");
        String lsQty = ""; 
        int lnCtr;
        
        BigDecimal ldblResrvAmt = new BigDecimal("0.00");
        BigDecimal ldblLaborAmt = new BigDecimal("0.00"); 
        BigDecimal ldblLaborDsc = new BigDecimal("0.00"); 
        BigDecimal ldblAccesAmt = new BigDecimal("0.00"); 
        BigDecimal ldblAccesDsc = new BigDecimal("0.00"); 
        BigDecimal ldblPartsAmt = new BigDecimal("0.00"); 
        BigDecimal ldblFinAmt = new BigDecimal("0.00");
        
        /*Compute Reservation Total*/
        for (lnCtr = 0; lnCtr <= getVSPReservationList().size()-1; lnCtr++){
            ldblResrvAmt = ldblResrvAmt.add(poVSPReservation.getDetailModel(lnCtr).getTranAmt());
        }
        
        poController.getMasterModel().setResrvFee(ldblResrvAmt);
        
        /*Compute Labor Total*/
        for (lnCtr = 0; lnCtr <= getVSPLaborList().size()-1; lnCtr++){
            //Net Lab Amount = Labor amount - Labor discount;
            poVSPLabor.getDetailModel(lnCtr).setNtLabAmt(poVSPLabor.getDetailModel(lnCtr).getLaborAmt().subtract(poVSPLabor.getDetailModel(lnCtr).getLaborDscount()));
            
            ldblLaborAmt = ldblLaborAmt.add(poVSPLabor.getDetailModel(lnCtr).getLaborAmt()).setScale(2, BigDecimal.ROUND_HALF_UP);
            ldblLaborDsc = ldblLaborDsc.add(poVSPLabor.getDetailModel(lnCtr).getLaborDscount()).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        
        /*Compute Parts Total*/
        for (lnCtr = 0; lnCtr <= getVSPPartsList().size()-1; lnCtr++){
            
            lsQty = String.valueOf(poVSPParts.getDetailModel(lnCtr).getQuantity());
            ldblPartsAmt = new BigDecimal(lsQty).multiply(poVSPParts.getDetailModel(lnCtr).getSelPrice());
            //Net Parts Amount = (parts amount * qty) - parts discount;
            poVSPParts.getDetailModel(lnCtr).setNtPrtAmt(ldblPartsAmt.subtract(poVSPParts.getDetailModel(lnCtr).getPartsDscount()));
            System.out.println(" ROW "+ lnCtr + " total amount >> " + poVSPParts.getDetailModel(lnCtr).getNtPrtAmt());
            
            ldblAccesAmt = ldblAccesAmt.add(ldblPartsAmt);
            ldblAccesDsc = ldblAccesDsc.add(poVSPParts.getDetailModel(lnCtr).getPartsDscount()); //.setScale(2, BigDecimal.ROUND_HALF_UP);
            lsQty = "";
        }
        
        //Amount to be Pay
        BigDecimal ldblUnitPrce = poController.getMasterModel().getUnitPrce(); 
        BigDecimal ldblDownPaym = poController.getMasterModel().getDownPaym(); 
        
        BigDecimal ldblAdvDwPmt = poController.getMasterModel().getAdvDwPmt(); //OMA/CMF
        BigDecimal ldblTPLAmtxx = poController.getMasterModel().getTPLAmt(); 
        BigDecimal ldblCompAmtx = poController.getMasterModel().getCompAmt();
        BigDecimal ldblLTOAmtxx = poController.getMasterModel().getLTOAmt();
        BigDecimal ldblChmoAmtx = poController.getMasterModel().getChmoAmt();
        BigDecimal ldblFrgtChrg = poController.getMasterModel().getFrgtChrg();
        BigDecimal ldblOthrChrg = poController.getMasterModel().getOthrChrg(); //MISC
        ldblLaborAmt = ldblLaborAmt.setScale(2, BigDecimal.ROUND_HALF_UP); //TOTAL LABOR
        ldblAccesAmt = ldblAccesAmt.setScale(2, BigDecimal.ROUND_HALF_UP); //TOTAL PARTS
        
        //Discounted Amount  
        ldblLaborDsc = ldblLaborDsc.setScale(2, BigDecimal.ROUND_HALF_UP);
        ldblAccesDsc = ldblAccesDsc.setScale(2, BigDecimal.ROUND_HALF_UP);
        BigDecimal ldblFleetDsc = poController.getMasterModel().getFleetDsc();
        BigDecimal ldblSPFltDsc = poController.getMasterModel().getSPFltDsc();
        BigDecimal ldblPromoDsc = poController.getMasterModel().getPromoDsc();
        BigDecimal ldblAddlDscx = poController.getMasterModel().getAddlDsc();
        BigDecimal ldblBndleDsc = poController.getMasterModel().getBndleDsc();
        BigDecimal ldblInsurDsc = new BigDecimal("0.00");  // poController.getMasterModel().getInsurDsc();
        BigDecimal ldblTranTotl = new BigDecimal("0.00"); 
        BigDecimal ldblNetTTotl = new BigDecimal("0.00"); 
        BigDecimal ldblDiscTotl= new BigDecimal("0.00"); 
        
        //CASH
        if (lsPayModex.equals("0")){ 
            //vsptotal = nUnitPrce + instpl + inscomp + lto  + chmo + freightchage + miscamt + omacmf + labtotal + partstotal //gross vsp total;
            ldblTranTotl = ldblUnitPrce.add(ldblTPLAmtxx).add(ldblCompAmtx).add(ldblLTOAmtxx).add(ldblChmoAmtx).add(ldblFrgtChrg).add(ldblOthrChrg).add(ldblAdvDwPmt).add(ldblLaborAmt).add(ldblAccesAmt);
            ldblTranTotl = ldblTranTotl.setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            //vsptotal = downpayment + instpl + inscomp + lto  + chmo + freightchage + miscamt + omacmf + labtotal + partstotal //gross vsp total;
            ldblTranTotl = ldblDownPaym.add(ldblTPLAmtxx).add(ldblCompAmtx).add(ldblLTOAmtxx).add(ldblChmoAmtx).add(ldblFrgtChrg).add(ldblOthrChrg).add(ldblAdvDwPmt).add(ldblLaborAmt).add(ldblAccesAmt);
            ldblTranTotl = ldblTranTotl.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        
        //vsptotal = vsptotal - (cashdisc + promodisc + stdfleetdisc + splfleet disc + bundledisc)  //gross vsp total less discounts and other deductibles
        ldblDiscTotl = ldblAddlDscx.add(ldblPromoDsc).add(ldblFleetDsc).add(ldblSPFltDsc).add(ldblBndleDsc).add(ldblLaborDsc).add(ldblAccesDsc).add(ldblInsurDsc);
        ldblDiscTotl = ldblDiscTotl.setScale(2, BigDecimal.ROUND_HALF_UP);
        pdblDiscTotl = ldblDiscTotl;
        
        //ldblTranTotl = ldblTranTotl.subtract(ldblDiscTotl);
        ldblTranTotl = ldblTranTotl.setScale(2, BigDecimal.ROUND_HALF_UP);
//        if (ldblTranTotl.compareTo(new BigDecimal("0.00")) < 0){
//            loJSON.put("result", "error");
//            loJSON.put("message", "Invalid Gross Amount Total: " + ldblTranTotl + " . ");
//            return loJSON;
//        }
        
        //TODO: Compute total amount paid of client made
        computeTotlAmtPaid();
        //Paid Amount
        BigDecimal ldblAmtPaidTotl = poController.getMasterModel().getAmtPaid();
        BigDecimal ldblResrvFee = poController.getMasterModel().getResrvFee();
        
        //Net Amount Due = vsp total -(rfee + dwntotal + otherpayment) 
        //To be continued no computation yet from receipt -jahn 09162023
        ldblNetTTotl = ldblTranTotl.subtract(ldblResrvFee.add(ldblAmtPaidTotl).add(ldblDiscTotl));
        ldblNetTTotl = ldblNetTTotl.setScale(2, BigDecimal.ROUND_HALF_UP);
//        if (ldblNetTTotl.compareTo(new BigDecimal("0.00")) < 0){
//            loJSON.put("result", "error");
//            loJSON.put("message", "Invalid Net Amount Due: " + ldblNetTTotl + " . ");
//            return loJSON;
//        }
        
        poController.getMasterModel().setTranTotl(ldblTranTotl);
        poController.getMasterModel().setNetTTotl(ldblNetTTotl);
        poController.getMasterModel().setLaborAmt(ldblLaborAmt);
        poController.getMasterModel().setAccesAmt(ldblAccesAmt);
        poController.getMasterModel().setToLabDsc(ldblLaborDsc);
        poController.getMasterModel().setToPrtDsc(ldblAccesDsc);
        
        //PO / FINANCING
        if (!lsPayModex.equals("0")){ 
            if (getVSPFinanceList().size()-1 >= 0){
                BigDecimal ldblRatexx = new BigDecimal("0.00"); 
                BigDecimal ldblMonAmort = new BigDecimal("0.00"); 
                BigDecimal ldblGrsMonth = new BigDecimal("0.00"); 
                BigDecimal ldblPNValuex = new BigDecimal("0.00"); 
                
//                BigDecimal ldblFinanceDownPaym = poController.getMasterModel().getDownPaym();
                BigDecimal ldblFinanceUnitPrce = ldblUnitPrce.setScale(2, BigDecimal.ROUND_HALF_UP);
                
                BigDecimal ldblNtDwnPmt = getVSPFinanceModel().getVSPFinanceModel().getNtDwnPmt();
                BigDecimal ldblDiscount = getVSPFinanceModel().getVSPFinanceModel().getDiscount();
                BigDecimal ldblRebatesx = getVSPFinanceModel().getVSPFinanceModel().getRebates(); //Prompt Payment Discount
                
                int lnAcctTerm = getVSPFinanceModel().getVSPFinanceModel().getAcctTerm(); 
                String lsAcctRate = String.valueOf( getVSPFinanceModel().getVSPFinanceModel().getAcctRate()); 
                BigDecimal ldblAcctRate = new BigDecimal("0.00");
                if(lsAcctRate != null && !lsAcctRate.equals("null")){
                    ldblAcctRate = new BigDecimal(lsAcctRate);
                }
                
                //-Amount Financed = nUnitPrce -(nDiscount + nNtDwnPmt)
                ldblFinAmt = ldblFinanceUnitPrce.subtract(ldblDiscount.add(ldblNtDwnPmt));
                ldblFinAmt = ldblFinAmt.setScale(2, BigDecimal.ROUND_HALF_UP); 
//                if (ldblFinAmt.compareTo(new BigDecimal("0.00")) < 0){
//                    loJSON.put("result", "error");
//                    loJSON.put("message", "Invalid Amount Finance : " + ldblFinAmt + " . ");
//                    return loJSON;
//                }
                
                //-Rate = (nAcctRate/100) + 1
                //ldblRatexx = (ldblAcctRate / 100) + 1; 
                //ldblRatexx = (ldblAcctRate.divide(100)).add(new BigDecimal("1"));
                if (ldblAcctRate.compareTo(BigDecimal.ZERO) != 0) {
                    ldblRatexx = (ldblAcctRate.divide(new BigDecimal("100")));
                    //ldblRatexx = ldblRatexx.setScale(2, BigDecimal.ROUND_HALF_UP); 
                    ldblRatexx = ldblRatexx.add(new BigDecimal("1"));
                    //ldblRatexx = ldblRatexx.setScale(2, BigDecimal.ROUND_HALF_UP); 
                }
                
                System.out.println("ldblRatexx : " + ldblRatexx);
                System.out.println("ldblFinAmt : " + ldblFinAmt);
                
                //-net Monthly Inst = (Amount Financed * Rate)/Terms Rate
                //ldblMonAmort = (ldblFinAmt * ldblRatexx) / lnAcctTerm; 
                if (lnAcctTerm > 0) {
                    ldblMonAmort = (ldblFinAmt.multiply(ldblRatexx));
                    ldblMonAmort = ldblMonAmort.setScale(2, BigDecimal.ROUND_HALF_UP); 
                    System.out.println("ldblMonAmort : " + ldblMonAmort);
                    
                    //ldblMonAmort = ldblMonAmort.divide(new BigDecimal(String.valueOf(lnAcctTerm)), 2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal lBgDcmTerm = new BigDecimal(String.valueOf(lnAcctTerm));
                    ldblMonAmort = ldblMonAmort.divide(lBgDcmTerm, RoundingMode.HALF_UP);
                    ldblMonAmort = ldblMonAmort.setScale(2, BigDecimal.ROUND_HALF_UP); 
                    System.out.println("lnAcctTerm : " + lnAcctTerm);
                    System.out.println("ldblMonAmort : " + ldblMonAmort);
                }
                
                //-Gross Monthly Inst = Net Monthly Inst + Prompt Payment Disc
                //ldblGrsMonth = ldblMonAmort + ldblRebatesx; 
                ldblGrsMonth = ldblMonAmort.add(ldblRebatesx); 
                ldblGrsMonth = ldblGrsMonth.setScale(2, BigDecimal.ROUND_HALF_UP); 
//                if (ldblGrsMonth.compareTo(new BigDecimal("0.00")) < 0){
//                    loJSON.put("result", "error");
//                    loJSON.put("message", "Invalid Gross Monthly Installment: " + ldblGrsMonth + " . ");
//                    return loJSON;
//                }
                
                //-Promisory Note Amount =Terms Rate * Gross Monthly Inst
                //ldblPNValuex = lnAcctTerm * ldblGrsMonth; 
                ldblPNValuex = ldblGrsMonth.multiply(new BigDecimal(String.valueOf(lnAcctTerm))); 
                ldblPNValuex = ldblPNValuex.setScale(2, BigDecimal.ROUND_HALF_UP); 
//                if (ldblPNValuex.compareTo(new BigDecimal("0.00")) < 0){
//                    loJSON.put("result", "error");
//                    loJSON.put("message", "Invalid Promissory Note Amount: " + ldblPNValuex + " . ");
//                    return loJSON;
//                }
                
                getVSPFinanceModel().getVSPFinanceModel().setFinAmt(ldblFinAmt); 
                getVSPFinanceModel().getVSPFinanceModel().setMonAmort(ldblMonAmort); 
                getVSPFinanceModel().getVSPFinanceModel().setGrsMonth(ldblGrsMonth); 
                getVSPFinanceModel().getVSPFinanceModel().setPNValue(ldblPNValuex);
                
                System.out.println("ldblRebatesx : " + ldblRebatesx);
                System.out.println("ldblMonAmort : " + ldblMonAmort);
                System.out.println("ldblGrsMonth : " + ldblGrsMonth);
                System.out.println("lnAcctTerm : " + lnAcctTerm);
                System.out.println("ldblPNValuex : " + ldblPNValuex);
                
                //Compute Dealer and Sales Executive Incentives
                Double ldblDIRate = 0.00;
                if(poController.getMasterModel().getDealrRte() != null){
                    ldblDIRate = poController.getMasterModel().getDealrRte() / 100;
                    poController.getMasterModel().setDealrAmt((new BigDecimal(ldblDIRate).multiply(ldblFinAmt)).setScale(2, BigDecimal.ROUND_HALF_UP)); 
                }
                Double ldblSIRate = 0.00; 
                if(poController.getMasterModel().getSlsInRte() != null){
                    ldblSIRate = poController.getMasterModel().getSlsInRte() / 100;
                    poController.getMasterModel().setSlsInAmt((new BigDecimal(ldblSIRate).multiply(ldblFinAmt)).setScale(2, BigDecimal.ROUND_HALF_UP)); 
                }
                
                System.out.println("nFinAmtxx : " + getVSPFinanceModel().getVSPFinanceModel().getFinAmt());
                System.out.println("nDealrAmt : " + poController.getMasterModel().getDealrAmt());
                System.out.println("nSlsInAmt : " + poController.getMasterModel().getSlsInAmt());
            }
        }
        
//        if (poController.getMasterModel().getTranTotl().equals(new BigDecimal("0.00"))) {
//            if (lsPayModex.equals("0")){
//                loJSON.put("result", "error");
//                loJSON.put("message", "Please Enter Amount to be transact.");
//                return loJSON;
//            } else {
//                if (getVSPFinanceList().size()-1 >= 0){
//                    ldblFinAmt = new BigDecimal(String.valueOf(getVSPFinanceModel().getVSPFinanceModel().getFinAmt())); //getVSPFinance("nFinAmtxx"))
//                    if (ldblFinAmt.compareTo(new BigDecimal("0.00")) <= 0){
//                        loJSON.put("result", "error");
//                        loJSON.put("message", "Please Enter Amount to be transact.");
//                        return loJSON;
//                    }
//                }
//            }
//        }
        
        System.out.println("nTranTotl : " +  poController.getMasterModel().getTranTotl()); //Gross Amount
        System.out.println("nNetTTotl : " +  poController.getMasterModel().getNetTTotl()); //Net Amount Due
        
        return loJSON;
    }
    
    private BigDecimal pdblDiscTotl = new BigDecimal("0.00");
    public BigDecimal getTotalDiscount(){
        return pdblDiscTotl;
    }
    
    private JSONObject validateEntry(){
        JSONObject loJSON = new JSONObject();
        String lsPayModex = poController.getMasterModel().getPayMode(); //(String) getMaster("cPayModex");
        BigDecimal ldblFinAmt = new BigDecimal("0.00");
        BigDecimal ldblNetTTotl = poController.getMasterModel().getNetTTotl();
        BigDecimal ldblTranTotl = poController.getMasterModel().getTranTotl();
        
        if(lsPayModex.equals("2") || lsPayModex.equals("4")){
            if(poVSPFinance.getVSPFinanceModel().getAcctTerm() == null) {
                loJSON.put("result", "error");
                loJSON.put("message", "Finance Term is not set.");
                return loJSON;
            } else {
                if (poVSPFinance.getVSPFinanceModel().getAcctTerm() <= 0.00){
                    loJSON.put("result", "error");
                    loJSON.put("message", "Finance Term is not set.");
                    return loJSON;
                }
            }

            if(poVSPFinance.getVSPFinanceModel().getAcctRate() == null) {
                loJSON.put("result", "error");
                loJSON.put("message", "Finance Rate is not set.");
                return loJSON;
            } else {
                if (poVSPFinance.getVSPFinanceModel().getAcctRate() <= 0.00){
                    loJSON.put("result", "error");
                    loJSON.put("message", "Finance Rate is not set.");
                    return loJSON;
                }
            }
            
            if(poVSPFinance.getVSPFinanceModel().getMonAmort() == null) {
                loJSON.put("result", "error");
                loJSON.put("message", "Finance Amortization is not set.");
                return loJSON;
            } else {
                if (poVSPFinance.getVSPFinanceModel().getMonAmort().compareTo(new BigDecimal("0.00")) <= 0){
                    loJSON.put("result", "error");
                    loJSON.put("message", "Finance Amortization is not set.");
                    return loJSON;
                }
            }

            if(poVSPFinance.getVSPFinanceModel().getPNValue() == null) {
                loJSON.put("result", "error");
                loJSON.put("message", "Finance Value is not set.");
                return loJSON;
            } else {
                if (poVSPFinance.getVSPFinanceModel().getPNValue().compareTo(new BigDecimal("0.00")) <= 0){
                    loJSON.put("result", "error");
                    loJSON.put("message", "Finance Value is not set.");
                    return loJSON;
                }
            }

            if(poVSPFinance.getVSPFinanceModel().getGrsMonth() == null) {
                loJSON.put("result", "error");
                loJSON.put("message", "Finance Gross Amount is not set.");
                return loJSON;
            } else {
                if (poVSPFinance.getVSPFinanceModel().getGrsMonth().compareTo(new BigDecimal("0.00")) <= 0){
                    loJSON.put("result", "error");
                    loJSON.put("message", "Finance Gross Amount is not set.");
                    return loJSON;
                }
            }
        }
        
        if (ldblTranTotl.compareTo(new BigDecimal("0.00")) < 0){
            loJSON.put("result", "error");
            loJSON.put("message", "Invalid Gross Amount Total: " + ldblTranTotl + " . ");
            return loJSON;
        }
        
        if (ldblNetTTotl.compareTo(new BigDecimal("0.00")) < 0){
            loJSON.put("result", "error");
            loJSON.put("message", "Invalid Net Amount Due: " + ldblNetTTotl + " . ");
            return loJSON;
        }
        
        if (getVSPFinanceList().size()-1 >= 0){
            BigDecimal ldblPNValuex = getVSPFinanceModel().getVSPFinanceModel().getPNValue();
            BigDecimal ldblGrsMonth = getVSPFinanceModel().getVSPFinanceModel().getGrsMonth();
            ldblFinAmt = getVSPFinanceModel().getVSPFinanceModel().getFinAmt();
            
            if (ldblFinAmt.compareTo(new BigDecimal("0.00")) < 0){
                loJSON.put("result", "error");
                loJSON.put("message", "Invalid Amount Finance : " + ldblFinAmt + " . ");
                return loJSON;
            }

            if (ldblGrsMonth.compareTo(new BigDecimal("0.00")) < 0){
                loJSON.put("result", "error");
                loJSON.put("message", "Invalid Gross Monthly Installment: " + ldblGrsMonth + " . ");
                return loJSON;
            }

            if (ldblPNValuex.compareTo(new BigDecimal("0.00")) < 0){
                loJSON.put("result", "error");
                loJSON.put("message", "Invalid Promissory Note Amount: " + ldblPNValuex + " . ");
                return loJSON;
            }
        }
        
        if (poController.getMasterModel().getTranTotl().equals(new BigDecimal("0.00"))) {
            if (lsPayModex.equals("0")){
                loJSON.put("result", "error");
                loJSON.put("message", "Please Enter Amount to be transact.");
                return loJSON;
            } else {
                if (getVSPFinanceList().size()-1 >= 0){
                    ldblFinAmt = new BigDecimal(String.valueOf(getVSPFinanceModel().getVSPFinanceModel().getFinAmt())); //getVSPFinance("nFinAmtxx"))
                    if (ldblFinAmt.compareTo(new BigDecimal("0.00")) <= 0){
                        loJSON.put("result", "error");
                        loJSON.put("message", "Please Enter Amount to be transact.");
                        return loJSON;
                    }
                }
            }
        }
        
//        loJSON = validateDetail();
//        if("error".equals((String) loJSON.get("result"))){
//            return loJSON;
//        }
        
        return loJSON;
    }
    
//    private JSONObject validateDetail(){
//        JSONObject loJSON = new JSONObject();
//        ValidatorInterface validator;
//        int lnSize = 0;
//        
//        lnSize = poVSPLabor.getDetailList().size() -1;
//        for (int lnCtr = 0; lnCtr <= lnSize; lnCtr++){
//            poVSPLabor.getDetailModel(lnCtr).setTransNo(poController.getMasterModel().getTransNo());
//            validator = ValidatorFactory.make(ValidatorFactory.TYPE.VehicleSalesProposal_Labor, poVSPLabor.getDetailModel(lnCtr));
//            validator.setGRider(poGRider);
//            if (!validator.isEntryOkay()){
//                loJSON.put("result", "error");
//                loJSON.put("message", validator.getMessage());
//                return loJSON;
//            }
//        }
//        
//        lnSize = poVSPParts.getDetailList().size() -1;
//        for (int lnCtr = 0; lnCtr <= lnSize; lnCtr++){
//            poVSPParts.getDetailModel(lnCtr).setTransNo(poController.getMasterModel().getTransNo());
//            validator = ValidatorFactory.make(ValidatorFactory.TYPE.VehicleSalesProposal_Parts, poVSPParts.getDetailModel(lnCtr));
//            validator.setGRider(poGRider);
//            if (!validator.isEntryOkay()){
//                loJSON.put("result", "error");
//                loJSON.put("message", validator.getMessage());
//                return loJSON;
//            }
//        }
//        return loJSON;
//    }
    
    //TODO
    private boolean computeTotlAmtPaid(){
        try {
            String lsWhere = "";
            ResultSet loRS;
            BigDecimal ldblPayment = new BigDecimal("0.00"); 
            String lsSQL =   " SELECT "                                             
                    + "   a.sTransNox "                                     
                    + " , a.sReferNox "                                     
                    + " , a.sSourceCD "                                     
                    + " , a.sSourceNo "                                     
                    + " , a.sTranType "                      
                    + " , a.nTranAmtx "                                    
                    + " , b.sReferNox AS sSINoxxxx "                        
                    + " , b.dTransact "                      
                    + " , b.cTranStat "                                
                    + " FROM si_master_source a "                           
                    + " LEFT JOIN si_master b ON b.sTransNox = a.sTransNox ";

            /* 1. Get all Invoice Receipts (SI) linked thru INQUIRY RESERVATION sTransNox */
//            for(int lnCtr = 0; lnCtr <= getVSPReservationList().size()-1; lnCtr++){
//                lsWhere = MiscUtil.addCondition(lsSQL, " b.cTranStat <> '0' "
//                                                    + " AND a.sReferNox = " + SQLUtil.toSQL(poVSPReservation.getDetailModel(lnCtr).getTransNo()) //getReservation(lnCtr, "sTransNox")
//                                                    );
//                System.out.println("EXISTING RESERVATION PAYMENT CHECK: " + lsSQL);
//                loRS = poGRider.executeQuery(lsWhere);
//                if (MiscUtil.RecordCount(loRS) > 0){
//                    while(loRS.next()){
//                        ldblPayment.add(new BigDecimal(String.valueOf(loRS.getDouble("nTranAmtx"))));
//                    }
//
//                    MiscUtil.close(loRS);
//                    return false;
//                }
//            }
            
            /* 2. Get all Invoice Receipts (SI) and PR linked thru VSP sTransNox */
            lsWhere = MiscUtil.addCondition(lsSQL, " b.cTranStat <> '0' "
                                                + " AND a.sReferNox = " + SQLUtil.toSQL(poController.getMasterModel().getTransNo())
                                                );
            System.out.println("EXISTING VSP PAYMENT CHECK: " + lsSQL);
            loRS = poGRider.executeQuery(lsWhere);

            if (MiscUtil.RecordCount(loRS) > 0){
                while(loRS.next()){
                    ldblPayment.add(new BigDecimal(String.valueOf(loRS.getDouble("nTranAmtx"))));
                }

                MiscUtil.close(loRS);
                return false;
            }
            
            poController.getMasterModel().setAmtPaid(ldblPayment);
            poController.getMasterModel().setResrvFee(poController.getMasterModel().getResrvFee().add(ldblPayment));
            
        } catch (SQLException ex) {
            Logger.getLogger(VehicleSalesProposal.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    public JSONObject loadPaymentHistory(){
        JSONObject loJSON = new JSONObject();
        
        return loJSON;
    } 
    
}
