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
import org.guanzon.auto.model.sales.Model_Activity_Town;
import org.guanzon.auto.validator.sales.ValidatorFactory;
import org.guanzon.auto.validator.sales.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Activity_Town {
    final String XML = "Model_Activity_Town.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    public JSONObject poJSON;
    
    ArrayList<Model_Activity_Town> paDetail;
    
    public Activity_Town(GRider foAppDrver){
        poGRider = foAppDrver;
    }

    public int getEditMode() {
        return pnEditMode;
    }
    
    public Model_Activity_Town getDetail(int fnIndex){
        if (fnIndex > paDetail.size() - 1 || fnIndex < 0) return null;
        
        return paDetail.get(fnIndex);
    }
    
    public JSONObject addDetail(String fsTransNo){
        
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        
        poJSON = new JSONObject();
        if (paDetail.size()<=0){
            paDetail.add(new Model_Activity_Town(poGRider));
            paDetail.get(0).newRecord();
            paDetail.get(0).setValue("sTransNox", fsTransNo);
            poJSON.put("result", "success");
            poJSON.put("message", "Activity Town add record.");
        } else {
            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Activity_Town, paDetail.get(paDetail.size()-1));
            validator.setGRider(poGRider);
            if(!validator.isEntryOkay()){
                poJSON.put("result", "error");
                poJSON.put("message", validator.getMessage());
                return poJSON;
            }
            paDetail.add(new Model_Activity_Town(poGRider));
            paDetail.get(paDetail.size()-1).newRecord();

            paDetail.get(paDetail.size()-1).setTransNo(fsTransNo);
            
            poJSON.put("result", "success");
            poJSON.put("message", "Activity Town add record.");
        }
        return poJSON;
    }
    
    public JSONObject openDetail(String fsValue){
        paDetail = new ArrayList<>();
        poJSON = new JSONObject();
        String lsSQL =    "  SELECT "              
                        + "    a.sTransNox "      
                        + "  , a.sTownIDxx "      
                        + "  , a.sAddressx "       
                        + " FROM activity_town a " ;
        lsSQL = MiscUtil.addCondition(lsSQL, "sTransNox = " + SQLUtil.toSQL(fsValue));
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        System.out.println(lsSQL);
       try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                while(loRS.next()){
                        paDetail.add(new Model_Activity_Town(poGRider));
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
//            paDetail.get(lnCtr).setEntryNo(lnCtr+1);

            if(lnCtr>0){
                if(paDetail.get(lnCtr).getTownID().isEmpty()){
                    paDetail.remove(lnCtr);
                }
            }
            
            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Activity_Town, paDetail.get(lnCtr));
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
    
    public ArrayList<Model_Activity_Town> getDetailList(){return paDetail;}
    public void setDetailList(ArrayList<Model_Activity_Town> foObj){this.paDetail = foObj;}
    
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
    
    
}
