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
import org.guanzon.appdriver.constant.TransactionStatus;
import org.guanzon.auto.main.sales.VehicleSalesProposal;
import org.guanzon.auto.model.sales.Model_VehicleSalesProposal_Parts;
import org.guanzon.auto.model.service.Model_JobOrder_Parts;
import org.guanzon.auto.validator.sales.ValidatorFactory;
import org.guanzon.auto.validator.sales.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class VehicleSalesProposal_Parts {
    final String XML = "Model_VehicleSalesProposal_Parts.xml";
    GRider poGRider;
    String psTargetBranchCd = "";
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    public JSONObject poJSON;
    
    
    ArrayList<Model_VehicleSalesProposal_Parts> paDetail;
    ArrayList<Model_VehicleSalesProposal_Parts> paRemDetail;
    
    public VehicleSalesProposal_Parts(GRider foAppDrver){
        poGRider = foAppDrver;
    }
    
    public int getEditMode() {
        return pnEditMode;
    }

    public Model_VehicleSalesProposal_Parts getVSPParts(int fnIndex){
        if (fnIndex > paDetail.size() - 1 || fnIndex < 0) return null;
        
        return paDetail.get(fnIndex);
    }
    
    public JSONObject addDetail(String fsTransNo){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        
        poJSON = new JSONObject();
        if (paDetail.size()<=0){
            paDetail.add(new Model_VehicleSalesProposal_Parts(poGRider));
            paDetail.get(0).newRecord();
            
            paDetail.get(0).setTransNo(fsTransNo);
            paDetail.get(0).setEntryNo(0);
            paDetail.get(0).setAddDate(poGRider.getServerDate());
            paDetail.get(0).setAddBy(poGRider.getUserID());
            
            poJSON.put("result", "success");
            poJSON.put("message", "VSP Parts add record.");
        } else {
            paDetail.add(new Model_VehicleSalesProposal_Parts(poGRider));
            paDetail.get(paDetail.size()-1).newRecord();

            paDetail.get(paDetail.size()-1).setTransNo(fsTransNo);
            paDetail.get(paDetail.size()-1).setEntryNo(0);
            paDetail.get(paDetail.size()-1).setAddDate(poGRider.getServerDate());
            paDetail.get(paDetail.size()-1).setAddBy(poGRider.getUserID());
            
            poJSON.put("result", "success");
            poJSON.put("message", "VSP Parts add record.");
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
                        + " , sStockIDx "   
                        + " , sDescript "                                              
                        + "  FROM vsp_parts " ;
        lsSQL = MiscUtil.addCondition(lsSQL, " sTransNox = " + SQLUtil.toSQL(fsValue))
                                                + "  ORDER BY nEntryNox ASC " ;
        System.out.println(lsSQL);
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        String lsParts = "";
        try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                while(loRS.next()){
                        paDetail.add(new Model_VehicleSalesProposal_Parts(poGRider));
                        lsParts = loRS.getString("sStockIDx");
                        if(lsParts.isEmpty()){
                            lsParts = loRS.getString("sDescript");
                        } 
                        paDetail.get(paDetail.size() - 1).openRecord(loRS.getString("sTransNox"), lsParts);
                        
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
            obj.put("message", "Target Branch code for vsp parts cannot be empty.");
            return obj;
        } else {
            if(psTargetBranchCd.isEmpty()){
                obj.put("result", "error");
                obj.put("continue", false);
                obj.put("message", "Target Branch code for vsp parts cannot be empty.");
                return obj;
            }
        }
        
        for (lnCtr = 0; lnCtr <= lnSize; lnCtr++){
            //if(lnCtr>0){
                if(paDetail.get(lnCtr).getDescript().trim().isEmpty()){
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
            
            ValidatorInterface validator = ValidatorFactory.make(ValidatorFactory.TYPE.VehicleSalesProposal_Parts, paDetail.get(lnCtr));
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
    
    public ArrayList<Model_VehicleSalesProposal_Parts> getDetailList(){
        if(paDetail == null){
           paDetail = new ArrayList<>();
        }
        return paDetail;
    }
    public void setDetailList(ArrayList<Model_VehicleSalesProposal_Parts> foObj){this.paDetail = foObj;}
    
    public void setDetail(int fnRow, int fnIndex, Object foValue){ paDetail.get(fnRow).setValue(fnIndex, foValue);}
    public void setDetail(int fnRow, String fsIndex, Object foValue){ paDetail.get(fnRow).setValue(fsIndex, foValue);}
    public Object getDetail(int fnRow, int fnIndex){return paDetail.get(fnRow).getValue(fnIndex);}
    public Object getDetail(int fnRow, String fsIndex){return paDetail.get(fnRow).getValue(fsIndex);}
    
    public Model_VehicleSalesProposal_Parts getDetailModel(int fnRow) {
        return paDetail.get(fnRow);
    }
    
    public Object removeDetail(int fnRow){
        JSONObject loJSON = new JSONObject();
        
        if(paDetail.get(fnRow).getDSNo() != null){
            if(!paDetail.get(fnRow).getDSNo().trim().isEmpty()){
                loJSON.put("result", "error");
                loJSON.put("message", "Part Description " + paDetail.get(fnRow).getDescript()+ " already linked thru Job Order No. "+paDetail.get(fnRow).getDSNo()+ ".\nDelete row aborted.");
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
        String lsParts = paDetail.get(fnRow).getStockID();
        if(lsParts.isEmpty()){
            lsParts = paDetail.get(fnRow).getDescript();
        }
        if (paRemDetail.size()<=0){
            paRemDetail.add(new Model_VehicleSalesProposal_Parts(poGRider));
            paRemDetail.get(0).openRecord(paDetail.get(fnRow).getTransNo(),lsParts);
            poJSON.put("result", "success");
            poJSON.put("message", "added to remove record.");
        } else {
            paRemDetail.add(new Model_VehicleSalesProposal_Parts(poGRider));
            paRemDetail.get(paRemDetail.size()-1).openRecord(paDetail.get(fnRow).getTransNo(),lsParts);
            poJSON.put("result", "success");
            poJSON.put("message", "added to remove record.");
        }
        return poJSON;
    }
    
    
    public JSONObject searchParts(String fsValue) {
        poJSON = new JSONObject();
        String lsHeader = "ID»Description";
        String lsColName = "sBarCodex»sDescript"; 
        String lsCriteria = "sBarCodex»sDescript";
        
        String lsSQL =   " SELECT "                                                
                + "   sStockIDx "                                           
                + " , sBarCodex "                                      
                + " , sDescript "                                       
                + " , nUnitPrce "                                      
                + " , nSelPrice "                                     
                + " , cRecdStat "                                      
                + " FROM inventory " ; 
        lsSQL = MiscUtil.addCondition(lsSQL,  " cRecdStat = '1' "
                                                + " AND sBarCodex LIKE " + SQLUtil.toSQL(fsValue + "%"));
        
        
        System.out.println("SEARCH PARTS: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    lsHeader,
                    lsColName,
                    lsCriteria,
                0);

        if (poJSON != null) {
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
        
        return poJSON;
    }
    
    
    /**
     * Check VSP Parts Quantity linked to JO
     * @param fsValue parts Stock ID
     * @param fnInputQty parts quantity to be input
     * @param fnRow VSP Parts row
    */
    public JSONObject checkVSPJOParts(String fsValue, int fnInputQty, int fnRow) {
        JSONObject loJSON = new JSONObject();
        Model_JobOrder_Parts loEntity = new Model_JobOrder_Parts(poGRider);
        String lsSQL = loEntity.makeSelectSQL();
        lsSQL = MiscUtil.addCondition(lsSQL, " sStockIDx = " + SQLUtil.toSQL(fsValue)) 
                                                + " AND sTransNox IN (SELECT diagnostic_master.sTransNox FROM diagnostic_master " 
                                                +                    " WHERE diagnostic_master.cTranStat <> " + SQLUtil.toSQL(TransactionStatus.STATE_CANCELLED) 
                                                                    + " AND diagnostic_master.sSourceNo = " + SQLUtil.toSQL(paDetail.get(fnRow).getTransNo()) + ")" ;
       
        System.out.println(lsSQL);
        int nTotalQty = 0;
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        if (MiscUtil.RecordCount(loRS) > 0){
            try {
                while(loRS.next()){
                    nTotalQty = nTotalQty + Integer.valueOf(loRS.getString("nQtyEstmt")) ;
                }
                MiscUtil.close(loRS);
                if (fnInputQty < nTotalQty){
                    loJSON.put("result", "error");
                    loJSON.put("message", "Quantity input to JO Parts cannot be less than the VSP Parts Quantity.");
                    return loJSON;
                }
            } catch (SQLException ex) {
                Logger.getLogger(VehicleSalesProposal_Parts.class.getName()).log(Level.SEVERE, null, ex);
            }
        }      
        return loJSON;
    }
}