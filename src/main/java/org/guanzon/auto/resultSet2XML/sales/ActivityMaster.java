/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.resultSet2XML.sales;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;

/**
 *
 * @author Arsiela
 */
public class ActivityMaster {
    
    public static void main (String [] args){
        String path;
        if(System.getProperty("os.name").toLowerCase().contains("win")){
            path = "D:/GGC_Maven_Systems";
        }
        else{
            path = "/srv/GGC_Maven_Systems";
        }
        System.setProperty("sys.default.path.config", path);
        
        GRider instance = new GRider("gRider");

        if (!instance.logUser("gRider", "M001000001")){
            System.err.println(instance.getErrMsg());
            System.exit(1);
        }

        System.out.println("Connected");
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Activity_Master.xml");
        
        String lsSQL =    " SELECT "                                                                   
                        + "   a.sActvtyID "                                                            
                        + " , a.sActNoxxx "                                                            
                        + " , a.sActTitle "                                                            
                        + " , a.sActDescx "                                                            
                        + " , a.sActTypID "                                                            
                        + " , a.sActSrcex "                                                            
                        + " , a.dDateFrom "                                                            
                        + " , a.dDateThru "                                                            
                        + " , a.sProvIDxx "                                                            
                        + " , a.sLocation "                                                            
                        + " , a.sCompnynx "                                                            
                        + " , a.nPropBdgt "                                                            
                        + " , a.nRcvdBdgt "                                                            
                        + " , a.nTrgtClnt "                                                            
                        + " , a.sEmployID "                                                            
                        + " , a.sDeptIDxx "                                                            
                        + " , a.sLogRemrk "                                                            
                        + " , a.sRemarksx "                                                            
                        + " , a.cTranStat "                                                            
                        + " , a.sEntryByx "                                                            
                        + " , a.dEntryDte "                                                            
                        + " , a.sApproved "                                                            
                        + " , a.dApproved "                                                            
                        + " , a.sModified "                                                            
                        + " , a.dModified "                                                            
                        + " , b.sDeptName "                                                            
                        + " , d.sCompnyNm "                                                            
                        + " , e.sBranchNm "                                                            
                        + " , f.sProvName "                                                            
                        + " , g.sEventTyp "                                                            
                        + " FROM activity_master a "                                                   
                        + " LEFT JOIN GGC_ISysDBF.Department b ON b.sDeptIDxx = a.sDeptIDxx "          
                        + " LEFT JOIN GGC_ISysDBF.Employee_Master001 c ON c.sEmployID = a.sEmployID "  
                        + " LEFT JOIN GGC_ISysDBF.Client_Master d ON d.sClientID = a.sEmployID "       
                        + " LEFT JOIN branch e ON e.sBranchCd = a.sLocation "                          
                        + " LEFT JOIN province f ON f.sProvIDxx = a.sProvIDxx "                        
                        + " LEFT JOIN event_type g ON g.sActTypID = a.sActTypID "     
                        + " WHERE 0=1 ";
        
        //System.out.println(lsSQL);
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "activity_master", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
