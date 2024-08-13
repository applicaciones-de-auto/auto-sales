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
public class BankApplication {
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
        
        System.setProperty("sys.default.path.metadata", "D:/GGC_Maven_Systems/config/metadata/Model_Bank_Application.xml");
        
        
        String lsSQL =    " SELECT "                                                 
                        + "    a.sTransNox "                                         
                        + "  , a.sApplicNo "                                         
                        + "  , a.dAppliedx "                                         
                        + "  , a.dApproved "                                         
                        + "  , a.cPayModex "                                         
                        + "  , a.sSourceCD "                                         
                        + "  , a.sSourceNo "                                         
                        + "  , a.sBrBankID "                                         
                        + "  , a.sRemarksx "                                         
                        + "  , a.cTranStat "                                         
                        + "  , a.sEntryByx "                                         
                        + "  , a.dEntryDte "                                         
                        + "  , a.sModified "                                         
                        + "  , a.dModified "                                         
                        + "  , a.sCancelld "                                         
                        + "  , a.dCancelld "                                         
                        + "  , b.sBrBankNm "                                         
                        + "  , c.sBankIDxx "                                         
                        + "  , c.sBankName "                                         
                        + "  , d.sTownName "                                         
                        + "  , e.sProvName " 
                        + "  , UPPER(CONCAT(b.sAddressx,' ', d.sTownName, ', ', e.sProvName)) sAddressx "                                      
                        + "  , c.sBankType " 
                        + " FROM bank_application a "                                
                        + " LEFT JOIN banks_branches b ON b.sBrBankID = a.sBrBankID "
                        + " LEFT JOIN banks c ON c.sBankIDxx = b.sBankIDxx          "
                        + " LEFT JOIN towncity d ON d.sTownIDxx = b.sTownIDxx       "
                        + " LEFT JOIN province e ON e.sProvIDxx = d.sProvIDxx       " 
                        + " WHERE 0=1";
        
        
        ResultSet loRS = instance.executeQuery(lsSQL);
        try {
            if (MiscUtil.resultSet2XML(instance, loRS, System.getProperty("sys.default.path.metadata"), "bank_application", "")){
                System.out.println("ResultSet exported.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}
