/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.sales;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.model.sales.Model_VehicleSalesProposal_Labor;
import org.guanzon.auto.model.sales.Model_VehicleSalesProposal_Master;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class VehicleSalesProposal_Labor {
    final String XML = "Model_VehicleSalesProposal_Master.xml";
    final String MOBILE_XML = "Model_VehicleSalesProposal_Labor.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    String psClientType = "0";
    
    Model_VehicleSalesProposal_Master poMaster;
    ArrayList<Model_VehicleSalesProposal_Labor> paVSPLabor;
    
    public JSONObject poJSON;
    
    public JSONObject addVSPLabor(String fsValue){
        poJSON = new JSONObject();
        if (paVSPLabor.isEmpty()){
            paVSPLabor.add(new Model_VehicleSalesProposal_Labor(poGRider));
            paVSPLabor.get(0).newRecord();
            paVSPLabor.get(0).setTransactionNo(fsValue);
            poJSON.put("result", "success");
            poJSON.put("message", "Social media add record.");
        } else {
            //ValidatorInterface validator = ValidatorFactory.make(types,  ValidatorFactory.TYPE.Client_Social_Media, paSocMed.get(paSocMed.size()-1));
//            if (!validator.isEntryOkay()){
//                poJSON.put("result", "error");
//                poJSON.put("message", validator.getMessage());
//                return poJSON;
//            }
            paVSPLabor.add(new Model_VehicleSalesProposal_Labor( poGRider));
            paVSPLabor.get(paVSPLabor.size()-1).newRecord();
            paVSPLabor.get(paVSPLabor.size()-1).setTransactionNo(fsValue);
            poJSON.put("result", "success");
            poJSON.put("message", "Social media add record.");
        }
        return poJSON;
    }
    
    public JSONObject openVSPLabor(String fsValue){
        String lsSQL = getSQL();
        lsSQL = MiscUtil.addCondition(lsSQL, "sTransNox = " + SQLUtil.toSQL(fsValue));
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        System.out.println(lsSQL);
       try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                paVSPLabor = new ArrayList<>();
                while(loRS.next()){
                        paVSPLabor.add(new Model_VehicleSalesProposal_Labor(poGRider));
                        paVSPLabor.get(paVSPLabor.size() - 1).openRecord(loRS.getString("sTransNox"));
                        
                        pnEditMode = EditMode.UPDATE;
                        lnctr++;
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record loaded successfully.");
                    } 
                
                System.out.println("lnctr = " + lnctr);
            }else{
                paVSPLabor = new ArrayList<>();
                addVSPLabor(fsValue);
                poJSON.put("result", "error");
                poJSON.put("continue", true);
                poJSON.put("message", "No record selected.");
            }
            MiscUtil.close(loRS);
        } catch (SQLException e) {
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        return checkData(poJSON);
    }
    
    public JSONObject saveVSPLabor (String fsValue) {
        poJSON = new JSONObject();
        
        int lnCtr;
        String lsSQL;
        
        for (lnCtr = 0; lnCtr <= paVSPLabor.size() -1; lnCtr++){
            paVSPLabor.get(lnCtr).setTransactionNo(fsValue);
            if(lnCtr>0){
                if(paVSPLabor.get(lnCtr).getValue("sLaborCde").toString().isEmpty()){
                    paVSPLabor.remove(lnCtr);
                }
            }
            
//            ValidatorInterface validator = ValidatorFactory.make(types,  ValidatorFactory.TYPE.Client_Social_Media, paSocMed.get(lnCtr));
//            if (!validator.isEntryOkay()){
//                poJSON.put("result", "error");
//                poJSON.put("message", validator.getMessage());
//                return poJSON;
//            }
            
            poJSON = paVSPLabor.get(lnCtr).saveRecord();
        }    
        
        return poJSON;
    }
    
    public Model_VehicleSalesProposal_Labor getVSPLabor(int fnIndex){
        if (fnIndex > paVSPLabor.size() - 1 || fnIndex < 0) return null;
        
        return paVSPLabor.get(fnIndex);
    }
    
    public ArrayList<Model_VehicleSalesProposal_Labor> getVSPLaborList(){return paVSPLabor;}
    public void setVSPLaborList(ArrayList<Model_VehicleSalesProposal_Labor> foObj){this.paVSPLabor = foObj;}
    
    public void setVSPLabor(int fnRow, int fnIndex, Object foValue){ paVSPLabor.get(fnRow).setValue(fnIndex, foValue);}
    public void setVSPLabor(int fnRow, String fsIndex, Object foValue){ paVSPLabor.get(fnRow).setValue(fsIndex, foValue);}
    public Object getVSPLabor(int fnRow, int fnIndex){return paVSPLabor.get(fnRow).getValue(fnIndex);}
    public Object getVSPLabor(int fnRow, String fsIndex){return paVSPLabor.get(fnRow).getValue(fsIndex);}
    
    private Connection setConnection(){
        Connection foConn;
        
        if (pbWtParent){
            foConn = (Connection) poGRider.getConnection();
            if (foConn == null) foConn = (Connection) poGRider.doConnect();
        }else foConn = (Connection) poGRider.doConnect();
        
        return foConn;
    }
    
    private JSONObject checkData(JSONObject joValue){
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
    
    public String getSQL(){
        return " SELECT " +
                "  IFNULL(a.sTransNox, '') AS sTransNox" + //1
                " , a.nEntryNox" + //2
                " , IFNULL(a.sLaborCde, '') AS sLaborCde" + //3
                " , a.nLaborAmt" + //4
                " , IFNULL(a.sChrgeTyp, '') AS sChrgeTyp" + //5
                " , IFNULL(a.sRemarksx, '') AS sRemarksx" + //6
                " , IFNULL(a.sLaborDsc, '') AS sLaborDsc" + //7
                " , a.cAddtlxxx" + //8
                " , a.dAddDatex" + //9
                " , IFNULL(a.sAddByxxx, '') AS sAddByxxx" + //10
                " , IFNULL(GROUP_CONCAT( DISTINCT c.sDSNoxxxx), '') AS sDSNoxxxx " + //11
                " , IFNULL(a.sApproved, '') AS sApproved " + //12
                " , a.dApproved" + //13
                " , IFNULL(d.sCompnyNm, '') AS sApprovBy " + //14
                " FROM vsp_labor a "+
                " LEFT JOIN diagnostic_labor b ON b.sLaborCde = a.sLaborCde " +
                " LEFT JOIN diagnostic_master c ON c.sTransNox = b.sTransNox and c.sSourceCD = a.sTransNox AND c.cTranStat = '1' "  +
                " LEFT JOIN GGC_ISysDBF.client_master d ON d.sClientID = a.sApproved " ;
    }
}
