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
public class VehicleDeliveryReceiptMaster {
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_VehicleDeliveryReceipt_Master.xml");
        
        
        String lsSQL =    " SELECT "                                                                                       
                        + "    a.sTransNox "                                                                              
                        + "  , a.dTransact "                                                                              
                        + "  , a.cCustType "                                                                              
                        + "  , a.sClientID "                                                                              
                        + "  , a.sSerialID "                                                                              
                        + "  , a.sReferNox "                                                                              
                        + "  , a.sRemarksx "                                                                              
                        + "  , a.nGrossAmt "                                                                              
                        + "  , a.nDiscount "                                                                              
                        + "  , a.nTranTotl "                                                                              
                        + "  , a.sPONoxxxx "                                                                              
                        + "  , a.sSourceCd "                                                                              
                        + "  , a.sSourceNo "                                                                              
                        + "  , a.cPrintedx "                                                                              
                        + "  , a.sPrepared "                                                                              
                        + "  , a.sApproved "                                                                              
                        + "  , a.cCallStat "                                                                              
                        + "  , a.cTranStat "                                                                              
                        + "  , a.sEntryByx "                                                                              
                        + "  , a.dEntryDte "                                                                              
                        + "  , a.sModified "                                                                              
                        + "  , a.dModified "                                                       
                        + "  , CASE "          
                        + " 	WHEN a.cTranStat = "+ SQLUtil.toSQL(TransactionStatus.STATE_CANCELLED)+" THEN 'CANCELLED' "     
                        + " 	WHEN a.cTranStat = "+ SQLUtil.toSQL(TransactionStatus.STATE_CLOSED)+" THEN 'APPROVED' "        
                        + " 	WHEN a.cTranStat = "+ SQLUtil.toSQL(TransactionStatus.STATE_OPEN)+" THEN 'ACTIVE' "          
                        + " 	WHEN a.cTranStat = "+ SQLUtil.toSQL(TransactionStatus.STATE_POSTED)+" THEN 'POSTED' "                             
                        + " 	ELSE 'ACTIVE'  "                                                          
                        + "    END AS sTranStat "                                                                                
                        /*BUYING COSTUMER*/                                                                               
                        + "  , b.sCompnyNm AS sBuyCltNm "                                                                 
                        + "  , b.cClientTp "                                                                              
                        + "  , TRIM(IFNULL(CONCAT( IFNULL(CONCAT(d.sHouseNox,' ') , ''), "                                
                        + "     IFNULL(CONCAT(d.sAddressx,' ') , ''),                    "                                
                        + "     IFNULL(CONCAT(e.sBrgyName,' '), ''),                     "                                
                        + "     IFNULL(CONCAT(f.sTownName, ', '),''),                    "                                
                        + "     IFNULL(CONCAT(g.sProvName),'') )	, '')) AS sAddressx    "                                
                        /*VSP*/                                                                                           
                        + "  , h.sTransNox AS sVSPTrans"                                                                              
                        + "  , DATE(h.dTransact) AS dVSPDatex "                                                                              
                        + "  , h.sVSPNOxxx "                                                                             
                        + "  , h.cIsVhclNw "                                                                             
                        + "  , DATE(h.dDelvryDt) AS dDelvryDt "                                                                              
                        + "  , h.sInqryIDx "                                                                               
                        + "  , h.sBranchCD "                                                                               
                        + "  , h.cPayModex "                                            
                        + " ,  q.sCompnyNm AS sSENamexx "                                                                               
                        /*CO-CLIENT*/                                                                                     
                        + "  , i.sCompnyNm AS sCoCltNmx "                                                                 
                        /*VEHICLE INFORMATION*/                                                                           
                        + "  , j.sCSNoxxxx "                                                                              
                        + "  , k.sPlateNox "                                                                              
                        + "  , j.sFrameNox "                                                                              
                        + "  , j.sEngineNo "                                                                              
                        + "  , j.sKeyNoxxx "                                                                              
                        + "  , l.sDescript AS sVhclFDsc "   
                        + "  ,  TRIM(CONCAT_WS(' ',la.sMakeDesc, lb.sModelDsc, lc.sTypeDesc, l.sTransMsn, l.nYearModl )) AS sVhclDesc "
                        + "  ,ld.sColorDsc "                                                              
                        /*BRANCH*/                                                                                        
                        + "  , m.sBranchNm    "
                        /*VSI*/
                        + " , o.sTransNox AS sSITransx" 
                        + " , o.sReferNox AS sSINoxxxx "                                                                          
                        + "  FROM udr_master a "                                                                          
                         /*BUYING CUSTOMER*/                                                                              
                        + "  LEFT JOIN client_master b ON b.sClientID = a.sClientID "                                     
                        + "  LEFT JOIN client_address c ON c.sClientID = a.sClientID AND c.cPrimaryx = 1 "                
                        + "  LEFT JOIN addresses d ON d.sAddrssID = c.sAddrssID "                                         
                        + "  LEFT JOIN barangay e ON e.sBrgyIDxx = d.sBrgyIDxx  "                                         
                        + "  LEFT JOIN towncity f ON f.sTownIDxx = d.sTownIDxx  "                                         
                        + "  LEFT JOIN province g ON g.sProvIDxx = f.sProvIDxx  "                                         
                        + "  LEFT JOIN client_mobile ba ON ba.sClientID = b.sClientID AND ba.cPrimaryx = 1 "              
                        + "  LEFT JOIN client_email_address bb ON bb.sClientID = b.sClientID AND bb.cPrimaryx = 1 "       
                        /*VSP*/                                                                                           
                        + "  LEFT JOIN vsp_master h ON h.sTransNox = a.sSourceNo "                                       
                        /*CO CLIENT*/                                                                                     
                        + "  LEFT JOIN client_master i ON i.sClientID = h.sCoCltIDx "                                     
                        /*VEHICLE INFORMATION*/                                                                           
                        + "  LEFT JOIN vehicle_serial j ON j.sSerialID = a.sSerialID "                                    
                        + "  LEFT JOIN vehicle_serial_registration k ON k.sSerialID = a.sSerialID "                       
                        + "  LEFT JOIN vehicle_master l ON l.sVhclIDxx = j.sVhclIDxx "          
                        + "  LEFT JOIN vehicle_make la ON la.sMakeIDxx = l.sMakeIDxx  "
                        + "  LEFT JOIN vehicle_model lb ON lb.sModelIDx = l.sModelIDx "
                        + "  LEFT JOIN vehicle_type lc ON lc.sTypeIDxx = l.sTypeIDxx  "
                        + "  LEFT JOIN vehicle_color ld ON ld.sColorIDx = l.sColorIDx "                                    
                        /*BRANCH*/                                                                                        
                        + "  LEFT JOIN branch m ON m.sBranchCd = h.sBranchCD "
                        /*VSI*/
                        + "  LEFT JOIN si_master_source n on n.sReferNox = a.sTransNox "
                        + "  LEFT JOIN si_master o ON o.sTransNox = n.sTransNox AND o.cTranStat <> " + SQLUtil.toSQL(TransactionStatus.STATE_CANCELLED) 
                        /*INQUIRY*/                
                        + "  LEFT JOIN customer_inquiry p ON p.sTransNox = h.sInqryIDx "    
                        + "  LEFT JOIN ggc_isysdbf.client_master q ON q.sClientID = p.sEmployID "
                        + "  WHERE 0=1";
        
        
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "udr_master", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
}
