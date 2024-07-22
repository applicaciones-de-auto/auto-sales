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
import org.guanzon.auto.model.sales.Model_VehicleSalesProposal_Parts;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class VehicleSalesProposal_Parts {
    final String XML = "Model_VehicleSalesProposal_Master.xml";
    final String MOBILE_XML = "Model_VehicleSalesProposal_Parts.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    String psClientType = "0";
    
    ArrayList<Model_VehicleSalesProposal_Parts> paVSPParts;
    
    public JSONObject poJSON;
    
    public JSONObject addVSPParts(String fsValue){
        poJSON = new JSONObject();
        if (paVSPParts.isEmpty()){
            paVSPParts.add(new Model_VehicleSalesProposal_Parts(poGRider));
            paVSPParts.get(0).newRecord();
            paVSPParts.get(0).setTransactionNo(fsValue);
            poJSON.put("result", "success");
            poJSON.put("message", "VSP Parts add record.");
        } else {
            //ValidatorInterface validator = ValidatorFactory.make(types,  ValidatorFactory.TYPE.Client_Social_Media, paSocMed.get(paSocMed.size()-1));
//            if (!validator.isEntryOkay()){
//                poJSON.put("result", "error");
//                poJSON.put("message", validator.getMessage());
//                return poJSON;
//            }
            paVSPParts.add(new Model_VehicleSalesProposal_Parts( poGRider));
            paVSPParts.get(paVSPParts.size()-1).newRecord();
            paVSPParts.get(paVSPParts.size()-1).setTransactionNo(fsValue);
            poJSON.put("result", "success");
            poJSON.put("message", "VSP Parts add record");
        }
        return poJSON;
    }
    
    public JSONObject openVSPParts(String fsValue){
        String lsSQL = getSQL();
        lsSQL = MiscUtil.addCondition(lsSQL, "sTransNox = " + SQLUtil.toSQL(fsValue));
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        System.out.println(lsSQL);
       try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                paVSPParts = new ArrayList<>();
                while(loRS.next()){
                        paVSPParts.add(new Model_VehicleSalesProposal_Parts(poGRider));
                        paVSPParts.get(paVSPParts.size() - 1).openRecord(loRS.getString("sTransNox"));
                        
                        pnEditMode = EditMode.UPDATE;
                        lnctr++;
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record loaded successfully.");
                    } 
                
                System.out.println("lnctr = " + lnctr);
            }else{
                paVSPParts = new ArrayList<>();
                addVSPParts(fsValue);
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
    
    public JSONObject saveVSPParts (String fsValue) {
        poJSON = new JSONObject();
        
        int lnCtr;
        String lsSQL;
        
        for (lnCtr = 0; lnCtr <= paVSPParts.size() -1; lnCtr++){
            paVSPParts.get(lnCtr).setTransactionNo(fsValue);
            if(lnCtr>0){
                if(paVSPParts.get(lnCtr).getValue("sDescript").toString().isEmpty()){
                    paVSPParts.remove(lnCtr);
                }
            }
            
//            ValidatorInterface validator = ValidatorFactory.make(types,  ValidatorFactory.TYPE.Client_Social_Media, paSocMed.get(lnCtr));
//            if (!validator.isEntryOkay()){
//                poJSON.put("result", "error");
//                poJSON.put("message", validator.getMessage());
//                return poJSON;
//            }
            
            poJSON = paVSPParts.get(lnCtr).saveRecord();
        }    
        
        return poJSON;
    }
    
    public Model_VehicleSalesProposal_Parts getVSPParts(int fnIndex){
        if (fnIndex > paVSPParts.size() - 1 || fnIndex < 0) return null;
        
        return paVSPParts.get(fnIndex);
    }
    
    public ArrayList<Model_VehicleSalesProposal_Parts> getVSPPartsList(){return paVSPParts;}
    public void setVSPPartsList(ArrayList<Model_VehicleSalesProposal_Parts> foObj){this.paVSPParts = foObj;}
    
    public void setVSPParts(int fnRow, int fnIndex, Object foValue){ paVSPParts.get(fnRow).setValue(fnIndex, foValue);}
    public void setVSPParts(int fnRow, String fsIndex, Object foValue){ paVSPParts.get(fnRow).setValue(fsIndex, foValue);}
    public Object getVSPParts(int fnRow, int fnIndex){return paVSPParts.get(fnRow).getValue(fnIndex);}
    public Object getVSPParts(int fnRow, String fsIndex){return paVSPParts.get(fnRow).getValue(fsIndex);}
    
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
        return " SELECT  " +
                " IFNULL(a.sTransNox, '') AS sTransNox" + //1
                "  , a.nEntryNox" + //2
                "  , IFNULL(a.sStockIDx, '') AS sStockIDx" + //3
                "  , a.nUnitPrce" + //4
                "  , a.nSelPrice" + //5
                "  , a.nQuantity" + //6
                "  , a.nReleased" + //7
                "  , IFNULL(a.sChrgeTyp, '') AS sChrgeTyp" + //8
                "  , IFNULL(a.sDescript, '') AS sDescript" + //9 Sales Parts Description
                "  , IFNULL(a.sPartStat, '') AS sPartStat" + //10
                "  , IFNULL(GROUP_CONCAT(DISTINCT d.sDSNoxxxx), '') AS sDSNoxxxx" + //11
                "  , a.dAddDatex" + //12
                "  , IFNULL(a.sAddByxxx, '') AS sAddByxxx" + //13
                "  , IFNULL(b.sBarCodex, '') AS sBarCodex" + //14 
                "  , IFNULL(a.nQuantity * a.nUnitPrce, '') AS sTotlAmtx " + //15
                "  , SUM(c.nQtyEstmt) AS sQtyEstmt " + //16 
                "  , IFNULL(b.sDescript, '') AS sPartDesc " + //17 Parts Description
                " , IFNULL(a.sApproved, '') AS sApproved " + //18
                " , a.dApproved" + //19
                " , IFNULL(e.sCompnyNm, '') AS sApprovBy" + //20
                //  /*dTImeStmp*/
                " FROM vsp_parts a " +
                " LEFT JOIN inventory b ON b.sStockIDx = a.sStockIDx " +
                " LEFT JOIN diagnostic_parts c ON c.sStockIDx = a.sStockIDx  " +
                " LEFT JOIN diagnostic_master d ON d.sTransNox = c.sTransNox AND d.sSourceCD = a.sTransNox AND d.cTranStat = '1' " +
                " LEFT JOIN GGC_ISysDBF.client_master e ON e.sClientID = a.sApproved " ;
    }
}