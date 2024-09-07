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
public class VehicleSalesProposalParts {
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_VehicleSalesProposal_Parts.xml");
        
        
        String lsSQL =    " SELECT "                                                                                                         
                        + "   a.sTransNox "                                                                                                  
                        + " , a.nEntryNox "                                                                                                  
                        + " , a.sStockIDx "                                                                                                  
                        + " , a.nUnitPrce "                                                                                                  
                        + " , a.nSelPrice "                                                                                                  
                        + " , a.nQuantity "                                                                                                  
                        + " , a.nReleased "                                                                                                  
                        + " , a.sChrgeTyp "                                                                                                  
                        + " , a.sDescript "                                                                                                   
                        + " , a.nPartsDsc "                                                                                                    
                        + " , a.nNtPrtAmt "                                                                                               
                        + " , a.sPartStat "                                                                                                  
                        + " , a.dAddDatex "                                                                                                  
                        + " , a.sAddByxxx "                                                                                                   
                        + " , b.sBarCodex "                                                                                                   
                        + " , b.sDescript AS sPartDesc "                                                                                               
                        + " , d.sDSNoxxxx "                                                                                                  
                        + " , d.dTransact "                                                                                                  
                        + " , e.sCompnyNm "                                                                                                   
                        + " FROM vsp_parts a "                                                                                               
                        + " LEFT JOIN inventory b ON b.sStockIDx = a.sStockIDx "                                                             
                        + " LEFT JOIN diagnostic_parts c ON c.sStockIDx = a.sStockIDx "                                                      
                        + " LEFT JOIN diagnostic_master d ON d.sTransNox = c.sTransNox AND d.sSourceCD = a.sTransNox AND d.cTranStat = '1' " 
                        + " LEFT JOIN GGC_ISysDBF.client_master e ON e.sClientID = a.sAddByxxx "
                        + " WHERE 0=1";
        
        
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "vsp_parts", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
}
