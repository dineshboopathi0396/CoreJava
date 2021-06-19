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
 * File         : CCPApproval.java
 * Author       : Menonani
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
package com.newgen.ws.usl.CCP;

import com.newgen.ws.usl.CCP.*;
import com.newgen.wfdesktop.xmlapi.WFXmlResponse;
import com.newgen.ws.usl.CommonMethods;
import static com.newgen.ws.usl.CommonMethods.selectQuery;
import java.io.IOException;
import java.sql.SQLException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("CCP")
public class CCPApproval {

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public String getXml() {
        throw new UnsupportedOperationException();
    }

    @POST
    @Path("/action")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    public CCPOutputXml postXml(CCPInputXml ix) throws SQLException, ClassNotFoundException, IOException {
        CommonMethods call = new CommonMethods();
        CCPOutputXml xml = new CCPOutputXml();
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
                if(sequenceId.equalsIgnoreCase("")){
                    String seqid = selectQuery("SELECT NEXT VALUE FOR SeqPTP", 1);
                    WFXmlResponse xmlSeqResponse = new WFXmlResponse(seqid);
                    sequenceId = xmlSeqResponse.getVal("Value1");
                }
                //Validate User
                String sQuery = "Select AssignedUser from WFINSTRUMENTTABLE with(nolock) where PROCESSINSTANCEID='" + ProcessInstId + "'";
                CommonMethods.GenLog("Assigned User Query-->> " + sQuery, "T");
                String OutputXml = CommonMethods.selectQuery(sQuery, 1);
                xmlResponse = new WFXmlResponse(OutputXml);
                String AssignedUser = "";
                AssignedUser = xmlResponse.getVal("Value1");
                CommonMethods.GenLog("Assigned User-->> " + AssignedUser, "T");

                if (Action.equalsIgnoreCase("approve")) {
                    String searchWorkstep = "Select isnull(CURR_WORKSTEP,'')+'`'+isnull(PREV_WORKSTEP,'')+'`'+isnull(ResearchType,'') from EXT_CCP with(nolock) where PROCESSINSTID='" + ProcessInstId + "'";
                    CommonMethods.GenLog("CCP Search Workstep-->> " + searchWorkstep, "T");
                    String sOutput = CommonMethods.selectQuery(searchWorkstep, 1);
                    xmlResponse = new WFXmlResponse(sOutput);
                    String Workstep = "";
                    Workstep = xmlResponse.getVal("Value1");
                    CommonMethods.GenLog("Workstep-->> " + Workstep, "T");

                    String[] WorkstepData = Workstep.split("`");
                    String Prev_Workstep = "", Curr_Workstep = "",strResearchType="";
                    if (WorkstepData.length > 0) {
                        Curr_Workstep = WorkstepData[0];
                        Prev_Workstep = WorkstepData[1];
                        strResearchType= WorkstepData[2];
                    }
                    CommonMethods.GenLog("Curr_Workstep-->> " + Curr_Workstep, "T");
                    CommonMethods.GenLog("Prev_Workstep-->> " + Prev_Workstep, "T");
                    CommonMethods.GenLog("ResearchType-->> " + strResearchType, "T");
                    String strCCPQuery = "UPDATE EXT_CCP SET ACTIONITEM='' where PROCESSINSTID='" + ProcessInstId + "'";
                    CommonMethods.GenLog("EXTERNAL Update-->> " + strCCPQuery, "T");
                    CommonMethods.ExecuteUpdateQuery(strCCPQuery);
                    String CCPUpdateQuery="",strCountQuery="",strCountUpdateQry="";
                    if (Curr_Workstep.equalsIgnoreCase("LMApproval") || Curr_Workstep.equalsIgnoreCase("CPTeamApproval") || Curr_Workstep.equalsIgnoreCase("PortfolioHeadApproval") || Curr_Workstep.equalsIgnoreCase("CPIHeadApproval")) {
                        CommonMethods.GenLog("Inside CCP "+Curr_Workstep+"", "T");
                        if (call.ReassignWorkitem(ProcessInstId, CommonMethods.UserName, AssignedUser)) {
                            if (Curr_Workstep.equalsIgnoreCase("LMApproval")) {
                                CCPUpdateQuery = "UPDATE EXT_CCP SET ACTIONITEM='Submit',Comments='" + Comments + "',APPROVALEDATE=getDate() where PROCESSINSTID='" + ProcessInstId + "'";
                            } else if (Curr_Workstep.equalsIgnoreCase("CPTeamApproval")) {
                                CCPUpdateQuery = "UPDATE EXT_CCP SET ACTIONITEM='Submit',Comments='" + Comments + "',CCPCompleteEdate=getDate() where PROCESSINSTID='" + ProcessInstId + "'";
                            } else if (Curr_Workstep.equalsIgnoreCase("PortfolioHeadApproval")) {
                                CCPUpdateQuery = "UPDATE EXT_CCP SET ACTIONITEM='Submit',Comments='" + Comments + "',PFHeadApprovalEdate=getDate() where PROCESSINSTID='" + ProcessInstId + "'";
                            } else if (Curr_Workstep.equalsIgnoreCase("CPIHeadApproval")) {
                                CCPUpdateQuery = "UPDATE EXT_CCP SET ACTIONITEM='Submit',Comments='" + Comments + "',CPIHeadApprovalEdate=getDate() where PROCESSINSTID='" + ProcessInstId + "'";
                            }
                            
                            CommonMethods.GenLog("EXTERNAL Update-->> " + CCPUpdateQuery, "T");
                            CommonMethods.ExecuteUpdateQuery(CCPUpdateQuery);
                            boolean Flag = call.CompleteWorkitem(ProcessInstId, "1");
                            CommonMethods.GenLog("CompleteWorkitem Flag-->> " + Flag, "T");
                            if (Flag) {
                                sMainCode = "0";
                                sMessage = "WorkItem Completed Successfully";
                                CommonMethods.GenLog("sMessage-->> " + sMessage, "T");
                                sType = "S";
                                if (Curr_Workstep.equalsIgnoreCase("LMApproval")) {
                                    CommonMethods.InsertCommentsHistory(ProcessInstId, "CPTeamApproval", "Approve", Curr_Workstep, Prev_Workstep, Curr_Workstep, UserName, Comments);
                                } else if (Curr_Workstep.equalsIgnoreCase("CPTeamApproval")) {
                                    if (strResearchType.equalsIgnoreCase("CCP")) {
                                        CommonMethods.InsertCommentsHistory(ProcessInstId, "Closure", "Approve", Curr_Workstep, Prev_Workstep, Curr_Workstep, UserName, Comments);
                                    } else if (strResearchType.equalsIgnoreCase("Research Brief")) {
                                        CommonMethods.InsertCommentsHistory(ProcessInstId, "PortfolioHeadApproval", "Approve", Curr_Workstep, Prev_Workstep, Curr_Workstep, UserName, Comments);
                                    }
                                } else if (Curr_Workstep.equalsIgnoreCase("PortfolioHeadApproval")) {
                                    CommonMethods.InsertCommentsHistory(ProcessInstId, "CPIHeadApproval", "Approve", Curr_Workstep, Prev_Workstep, Curr_Workstep, UserName, Comments);
                                } else if (Curr_Workstep.equalsIgnoreCase("CPIHeadApproval")) {
                                    CommonMethods.InsertCommentsHistory(ProcessInstId, "Exit", "Approve", Curr_Workstep, Prev_Workstep, Curr_Workstep, UserName, Comments);
                                }
                                try {
                                    if (Curr_Workstep.equalsIgnoreCase("LMApproval")|| Curr_Workstep.equalsIgnoreCase("PortfolioHeadApproval") ||(Curr_Workstep.equalsIgnoreCase("CPTeamApproval") && strResearchType.equalsIgnoreCase("Research Brief"))) {
                                        CommonMethods.setCurrentLoopCount(ProcessInstId);
                                        CommonMethods.mailTrigger_CCPApproval(ProcessInstId, Comments,Curr_Workstep,Action);
                                    }
                                    else if((Curr_Workstep.equalsIgnoreCase("CPTeamApproval") && strResearchType.equalsIgnoreCase("CCP")) || Curr_Workstep.equalsIgnoreCase("CPIHeadApproval")){
                                        CommonMethods.setCurrentLoopCount(ProcessInstId);
                                        CommonMethods.mailTrigger_CCPApproval(ProcessInstId, Comments,Curr_Workstep,"Closure");
                                    }
                                    
                                } catch (Exception e) {
                                    CommonMethods.GenLog("Exception in mailTrigger_CCP Approval-->> " + e, "E");
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
                            sMainCode = "26";
                            sMessage = "Error in ReassignWorkItem";
                            CommonMethods.GenLog("sMessage-->> " + sMessage, "T");
                            sType = "E";
                        }

                    } 

                } else if (Action.equalsIgnoreCase("reject")) {
                    String searchWorkstep = "Select CURR_WORKSTEP+'`'+PREV_WORKSTEP from EXT_CCP with(nolock) where PROCESSINSTID='" + ProcessInstId + "'";
                    CommonMethods.GenLog("searchWorkstep-->> " + searchWorkstep, "T");
                    String sOutput = CommonMethods.selectQuery(searchWorkstep, 1);
                    xmlResponse = new WFXmlResponse(sOutput);
                    String Workstep = "";
                    Workstep = xmlResponse.getVal("Value1");
                    CommonMethods.GenLog("Workstep-->> " + Workstep, "T");
                    String strCountQuery="",strCountUpdateQry="";
                    String[] WorkstepData = Workstep.split("`");
                    String Prev_Workstep = "", Curr_Workstep = "";
                    if (WorkstepData.length > 0) {
                        Curr_Workstep = WorkstepData[0];
                        Prev_Workstep = WorkstepData[1];
                    }
                    CommonMethods.GenLog("Curr_Workstep-->> " + Curr_Workstep, "T");
                    CommonMethods.GenLog("Prev_Workstep-->> " + Prev_Workstep, "T");
                    String strCCPQuery = "UPDATE EXT_CCP SET ACTIONITEM='' where PROCESSINSTID='" + ProcessInstId + "'";
                    String CCPUpdateQuery="";
                    CommonMethods.GenLog("EXTERNAL Update-->> " + strCCPQuery, "T");
                    CommonMethods.ExecuteUpdateQuery(strCCPQuery);
                    
                    if (Curr_Workstep.equalsIgnoreCase("LMApproval") || Curr_Workstep.equalsIgnoreCase("CPTeamApproval") || Curr_Workstep.equalsIgnoreCase("PortfolioHeadApproval") || Curr_Workstep.equalsIgnoreCase("CPIHeadApproval")) {
                        CommonMethods.GenLog("Inside CCP "+Curr_Workstep+"", "T");
                        if (call.ReassignWorkitem(ProcessInstId, CommonMethods.UserName, AssignedUser)) {
                            if (Curr_Workstep.equalsIgnoreCase("LMApproval")) {
                                CCPUpdateQuery = "UPDATE EXT_CCP SET ACTIONITEM='Rework',Comments='" + Comments + "',APPROVALEDATE=getDate() where PROCESSINSTID='" + ProcessInstId + "'";
                            } else if (Curr_Workstep.equalsIgnoreCase("CPTeamApproval")) {
                                CCPUpdateQuery = "UPDATE EXT_CCP SET ACTIONITEM='Rework',Comments='" + Comments + "',CCPCompleteEdate=getDate() where PROCESSINSTID='" + ProcessInstId + "'";
                            } else if (Curr_Workstep.equalsIgnoreCase("PortfolioHeadApproval")) {
                                CCPUpdateQuery = "UPDATE EXT_CCP SET ACTIONITEM='Rework',Comments='" + Comments + "',PFHeadApprovalEdate=getDate() where PROCESSINSTID='" + ProcessInstId + "'";
                            } else if (Curr_Workstep.equalsIgnoreCase("CPIHeadApproval")) {
                                CCPUpdateQuery = "UPDATE EXT_CCP SET ACTIONITEM='Rework',Comments='" + Comments + "',CPIHeadApprovalEdate=getDate() where PROCESSINSTID='" + ProcessInstId + "'";
                            }
                            
                            
                            CommonMethods.GenLog("EXTERNAL Update-->> " + CCPUpdateQuery, "T");
                            CommonMethods.ExecuteUpdateQuery(CCPUpdateQuery);
                            boolean Flag = call.CompleteWorkitem(ProcessInstId, "1");
                            CommonMethods.GenLog("CompleteWorkitem Flag-->> " + Flag, "T");
                            if (Flag) {
                                sMainCode = "0";
                                sMessage = "WorkItem Completed Successfully";
                                CommonMethods.GenLog("sMessage-->> " + sMessage, "T");
                                sType = "S";
                                CommonMethods.InsertCommentsHistory(ProcessInstId, "Requestor", "Reject", Curr_Workstep, Prev_Workstep, Curr_Workstep, UserName, Comments);
                                String UpdateHistory = "Insert INTO WS_MAILAPPROVAL_HISTORY (PROCESS,PROCESSINSTID,USERNAME,WORKSTEP,COMPLETIONDATE,STATUS,ACTION,MODE,SequenceNo) values ('" + Process + "','" + ProcessInstId + "','" + UserName + "','" + Curr_Workstep + "',getDate(),'" + sMessage + "','" + Action + "','" + mode + "','" + sequenceId + "')";
                                CommonMethods.ExecuteUpdateQuery(UpdateHistory);
                                CommonMethods.setCurrentLoopCount(ProcessInstId);
                                CommonMethods.mailTrigger_CCPApproval(ProcessInstId, Comments,Curr_Workstep,Action);
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
                }

            } catch (Exception e) {
                CommonMethods.GenLog("CCP LMApproval Exception-->> " + e, "E");
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
