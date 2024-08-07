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
public class InquiryRequirements {
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Inquiry_Requirements.xml");
        
        
        String lsSQL =    " SELECT "                                                            
                        + "    a.sTransNox "                                                    
                        + "  , a.nEntryNox "                                                    
                        + "  , a.sRqrmtCde "                                                    
                        + "  , a.cRequired "                                                    
                        + "  , a.cSubmittd "                                                    
                        + "  , a.sReceived "                                                    
                        + "  , a.dReceived "                                                    
                        + "  , b.sDescript "                                                    
                        + "  , d.cPayModex "                                                    
                        + "  , d.cCustGrpx "                                                    
                        + "  , e.sCompnyNm "                                                    
                        + " FROM customer_inquiry_requirements a "                              
                        + " LEFT JOIN requirement_source b ON a.sRqrmtCde = b.sRqrmtCde "       
                        + " LEFT JOIN customer_inquiry d ON d.sTransNox = a.sTransNox   "       
                        + " LEFT JOIN GGC_ISysDBF.Client_Master e ON e.sClientID = a.sReceived " 
                        + " WHERE 0=1";
        
        
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "customer_inquiry_requirements", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
}
