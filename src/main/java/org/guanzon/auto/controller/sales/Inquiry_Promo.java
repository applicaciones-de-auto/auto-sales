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
import org.guanzon.auto.model.sales.Model_Inquiry_Promo;
import org.guanzon.auto.model.sales.Model_Inquiry_VehiclePriority;
import org.json.simple.JSONObject;

/**
 *
 * @author MIS-PC
 */
public class Inquiry_Promo{
    final String XML = "Model_Inquiry_Promo.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    
    public JSONObject poJSON;
    
    ArrayList<Model_Inquiry_Promo> paPromo;
    
    public Inquiry_Promo(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
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

    public Model_Inquiry_Promo getPromo(int fnIndex){
        if (fnIndex > paPromo.size() - 1 || fnIndex < 0) return null;
        return paPromo.get(fnIndex);
    }
    
    public ArrayList<Model_Inquiry_Promo> getPromoList(){return paPromo;}
    public void setPromoList(ArrayList<Model_Inquiry_Promo> foObj){this.paPromo = foObj;}
    
    public void setPromo(int fnRow, int fnIndex, Object foValue){ paPromo.get(fnRow).setValue(fnIndex, foValue);}
    public void setPromo(int fnRow, String fsIndex, Object foValue){ paPromo.get(fnRow).setValue(fsIndex, foValue);}
    public Object getPromo(int fnRow, int fnIndex){return paPromo.get(fnRow).getValue(fnIndex);}
    public Object getPromo(int fnRow, String fsIndex){return paPromo.get(fnRow).getValue(fsIndex);}
    
    
    public JSONObject addPromo(String fsTransNox) {
        poJSON = new JSONObject();
        paPromo = new ArrayList<>();
        if (paPromo.size()<=0){
            paPromo.add(new Model_Inquiry_Promo(poGRider));
            paPromo.get(0).newRecord();
            paPromo.get(0).setValue("sTransNox", fsTransNox);
            poJSON.put("result", "success");
            poJSON.put("message", "Inquiry Promo add record.");
        } else {
            
//            Validator_Client_Mobile  validator = new Validator_Client_Mobile(paMobile.get(paMobile.size()-1));
            //ValidatorInterface validator = ValidatorFactory.make(types,  ValidatorFactory.TYPE.Client_Mobile, paMobile.get(paMobile.size()-1));
//            if(!validator.isEntryOkay()){
//                poJSON.put("result", "error");
//                poJSON.put("message", validator.getMessage());
//                return poJSON;
//            }
            paPromo.add(new Model_Inquiry_Promo(poGRider));
            paPromo.get(paPromo.size()-1).newRecord();
            paPromo.get(paPromo.size()-1).setTransNox(fsTransNox);
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
                paPromo = new ArrayList<>();
                while(loRS.next()){
                        paPromo.add(new Model_Inquiry_Promo(poGRider));
                        paPromo.get(paPromo.size() - 1).openRecord(loRS.getString("sTransNox"));
                        
                        pnEditMode = EditMode.UPDATE;
                        lnctr++;
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record loaded successfully.");
                    } 
                
                System.out.println("lnctr = " + lnctr);
                
            }else{
                paPromo = new ArrayList<>();
                addPromo(fsValue);
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
    
    public JSONObject savePromo(String fsTransNox){
        
        JSONObject obj = new JSONObject();
        if (paPromo.size()<= 0){
            obj.put("result", "error");
            obj.put("message", "No client address detected. Please encode client address.");
            return obj;
        }
        
        int lnCtr;
        String lsSQL;
        
        for (lnCtr = 0; lnCtr <= paPromo.size() -1; lnCtr++){
            paPromo.get(lnCtr).setTransNox(fsTransNox);
            if(lnCtr>0){
                if(paPromo.get(lnCtr).getPromoID().isEmpty()){
                    paPromo.remove(lnCtr);
                }
            }
            //ValidatorInterface validator = ValidatorFactory.make(types,  ValidatorFactory.TYPE.Client_Address, paPromo.get(lnCtr));
           
//            if (!validator.isEntryOkay()){
//                obj.put("result", "error");
//                obj.put("message", validator.getMessage());
//                return obj;
//            
//            }
            obj = paPromo.get(lnCtr).saveRecord();

        }    
        
        return obj;
    }
    
    
}
