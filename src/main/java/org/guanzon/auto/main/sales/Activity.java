/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.main.sales;

import java.util.ArrayList;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.constant.EditMode;
import org.guanzon.appdriver.iface.GTransaction;
import org.guanzon.auto.controller.sales.Activity_Master;
import org.guanzon.auto.controller.sales.Activity_Member;
import org.guanzon.auto.controller.sales.Activity_Town;
import org.guanzon.auto.controller.sales.Activity_Vehicle;
import org.json.simple.JSONObject;

/**
 *
 * @author Arsiela
 */
public class Activity implements GTransaction{
    final String XML = "Model_Activity_Master.xml";
    GRider poGRider;
    String psBranchCd;
    boolean pbWtParent;
    int pnEditMode;
    String psTransStat;
    String psMessagex;
    public JSONObject poJSON;
    
    Activity_Master poController;
    Activity_Town poActTown;
    Activity_Member poActMember;
    Activity_Vehicle poActVehicle;
    
    public Activity(GRider foAppDrver, boolean fbWtParent, String fsBranchCd){
        poController = new Activity_Master(foAppDrver,fbWtParent,fsBranchCd);
        poActTown = new Activity_Town(foAppDrver);
        poActMember = new Activity_Member(foAppDrver);
        poActVehicle = new Activity_Vehicle(foAppDrver);
        
        poGRider = foAppDrver;
        pbWtParent = fbWtParent;
        psBranchCd = fsBranchCd.isEmpty() ? foAppDrver.getBranchCode() : fsBranchCd;
    }

    @Override
    public int getEditMode() {
        pnEditMode = poController.getEditMode();
        return pnEditMode;
    }

    @Override
    public void setTransactionStatus(String string) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public JSONObject setMaster(int fnCol, Object foData) {
        return poController.setMaster(fnCol, foData);
    }

    @Override
    public JSONObject setMaster(String fsCol, Object foData) {
        return poController.setMaster(fsCol, foData);
    }

    public Object getMaster(int fnCol) {
        if(pnEditMode == EditMode.UNKNOWN)
            return null;
        else 
            return poController.getMaster(fnCol);
    }

    public Object getMaster(String fsCol) {
        return poController.getMaster(fsCol);
    }
    
    @Override
    public JSONObject newTransaction() {
        poJSON = new JSONObject();
        try{
            poJSON = poController.newTransaction();
            
            if("success".equals(poJSON.get("result"))){
                pnEditMode = poController.getEditMode();
            } else {
                pnEditMode = EditMode.UNKNOWN;
            }
               
        }catch(NullPointerException e){
            poJSON.put("result", "error");
            poJSON.put("message", e.getMessage());
            pnEditMode = EditMode.UNKNOWN;
        }
        return poJSON;
    }

    @Override
    public JSONObject openTransaction(String fsValue) {
        poJSON = new JSONObject();
        
        poJSON = poController.openTransaction(fsValue);
        if("success".equals(poJSON.get("result"))){
            pnEditMode = poController.getEditMode();
        } else {
            pnEditMode = EditMode.UNKNOWN;
        }
        
        poJSON = poActTown.openDetail(fsValue);
        if(!"success".equals(poJSON.get("result"))){
            pnEditMode = EditMode.UNKNOWN;
        }
        
        poJSON = poActMember.openDetail(fsValue);
        if(!"success".equals(poJSON.get("result"))){
            pnEditMode = EditMode.UNKNOWN;
        }
        
        poJSON = checkData(poActVehicle.openDetail(fsValue));
        
        return poJSON;
    }

    @Override
    public JSONObject updateTransaction() {
        poJSON = new JSONObject();  
        poJSON = poController.updateTransaction();
        pnEditMode = poController.getEditMode();
        return poJSON;
    }

    @Override
    public JSONObject saveTransaction() {
        
        poJSON = new JSONObject();  
        
//        poJSON = validateEntry();
//        if("error".equalsIgnoreCase((String)poJSON.get("result"))){
//            return poJSON;
//        }
        
        poJSON =  poController.saveTransaction();
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        if (!pbWtParent) poGRider.beginTrans();
        
        poJSON =  poActTown.saveDetail((String) poController.getModel().getActvtyID());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        poJSON =  poActMember.saveDetail((String) poController.getModel().getActvtyID());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
      
        poJSON =  poActVehicle.saveDetail((String) poController.getModel().getActvtyID());
        if("error".equalsIgnoreCase((String)checkData(poJSON).get("result"))){
            if (!pbWtParent) poGRider.rollbackTrans();
            return checkData(poJSON);
        }
        
        if (!pbWtParent) poGRider.commitTrans();
        
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
        poJSON =  poController.cancelTransaction(fsValue);
        return poJSON;
    }
    
    public JSONObject searchTransaction(String fsValue, boolean fbByCode) {
        poJSON = new JSONObject();  
        poJSON = poController.searchTransaction(fsValue, fbByCode);
        if(!"error".equals(poJSON.get("result"))){
            poJSON = openTransaction((String) poJSON.get("sActvtyID"));
        }
        return poJSON;
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
    public Activity_Master getMasterModel() {
        return poController;
    }
    
    private JSONObject checkData(JSONObject joValue){
        if(pnEditMode == EditMode.ADDNEW ||pnEditMode == EditMode.READY || pnEditMode == EditMode.UPDATE){
            if(joValue.containsKey("continue")){
                if(true == (boolean)joValue.get("continue")){
                    joValue.put("result", "success");
                    joValue.put("message", "Record saved successfully.");
                }
            }
        }
        return joValue;
    }
    
    public ArrayList getActTownList(){return poActTown.getDetailList();}
    public void setActTownList(ArrayList foObj){this.poActTown.setDetailList(foObj);}
    
    public ArrayList getActMemberList(){return poActMember.getDetailList();}
    public void setActMemberList(ArrayList foObj){this.poActMember.setDetailList(foObj);}
    
    public ArrayList getActVehicleList(){return poActVehicle.getDetailList();}
    public void setActVehicleList(ArrayList foObj){this.poActVehicle.setDetailList(foObj);}
    
    public void setActTown(int fnRow, int fnIndex, Object foValue){ poActTown.setDetail(fnRow, fnIndex, foValue);}
    public void setActTown(int fnRow, String fsIndex, Object foValue){ poActTown.setDetail(fnRow, fsIndex, foValue);}
    public Object getActTown(int fnRow, int fnIndex){return poActTown.getDetail(fnRow, fnIndex);}
    public Object getActTown(int fnRow, String fsIndex){return poActTown.getDetail(fnRow, fsIndex);}
    
    public Object addActTown(){ return poActTown.addDetail(poController.getModel().getActvtyID());}
    public Object removeActTown(int fnRow){ return poActTown.removeDetail(fnRow);}
    
    public void setActMember(int fnRow, int fnIndex, Object foValue){ poActMember.setDetail(fnRow, fnIndex, foValue);}
    public void setActMember(int fnRow, String fsIndex, Object foValue){ poActMember.setDetail(fnRow, fsIndex, foValue);}
    public Object getActMember(int fnRow, int fnIndex){return poActMember.getDetail(fnRow, fnIndex);}
    public Object getActMember(int fnRow, String fsIndex){return poActMember.getDetail(fnRow, fsIndex);}
    
    public Object addActMember(){ return poActMember.addDetail(poController.getModel().getActvtyID());}
    public Object removeActMember(int fnRow){ return poActMember.removeDetail(fnRow);}
    
    public void setActVehicle(int fnRow, int fnIndex, Object foValue){ poActVehicle.setDetail(fnRow, fnIndex, foValue);}
    public void setActVehicle(int fnRow, String fsIndex, Object foValue){ poActVehicle.setDetail(fnRow, fsIndex, foValue);}
    public Object getActVehicle(int fnRow, int fnIndex){return poActVehicle.getDetail(fnRow, fnIndex);}
    public Object getActVehicle(int fnRow, String fsIndex){return poActVehicle.getDetail(fnRow, fsIndex);}
    
    public Object addActVehicle(){ return poActVehicle.addDetail(poController.getModel().getActvtyID());}
    public Object removeActVehicle(int fnRow){ return poActVehicle.removeDetail(fnRow);}
    
    
     /**
     *
     * Searches for a department based on the specified value.
     * @param fsValue the value used for searching the department
     * @return {@code true} if the department is found, {@code false} otherwise
     */
    public JSONObject searchDepartment(String fsValue) {
        return poController.searchDepartment(fsValue);
    }

    /**
     *
     * Searches for an employee based on the specified value.
     * @param fsValue the value used for searching the employee
     * @return {@code true} if the employee is found, {@code false} otherwise
     */
    
    public JSONObject searchEmployee(String fsValue) {
        return poController.searchEmployee(fsValue);
    }
    
    
    /**
     * Searches for a province based on the provided value.
     *
     * @param fsValue the value used to search for a province
     * @return true if the province is found and set as the master record, false
     */
    public JSONObject searchProvince(String fsValue) {
        return poController.searchProvince(fsValue);
        
    }
    
    
    /**
     * Searches for an event type based on the provided value.
     *
     * @param fsValue The value to search for in the event type.
     * @return {@code true} if a matching event type is found, {@code false}
     */
    public JSONObject searchEventType(String fsValue) {
        return poController.searchEventType(fsValue);
        
    }
    
}
