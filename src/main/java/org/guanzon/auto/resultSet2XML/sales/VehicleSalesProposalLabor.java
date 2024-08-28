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
public class VehicleSalesProposalLabor {
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_VehicleSalesProposal_Labor.xml");
        
        
        String lsSQL =    " SELECT "                                                                                                           
                        + "   a.sTransNox "                                                                                                    
                        + " , a.nEntryNox "                                                                                                    
                        + " , a.sLaborCde "                                                                                                    
                        + " , a.nLaborAmt "                                                                                                    
                        + " , a.sChrgeTyp "                                                                                                    
                        + " , a.sRemarksx "                                                                                                    
                        + " , a.sLaborDsc "                                                                                                    
                        + " , a.nLaborDsc "                                                                                                    
                        + " , a.nNtLabAmt "                                                                                                   
                        + " , a.cAddtlxxx "                                                                                                    
                        + " , a.dAddDatex "                                                                                                    
                        + " , a.sAddByxxx "                                                                                                    
                        + " , c.sDSNoxxxx "                                                                                                    
                        + " , c.dTransact "                                                                                                    
                        + " , d.sCompnyNm "                                                                                                    
                        + " FROM vsp_labor a "                                                                                                 
                        + " LEFT JOIN diagnostic_labor b ON b.sLaborCde = a.sLaborCde "                                                        
                        + " LEFT JOIN diagnostic_master c ON c.sTransNox = b.sTransNox and c.sSourceCD = a.sTransNox AND c.cTranStat = '1' "   
                        + " LEFT JOIN GGC_ISysDBF.client_master d ON d.sClientID = a.sAddByxxx "
                        + " WHERE 0=1";
        
        
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "vsp_labor", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
}
