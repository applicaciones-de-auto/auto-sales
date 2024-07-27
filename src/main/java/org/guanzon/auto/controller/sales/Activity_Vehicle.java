/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.sales;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.auto.model.sales.Model_Activity_Vehicle;
import org.guanzon.auto.validator.sales.ValidatorFactory;
import org.guanzon.auto.validator.sales.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Activity_Vehicle  {
    final String XML = "Model_Activity_Vehicle.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    public JSONObject poJSON;
    
    ArrayList<Model_Activity_Vehicle> paDetail;
    ArrayList<Model_Activity_Vehicle> paRemDetail;
    
    ArrayList<String> paSerialID;
    ArrayList<String> paVehicleDesc;
    ArrayList<String> paVehicleCSNo;
    
    public Activity_Vehicle(GRider foAppDrver){
        poGRider = foAppDrver;
    }

    public int getEditMode() {
        return pnEditMode;
    }
    
    public Model_Activity_Vehicle getDetail(int fnIndex){
        if (fnIndex > paDetail.size() - 1 || fnIndex < 0) return null;
        
        return paDetail.get(fnIndex);
    }
    
    public JSONObject addDetail(String fsTransNo){
        
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        
        poJSON = new JSONObject();
        if (paDetail.size()<=0){
            paDetail.add(new Model_Activity_Vehicle(poGRider));
            paDetail.get(0).newRecord();
            
            paDetail.get(0).setValue("sTransNox", fsTransNo);
            paDetail.get(paDetail.size()-1).setEntryNo(0);
            poJSON.put("result", "success");
            poJSON.put("message", "Activity Vehicle add record.");
        } else {
            paDetail.add(new Model_Activity_Vehicle(poGRider));
            paDetail.get(paDetail.size()-1).newRecord();

            paDetail.get(paDetail.size()-1).setTransNo(fsTransNo);
            paDetail.get(paDetail.size()-1).setEntryNo(0);
            poJSON.put("result", "success");
            poJSON.put("message", "Activity Vehicle add record.");
        }
        return poJSON;
    }
    
    public JSONObject openDetail(String fsValue){
        paDetail = new ArrayList<>();
        paRemDetail = new ArrayList<>();
        poJSON = new JSONObject();
        String lsSQL =    "  SELECT "                                                  
                        + "  sTransNox "                                             
                        + ", nEntryNox "                                             
                        + ", sSerialID "                                             
                        + "  FROM activity_vehicle "  ;
        lsSQL = MiscUtil.addCondition(lsSQL, " sTransNox = " + SQLUtil.toSQL(fsValue));
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        System.out.println(lsSQL);
       try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                while(loRS.next()){
                        paDetail.add(new Model_Activity_Vehicle(poGRider));
                        paDetail.get(paDetail.size() - 1).openRecord(loRS.getString("sTransNox"), loRS.getString("sSerialID"));
                        
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
            if(lnCtr>0){
                if(paDetail.get(lnCtr).getSerialID().isEmpty()){
                    paDetail.remove(lnCtr);
                    lnCtr++;
                    if(lnCtr > lnSize){
                        break;
                    } 
                }
            }
            
            paDetail.get(lnCtr).setTransNo(fsTransNo);
            paDetail.get(lnCtr).setEntryNo(lnCtr+1);
            
            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Activity_Vehicle, paDetail.get(lnCtr));
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
    
    public ArrayList<Model_Activity_Vehicle> getDetailList(){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        return paDetail;
    }
    public void setDetailList(ArrayList<Model_Activity_Vehicle> foObj){this.paDetail = foObj;}
    
    public void setDetail(int fnRow, int fnIndex, Object foValue){ paDetail.get(fnRow).setValue(fnIndex, foValue);}
    public void setDetail(int fnRow, String fsIndex, Object foValue){ paDetail.get(fnRow).setValue(fsIndex, foValue);}
    public Object getDetail(int fnRow, int fnIndex){return paDetail.get(fnRow).getValue(fnIndex);}
    public Object getDetail(int fnRow, String fsIndex){return paDetail.get(fnRow).getValue(fsIndex);}
    
    
    public Object removeDetail(int fnRow){
        JSONObject loJSON = new JSONObject();
        
        if(paDetail.get(fnRow).getEntryNo() != 0){
            RemoveDetail(fnRow);
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
            paRemDetail.add(new Model_Activity_Vehicle(poGRider));
            paRemDetail.get(0).openRecord(paDetail.get(fnRow).getTransNo(),paDetail.get(fnRow).getSerialID());
//            paRemDetail.get(0).setValue("sTransNox", paDetail.get(fnRow).getTransNo());
//            paRemDetail.get(0).setValue("nEntryNox", paDetail.get(fnRow).getEntryNo());
//            paRemDetail.get(0).setValue("sSerialID", paDetail.get(fnRow).getSerialID());
            poJSON.put("result", "success");
            poJSON.put("message", "added to remove record.");
        } else {
            paRemDetail.add(new Model_Activity_Vehicle(poGRider));
            paRemDetail.get(paRemDetail.size()-1).openRecord(paDetail.get(fnRow).getTransNo(),paDetail.get(fnRow).getSerialID());
//            paRemDetail.get(paRemDetail.size()-1).setValue("sTransNox", paDetail.get(fnRow).getTransNo());
//            paRemDetail.get(paRemDetail.size()-1).setValue("nEntryNox", paDetail.get(fnRow).getEntryNo());
//            paRemDetail.get(paRemDetail.size()-1).setValue("sSerialID", paDetail.get(fnRow).getSerialID());
            poJSON.put("result", "success");
            poJSON.put("message", "added to remove record.");
        }
        return poJSON;
    }
    
    /**
     * Loads the department data.
     * @return {@code true} if the department data is successfully loaded,
     * {@code false} otherwise
     */
    public JSONObject loadVehicle() {
        paSerialID = new ArrayList<>();
        paVehicleDesc = new ArrayList<>();
        paVehicleCSNo = new ArrayList<>();
        JSONObject jObj = new JSONObject();
        String lsSQL =    "  SELECT  " 
                        + "  a.sSerialID " 
                        + ", a.sCSNoxxxx " 
                        + ", b.sDescript " 
                        + ", c.sPlateNox " 
                        + "  FROM vehicle_serial a " 
                        + "  LEFT JOIN vehicle_master b ON b.sVhclIDxx = a.sVhclIDxx "
                        + "  LEFT JOIN vehicle_serial_registration c ON c.sSerialID = a.sSerialID ";
        lsSQL = MiscUtil.addCondition(lsSQL, " (a.cSoldStat = '1' ) AND (ISNULL(a.sClientID) OR TRIM(a.sClientID) = '' ) ")
                + " ORDER BY sDescript DESC ";
        
        System.out.println(lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                while(loRS.next()){
                    paSerialID.add(lnctr, loRS.getString("sSerialID"));
                    paVehicleDesc.add(lnctr, loRS.getString("sDescript"));
                    
                    if(loRS.getString("sCSNoxxxx") == null){
                        paVehicleCSNo.add(lnctr, loRS.getString("sPlateNox"));
                    } else {
                        if(loRS.getString("sCSNoxxxx").trim().isEmpty()){
                            paVehicleCSNo.add(lnctr, loRS.getString("sPlateNox"));
                        } else {
                            paVehicleCSNo.add(lnctr, loRS.getString("sCSNoxxxx"));
                        }
                    }
                    
                } 
                     
                if(paSerialID.size() == paVehicleDesc.size()){
                    jObj.put("result", "success");
                    jObj.put("message", "Vehicle loaded successfully.");
                } else {
                    jObj.put("result", "error");
                    jObj.put("message", "Vehicle did not load properly.");
                }
                
            }else{
                jObj.put("result", "error");
                jObj.put("message", "No Vehicle loaded.");
            }
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            jObj.put("result", "error");
            jObj.put("message", e.getMessage());
        }
       
        return jObj;
    }
    
    public ArrayList<String> getVehicleList(){return paSerialID;}
    public Object getSerialID(int fnRow, int fnIndex){return paSerialID.get(fnRow);}
    public Object getSerialID(int fnRow, String fsIndex){return paSerialID.get(fnRow);}
    
    public Object getVehicleDesc(int fnRow, int fnIndex){return paVehicleDesc.get(fnRow);}
    public Object getVehicleDesc(int fnRow, String fsIndex){return paVehicleDesc.get(fnRow);}
    
    public Object getVehicleCSNo(int fnRow, int fnIndex){return paVehicleCSNo.get(fnRow);}
    public Object getVehicleCSNo(int fnRow, String fsIndex){return paVehicleCSNo.get(fnRow);}
    
}
