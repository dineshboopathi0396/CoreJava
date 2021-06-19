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
 * File         : LegalApproval.java
 * Author       : anil.baggio
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
package com.newgen.ws.usl.legal;

import com.newgen.wfdesktop.xmlapi.WFXmlResponse;
import com.newgen.ws.usl.CommonMethods;
import java.io.IOException;
import java.sql.SQLException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("legal")
public class LegalApproval {

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/action")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public LegalOutputXml postXml(LegalInputXml ix) throws SQLException, ClassNotFoundException, IOException {
        CommonMethods call = new CommonMethods();
        LegalOutputXml xml = new LegalOutputXml();
        String sMainCode = "", sMessage = "", sType = "";
        WFXmlResponse xmlResponse = null;

        if (call.ReadIni()) {
            String UserName = ix.getUsername();
            String ProcessInstId = ix.getProcessinstid();
            String Action = ix.getStatus();
            String Comments = ix.getComments();
            String Process = ix.getProcess();
            String mode = ix.getMode();
            String sequenceId = ix.getSeqid();

            try {
                //Connect Cabinet
                call.connectCabinet();

                //Validate User
                String sQuery = "Select AssignedUser from WFINSTRUMENTTABLE with(nolock) where PROCESSINSTANCEID='" + ProcessInstId + "'";
                CommonMethods.GenLog("Assigned User Query-->> " + sQuery, "T");
                String OutputXml = CommonMethods.selectQuery(sQuery, 1);
                xmlResponse = new WFXmlResponse(OutputXml);
                String AssignedUser = "";
                AssignedUser = xmlResponse.getVal("Value1");
                CommonMethods.GenLog("Assigned User-->> " + AssignedUser, "T");

                if (Action.equalsIgnoreCase("approve")) {
                    String searchWorkstep = "Select isnull(CURR_WORKSTEP,'')+'`'+isnull(PREV_WORKSTEP,'') from EXT_LEGAL with(nolock) where PROCESSINSTID='" + ProcessInstId + "'";
                    CommonMethods.GenLog("searchWorkstep-->> " + searchWorkstep, "T");
                    String sOutput = CommonMethods.selectQuery(searchWorkstep, 1);
                    xmlResponse = new WFXmlResponse(sOutput);
                    String Workstep = "";
                    Workstep = xmlResponse.getVal("Value1");
                    CommonMethods.GenLog("Workstep-->> " + Workstep, "T");

                    String[] WorkstepData = Workstep.split("`");
                    String Prev_Workstep = "", Curr_Workstep = "";
                    if (WorkstepData.length > 0) {
                        Curr_Workstep = WorkstepData[0];
                        Prev_Workstep = WorkstepData[1];
                    }

                    if (Curr_Workstep.equalsIgnoreCase("RO_Approval")) {
                        CommonMethods.GenLog("Inside Legal RO_Approval", "T");
                        if (call.ReassignWorkitem(ProcessInstId, CommonMethods.UserName, AssignedUser)) {
                            String Query = "UPDATE EXT_LEGAL SET ACTIONITEM='Submit',Comments='" + Comments + "',APPROVEREDATE=getDate() where PROCESSINSTID='" + ProcessInstId + "'";
                            CommonMethods.GenLog("EXTERNAL Update-->> " + Query, "T");
                            CommonMethods.ExecuteUpdateQuery(Query);
                            boolean Flag = call.CompleteWorkitem(ProcessInstId, "1");
                            CommonMethods.GenLog("CompleteWorkitem Flag-->> " + Flag, "T");
                            if (Flag) {
                                sMainCode = "0";
                                sMessage = "WorkItem Completed Successfully";
                                CommonMethods.GenLog("sMessage-->> " + sMessage, "T");
                                sType = "S";
                                try {
//                                    CommonMethods.mailTrigger_Legal(ProcessInstId, Comments);
                                } catch (Exception e) {
                                    CommonMethods.GenLog("Exception in mailTrigger_Legal-->> " + e, "E");
                                }
                                CommonMethods.InsertCommentsHistory(ProcessInstId, "Approval", "Approval", Curr_Workstep, Prev_Workstep, Curr_Workstep, UserName, Comments);
                                String UpdateHistory = "Insert INTO WS_MAILAPPROVAL_HISTORY (PROCESS,PROCESSINSTID,USERNAME,WORKSTEP,COMPLETIONDATE,STATUS,ACTION,MODE,SequenceNo) values ('" + Process + "','" + ProcessInstId + "','" + UserName + "','" + Curr_Workstep + "',getDate(),'" + sMessage + "','" + Action + "','" + mode + "','" + sequenceId + "')";
                                CommonMethods.ExecuteUpdateQuery(UpdateHistory);
                                CommonMethods.GenLog("History Update-->> " + UpdateHistory, "T");
                            } else {
                                sMainCode = "15";
                                sMessage = "Error in CompleteWorkItem";
                                CommonMethods.GenLog("sMessage-->> " + sMessage, "T");
                                sType = "E";
                            }
                        } else {
                            sMainCode = "26";
                            sMessage = "Error in ReassignWorkItem";
                            CommonMethods.GenLog("sMessage-->> " + sMessage, "T");
                            sType = "E";
                        }

                    } else if (Curr_Workstep.equalsIgnoreCase("Legal_Approval")) {
                        CommonMethods.GenLog("Inside Legal Legal_Approval", "T");
                        String Query = "UPDATE EXT_LEGAL SET ACTIONITEM='Submit',Comments='" + Comments + "',LEGALAPPEDATE=getDate() where PROCESSINSTID='" + ProcessInstId + "'";
                        CommonMethods.ExecuteUpdateQuery(Query);
                        boolean Flag = call.CompleteWorkitem(ProcessInstId, "1");
                        CommonMethods.GenLog("CompleteWorkitem Flag-->> " + Flag, "T");
                        if (Flag) {
                            sMainCode = "0";
                            sMessage = "WorkItem Completed Successfully";
                            CommonMethods.GenLog("sMessage-->> " + sMessage, "T");
                            sType = "S";
                            CommonMethods.InsertCommentsHistory(ProcessInstId, "Exit", "Exit", Curr_Workstep, Prev_Workstep, Curr_Workstep, UserName, Comments);
                            String UpdateHistory = "Insert INTO WS_MAILAPPROVAL_HISTORY (PROCESS,PROCESSINSTID,USERNAME,WORKSTEP,COMPLETIONDATE,STATUS,ACTION,MODE,SequenceNo) values ('" + Process + "','" + ProcessInstId + "','" + UserName + "','" + Curr_Workstep + "',getDate(),'" + sMessage + "','" + Action + "','" + mode + "','" + sequenceId + "')";
                            CommonMethods.ExecuteUpdateQuery(UpdateHistory);
                            CommonMethods.GenLog("History Update-->> " + UpdateHistory, "T");
                        } else {
                            sMainCode = "5";
                            sMessage = "Error in CompleteWorkItem";
                            CommonMethods.GenLog("sMessage-->> " + sMessage, "T");
                            sType = "E";
                        }
                    }

                } else if (Action.equalsIgnoreCase("reject")) {
                    String searchWorkstep = "Select CURR_WORKSTEP+'`'+PREV_WORKSTEP from EXT_LEGAL with(nolock) where PROCESSINSTID='" + ProcessInstId + "'";
                    CommonMethods.GenLog("searchWorkstep-->> " + searchWorkstep, "T");
                    String sOutput = CommonMethods.selectQuery(searchWorkstep, 1);
                    xmlResponse = new WFXmlResponse(sOutput);
                    String Workstep = "";
                    Workstep = xmlResponse.getVal("Value1");
                    CommonMethods.GenLog("Workstep-->> " + Workstep, "T");

                    String[] WorkstepData = Workstep.split("`");
                    String Prev_Workstep = "", Curr_Workstep = "";
                    if (WorkstepData.length > 0) {
                        Curr_Workstep = WorkstepData[0];
                        Prev_Workstep = WorkstepData[1];
                    }

                    if (Curr_Workstep.equalsIgnoreCase("RO_Approval")) {
                        CommonMethods.GenLog("Inside Legal RO_Approval", "T");
                        if (call.ReassignWorkitem(ProcessInstId, CommonMethods.UserName, AssignedUser)) {
                            String Query = "UPDATE EXT_LEGAL SET ACTIONITEM='Reject',Comments='" + Comments + "',APPROVEREDATE=getDate() where PROCESSINSTID='" + ProcessInstId + "'";
                            CommonMethods.GenLog("EXTERNAL Update-->> " + Query, "T");
                            CommonMethods.ExecuteUpdateQuery(Query);
                            boolean Flag = call.CompleteWorkitem(ProcessInstId, "1");
                            CommonMethods.GenLog("CompleteWorkitem Flag-->> " + Flag, "T");
                            if (Flag) {
                                sMainCode = "0";
                                sMessage = "WorkItem Completed Successfully";
                                CommonMethods.GenLog("sMessage-->> " + sMessage, "T");
                                sType = "S";
                                CommonMethods.InsertCommentsHistory(ProcessInstId, "Requestor", "Requestor", Curr_Workstep, Prev_Workstep, Curr_Workstep, UserName, Comments);
                                String UpdateHistory = "Insert INTO WS_MAILAPPROVAL_HISTORY (PROCESS,PROCESSINSTID,USERNAME,WORKSTEP,COMPLETIONDATE,STATUS,ACTION,MODE,SequenceNo) values ('" + Process + "','" + ProcessInstId + "','" + UserName + "','" + Curr_Workstep + "',getDate(),'" + sMessage + "','" + Action + "','" + mode + "','" + sequenceId + "')";
                                CommonMethods.ExecuteUpdateQuery(UpdateHistory);
                                CommonMethods.GenLog("History Update-->> " + UpdateHistory, "T");
                            } else {
                                sMainCode = "15";
                                sMessage = "Error in CompleteWorkItem";
                                CommonMethods.GenLog("sMessage-->> " + sMessage, "T");
                                sType = "E";
                            }
                        } else {
                            sMainCode = "26";
                            sMessage = "Error in ReassignWorkItem";
                            CommonMethods.GenLog("sMessage-->> " + sMessage, "T");
                            sType = "E";
                        }

                    } else if (Curr_Workstep.equalsIgnoreCase("Legal_Approval")) {
                        CommonMethods.GenLog("Inside Legal Legal_Approval", "T");
                        String Query = "UPDATE EXT_LEGAL SET ACTIONITEM='Reject',Comments='" + Comments + "',LEGALAPPEDATE=getDate() where PROCESSINSTID='" + ProcessInstId + "'";
                        CommonMethods.ExecuteUpdateQuery(Query);
                        boolean Flag = call.CompleteWorkitem(ProcessInstId, "1");
                        CommonMethods.GenLog("CompleteWorkitem Flag-->> " + Flag, "T");
                        if (Flag) {
                            sMainCode = "0";
                            sMessage = "WorkItem Completed Successfully";
                            CommonMethods.GenLog("sMessage-->> " + sMessage, "T");
                            sType = "S";
                            CommonMethods.InsertCommentsHistory(ProcessInstId, "Requestor", "Requestor", Curr_Workstep, Prev_Workstep, Curr_Workstep, UserName, Comments);
                            String UpdateHistory = "Insert INTO WS_MAILAPPROVAL_HISTORY (PROCESS,PROCESSINSTID,USERNAME,WORKSTEP,COMPLETIONDATE,STATUS,ACTION,MODE,SequenceNo) values ('" + Process + "','" + ProcessInstId + "','" + UserName + "','" + Curr_Workstep + "',getDate(),'" + sMessage + "','" + Action + "','" + mode + "','" + sequenceId + "')";
                            CommonMethods.ExecuteUpdateQuery(UpdateHistory);
                            CommonMethods.GenLog("History Update-->> " + UpdateHistory, "T");
                        } else {
                            sMainCode = "5";
                            sMessage = "Error in CompleteWorkItem";
                            CommonMethods.GenLog("sMessage-->> " + sMessage, "T");
                            sType = "E";
                        }
                    }
                }

            } catch (Exception e) {
                CommonMethods.GenLog("LegalApproval Exception-->> " + e, "E");
            }

            xml.setMainCode(sMainCode);
            xml.setMessage(sMessage);
            xml.setType(sType);
        }
        return xml;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_XML)
    public void putXml(String content) {
    }

}
