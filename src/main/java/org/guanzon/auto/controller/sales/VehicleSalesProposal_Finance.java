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
import org.guanzon.auto.model.sales.Model_VehicleSalesProposal_Finance;
import org.guanzon.auto.validator.sales.ValidatorFactory;
import org.guanzon.auto.validator.sales.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class VehicleSalesProposal_Finance {
    
    final String FINANCE_XML = "Model_VehicleSalesProposal_Finance.xml";
    GRider poGRider;
    String psTargetBranchCd = "";
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    public JSONObject poJSON;
    
    ArrayList<Model_VehicleSalesProposal_Finance> paDetail;
    ArrayList<Model_VehicleSalesProposal_Finance> paRemDetail;

    public VehicleSalesProposal_Finance(GRider foAppDrver){
        poGRider = foAppDrver;
    }
    
    public int getEditMode() {
        return pnEditMode;
    }

    public Model_VehicleSalesProposal_Finance getVSPFinance(int fnIndex){
        if (fnIndex > paDetail.size() - 1 || fnIndex < 0) return null;
        
        return paDetail.get(fnIndex);
    }
    
    public JSONObject addDetail(String fsTransNo){
//        if(paDetail == null){
           paDetail = new ArrayList<>();
//        }
        
        poJSON = new JSONObject();
        paDetail.add(new Model_VehicleSalesProposal_Finance(poGRider));
        paDetail.get(0).newRecord();

//        paDetail.get(0).setValue("sTransNox", fsTransNo);
        poJSON.put("result", "success");
        poJSON.put("message", "VSP Finance add record.");
        return poJSON;
    }
    
    public JSONObject openDetail(String fsValue){
        paDetail = new ArrayList<>();
        paRemDetail = new ArrayList<>();
        poJSON = new JSONObject();
        String lsSQL =    "  SELECT "                                                  
                        + "    sTransNox "
                        + "  , cFinPromo "
                        + "  , sBankIDxx "
                        + "  , sBankname "                                            
                        + "  FROM vsp_finance " ;
        lsSQL = MiscUtil.addCondition(lsSQL, " sTransNox = " + SQLUtil.toSQL(fsValue));
        System.out.println(lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
       try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                paDetail = new ArrayList<>();
                while(loRS.next()){
                        paDetail.add(new Model_VehicleSalesProposal_Finance(poGRider));
                        paDetail.get(paDetail.size() - 1).openRecord(loRS.getString("sTransNox"));
                        
                        pnEditMode = EditMode.UPDATE;
                        lnctr++;
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record loaded successfully.");
                    } 
                
                System.out.println("lnctr = " + lnctr);
            }else{
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
        
        if(psTargetBranchCd == null){
            obj.put("result", "error");
            obj.put("continue", false);
            obj.put("message", "Target Branch code for finance cannot be empty.");
            return obj;
        } else {
            if(psTargetBranchCd.isEmpty()){
                obj.put("result", "error");
                obj.put("continue", false);
                obj.put("message", "Target Branch code for finance cannot be empty.");
                return obj;
            }
        }
        
        for (lnCtr = 0; lnCtr <= lnSize; lnCtr++){
            if(paDetail.get(lnCtr).getTransNo().isEmpty()){
                continue; //skip, instead of removing the actual detail
            }
            
            paDetail.get(lnCtr).setTransNo(fsTransNo);
            paDetail.get(lnCtr).setTargetBranchCd(psTargetBranchCd);
            
            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.VehicleSalesProposal_Finance, paDetail.get(lnCtr));
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
    
    public ArrayList<Model_VehicleSalesProposal_Finance> getDetailList(){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        return paDetail;
    }
    
    public Model_VehicleSalesProposal_Finance getVSPFinanceModel() {
        return paDetail.get(0);
    }
    
    public Object removeDetail(int fnRow){
        JSONObject loJSON = new JSONObject();
        
        if(paDetail.get(fnRow).getTransNo()!= null){
            if(paDetail.get(fnRow).getTransNo().trim().isEmpty()){
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
            paRemDetail.add(new Model_VehicleSalesProposal_Finance(poGRider));
            paRemDetail.get(0).openRecord(paDetail.get(fnRow).getTransNo());
            poJSON.put("result", "success");
            poJSON.put("message", "added to remove record.");
        } else {
            paRemDetail.add(new Model_VehicleSalesProposal_Finance(poGRider));
            paRemDetail.get(paRemDetail.size()-1).openRecord(paDetail.get(fnRow).getTransNo());
            poJSON.put("result", "success");
            poJSON.put("message", "added to remove record.");
        }
        return poJSON;
    }
    
}
