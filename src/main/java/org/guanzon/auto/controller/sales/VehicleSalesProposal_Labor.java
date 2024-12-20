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
import org.guanzon.auto.model.sales.Model_VehicleSalesProposal_Labor;
import org.guanzon.auto.validator.sales.ValidatorFactory;
import org.guanzon.auto.validator.sales.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class VehicleSalesProposal_Labor {
    final String XML = "Model_VehicleSalesProposal_Labor.xml";
    GRider poGRider;
    String psTargetBranchCd = "";
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    public JSONObject poJSON;
    
    ArrayList<Model_VehicleSalesProposal_Labor> paDetail;
    ArrayList<Model_VehicleSalesProposal_Labor> paRemDetail;
    
    public VehicleSalesProposal_Labor(GRider foAppDrver){
        poGRider = foAppDrver;
    }
    
    public int getEditMode() {
        return pnEditMode;
    }

    public Model_VehicleSalesProposal_Labor getVSPLabor(int fnIndex){
        if (fnIndex > paDetail.size() - 1 || fnIndex < 0) return null;
        
        return paDetail.get(fnIndex);
    }
    
    public JSONObject addDetail(String fsTransNo){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        
        poJSON = new JSONObject();
        if (paDetail.size()<=0){
            paDetail.add(new Model_VehicleSalesProposal_Labor(poGRider));
            paDetail.get(0).newRecord();
            
            paDetail.get(0).setTransNo(fsTransNo);
            paDetail.get(0).setEntryNo(0);
            paDetail.get(0).setAddDate(poGRider.getServerDate());
            paDetail.get(0).setAddBy(poGRider.getUserID());
            poJSON.put("result", "success");
            poJSON.put("message", "VSP Labor add record.");
        } else {
            paDetail.add(new Model_VehicleSalesProposal_Labor(poGRider));
            paDetail.get(paDetail.size()-1).newRecord();

            paDetail.get(paDetail.size()-1).setTransNo(fsTransNo);
            paDetail.get(paDetail.size()-1).setEntryNo(0);
            paDetail.get(paDetail.size()-1).setAddDate(poGRider.getServerDate());
            paDetail.get(paDetail.size()-1).setAddBy(poGRider.getUserID());
            
            poJSON.put("result", "success");
            poJSON.put("message", "VSP Labor add record.");
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
                        + " , sLaborCde "                                               
                        + "  FROM vsp_labor " ;
        lsSQL = MiscUtil.addCondition(lsSQL, " sTransNox = " + SQLUtil.toSQL(fsValue))
                                                + "  ORDER BY nEntryNox ASC " ;
        System.out.println(lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                while(loRS.next()){
                        paDetail.add(new Model_VehicleSalesProposal_Labor(poGRider));
                        paDetail.get(paDetail.size() - 1).openRecord(loRS.getString("sTransNox"), loRS.getString("sLaborCde"));
                        
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
        
        if(psTargetBranchCd == null){
            obj.put("result", "error");
            obj.put("continue", false);
            obj.put("message", "Target Branch code for vsp labor cannot be empty.");
            return obj;
        } else {
            if(psTargetBranchCd.isEmpty()){
                obj.put("result", "error");
                obj.put("continue", false);
                obj.put("message", "Target Branch code for vsp labor cannot be empty.");
                return obj;
            }
        }
        
        for (lnCtr = 0; lnCtr <= lnSize; lnCtr++){
            //if(lnCtr>0){
                if(paDetail.get(lnCtr).getLaborCde().isEmpty()){
                    if(lnSize == 0){
                        obj.put("result", "error");
                        obj.put("continue", true);
                        return obj;
                    }
                    continue; //skip, instead of removing the actual detail
//                    paDetail.remove(lnCtr);
//                    lnCtr++;
//                    if(lnCtr > lnSize){
//                        break;
//                    } 
                }
            //}
            
            paDetail.get(lnCtr).setTransNo(fsTransNo);
            paDetail.get(lnCtr).setEntryNo(lnCtr+1);
            paDetail.get(lnCtr).setTargetBranchCd(psTargetBranchCd);
            
            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.VehicleSalesProposal_Labor, paDetail.get(lnCtr));
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
    
    public ArrayList<Model_VehicleSalesProposal_Labor> getDetailList(){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        return paDetail;
    }
    public void setDetailList(ArrayList<Model_VehicleSalesProposal_Labor> foObj){this.paDetail = foObj;}
    
    public void setDetail(int fnRow, int fnIndex, Object foValue){ paDetail.get(fnRow).setValue(fnIndex, foValue);}
    public void setDetail(int fnRow, String fsIndex, Object foValue){ paDetail.get(fnRow).setValue(fsIndex, foValue);}
    public Object getDetail(int fnRow, int fnIndex){return paDetail.get(fnRow).getValue(fnIndex);}
    public Object getDetail(int fnRow, String fsIndex){return paDetail.get(fnRow).getValue(fsIndex);}
    
    public Model_VehicleSalesProposal_Labor getDetailModel(int fnRow) {
        return paDetail.get(fnRow);
    }
    
    public Object removeDetail(int fnRow){
        JSONObject loJSON = new JSONObject();
        
        if(paDetail.get(fnRow).getDSNo() != null){
            if(!paDetail.get(fnRow).getDSNo().trim().isEmpty()){
                loJSON.put("result", "error");
                loJSON.put("message", "Labor " + paDetail.get(fnRow).getLaborDsc() + " already linked thru job order.\nDelete row aborted.");
                return loJSON;
            }
        }
        
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
            paRemDetail.add(new Model_VehicleSalesProposal_Labor(poGRider));
            paRemDetail.get(0).openRecord(paDetail.get(fnRow).getTransNo(),paDetail.get(fnRow).getLaborCde());
            poJSON.put("result", "success");
            poJSON.put("message", "added to remove record.");
        } else {
            paRemDetail.add(new Model_VehicleSalesProposal_Labor(poGRider));
            paRemDetail.get(paRemDetail.size()-1).openRecord(paDetail.get(fnRow).getTransNo(),paDetail.get(fnRow).getLaborCde());
            poJSON.put("result", "success");
            poJSON.put("message", "added to remove record.");
        }
        return poJSON;
    }
    
    public JSONObject searchLabor(String fsValue, boolean withUI) {
        poJSON = new JSONObject();
        String lsHeader = "ID»Description";
        String lsColName = "sLaborCde»sLaborDsc"; 
        String lsCriteria = "sLaborCde»sLaborDsc";
        
        String lsSQL =   " SELECT "                                             
                + "   sLaborCde "                                      
                + " , sLaborDsc "                                      
                + " , cRecdStat "                                      
                + " FROM labor " ; 
        if(withUI){
            lsSQL = MiscUtil.addCondition(lsSQL,  " cRecdStat = '1' "
                                                + " AND sLaborDsc LIKE " + SQLUtil.toSQL(fsValue + "%"));
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL,  " cRecdStat = '1' "
                                                + " AND TRIM(REPLACE(sLaborDsc, ' ', '')) = " + SQLUtil.toSQL(fsValue.replace(" ", "")))
                                                + " LIMIT 1 ";
        }
        
        System.out.println("SEARCH LABOR: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    lsHeader,
                    lsColName,
                    lsCriteria,
                1);

        if (poJSON != null) {
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
        
        return poJSON;
    }
    
    public JSONObject searchVSPLabor(String fsValue, String fsTransNo, boolean fbByCode) {
        JSONObject loJSON = new JSONObject();
        String lsHeader = "ID»Description";
        String lsColName = "sLaborCde»sLaborDsc"; 
        String lsCriteria = "sLaborCde»sLaborDsc";
        
        Model_VehicleSalesProposal_Labor loEntity = new Model_VehicleSalesProposal_Labor(poGRider);
        String lsSQL =  loEntity.getSQL() ; 
        
        if(fbByCode){
            lsSQL = MiscUtil.addCondition(lsSQL,  " a.sLaborCde =  " + SQLUtil.toSQL(fsValue)
                                                    + " AND a.sTransNox = " + SQLUtil.toSQL(fsTransNo))
                                                    + " GROUP BY a.sLaborCde ";
            
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL,  " a.sLaborDsc LIKE  " + SQLUtil.toSQL(fsValue + "%")
                                                    + " AND a.sTransNox = " + SQLUtil.toSQL(fsTransNo))
                                                    + " GROUP BY a.sLaborCde ";
        
        }
        
        System.out.println("SEARCH VSP LABOR: " + lsSQL);
        loJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    lsHeader,
                    lsColName,
                    lsCriteria,
                fbByCode ? 0 : 1);

        if (loJSON != null) {
        } else {
            loJSON = new JSONObject();
            loJSON.put("result", "error");
            loJSON.put("message", "No record loaded.");
            return loJSON;
        }
        
        return loJSON;
    }
}
