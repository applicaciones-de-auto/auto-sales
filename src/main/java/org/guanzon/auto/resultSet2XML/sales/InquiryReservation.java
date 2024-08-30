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
public class InquiryReservation {
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Inquiry_Reservation.xml");
        
        
        String lsSQL =    " SELECT "                                                                      
                        + "    a.sTransNox "                                                              
                        + "  , a.dTransact "                                                              
                        + "  , a.sReferNox "                                                              
                        + "  , a.sClientID "                                                              
                        + "  , a.nAmountxx "                                                              
                        + "  , a.sRemarksx "                                                              
                        + "  , a.sSourceCD "                                                              
                        + "  , a.sSourceNo "                                                              
                        + "  , a.nPrintedx "                                                              
                        + "  , a.sResrvCde "                                                              
                        + "  , a.cResrvTyp "                                                             
                        + "  , a.sTransIDx "    //where the reservation has been linked                                                           
                        + "  , a.cTranStat "                                                              
                        + "  , a.sApproved "                                                              
                        + "  , a.dApproved "                                                              
                        + "  , a.sEntryByx "                                                              
                        + "  , a.dEntryDte "                                                              
                        + "  , a.sModified "                                                              
                        + "  , a.dModified "                                                              
                        + "  , b.sCompnyNm "                                                              
                        + "  , b.cClientTp "                                                              
                        + "  , IFNULL(CONCAT( IFNULL(CONCAT(d.sHouseNox,' ') , ''), "                     
                        + "  IFNULL(CONCAT(d.sAddressx,' ') , ''), "                                      
                        + "  IFNULL(CONCAT(e.sBrgyName,' '), ''),  "                                      
                        + "  IFNULL(CONCAT(f.sTownName, ', '),''), "                                      
                        + "  IFNULL(CONCAT(g.sProvName),'') )	, '') AS sAddressx  "
                        + "  , i.sReferNox  AS sSINoxxxx " 
                        + "  , DATE(i.dTransact) AS dSIDatexx "     
                        + "  , h.nTranAmtx "                                    
                        + " FROM customer_inquiry_reservation a    "                                      
                        + " LEFT JOIN client_master b ON b.sClientID = a.sClientID "                      
                        + " LEFT JOIN client_address c ON c.sClientID = a.sClientID AND c.cPrimaryx = 1 " 
                        + " LEFT JOIN addresses d ON d.sAddrssID = c.sAddrssID "                          
                        + " LEFT JOIN barangay e ON e.sBrgyIDxx = d.sBrgyIDxx  "                          
                        + " LEFT JOIN towncity f ON f.sTownIDxx = d.sTownIDxx  "                          
                        + " LEFT JOIN province g ON g.sProvIDxx = f.sProvIDxx  "
                        + " LEFT JOIN si_master_source h ON h.sReferNox = a.sTransNox " 
                        + " LEFT JOIN si_master i ON i.sTransNox = h.sTransNox  "
                        + " WHERE 0=1";
        
        
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "customer_inquiry_reservation", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
