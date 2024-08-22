/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.main.sales;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GTransaction;
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
    
    public VehicleSalesProposal(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poController = new VehicleSalesProposal_Master(foAppDrver,fbWtParent,fsBranchCd);
        poVSPFinance = new VehicleSalesProposal_Finance(foAppDrver);
        poVSPLabor = new VehicleSalesProposal_Labor(foAppDrver);
        
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
        
        poJSON =  poVSPFinance.saveDetail((String) poController.getMasterModel().getTransNo());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        poJSON =  poVSPLabor.saveDetail((String) poController.getMasterModel().getTransNo());
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
    
    public void setVSPFinance(int fnRow, int fnIndex, Object foValue){ poVSPFinance.setDetail(fnRow, fnIndex, foValue);}
    public void setVSPFinance(int fnRow, String fsIndex, Object foValue){ poVSPFinance.setDetail(fnRow, fsIndex, foValue);}
    public Object getVSPFinance(int fnRow, int fnIndex){return poVSPFinance.getDetail(fnRow, fnIndex);}
    public Object getVSPFinance(int fnRow, String fsIndex){return poVSPFinance.getDetail(fnRow, fsIndex);}
    
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
//    
//    /**
//     * Compute amounts on VSP Transaction.
//     * This method performs the computation of amount that has been input to the VSP Record.
//     * 
//    */
//    public JSONObject computeAmount() {
//        JSONObject loJSON = new JSONObject();
//        String lsPayModex = (String) getMaster("cPayModex");
//        int lnCtr;
//        String lsQty = ""; 
//        BigDecimal ldblLaborAmt = new BigDecimal("0.00"); 
//        BigDecimal ldblAccesAmt = new BigDecimal("0.00"); 
//        BigDecimal ldblPartsAmt = new BigDecimal("0.00"); 
//        /*Compute Labor Total*/
//        for (lnCtr = 1; lnCtr <= getVSPLaborCount(); lnCtr++){
//            //ldblLaborAmt = ldblLaborAmt + (Double) getVSPLaborDetail(lnCtr, "nLaborAmt");
//            if(String.valueOf( getVSPLaborDetail(lnCtr, "nLaborAmt")) != null){
//                ldblLaborAmt = ldblLaborAmt.add(new BigDecimal( String.valueOf( getVSPLaborDetail(lnCtr, "nLaborAmt")))).setScale(2, BigDecimal.ROUND_HALF_UP);
//            }
//        }
//        ldblLaborAmt = ldblLaborAmt.setScale(2, BigDecimal.ROUND_HALF_UP);
//        /*Compute Parts Total*/
//        for (lnCtr = 1; lnCtr <= getVSPPartsCount(); lnCtr++){
//            //ldblAccesAmt = ldblAccesAmt + ((Double) getVSPPartsDetail(lnCtr, "nSelPrice") * (Integer) getVSPPartsDetail(lnCtr, "nQuantity"));
//            //ldblAccesAmt = ldblAccesAmt.add(new BigDecimal( String.valueOf( getVSPPartsDetail(lnCtr, "nSelPrice"))));
//            //ldblAccesAmt = ldblAccesAmt.add(new BigDecimal( String.valueOf( getVSPPartsDetail(lnCtr, "sTotlAmtx")))); //nUnitPrce
//            if(String.valueOf(getVSPPartsDetail(lnCtr, "nQuantity")) != null){
//                lsQty = String.valueOf(getVSPPartsDetail(lnCtr, "nQuantity"));
//            } else {
//                lsQty = "0";
//            }
//            if(String.valueOf( getVSPPartsDetail(lnCtr, "nUnitPrce")) != null){
//                ldblPartsAmt = new BigDecimal(lsQty).multiply(new BigDecimal( String.valueOf( getVSPPartsDetail(lnCtr, "nUnitPrce"))));
//            }
//            setVSPPartsDetail(lnCtr,"sTotlAmtx",String.valueOf(ldblPartsAmt));
//            System.out.println(" ROW "+ lnCtr + " total amount >> " + getVSPPartsDetail(lnCtr, "sTotlAmtx"));
//            ldblAccesAmt = ldblAccesAmt.add(ldblPartsAmt);
//        }
//        
//        ldblAccesAmt = ldblAccesAmt.setScale(2, BigDecimal.ROUND_HALF_UP);
//        
//        if (!computeTotlAmtPaid()){
//            return false;
//        }
//        
//        BigDecimal ldblTranTotl = new BigDecimal("0.00"); 
//        BigDecimal ldblNetTTotl = new BigDecimal("0.00"); 
//        BigDecimal ldblDiscntxx= new BigDecimal("0.00"); 
//        
//        //Amount to be Pay
//        String lsUnitPrce = String.valueOf( getMaster("nUnitPrce"));   
//        BigDecimal ldblUnitPrce =  new BigDecimal("0.00");
//        if(lsUnitPrce != null && !lsUnitPrce.equals("null")){
//            ldblUnitPrce = new BigDecimal(lsUnitPrce);
//        }
//        
//        String lsTPLAmtxx = String.valueOf( getMaster("nTPLAmtxx"));
//        BigDecimal ldblTPLAmtxx = new BigDecimal("0.00");
//        if(lsTPLAmtxx != null && !lsTPLAmtxx.equals("null")){
//            ldblTPLAmtxx = new BigDecimal(lsTPLAmtxx);
//        }
//        
//        String lsCompAmtx = String.valueOf( getMaster("nCompAmtx"));
//        BigDecimal ldblCompAmtx = new BigDecimal("0.00");
//        if(lsCompAmtx != null && !lsCompAmtx.equals("null")){
//            ldblCompAmtx = new BigDecimal(lsCompAmtx);
//        }
//        
//        String lsLTOAmtxx = String.valueOf( getMaster("nLTOAmtxx")); 
//        BigDecimal ldblLTOAmtxx = new BigDecimal("0.00");
//        if(lsLTOAmtxx != null && !lsLTOAmtxx.equals("null")){
//            ldblLTOAmtxx = new BigDecimal(lsLTOAmtxx);
//        }
//        
//        String lsChmoAmtx = String.valueOf( getMaster("nChmoAmtx")); 
//        BigDecimal ldblChmoAmtx = new BigDecimal("0.00");
//        if(lsChmoAmtx != null && !lsChmoAmtx.equals("null")){
//            ldblChmoAmtx = new BigDecimal(lsChmoAmtx);
//        }
//        
//        String lsFrgtChrg = String.valueOf( getMaster("nFrgtChrg"));
//        BigDecimal ldblFrgtChrg = new BigDecimal("0.00");
//        if(lsFrgtChrg != null && !lsFrgtChrg.equals("null")){
//            ldblFrgtChrg = new BigDecimal(lsFrgtChrg);
//        }
//        
//        String lsOthrChrg = String.valueOf( getMaster("nOthrChrg")); 
//        BigDecimal ldblOthrChrg = new BigDecimal("0.00");
//        if(lsOthrChrg != null && !lsOthrChrg.equals("null")){
//            ldblOthrChrg = new BigDecimal(lsOthrChrg);
//        }
//        
//        String lsAdvDwPmt = String.valueOf( getMaster("nAdvDwPmt"));
//        BigDecimal ldblAdvDwPmt = new BigDecimal("0.00");
//        if(lsAdvDwPmt != null && !lsAdvDwPmt.equals("null")){
//            ldblAdvDwPmt = new BigDecimal(lsAdvDwPmt);
//        }
//        //Discounted Amount                        
//        String lsAddlDscx = String.valueOf( getMaster("nAddlDscx"));  
//        BigDecimal ldblAddlDscx = new BigDecimal("0.00");
//        if(lsAddlDscx != null && !lsAddlDscx.equals("null")){
//            ldblAddlDscx = new BigDecimal(lsAddlDscx);
//        }
//        String lsPromoDsc = String.valueOf( getMaster("nPromoDsc"));
//        BigDecimal ldblPromoDsc = new BigDecimal("0.00");
//        if(lsPromoDsc != null && !lsPromoDsc.equals("null")){
//            ldblPromoDsc = new BigDecimal(lsPromoDsc);
//        }
//        String lsFleetDsc = String.valueOf( getMaster("nFleetDsc"));
//        BigDecimal ldblFleetDsc = new BigDecimal("0.00");
//        if(lsFleetDsc != null && !lsFleetDsc.equals("null")){
//            ldblFleetDsc = new BigDecimal(lsFleetDsc);
//        }
//        
//        String lsSPFltDsc = String.valueOf( getMaster("nSPFltDsc"));
//        BigDecimal ldblSPFltDsc = new BigDecimal("0.00");
//        if(lsSPFltDsc != null && !lsSPFltDsc.equals("null")){
//            ldblSPFltDsc = new BigDecimal(lsSPFltDsc);
//        }
//        
//        String lsBndleDsc = String.valueOf( getMaster("nBndleDsc"));
//        BigDecimal ldblBndleDsc = new BigDecimal("0.00");
//        if(lsBndleDsc != null && !lsBndleDsc.equals("null")){
//            ldblBndleDsc = new BigDecimal(lsBndleDsc);
//        }
//        
//        //Paid Amount
//        //double ldblDownPaym = (Double) getMaster("nDownPaym"); 
//        BigDecimal ldblDownPaym = new BigDecimal("0.00"); 
//        
//        String lsResrvFee = String.valueOf( getMaster("nResrvFee")); 
//        BigDecimal ldblResrvFee = new BigDecimal("0.00");
//        if(lsResrvFee != null && !lsResrvFee.equals("null")){
//            ldblResrvFee = new BigDecimal(lsResrvFee);
//        }
//        
//        if (!lsPayModex.equals("0")){ 
//            String lsDownPaym = String.valueOf( getMaster("nDownPaym")); 
//            if(lsDownPaym != null && !lsDownPaym.equals("null")){
//                ldblUnitPrce = new BigDecimal(lsDownPaym);
//            }
//            //ldblUnitPrce = new BigDecimal(String.valueOf( getMaster("nDownPaym")));
//            ldblUnitPrce = ldblUnitPrce.setScale(2, BigDecimal.ROUND_HALF_UP);
//        }
//        
//        //vsptotal = nUnitPrce + instpl + inscomp + lto  + chmo + freightchage + miscamt + omacmf + labtotal + partstotal //gross vsp tota;
//        //ldblTranTotl = ldblUnitPrce + ldblTPLAmtxx + ldblCompAmtx + ldblLTOAmtxx + ldblChmoAmtx + ldblFrgtChrg + ldblOthrChrg + ldblAdvDwPmt + ldblLaborAmt + ldblAccesAmt;
//        ldblTranTotl = ldblUnitPrce.add(ldblTPLAmtxx).add(ldblCompAmtx).add(ldblLTOAmtxx).add(ldblChmoAmtx).add(ldblFrgtChrg).add(ldblOthrChrg).add(ldblAdvDwPmt).add(ldblLaborAmt).add(ldblAccesAmt);
//        ldblTranTotl = ldblTranTotl.setScale(2, BigDecimal.ROUND_HALF_UP);
////        
//        //vsptotal = vsptotal - (cashdisc + promodisc + stdfleetdisc + splfleet disc + bundledisc)  //gross vsp total less discounts and other deductibles
//        //ldblTranTotl = ldblTranTotl - (ldblAddlDscx + ldblPromoDsc + ldblFleetDsc + ldblSPFltDsc + ldblBndleDsc);
//        ldblDiscntxx = ldblAddlDscx.add(ldblPromoDsc).add(ldblFleetDsc).add(ldblSPFltDsc).add(ldblBndleDsc);
//        ldblDiscntxx = ldblDiscntxx.setScale(2, BigDecimal.ROUND_HALF_UP);
//        
//        ldblTranTotl = ldblTranTotl.subtract(ldblDiscntxx);
//        ldblTranTotl = ldblTranTotl.setScale(2, BigDecimal.ROUND_HALF_UP);
//        
//        //Net Amount Due = vsp total -(rfee + dwntotal + otherpayment) 
//        //To be continued no computation yet from receipt -jahn 09162023
//        //ldblNetTTotl = ldblTranTotl - (ldblDownPaym + ldblResrvFee);
//        ldblNetTTotl = ldblTranTotl.subtract(ldblDownPaym.add(ldblResrvFee));
//        ldblNetTTotl = ldblNetTTotl.setScale(2, BigDecimal.ROUND_HALF_UP);
//        //if (ldblTranTotl < 0.00){
//        if (ldblTranTotl.compareTo(new BigDecimal("0.00")) < 0){
//            psMessage = "Invalid Gross Amount Total: " + ldblTranTotl + " . ";
//            return false;
//        }
//        
//        //if (ldblNetTTotl < 0.00){
//        if (ldblNetTTotl.compareTo(new BigDecimal("0.00")) < 0){
//            psMessage = "Invalid Net Amount Due: " + ldblNetTTotl + " . ";
//            return false;
//        }
//        
//        setMaster("nTranTotl",ldblTranTotl);
//        setMaster("nNetTTotl",ldblNetTTotl);
//        setMaster("nLaborAmt",ldblLaborAmt);
//        setMaster("nAccesAmt",ldblAccesAmt);
//        
//        //PO / FINANCING
//        if (!lsPayModex.equals("0")){ 
//            if (getVSPFinanceCount() > 0){
//                BigDecimal ldblFinAmt = new BigDecimal("0.00"); 
//                BigDecimal ldblRatexx = new BigDecimal("0.00"); 
//                BigDecimal ldblMonAmort = new BigDecimal("0.00"); 
//                BigDecimal ldblGrsMonth = new BigDecimal("0.00"); 
//                BigDecimal ldblPNValuex = new BigDecimal("0.00"); 
//                
//                String lsDiscount = String.valueOf( getVSPFinance("nDiscount")); 
//                BigDecimal ldblDiscount = new BigDecimal("0.00");
//                if(lsDiscount != null && !lsDiscount.equals("null")){
//                    ldblDiscount = new BigDecimal(lsDiscount);
//                }
//                
//                
//                String lsNtDwnPmt = String.valueOf( getVSPFinance("nNtDwnPmt")); 
//                BigDecimal ldblNtDwnPmt = new BigDecimal("0.00");
//                if(lsNtDwnPmt != null && !lsNtDwnPmt.equals("null")){
//                    ldblNtDwnPmt = new BigDecimal(lsNtDwnPmt);
//                }
//                
//                String lsRebatesx = String.valueOf( getVSPFinance("nRebatesx")); 
//                BigDecimal ldblRebatesx = new BigDecimal("0.00");
//                if(lsRebatesx != null && !lsRebatesx.equals("null")){
//                    ldblRebatesx = new BigDecimal(lsRebatesx);
//                }
//                
//                String lsAcctRate = String.valueOf( getVSPFinance("nAcctRate")); 
//                BigDecimal ldblAcctRate = new BigDecimal("0.00");
//                if(lsAcctRate != null && !lsAcctRate.equals("null")){
//                    ldblAcctRate = new BigDecimal(lsAcctRate);
//                }
//                
//                
//                int lnAcctTerm = (Integer) getVSPFinance("nAcctTerm");
//                
//                lsUnitPrce = String.valueOf( getMaster("nUnitPrce"));
//                if(lsUnitPrce != null && !lsUnitPrce.equals("null")){
//                    ldblUnitPrce = new BigDecimal(lsUnitPrce);
//                }
//                ldblUnitPrce = ldblUnitPrce.setScale(2, BigDecimal.ROUND_HALF_UP);
//                
//                //-Amount Financed = nUnitPrce -(nDiscount + nNtDwnPmt)
//                //ldblFinAmt = ldblUnitPrce - (ldblDiscount + ldblNtDwnPmt); 
//                ldblFinAmt = ldblUnitPrce.subtract(ldblDiscount.add(ldblNtDwnPmt));
//                ldblFinAmt = ldblFinAmt.setScale(2, BigDecimal.ROUND_HALF_UP); 
//                if (ldblFinAmt.compareTo(new BigDecimal("0.00")) < 0){
//                    psMessage = "Invalid Amount Finance : " + ldblFinAmt + " . ";
//                    return false;
//                }
//                //-Rate = (nAcctRate/100) + 1
//                //ldblRatexx = (ldblAcctRate / 100) + 1; 
//                //ldblRatexx = (ldblAcctRate.divide(100)).add(new BigDecimal("1"));
//                if (ldblAcctRate.compareTo(BigDecimal.ZERO) != 0) {
//                    ldblRatexx = (ldblAcctRate.divide(new BigDecimal("100")));
//                    ldblRatexx = ldblRatexx.setScale(2, BigDecimal.ROUND_HALF_UP); 
//                    ldblRatexx = ldblRatexx.add(new BigDecimal("1"));
//                    ldblRatexx = ldblRatexx.setScale(2, BigDecimal.ROUND_HALF_UP); 
//                }
//                
//                //System.out.println("ldblRatexx " + ldblRatexx);
//                //System.out.println("ldblFinAmt " + ldblFinAmt);
//                //-net Monthly Inst = (Amount Financed * Rate)/Terms Rate
//                //ldblMonAmort = (ldblFinAmt * ldblRatexx) / lnAcctTerm; 
//                
//                if (lnAcctTerm > 0) {
//                    ldblMonAmort = (ldblFinAmt.multiply(ldblRatexx));
//                    ldblMonAmort = ldblMonAmort.setScale(2, BigDecimal.ROUND_HALF_UP); 
//                    //System.out.println("ldblMonAmort >>> " + ldblMonAmort);
//                    //ldblMonAmort = ldblMonAmort.divide(new BigDecimal(String.valueOf(lnAcctTerm)), 2, BigDecimal.ROUND_HALF_UP);
//                    BigDecimal bgTerm = new BigDecimal(String.valueOf(lnAcctTerm));
//                    ldblMonAmort = ldblMonAmort.divide(bgTerm, RoundingMode.HALF_UP);
//                    ldblMonAmort = ldblMonAmort.setScale(2, BigDecimal.ROUND_HALF_UP); 
//                    //System.out.println("lnAcctTerm " + lnAcctTerm);
//                    //System.out.println("ldblMonAmort " + ldblMonAmort);
//                }
//                //-Gross Monthly Inst = Net Monthly Inst + Prompt Payment Disc
//                //ldblGrsMonth = ldblMonAmort + ldblRebatesx; 
//                ldblGrsMonth = ldblMonAmort.add(ldblRebatesx); 
//                ldblGrsMonth = ldblGrsMonth.setScale(2, BigDecimal.ROUND_HALF_UP); 
//                
//                if (ldblGrsMonth.compareTo(new BigDecimal("0.00")) < 0){
//                    psMessage = "Invalid Gross Monthly Installment: " + ldblGrsMonth + " . ";
//                    return false;
//                }
//                //-Promisory Note Amount =Terms Rate * Gross Monthly Inst
//                //ldblPNValuex = lnAcctTerm * ldblGrsMonth; 
//                ldblPNValuex = ldblGrsMonth.multiply(new BigDecimal(String.valueOf(lnAcctTerm))); 
//                ldblPNValuex = ldblPNValuex.setScale(2, BigDecimal.ROUND_HALF_UP); 
//                if (ldblPNValuex.compareTo(new BigDecimal("0.00")) < 0){
//                    psMessage = "Invalid Promissory Note Amount: " + ldblPNValuex + " . ";
//                    return false;
//                }
//                
////                System.out.println("ldblRebatesx " + ldblRebatesx);
////                System.out.println("ldblMonAmort " + ldblMonAmort);
////                System.out.println("ldblGrsMonth " + ldblGrsMonth);
////                System.out.println("lnAcctTerm " + lnAcctTerm);
////                System.out.println("ldblPNValuex " + ldblPNValuex);
//                
//                setVSPFinance("nFinAmtxx",ldblFinAmt);
//                setVSPFinance("nMonAmort",ldblMonAmort);
//                setVSPFinance("nGrsMonth",ldblGrsMonth);
//                setVSPFinance("nPNValuex",ldblPNValuex);
//            }
//        }
//        
////        System.out.println("nTranTotl >>> " + String.valueOf( getMaster("nTranTotl")) ); //Gross Amount
////        System.out.println("nNetTTotl >>> " + String.valueOf(getMaster("nNetTTotl")) ); //Net Amount Due
//        
//        return true;
//    }
}
