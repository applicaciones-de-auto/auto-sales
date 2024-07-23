/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.controller.sales;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.guanzon.appdriver.agent.ShowDialogFX;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.general.SearchDialog;
import org.guanzon.auto.model.sales.Model_Activity_Master;
import org.guanzon.auto.validator.sales.ValidatorFactory;
import org.guanzon.auto.validator.sales.ValidatorInterface;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Activity_Master implements GTransaction {
    final String XML = "Model_Inquiry_Master.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    int pnEditMode;
    String psTransStat;
    
    String psMessagex;
    public JSONObject poJSON;
    
    Model_Activity_Master poMaster;
    
    public Activity_Master(GRider foGRider, boolean fbWthParent, String fsBranchCd) {
        poGRider = foGRider;
        pbWtParent = fbWthParent;
        psBranchCd = fsBranchCd.isEmpty() ? foGRider.getBranchCode() : fsBranchCd;

        poMaster = new Model_Activity_Master(foGRider);
        pnEditMode = EditMode.UNKNOWN;
    }

    @Override
    public int getEditMode() {
        return pnEditMode;
    }

    @Override
    public void setTransactionStatus(String fsValue) {
        psTransStat = fsValue;
    }

    @Override
    public JSONObject setMaster(int fnCol, Object foData) {
        JSONObject obj = new JSONObject();
        obj.put("pnEditMode", pnEditMode);
        if (pnEditMode != EditMode.UNKNOWN){
            // Don't allow specific fields to assign values
            if(!(fnCol == poMaster.getColumn("sTransNox") ||
                fnCol == poMaster.getColumn("cRecdStat") ||
                fnCol == poMaster.getColumn("sModified") ||
                fnCol == poMaster.getColumn("dModified"))){
                poMaster.setValue(fnCol, foData);
                obj.put(fnCol, pnEditMode);
            }
        }
        return obj;
    }

    @Override
    public JSONObject setMaster(String fsCol, Object foData) {
        return setMaster(poMaster.getColumn(fsCol), foData);
    }
    
    public Object getMaster(int fnCol) {
        if(pnEditMode == EditMode.UNKNOWN)
            return null;
        else 
            return poMaster.getValue(fnCol);
    }

    public Object getMaster(String fsCol) {
        return getMaster(poMaster.getColumn(fsCol));
    }
    
    @Override
    public JSONObject newTransaction() {
        poJSON = new JSONObject();
        try{
            pnEditMode = EditMode.ADDNEW;
            org.json.simple.JSONObject obj;

            poMaster = new Model_Activity_Master(poGRider);
            Connection loConn = null;
            loConn = setConnection();
            poMaster.setActvtyID(MiscUtil.getNextCode(poMaster.getTable(), "sActvtyID", true, poGRider.getConnection(), poGRider.getBranchCode()));
            poMaster.setActNo(MiscUtil.getNextCode(poMaster.getTable(), "sActNoxxx", true, poGRider.getConnection(), poGRider.getBranchCode()+"ACT"));
            poMaster.newRecord();
            
            if (poMaster == null){
                poJSON.put("result", "error");
                poJSON.put("message", "initialized new record failed.");
                return poJSON;
            }else{
                poJSON.put("result", "success");
                poJSON.put("message", "initialized new record.");
                pnEditMode = EditMode.ADDNEW;
            }
        }catch(NullPointerException e){
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
        }
        
        return poJSON;
    }
    
    private Connection setConnection(){
        Connection foConn;
        if (pbWtParent){
            foConn = (Connection) poGRider.getConnection();
            if (foConn == null) foConn = (Connection) poGRider.doConnect();
        }else foConn = (Connection) poGRider.doConnect();
        return foConn;
    }
    
    @Override
    public JSONObject openTransaction(String fsValue) {
        pnEditMode = EditMode.READY;
        poJSON = new JSONObject();
        
        poMaster = new Model_Activity_Master(poGRider);
        poJSON = poMaster.openRecord(fsValue);
        
        return poJSON;
    }

    @Override
    public JSONObject updateTransaction() {
        poJSON = new JSONObject();
        if (pnEditMode != EditMode.READY && pnEditMode != EditMode.UPDATE){
            poJSON.put("result", "error");
            poJSON.put("message", "Invalid edit mode.");
            return poJSON;
        }
        pnEditMode = EditMode.UPDATE;
        poJSON.put("result", "success");
        poJSON.put("message", "Update mode success.");
        return poJSON;
    }

    @Override
    public JSONObject saveTransaction() {
        poJSON = new JSONObject();  
//        ValidatorInterface validator = ValidatorFactory.make( ValidatorFactory.TYPE.Activity_Master, poMaster);
//        if (!validator.isEntryOkay()){
//            poJSON.put("result", "error");
//            poJSON.put("message", validator.getMessage());
//            return poJSON;
//        }
        
        poJSON =  poMaster.saveRecord();
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        return poJSON;
    }

    @Override
    public JSONObject deleteTransaction(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject closeTransaction(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject postTransaction(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject voidTransaction(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject cancelTransaction(String fsValue) {
        poJSON = new JSONObject();

        if (poMaster.getEditMode() == EditMode.UPDATE) {
            poJSON = poMaster.setActive(false);

            if ("error".equals((String) poJSON.get("result"))) {
                return poJSON;
            }
            
//            poJSON = validateEntry();
//            if ("error".equals((String) poJSON.get("result"))) {
//                return poJSON;
//            }

            poJSON = poMaster.saveRecord();
            if ("success".equals((String) poJSON.get("result"))) {
                poJSON.put("result", "success");
                poJSON.put("message", "Cancellation success.");
            } else {
                poJSON.put("result", "error");
                poJSON.put("message", "Cancellation failed.");
            }
        } else {
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No transaction loaded to update.");
        }
        return poJSON;
    }
    
    public JSONObject searchTransaction(String fsValue, boolean fbByCode) {
        String lsHeader = "Activity Start Date»Activity End Date»Activity No»Activity Title";
        String lsColName = "dDateFrom»dDateThru»sActNoxxx»sActTitle";
        String lsSQL =  poMaster.getSQL(); ;  
        
        if (fbByCode){
            lsSQL = MiscUtil.addCondition(lsSQL, " a.sActNoxxx = " + SQLUtil.toSQL(fsValue));
        } else {
            lsSQL = MiscUtil.addCondition(lsSQL, " a.sActTitle LIKE " + SQLUtil.toSQL(fsValue + "%"));
        }
        
        System.out.println(lsSQL);
        JSONObject loJSON = SearchDialog.jsonSearch(
                    poGRider,
                    lsSQL,
                    fsValue,
                    lsHeader,
                    lsColName,
                "0.3D»0.3D»0.5D", 
                    "ACTIVITY",
                    0);
            
        if (loJSON != null && !"error".equals((String) loJSON.get("result"))) {
        }else {
            loJSON.put("result", "error");
            loJSON.put("message", "No Transaction loaded.");
            return loJSON;
        }
        return loJSON;
    }

    @Override
    public JSONObject searchWithCondition(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject searchTransaction(String string, String string1, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject searchMaster(String string, String string1, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public JSONObject searchMaster(int i, String string, boolean bln) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object getMasterModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Model_Activity_Master getModel() {
        return poMaster;
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
    
    public JSONObject searchDepartment(String fsValue) {
        poJSON = new JSONObject();
         
        String lsSQL =    " SELECT "
                        + " sDeptIDxx"
                        + " , sDeptName "
                        + " , cRecdStat "
                        + "FROM ggc_isysdbf.department ";
        
        lsSQL = MiscUtil.addCondition(lsSQL, " sDeptName LIKE " + SQLUtil.toSQL(fsValue + "%")
                                               + " AND cRecdStat = '1'");

        System.out.println("SEARCH DEPARTMENT: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                "ID»Department",
                "sDeptIDxx»sDeptName",
                "sDeptIDxx»sDeptName",
                1);

        if (poJSON != null) {
            if(!"error".equals((String) poJSON.get("result"))){
                poMaster.setDeptID((String) poJSON.get("sDeptIDxx"));
                poMaster.setDeptName((String) poJSON.get("sDeptName"));
            }
        } else {
            poMaster.setDeptID("");
            poMaster.setDeptName("");
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
        
        return poJSON;
    }
    
    private String getSQ_Employee() {
        return " SELECT "
                + " c.sCompnyNm  "
                + " ,a.sEmployID "
                + " ,b.sDeptName "
                + " ,a.sDeptIDxx "
                + " ,e.sBranchNm "
                + " FROM GGC_ISysDBF.Employee_Master001 a	"
                + " LEFT JOIN GGC_ISysDBF.Department b ON  b.sDeptIDxx = a.sDeptIDxx "
                + " LEFT JOIN GGC_ISysDBF.Client_Master c on c.sClientID = a.sEmployID "
                + " LEFT JOIN GGC_ISysDBF.Branch_Others d ON d.sBranchCD = a.sBranchCd "
                + " LEFT JOIN GGC_ISysDBF.Branch e ON e.sBranchCd = a.sBranchCd "
                + " WHERE a.cRecdStat = '1' AND ISNULL(a.dFiredxxx) " 
                + " AND d.cDivision = (SELECT cDivision FROM GGC_ISysDBF.Branch_Others WHERE sBranchCd = " +  SQLUtil.toSQL(psBranchCd) + ")";
    }
    
    public JSONObject searchEmployee(String fsValue) {
        poJSON = new JSONObject();
        
        String lsSQL = getSQ_Employee() + " AND sDeptName LIKE " + SQLUtil.toSQL(fsValue + "%");

        System.out.println("SEARCH EMPLOYEE: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    "Employee Name»Department Name»Branch",
                    "sCompnyNm»sDeptName»sBranchNm",
                    "sCompnyNm»sDeptName»sBranchNm",
                1);

        if (poJSON != null) {
            if(!"error".equals((String) poJSON.get("result"))){
                poMaster.setEmployID((String) poJSON.get("sEmployID"));
                poMaster.setEmpInCharge((String) poJSON.get("sCompnyNm"));
            }
        } else {
            poMaster.setEmployID("");
            poMaster.setEmpInCharge("");
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
        
        return poJSON;
    }
    
    /**
    * Searches for a branch by name and retrieves branch details.
    *
    * This method performs a search for a branch by name and retrieves branch details such as location and branch name. It allows both UI and non-UI search modes and provides feedback if no records are found.
    *
    * @param fsValue The branch name or a search query.
    * @return True if the branch is successfully found and details are retrieved, otherwise false.
    */
    public JSONObject searchBranch(String fsValue) {
        poJSON = new JSONObject();
        
        String lsSQL =   " SELECT "
                + " IFNULL(a.sBranchCd, '') sBranchCd "
                + " , IFNULL(a.sBranchNm, '') sBranchNm "
                + " , IFNULL(b.cDivision, '') cDivision "
                + " FROM branch a "
                + " LEFT JOIN branch_others b ON a.sBranchCd = b.sBranchCd  "
                + " WHERE a.cRecdStat = '1'  "
                + " AND b.cDivision = (SELECT cDivision FROM branch_others WHERE sBranchCd = " + SQLUtil.toSQL(psBranchCd) + ")"
                + " AND sProvName LIKE " + SQLUtil.toSQL(fsValue + "%");

        System.out.println("SEARCH BRANCH: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    "Branch Code»Branch Name",
                    "sBranchCd»sBranchNm",
                    "sBranchCd»sBranchNm",
                1);

        if (poJSON != null) {
            if(!"error".equals((String) poJSON.get("result"))){
                poMaster.setLocation((String) poJSON.get("sBranchCd"));
                poMaster.setBranchNm((String) poJSON.get("sBranchNm"));
            }
        } else {
            poMaster.setLocation("");
            poMaster.setBranchNm("");
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
        
        return poJSON;
    }
    
    public JSONObject searchEventType(String fsValue) {
        poJSON = new JSONObject();
        
        String lsSQL =    " SELECT"
                        + " sActTypID "
                        + " ,sActTypDs "
                        + " ,sEventTyp "
                        + " ,cRecdStat "
                        + " FROM event_type "
                        + " WHERE sEventTyp LIKE " + SQLUtil.toSQL(fsValue + "%");

        System.out.println("SEARCH EVENT TYPE: " + lsSQL);
        poJSON = ShowDialogFX.Search(poGRider,
                lsSQL,
                fsValue,
                    "Event Type»Source",
                    "sEventTyp»sActTypDs",
                    "sEventTyp»sActTypDs",
                1);

        if (poJSON != null) {
            if(!"error".equals((String) poJSON.get("result"))){
                poMaster.setActTypID((String) poJSON.get("sActTypID"));
                poMaster.setEventTyp((String) poJSON.get("sEventTyp"));
            }
        } else {
            poMaster.setActTypID("");
            poMaster.setEventTyp("");
            poJSON = new JSONObject();
            poJSON.put("result", "error");
            poJSON.put("message", "No record loaded.");
            return poJSON;
        }
        
        return poJSON;
    }
}
