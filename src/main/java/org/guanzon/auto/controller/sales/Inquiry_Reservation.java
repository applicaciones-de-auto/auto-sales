/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.sales;

import java.sql.ResultSet;
import java.sql.SQLException;
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
    String psBranchCd;
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
    
    public JSONObject openDetail(String fsValue){
        paDetail = new ArrayList<>();
        paRemDetail = new ArrayList<>();
        poJSON = new JSONObject();
        String lsSQL =    "  SELECT "                                                  
                        + "  sTransNox "                                             
                        + ", sReferNox "                                             
                        + ", sClientID "                                             
                        + ", sSourceNo "                                            
                        + "  FROM customer_inquiry_reservation "  ;
        lsSQL = MiscUtil.addCondition(lsSQL, " sSourceNo = " + SQLUtil.toSQL(fsValue));
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
    
    public JSONObject saveDetail(String fsTransNo){
        JSONObject obj = new JSONObject();
        
        int lnCtr;
        
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
        
        for (lnCtr = 0; lnCtr <= lnSize; lnCtr++){
            if(paDetail.get(lnCtr).getAmount() == null ){
                continue; //skip, instead of removing the actual detail
            } else if(paDetail.get(lnCtr).getAmount() == 0){
                continue; //skip, instead of removing the actual detail
            } else if(paDetail.get(lnCtr).getAmount() == 0.00){
                continue; //skip, instead of removing the actual detail
            }
            
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
    
    
    public JSONObject removeDetail(int fnRow){
        JSONObject loJSON = new JSONObject();
        
        if(paDetail.get(fnRow).getEntryBy()== null){
            paDetail.remove(fnRow);
        } else {
            if(paDetail.get(fnRow).getEntryBy().trim().isEmpty()){
                paDetail.remove(fnRow);
            } else {
                loJSON.put("result", "error");
                loJSON.put("message", "Reservation No. "+paDetail.get(fnRow).getReferNo()+" is already saved cannot be removed.\nReservation needs to be cancelled.");
                return loJSON;
            }
        }
        
        return loJSON;
    }
    
    public JSONObject cancelReservation(int fnRow){
        JSONObject loJSON = new JSONObject();
        try {
            CancelForm cancelform = new CancelForm();
            if (!cancelform.loadCancelWindow(poGRider, paDetail.get(fnRow).getReferNo(), paDetail.get(fnRow).getTransNo(), "RESERVATION")) {
                poJSON.put("result", "error");
                poJSON.put("message", "Cancellation failed.");
                return poJSON;
            }
            
            paDetail.get(fnRow).setTranStat("0");
            loJSON = paDetail.get(fnRow).saveRecord(); //paDetail.get(fnRow).getTransNo());
            
        } catch (SQLException ex) {
            Logger.getLogger(Inquiry_Reservation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return loJSON;
    }
    
}
