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
public class ActivityMember {
    
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Activity_Member.xml");
        
        String lsSQL =    "   SELECT "                                                                 
                        + "   a.sTransNox "                                                            
                        + " , a.nEntryNox "                                                            
                        + " , a.sEmployID "                                                            
                        + " , a.cOriginal "                                                            
                        + " , a.sEntryByx "                                                            
                        + " , a.dEntryDte "                                                            
                        + " , c.sCompnyNm "                                                            
                        + " , d.sDeptName "                                                            
                        + "FROM activity_member a "                                                    
                        + "LEFT JOIN GGC_ISysDBF.Employee_Master001 b ON b.sEmployID = a.sEmployID "   
                        + "LEFT JOIN GGC_ISysDBF.Client_Master c ON c.sClientID = a.sEmployID "        
                        + "LEFT JOIN GGC_ISysDBF.Department d on d.sDeptIDxx = b.sDeptIDxx " 
                        + " WHERE 0=1 ";
        
        //System.out.println(lsSQL);
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "activity_member", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
