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
    
    ArrayList<Model_Inquiry_Requirements> paRequirements;
    
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
    
    public JSONObject addDetail(){ //String fsTransNo
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        
        poJSON = new JSONObject();
        if (paDetail.size()<=0){
            paDetail.add(new Model_Inquiry_Requirements(poGRider));
            paDetail.get(0).newRecord();
            
            //paDetail.get(0).setValue("sTransNox", fsTransNo);
            poJSON.put("result", "success");
            poJSON.put("message", "Inquiry Requirements add record.");
        } else {
            paDetail.add(new Model_Inquiry_Requirements(poGRider));
            paDetail.get(paDetail.size()-1).newRecord();

            //paDetail.get(paDetail.size()-1).setTransNo(fsTransNo);
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
    
//    public JSONObject loadRequirements(String fsTransNo, String fsPaymode, String fsCustGrp) {
//        poJSON = new JSONObject();
//        if(paDetail == null){
//           paDetail = new ArrayList<>();
//        }
//        boolean lbExist = false;
//        String lsSQL =    " SELECT "                                                              
//                        + "    a.sRqrmtCde "                                                      
//                        + "  , a.sDescript "                                                      
//                        + "  , b.sRqrmtIDx "                                                      
//                        + "  , b.cPayModex "                                                      
//                        + "  , b.cCustGrpx "                                                      
//                        + " FROM requirement_source a "                                           
//                        + " LEFT JOIN requirement_source_pergroup b ON b.sRqrmtCde = a.sRqrmtCde " ;  
//        lsSQL = MiscUtil.addCondition(lsSQL, " b.cPayModex = " + SQLUtil.toSQL(fsPaymode)
//                                               + " AND b.cCustGrpx = " + SQLUtil.toSQL(fsCustGrp));  
//
//        System.out.println("LOAD REQUIREMENTS: " + lsSQL);
//        ResultSet loRS = poGRider.executeQuery(lsSQL);
//        if (MiscUtil.RecordCount(loRS) > 0){
//            try {
//                while(loRS.next()){
//                    for (int lnCtr = 0; lnCtr <= paDetail.size()-1;lnCtr++){
//                        if(paDetail.get(lnCtr).getRqrmtCde().equals( loRS.getString("sRqrmtCde"))){
//                           lbExist = true;
//                           break;
//                        }
//                    }
//                    
//                    if(!lbExist){
//                        addDetail(fsTransNo);
//                        setDetail(paDetail.size()-1,"sRqrmtCde", (String) loRS.getString("sRqrmtCde"));
//                        setDetail(paDetail.size()-1,"sDescript", (String) loRS.getString("sDescript"));
//                    }
//                }
//                
//                poJSON.put("result", "success");
//                poJSON.put("message", "Requirements added successfully.");
//            } catch (SQLException ex) {
//                Logger.getLogger(Inquiry_Requirements.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } else {
//            poJSON = new JSONObject();
//            poJSON.put("result", "error");
//            poJSON.put("message", "No record loaded.");
//            return poJSON;
//        }
//        
//        return poJSON;
//    }
    
    public JSONObject addRequirements(){
        if(paRequirements == null){
           paRequirements = new ArrayList<>();
        }
        
        poJSON = new JSONObject();
        if (paRequirements.size()<=0){
            paRequirements.add(new Model_Inquiry_Requirements(poGRider));
            paRequirements.get(0).newRecord();
            poJSON.put("result", "success");
            poJSON.put("message", "Inquiry Requirements add record.");
        } else {
            paRequirements.add(new Model_Inquiry_Requirements(poGRider));
            paRequirements.get(paRequirements.size()-1).newRecord();
            poJSON.put("result", "success");
            poJSON.put("message", "Inquiry Requirements add record.");
        }
        return poJSON;
    }
    
    public JSONObject loadRequirements(String fsTransNo, String fsPaymode, String fsCustGrp) {
        poJSON = new JSONObject();
        paRequirements = new ArrayList<>();
        boolean lbExist = false;
        int lnRow = 0;
        String lsSQL =    " SELECT "                                                              
                        + "    a.sRqrmtCde "                                                      
                        + "  , a.sDescript "                                                      
                        + "  , b.sRqrmtIDx "                                                      
                        + "  , b.cPayModex "                                                      
                        + "  , b.cCustGrpx "                                                      
                        + " FROM requirement_source a "                                           
                        + " LEFT JOIN requirement_source_pergroup b ON b.sRqrmtCde = a.sRqrmtCde " ;  
        lsSQL = MiscUtil.addCondition(lsSQL, " b.cPayModex = " + SQLUtil.toSQL(fsPaymode)
                                               + " AND b.cCustGrpx = " + SQLUtil.toSQL(fsCustGrp));  

        System.out.println("LOAD REQUIREMENTS: " + lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        if (MiscUtil.RecordCount(loRS) > 0){
            try {
                while(loRS.next()){
                    
                    addRequirements();
                    setRequirements(paRequirements.size()-1,"sRqrmtCde", (String) loRS.getString("sRqrmtCde"));
                    setRequirements(paRequirements.size()-1,"sDescript", (String) loRS.getString("sDescript"));
                    
                    for (int lnCtr = 0; lnCtr <= paDetail.size()-1;lnCtr++){
                        if(paDetail.get(lnCtr).getRqrmtCde().equals( loRS.getString("sRqrmtCde"))){
                            setRequirements(paRequirements.size()-1,"sReceived", paDetail.get(lnCtr).getReceived());
                            setRequirements(paRequirements.size()-1,"sCompnyNm", paDetail.get(lnCtr).getCompnyNm());
                            setRequirements(paRequirements.size()-1,"dReceived", paDetail.get(lnCtr).getReceivedDte());
                            setRequirements(paRequirements.size()-1,"cSubmittd", paDetail.get(lnCtr).getSubmittd());
                            lbExist = true;
                            lnRow = lnCtr;
                            break;
                        }
                    }
                    
                    if(!lbExist){
                        setRequirements(paRequirements.size()-1,"sReceived", "");
                        setRequirements(paRequirements.size()-1,"sCompnyNm", "");
                        setRequirements(paRequirements.size()-1,"dReceived", "");
                        setRequirements(paRequirements.size()-1,"cSubmittd", "");
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
    
    public ArrayList<Model_Inquiry_Requirements> getRequirementsList(){
        if(paRequirements == null){
           paRequirements = new ArrayList<>();
        }
        return paRequirements;
    }
    public void setRequirementsList(ArrayList<Model_Inquiry_Requirements> foObj){this.paRequirements = foObj;}
    
    public void setRequirements(int fnRow, int fnIndex, Object foValue){ paRequirements.get(fnRow).setValue(fnIndex, foValue);}
    public void setRequirements(int fnRow, String fsIndex, Object foValue){ paRequirements.get(fnRow).setValue(fsIndex, foValue);}
    public Object getRequirements(int fnRow, int fnIndex){return paRequirements.get(fnRow).getValue(fnIndex);}
    public Object getRequirements(int fnRow, String fsIndex){return paRequirements.get(fnRow).getValue(fsIndex);}
    
    public JSONObject searchEmployee(String fsRqrmtCde, String fsDescript) { //, String fsTransNo
        poJSON = new JSONObject();
        boolean lbExist = false;
        int lnRow = 0;
        
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
                
                for (int lnCtr = 0; lnCtr <= paDetail.size()-1;lnCtr++){
                    if(paDetail.get(lnCtr).getRqrmtCde().equals(fsRqrmtCde )){
                       lbExist = true;
                       lnRow = lnCtr;
                       break;
                    }
                }
                
                if(lbExist){
                    setDetail(lnRow,"sReceived", (String) poJSON.get("sClientID"));
                    setDetail(lnRow,"sCompnyNm", (String) poJSON.get("sCompnyNm"));
                    setDetail(lnRow,"dReceived", poGRider.getServerDate());
                    setDetail(lnRow,"cSubmittd", "1");
                } else {
                    addDetail();
                     if (paDetail.size()<=0){
                         
//                        paDetail.get(0).setRqrmtCde(fsRqrmtCde);
//                        paDetail.get(0).setDescript(fsDescript);
//                        paDetail.get(0).setReceived((String) poJSON.get("sClientID"));
//                        paDetail.get(0).setCompnyNm((String) poJSON.get("sCompnyNm"));
//                        paDetail.get(0).setReceivedDte(poGRider.getServerDate());
//                        paDetail.get(0).setSubmittd("1");
                        paDetail.get(0).setValue("sRqrmtCde", fsRqrmtCde);
                        paDetail.get(0).setValue("sDescript", fsDescript);
                        paDetail.get(0).setValue("sReceived",  (String) poJSON.get("sClientID"));
                        paDetail.get(0).setValue("sCompnyNm",  (String) poJSON.get("sCompnyNm"));
                        paDetail.get(0).setValue("dReceived", poGRider.getServerDate());
                        paDetail.get(0).setValue("cSubmittd", "1");
                     }else {
                        setDetail(paDetail.size()-1,"sRqrmtCde", fsRqrmtCde);
                        setDetail(paDetail.size()-1,"sDescript", fsDescript);
                        setDetail(paDetail.size()-1,"sReceived", (String) poJSON.get("sClientID"));
                        setDetail(paDetail.size()-1,"sCompnyNm", (String) poJSON.get("sCompnyNm"));
                        setDetail(paDetail.size()-1,"dReceived", poGRider.getServerDate());
                        setDetail(paDetail.size()-1,"cSubmittd", "1");
                     }
                    
                    
                    
                }
                
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
    
    public void removeEmployee(String fsRqrmtCde) {
        boolean lbExist = false;
        int lnRow = 0;
        
        for (int lnCtr = 0; lnCtr <= paDetail.size()-1;lnCtr++){
            if(paDetail.get(lnCtr).getRqrmtCde().equals(fsRqrmtCde )){
               lbExist = true;
               lnRow = lnCtr;
               break;
            }
        }

        if(lbExist){
            
            if(((String)getDetail(lnRow, "sTransNox")).isEmpty()){
                setDetail(lnRow,"sReceived", "");
                setDetail(lnRow,"sCompnyNm", "");
                setDetail(lnRow,"cSubmittd", "");
            } else {
                setDetail(lnRow,"cSubmittd", "0");
            }
        } 
        
    }
    
    
}
