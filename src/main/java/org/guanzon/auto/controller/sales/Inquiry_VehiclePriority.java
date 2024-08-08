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
import org.guanzon.auto.model.sales.Model_Inquiry_VehiclePriority;
import org.guanzon.auto.validator.sales.ValidatorFactory;
import org.guanzon.auto.validator.sales.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Inquiry_VehiclePriority {
    final String XML = "Model_Inquiry_Promo.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    public JSONObject poJSON;
    
    ArrayList<Model_Inquiry_VehiclePriority> paDetail;
    ArrayList<Model_Inquiry_VehiclePriority> paRemDetail;
    
    public Inquiry_VehiclePriority(GRider foAppDrver){
        poGRider = foAppDrver;
    }
    
    public int getEditMode() {
        return pnEditMode;
    }

    public Model_Inquiry_VehiclePriority getVehiclePriority(int fnIndex){
        if (fnIndex > paDetail.size() - 1 || fnIndex < 0) return null;
        
        return paDetail.get(fnIndex);
    }
    
    public JSONObject addDetail(String fsTransNo){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        
        poJSON = new JSONObject();
        if (paDetail.size()<=0){
            paDetail.add(new Model_Inquiry_VehiclePriority(poGRider));
            paDetail.get(0).newRecord();
            
            paDetail.get(0).setValue("sTransNox", fsTransNo);
            paDetail.get(paDetail.size()-1).setPriority(1);
            poJSON.put("result", "success");
            poJSON.put("message", "Inquiry Vehicle add record.");
        } else {
            paDetail.add(new Model_Inquiry_VehiclePriority(poGRider));
            paDetail.get(paDetail.size()-1).newRecord();

            paDetail.get(paDetail.size()-1).setTransNo(fsTransNo);
            paDetail.get(paDetail.size()-1).setPriority(paDetail.size());
            poJSON.put("result", "success");
            poJSON.put("message", "Inquiry Vehicle add record.");
        }
        return poJSON;
    }
    
    public JSONObject openDetail(String fsValue){
        paDetail = new ArrayList<>();
        paRemDetail = new ArrayList<>();
        poJSON = new JSONObject();
        String lsSQL =    "  SELECT "                                                  
                        + "  sTransNox "                                             
                        + ", nPriority "                                             
                        + ", sVhclIDxx "                                             
                        + "  FROM customer_inquiry_vehicle_priority "  ;
        lsSQL = MiscUtil.addCondition(lsSQL, " sTransNox = " + SQLUtil.toSQL(fsValue));
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        System.out.println(lsSQL);
       try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                while(loRS.next()){
                        paDetail.add(new Model_Inquiry_VehiclePriority(poGRider));
                        paDetail.get(paDetail.size() - 1).openRecord(loRS.getString("sTransNox"), loRS.getString("sVhclIDxx"));
                        
                        pnEditMode = EditMode.UPDATE;
                        lnctr++;
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record loaded successfully.");
                    } 
                
            }else{
//                paDetail = new ArrayList<>();
//                addDetail(fsValue);
                poJSON.put("result", "error");
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
            //if(lnCtr>0){
                if(paDetail.get(lnCtr).getVhclID().isEmpty()){
                    continue; //skip, instead of removing the actual detail
//                    paDetail.remove(lnCtr);
//                    lnCtr++;
//                    if(lnCtr > lnSize){
//                        break;
//                    } 
                }
            //}
            
            paDetail.get(lnCtr).setTransNo(fsTransNo);
            
            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Inquiry_Vehicle_Priority, paDetail.get(lnCtr));
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
    
    public ArrayList<Model_Inquiry_VehiclePriority> getDetailList(){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        return paDetail;
    }
    public void setDetailList(ArrayList<Model_Inquiry_VehiclePriority> foObj){this.paDetail = foObj;}
    
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
            paRemDetail.add(new Model_Inquiry_VehiclePriority(poGRider));
            paRemDetail.get(0).openRecord(paDetail.get(fnRow).getTransNo(),paDetail.get(fnRow).getVhclID());
            poJSON.put("result", "success");
            poJSON.put("message", "added to remove record.");
        } else {
            paRemDetail.add(new Model_Inquiry_VehiclePriority(poGRider));
            paRemDetail.get(paRemDetail.size()-1).openRecord(paDetail.get(fnRow).getTransNo(),paDetail.get(fnRow).getVhclID());
            poJSON.put("result", "success");
            poJSON.put("message", "added to remove record.");
        }
        return poJSON;
    }
    
    public JSONObject searchVehicle() {
        poJSON = new JSONObject();
        String lsHeader = "ID»Description";
        String lsColName = "sVhclIDxx»sDescript"; 
        String lsCriteria = "a.sVhclIDxx»a.sDescript";
        
        String lsSQL =   " SELECT "
                    + "  sDescript "
                    + " , sValuexxx "
                    + " FROM xxxstandard_sets "
                    + " WHERE sDescript = 'mainproduct' ";
        System.out.println("MAIN PRODUCT CHECK: " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        lsSQL =   " SELECT "                                             
                + "   a.sVhclIDxx "                                      
                + " , a.sDescript "                                      
                + " , a.cRecdStat "                                      
                + " , b.sMakeDesc "                                      
                + " FROM vehicle_master a"                                
                + " LEFT JOIN vehicle_make b ON b.sMakeIDxx = a.sMakeIDxx" ; 
        if (MiscUtil.RecordCount(loRS) > 0){
            lsSQL = MiscUtil.addCondition(lsSQL,  " a.cRecdStat = '1' AND b.sMakeDesc = (SELECT sValuexxx FROM xxxstandard_sets WHERE sDescript = 'mainproduct') ");
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL,  " a.cRecdStat = '1' ");
        }
        
        System.out.println("SEARCH VEHICLE MASTER: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                "",
                    lsHeader,
                    lsColName,
                    lsCriteria,
                1);

        if (poJSON != null) {
//            if(!"error".equals((String) poJSON.get("result"))){
//                addDetail(fsTransNo);
//                setDetail(paDetail.size()-1,"sVhclIDxx", (String) poJSON.get("sVhclIDxx"));
//                setDetail(paDetail.size()-1,"sDescript", (String) poJSON.get("sDescript"));
//                poJSON.put("result", "success");
//                poJSON.put("message", "Vehicle Priority added successfully.");
//            } 
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
        
        return poJSON;
    }
    
    
}
