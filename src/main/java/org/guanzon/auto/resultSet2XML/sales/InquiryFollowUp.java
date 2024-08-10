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
public class InquiryFollowUp {
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Inquiry_FollowUp.xml");
        
        
        String lsSQL =    " SELECT "                          
                        + "    sTransNox "                    
                        + "  , sReferNox "                    
                        + "  , dTransact "                    
                        + "  , sRemarksx "                    
                        + "  , sMessagex "                    
                        + "  , sMethodCd "                    
                        + "  , sSclMedia "                    
                        + "  , dFollowUp "                    
                        + "  , tFollowUp "                    
                        + "  , sGdsCmptr "                    
                        + "  , sMkeCmptr "                    
                        + "  , sDlrCmptr "                    
                        + "  , sRspnseCd "                    
                        + "  , sEmployID "                    
                        + "  , sEntryByx "                    
                        + "  , dEntryDte "                    
                        + " FROM customer_inquiry_followup "  
                        + " WHERE 0=1";
        
        
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "customer_inquiry_followup", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
