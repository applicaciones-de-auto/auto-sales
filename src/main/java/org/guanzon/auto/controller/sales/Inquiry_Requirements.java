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
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.auto.model.sales.Model_Inquiry_Requirements;
import org.guanzon.auto.validator.sales.ValidatorFactory;
import org.guanzon.auto.validator.sales.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Inquiry_Requirements {
    
    final String XML = "Model_Inquiry_Requirements.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    public JSONObject poJSON;
    
    ArrayList<Model_Inquiry_Requirements> paDetail;
    ArrayList<Model_Inquiry_Requirements> paRemDetail;
    
    public Inquiry_Requirements(GRider foAppDrver){
        poGRider = foAppDrver;
    }
    
    public int getEditMode() {
        return pnEditMode;
    }

    public Model_Inquiry_Requirements getPromo(int fnIndex){
        if (fnIndex > paDetail.size() - 1 || fnIndex < 0) return null;
        
        return paDetail.get(fnIndex);
    }
    
    public JSONObject addDetail(String fsTransNo){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        
        poJSON = new JSONObject();
        if (paDetail.size()<=0){
            paDetail.add(new Model_Inquiry_Requirements(poGRider));
            paDetail.get(0).newRecord();
            
            paDetail.get(0).setValue("sTransNox", fsTransNo);
            poJSON.put("result", "success");
            poJSON.put("message", "Inquiry Requirements add record.");
        } else {
            paDetail.add(new Model_Inquiry_Requirements(poGRider));
            paDetail.get(paDetail.size()-1).newRecord();

            paDetail.get(paDetail.size()-1).setTransNo(fsTransNo);
            poJSON.put("result", "success");
            poJSON.put("message", "Inquiry Requirements add record.");
        }
        return poJSON;
    }
    
    public JSONObject openDetail(String fsValue){
        paDetail = new ArrayList<>();
        paRemDetail = new ArrayList<>();
        poJSON = new JSONObject();
        String lsSQL =    "  SELECT "                                                  
                        + "   sTransNox "  
                        + " , nEntryNox "  
                        + " , sRqrmtCde "                                            
                        + "  FROM customer_inquiry_requirements "  ;
        lsSQL = MiscUtil.addCondition(lsSQL, " sTransNox = " + SQLUtil.toSQL(fsValue));
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        System.out.println(lsSQL);
       try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                while(loRS.next()){
                        paDetail.add(new Model_Inquiry_Requirements(poGRider));
                        paDetail.get(paDetail.size() - 1).openRecord(loRS.getString("sTransNox"), loRS.getString("sRqrmtCde"));
                        
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
        

        int lnEntryNo = 1;
        for (lnCtr = 0; lnCtr <= lnSize; lnCtr++){
            if(paDetail.get(lnCtr).getReceived().trim().isEmpty()){
//                paDetail.remove(lnCtr);
                continue; //skip instead of removing the actual detail
            }
            
            paDetail.get(lnCtr).setTransNo(fsTransNo);
            paDetail.get(lnCtr).setEntryNo(lnEntryNo);

            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Inquiry_Requirements, paDetail.get(lnCtr));
            validator.setGRider(poGRider);
            if (!validator.isEntryOkay()){
                obj.put("result", "error");
                obj.put("message", validator.getMessage());
                return obj;
            }
            obj = paDetail.get(lnCtr).saveRecord();
            lnEntryNo++;
        }    
        
        return obj;
    }
    
    public ArrayList<Model_Inquiry_Requirements> getDetailList(){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        return paDetail;
    }
    public void setDetailList(ArrayList<Model_Inquiry_Requirements> foObj){this.paDetail = foObj;}
    
    public void setDetail(int fnRow, int fnIndex, Object foValue){ paDetail.get(fnRow).setValue(fnIndex, foValue);}
    public void setDetail(int fnRow, String fsIndex, Object foValue){ paDetail.get(fnRow).setValue(fsIndex, foValue);}
    public Object getDetail(int fnRow, int fnIndex){return paDetail.get(fnRow).getValue(fnIndex);}
    public Object getDetail(int fnRow, String fsIndex){return paDetail.get(fnRow).getValue(fsIndex);}
    
    
    public Object removeDetail(int fnRow){
        JSONObject loJSON = new JSONObject();
        
        if(paDetail.get(fnRow).getEntryNo() != null){
            if(paDetail.get(fnRow).getEntryNo() != 0){
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
            paRemDetail.add(new Model_Inquiry_Requirements(poGRider));
            paRemDetail.get(0).openRecord(paDetail.get(fnRow).getTransNo(),paDetail.get(fnRow).getRqrmtCde());
            poJSON.put("result", "success");
            poJSON.put("message", "added to remove record.");
        } else {
            paRemDetail.add(new Model_Inquiry_Requirements(poGRider));
            paRemDetail.get(paRemDetail.size()-1).openRecord(paDetail.get(fnRow).getTransNo(),paDetail.get(fnRow).getRqrmtCde());
            poJSON.put("result", "success");
            poJSON.put("message", "added to remove record.");
        }
        return poJSON;
    }
    
    public JSONObject loadRequirements(String fsTransNo, String fsPaymode, String fsCustGrp) {
        poJSON = new JSONObject();
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        boolean lbExist = false;
        String lsSQL =    " SELECT "                                                     
                        + "    a.sRqrmtIDx "                                             
                        + "  , a.cPayModex "                                             
                        + "  , a.cCustGrpx "                                             
                        + "  , a.sRqrmtCde "                                             
                        + "  , b.sDescript "                                             
                        + " FROM requirement_source_pergroup a "                         
                        + " LEFT JOIN requirement_source b ON b.sRqrmtCde = a.sRqrmtCde " ;  
        lsSQL = MiscUtil.addCondition(lsSQL, " a.cPayModex = " + SQLUtil.toSQL(fsPaymode)
                                               + " AND a.cCustGrpx = " + SQLUtil.toSQL(fsCustGrp));  

        System.out.println("LOAD REQUIREMENTS: " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        if (MiscUtil.RecordCount(loRS) > 0){
            try {
                while(loRS.next()){
                    for (int lnCtr = 0; lnCtr <= paDetail.size()-1;lnCtr++){
                        if(paDetail.get(lnCtr).getRqrmtCde().equals((String) poJSON.get("sRqrmtCde"))){
                           lbExist = true;
                           break;
                        }
                    }
                    
                    if(!lbExist){
                        addDetail(fsTransNo);
                        setDetail(paDetail.size()-1,"sRqrmtCde", (String) loRS.getString("sRqrmtCde"));
                        setDetail(paDetail.size()-1,"sDescript", (String) loRS.getString("sDescript"));
                    }
                }
                
                poJSON.put("result", "success");
                poJSON.put("message", "Requirements added successfully.");
            } catch (SQLException ex) {
                Logger.getLogger(Inquiry_Requirements.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
        
        return poJSON;
    }
    
    public JSONObject searchEmployee(int fnRow) {
        poJSON = new JSONObject();
        
        String lsSQL =   " SELECT "
                       + " a.sClientID "
                       + " , a.cRecdStat "
                       + " , b.sCompnyNm "
                       + " FROM sales_executive a "
                       + " LEFT JOIN GGC_ISysDBF.Client_Master b ON b.sClientID = a.sClientID " ;  
                
        lsSQL = MiscUtil.addCondition(lsSQL, "a.cRecdStat = '1' ");  

        System.out.println("SEARCH EMPLOYEE: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                "",
                    "Employee Name",
                    "sCompnyNm",
                    "b.sCompnyNm",
                0);

        if (poJSON != null) {
            
            if(!"error".equals((String) poJSON.get("result"))){
                setDetail(fnRow,"sReceived", (String) poJSON.get("sClientID"));
                setDetail(fnRow,"sCompnyNm", (String) poJSON.get("sCompnyNm"));
                setDetail(fnRow,"dReceived", poGRider.getServerDate());
                setDetail(fnRow,"cSubmittd", "1");
                
                poJSON.put("result", "success");
                poJSON.put("message", "Requirements added successfully.");
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
