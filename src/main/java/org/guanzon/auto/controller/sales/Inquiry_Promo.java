/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.sales;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.auto.model.sales.Model_Inquiry_Promo;
import org.guanzon.auto.model.sales.Model_Inquiry_Promo;
import org.guanzon.auto.validator.sales.ValidatorFactory;
import org.guanzon.auto.validator.sales.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Inquiry_Promo {
    final String XML = "Model_Inquiry_Promo.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    public JSONObject poJSON;
    
    ArrayList<Model_Inquiry_Promo> paDetail;
    ArrayList<Model_Inquiry_Promo> paRemDetail;
    
    public Inquiry_Promo(GRider foAppDrver){
        poGRider = foAppDrver;
    }
    
    public int getEditMode() {
        return pnEditMode;
    }

    public Model_Inquiry_Promo getPromo(int fnIndex){
        if (fnIndex > paDetail.size() - 1 || fnIndex < 0) return null;
        
        return paDetail.get(fnIndex);
    }
    
    public JSONObject addDetail(String fsTransNo){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        
        poJSON = new JSONObject();
        if (paDetail.size()<=0){
            paDetail.add(new Model_Inquiry_Promo(poGRider));
            paDetail.get(0).newRecord();
            
            paDetail.get(0).setValue("sTransNox", fsTransNo);
            poJSON.put("result", "success");
            poJSON.put("message", "Inquiry Promo add record.");
        } else {
            paDetail.add(new Model_Inquiry_Promo(poGRider));
            paDetail.get(paDetail.size()-1).newRecord();

            paDetail.get(paDetail.size()-1).setTransNo(fsTransNo);
            poJSON.put("result", "success");
            poJSON.put("message", "Inquiry Promo add record.");
        }
        return poJSON;
    }
    
    public JSONObject openDetail(String fsValue){
        paDetail = new ArrayList<>();
        paRemDetail = new ArrayList<>();
        poJSON = new JSONObject();
        String lsSQL =    "  SELECT "                                                  
                        + "   sTransNox "  
                        + " , sPromoIDx "                                             
                        + "  FROM customer_inquiry_promo "  ;
        lsSQL = MiscUtil.addCondition(lsSQL, " sTransNox = " + SQLUtil.toSQL(fsValue));
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        System.out.println(lsSQL);
       try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                while(loRS.next()){
                        paDetail.add(new Model_Inquiry_Promo(poGRider));
                        paDetail.get(paDetail.size() - 1).openRecord(loRS.getString("sTransNox"), loRS.getString("sPromoIDx"));
                        
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
        if(paRemDetail != null){
            int lnRemSize = paRemDetail.size() -1;
            if(lnRemSize >= 0){
                for(lnCtr = 0; lnCtr <= lnRemSize; lnCtr++){
                    obj = paRemDetail.get(lnCtr).deleteRecord();
                    if("error".equals((String) obj.get("result"))){
                        return obj;
                    }
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
        
        for (lnCtr = 0; lnCtr <= lnSize; lnCtr++){
            if(paDetail.get(lnCtr).getPromoID().isEmpty()){
                continue; //skip instead of removing the actual detail
//                    paDetail.remove(lnCtr);
//                    lnCtr++;
//                    if(lnCtr > lnSize){
//                        break;
//                    } 
            }
            
            paDetail.get(lnCtr).setTransNo(fsTransNo);
            
            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Inquiry_Promo, paDetail.get(lnCtr));
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
    
    public ArrayList<Model_Inquiry_Promo> getDetailList(){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        return paDetail;
    }
    public void setDetailList(ArrayList<Model_Inquiry_Promo> foObj){this.paDetail = foObj;}
    
    public void setDetail(int fnRow, int fnIndex, Object foValue){ paDetail.get(fnRow).setValue(fnIndex, foValue);}
    public void setDetail(int fnRow, String fsIndex, Object foValue){ paDetail.get(fnRow).setValue(fsIndex, foValue);}
    public Object getDetail(int fnRow, int fnIndex){return paDetail.get(fnRow).getValue(fnIndex);}
    public Object getDetail(int fnRow, String fsIndex){return paDetail.get(fnRow).getValue(fsIndex);}
    
    
    public Object removeDetail(int fnRow){
        JSONObject loJSON = new JSONObject();
        
        if(paDetail.get(fnRow).getEntryBy() != null){
            if(!paDetail.get(fnRow).getEntryBy().trim().isEmpty()){
                RemoveDetail(fnRow);
            }
        }
        
        paDetail.remove(fnRow);
        return loJSON;
    }
    
    private JSONObject RemoveDetail(Integer fnRow){
        
        if(paRemDetail == null){
           paRemDetail = new ArrayList<>();
        }
        
        poJSON = new JSONObject();
        if (paRemDetail.size()<=0){
            paRemDetail.add(new Model_Inquiry_Promo(poGRider));
            paRemDetail.get(0).openRecord(paDetail.get(fnRow).getTransNo(),paDetail.get(fnRow).getPromoID());
            poJSON.put("result", "success");
            poJSON.put("message", "added to remove record.");
        } else {
            paRemDetail.add(new Model_Inquiry_Promo(poGRider));
            paRemDetail.get(paRemDetail.size()-1).openRecord(paDetail.get(fnRow).getTransNo(),paDetail.get(fnRow).getPromoID());
            poJSON.put("result", "success");
            poJSON.put("message", "added to remove record.");
        }
        return poJSON;
    }
    
    public JSONObject searchPromo(Date fdInqDate) {
        poJSON = new JSONObject();
        String lsHeader = "Actvitiy Date From»Actvitiy Date To»Activity ID»Activity No»Activity Title";
        String lsColName = "dDateFrom»dDateThru»sActvtyID»sActNoxxx»sActTitle"; 
        String lsCriteria = "dDateFrom»dDateThru»sActvtyID»sActNoxxx»sActTitle";
        
        String lsSQL =    " SELECT "                                                                   
                        + "   a.sActvtyID "                                                            
                        + " , a.sActNoxxx "                                                            
                        + " , a.sActTitle "                                                           
                        + " , a.sActTypID "                                                           
                        + " , a.dDateFrom "                                                            
                        + " , a.dDateThru "                                                           
                        + " , a.cTranStat "                                                             
                        + " , b.sEventTyp "                                                            
                        + " FROM activity_master a"                                                       
                        + " LEFT JOIN event_type b ON b.sActTypID = a.sActTypID "   ;  
                
        lsSQL = MiscUtil.addCondition(lsSQL, " a.cTranStat = '3' AND b.sEventTyp = 'pro' " 
                                                + " AND a.dDateFrom <=" + SQLUtil.toSQL(fdInqDate)
                                                + " AND a.dDateThru >=" + SQLUtil.toSQL(fdInqDate));  

        System.out.println("SEARCH PROMO ACTIVITY: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                "",
                    lsHeader,
                    lsColName,
                    lsCriteria,
                0);

        if (poJSON != null) {
            if(!"error".equals((String) poJSON.get("result"))){
//                addDetail(fsTransNo);
//                setDetail(paDetail.size()-1,"sPromoIDx", (String) poJSON.get("sActvtyID"));
//                setDetail(paDetail.size()-1,"sActNoxxx", (String) poJSON.get("sActNoxxx"));
//                setDetail(paDetail.size()-1,"sActTitle", (String) poJSON.get("sActTitle"));
//                setDetail(paDetail.size()-1,"dDateFrom", (String) poJSON.get("dDateFrom"));
//                setDetail(paDetail.size()-1,"dDateThru", (String) poJSON.get("dDateThru"));
//                poJSON.put("result", "success");
//                poJSON.put("message", "Promo added successfully.");
            } 
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
        
        return poJSON;
    }
    
}
