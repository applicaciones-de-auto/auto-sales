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
import org.guanzon.auto.model.sales.Model_VehicleSalesProposal_Finance;
import org.guanzon.auto.model.sales.Model_VehicleSalesProposal_Master;
import org.guanzon.auto.model.sales.Model_VehicleSalesProposal_Parts;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class VehicleSalesProposal_Finance {
    
    final String FINANCE_XML = "Model_VehicleSalesProposal_Finance.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    
    int pnEditMode;
    String psMessagex;
    
    ArrayList<Model_VehicleSalesProposal_Finance> paVSPFinance;
    
    public JSONObject poJSON;

    public JSONObject addVSPFinance(String fsValue) {
        poJSON = new JSONObject();
        if (paVSPFinance.isEmpty()){
            paVSPFinance.add(new Model_VehicleSalesProposal_Finance(poGRider));
            paVSPFinance.get(0).newRecord();
            paVSPFinance.get(0).setTransactionNo(fsValue);
            poJSON.put("result", "success");
            poJSON.put("message", "VSP Finance add record.");
        } else {
            //ValidatorInterface validator = ValidatorFactory.make(types,  ValidatorFactory.TYPE.Client_Social_Media, paSocMed.get(paSocMed.size()-1));
//            if (!validator.isEntryOkay()){
//                poJSON.put("result", "error");
//                poJSON.put("message", validator.getMessage());
//                return poJSON;
//            }
            paVSPFinance.add(new Model_VehicleSalesProposal_Finance( poGRider));
            paVSPFinance.get(paVSPFinance.size()-1).newRecord();
            paVSPFinance.get(paVSPFinance.size()-1).setTransactionNo(fsValue);
            poJSON.put("result", "success");
            poJSON.put("message", "VSP Finance add record.");
        }
        return poJSON;
    }
    
    public JSONObject openVSPFinance (String fsValue) {
        
        String lsSQL = MiscUtil.addCondition(getSQL(), "sTransNox = " + SQLUtil.toSQL(fsValue));
        ResultSet loRS = poGRider.executeQuery(lsSQL);
        
        System.out.println(lsSQL);
       try {
            int lnctr = 0;
            if (MiscUtil.RecordCount(loRS) > 0) {
                paVSPFinance = new ArrayList<>();
                while(loRS.next()){
                        paVSPFinance.add(new Model_VehicleSalesProposal_Finance(poGRider));
                        paVSPFinance.get(paVSPFinance.size() - 1).openRecord(loRS.getString("sTransNox"));
                        
                        pnEditMode = EditMode.UPDATE;
                        lnctr++;
                        poJSON.put("result", "success");
                        poJSON.put("message", "Record loaded successfully.");
                    } 
                
                System.out.println("lnctr = " + lnctr);
            }else{
                paVSPFinance = new ArrayList<>();
                addVSPFinance(fsValue);
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
    
    public JSONObject saveVSPFinance (String fsValue) {
        poJSON = new JSONObject();
        
        int lnCtr;
        String lsSQL;
        
        for (lnCtr = 0; lnCtr <= paVSPFinance.size() -1; lnCtr++){
            paVSPFinance.get(lnCtr).setTransactionNo(fsValue);
            if(lnCtr>0){
                if(paVSPFinance.get(lnCtr).getValue("sBankIDxx").toString().isEmpty()){
                    paVSPFinance.remove(lnCtr);
                }
            }
            
//            ValidatorInterface validator = ValidatorFactory.make(types,  ValidatorFactory.TYPE.Client_Social_Media, paSocMed.get(lnCtr));
//            if (!validator.isEntryOkay()){
//                poJSON.put("result", "error");
//                poJSON.put("message", validator.getMessage());
//                return poJSON;
//            }
            
            poJSON = paVSPFinance.get(lnCtr).saveRecord();
        }    
        
        return poJSON;
    }
    
    public Model_VehicleSalesProposal_Finance getVSPFinance(int fnIndex){
        if (fnIndex > paVSPFinance.size() - 1 || fnIndex < 0) return null;
        
        return paVSPFinance.get(fnIndex);
    }
    
    public ArrayList<Model_VehicleSalesProposal_Finance> getVSPFinanceList(){return paVSPFinance;}
    public void setVSPFinanceList(ArrayList<Model_VehicleSalesProposal_Finance> foObj){this.paVSPFinance = foObj;}
    
    public void setVSPFinance(int fnRow, int fnIndex, Object foValue){ paVSPFinance.get(fnRow).setValue(fnIndex, foValue);}
    public void setVSPFinance(int fnRow, String fsIndex, Object foValue){ paVSPFinance.get(fnRow).setValue(fsIndex, foValue);}
    public Object getVSPFinance(int fnRow, int fnIndex){return paVSPFinance.get(fnRow).getValue(fnIndex);}
    public Object getVSPFinance(int fnRow, String fsIndex){return paVSPFinance.get(fnRow).getValue(fsIndex);}

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
            "  IFNULL(a.sTransNox, '') as sTransNox" + //1
            "  , a.cFinPromo" + //2
            "  , IFNULL(a.sBankIDxx, '') AS sBankIDxx" + //3
            "  , IFNULL(a.sBankname, '') AS sBankname" + //4
            "  , a.nFinAmtxx" + //5
            "  , a.nAcctTerm" + //6
            "  , a.nAcctRate" + //7
            "  , a.nRebatesx" + //8
            "  , a.nMonAmort" + //9
            "  , a.nPNValuex" + //10
            "  , a.nBnkPaidx" + //11
            "  , a.nGrsMonth" + //12
            "  , a.nNtDwnPmt" + //13
            "  , a.nDiscount" + //14
              /*dTimeStmp*/
            " FROM vsp_finance a"  ;
    }
}
