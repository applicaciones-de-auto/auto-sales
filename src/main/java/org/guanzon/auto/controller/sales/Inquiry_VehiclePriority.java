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
import org.guanzon.appdriver.iface.GRecord;
import org.guanzon.auto.model.sales.Model_Inquiry_Master;
import org.guanzon.auto.model.sales.Model_Inquiry_VehiclePriority;
import org.json.simple.JSONObject;

/**
 *
 * @author MIS-PC
 */
public class Inquiry_VehiclePriority {
    final String XML = "Model_Inquiry_Promo.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    
    public JSONObject poJSON;
    
    ArrayList<Model_Inquiry_VehiclePriority> paVehiclePriority;
    
    public Inquiry_VehiclePriority(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poGRider = foAppDrver;
        pbWtParent = fbWtParent;
        psBranchCd = fsBranchCd.isEmpty() ? foAppDrver.getBranchCode() : fsBranchCd;
    }
    
    public JSONObject checkData(JSONObject joValue){
        if(pnEditMode == EditMode.READY || pnEditMode == EditMode.UPDATE){
            if(joValue.containsKey("continue")){
                if(true == (boolean)joValue.get("continue")){
                    joValue.put("result", "success");
                    joValue.put("message", "Record saved successfully.");
                }
            }
        }
        return joValue;
    }

    public Model_Inquiry_VehiclePriority getVehiclePriority(int fnIndex){
        if (fnIndex > paVehiclePriority.size() - 1 || fnIndex < 0) return null;
        return paVehiclePriority.get(fnIndex);
    }
    
    public ArrayList<Model_Inquiry_VehiclePriority> getVehiclePriorityList(){return paVehiclePriority;}
    public void setVehiclePriorityList(ArrayList<Model_Inquiry_VehiclePriority> foObj){this.paVehiclePriority = foObj;}
    
    public void setVehiclePriority(int fnRow, int fnIndex, Object foValue){ paVehiclePriority.get(fnRow).setValue(fnIndex, foValue);}
    public void setVehiclePriority(int fnRow, String fsIndex, Object foValue){ paVehiclePriority.get(fnRow).setValue(fsIndex, foValue);}
    public Object getVehiclePriority(int fnRow, int fnIndex){return paVehiclePriority.get(fnRow).getValue(fnIndex);}
    public Object getVehiclePriority(int fnRow, String fsIndex){return paVehiclePriority.get(fnRow).getValue(fsIndex);}
    
    public JSONObject addVehiclePriority(String fsTransNox) {
        poJSON = new JSONObject();
        paVehiclePriority = new ArrayList<>();
        if (paVehiclePriority.size()<=0){
            paVehiclePriority.add(new Model_Inquiry_VehiclePriority(poGRider));
            paVehiclePriority.get(0).newRecord();
            paVehiclePriority.get(0).setValue("sTransNox", fsTransNox);
            poJSON.put("result", "success");
            poJSON.put("message", "Inquiry Vehicle Priority add record.");
        } else {
            
//            Validator_Client_Mobile  validator = new Validator_Client_Mobile(paMobile.get(paMobile.size()-1));
            //ValidatorInterface validator = ValidatorFactory.make(types,  ValidatorFactory.TYPE.Client_Mobile, paMobile.get(paMobile.size()-1));
//            if(!validator.isEntryOkay()){
//                poJSON.put("result", "error");
//                poJSON.put("message", validator.getMessage());
//                return poJSON;
//            }
            paVehiclePriority.add(new Model_Inquiry_VehiclePriority(poGRider));
            paVehiclePriority.get(paVehiclePriority.size()-1).newRecord();
            paVehiclePriority.get(paVehiclePriority.size()-1).setTransNox(fsTransNox);
        }
        
        return poJSON;
    }
    
    public JSONObject openPromo(String fsValue){
        String lsSQL = " SELECT " +
                            " IFNULL(a.sTransNox,'') sTransNox " +  
                            " , IFNULL(a.sPromoIDx,'') sPromoIDx " + 
                            " , a.sEntryByx " +
                            " , a.dEntryDte " +
                            " , IFNULL(b.sActTitle,'') sActTitle " +
                            " , b.dDateFrom " +  
                            " , b.dDateThru " +                  
                        "  FROM customer_inquiry_promo a " +  
                        "  LEFT JOIN activity_master b ON b.sActvtyID = a.sPromoIDx";     
        lsSQL = MiscUtil.addCondition(lsSQL, "a.sTransNox = " + SQLUtil.toSQL(fsValue) + " GROUP BY sTransNox");
        System.out.println(lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);

        try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                paVehiclePriority = new ArrayList<>();
                while(loRS.next()){
                        paVehiclePriority.add(new Model_Inquiry_VehiclePriority(poGRider));
                        paVehiclePriority.get(paVehiclePriority.size() - 1).openRecord(loRS.getString("sTransNox"));
                        
                        pnEditMode = EditMode.UPDATE;
                        lnctr++;
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record loaded successfully.");
                    } 
                
                System.out.println("lnctr = " + lnctr);
                
            }else{
                paVehiclePriority = new ArrayList<>();
                addVehiclePriority(fsValue);
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
    
    public JSONObject saveVehiclePriority(String fsTransNox){
        
        JSONObject obj = new JSONObject();
        if (paVehiclePriority.size()<= 0){
            obj.put("result", "error");
            obj.put("message", "No client address detected. Please encode client address.");
            return obj;
        }
        
        int lnCtr;
        String lsSQL;
        
        for (lnCtr = 0; lnCtr <= paVehiclePriority.size() -1; lnCtr++){
            paVehiclePriority.get(lnCtr).setTransNox(fsTransNox);
            if(lnCtr>0){
                if(paVehiclePriority.get(lnCtr).getVehicleID().isEmpty()){
                    paVehiclePriority.remove(lnCtr);
                }
            }
            //ValidatorInterface validator = ValidatorFactory.make(types,  ValidatorFactory.TYPE.Client_Address, paPromo.get(lnCtr));
           
//            if (!validator.isEntryOkay()){
//                obj.put("result", "error");
//                obj.put("message", validator.getMessage());
//                return obj;
//            
//            }
            obj = paVehiclePriority.get(lnCtr).saveRecord();

        }    
        
        return obj;
    }
    
}
