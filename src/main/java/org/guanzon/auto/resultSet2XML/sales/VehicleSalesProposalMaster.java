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
public class VehicleSalesProposalMaster {
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_VehicleSalesProposal_Master.xml");
        
        
        String lsSQL =    " SELECT DISTINCT "                                                                      
                        + "   a.sTransNox "                                                               
                        + " , a.dTransact "                                                               
                        + " , a.sVSPNOxxx "                                                               
                        + " , a.dDelvryDt "                                                               
                        + " , a.sInqryIDx "                                                               
                        + " , a.sClientID "                                                               
                        + " , a.sCoCltIDx "                                                               
                        + " , a.sSerialID "                                                               
                        + " , a.nUnitPrce "                                                               
                        + " , a.sRemarksx "                                                               
                        + " , a.nAdvDwPmt "                                                               
                        + " , a.sOthrDesc "                                                               
                        + " , a.nOthrChrg "                                                               
                        + " , a.nLaborAmt "                                                               
                        + " , a.nAccesAmt "                                                               
                        + " , a.nInsurAmt "                                                               
                        + " , a.nTPLAmtxx "                                                               
                        + " , a.nCompAmtx "                                                               
                        + " , a.nLTOAmtxx "                                                               
                        + " , a.nChmoAmtx "                                                               
                        + " , a.sChmoStat "                                                               
                        + " , a.sTPLStatx "                                                               
                        + " , a.sCompStat "                                                               
                        + " , a.sLTOStatx "                                                               
                        + " , a.sInsurTyp "                                                               
                        + " , a.nInsurYrx "                                                               
                        + " , a.sInsTplCd "                                                               
                        + " , a.sInsCodex "                                                                
                        + " , a.nToLabDsc "                                                               
                        + " , a.nToPrtDsc "                                                              
                        + " , a.nPromoDsc "                                                               
                        + " , a.nFleetDsc "                                                               
                        + " , a.nSPFltDsc "                                                               
                        + " , a.nBndleDsc "                                                               
                        + " , a.nAddlDscx "                                                               
                        + " , a.nDealrInc "                                                               
                        + " , a.cPayModex "                                                               
                        + " , a.sBnkAppCD "                                                               
                        + " , a.nTranTotl "                                                               
                        + " , a.nResrvFee "                                                               
                        + " , a.nDownPaym "                                                               
                        + " , a.nNetTTotl "                                                               
                        + " , a.nAmtPaidx "                                                               
                        + " , a.nFrgtChrg "                                                               
                        + " , a.nDue2Supx "                                                               
                        + " , a.nDue2Dlrx "                                                               
                        + " , a.nSPFD2Sup "                                                               
                        + " , a.nSPFD2Dlr "                                                               
                        + " , a.nPrmD2Sup "                                                               
                        + " , a.nPrmD2Dlr "                                                               
                        + " , a.sEndPlate "                                                               
                        + " , a.sBranchCD "                                                               
                        + " , a.nDealrRte "                                                               
                        + " , a.nDealrAmt "                                                               
                        + " , a.nSlsInRte "                                                               
                        + " , a.nSlsInAmt "                                                               
                        + " , a.cIsVhclNw "                                                               
                        + " , a.cIsVIPxxx "                                                               
                        + " , a.sDcStatCd "                                                               
                        + " , a.dDcStatDt "                                                               
                        + " , a.cPrintedx "                                                               
                        + " , a.sLockedBy "                                                               
                        + " , a.dLockedDt "                                                               
                        + " , a.cTranStat "                                                               
                        + " , a.sCancelld "                                                               
                        + " , a.dCancelld "                                                               
                        + " , a.sApproved "                                                               
                        + " , a.dApproved "                                                               
                        + " , a.sEntryByx "                                                               
                        + " , a.dEntryDte "                                                               
                        + " , a.sModified "                                                               
                        + " , a.dModified "                                                        
                        + "  , CASE "                           
                        + " 	WHEN a.cTranStat = '1' THEN 'ACTIVE' "                                         
                        + " 	ELSE 'CANCELLED'  "                                                          
                        + "    END AS sTranStat "   
                          /*BUYING COSTUMER*/                                                             
                        + " , b.sCompnyNm AS sBuyCltNm"                                                               
                        + " , b.cClientTp "                                                               
                        + " , IFNULL(CONCAT( IFNULL(CONCAT(d.sHouseNox,' ') , ''),  "                     
                        + "   IFNULL(CONCAT(d.sAddressx,' ') , ''),                 "                     
                        + "   IFNULL(CONCAT(e.sBrgyName,' '), ''),                  "                     
                        + "   IFNULL(CONCAT(f.sTownName, ', '),''),                 "                     
                        + "   IFNULL(CONCAT(g.sProvName),'') )	, '') AS sAddressx  "                     
                          /*INQUIRY*/                                                                     
                        + " , h.dTransact AS dInqryDte "                                                  
                        + " , h.sClientID AS sInqCltID "                                                  
                        + " , i.sCompnyNm AS sInqCltNm "                                                  
                        + " , i.cClientTp AS cInqCltTp "                                                  
                        + " , h.sContctID              "                                                  
                        + " , j.sCompnyNm AS sContctNm "                                                  
                        + " , h.sSourceCD              "                                                  
                        + " , h.sSourceNo              "                                                  
                        + " , k.sPlatform              "                                                  
                        + " , h.sAgentIDx              "                                                  
                        + " , l.sCompnyNm AS sAgentNmx "                                                  
                        + " , h.sEmployID              "                                                  
                        + " , m.sCompnyNm AS sSENamexx "                                                  
                        //+ " , SUM(n.nAmountxx) AS nRsvAmtTl "                                                  
                          /*CO-CLIENT*/                                                                   
                        + " , o.sCompnyNm AS sCoCltNmx "                                                  
                          /*VEHICLE INFORMATION*/                                                         
                        + " , p.sCSNoxxxx "                                                               
                        + " , q.sPlateNox "                                                               
                        + " , p.sFrameNox "                                                               
                        + " , p.sEngineNo "                                                               
                        + " , p.sKeyNoxxx "                                                               
                        + " , r.sDescript AS sVhclDesc "                                                  
                          /*BRANCH*/                                                                      
                        + " , s.sBranchNm "                                                               
                          /*INSURANCE*/                                                                   
                        + " , t.sBrInsNme AS sTPLBrIns "                                                  
                        + " , u.sInsurNme AS sTPLInsNm "                                                  
                        + " , v.sBrInsNme AS sCOMBrIns "                                                  
                        + " , w.sInsurNme AS sCOMInsNm "                                                  
                          /*BANK*/                                                                        
                        + " , x.sApplicNo "                                                               
                        + " , y.sBrBankNm "                                                               
                        + " , z.sBankName " 
                         /*VSP LINKED THRU THE FOLLOWING FORMS*/     
                        + " , za.sReferNox AS sUDRNoxxx "
                        + " , CONCAT(zb.sDSNoxxxx) AS sDSNoxxxx "
                        + " , CONCAT(zd.sReferNox) AS sSINOxxxx "  
                           /*TODO GATEPASS*/                                                               
                        + " FROM vsp_master a "                                                           
                         /*BUYING CUSTOMER*/                                                              
                        + " LEFT JOIN client_master b ON b.sClientID = a.sClientID "                      
                        + " LEFT JOIN client_address c ON c.sClientID = a.sClientID AND c.cPrimaryx = 1 " 
                        + " LEFT JOIN addresses d ON d.sAddrssID = c.sAddrssID "                          
                        + " LEFT JOIN barangay e ON e.sBrgyIDxx = d.sBrgyIDxx  "                          
                        + " LEFT JOIN towncity f ON f.sTownIDxx = d.sTownIDxx  "                          
                        + " LEFT JOIN province g ON g.sProvIDxx = f.sProvIDxx  "                          
                         /*INQUIRY*/                                                                      
                        + " LEFT JOIN customer_inquiry h ON h.sTransNox = a.sInqryIDx "                   
                        + " LEFT JOIN client_master i ON i.sClientID = h.sClientID    "                   
                        + " LEFT JOIN client_master j ON j.sClientID = h.sContctID    "                   
                        + " LEFT JOIN online_platforms k ON k.sTransNox = h.sSourceNo "                   
                        + " LEFT JOIN client_master l ON l.sClientID = h.sAgentIDx    "                   
                        + " LEFT JOIN ggc_isysdbf.client_master m ON m.sClientID = h.sEmployID    "       
                        //+ " LEFT JOIN customer_inquiry_reservation n ON n.sSourceNo = a.sInqryIDx "       
                         /*CO CLIENT*/                                                                    
                        + " LEFT JOIN client_master o ON o.sClientID = a.sCoCltIDx "                      
                         /*VEHICLE INFORMATION*/                                                          
                        + " LEFT JOIN vehicle_serial p ON p.sSerialID = a.sSerialID "                     
                        + " LEFT JOIN vehicle_serial_registration q ON q.sSerialID = a.sSerialID "        
                        + " LEFT JOIN vehicle_master r ON r.sVhclIDxx = p.sVhclIDxx "                     
                         /*BRANCH*/                                                                       
                        + " LEFT JOIN branch s ON s.sBranchCd = a.sBranchCD "                             
                         /*TPL INSURANCE*/                                                                
                        + " LEFT JOIN insurance_company_branches t ON t.sBrInsIDx = a.sInsTplCd "         
                        + " LEFT JOIN insurance_company u ON u.sInsurIDx = t.sInsurIDx "                  
                         /*COMPREHENSIVE INSURANCE*/                                                      
                        + " LEFT JOIN insurance_company_branches v ON v.sBrInsIDx = a.sInsCodex "         
                        + " LEFT JOIN insurance_company w ON w.sInsurIDx = v.sInsurIDx "                  
                         /*BANK*/                                                                         
                        + " LEFT JOIN bank_application x ON x.sTransNox = a.sBnkAppCD "                   
                        + " LEFT JOIN banks_branches y ON y.sBrBankID = x.sBrBankID   "                   
                        + " LEFT JOIN banks z ON z.sBankIDxx = y.sBankIDxx            "  
                         /*VSP LINKED THRU THE FOLLOWING FORMS*/                                                             
                        + " LEFT JOIN udr_master za ON za.sSourceNo = a.sTransNox AND za.cTranStat = '1' "   
                        + " LEFT JOIN diagnostic_master zb ON zb.sSourceNo = a.sTransNox AND zb.cTranStat = '1' "
                        + " LEFT JOIN si_master_source zc ON zc.sSourceNo = a.sTransNox "
                        + " LEFT JOIN si_master zd ON zd.sTransNox = zc.sReferNox AND zd.cTranStat = '1' "
                        + " WHERE 0=1";
        
        
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "vsp_master", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
