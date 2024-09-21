/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.sales;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.auto.model.sales.Model_Activity_Member;
import org.guanzon.auto.validator.sales.ValidatorFactory;
import org.guanzon.auto.validator.sales.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Activity_Member {
    final String XML = "Model_Activity_Member.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    public JSONObject poJSON;
    
    ArrayList<Model_Activity_Member> paDetail;
    ArrayList<String> paDepartmentID;
    ArrayList<String> paDepartmentNm;
    ArrayList<String> paEmployeeID;
    ArrayList<String> paEmployeeNm;
    ArrayList<String> paEmpDeptNm;
    
    public Activity_Member(GRider foAppDrver){
        poGRider = foAppDrver;
    }

    public int getEditMode() {
        return pnEditMode;
    }
    
    public Model_Activity_Member getDetailModel(int fnIndex){
        if (fnIndex > paDetail.size() - 1 || fnIndex < 0) return null;
        
        return paDetail.get(fnIndex);
    }
    
    public JSONObject addDetail(String fsTransNo){
        
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        
        poJSON = new JSONObject();
        if (paDetail.size()<=0){
            paDetail.add(new Model_Activity_Member(poGRider));
            paDetail.get(0).newRecord();
            
            paDetail.get(0).setValue("sTransNox", fsTransNo);
            paDetail.get(0).setValue("cOriginal", "1");
            paDetail.get(0).setValue("nEntryNox", 0);
            poJSON.put("result", "success");
            poJSON.put("message", "Activity Member add record.");
        } else {
            paDetail.add(new Model_Activity_Member(poGRider));
            paDetail.get(paDetail.size()-1).newRecord();

            paDetail.get(paDetail.size()-1).setTransNo(fsTransNo);
            paDetail.get(paDetail.size()-1).setValue("cOriginal", "1");
            paDetail.get(paDetail.size()-1).setValue("nEntryNox", 0);
            poJSON.put("result", "success");
            poJSON.put("message", "Activity Member add record.");
        }
        return poJSON;
    }
    
    public JSONObject openDetail(String fsValue){
        paDetail = new ArrayList<>();
        poJSON = new JSONObject();
        String lsSQL =      "   SELECT "                                                                 
                        + "   sTransNox "                                                            
                        + " , nEntryNox "                                                            
                        + " , cOriginal "                                                           
                        + " , sEmployID "                                                            
                        + " FROM activity_member " ;
        lsSQL = MiscUtil.addCondition(lsSQL, "sTransNox = " + SQLUtil.toSQL(fsValue));
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        System.out.println(lsSQL);
       try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                while(loRS.next()){
                        paDetail.add(new Model_Activity_Member(poGRider));
                        paDetail.get(paDetail.size() - 1).openRecord(loRS.getString("sTransNox"), loRS.getString("sEmployID"));
                        
                        pnEditMode = EditMode.UPDATE;
                        lnctr++;
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record loaded successfully.");
                    } 
                
            }else{
//                paDetail = new ArrayList<>();
//                addDetail(fsValue);
                poJSON.put("result", "error");
                poJSON.put("continue", false);
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
        int lnEntryNo = 1;
        for (lnCtr = 0; lnCtr <= lnSize; lnCtr++){
            //if(lnCtr>0){
                if(paDetail.get(lnCtr).getEmployID().isEmpty()){
                    continue; //skip, instead of removing the actual detail
//                    paDetail.remove(lnCtr);
//                    lnCtr++;
//                    if(lnCtr > lnSize){
//                        break;
//                    } 
                }
            //} 
            paDetail.get(lnCtr).setTransNo(fsTransNo);
            paDetail.get(lnCtr).setEntryNo(lnEntryNo);
            
            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.Activity_Member, paDetail.get(lnCtr));
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
    
    public ArrayList<Model_Activity_Member> getDetailList(){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        return paDetail;
    }
    public void setDetailList(ArrayList<Model_Activity_Member> foObj){this.paDetail = foObj;}
    
    public void setDetail(int fnRow, int fnIndex, Object foValue){ paDetail.get(fnRow).setValue(fnIndex, foValue);}
    public void setDetail(int fnRow, String fsIndex, Object foValue){ paDetail.get(fnRow).setValue(fsIndex, foValue);}
    public Object getDetail(int fnRow, int fnIndex){return paDetail.get(fnRow).getValue(fnIndex);}
    public Object getDetail(int fnRow, String fsIndex){return paDetail.get(fnRow).getValue(fsIndex);}
    
    public Object removeDetail(int fnRow){
        JSONObject loJSON = new JSONObject();
        fnRow = fnRow-1;
        if(paDetail.get(fnRow).getEntryNo() != 0){
            paDetail.get(fnRow).setOriginal("0");
        } else {
            paDetail.remove(fnRow);
        }
        
        return loJSON;
    }
    
    /**
     * Loads the department data.
     * @return {@code true} if the department data is successfully loaded,
     * {@code false} otherwise
     */
    public JSONObject loadDepartment() {
        paDepartmentID = new ArrayList<>();
        paDepartmentNm = new ArrayList<>();
        JSONObject jObj = new JSONObject();
        String lsSQL =    " SELECT "
                        + " sDeptIDxx"
                        + " , sDeptName "
                        + " , cRecdStat "
                        + "FROM GGC_ISysDBF.Department ";
        lsSQL = MiscUtil.addCondition(lsSQL, " cRecdStat = " + SQLUtil.toSQL("1")) 
                + " ORDER BY sDeptIDxx DESC ";
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        System.out.println(lsSQL);
       try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                while(loRS.next()){
                    paDepartmentID.add(lnctr, loRS.getString("sDeptIDxx"));
                    paDepartmentNm.add(lnctr, loRS.getString("sDeptName"));
                } 
                     
                if(paDepartmentID.size() == paDepartmentNm.size()){
                    jObj.put("result", "success");
                    jObj.put("message", "Department loaded successfully.");
                } else {
                    jObj.put("result", "error");
                    jObj.put("message", "Department did not load properly.");
                }
                
            }else{
                jObj.put("result", "error");
                jObj.put("message", "No Department loaded.");
            }
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            jObj.put("result", "error");
            jObj.put("message", e.getMessage());
        }
       
        return jObj;
    }
    
    public ArrayList<String> getDepartmentList(){return paDepartmentID;}
    public Object getDepartmentID(int fnRow, int fnIndex){return paDepartmentID.get(fnRow);}
    public Object getDepartmentID(int fnRow, String fsIndex){return paDepartmentID.get(fnRow);}
    
    public Object getDepartmentNm(int fnRow, int fnIndex){return paDepartmentNm.get(fnRow);}
    public Object getDepartmentNm(int fnRow, String fsIndex){return paDepartmentNm.get(fnRow);}
    
    /**
     * Loads employee data based on the specified value and load mode.
     * @param fsValue Department ID
     */
    public JSONObject loadEmployee(String fsValue) {
        paEmployeeID = new ArrayList<>();
        paEmployeeNm = new ArrayList<>();
        paEmpDeptNm = new ArrayList<>();
        JSONObject jObj = new JSONObject();
        
        if(fsValue.trim().isEmpty()){
            jObj.put("result", "error");
            jObj.put("message", "Passed department cannot be empty.");
        }
        String lsSQL =    " SELECT "                                                             
                        + "   a.sEmployID "                                                      
                        + " , a.sDeptIDxx "                                                      
                        + " , b.sCompnyNm "  
                        + " , c.sDeptName "                                                    
                        + " FROM GGC_ISysDBF.Employee_Master001 a "                              
                        + " LEFT JOIN GGC_ISysDBF.Client_Master b ON b.sClientID = a.sEmployID " 
                        + " LEFT JOIN GGC_ISysDBF.Department c ON c.sDeptIDxx = a.sDeptIDxx "
                        + " WHERE a.cRecdStat = '1' AND ISNULL(a.dFiredxxx) "
                        + " AND a.sDeptIDxx = " + SQLUtil.toSQL(fsValue)   
                        + " ORDER BY b.sCompnyNm DESC ";
        
        System.out.println(lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                while(loRS.next()){
                    paEmployeeID.add(lnctr, loRS.getString("sEmployID"));
                    paEmployeeNm.add(lnctr, loRS.getString("sCompnyNm"));
                    paEmpDeptNm.add(lnctr, loRS.getString("sDeptName"));
                } 
                     
                if(paEmployeeID.size() == paEmployeeNm.size()){
                    jObj.put("result", "success");
                    jObj.put("message", "Employee loaded successfully.");
                } else {
                    jObj.put("result", "error");
                    jObj.put("message", "Employee did not load properly.");
                }
                
            }else{
                jObj.put("result", "error");
                jObj.put("message", "No Department loaded.");
            }
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            jObj.put("result", "error");
            jObj.put("message", e.getMessage());
        }
       
        return jObj;
    }
    
    public ArrayList<String> getEmployeeList(){return paEmployeeID;}
    public Object getEmployeeID(int fnRow, int fnIndex){return paEmployeeID.get(fnRow);}
    public Object getEmployeeID(int fnRow, String fsIndex){return paEmployeeID.get(fnRow);}
    
    public Object getEmployeeNm(int fnRow, int fnIndex){return paEmployeeNm.get(fnRow);}
    public Object getEmployeeNm(int fnRow, String fsIndex){return paEmployeeNm.get(fnRow);}
    
    public Object getEmpDeptNm(int fnRow, int fnIndex){return paEmpDeptNm.get(fnRow);}
    public Object getEmpDeptNm(int fnRow, String fsIndex){return paEmpDeptNm.get(fnRow);}
}
