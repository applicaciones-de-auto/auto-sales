/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.main.sales;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.controller.sales.Inquiry_Reservation;
import org.guanzon.auto.controller.sales.VehicleSalesProposal_Finance;
import org.guanzon.auto.controller.sales.VehicleSalesProposal_Labor;
import org.guanzon.auto.controller.sales.VehicleSalesProposal_Master;
import org.guanzon.auto.controller.sales.VehicleSalesProposal_Parts;
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
    Inquiry_Reservation poReservation;
    
    public VehicleSalesProposal(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poController = new VehicleSalesProposal_Master(foAppDrver,fbWtParent,fsBranchCd);
        poVSPFinance = new VehicleSalesProposal_Finance(foAppDrver);
        poVSPLabor = new VehicleSalesProposal_Labor(foAppDrver);
        poVSPParts = new VehicleSalesProposal_Parts(foAppDrver);
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
        
        poJSON = poReservation.openDetail(fsValue, false);
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
        
        for(int lnCtr = 0; lnCtr <= getReservationList().size()-1; lnCtr++){
            poReservation.setDetail(lnCtr, "sTransIDx", poController.getMasterModel().getTransNo());
        }
        poJSON =  poReservation.saveDetail("");
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
    
    public ArrayList getVSPFinanceList(){return poVSPFinance.getDetailList();}
    public void setVSPFinanceList(ArrayList foObj){this.poVSPFinance.setDetailList(foObj);}
    
//    public void setVSPFinance(int fnIndex, Object foValue){ poVSPFinance.setDetail(fnRow, fnIndex, foValue);}
//    public void setVSPFinance(String fsIndex, Object foValue){ poVSPFinance.setDetail(fnRow, fsIndex, foValue);}
//    public Object getVSPFinance(int fnIndex){return poVSPFinance.getDetail(fnRow, fnIndex);}
//    public Object getVSPFinance(String fsIndex){return poVSPFinance.getDetail(fnRow, fsIndex);}
    
    public JSONObject setVSPFinance(int fnCol, Object foData) {
        return poVSPFinance.setMaster(fnCol, foData);
    }

    public JSONObject setVSPFinance(String fsCol, Object foData) {
        return poVSPFinance.setMaster(fsCol, foData);
    }

    public Object getVSPFinance(int fnCol) {
        return poVSPFinance.getMaster(fnCol);
    }

    public Object getVSPFinance(String fsCol) {
        return poVSPFinance.getMaster(fsCol);
    }
    
    public Object addVSPFinance(){ return poVSPFinance.addDetail(poController.getMasterModel().getTransNo());}
    //public Object removeVSPFinance(int fnRow){ return poVSPFinance.removeDetail(fnRow);}
    
    public ArrayList getVSPLaborList(){return poVSPLabor.getDetailList();}
    public void setVSPLaborList(ArrayList foObj){this.poVSPLabor.setDetailList(foObj);}
    
    public void setVSPLabor(int fnRow, int fnIndex, Object foValue){ poVSPLabor.setDetail(fnRow, fnIndex, foValue);}
    public void setVSPLabor(int fnRow, String fsIndex, Object foValue){ poVSPLabor.setDetail(fnRow, fsIndex, foValue);}
    public Object getVSPLabor(int fnRow, int fnIndex){return poVSPLabor.getDetail(fnRow, fnIndex);}
    public Object getVSPLabor(int fnRow, String fsIndex){return poVSPLabor.getDetail(fnRow, fsIndex);}
    
    public Object addVSPLabor(){ return poVSPLabor.addDetail(poController.getMasterModel().getTransNo());}
    public Object removeVSPLabor(int fnRow){ return poVSPLabor.removeDetail(fnRow);}
    
    public ArrayList getVSPPartsList(){return poVSPParts.getDetailList();}
    public void setVSPPartsList(ArrayList foObj){this.poVSPParts.setDetailList(foObj);}
    
    public void setVSPParts(int fnRow, int fnIndex, Object foValue){ poVSPParts.setDetail(fnRow, fnIndex, foValue);}
    public void setVSPParts(int fnRow, String fsIndex, Object foValue){ poVSPParts.setDetail(fnRow, fsIndex, foValue);}
    public Object getVSPParts(int fnRow, int fnIndex){return poVSPParts.getDetail(fnRow, fnIndex);}
    public Object getVSPParts(int fnRow, String fsIndex){return poVSPParts.getDetail(fnRow, fsIndex);}
    
    public Object addVSPParts(){ return poVSPParts.addDetail(poController.getMasterModel().getTransNo());}
    public Object removeVSPParts(int fnRow){ return poVSPParts.removeDetail(fnRow);}
    
    public JSONObject loadReservationList() {
        JSONObject loJSON = new JSONObject();
        loJSON = poReservation.openDetail(poController.getMasterModel().getTransNo(),false);
        if(!"success".equals(poJSON.get("result"))){
            if(true == (boolean) poJSON.get("continue")){
                loJSON.put("result", "success");
                loJSON.put("message", "Record loaded succesfully.");
            }
        }
        return loJSON;
    }
    
    public ArrayList getReservationList(){return poReservation.getDetailList();}
    public Object getReservation(int fnRow, int fnIndex){return poReservation.getDetail(fnRow, fnIndex);}
    public Object getReservation(int fnRow, String fsIndex){return poReservation.getDetail(fnRow, fsIndex);}
    public Object removeReservation(int fnRow){ return poReservation.removeDetail(fnRow,false);}
    
    public JSONObject addOthReservation(String fsRsvTrnNo){ 
        JSONObject loJSON = new JSONObject();
        //Check if reservation already exist
        for(int lnCtr = 0; lnCtr <= getReservationList().size()-1;lnCtr++){
            if(((String) getReservation(lnCtr, "sTransNox")).equals(fsRsvTrnNo)){
                loJSON.put("result", "error");
                loJSON.put("message", "Reservation already exist.");
                return loJSON;
            }
        }
        
        loJSON = poReservation.openRecord(fsRsvTrnNo);
        if("error".equals((String) loJSON.get("resutl"))){
            removeReservation(getReservationList().size()-1);
            loJSON.put("result", "error");
            loJSON.put("message", "Cannot add other reservation.");
        } else {
            poReservation.setDetail(getReservationList().size()-1, "sTransIDx", poController.getMasterModel().getTransNo());
        }
        
        return loJSON;
    }
    
    
    public JSONObject loadOthReservation() {
        return poController.loadOthReservation();
    }
    
    public int getOthReservationCount() throws SQLException{
        return poController.getOthReservationCount();
    }
    
    public Object getOthReservationDetail(int fnRow, int fnIndex) throws SQLException{
        return poController.getOthReservationDetail(fnRow, fnIndex);
    }
    
    public Object getOthReservationDetail(int fnRow, String fsIndex) throws SQLException{
        return poController.getOthReservationDetail(fnRow, fsIndex);
    }
    
    public JSONObject searchInquiry(String fsValue){
        JSONObject loJSON = new JSONObject();
        loJSON = poController.searchInquiry(fsValue);
        if(!"error".equals((String) loJSON.get("result"))){
            //Buying Customer Default
            setMaster("sClientID", (String) loJSON.get("sClientID"));
            setMaster("sBuyCltNm", (String) loJSON.get("sCompnyNm"));
            setMaster("sAddressx", (String) loJSON.get("sAddressx"));
            setMaster("cPayModex", (String) loJSON.get("cPayModex"));
            setMaster("cVhclNewx", (String) loJSON.get("cVhclNewx"));
            setMaster("nResrvFee", (Double) loJSON.get("nAmountxx"));
            //Inquiring Customer
            setMaster( "sInqryIDx", (String) loJSON.get("sTransNox"));
            setMaster("dInqryDte", (String) loJSON.get("dTransact"));
            setMaster("sInqCltID", (String) loJSON.get("sClientID"));
            setMaster("sInqCltNm", (String) loJSON.get("sCompnyNm"));
            setMaster("cInqCltTp", (String) loJSON.get("cClientTp"));
            setMaster("sSourceCD", (String) loJSON.get("sSourceCD"));
            setMaster("sSourceNo", (String) loJSON.get("sSourceNo"));
            setMaster("sPlatform", (String) loJSON.get("sPlatform"));
            setMaster("sAgentIDx", (String) loJSON.get("sAgentIDx"));
            setMaster("sAgentNmx", (String) loJSON.get("sSalesAgn"));
            setMaster("sEmployID", (String) loJSON.get("sEmployID"));
            setMaster("sSENamexx", (String) loJSON.get("sSalesExe"));
            setMaster( "sContctNm", (String) loJSON.get("sContctNm"));
            setMaster( "sBranchCd", (String) loJSON.get("sBranchCd"));
            setMaster( "sBranchNm", (String) loJSON.get("sBranchNm"));
        } else {
            setMaster("sClientID", ""); 
            setMaster("sBuyCltNm", ""); 
            setMaster("sAddressx", ""); 
            setMaster("nResrvFee", 0.00);
            //Inquiring Customer        
            setMaster("sInqryIDx","");  
            setMaster("dInqryDte", ""); 
            setMaster("sInqCltID", ""); 
            setMaster("sInqCltNm", ""); 
            setMaster("cInqCltTp", ""); 
            setMaster("sSourceCD", ""); 
            setMaster("sSourceNo", ""); 
            setMaster("sPlatform", ""); 
            setMaster("sAgentIDx", ""); 
            setMaster("sAgentNmx", ""); 
            setMaster("sEmployID", ""); 
            setMaster("sSENamexx", ""); 
            setMaster( "sContctNm",""); 
            setMaster( "sBranchCd",""); 
            setMaster( "sBranchNm",""); 
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
                return loJSONCheck;
            }
        } else {
            poController.getMasterModel().setSerialID("");
            poController.getMasterModel().setFrameNo("");
            poController.getMasterModel().setEngineNo("");
            poController.getMasterModel().setCSNo("");
            poController.getMasterModel().setPlateNo("");
            poController.getMasterModel().setVhclDesc("");
        }
        
        return loJSON;
    }
    
    public JSONObject searchBankApp(String fsValue){
        JSONObject loJSON = new JSONObject();
        loJSON = poController.searchBankApp(fsValue);
        if(!"error".equals((String) loJSON.get("result"))){
            setMaster( "sBnkAppCD", (String) loJSON.get("sTransNox"));
            setMaster( "sBankName", (String) loJSON.get("sBankName"));
            setMaster( "sBrBankNm", (String) loJSON.get("sBrBankNm"));
        } else {
            setMaster( "sBnkAppCD",""); 
            setMaster( "sBankName",""); 
            setMaster( "sBrBankNm",""); 
        }
        return loJSON;
    }
    
    public JSONObject searchInsurance(String fsValue, boolean fbisTPL){
        JSONObject loJSON = new JSONObject();
        loJSON = poController.searchInsurance(fsValue);
        if(!"error".equals((String) loJSON.get("result"))){
            if(fbisTPL){
                setMaster( "sInsTplCd", (String) loJSON.get("sBrInsIDx"));
                setMaster( "sTPLInsNm", (String) loJSON.get("sInsurNme"));
                setMaster( "sTPLBrIns", (String) loJSON.get("sBrInsNme"));
            } else {
                setMaster( "sInsCodex", (String) loJSON.get("sBrInsIDx"));
                setMaster( "sCOMInsNm", (String) loJSON.get("sInsurNme"));
                setMaster( "sCOMBrIns", (String) loJSON.get("sBrInsNme"));
            }
        } else {
            if(fbisTPL){
                setMaster( "sInsTplCd", "");
                setMaster( "sTPLInsNm", "");
                setMaster( "sTPLBrIns", "");
            } else {
                setMaster( "sInsCodex", "");
                setMaster( "sCOMInsNm", "");
                setMaster( "sCOMBrIns", "");
            }
        }
        return loJSON;
    }
    
    public JSONObject searchLabor(String fsValue, int fnRow, boolean withUI){
        JSONObject loJSON = new JSONObject();
        loJSON = poVSPLabor.searchLabor(fsValue, withUI);
        if(!"error".equals((String) loJSON.get("result"))){
            setVSPLabor(fnRow, "sLaborCde", (String) loJSON.get("sLaborCde"));
            setVSPLabor(fnRow, "sLaborDsc", (String) loJSON.get("sLaborDsc"));
        } else {
            setVSPLabor(fnRow, "sLaborCde","");
            setVSPLabor(fnRow, "sLaborDsc", "");
        }
        return loJSON;
    }
    
    public JSONObject searchParts(String fsValue, int fnRow, boolean withUI){
        JSONObject loJSON = new JSONObject();
        loJSON = poVSPParts.searchParts(fsValue);
        if(!"error".equals((String) loJSON.get("result"))){
            setVSPParts(fnRow, "sStockIDx", (String) loJSON.get("sStockIDx"));
            setVSPParts(fnRow, "sBarCodex", (String) loJSON.get("sBarCodex"));
            setVSPParts(fnRow, "nSelPrice", (String) loJSON.get("nSelPrice"));
            //setVSPParts(fnRow, "sDescript", (String) loJSON.get("sDescript"));
        } else {
            setVSPParts(fnRow, "sStockIDx","");
            setVSPParts(fnRow, "sBarCodex","");
            setVSPParts(fnRow, "nSelPrice","");
            //setVSPParts(fnRow, "sDescript", "");
        }
        return loJSON;
    }
    
    /**
     * Compute amounts on VSP Transaction.
     * This method performs the computation of amount that has been input to the VSP Record.
     * 
    */
    public JSONObject computeAmount() {
        JSONObject loJSON = new JSONObject();
        String lsPayModex = (String) getMaster("cPayModex");
        int lnCtr;
        String lsQty = ""; 
        BigDecimal ldblLaborAmt = new BigDecimal("0.00"); 
        BigDecimal ldblLaborDsc = new BigDecimal("0.00"); 
        BigDecimal ldblAccesAmt = new BigDecimal("0.00"); 
        BigDecimal ldblAccesDsc = new BigDecimal("0.00"); 
        BigDecimal ldblPartsAmt = new BigDecimal("0.00"); 
        BigDecimal ldblFinAmt = new BigDecimal("0.00");
        
        /*Compute Labor Total*/
        for (lnCtr = 0; lnCtr <= getVSPLaborList().size()-1; lnCtr++){
            if(String.valueOf( getVSPLabor(lnCtr, "nLaborAmt")) != null){
                ldblLaborAmt = ldblLaborAmt.add(new BigDecimal( String.valueOf( getVSPLabor(lnCtr, "nLaborAmt")))).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
            
            if(String.valueOf( getVSPLabor(lnCtr, "nToLabDsc")) != null){
                ldblLaborDsc = ldblLaborDsc.add(new BigDecimal( String.valueOf( getVSPLabor(lnCtr, "nToLabDsc")))).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
        }
        
        ldblLaborAmt = ldblLaborAmt.setScale(2, BigDecimal.ROUND_HALF_UP);
        ldblLaborDsc = ldblLaborDsc.setScale(2, BigDecimal.ROUND_HALF_UP);
        /*Compute Parts Total*/
        for (lnCtr = 1; lnCtr <= getVSPPartsList().size()-1; lnCtr++){
            if(String.valueOf(getVSPParts(lnCtr, "nQuantity")) != null){
                lsQty = String.valueOf(getVSPParts(lnCtr, "nQuantity"));
            } else {
                lsQty = "0";
            }
            
            if(String.valueOf( getVSPParts(lnCtr, "nUnitPrce")) != null){
                ldblPartsAmt = new BigDecimal(lsQty).multiply(new BigDecimal( String.valueOf( getVSPParts(lnCtr, "nUnitPrce"))));
            }
            
            setVSPParts(lnCtr,"sTotlAmtx",String.valueOf(ldblPartsAmt));
            System.out.println(" ROW "+ lnCtr + " total amount >> " + getVSPParts(lnCtr, "sTotlAmtx"));
            ldblAccesAmt = ldblAccesAmt.add(ldblPartsAmt);
            
            if(String.valueOf( getVSPParts(lnCtr, "nToPrtDsc")) != null){
                ldblAccesDsc = ldblAccesDsc.add(new BigDecimal( String.valueOf( getVSPParts(lnCtr, "nToPrtDsc")))).setScale(2, BigDecimal.ROUND_HALF_UP);
            }
        }
        
        ldblAccesAmt = ldblAccesAmt.setScale(2, BigDecimal.ROUND_HALF_UP);
        ldblAccesDsc = ldblAccesDsc.setScale(2, BigDecimal.ROUND_HALF_UP);
        
        //TODO
//        if (!computeTotlAmtPaid()){
//            return false;
//        }
        
        BigDecimal ldblTranTotl = new BigDecimal("0.00"); 
        BigDecimal ldblNetTTotl = new BigDecimal("0.00"); 
        BigDecimal ldblDiscntxx= new BigDecimal("0.00"); 
        
        //Amount to be Pay
        String lsUnitPrce = String.valueOf( getMaster("nUnitPrce"));   
        BigDecimal ldblUnitPrce =  new BigDecimal("0.00");
        if(lsUnitPrce != null && !lsUnitPrce.equals("null")){
            ldblUnitPrce = new BigDecimal(lsUnitPrce);
        }
        
        String lsTPLAmtxx = String.valueOf( getMaster("nTPLAmtxx"));
        BigDecimal ldblTPLAmtxx = new BigDecimal("0.00");
        if(lsTPLAmtxx != null && !lsTPLAmtxx.equals("null")){
            ldblTPLAmtxx = new BigDecimal(lsTPLAmtxx);
        }
        
        String lsCompAmtx = String.valueOf( getMaster("nCompAmtx"));
        BigDecimal ldblCompAmtx = new BigDecimal("0.00");
        if(lsCompAmtx != null && !lsCompAmtx.equals("null")){
            ldblCompAmtx = new BigDecimal(lsCompAmtx);
        }
        
        String lsLTOAmtxx = String.valueOf( getMaster("nLTOAmtxx")); 
        BigDecimal ldblLTOAmtxx = new BigDecimal("0.00");
        if(lsLTOAmtxx != null && !lsLTOAmtxx.equals("null")){
            ldblLTOAmtxx = new BigDecimal(lsLTOAmtxx);
        }
        
        String lsChmoAmtx = String.valueOf( getMaster("nChmoAmtx")); 
        BigDecimal ldblChmoAmtx = new BigDecimal("0.00");
        if(lsChmoAmtx != null && !lsChmoAmtx.equals("null")){
            ldblChmoAmtx = new BigDecimal(lsChmoAmtx);
        }
        
        String lsFrgtChrg = String.valueOf( getMaster("nFrgtChrg"));
        BigDecimal ldblFrgtChrg = new BigDecimal("0.00");
        if(lsFrgtChrg != null && !lsFrgtChrg.equals("null")){
            ldblFrgtChrg = new BigDecimal(lsFrgtChrg);
        }
        
        String lsOthrChrg = String.valueOf( getMaster("nOthrChrg")); 
        BigDecimal ldblOthrChrg = new BigDecimal("0.00");
        if(lsOthrChrg != null && !lsOthrChrg.equals("null")){
            ldblOthrChrg = new BigDecimal(lsOthrChrg);
        }
        
        String lsAdvDwPmt = String.valueOf( getMaster("nAdvDwPmt"));
        BigDecimal ldblAdvDwPmt = new BigDecimal("0.00");
        if(lsAdvDwPmt != null && !lsAdvDwPmt.equals("null")){
            ldblAdvDwPmt = new BigDecimal(lsAdvDwPmt);
        }
        //Discounted Amount                        
        String lsAddlDscx = String.valueOf( getMaster("nAddlDscx"));  
        BigDecimal ldblAddlDscx = new BigDecimal("0.00");
        if(lsAddlDscx != null && !lsAddlDscx.equals("null")){
            ldblAddlDscx = new BigDecimal(lsAddlDscx);
        }
        String lsPromoDsc = String.valueOf( getMaster("nPromoDsc"));
        BigDecimal ldblPromoDsc = new BigDecimal("0.00");
        if(lsPromoDsc != null && !lsPromoDsc.equals("null")){
            ldblPromoDsc = new BigDecimal(lsPromoDsc);
        }
        String lsFleetDsc = String.valueOf( getMaster("nFleetDsc"));
        BigDecimal ldblFleetDsc = new BigDecimal("0.00");
        if(lsFleetDsc != null && !lsFleetDsc.equals("null")){
            ldblFleetDsc = new BigDecimal(lsFleetDsc);
        }
        
        String lsSPFltDsc = String.valueOf( getMaster("nSPFltDsc"));
        BigDecimal ldblSPFltDsc = new BigDecimal("0.00");
        if(lsSPFltDsc != null && !lsSPFltDsc.equals("null")){
            ldblSPFltDsc = new BigDecimal(lsSPFltDsc);
        }
        
        String lsBndleDsc = String.valueOf( getMaster("nBndleDsc"));
        BigDecimal ldblBndleDsc = new BigDecimal("0.00");
        if(lsBndleDsc != null && !lsBndleDsc.equals("null")){
            ldblBndleDsc = new BigDecimal(lsBndleDsc);
        }
        
        //Paid Amount
        //double ldblDownPaym = (Double) getMaster("nDownPaym"); 
        BigDecimal ldblDownPaym = new BigDecimal("0.00"); 
        
        String lsResrvFee = String.valueOf( getMaster("nResrvFee")); 
        BigDecimal ldblResrvFee = new BigDecimal("0.00");
        if(lsResrvFee != null && !lsResrvFee.equals("null")){
            ldblResrvFee = new BigDecimal(lsResrvFee);
        }
        
        if (!lsPayModex.equals("0")){ 
            String lsDownPaym = String.valueOf( getMaster("nDownPaym")); 
            if(lsDownPaym != null && !lsDownPaym.equals("null")){
                ldblUnitPrce = new BigDecimal(lsDownPaym);
            }
            //ldblUnitPrce = new BigDecimal(String.valueOf( getMaster("nDownPaym")));
            ldblUnitPrce = ldblUnitPrce.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        
        //vsptotal = nUnitPrce + instpl + inscomp + lto  + chmo + freightchage + miscamt + omacmf + labtotal + partstotal //gross vsp tota;
        //ldblTranTotl = ldblUnitPrce + ldblTPLAmtxx + ldblCompAmtx + ldblLTOAmtxx + ldblChmoAmtx + ldblFrgtChrg + ldblOthrChrg + ldblAdvDwPmt + ldblLaborAmt + ldblAccesAmt;
        ldblTranTotl = ldblUnitPrce.add(ldblTPLAmtxx).add(ldblCompAmtx).add(ldblLTOAmtxx).add(ldblChmoAmtx).add(ldblFrgtChrg).add(ldblOthrChrg).add(ldblAdvDwPmt).add(ldblLaborAmt).add(ldblAccesAmt);
        ldblTranTotl = ldblTranTotl.setScale(2, BigDecimal.ROUND_HALF_UP);
//        
        //vsptotal = vsptotal - (cashdisc + promodisc + stdfleetdisc + splfleet disc + bundledisc)  //gross vsp total less discounts and other deductibles
        //ldblTranTotl = ldblTranTotl - (ldblAddlDscx + ldblPromoDsc + ldblFleetDsc + ldblSPFltDsc + ldblBndleDsc);
        ldblDiscntxx = ldblAddlDscx.add(ldblPromoDsc).add(ldblFleetDsc).add(ldblSPFltDsc).add(ldblBndleDsc).add(ldblLaborDsc).add(ldblAccesDsc);
        ldblDiscntxx = ldblDiscntxx.setScale(2, BigDecimal.ROUND_HALF_UP);
        
        ldblTranTotl = ldblTranTotl.subtract(ldblDiscntxx);
        ldblTranTotl = ldblTranTotl.setScale(2, BigDecimal.ROUND_HALF_UP);
        
        //Net Amount Due = vsp total -(rfee + dwntotal + otherpayment) 
        //To be continued no computation yet from receipt -jahn 09162023
        //ldblNetTTotl = ldblTranTotl - (ldblDownPaym + ldblResrvFee);
        ldblNetTTotl = ldblTranTotl.subtract(ldblDownPaym.add(ldblResrvFee));
        ldblNetTTotl = ldblNetTTotl.setScale(2, BigDecimal.ROUND_HALF_UP);
        //if (ldblTranTotl < 0.00){
        if (ldblTranTotl.compareTo(new BigDecimal("0.00")) < 0){
            loJSON.put("result", "error");
            loJSON.put("message", "Invalid Gross Amount Total: " + ldblTranTotl + " . ");
            return loJSON;
        }
        
        //if (ldblNetTTotl < 0.00){
        if (ldblNetTTotl.compareTo(new BigDecimal("0.00")) < 0){
            loJSON.put("result", "error");
            loJSON.put("message", "Invalid Net Amount Due: " + ldblNetTTotl + " . ");
            return loJSON;
        }
        
        setMaster("nTranTotl",ldblTranTotl);
        setMaster("nNetTTotl",ldblNetTTotl);
        setMaster("nLaborAmt",ldblLaborAmt);
        setMaster("nAccesAmt",ldblAccesAmt);
        setMaster("nToLabDsc",ldblLaborDsc);
        setMaster("nToPrtDsc",ldblAccesDsc);
        
        //PO / FINANCING
        if (!lsPayModex.equals("0")){ 
            if (getVSPFinanceList().size()-1 >= 0){
                ldblFinAmt = new BigDecimal("0.00"); 
                BigDecimal ldblRatexx = new BigDecimal("0.00"); 
                BigDecimal ldblMonAmort = new BigDecimal("0.00"); 
                BigDecimal ldblGrsMonth = new BigDecimal("0.00"); 
                BigDecimal ldblPNValuex = new BigDecimal("0.00"); 
                
                String lsDiscount = String.valueOf( getVSPFinance("nDiscount")); 
                BigDecimal ldblDiscount = new BigDecimal("0.00");
                if(lsDiscount != null && !lsDiscount.equals("null")){
                    ldblDiscount = new BigDecimal(lsDiscount);
                }
                
                
                String lsNtDwnPmt = String.valueOf( getVSPFinance("nNtDwnPmt")); 
                BigDecimal ldblNtDwnPmt = new BigDecimal("0.00");
                if(lsNtDwnPmt != null && !lsNtDwnPmt.equals("null")){
                    ldblNtDwnPmt = new BigDecimal(lsNtDwnPmt);
                }
                
                String lsRebatesx = String.valueOf( getVSPFinance("nRebatesx")); 
                BigDecimal ldblRebatesx = new BigDecimal("0.00");
                if(lsRebatesx != null && !lsRebatesx.equals("null")){
                    ldblRebatesx = new BigDecimal(lsRebatesx);
                }
                
                String lsAcctRate = String.valueOf( getVSPFinance("nAcctRate")); 
                BigDecimal ldblAcctRate = new BigDecimal("0.00");
                if(lsAcctRate != null && !lsAcctRate.equals("null")){
                    ldblAcctRate = new BigDecimal(lsAcctRate);
                }
                
                
                int lnAcctTerm = (Integer) getVSPFinance("nAcctTerm");
                
                lsUnitPrce = String.valueOf( getMaster("nUnitPrce"));
                if(lsUnitPrce != null && !lsUnitPrce.equals("null")){
                    ldblUnitPrce = new BigDecimal(lsUnitPrce);
                }
                ldblUnitPrce = ldblUnitPrce.setScale(2, BigDecimal.ROUND_HALF_UP);
                
                //-Amount Financed = nUnitPrce -(nDiscount + nNtDwnPmt)
                //ldblFinAmt = ldblUnitPrce - (ldblDiscount + ldblNtDwnPmt); 
                ldblFinAmt = ldblUnitPrce.subtract(ldblDiscount.add(ldblNtDwnPmt));
                ldblFinAmt = ldblFinAmt.setScale(2, BigDecimal.ROUND_HALF_UP); 
                if (ldblFinAmt.compareTo(new BigDecimal("0.00")) < 0){
                    loJSON.put("result", "error");
                    loJSON.put("message", "Invalid Amount Finance : " + ldblFinAmt + " . ");
                    return loJSON;
                }
                //-Rate = (nAcctRate/100) + 1
                //ldblRatexx = (ldblAcctRate / 100) + 1; 
                //ldblRatexx = (ldblAcctRate.divide(100)).add(new BigDecimal("1"));
                if (ldblAcctRate.compareTo(BigDecimal.ZERO) != 0) {
                    ldblRatexx = (ldblAcctRate.divide(new BigDecimal("100")));
                    ldblRatexx = ldblRatexx.setScale(2, BigDecimal.ROUND_HALF_UP); 
                    ldblRatexx = ldblRatexx.add(new BigDecimal("1"));
                    ldblRatexx = ldblRatexx.setScale(2, BigDecimal.ROUND_HALF_UP); 
                }
                
                //System.out.println("ldblRatexx " + ldblRatexx);
                //System.out.println("ldblFinAmt " + ldblFinAmt);
                //-net Monthly Inst = (Amount Financed * Rate)/Terms Rate
                //ldblMonAmort = (ldblFinAmt * ldblRatexx) / lnAcctTerm; 
                
                if (lnAcctTerm > 0) {
                    ldblMonAmort = (ldblFinAmt.multiply(ldblRatexx));
                    ldblMonAmort = ldblMonAmort.setScale(2, BigDecimal.ROUND_HALF_UP); 
                    //System.out.println("ldblMonAmort >>> " + ldblMonAmort);
                    //ldblMonAmort = ldblMonAmort.divide(new BigDecimal(String.valueOf(lnAcctTerm)), 2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal bgTerm = new BigDecimal(String.valueOf(lnAcctTerm));
                    ldblMonAmort = ldblMonAmort.divide(bgTerm, RoundingMode.HALF_UP);
                    ldblMonAmort = ldblMonAmort.setScale(2, BigDecimal.ROUND_HALF_UP); 
                    //System.out.println("lnAcctTerm " + lnAcctTerm);
                    //System.out.println("ldblMonAmort " + ldblMonAmort);
                }
                //-Gross Monthly Inst = Net Monthly Inst + Prompt Payment Disc
                //ldblGrsMonth = ldblMonAmort + ldblRebatesx; 
                ldblGrsMonth = ldblMonAmort.add(ldblRebatesx); 
                ldblGrsMonth = ldblGrsMonth.setScale(2, BigDecimal.ROUND_HALF_UP); 
                
                if (ldblGrsMonth.compareTo(new BigDecimal("0.00")) < 0){
                    loJSON.put("result", "error");
                    loJSON.put("message", "Invalid Gross Monthly Installment: " + ldblGrsMonth + " . ");
                    return loJSON;
                }
                //-Promisory Note Amount =Terms Rate * Gross Monthly Inst
                //ldblPNValuex = lnAcctTerm * ldblGrsMonth; 
                ldblPNValuex = ldblGrsMonth.multiply(new BigDecimal(String.valueOf(lnAcctTerm))); 
                ldblPNValuex = ldblPNValuex.setScale(2, BigDecimal.ROUND_HALF_UP); 
                if (ldblPNValuex.compareTo(new BigDecimal("0.00")) < 0){
                    loJSON.put("result", "error");
                    loJSON.put("message", "Invalid Promissory Note Amount: " + ldblPNValuex + " . ");
                    return loJSON;
                }
                
//                System.out.println("ldblRebatesx " + ldblRebatesx);
//                System.out.println("ldblMonAmort " + ldblMonAmort);
//                System.out.println("ldblGrsMonth " + ldblGrsMonth);
//                System.out.println("lnAcctTerm " + lnAcctTerm);
//                System.out.println("ldblPNValuex " + ldblPNValuex);
                
                setVSPFinance("nFinAmtxx",ldblFinAmt);
                setVSPFinance("nMonAmort",ldblMonAmort);
                setVSPFinance("nGrsMonth",ldblGrsMonth);
                setVSPFinance("nPNValuex",ldblPNValuex);
            }
        }
        
        if (poController.getMasterModel().getTranTotl() == 0.00) {
            if (lsPayModex.equals("0")){
                loJSON.put("result", "error");
                loJSON.put("message", "Please Enter Amount to be transact.");
                return loJSON;
            } else {
                ldblFinAmt = new BigDecimal(String.valueOf(getVSPFinance("nFinAmtxx")));
                if (ldblFinAmt.compareTo(new BigDecimal("0.00")) <= 0){
                    loJSON.put("result", "error");
                    loJSON.put("message", "Please Enter Amount to be transact.");
                    return loJSON;
                }
            }
        }
        
//        System.out.println("nTranTotl >>> " + String.valueOf( getMaster("nTranTotl")) ); //Gross Amount
//        System.out.println("nNetTTotl >>> " + String.valueOf(getMaster("nNetTTotl")) ); //Net Amount Due
        
        return loJSON;
    }
    
    //TODO
    private boolean computeTotlAmtPaid(){
        return true;
    }
}
