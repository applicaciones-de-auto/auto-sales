/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.sales;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.auto.model.sales.Model_Activity_Location;
import org.guanzon.auto.validator.sales.ValidatorFactory;
import org.guanzon.auto.validator.sales.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Activity_Location {
    final String XML = "Model_Activity_Location.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    public JSONObject poJSON;
    
    ArrayList<Model_Activity_Location> paDetail;
    
    public Activity_Location(GRider foAppDrver){
        poGRider = foAppDrver;
    }

    public int getEditMode() {
        return pnEditMode;
    }
    
    public Model_Activity_Location getDetail(int fnIndex){
        if (fnIndex > paDetail.size() - 1 || fnIndex < 0) return null;
        
        return paDetail.get(fnIndex);
    }
    
    public JSONObject addDetail(String fsTransNo){
        
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        
        poJSON = new JSONObject();
        if (paDetail.size()<=0){
            paDetail.add(new Model_Activity_Location(poGRider));
            paDetail.get(0).newRecord();
            paDetail.get(0).setValue("sTransNox", fsTransNo);
            poJSON.put("result", "success");
            poJSON.put("message", "Activity Location add record.");
        } else {
            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Activity_Location, paDetail.get(paDetail.size()-1));
            validator.setGRider(poGRider);
            if(!validator.isEntryOkay()){
                poJSON.put("result", "error");
                poJSON.put("message", validator.getMessage());
                return poJSON;
            }
            paDetail.add(new Model_Activity_Location(poGRider));
            paDetail.get(paDetail.size()-1).newRecord();

            paDetail.get(paDetail.size()-1).setTransNo(fsTransNo);
            
            poJSON.put("result", "success");
            poJSON.put("message", "Activity Location add record.");
        }
        return poJSON;
    }
    
    public JSONObject openDetail(String fsValue){
        paDetail = new ArrayList<>();
        poJSON = new JSONObject();
        String lsSQL =    "  SELECT "              
                        + "    sTransNox "      
                        + "  , sTownIDxx "      
                        + "  , sAddressx "       
                        + " FROM activity_town " ;
        lsSQL = MiscUtil.addCondition(lsSQL, " sTransNox = " + SQLUtil.toSQL(fsValue));
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        System.out.println(lsSQL);
       try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                while(loRS.next()){
                        paDetail.add(new Model_Activity_Location(poGRider));
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
        
        if(paDetail == null){
            obj.put("result", "error");
            obj.put("continue", false);
            return obj;
        }
        
        int lnSize = paDetail.size() -1;
        if(lnSize < 0){
            obj.put("result", "error");
            obj.put("continue", false);
            return obj;
        }

        int lnCtr;
        String lsSQL;
        
        for (lnCtr = 0; lnCtr <= lnSize; lnCtr++){
            paDetail.get(lnCtr).setTransNo(fsTransNo);
            paDetail.get(lnCtr).setEntryNo(lnCtr+1);

            if(lnCtr>0){
                if(paDetail.get(lnCtr).getTownID().isEmpty()){
                    paDetail.remove(lnCtr);
                }
            }
            
            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Activity_Location, paDetail.get(lnCtr));
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
    
    public ArrayList<Model_Activity_Location> getDetailList(){return paDetail;}
    public void setDetailList(ArrayList<Model_Activity_Location> foObj){this.paDetail = foObj;}
    
    public void setDetail(int fnRow, int fnIndex, Object foValue){ paDetail.get(fnRow).setValue(fnIndex, foValue);}
    public void setDetail(int fnRow, String fsIndex, Object foValue){ paDetail.get(fnRow).setValue(fsIndex, foValue);}
    public Object getDetail(int fnRow, int fnIndex){return paDetail.get(fnRow).getValue(fnIndex);}
    public Object getDetail(int fnRow, String fsIndex){return paDetail.get(fnRow).getValue(fsIndex);}
    
    public Object removeDetail(int fnRow){
        JSONObject loJSON = new JSONObject();
        //if(paDetail.get(fnRow).getEntryBy().isEmpty()){
            paDetail.remove(fnRow);
        //} 
//        else {
//            loJSON.put("result", "error");
//            loJSON.put("message", "You cannot remove Detail that already saved, Deactivate it instead.");
//            return loJSON;
//        }
        return loJSON;
    }
    
    
    
    /**
     * Searches for a province based on the provided value.
     *
     * @param fsValue the value used to search for a province
     * @return true if the province is found and set as the master record, false
     * otherwise
     */
    public JSONObject searchProvince(String fsValue, int fnRow, boolean fbByCode) {
        JSONObject loJSON = new JSONObject();
        if (fbByCode){
            if (fsValue.equals((String) paDetail.get(fnRow).getProvID())) {
                loJSON.put("result", "success");
                loJSON.put("message", "Search province success.");
                return loJSON;
            }
        }else{
            String lsProvince = String.valueOf(paDetail.get(fnRow).getValue("sProvName"));
            
            if(!lsProvince.isEmpty()){
                if (fsValue.equals(lsProvince)){
                    loJSON.put("result", "success");
                    loJSON.put("message", "Search province success.");
                    return loJSON;
                }
            }
        }
        
       String lsSQL = " SELECT "
                    + " sProvName "
                    + ", sProvIDxx "
                    + " FROM Province  " 
                    + " WHERE cRecdStat = '1'";
        
        if (fbByCode) {
            lsSQL = MiscUtil.addCondition(lsSQL, "sProvIDxx = " + SQLUtil.toSQL(fsValue));
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL, "sProvName LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }
        System.out.println(lsSQL);
        loJSON = ShowDialogFX.Search(poGRider, 
                            lsSQL, 
                            fsValue,
                            "ID»Province", 
                            "sProvIDxx»sProvName", 
                            "sProvIDxx»sProvName", 
                            fbByCode ? 0 : 1);
            
        if (loJSON != null) {
            if("error".equals(loJSON.get("result"))){
                paDetail.get(fnRow).setProvID("");
                paDetail.get(fnRow).setProvName("");
                paDetail.get(fnRow).setTownID("");
                paDetail.get(fnRow).setTownName("");
                paDetail.get(fnRow).setZippCode("");
            } else {
                paDetail.get(fnRow).setProvID((String) loJSON.get("sProvIDxx"));
                paDetail.get(fnRow).setProvName((String) loJSON.get("sProvName"));
                paDetail.get(fnRow).setTownID("");
                paDetail.get(fnRow).setTownName("");
                paDetail.get(fnRow).setZippCode("");
            }
        }else {
            paDetail.get(fnRow).setProvID("");
            paDetail.get(fnRow).setProvName("");
            paDetail.get(fnRow).setTownID("");
            paDetail.get(fnRow).setTownName("");
            paDetail.get(fnRow).setZippCode("");
            loJSON  = new JSONObject();  
            loJSON.put("result", "error");
            loJSON.put("message", "No record selected.");
            return loJSON;
        }
        
        return loJSON;
    }
    /**
     * Searches for a town based on the provided value.
     *
     * @param fsValue the value used to search for a province
     * @return true if the province is found and set as the master record, false
     * otherwise
     */
    public JSONObject searchTown(String fsValue, int fnRow, boolean fbByCode) {
        JSONObject loJSON = new JSONObject();
        
        if(paDetail.get(fnRow).getProvID()== null){
            loJSON.put("result", "error");
            loJSON.put("message", "Province cannot be empty.");
            return loJSON;
        } else {
            if(paDetail.get(fnRow).getProvID().trim().isEmpty()){
                loJSON.put("result", "error");
                loJSON.put("message", "Province cannot be empty.");
                return loJSON;
            }
        }
        
        if (fbByCode){
            if (fsValue.equals((String) paDetail.get(fnRow).getTownID())) {
                loJSON = new JSONObject();
                loJSON.put("result", "success");
                loJSON.put("message", "Search town success.");
                return loJSON;
            }
        }else{
            
            String townProvince = String.valueOf(paDetail.get(fnRow).getValue("sTownName"));
            if(!townProvince.isEmpty()){
                if (fsValue.equals(townProvince)){
                    loJSON = new JSONObject();
                    loJSON.put("result", "success");
                    loJSON.put("message", "Search town success.");
                    return loJSON;
                }
            }
        }
        
       String lsSQL = "SELECT " +
                            "  a.sTownIDxx" +
                            ", a.sTownName" + 
                            ", a.sZippCode" +
                            ", b.sProvName" + 
                            ", b.sProvIDxx" +
                        " FROM TownCity a" +
                            ", Province b" +
                        " WHERE a.sProvIDxx = b.sProvIDxx AND a.cRecdStat = '1'";
        
        if (fbByCode) {
            lsSQL = MiscUtil.addCondition(lsSQL, "a.sProvIDxx = "  + SQLUtil.toSQL(paDetail.get(fnRow).getProvID()) + " AND a.sTownIDxx = " + SQLUtil.toSQL(fsValue));
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL, "a.sProvIDxx = "  + SQLUtil.toSQL(paDetail.get(fnRow).getProvID()) + " AND a.sTownName LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }
        System.out.println(lsSQL);
        loJSON = ShowDialogFX.Search(poGRider, 
                            lsSQL, 
                            fsValue,
                            "ID»Town»Postal Code»Province", 
                            "sTownIDxx»sTownName»sZippCode»sProvName", 
                            "a.sTownIDxx»a.sTownName»a.sZippCode»b.sProvName", 
                            fbByCode ? 0 : 1);
            
            if (loJSON != null) {
                if("error".equals(loJSON.get("result"))){
                    paDetail.get(fnRow).setTownID("");
                    paDetail.get(fnRow).setTownName("");
                    paDetail.get(fnRow).setZippCode("");
                    paDetail.get(fnRow).setBrgyID("");
                    paDetail.get(fnRow).setBrgyName("");
                } else {
                    paDetail.get(fnRow).setTownID((String) loJSON.get("sTownIDxx"));
                    paDetail.get(fnRow).setTownName((String) loJSON.get("sTownName"));
                    paDetail.get(fnRow).setZippCode((String) loJSON.get("sZippCode"));
                    paDetail.get(fnRow).setBrgyID("");
                    paDetail.get(fnRow).setBrgyName("");
                }
            }else {
                paDetail.get(fnRow).setTownID("");
                paDetail.get(fnRow).setTownName("");
                paDetail.get(fnRow).setZippCode("");
                    paDetail.get(fnRow).setBrgyID("");
                    paDetail.get(fnRow).setBrgyName("");
                loJSON  = new JSONObject();  
                loJSON.put("result", "error");
                loJSON.put("message", "No record selected.");
                return loJSON;
            }
            
        return loJSON;
    }
    
    public JSONObject searchBarangay(String fsValue, int fnRow, boolean fbByCode) {
        JSONObject loJSON = new JSONObject();
        
        if(paDetail.get(fnRow).getProvID()== null){
            loJSON.put("result", "error");
            loJSON.put("message", "Province cannot be empty.");
            return loJSON;
        } else {
            if(paDetail.get(fnRow).getProvID().trim().isEmpty()){
                loJSON.put("result", "error");
                loJSON.put("message", "Province cannot be empty.");
                return loJSON;
            }
        }
        
        if(paDetail.get(fnRow).getTownID()== null){
            loJSON.put("result", "error");
            loJSON.put("message", "Town cannot be empty.");
            return loJSON;
        } else {
            if(paDetail.get(fnRow).getTownID().trim().isEmpty()){
                loJSON.put("result", "error");
                loJSON.put("message", "Town cannot be empty.");
                return loJSON;
            }
        }
        
        if (fbByCode){
            if (fsValue.equals((String) paDetail.get(fnRow).getBrgyID())) {
                loJSON = new JSONObject();
                loJSON.put("result", "success");
                loJSON.put("message", "Search barangay success.");
                return loJSON;
            }
        }else{
            String lsbarangay = String.valueOf(paDetail.get(fnRow).getValue("sBrgyName"));
            if(!lsbarangay.isEmpty()){
                if (fsValue.equals(lsbarangay)){
                    loJSON = new JSONObject();
                    loJSON.put("result", "success");
                    loJSON.put("message", "Search barangay success.");
                    return loJSON;
                }
            }
        }
        
       String lsSQL = "SELECT " +
                        "  a.sBrgyIDxx" +
                        ", a.sBrgyName" +
                        ", b.sTownName" + 
                        ", b.sZippCode" +
                        ", c.sProvName" + 
                        ", c.sProvIDxx" +
                        ", b.sTownIDxx" +
                    " FROM Barangay a" + 
                        ", TownCity b" +
                        ", Province c" +
                    " WHERE a.sTownIDxx = b.sTownIDxx" + 
                        " AND b.sProvIDxx = c.sProvIDxx" + 
                        " AND a.cRecdStat = '1'" + 
                        " AND b.cRecdStat = '1'" + 
                        " AND c.cRecdStat = '1'" + 
                        " AND a.sTownIDxx = " + SQLUtil.toSQL(paDetail.get(fnRow).getTownID());
        
        if (fbByCode){
            lsSQL = MiscUtil.addCondition(lsSQL, "a.sBrgyIDxx = " + SQLUtil.toSQL(fsValue));
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL, "a.sBrgyName LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }
        System.out.println(lsSQL);
        loJSON = ShowDialogFX.Search(poGRider, 
                            lsSQL, 
                            fsValue,
                            "ID»Barangay»Town»Province", 
                            "sBrgyIDxx»sBrgyName»sTownName»sProvName",
                            "sBrgyIDxx»sBrgyName»sTownName»sProvName",
                            fbByCode ? 0 : 1);
            
        if (loJSON != null) {
            if("error".equals(loJSON.get("result"))){
                paDetail.get(fnRow).setBrgyID("");
                paDetail.get(fnRow).setBrgyName("");
            } else {
                paDetail.get(fnRow).setBrgyID((String) loJSON.get("sBrgyIDxx"));
                paDetail.get(fnRow).setBrgyName((String) loJSON.get("sBrgyName"));
            }
        }else {
            paDetail.get(fnRow).setBrgyID("");
            paDetail.get(fnRow).setBrgyName("");
            loJSON  = new JSONObject();  
            loJSON.put("result", "error");
            loJSON.put("message", "No record selected.");
            return loJSON;
        }
            
        return loJSON;
    }
    
}
