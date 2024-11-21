/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.main.sales;

import java.math.BigDecimal;
import java.util.Date;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.controller.sales.VehicleDeliveryReceipt_Master;
import org.guanzon.auto.controller.sales.VehicleSalesProposal_Master;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class VehicleDeliveryReceipt  implements GTransaction{
    final String XML = "Model_VehicleDeliveryReceipt_Master.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    int pnEditMode;
    String psTransStat;
    String psMessagex;
    public JSONObject poJSON;
    
    VehicleDeliveryReceipt_Master poController;
    VehicleSalesProposal_Master poVSPMaster;
    
    public VehicleDeliveryReceipt(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poController = new VehicleDeliveryReceipt_Master(foAppDrver,fbWtParent,fsBranchCd);
        poVSPMaster =  new VehicleSalesProposal_Master(foAppDrver,fbWtParent,fsBranchCd);
        
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
        
        if (!pbWtParent) poGRider.beginTrans();
        
        poJSON =  poController.saveTransaction();
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        if (!pbWtParent) poGRider.commitTrans();
        
        return poJSON;
    }
    
    public JSONObject savePrint() {
        return poController.savePrinted();
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
        poJSON = new JSONObject();  
        
        if (!pbWtParent) poGRider.beginTrans();
        
        poJSON =   poController.cancelTransaction(fsValue);
        if("error".equalsIgnoreCase((String) poJSON.get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return poJSON;
        }
        if (!pbWtParent) poGRider.commitTrans();
        
        return poJSON;
    }
    
    public JSONObject searchTransaction(String fsValue, boolean fIsActive) {
        poJSON = new JSONObject();  
        poJSON = poController.searchTransaction(fsValue, fIsActive);
        if(!"error".equals((String) poJSON.get("result"))){
            poJSON = openTransaction((String) poJSON.get("sTransNox"));
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
    public VehicleDeliveryReceipt_Master getMasterModel() {
        return poController;
    }

    @Override
    public void setTransactionStatus(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public JSONObject searchVSP(String fsValue, boolean fbByCode) {
        JSONObject loJSON = poController.searchVSP(fsValue, fbByCode);
        if(!"error".equals(loJSON.get("result"))){
            if(((String) loJSON.get("sUDRNoxxx")) != null){
                if(!((String) loJSON.get("sUDRNoxxx")).trim().isEmpty()){
                    loJSON.put("result", "error");
                    loJSON.put("message", "VSP No. "+(String) loJSON.get("sVSPNOxxx")+" has existing DR No. " + (String) loJSON.get("sUDRNoxxx") 
                                            + "\n\nLinking aborted.");
                    return loJSON;
                }
            }
            
            poController.getMasterModel().setClientID((String) loJSON.get("sClientID"));                                                        
            poController.getMasterModel().setBuyCltNm((String) loJSON.get("sBuyCltNm"));                                                           
            poController.getMasterModel().setAddress((String) loJSON.get("sAddressx"));                                                  
            poController.getMasterModel().setCoCltNm((String) loJSON.get("sCoCltNmx"));                                                       
            poController.getMasterModel().setIsVhclNw((String) loJSON.get("cIsVhclNw"));             
            poController.getMasterModel().setVSPTrans((String) loJSON.get("sTransNox"));          
            poController.getMasterModel().setSourceNo((String) loJSON.get("sTransNox")); 
            poController.getMasterModel().setVSPNO((String) loJSON.get("sVSPNOxxx"));
            poController.getMasterModel().setInqTran((String) loJSON.get("sInqryIDx")); 
            poController.getMasterModel().setBranchCD((String) loJSON.get("sBranchCD"));
            poController.getMasterModel().setBranchNm((String) loJSON.get("sBranchNm"));
            poController.getMasterModel().setPayMode((String) loJSON.get("cPayModex"));
            poController.getMasterModel().setVSPDate(SQLUtil.toDate((String) loJSON.get("dTransact"), SQLUtil.FORMAT_SHORT_DATE) ); 
            poController.getMasterModel().setDelvryDte(SQLUtil.toDate((String) loJSON.get("dDelvryDt"), SQLUtil.FORMAT_SHORT_DATE) ); 
            
            BigDecimal ldblInsurDsc =new BigDecimal("0.00");
            
            BigDecimal ldblDiscTotl = (new BigDecimal((String) loJSON.get("nAddlDscx"))).add(new BigDecimal((String) loJSON.get("nPromoDsc"))).add(new BigDecimal((String) loJSON.get("nFleetDsc"))).add(new BigDecimal((String) loJSON.get("nSPFltDsc"))).add(new BigDecimal((String) loJSON.get("nBndleDsc")));
            ldblDiscTotl = ldblDiscTotl.add(new BigDecimal((String) loJSON.get("nToLabDsc"))).add(new BigDecimal((String) loJSON.get("nToPrtDsc"))).add(ldblInsurDsc);
            ldblDiscTotl = ldblDiscTotl.setScale(2, BigDecimal.ROUND_HALF_UP);
            poController.getMasterModel().setDiscount(ldblDiscTotl);
            poController.getMasterModel().setGrossAmt(new BigDecimal((String) loJSON.get("nTranTotl")));
            poController.getMasterModel().setTranTotl(new BigDecimal((String) loJSON.get("nNetTTotl")));
            
            poController.getMasterModel().setSerialID((String) loJSON.get("sSerialID"));
            poController.getMasterModel().setCSNo((String) loJSON.get("sCSNoxxxx"));
            poController.getMasterModel().setPlateNo((String) loJSON.get("sPlateNox")); 
            poController.getMasterModel().setFrameNo((String) loJSON.get("sFrameNox"));
            poController.getMasterModel().setEngineNo((String) loJSON.get("sEngineNo"));
            poController.getMasterModel().setKeyNo((String) loJSON.get("sKeyNoxxx"));
            poController.getMasterModel().setVhclDesc((String) loJSON.get("sVhclDesc"));
            poController.getMasterModel().setVhclFDsc((String) loJSON.get("sVhclFDsc"));
            
        } else {
            poController.getMasterModel().setClientID("");                                                        
            poController.getMasterModel().setBuyCltNm("");                                                           
            poController.getMasterModel().setAddress("");                                                  
            poController.getMasterModel().setCoCltNm("");                                                       
            poController.getMasterModel().setIsVhclNw("");             
            poController.getMasterModel().setVSPTrans(""); 
            poController.getMasterModel().setVSPNO("");
            poController.getMasterModel().setInqTran(""); 
            poController.getMasterModel().setBranchCD("");  
             poController.getMasterModel().setVSPDate(SQLUtil.toDate("1900-01-01", SQLUtil.FORMAT_SHORT_DATE));                                        
            poController.getMasterModel().setDelvryDte(SQLUtil.toDate("1900-01-01", SQLUtil.FORMAT_SHORT_DATE));      
            poController.getMasterModel().setGrossAmt(new BigDecimal("0.00"));
            poController.getMasterModel().setDiscount(new BigDecimal("0.00"));          
            poController.getMasterModel().setTranTotl(new BigDecimal("0.00"));
            poController.getMasterModel().setCSNo("");
            poController.getMasterModel().setPlateNo(""); 
            poController.getMasterModel().setFrameNo("");
            poController.getMasterModel().setEngineNo("");
            poController.getMasterModel().setKeyNo("");
            poController.getMasterModel().setVhclDesc("");
            poController.getMasterModel().setVhclFDsc("");
            
        }
        
        return loJSON;
    }
    
}
