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
 * File         : OTCApproval.java
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
package com.newgen.ws.usl.OTCBRZR;

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

@Path("OTCBRZR")
public class OTCBRZRApproval {

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
    public OTCBRZROutputXml postXml(OTCBRZRInputXml ix) throws SQLException, ClassNotFoundException, IOException {
        CommonMethods call = new CommonMethods();
        OTCBRZROutputXml xml = new OTCBRZROutputXml();
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
                    //fetch Current & Previous Workstep
                    String searchWorkstep = "Select CURR_WORKSTEP+'`'+PREV_WORKSTEP+'`' from EXT_CustomerDispute with(nolock) where PROCESSINSTID='" + ProcessInstId + "'";
                    CommonMethods.GenLog("searchWorkstep-->> " + searchWorkstep, "T");
                    String sOutput = CommonMethods.selectQuery(searchWorkstep, 1);
                    xmlResponse = new WFXmlResponse(sOutput);
                    String Workstep = "";
                    Workstep = xmlResponse.getVal("Value1");
                    CommonMethods.GenLog("Workstep-->> " + Workstep, "T");

                    String[] WorkstepData = Workstep.split("`");
                    String Prev_Workstep = "", Curr_Workstep = "", CurrentLoop = "", TotalLoop = "";
                    if (WorkstepData.length==2) {
                        Curr_Workstep = WorkstepData[0];
                        Prev_Workstep = WorkstepData[1];
                        

                        CommonMethods.GenLog("Inside OTC BRZR Approval", "T");
                        if (call.ReassignWorkitem(ProcessInstId, CommonMethods.UserName, AssignedUser)) {
//                            String Query = "";
                          
                            String Query = "UPDATE EXT_CustomerDispute SET ACTIONITEM='Submit',Comments='" + Comments + "',APPROVALEDATE=getDate() where PROCESSINSTID='" + ProcessInstId + "'";
                            CommonMethods.GenLog("EXTERNAL Update-->> " + Query, "T");
                            CommonMethods.ExecuteUpdateQuery(Query);

                            boolean Flag = call.CompleteWorkitem(ProcessInstId, "1");
                            CommonMethods.GenLog("CompleteWorkitem Flag-->> " + Flag, "T");
                            if (Flag) {
                                sMainCode = "0";
                                sMessage = "WorkItem Completed Successfully";
                                CommonMethods.GenLog("sMessage-->> " + sMessage, "T");
                                sType = "S";
//                                try {
//                                    CommonMethods.mailTrigger_OTC(ProcessInstId, Comments);
//                                } catch (Exception e) {
//                                    CommonMethods.GenLog("Exception in mailTrigger_OTC-->> " + e, "E");
//                                }
                                String UpdateHistory = "Insert INTO WS_MAILAPPROVAL_HISTORY (PROCESS,PROCESSINSTID,USERNAME,WORKSTEP,COMPLETIONDATE,STATUS,ACTION,MODE,SequenceNo) values ('" + Process + "','" + ProcessInstId + "','" + UserName + "','" + Curr_Workstep + "',getDate(),'" + sMessage + "','" + Action + "','" + mode + "','" + sequenceId + "')";
                                CommonMethods.GenLog("History Update-->> " + UpdateHistory, "T");
                                CommonMethods.ExecuteUpdateQuery(UpdateHistory);
                                   CommonMethods.InsertCommentsHistory(ProcessInstId, "BRZR_Posting", "Submit", Curr_Workstep, Prev_Workstep, "BRZR_Approval", UserName, Comments);
                                

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
                    } else {
                        sMainCode = "103";
                        sMessage = "Error in fetching workitem data";
                        CommonMethods.GenLog("sMessage-->> " + sMessage, "T");
                        sType = "E";
                        CommonMethods.GenLog("Error in fetching workitem data " + ProcessInstId, "E");
                    }
                } else if (Action.equalsIgnoreCase("reject")) {
                    //fetch Current & Previous Workstep
                    String searchWorkstep = "Select CURR_WORKSTEP+'`'+PREV_WORKSTEP+'`' from EXT_CustomerDispute with(nolock) where PROCESSINSTID='" + ProcessInstId + "'";
                    CommonMethods.GenLog("searchWorkstep-->> " + searchWorkstep, "T");
                    String sOutput = CommonMethods.selectQuery(searchWorkstep, 1);
                    xmlResponse = new WFXmlResponse(sOutput);
                    String Workstep = "";
                    Workstep = xmlResponse.getVal("Value1");
                    CommonMethods.GenLog("Workstep-->> " + Workstep, "T");

                    String[] WorkstepData = Workstep.split("`");
                    String Prev_Workstep = "", Curr_Workstep = "", CurrentLoop = "", TotalLoop = "";
                    if (WorkstepData.length==2) {
                        Curr_Workstep = WorkstepData[0];
                        Prev_Workstep = WorkstepData[1];
                        

                        CommonMethods.GenLog("Inside OTC BRZR Reject", "T");
                        if (call.ReassignWorkitem(ProcessInstId, CommonMethods.UserName, AssignedUser)) {
//                            String Query = "";
                          
                            String Query = "UPDATE EXT_CustomerDispute SET ACTIONITEM='Reject',Comments='" + Comments + "',APPROVALEDATE=getDate() where PROCESSINSTID='" + ProcessInstId + "'";
                            CommonMethods.GenLog("EXTERNAL Update-->> " + Query, "T");
                            CommonMethods.ExecuteUpdateQuery(Query);

                            boolean Flag = call.CompleteWorkitem(ProcessInstId, "1");
                            CommonMethods.GenLog("CompleteWorkitem Flag-->> " + Flag, "T");
                            if (Flag) {
                                sMainCode = "0";
                                sMessage = "WorkItem Completed Successfully";
                                CommonMethods.GenLog("sMessage-->> " + sMessage, "T");
                                sType = "S";
//                                try {
//                                    CommonMethods.mailTrigger_OTC(ProcessInstId, Comments);
//                                } catch (Exception e) {
//                                    CommonMethods.GenLog("Exception in mailTrigger_OTC-->> " + e, "E");
//                                }
                                String UpdateHistory = "Insert INTO WS_MAILAPPROVAL_HISTORY (PROCESS,PROCESSINSTID,USERNAME,WORKSTEP,COMPLETIONDATE,STATUS,ACTION,MODE,SequenceNo) values ('" + Process + "','" + ProcessInstId + "','" + UserName + "','" + Curr_Workstep + "',getDate(),'" + sMessage + "','" + Action + "','" + mode + "','" + sequenceId + "')";
                                CommonMethods.GenLog("History Update-->> " + UpdateHistory, "T");
                                CommonMethods.ExecuteUpdateQuery(UpdateHistory);
                                   CommonMethods.InsertCommentsHistory(ProcessInstId, "BRZR_Requestor", "Reject", Curr_Workstep, Prev_Workstep, "BRZR_Approval", UserName, Comments);
                                   //CommonMethods.mailTrigger_Requestor(ProcessInstId);

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
                    } else {
                        sMainCode = "103";
                        sMessage = "Error in fetching workitem data";
                        CommonMethods.GenLog("sMessage-->> " + sMessage, "T");
                        sType = "E";
                        CommonMethods.GenLog("Error in fetching workitem data " + ProcessInstId, "E");
                    }
                }

            } catch (Exception e) {
                CommonMethods.GenLog("OTCApproval Exception-->> " + e, "E");
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
