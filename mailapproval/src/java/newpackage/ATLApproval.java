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
 * File         : RTRApproval.java
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
package com.newgen.ws.usl.atl;

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

@Path("ATL")
public class ATLApproval {

    String AssignedUser = "";

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/action")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public ATLOutputXml postXml(ATLInputXml ix) throws SQLException, ClassNotFoundException, IOException {
        CommonMethods call = new CommonMethods();
        ATLOutputXml xml = new ATLOutputXml();
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

                AssignedUser = xmlResponse.getVal("Value1");
                CommonMethods.GenLog("Assigned User-->> " + AssignedUser, "T");

                if (Action.equalsIgnoreCase("approve")) {
                    String searchWorkstep = "Select isnull(CURR_WORKSTEP,'')+'`'+isnull(PREV_WORKSTEP,'') from EXT_ATL with(nolock) where PROCESSINSTID='" + ProcessInstId + "'";
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

                    CommonMethods.GenLog("Inside ATL RO_Approval", "T");
                    if (call.ReassignWorkitem(ProcessInstId, CommonMethods.UserName, AssignedUser)) {
                        String sQry = "Select isnull(JournalType,'')+'`'+isnull(CURR_APPROVERCOUNT,'')+'`'+isnull(SA_CurrentLoop,'') FROM EXT_ATL WITH(NOLOCK) WHERE PROCESSINSTID='" + ProcessInstId + "'";
                        CommonMethods.GenLog("fetchWorkitem sQry-->> " + sQry, "T");
                        String fetchXml = CommonMethods.selectQuery(sQry, 1);
                        xmlResponse = new WFXmlResponse(fetchXml);
                        String workItemData = "";
                        workItemData = xmlResponse.getVal("Value1");

                        //Fetch Compliance flag  
                        String sComplianceQry = "Select FLAG_COMPLIANCE FROM EXT_ATL WITH(NOLOCK) WHERE PROCESSINSTID='" + ProcessInstId + "'";
                        CommonMethods.GenLog("sComplianceQry -->> " + sComplianceQry, "T");
                        String fetchCompliance = CommonMethods.selectQuery(sQry, 1);
                        xmlResponse = new WFXmlResponse(fetchCompliance);
                        String complianceFlag = xmlResponse.getVal("Value1");

                        String[] workItemFetch = workItemData.split("`");
                        if (workItemFetch.length > 1) {
                            String PostingFlag = "";
                            if (workItemFetch[0].equalsIgnoreCase("Mass Upload") || workItemFetch[0].equalsIgnoreCase("InterCompany Recharge")) {
                                PostingFlag = "Manual";
                            } else {
                                PostingFlag = "Auto";
                            }
                            String approverCount = workItemFetch[1];
                            String currentLoop = workItemFetch[2];

                            String Query = "UPDATE EXT_ATL SET ACTIONITEM='Approve',Comments='" + Comments + "',APPROVALEDATE=getDate() where PROCESSINSTID='" + ProcessInstId + "'";
                            CommonMethods.GenLog("EXTERNAL Update-->> " + Query, "T");
                            CommonMethods.ExecuteUpdateQuery(Query);

                            boolean Flag = call.CompleteWorkitem(ProcessInstId, "1");
                            CommonMethods.GenLog("CompleteWorkitem Flag-->> " + Flag, "T");
                            if (Flag) {
                                sMainCode = "0";
                                sMessage = "WorkItem Completed Successfully";
                                CommonMethods.GenLog("sMessage-->> " + sMessage, "T");
                                sType = "S";
                                if (approverCount.equals("1")) {
                                    if (PostingFlag.equalsIgnoreCase("Manual")) {
                                        CommonMethods.InsertCommentsHistory(ProcessInstId, "ManualPosting", "ManualPosting", Curr_Workstep, Prev_Workstep, Curr_Workstep, UserName, Comments);
                                    } else {
                                        if (complianceFlag.equalsIgnoreCase("true")) {
                                            CommonMethods.InsertCommentsHistory(ProcessInstId, "Compliance", "Compliance", Curr_Workstep, Prev_Workstep, Curr_Workstep, UserName, Comments);
                                        } else {
                                            CommonMethods.InsertCommentsHistory(ProcessInstId, "Posting", "Posting", Curr_Workstep, Prev_Workstep, Curr_Workstep, UserName, Comments);
                                        }
                                    }
                                } else {
                                    if (currentLoop.equals("1")) {
                                        String fetchApproval = "SELECT isnull(APPROVER2,'')+'`'+isnull(APPROVER2EMAIL,'') from EXT_ATL with(nolock) WHERE PROCESSINSTID='" + ProcessInstId + "'";
                                        CommonMethods.GenLog("fetchApproval -->> " + fetchApproval, "T");
                                        String fetchApprovalXml = CommonMethods.selectQuery(fetchApproval, 1);
                                        xmlResponse = new WFXmlResponse(fetchApprovalXml);
                                        String approvalData = xmlResponse.getVal("Value1");
                                        String[] approvalArr = approvalData.split("`");
                                        if (approvalArr.length > 0) {
                                            String Approver2 = approvalArr[0];
                                            String Approver2Mail = approvalArr[1];
                                            try {
                                                CommonMethods.mailTrigger_ATL(ProcessInstId, Approver2Mail, AssignedUser, Approver2, Comments);
                                            } catch (Exception e) {
                                                CommonMethods.GenLog("Exception in ATL mailtrigger" + e, "E");
                                            }
                                        }
                                        CommonMethods.InsertCommentsHistory(ProcessInstId, "Approval", "Approval", Curr_Workstep, Prev_Workstep, Curr_Workstep, UserName, Comments);
                                    } else {
                                        if (PostingFlag.equalsIgnoreCase("Manual")) {
                                            CommonMethods.InsertCommentsHistory(ProcessInstId, "ManualPosting", "ManualPosting", Curr_Workstep, Prev_Workstep, Curr_Workstep, UserName, Comments);
                                        } else {
                                            if (complianceFlag.equalsIgnoreCase("true")) {
                                                CommonMethods.InsertCommentsHistory(ProcessInstId, "Compliance", "Compliance", Curr_Workstep, Prev_Workstep, Curr_Workstep, UserName, Comments);
                                            } else {
                                                CommonMethods.InsertCommentsHistory(ProcessInstId, "Posting", "Posting", Curr_Workstep, Prev_Workstep, Curr_Workstep, UserName, Comments);
                                            }
                                        }
                                    }
                                }

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
                            sMainCode = "63";
                            sMessage = "Error in fetching data from EXT_ATL";
                            CommonMethods.GenLog("sMessage-->> " + sMessage, "T");
                            sType = "E";
                        }
                    } else {
                        sMainCode = "26";
                        sMessage = "Error in ReassignWorkItem";
                        CommonMethods.GenLog("sMessage-->> " + sMessage, "T");
                        sType = "E";
                    }

                } else if (Action.equalsIgnoreCase("reject")) {
                    String searchWorkstep = "Select isnull(CURR_WORKSTEP,'')+'`'+isnull(PREV_WORKSTEP,'') from EXT_ATL with(nolock) where PROCESSINSTID='" + ProcessInstId + "'";
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

                    CommonMethods.GenLog("Inside ATL Reject", "T");
                    if (call.ReassignWorkitem(ProcessInstId, CommonMethods.UserName, AssignedUser)) {
                        String Query = "UPDATE EXT_ATL SET ACTIONITEM='Reject',Comments='" + Comments + "',REJECTSDATE=getDate() where PROCESSINSTID='" + ProcessInstId + "'";
                        CommonMethods.GenLog("EXTERNAL Update-->> " + Query, "T");
                        CommonMethods.ExecuteUpdateQuery(Query);
                        boolean Flag = call.CompleteWorkitem(ProcessInstId, "1");
                        CommonMethods.GenLog("CompleteWorkitem Flag-->> " + Flag, "T");
                        if (Flag) {
                            sMainCode = "0";
                            sMessage = "WorkItem Completed Successfully";
                            CommonMethods.GenLog("sMessage-->> " + sMessage, "T");
                            sType = "S";
                            CommonMethods.InsertCommentsHistory(ProcessInstId, "Reject", "Reject", Curr_Workstep, Prev_Workstep, Curr_Workstep, UserName, Comments);
                            String UpdateHistory = "Insert INTO WS_MAILAPPROVAL_HISTORY (PROCESS,PROCESSINSTID,USERNAME,WORKSTEP,COMPLETIONDATE,STATUS,ACTION,Mode,SequenceNo) values ('" + Process + "','" + ProcessInstId + "','" + UserName + "','" + Curr_Workstep + "',getDate(),'" + sMessage + "','" + Action + "','" + mode + "','" + sequenceId + "')";
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

                }

            } catch (Exception e) {
                CommonMethods.GenLog("ATLApproval Exception-->> " + e, "E");
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
