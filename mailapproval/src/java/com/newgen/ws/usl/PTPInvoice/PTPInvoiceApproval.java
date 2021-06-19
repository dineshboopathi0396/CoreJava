/**
 * Copyright 1992-2017 Newgen Software Technologies, Inc. A-6, Satsang Vihar
 * Marg, Qutab Institutional Area, New Delhi -110 067 INDIA All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Newgen
 * Software Technologies, Inc. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in accordance
 * with the terms of the license agreement you entered into with newgen.
 *
 *
 * Product      : Webservice
 * Application  : Webservice
 * Module       :
 * File         : PTPApproval.java
 * Author       : anish.j
 * Reviewed By	:
 * Review Date	:
 * * **********************************************************************************************************************************************
 * Change History
 * **********************************************************************************************************************************************
 * Date Change By Bug No.(If Any) Change Description (DD-MMM-YYYY)
 * **********************************************************************************************************************************************
 *
 *
 ************************************************************************************************************************************************
 */
package com.newgen.ws.usl.PTPInvoice;

import com.newgen.wfdesktop.xmlapi.WFXmlResponse;
import com.newgen.ws.usl.CommonMethods;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("PTPInvoice")
public class PTPInvoiceApproval {

    private String SystemUser = "";
    private String checkflag = "";
    private String tempusername="";

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/action")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public PTPInvoiceOutputXml postXml(PTPInvoiceInputXml ix) throws SQLException, ClassNotFoundException, IOException {
        CommonMethods call = new CommonMethods();
        PTPInvoiceOutputXml xml = new PTPInvoiceOutputXml();
        String sMainCode = "", sMessage = "", sType = "";
        WFXmlResponse xmlResponse = null;

        if (call.ReadIni()) {
            String UserName = ix.getUsername();
            String ProcessInstId = ix.getProcessinstid();
            String Action = ix.getStatus();
            String Comments = ix.getComments();
            String Process = ix.getProcess();
            String mode = ix.getMode();
            String seqid= ix.getSeqid();
            CommonMethods.GenLog("*******Inside PTPApproval>>","T");
            CommonMethods.GenLog("*******UserName>>"+UserName,"T");
            CommonMethods.GenLog("*******ProcessInstId>>"+ProcessInstId,"T");
            CommonMethods.GenLog("*******Action>>"+Action,"T");
            CommonMethods.GenLog("*******Comments>>"+Comments,"T");
            CommonMethods.GenLog("*******Process>>"+Process,"T");
            CommonMethods.GenLog("*******mode>>"+mode,"T");
            CommonMethods.GenLog("*******seqid>>"+seqid,"T");

            try {
                FileInputStream sInput = new FileInputStream(System.getProperty("user.dir") + File.separator + "PropertyFiles" + File.separator + "mailApproval.ini");
                Properties prop = new Properties();
                prop.load(sInput);
                CommonMethods.GenLog("*******Reading INI File>>"+sInput,"T");
                SystemUser = prop.getProperty("UserName");
                CommonMethods.GenLog("*******SystemUser>>"+SystemUser,"T");
                //Connect Cabinet
                String sessid=call.connectCabinet();
                if(sessid.equalsIgnoreCase(""))
                {
                    sMainCode = "18";
                    sMessage = "Connection not established";
                    sType = "E";
                    CommonMethods.GenLog("sMessage-->> " + sMessage,"E");
                    Action="Error";                    
                    CommonMethods.GenLog("*******An error occured while connecting cabinet>>"+sessid,"E");                   
                    String UpdateHistory = "Insert INTO WS_MAILAPPROVAL_HISTORY (PROCESS,PROCESSINSTID,USERNAME,COMPLETIONDATE,STATUS,ACTION,Mode) values ('" + Process + "','" + ProcessInstId + "','" + tempusername + "',getDate(),'" + sMessage + "','"+Action+"','" + mode + "')";
                    call.ExecuteUpdateQuery(UpdateHistory);
                    CommonMethods.GenLog("History Update-->> " + UpdateHistory,"E");                     
                }
                 
                //Validate User
                String sQuery = "Select AssignedUser from WFINSTRUMENTTABLE with(nolock) where PROCESSINSTANCEID='" + ProcessInstId + "'";
                CommonMethods.GenLog("Assigned User Query-->> " + sQuery,"T");
                String OutputXml = CommonMethods.selectQuery(sQuery, 1);
                xmlResponse = new WFXmlResponse(OutputXml);
                String AssignedUser = "";
                AssignedUser = xmlResponse.getVal("Value1");
                CommonMethods.GenLog("Assigned User-->> " + AssignedUser,"T");
                tempusername =UserName;
                CommonMethods.GenLog("********************!!!!!!!!!!!!!!!!tempusername>>"+tempusername,"T");

                if ((!AssignedUser.equalsIgnoreCase(UserName)) && (AssignedUser.equalsIgnoreCase(""))) {

                    String Userindex_Browser = "SELECT CAST(UserIndex AS NVARCHAR) from PDBUser with(nolock) where UserName ='" + UserName + "'";
                    CommonMethods.GenLog("Userindex_Browser-->> " + Userindex_Browser,"T");
                    String OutputXml_Browser = CommonMethods.selectQuery(Userindex_Browser, 1);
                    xmlResponse = new WFXmlResponse(OutputXml_Browser);
                    String uBrowserIdex = "";
                    uBrowserIdex = xmlResponse.getVal("Value1");
                    CommonMethods.GenLog("Browser User Index-->> " + uBrowserIdex,"T");

                    String userindices = "SELECT CAST(var_int1 AS NVARCHAR)+'~'+CAST(var_int2 AS NVARCHAR) FROM WFINSTRUMENTTABLE WHERE processinstanceid='" + ProcessInstId + "'";
                    CommonMethods.GenLog("userindices-->> " + userindices,"T");
                    String OutputXml_UserIndices = CommonMethods.selectQuery(userindices, 1);
                    xmlResponse = new WFXmlResponse(OutputXml_UserIndices);
                    String userIndexes;
                    userIndexes = xmlResponse.getVal("Value1").toString();
                    CommonMethods.GenLog("User Indices-->> " + userIndexes,"T");

                    //to check whether the user is present in given list of users
                    if (userIndexes.contains(uBrowserIdex)) {
                            CommonMethods.setNextApprover(ProcessInstId,UserName);
                            checkflag="Y";
                         

                    }
                    else{
                    sMainCode = "18";
                    sMessage = "Invalid UserID";
                    sType = "E";
                    CommonMethods.GenLog("sMessage-->> " + sMessage,"E");
                    Action="InvalidUserID";
                    String UpdateHistory = "Insert INTO WS_MAILAPPROVAL_HISTORY (PROCESS,PROCESSINSTID,USERNAME,COMPLETIONDATE,STATUS,ACTION,Mode,SequenceNo) values ('" + Process + "','" + ProcessInstId + "','" + tempusername + "',getDate(),'" + sMessage + "','"+Action+"','" + mode + "','"+seqid+"')";
                    call.ExecuteUpdateQuery(UpdateHistory);
                    CommonMethods.GenLog("History Update-->> " + UpdateHistory,"E");    
                    }
                    UserName="";
                    UserName=SystemUser;

                } else if ((!AssignedUser.equalsIgnoreCase(UserName)) && (!AssignedUser.equalsIgnoreCase(""))) {
                    sMainCode = "18";
                    sMessage = "Invalid Workitem";
                    sType = "E";
                    CommonMethods.GenLog("sMessage-->> " + sMessage,"E");
                    Action="Processed";
                    String UpdateHistory = "Insert INTO WS_MAILAPPROVAL_HISTORY (PROCESS,PROCESSINSTID,USERNAME,COMPLETIONDATE,STATUS,ACTION,Mode,SequenceNo) values ('" + Process + "','" + ProcessInstId + "','" + tempusername + "',getDate(),'" + sMessage + "','"+Action+"','" + mode + "','"+seqid+"')";
                    call.ExecuteUpdateQuery(UpdateHistory);
                    CommonMethods.GenLog("History Update-->> " + UpdateHistory,"E"); 

                } else if (AssignedUser.equalsIgnoreCase(UserName)) {
                    CommonMethods.setNextApprover(ProcessInstId,UserName);
                    checkflag="Y";

                }
                //get currentloop and total loop
                String sQuery_total="SELECT SA_CurrentLoop,SA_Total FROM EXT_PTP WITH (nolock) WHERE PROCESSINSTID='"+ProcessInstId+"'";
                CommonMethods.GenLog("Assigned User total-->> " + sQuery_total,"T");
                String OutputXml_total = CommonMethods.selectQuery(sQuery_total, 2);
                xmlResponse = new WFXmlResponse(OutputXml_total);
                int SA_CurrentLoop ;
                int SA_Total;
                SA_CurrentLoop = Integer.parseInt(xmlResponse.getVal("Value1"));                
                SA_Total = Integer.parseInt(xmlResponse.getVal("Value2"));
                CommonMethods.GenLog("SA_CurrentLoop-->> " + SA_CurrentLoop,"T"); 
                CommonMethods.GenLog("SA_Total-->> " + SA_Total,"T"); 
                if(checkflag.equalsIgnoreCase("Y"))
                {
                       if (Action.equalsIgnoreCase("approve")) {
                                String searchWorkstep = "Select CURR_WORKSTEP from EXT_PTP with(nolock) where PROCESSINSTID='" + ProcessInstId + "'";
                                CommonMethods.GenLog("searchWorkstep-->> " + searchWorkstep,"T");
                                String sOutput = CommonMethods.selectQuery(searchWorkstep, 1);
                                xmlResponse = new WFXmlResponse(sOutput);
                                String Curr_Workstep = "";
                                Curr_Workstep = xmlResponse.getVal("Value1");
                                CommonMethods.GenLog("CURR_WORKSTEP-->> " + Curr_Workstep,"T");

                                String searchPREV_Workstep = "Select PREV_WORKSTEP from EXT_PTP with(nolock) where PROCESSINSTID='" + ProcessInstId + "'";
                                CommonMethods.GenLog("searchWorkstep-->> " + searchPREV_Workstep,"T");
                                String sOutputXml = CommonMethods.selectQuery(searchPREV_Workstep, 1);
                                xmlResponse = new WFXmlResponse(sOutputXml);
                                String Prev_Workstep = "";
                                Prev_Workstep = xmlResponse.getVal("Value1");
                                CommonMethods.GenLog("PREV_WORKSTEP-->> " + Prev_Workstep,"T");

                                if (Curr_Workstep.equalsIgnoreCase("Approver")) {
                                    CommonMethods.GenLog("Inside PTP Approver","T");
                                    if (call.ReassignWorkitem(ProcessInstId, SystemUser,UserName)){// AssignedUser)) {
                                        String Query = "UPDATE EXT_PTP SET ACTIONITEM='Submit',Comments='" + Comments + "',APPROVALEDATE=getDate() where PROCESSINSTID='" + ProcessInstId + "'";
                                        CommonMethods.GenLog("EXTERNAL Update-->> " + Query,"T");
                                        call.ExecuteUpdateQuery(Query);
                                        boolean Flag = call.CompleteWorkitem(ProcessInstId, "1");
                                        CommonMethods.GenLog("CompleteWorkitem Flag-->> " + Flag,"T");
                                        if (Flag) {
                                            sMainCode = "0";
                                            sMessage = "WorkItem Completed Successfully";
                                            CommonMethods.GenLog("Mail Trigger execution starts","T");
                                            call.InsertCommentsHistory(ProcessInstId, "Approval", "Approval", Curr_Workstep, Prev_Workstep, Curr_Workstep, tempusername, Comments);
                                            if(SA_CurrentLoop<=SA_Total){
                                            CommonMethods.mailTrigger_PTP(ProcessInstId);
                                            }
                                            CommonMethods.GenLog("sMessage-->> " + sMessage,"T");
                                            sType = "S";
                                            String UpdateHistory = "Insert INTO WS_MAILAPPROVAL_HISTORY (PROCESS,PROCESSINSTID,USERNAME,WORKSTEP,COMPLETIONDATE,STATUS,ACTION,MODE,SequenceNo) values ('" + Process + "','" + ProcessInstId + "','" + tempusername + "','" + Curr_Workstep + "',getDate(),'" + sMessage + "','" + Action + "','" + mode + "','"+seqid+"')";
                                            call.ExecuteUpdateQuery(UpdateHistory);
                                            CommonMethods.GenLog("History Update-->> " + UpdateHistory,"T");
                                            CommonMethods.DisconnectCabinet();
                                        } else {
                                            sMainCode = "15";
                                            sMessage = "Error in CompleteWorkItem";
                                            Action="Error";
                                            String UpdateHistory = "Insert INTO WS_MAILAPPROVAL_HISTORY (PROCESS,PROCESSINSTID,USERNAME,WORKSTEP,COMPLETIONDATE,STATUS,ACTION,Mode,SequenceNo) values ('" + Process + "','" + ProcessInstId + "','" + tempusername + "','" + Curr_Workstep + "',getDate(),'" + sMessage + "','"+Action+"','" + mode + "','"+seqid+"')";
                                            call.ExecuteUpdateQuery(UpdateHistory);
                                            CommonMethods.GenLog("History Update-->> " + UpdateHistory,"T");                                             
                                            CommonMethods.GenLog("sMessage-->> " + sMessage,"T");
                                            CommonMethods.DisconnectCabinet();
                                            sType = "E";
                                        }
                                    } else {
                                        sMainCode = "26";
                                        sMessage = "Error in ReassignWorkItem";
                                        Action="Error";
                                        String UpdateHistory = "Insert INTO WS_MAILAPPROVAL_HISTORY (PROCESS,PROCESSINSTID,USERNAME,WORKSTEP,COMPLETIONDATE,STATUS,ACTION,Mode,SequenceNo) values ('" + Process + "','" + ProcessInstId + "','" + tempusername + "','" + Curr_Workstep + "',getDate(),'" + sMessage + "','"+Action+"','" + mode + "','"+seqid+"')";
                                        call.ExecuteUpdateQuery(UpdateHistory);
                                        CommonMethods.GenLog("History Update-->> " + UpdateHistory,"E");                                         
                                        CommonMethods.GenLog("sMessage-->> " + sMessage,"E");
                                        sType = "E";
                                    }

                                }
                                else{
                                    sMainCode = "18";
                                    sMessage = "Processed";
                                    sType = "E";
                                    CommonMethods.GenLog("sMessage-->> " + sMessage, "E");
                                    Action = "Processed";
                                    String UpdateHistory = "Insert INTO WS_MAILAPPROVAL_HISTORY (PROCESS,PROCESSINSTID,USERNAME,COMPLETIONDATE,STATUS,ACTION,Mode,SequenceNo) values ('" + Process + "','" + ProcessInstId + "','" + tempusername + "',getDate(),'" + sMessage + "','" + Action + "','" + mode + "','" + seqid + "')";
                                    call.ExecuteUpdateQuery(UpdateHistory);
                                    CommonMethods.GenLog("History Update-->> " + UpdateHistory, "E");                                
                                }

                            } else if (Action.equalsIgnoreCase("reject")) {
                                String searchWorkstep = "Select CURR_WORKSTEP from EXT_PTP with(nolock) where PROCESSINSTID='" + ProcessInstId + "'";
                                CommonMethods.GenLog("searchWorkstep-->> " + searchWorkstep,"T");
                                String sOutput = CommonMethods.selectQuery(searchWorkstep, 1);
                                xmlResponse = new WFXmlResponse(sOutput);
                                String Curr_Workstep = "";
                                Curr_Workstep = xmlResponse.getVal("Value1");
                                CommonMethods.GenLog("CURR_WORKSTEP-->> " + Curr_Workstep,"T");

                                String searchPREV_Workstep = "Select PREV_WORKSTEP from EXT_PTP with(nolock) where PROCESSINSTID='" + ProcessInstId + "'";
                                CommonMethods.GenLog("searchWorkstep-->> " + searchPREV_Workstep,"T");
                                String sOutputXml = CommonMethods.selectQuery(searchPREV_Workstep, 1);
                                xmlResponse = new WFXmlResponse(sOutputXml);
                                String Prev_Workstep = "";
                                Prev_Workstep = xmlResponse.getVal("Value1");
                                CommonMethods.GenLog("PREV_WORKSTEP-->> " + Prev_Workstep,"T");

                                if (Curr_Workstep.equalsIgnoreCase("Approver")) {
                                    CommonMethods.GenLog("Inside PTP Approval","T");
                                    if (call.ReassignWorkitem(ProcessInstId, SystemUser, UserName)){//AssignedUser)) {
                                        String Query = "UPDATE EXT_PTP SET ACTIONITEM='Reject',Comments='" + Comments + "',APPROVALEDATE=getDate() where PROCESSINSTID='" + ProcessInstId + "'";
                                        CommonMethods.GenLog("EXTERNAL Update-->> " + Query,"T");
                                        call.ExecuteUpdateQuery(Query);
                                        boolean Flag = call.CompleteWorkitem(ProcessInstId, "1");
                                        CommonMethods.GenLog("CompleteWorkitem Flag-->> " + Flag,"T");
                                        if (Flag) {
                                            sMainCode = "0";
                                            sMessage = "WorkItem Completed Successfully";
                                            CommonMethods.GenLog("Mail Trigger execution starts","T");
                                            call.InsertCommentsHistory(ProcessInstId, "Requestor", "Requestor", Curr_Workstep, Prev_Workstep, Curr_Workstep, tempusername, Comments);
                                            if(SA_CurrentLoop<=SA_Total){
                                            CommonMethods.mailTrigger_PTP(ProcessInstId);
                                            }
                                            CommonMethods.GenLog("sMessage-->> " + sMessage,"T");
                                            sType = "S";
                                            CommonMethods.GenLog("*********************","T");
                                            
                                            CommonMethods.GenLog("tempusername-->> " + tempusername,"T");
                                            String UpdateHistory = "Insert INTO WS_MAILAPPROVAL_HISTORY (PROCESS,PROCESSINSTID,USERNAME,WORKSTEP,COMPLETIONDATE,STATUS,ACTION,Mode,SequenceNo) values ('" + Process + "','" + ProcessInstId + "','" + tempusername + "','" + Curr_Workstep + "',getDate(),'" + sMessage + "','"+Action+"','" + mode + "','"+seqid+"')";
                                            call.ExecuteUpdateQuery(UpdateHistory);
                                            CommonMethods.GenLog("History Update-->> " + UpdateHistory,"T");
                                            CommonMethods.DisconnectCabinet();
                                        } else {
                                            sMainCode = "15";
                                            Action="Error";
                                            sMessage = "Error in CompleteWorkItem";
                                            String UpdateHistory = "Insert INTO WS_MAILAPPROVAL_HISTORY (PROCESS,PROCESSINSTID,USERNAME,WORKSTEP,COMPLETIONDATE,STATUS,ACTION,Mode,SequenceNo) values ('" + Process + "','" + ProcessInstId + "','" + tempusername + "','" + Curr_Workstep + "',getDate(),'" + sMessage + "','"+Action+"','" + mode + "','"+seqid+"')";
                                            call.ExecuteUpdateQuery(UpdateHistory);
                                            CommonMethods.GenLog("History Update-->> " + UpdateHistory,"T");                                            
                                            CommonMethods.GenLog("sMessage-->> " + sMessage,"T");
                                            CommonMethods.DisconnectCabinet();
                                            sType = "E";
                                        }
                                    } else {
                                        sMainCode = "26";
                                        sMessage = "Error in ReassignWorkItem";
                                        CommonMethods.GenLog("sMessage-->> " + sMessage,"T");
                                        Action="Error";
                                        String UpdateHistory = "Insert INTO WS_MAILAPPROVAL_HISTORY (PROCESS,PROCESSINSTID,USERNAME,WORKSTEP,COMPLETIONDATE,STATUS,ACTION,Mode,SequenceNo) values ('" + Process + "','" + ProcessInstId + "','" + tempusername + "','" + Curr_Workstep + "',getDate(),'" + sMessage + "','"+Action+"','" + mode + "','"+seqid+"')";
                                        call.ExecuteUpdateQuery(UpdateHistory);
                                        CommonMethods.GenLog("History Update-->> " + UpdateHistory,"T");                                            
                                            
                                        sType = "E";
                                    }

                                }else{
                                    sMainCode = "18";
                                    sMessage = "Processed";
                                    sType = "E";
                                    CommonMethods.GenLog("sMessage-->> " + sMessage, "E");
                                    Action = "Processed";
                                    String UpdateHistory = "Insert INTO WS_MAILAPPROVAL_HISTORY (PROCESS,PROCESSINSTID,USERNAME,COMPLETIONDATE,STATUS,ACTION,Mode,SequenceNo) values ('" + Process + "','" + ProcessInstId + "','" + tempusername + "',getDate(),'" + sMessage + "','" + Action + "','" + mode + "','" + seqid + "')";
                                    call.ExecuteUpdateQuery(UpdateHistory);
                                    CommonMethods.GenLog("History Update-->> " + UpdateHistory, "E");                                       
                                }
                            }
                }
            } catch (Exception e) {
                CommonMethods.GenLog("PTPApproval Exception-->> " + e,"T");
                Action="Error";
                String UpdateHistory = "Insert INTO WS_MAILAPPROVAL_HISTORY (PROCESS,PROCESSINSTID,USERNAME,COMPLETIONDATE,STATUS,ACTION,Mode,SequenceNo) values ('" + Process + "','" + ProcessInstId + "','" + tempusername + "',getDate(),'" + sMessage + "','"+Action+"','" + mode + "','"+seqid+"')";
                call.ExecuteUpdateQuery(UpdateHistory);                
            }

            xml.setMainCode(sMainCode);
            xml.setMessage(sMessage);
            xml.setType(sType);
            xml.setSeqid(seqid);
        }
        return xml;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }

}
