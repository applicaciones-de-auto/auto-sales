/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.sales;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.auto.general.CancelForm;
import org.guanzon.auto.model.sales.Model_Inquiry_Reservation;
import org.guanzon.auto.validator.sales.ValidatorFactory;
import org.guanzon.auto.validator.sales.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Inquiry_Reservation {
    final String XML = "Model_Inquiry_Promo.xml";
    GRider poGRider;
    String psTargetBranchCd = "";
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    public JSONObject poJSON;
    
    ArrayList<Model_Inquiry_Reservation> paDetail;
    ArrayList<Model_Inquiry_Reservation> paRemDetail;
    
    public Inquiry_Reservation(GRider foAppDrver){
        poGRider = foAppDrver;
    }
    
    public int getEditMode() {
        return pnEditMode;
    }

    public Model_Inquiry_Reservation getReservation(int fnIndex){
        if (fnIndex > paDetail.size() - 1 || fnIndex < 0) return null;
        
        return paDetail.get(fnIndex);
    }
    
    public JSONObject addDetail(String fsSourceCD, String fsSourceNo, String fsClientID){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        
        poJSON = new JSONObject();
        if (paDetail.size()<=0){
            paDetail.add(new Model_Inquiry_Reservation(poGRider));
            paDetail.get(0).newRecord();
            
            paDetail.get(0).setValue("sClientID", fsClientID);
            paDetail.get(0).setValue("sSourceCD", fsSourceCD);
            paDetail.get(0).setValue("sSourceNo", fsSourceNo);
            poJSON.put("result", "success");
            poJSON.put("message", "Reservation add record.");
        } else {
            paDetail.add(new Model_Inquiry_Reservation(poGRider));
            paDetail.get(paDetail.size()-1).newRecord();

            paDetail.get(paDetail.size()-1).setClientID(fsClientID);
            paDetail.get(paDetail.size()-1).setSourceCD(fsSourceCD);
            paDetail.get(paDetail.size()-1).setSourceNo(fsSourceNo);
            poJSON.put("result", "success");
            poJSON.put("message", "Reservation add record.");
        }
        return poJSON;
    }
    
    /**
     * LOAD INQUIRY RESERVATION
     * @param fsValue store Inquiry sTransNox or VSP sTransNox
     * @param fbIsInq set true when retrieving for Inquiry else false when retrieving for VSP
     * @param fbOthRsv set true when retrieving for other reservation that is not equal to actual Inquiry sTransNox
     * @return 
     */
    public JSONObject openDetail(String fsValue, boolean fbIsInq, boolean fbOthRsv){
        paDetail = new ArrayList<>();
        paRemDetail = new ArrayList<>();
        poJSON = new JSONObject();
        String lsSQL =    " SELECT "                                                     
                        + "   a.sTransNox "                                              
                        + " , a.sReferNox "                                              
                        + " , a.sClientID "                                              
                        + " , a.sSourceNo "                                              
                        + " , a.sTransIDx "                                              
                        + " , a.sApproved "                                               
                        + " , a.cTranStat "                                            
                        + " , b.sReferNox "                                              
                        + " FROM customer_inquiry_reservation a "                        
                        + " LEFT JOIN si_master_source b ON b.sReferNox = a.sTransNox "  
                        + " LEFT JOIN si_master c ON c.sTransNox = b.sTransNox "         ;
        if(!fbOthRsv){
            if(fbIsInq){
                lsSQL = MiscUtil.addCondition(lsSQL, " a.sSourceNo = " + SQLUtil.toSQL(fsValue));
            } else {
                lsSQL = MiscUtil.addCondition(lsSQL, " a.sTransIDx = " + SQLUtil.toSQL(fsValue));
            }
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL, " a.cTranStat = '2' "
                                                  + " AND (c.sReferNox <> NULL OR TRIM(c.sReferNox) <> '')"
                                                  //+ " AND (a.sTransIDx = NULL OR TRIM(a.sTransIDx) = '')"
                                                  + " AND a.sSourceNo <> " + SQLUtil.toSQL(fsValue));
        }
        
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        System.out.println(lsSQL);
       try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                while(loRS.next()){
                        paDetail.add(new Model_Inquiry_Reservation(poGRider));
                        paDetail.get(paDetail.size() - 1).openRecord(loRS.getString("sTransNox"));
                        
                        pnEditMode = EditMode.UPDATE;
                        lnctr++;
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record loaded successfully.");
                    } 
                
            }else{
//                paDetail = new ArrayList<>();
//                addDetail(fsValue);
                poJSON.put("result", "error");
                poJSON.put("continue", true);
                poJSON.put("message", "No record selected.");
            }
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        return poJSON;
    }
    
    public JSONObject openRecord(String fsValue){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        
        poJSON = new JSONObject();
        paDetail.add(new Model_Inquiry_Reservation(poGRider));
        poJSON = paDetail.get(paDetail.size() - 1).openRecord(fsValue);
        
        if(!"error".equals((String) poJSON.get("result"))){
            pnEditMode = EditMode.UPDATE;
            poJSON.put("result", "success");
            poJSON.put("message", "Record loaded successfully.");
        }
        
        return poJSON;
    }
    
    public JSONObject saveDetail(String fsTransNo){
        JSONObject obj = new JSONObject();
        int lnCtr;
        
        if(paRemDetail != null){
            int lnRemSize = paRemDetail.size() -1;
            if(lnRemSize >= 0){
                for(lnCtr = 0; lnCtr <= lnRemSize; lnCtr++){
                    paRemDetail.get(lnCtr).setTransID("");
                    obj = paRemDetail.get(lnCtr).saveRecord();
                }
            }
        }
        
        if(paDetail == null){
            obj.put("result", "error");
            obj.put("continue", true);
            return obj;
        }
        
        int lnSize = paDetail.size() -1;
        if(lnSize < 0){
            obj.put("result", "error");
            obj.put("continue", true);
            return obj;
        }
        
        obj = validateReservationSum();
        if("error".equals((String) obj.get("result"))){
            obj.put("continue", false);
            return obj;
        }
        
        if(psTargetBranchCd == null){
            obj.put("result", "error");
            obj.put("continue", false);
            obj.put("message", "Target Branch code for reservation cannot be empty.");
            return obj;
        } else {
            if(psTargetBranchCd.isEmpty()){
                obj.put("result", "error");
                obj.put("continue", false);
                obj.put("message", "Target Branch code for reservation cannot be empty.");
                return obj;
            }
        }
        
        for (lnCtr = 0; lnCtr <= lnSize; lnCtr++){
            if(paDetail.get(lnCtr).getAmount() == null ){
                continue; //skip, instead of removing the actual detail
            }
            
            if(paDetail.get(lnCtr).getAmount() <= 0){
                continue; //skip, instead of removing the actual detail
            }
            
            if(paDetail.get(lnCtr).getAmount() <= 0.00){
                continue; //skip, instead of removing the actual detail
            }
            
            
            paDetail.get(lnCtr).setTargetBranchCd(psTargetBranchCd);
//            paDetail.get(lnCtr).setReferNo(MiscUtil.getNextCode(paDetail.get(lnCtr).getTable(), "sReferNox", true, poGRider.getConnection(), poGRider.getBranchCode()));
               
            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Inquiry_Reservation, paDetail.get(lnCtr));
            validator.setGRider(poGRider);
            if (!validator.isEntryOkay()){
                obj.put("result", "error");
                obj.put("message", validator.getMessage());
                return obj;
            }
            obj = paDetail.get(lnCtr).saveRecord();
        }    
        
        return obj;
    }
    
    public void setTargetBranchCd(String fsBranchCd){
        psTargetBranchCd = fsBranchCd; 
    }
    
    private JSONObject validateReservationSum(){
        JSONObject loJSON = new JSONObject();
        
        String lsValue = "0.00";
        try {
            String lsStandardSets = "SELECT sValuexxx FROM xxxstandard_sets WHERE sDescript = 'vhclreservation_max_amt'";
            System.out.println("CHECK STANDARD SETS FROM vhclreservation_max_amt : " + lsStandardSets);
            ResultSet loRS = poGRider.executeQuery(lsStandardSets);
            //Check for existing inquiry with same SE
            if (MiscUtil.RecordCount(loRS) > 0){
                    while(loRS.next()){
                        lsValue = loRS.getString("sValuexxx");
                    }

                    MiscUtil.close(loRS);
            }else {
                loJSON.put("result", "error");
                loJSON.put("message", "Notify System Administrator to config Standard set for `vhclreservation_max_amt`.");
                return loJSON;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Inquiry_Reservation.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //sum reservation amount should not be greater than the setted maximum amount on vehicle advances
        double ldblRsvSum = 0.00;
        for (int lnCtr = 0; lnCtr <= paDetail.size() -1; lnCtr++){
            if(!paDetail.get(lnCtr).getTranStat().equals("0")){
                ldblRsvSum = ldblRsvSum + paDetail.get(lnCtr).getAmount();
            }
        }
        
//        if(ldblRsvSum <= 0 || ldblRsvSum <= 0.00){
//            loJSON.put("result", "error");
//            loJSON.put("message", "Invalid reservation amount");
//            return loJSON;
//        }
        
        DecimalFormat lDcmFmt = new DecimalFormat("#,##0.00");
        if(ldblRsvSum > Double.parseDouble(lsValue)){
            loJSON.put("result", "error");
            loJSON.put("message", "Reservation cannot be exceed from the amount of " + lDcmFmt.format(Double.parseDouble(lsValue)) + ".");
            return loJSON;
        }
        
        return loJSON;
    }
    
    public ArrayList<Model_Inquiry_Reservation> getDetailList(){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        return paDetail;
    }
    public void setDetailList(ArrayList<Model_Inquiry_Reservation> foObj){this.paDetail = foObj;}
    
    public void setDetail(int fnRow, int fnIndex, Object foValue){ paDetail.get(fnRow).setValue(fnIndex, foValue);}
    public void setDetail(int fnRow, String fsIndex, Object foValue){ paDetail.get(fnRow).setValue(fsIndex, foValue);}
    public Object getDetail(int fnRow, int fnIndex){return paDetail.get(fnRow).getValue(fnIndex);}
    public Object getDetail(int fnRow, String fsIndex){return paDetail.get(fnRow).getValue(fsIndex);}
    
    public Model_Inquiry_Reservation getDetailModel(int fnRow) {
        return paDetail.get(fnRow);
    }
    
    public JSONObject removeDetail(int fnRow, boolean fbIsInq){
        JSONObject loJSON = new JSONObject();
        
        if(fbIsInq){
            if(paDetail.get(fnRow).getEntryBy()== null){
                paDetail.remove(fnRow);
            } else {
                if(paDetail.get(fnRow).getEntryBy().trim().isEmpty()){
                    paDetail.remove(fnRow);
                } else {
                    loJSON.put("result", "error");
                    loJSON.put("message", "Reservation No. "+paDetail.get(fnRow).getReferNo()+" is already saved.\n\nCancel reservation instead.");
                    return loJSON;
                }
            }
        } else {
            if(paDetail.get(fnRow).getTransID() != null){
                if(!paDetail.get(fnRow).getTransID().trim().isEmpty()){
                    RemoveDetail(fnRow);
                }
            }
            
            paDetail.remove(fnRow);
            
        }
        
        return loJSON;
    }
    
    
    private JSONObject RemoveDetail(Integer fnRow){
        
        if(paRemDetail == null){
           paRemDetail = new ArrayList<>();
        }
        
        poJSON = new JSONObject();
        if (paRemDetail.size()<=0){
            paRemDetail.add(new Model_Inquiry_Reservation(poGRider));
            paRemDetail.get(0).openRecord(paDetail.get(fnRow).getTransNo());
            poJSON.put("result", "success");
            poJSON.put("message", "added to remove record.");
        } else {
            paRemDetail.add(new Model_Inquiry_Reservation(poGRider));
            paRemDetail.get(paRemDetail.size()-1).openRecord(paDetail.get(fnRow).getTransNo());
            poJSON.put("result", "success");
            poJSON.put("message", "added to remove record.");
        }
        return poJSON;
    }
    
    public JSONObject cancelReservation(int fnRow){
        JSONObject loJSON = new JSONObject();
        int lnRsvRow = fnRow + 1;
        try {
            if(paDetail.get(fnRow).getReferNo() == null){
                loJSON.put("result", "error");
                loJSON.put("message", "Reservation row "+lnRsvRow+" is not yet save.\n\nRemove this instead.");
                return loJSON;
            } else {
                if(paDetail.get(fnRow).getReferNo().isEmpty()){
                    loJSON.put("result", "error");
                    loJSON.put("message", "Reservation row "+lnRsvRow+" is not yet save.\n\nRemove this instead.");
                    return loJSON;
                }
            }
            
            if(paDetail.get(fnRow).getTranStat().equals("2")){
                loJSON.put("result", "error");
                loJSON.put("message", "Reservation No. "+paDetail.get(fnRow).getReferNo()+" is already approved.\n\nCancellation aborted.");
                return loJSON;
            }
            
            if(paDetail.get(fnRow).getTranStat().equals("0")){
                loJSON.put("result", "error");
                loJSON.put("message", "Reservation No. "+paDetail.get(fnRow).getReferNo()+" is already cancelled.");
                return loJSON;
            } 
           
            CancelForm cancelform = new CancelForm();
            if (!cancelform.loadCancelWindow(poGRider, paDetail.get(fnRow).getReferNo(), paDetail.get(fnRow).getTransNo(), "RESERVATION")) {
                poJSON.put("result", "error");
                poJSON.put("message", "Cancellation failed.");
                return poJSON;
            }
            
            paDetail.get(fnRow).setTranStat("0");
            paDetail.get(fnRow).setTargetBranchCd(psTargetBranchCd);
            loJSON = paDetail.get(fnRow).saveRecord(); //paDetail.get(fnRow).getTransNo());
            
        } catch (SQLException ex) {
            Logger.getLogger(Inquiry_Reservation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return loJSON;
    }
    
}
