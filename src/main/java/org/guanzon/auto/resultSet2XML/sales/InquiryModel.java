/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.guanzon.auto.resultSet2XML.sales;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.xml.crypto.KeySelector;
import org.guanzon.appdriver.base.GRider;
import org.guanzon.appdriver.base.MiscUtil;

/**
 *
 * @author Arsiela
 */
public class InquiryModel {
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Inquiry_Master.xml");
        
        
        String lsSQL =     " SELECT "                                                                               
                        + "   a.sTransNox "                                                                            
                        + " , a.sInqryIDx "                                                                       
                        + " , a.sBranchCd "                                                                        
                        + " , a.dTransact "                                                                        
                        + " , a.sEmployID "                                                                        
                        + " , a.cIsVhclNw "                                                                        
                        + " , a.sVhclIDxx "                                                                        
                        + " , a.sClientID "                                                                         
                        + " , a.sContctID "                                                                        
                        + " , a.sRemarksx "                                                                        
                        + " , a.sAgentIDx "                                                                        
                        + " , a.dTargetDt "                                                                        
                        + " , a.cIntrstLv "                                                                        
                        + " , a.sSourceCD "                                                                        
                        + " , a.sSourceNo "                                                                        
                        + " , a.sTestModl "                                                                        
                        + " , a.sActvtyID "                                                                        
                        + " , a.dLastUpdt "                                                                           
                        + " , a.sLockedBy "                                                                        
                        + " , a.dLockedDt "                                                                        
                        + " , a.sApproved "                                                                        
                        + " , a.sSerialID "                                                                        
                        + " , a.sInqryCde "                                                                        
                        + " , a.cTranStat "                                                                        
                        + " , a.cPayModex "                                                                        
                        + " , a.cCustGrpx "                                                                        
                        + " , a.sEntryByx "                                                                        
                        + " , a.dEntryDte "                                                                        
                        + " , a.sModified "                                                                        
                        + " , a.dModified "                                                                        
                        + " , b.sCompnyNm AS sClientNm"                                                                        
                        + " , b.cClientTp "                                                                        
                        + " , IFNULL(CONCAT( IFNULL(CONCAT(d.sHouseNox,' ') , ''), "                               
                        + "	IFNULL(CONCAT(d.sAddressx,' ') , ''),  "                                               
                        + "	IFNULL(CONCAT(e.sBrgyName,' '), ''),   "                                               
                        + "	IFNULL(CONCAT(f.sTownName, ', '),''),  "                                               
                        + "	IFNULL(CONCAT(g.sProvName),'') )	, '') AS sAddressx  "                                
                        + " , h.sMobileNo "                                                                        
                        + " , i.sEmailAdd "                                                                        
                        + " , j.sAccountx "                                                                        
                        + " , k.sCompnyNm AS sContctNm "                                                           
                        + " , l.sCompnyNm AS sSalesExe "                                                           
                        + " , m.sCompnyNm AS sSalesAgn "                                                           
                        + " , n.sPlatform "                                                                        
                        + " , o.sActTitle "                                                                        
                        + " , p.sBranchNm "                                                                                      
                        + " , q.sFrameNox "                                                                                           
                        + " , q.sEngineNo "                                                                                                        
                        + " , q.sCSNoxxxx "                                                                                   
                        + " , r.sPlateNox "                                                                                            
                        + " , s.sDescript "                                                                      
                        + " FROM customer_inquiry a "                                                              
                        + " LEFT JOIN client_master b ON a.sClientID = b.sClientID   "                             
                        + " LEFT JOIN client_address c ON c.sClientID = a.sClientID AND c.cPrimaryx = 1 "          
                        + " LEFT JOIN addresses d ON d.sAddrssID = c.sAddrssID  "                                  
                        + " LEFT JOIN barangay e ON e.sBrgyIDxx = d.sBrgyIDxx   "                                  
                        + " LEFT JOIN towncity f ON f.sTownIDxx = d.sTownIDxx   "                                  
                        + " LEFT JOIN province g ON g.sProvIDxx = f.sProvIDxx   "                                  
                        + " LEFT JOIN client_mobile h ON h.sClientID = a.sClientID AND h.cPrimaryx = 1  "          
                        + " LEFT JOIN client_email_address i ON i.sClientID = a.sClientID AND h.cPrimaryx = 1  "   
                        + " LEFT JOIN client_social_media j ON j.sClientID = a.sClientID AND j.cRecdStat = 1   "   
                        + " LEFT JOIN client_master k ON k.sClientID = a.sContctID   "                             
                        + " LEFT JOIN ggc_isysdbf.client_master l ON l.sClientID = a.sEmployID "                   
                        + " LEFT JOIN client_master m ON m.sClientID = a.sAgentIDx    "                            
                        + " LEFT JOIN online_platforms n ON n.sTransNox = a.sSourceNo "                            
                        + " LEFT JOIN activity_master o ON o.sActvtyID = a.sActvtyID  "                            
                        + " LEFT JOIN branch p ON p.sBranchCd = a.sBranchCd           "                             
                        + " LEFT JOIN vehicle_serial q ON q.sSerialID = a.sSerialID           "    
                        + " LEFT JOIN vehicle_serial_registration r ON r.sSerialID = a.sSerialID "              
                        + " LEFT JOIN vehicle_master s ON s.sVhclIDxx = q.sVhclIDxx "   
                        + " WHERE 0=1";
        
        
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "customer_inquiry", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
