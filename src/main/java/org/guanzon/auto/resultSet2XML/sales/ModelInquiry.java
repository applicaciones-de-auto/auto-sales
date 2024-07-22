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
 * @author MIS-PC
 */
public class ModelInquiry {
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
        
        
        String lsSQL =  "SELECT " +
                    " a.sTransNox"  + //1
                    ", a.sBranchCD" + //2
                    ", a.dTransact" + //3
                    ", a.sEmployID" + //4
                    ", a.cIsVhclNw" + //5
                    ", a.sVhclIDxx" + //6
                    ", a.sClientID" + //7
                    ", a.sRemarksx" + //8
                    ", a.sAgentIDx" + //9
                    ", a.dTargetDt" + //10
                    ", a.cIntrstLv" + //11
                    ", a.sSourceCD" + //12
                    ", a.sSourceNo" + //13
                    ", a.sTestModl" + //14
                    ", a.sActvtyID" + //15
                    ", a.dLastUpdt" + //16
                    ", a.nReserved" + //17
                    ", a.nRsrvTotl" + //18
                    ", a.sLockedBy" + //19
                    ", a.sLockedDt" + //20
                    ", a.sApproved" + //21
                    ", a.sSerialID" + //22
                    ", a.sInqryCde" + //23
                    ", a.cTranStat" + //24
                    ", a.sEntryByx" + //25
                    ", a.dEntryDte" + //26
                    ", a.sModified" + //27
                    ", a.dModified" + //28
                    ",IFNULL(b.sCompnyNm,'') as sCompnyNm " +//29
                    ",IFNULL(c.sMobileNo,'') as sMobileNo " +//30
                    ",IFNULL(h.sAccountx,'') as sAccountx " +//31
                    ",IFNULL(i.sEmailAdd,'') as sEmailAdd " +//32
                    ", IFNULL(CONCAT( IFNULL(CONCAT(dd.sHouseNox,' ') , ''), IFNULL(CONCAT(dd.sAddressx,' ') , ''), " +
                    " 	IFNULL(CONCAT(f.sBrgyName,' '), ''), " +
                    " 	IFNULL(CONCAT(e.sTownName, ', '),''), " +
                    " 	IFNULL(CONCAT(g.sProvName),'') )	, '') AS sAddressx " + //33
                    " ,IFNULL(j.sCompnyNm, '') AS sSalesExe   " + //34
                    " ,IFNULL(l.sCompnyNm, '') AS sSalesAgn   " + //35
                    " ,IFNULL(m.sPlatform, '') AS sPlatform   " + //36
                    " ,IFNULL(n.sActTitle, '') AS sActTitle   " + //37
                    " ,IFNULL(k.sBranchNm, '') AS sBranchNm   " + //38
                    " ,IFNULL(b.cClientTp,'') AS cClientTp" + //39
                    " ,IFNULL(a.sContctID,'') AS sContctID" + //40
                    " ,IFNULL(o.sCompnyNm,'') AS sContctNm" + //41
                " FROM customer_inquiry a " +
                " LEFT JOIN client_master b ON b.sClientID = a.sClientID" +
                " LEFT JOIN client_mobile c ON c.sClientID = b.sClientID AND c.cPrimaryx = '1' " +
                " LEFT JOIN client_address d ON d.sClientID = b.sClientID AND d.cPrimaryx = '1' " + 
                " LEFT JOIN addresses dd ON dd.sAddrssID = d.sAddrssID" + 
                " LEFT JOIN TownCity e ON e.sTownIDxx = dd.sTownIDxx" +
                " LEFT JOIN Barangay f ON f.sBrgyIDxx = dd.sBrgyIDxx AND f.sTownIDxx = dd.sTownIDxx" + 
                " LEFT JOIN Province g on g.sProvIDxx = e.sProvIDxx" +
                " LEFT JOIN client_social_media h ON h.sClientID = b.sClientID" +
                " LEFT JOIN client_email_address i ON i.sClientID = b.sClientID AND i.cPrimaryx = '1' " +
                " LEFT JOIN ggc_isysdbf.client_master j ON j.sClientID = a.sEmployID  " +
                " LEFT JOIN branch k on k.sBranchCd = a.sBranchCd " +
                " LEFT JOIN client_master l ON l.sClientID = a.sAgentIDx" + 
                " LEFT JOIN online_platforms m ON m.sTransNox = a.sSourceNo" + 
                " LEFT JOIN activity_master n ON n.sActvtyID = a.sActvtyID"  +
                " LEFT JOIN client_master o ON o.sClientID = a.sContctID" +
                " WHERE 0=1";
        
        
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
