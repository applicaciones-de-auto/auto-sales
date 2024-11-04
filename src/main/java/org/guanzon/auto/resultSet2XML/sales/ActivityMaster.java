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
import org.guanzon.appdriver.base.SQLUtil;
import org.guanzon.appdriver.constant.TransactionStatus;

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
        
        String lsSQL =   " SELECT "                                                                   
                        + "   a.sActvtyID "                                                            
                        + " , a.sActNoxxx "                                                            
                        + " , a.sActTitle "                                                            
                        + " , a.sActDescx "                                                            
                        + " , a.sActTypID "                                                            
                        + " , a.sActSrcex "                                                            
                        + " , a.dDateFrom "                                                            
                        + " , a.dDateThru "                                                             
                        + " , a.sLocation "                                                              
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
        //                + " , a.sApproved "                                                            
        //                + " , a.dApproved "                                                            
                        + " , a.sModified "                                                            
                        + " , a.dModified "                                                            
                        + " , b.sDeptName "                                                            
                        + " , d.sCompnyNm "                                                            
                        + " , e.sBranchNm "                                                              
                        + " , f.sEventTyp "                                                              
                        + " , f.sActTypDs "                                                                                     
                        + " , DATE(g.dApproved) AS dApprovex "                                                                           
                        + " , h.sCompnyNm AS sApprover "                                                       
                        + " FROM activity_master a "                                                   
                        + " LEFT JOIN GGC_ISysDBF.Department b ON b.sDeptIDxx = a.sDeptIDxx "          
                        + " LEFT JOIN GGC_ISysDBF.Employee_Master001 c ON c.sEmployID = a.sEmployID "  
                        + " LEFT JOIN GGC_ISysDBF.Client_Master d ON d.sClientID = a.sEmployID "       
                        + " LEFT JOIN branch e ON e.sBranchCd = a.sLocation "                           
                        + " LEFT JOIN event_type f ON f.sActTypID = a.sActTypID "
                        + " LEFT JOIN transaction_status_history g ON g.sSourceNo = a.sActvtyID  AND g.cRefrStat = "+ SQLUtil.toSQL(TransactionStatus.STATE_CLOSED) + " AND g.cTranStat <> "+ SQLUtil.toSQL(TransactionStatus.STATE_CANCELLED)
                        + " LEFT JOIN ggc_isysdbf.client_master h ON h.sClientID = g.sApproved "
                        + " WHERE 0=1 ";
        
        System.out.println(lsSQL);
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
