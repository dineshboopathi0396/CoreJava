package com.newgen.ws.usl;

import com.newgen.wfdesktop.xmlapi.WFCallBroker;
import com.newgen.wfdesktop.xmlapi.WFXmlList;
import com.newgen.wfdesktop.xmlapi.WFXmlResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class CommonMethods {
    static String IPAddress = "";
    static String Port = "";
    static String CabinetName = "";
    public static String UserName = "";
    static String Password = "";
    static String DiageoLogo = "";
    static String ApproveLogo = "";
    static String PendingLogo = "";
    static String GenLogPath = "";
    static String SessionId = "";
    static String url = "";

    public boolean ReadIni() {
        try {
            FileInputStream fisInput = new FileInputStream(System.getProperty("user.dir") + File.separator + "PropertyFiles" + File.separator + "mailApproval.ini");
            Properties prop = new Properties();
            prop.load(fisInput);
            url = prop.getProperty("URL");
            IPAddress = prop.getProperty("JBossIpAdress");

            if (url.equalsIgnoreCase("")) {
                GenLog("URL not found in ini file", "E");
                return false;
            }
            if (IPAddress.equalsIgnoreCase("")) {
                GenLog("IPAddress not found in ini file", "E");
                return false;
            }
            Port = prop.getProperty("Port");
            if (Port.equalsIgnoreCase("")) {
                GenLog("Port not found in ini file", "E");
                return false;
            }
            CabinetName = prop.getProperty("CabinetName");
            if (CabinetName.equalsIgnoreCase("")) {
                GenLog("CabinetName not found in ini file", "E");
                return false;
            }
            UserName = prop.getProperty("UserName");
            if (UserName.equalsIgnoreCase("")) {
                GenLog("UserName not found in ini file", "E");
                return false;
            }
            Password = prop.getProperty("Password");
            if (Password.equalsIgnoreCase("")) {
                GenLog("Password not found in ini file", "E");
                return false;
            }
            GenLogPath = prop.getProperty("GenLogPath");
            if (GenLogPath.equalsIgnoreCase("")) {
                GenLog("GenLogPath not found in ini file", "E");
                return false;
            }
            DiageoLogo = prop.getProperty("DiageoLogo");
            if (DiageoLogo.equalsIgnoreCase("")) {
                GenLog("DiageoLogo PATH not found in ini file", "E");
                return false;
            }
            ApproveLogo = prop.getProperty("ApproveLogo");
            if (ApproveLogo.equalsIgnoreCase("")) {
                GenLog("ApproveLogo PATH not found in ini file", "E");
                return false;
            }
            PendingLogo = prop.getProperty("PendingLogo");
            if (PendingLogo.equalsIgnoreCase("")) {
                GenLog("PendingLogo PATH not found in ini file", "E");
                return false;
            }

        } catch (FileNotFoundException e) {
            GenLog("mailApproval.ini file not found-->>" + e, "E");
            System.out.println("mailApproval.ini file not found-->>" + e);
        } catch (IOException e) {
            GenLog("mailApproval.ini file not found-->>" + e, "E");
            System.out.println("mailApproval.ini file not found-->>" + e);
        }
        return true;
    }

    public String connectCabinet() {
        StringBuffer strBuffer = new StringBuffer();
        try {
            strBuffer.append(" <?xml version=1.0?>\n");
            strBuffer.append("<NGOConnectCabinet_Input>\n");
            strBuffer.append("<Option>NGOConnectCabinet</Option>\n");
            strBuffer.append("<CabinetName>" + CabinetName + "</CabinetName>\n");
            strBuffer.append("<UserName>" + UserName + "</UserName>\n");
            strBuffer.append("<UserPassword>" + Password + "</UserPassword>");
            strBuffer.append("<CurrentDateTime></CurrentDateTime>");
            strBuffer.append("<UserExist>N</UserExist>");
            strBuffer.append("</NGOConnectCabinet_Input>");
            GenLog("connectCabinet Input xml :" + strBuffer.toString() + "\n", "X");
            GenLog("IP :" + IPAddress + " Port :" + Port + "\n", "T");
            String xmlout = WFCallBroker.execute(strBuffer.toString(), IPAddress, Integer.parseInt(Port), 0);
            GenLog("connectCabinet Output xml :" + xmlout + "\n", "X");
            WFXmlResponse xmlResponse = new WFXmlResponse(xmlout);
            if (xmlResponse.getVal("Status").equals("0")) {
                SessionId = xmlResponse.getVal(("UserDBId"));
                GenLog("\nSession ID of this connection: " + SessionId, "T");
                GenLog("Cabinet Connected Successfully" + "\n", "T");
                return SessionId;
            } else {
                GenLog("Cabinet not Connected Successfully" + "\n", "T");
                return "";
            }
        } catch (Exception e) {
            GenLog("Exception" + e.getMessage() + "\n", "E");
        }
        return "";
    }

    public static void DisconnectCabinet() {
        WFXmlResponse xmlResponse = null;
        String Xmlout = "";
        try {
            StringBuffer strBuffer = new StringBuffer();
            strBuffer.append("<?xml version='1.0'?>");
            strBuffer.append("<NGODisconnectCabinet_Input>");
            strBuffer.append("<Option>NGODisconnectCabinet</Option>");
            strBuffer.append("<CabinetName>" + CabinetName + "</CabinetName>");
            strBuffer.append("<UserDBId>" + SessionId + "</UserDBId>");
            strBuffer.append("</NGODisconnectCabinet_Input>");
            Xmlout = WFCallBroker.execute(strBuffer.toString(), IPAddress, Integer.parseInt(Port), 0);
            GenLog("DisconnectCabinet Input xml :" + strBuffer.toString() + "\n", "X");
            xmlResponse = new WFXmlResponse(Xmlout);
            GenLog("DisconnectCabinet Output xml :" + Xmlout + "\n", "X");
            if (xmlResponse.getVal("Status").equals("0")) {
                GenLog("Cabinet Disconnected Successfully .....", "T");
            } else {
                GenLog("Cabinet Disconnection Failed:" + xmlResponse.getVal("Error"), "E");
            }
        } catch (Exception e) {
            GenLog("\n\n### **[Error]** Exception in Disconnecting cabinet : " + e, "E");
        }
    }

    boolean lockWorkitem(String ProcessInstanceId, String WorkitemId) {
        String sOutputXml = null;
        WFXmlResponse xmlResponse = null;
        String sMainCode = null;
        StringBuffer strInputXml = new StringBuffer();
        try {
            strInputXml.append("<?xml version=\"1.0\"?>");
            strInputXml.append("<WMGetWorkItem_Input>");
            strInputXml.append("<Option>WMGetWorkItem</Option>");
            strInputXml.append("<EngineName>" + CabinetName + "</EngineName>");
            strInputXml.append("<SessionId>" + SessionId + "</SessionId>");
            strInputXml.append("<ProcessInstanceId>" + ProcessInstanceId + "</ProcessInstanceId>");
            strInputXml.append("<WorkItemId>1</WorkItemId>");
            strInputXml.append("</WMGetWorkItem_Input>");
            GenLog("lockWorkitem Input xml :" + strInputXml.toString() + "\n", "X");
            sOutputXml = WFCallBroker.execute(strInputXml.toString(), IPAddress, Integer.parseInt(Port), 0);
            GenLog("lockWorkitem Output xml :" + sOutputXml + "\n", "X");
            xmlResponse = new WFXmlResponse(sOutputXml);
            sMainCode = xmlResponse.getVal("MainCode");
            if (sMainCode.equals("0")) {
                GenLog("lockWorkitem Successful \n", "T");
                return true;
            } else {
                GenLog("For workitem ==" + ProcessInstanceId + " Problem locking workitem==" + xmlResponse.getVal("Error"), "E");
                return false;
            }
        } catch (Exception e) {
            GenLog("\n*** Exception In Function  lockWorkitem() " + e.getMessage(), "E");
            return false;
        } finally {
            if (sOutputXml != null) {
                sOutputXml = null;
            }

            if (sMainCode != null) {
                sMainCode = null;
            }
            if (strInputXml != null) {
                strInputXml = null;
            }
        }
    }

    public boolean ReassignWorkitem(String ProcessInstanceId, String TargetUser, String SourceUser) {
        String sOutputXml = null;
        WFXmlResponse xmlResponse = null;
        String sMainCode = null;
        StringBuffer strInputXml = new StringBuffer();
        try {
            strInputXml.append("<?xml version=\"1.0\"?>");
            strInputXml.append("<WMReassignWorkItem_Input>");
            strInputXml.append("<Option>WMReassignWorkItem</Option>");
            strInputXml.append("<EngineName>" + CabinetName + "</EngineName>");
            strInputXml.append("<SessionId>" + SessionId + "</SessionId>");
            strInputXml.append("<ProcessInstanceId>" + ProcessInstanceId + "</ProcessInstanceId>");
            strInputXml.append("<WorkItemId>1</WorkItemId>");
            strInputXml.append("<SourceUser>" + SourceUser + "</SourceUser>");
            strInputXml.append("<TargetUser>" + TargetUser + "</TargetUser>");
            strInputXml.append("<Comments>Self Assign</Comments>");
            strInputXml.append("<OpenMode>PM</OpenMode>");
            strInputXml.append("</WMReassignWorkItem_Input>");
            GenLog("ReAssignWorkItem InputXMl::" + strInputXml.toString(), "X");
            sOutputXml = WFCallBroker.execute(strInputXml.toString(), IPAddress, Integer.parseInt(Port), 0);
            GenLog("ReAssignWorkItem OutputXMl::" + sOutputXml, "X");
            xmlResponse = new WFXmlResponse(sOutputXml);
            sMainCode = xmlResponse.getVal("MainCode");
            if (sMainCode.equals("0")) {
                unlockWorkitem(ProcessInstanceId);
                GenLog("ReAssignWorkItem Successful..", "T");
                return true;
            } else {
                GenLog("For workitem ==" + ProcessInstanceId + " Problem Reassigning workitem==" + xmlResponse.getVal("Error"), "E");
                return false;
            }

        } catch (Exception e) {
            GenLog("\n*** Exception In Function  ReassignWorkitem() " + e.getMessage(), "E");
            return false;
        } finally {
            if (sOutputXml != null) {
                sOutputXml = null;
            }
            if (sMainCode != null) {
                sMainCode = null;
            }
            if (strInputXml != null) {
                strInputXml = null;
            }
        }

    }

    public boolean CompleteWorkitem(String strProcessInstanceId, String strWorkItemId) {

        WFXmlResponse xmlResponse = null;
        String Xmlout = "";
        if (lockWorkitem(strProcessInstanceId, "1")) {
            try {
                StringBuffer strInputXml = new StringBuffer();
                strInputXml.append("<?xml version=\"1.0\"?>");
                strInputXml.append("<WMCompleteWorkItem_Input>");
                strInputXml.append("<Option>WMCompleteWorkItem</Option>");
                strInputXml.append("<EngineName>" + CabinetName + "</EngineName>");
                strInputXml.append("<SessionId>" + SessionId + "</SessionId>");
                strInputXml.append("<ProcessInstanceId>" + strProcessInstanceId + "</ProcessInstanceId>");
                strInputXml.append("<WorkItemId>1</WorkItemId>");
                strInputXml.append("<AuditStatus></AuditStatus>");
                strInputXml.append("<Comments></Comments>");
                strInputXml.append("</WMCompleteWorkItem_Input>");
                GenLog("CompleteWorkitem InputXml:" + strInputXml.toString(), "X");
                Xmlout = WFCallBroker.execute(strInputXml.toString(), IPAddress, Integer.parseInt(Port), 0);
                GenLog("CompleteWorkitem OutputXml:" + Xmlout, "X");
                xmlResponse = new WFXmlResponse(Xmlout);
                if (xmlResponse.getVal("MainCode").equals("0")) {
                    GenLog("WorkItem Completed Successfully..", "T");
                    return true;
                } else {
                    unlockWorkitem(strProcessInstanceId);
                    GenLog("Error in Complete WorkItem:" + Xmlout, "E");
                    return false;
                }
            } catch (Exception e) {
                GenLog("\n\n### **[Error]** Exception in Complete WorkItem :" + e, "E");
                return false;
            }
        }
        return false;
    }

    boolean unlockWorkitem(String ProcessInstanceId) {

        String sOutputXml = null;
        WFXmlResponse xmlResponse = null;
        String sMainCode = null;
        StringBuffer strInputXml = new StringBuffer();

        try {
            strInputXml.append("<?xml version=\"1.0\"?>");
            strInputXml.append("<WMUnlockWorkItem_Input>");
            strInputXml.append("<Option>WMUnlockWorkItem</Option>");
            strInputXml.append("<EngineName>" + CabinetName + "</EngineName>");
            strInputXml.append("<SessionId>" + SessionId + "</SessionId>");
            strInputXml.append("<ProcessInstanceId>" + ProcessInstanceId + "</ProcessInstanceId>");
            strInputXml.append("<WorkItemId>1</WorkItemId>");
            strInputXml.append("</WMUnlockWorkItem_Input>");
            GenLog("UnlockWorkItem InputXml:: " + strInputXml.toString(), "X");
            sOutputXml = WFCallBroker.execute(strInputXml.toString(), IPAddress, Integer.parseInt(Port), 0);
            GenLog("UnlockWorkItem OutputXml:: " + sOutputXml, "X");
            xmlResponse = new WFXmlResponse(sOutputXml);

            sMainCode = xmlResponse.getVal("MainCode");
            if (sMainCode.equals("0")) {
                GenLog("Unlock workitem Successfull..", "T");
                return true;
            } else {
                GenLog("For workitem ==" + ProcessInstanceId + "Problem locking workitem==" + xmlResponse.getVal("Error"), "E");
                return false;
            }
        } catch (Exception e) {
            GenLog("\n*** Exception In Function  lockWorkitem() " + e.getMessage(), "E");
            return false;
        } finally {
            if (sOutputXml != null) {
                sOutputXml = null;
            }
            if (sMainCode != null) {
                sMainCode = null;
            }
            if (strInputXml != null) {
                strInputXml = null;
            }
        }
    }

    /*
     * ----------------------------------------------------------------------------------
     * Function Name : GenLog 
     * Description   : load current date
     * Return Value  : Null 
     * -----------------------------------------------------------------------------------
     */
    public static void GenLog(String strMsg, String logType) {

        java.util.Date dateHourFormat = new java.util.Date();
        SimpleDateFormat sDateHour = new SimpleDateFormat("dd-MM-yyyy HH");
        String strCurDateHour = sDateHour.format(dateHourFormat);

        java.util.Date dateTimeFormat = new java.util.Date();
        SimpleDateFormat sDateTime = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
        String strCurDateTime = sDateTime.format(dateTimeFormat);

        String strLogFilePath = "";

        if (logType.equalsIgnoreCase("E")) {
            strLogFilePath = GenLogPath + File.separator + "Error_" + strCurDateHour + ".log";
        } else if (logType.equalsIgnoreCase("X")) {
            strLogFilePath = GenLogPath + File.separator + "XML_" + strCurDateHour + ".log";
        } else if (logType.equalsIgnoreCase("T")) {
            strLogFilePath = GenLogPath + File.separator + "GenLog_" + strCurDateHour + ".log";
        } else {
            strLogFilePath = GenLogPath + File.separator + "TransactionLog_" + strCurDateHour + ".log";
        }

        try {
            File dir = new File(GenLogPath);
            if (dir.exists() == false) {
                dir.mkdirs();
            }
            File file = new File(strLogFilePath);
            if (file.exists() == false) {
                file.createNewFile();
            }
            RandomAccessFile logFile = new RandomAccessFile(file, "rw");
            logFile.seek(file.length());
            logFile.writeBytes(strCurDateTime + " : " + strMsg + "\r\n");
            logFile.close();
        } catch (IOException e) {
            System.out.println("Error in GenLog generation for mailapproval::" + strCurDateTime + "---" + e.getMessage());
        }
    }

    public static String selectQuery(String sQuery, int iColCount) {

        GenLog("Inside Select Query : " + sQuery + "\n" + iColCount + "\n" + IPAddress + "\n" + CabinetName + "\n" + SessionId + "\n", "T");
        String strInputXml = "";
        String strOutputXml = "";
        try {
            strInputXml = strInputXml + ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            strInputXml = strInputXml + ("<IGGetData>");
            strInputXml = strInputXml + ("<Option>IGGetData</Option>");
            strInputXml = strInputXml + ("<EngineName>") + CabinetName + ("</EngineName>");
            strInputXml = strInputXml + ("<SessionId>");
            strInputXml = strInputXml + (SessionId);
            strInputXml = strInputXml + ("</SessionId>");
            strInputXml = strInputXml + ("<QueryString>");
            strInputXml = strInputXml + (sQuery);
            strInputXml = strInputXml + ("</QueryString>");
            strInputXml = strInputXml + ("<ColumnNo>") + iColCount + ("</ColumnNo>");
            strInputXml = strInputXml + ("</IGGetData>");
            GenLog("Input XML for Select Query : " + strInputXml + "\n", "X");
            strInputXml = strInputXml.replace("&", "&amp;");
            strOutputXml = WFCallBroker.execute(strInputXml, IPAddress, Integer.parseInt(Port), 0);
            GenLog("strOutputXml::" + strOutputXml + "\n", "X");
            strOutputXml = strOutputXml.replaceAll("DataList Columns=\"" + iColCount + "\"", "DataList");

        } catch (Exception ex) {
            GenLog("Exception in Executing select query : " + ex.getMessage() + "\n", "E");
            return null;
        }
        return strOutputXml;
    }

    public static void ExecuteUpdateQuery(String Query) {
        String Xmlout = "";
        try {
            StringBuffer strBuffer = new StringBuffer();
            strBuffer.append("<?xml version='1.0'?>");
            strBuffer.append("<WFCustomBean_Input>");
            strBuffer.append("<Option>NGSetData</Option><QryOption>NGSetData</QryOption>");
            strBuffer.append("<EngineName>").append(CabinetName).append("</EngineName>");
            strBuffer.append("<SessionId>").append(SessionId).append("</SessionId>");
            strBuffer.append("<Query>").append(Query).append("</Query>");
            strBuffer.append("<QueryCount>1</QueryCount>");
            strBuffer.append("</WFCustomBean_Input>");
            String sInput = strBuffer.toString();
            sInput = sInput.replace("&", "&amp;");
            GenLog("ExecuteUpdateQuery InputXml:" + sInput, "X");
            Xmlout = WFCallBroker.execute(sInput, "127.0.0.1", Integer.parseInt(Port), 0);
            GenLog("ExecuteUpdateQuery OutputXml:" + Xmlout, "X");
            WFXmlResponse xmlResponse = new WFXmlResponse(Xmlout);
            if (xmlResponse.getVal("MainCode").equals("0")) {
                GenLog("Update Query Executed Successfully..", "T");
            } else {
                GenLog("Update Query Execution failed for : " + Query, "E");
            }
        } catch (Exception e) {
            GenLog("Exception in ExecuteUpdateQuery: " + e, "E");
        }
    }

    /*
     * ----------------------------------------------------------------------------------
     * Function Name :InsertCommentsHistory 
     * Description   :Insert Required values for showing Commnets History
     * Return Value : NULL  
     * -----------------------------------------------------------------------------------
     */
    public static void InsertCommentsHistory(String PId, String ToQueueName, String Decision, String Curr_workstep, String Prev_workstep, String strActivityName, String strusername, String Comments) {

        try {
            if (!Comments.equalsIgnoreCase("")) {
                String strQuery = "";
                GenLog("PId :" + PId, "T");
                if (PId.substring(0, PId.indexOf("-")).equalsIgnoreCase("VM")) {
                    strQuery = "Insert into CommentsHistory_VM (ProcessInstID,QueueName,UserName,Comments,ProcessedDate,To_QueueName,Decision) values('" + PId + "','" + strActivityName + "','" + strusername + "','" + Comments + "',getDate(),'" + ToQueueName + "','" + Decision + "')";
                } else if (PId.substring(0, PId.indexOf("-")).equalsIgnoreCase("PTP")) {
                    strQuery = "Insert into CommentsHistory_PTP (ProcessInstID,QueueName,UserName,Comments,ProcessedDate,To_QueueName,Decision) values('" + PId + "','" + strActivityName + "','" + strusername + "','" + Comments + "',getDate(),'" + ToQueueName + "','" + Decision + "')";
                } else if (PId.substring(0, PId.indexOf("-")).equalsIgnoreCase("RTR")) {
                    strQuery = "Insert into CommentsHistory_RTR (ProcessInstID,QueueName,UserName,Comments,ProcessedDate,To_QueueName,Decision) values('" + PId + "','" + strActivityName + "','" + strusername + "','" + Comments + "',getDate(),'" + ToQueueName + "','" + Decision + "')";
                } else if (PId.substring(0, PId.indexOf("-")).equalsIgnoreCase("OTC")) {
                    strQuery = "Insert into CommentsHistory_O2C (ProcessInstID,QueueName,UserName,Comments,ProcessedDate,To_QueueName,Decision) values('" + PId + "','" + strActivityName + "','" + strusername + "','" + Comments + "',getDate(),'" + ToQueueName + "','" + Decision + "')";
                } else if (PId.substring(0, PId.indexOf("-")).equalsIgnoreCase("CCP")) {
                    strQuery = "Insert into CommentsHistory_CCP (ProcessInstID,QueueName,UserName,Comments,ProcessedDate,To_QueueName,Decision) values('" + PId + "','" + strActivityName + "','" + strusername + "','" + Comments + "',getDate(),'" + ToQueueName + "','" + Decision + "')";
                }

                GenLog("CommentsHistory Query :" + strQuery, "T");
                ExecuteUpdateQuery(strQuery);
            } else {
                GenLog("Comments Empty..", "T");
            }
        } catch (Exception e) {
            GenLog("Exception in InsertCommentsHistory Function.." + e, "E");
        }
    }

    /*
     * ----------------------------------------------------------------------------------
     * Function Name :setNextApprover
     * Description   :To set next approver
     * Return Value : NULL
     * -----------------------------------------------------------------------------------
     */
    public static void setNextApprover(String processinstanceid, String username) {
        int cLoop;
        String tempData = "";
        String[] tempStore;
        String Q_UserIndex1 = null;
        String Q_UserIndex2 = null;
        String UserQuery = "";
        String UpdateQuery = "";
        //C3-005 Starts here
        String strCurApprover = "";
        String strCurApproverName = "";
        String SA_CurrentManagerEmailID = "";
        WFXmlResponse xmlResponse = null;
        String[] tempIDStore;
        //C3-005 Ends here
        if (processinstanceid.substring(0, processinstanceid.indexOf("-")).toUpperCase().contains("VM")) {
            UserQuery = "SELECT SA_Total,SA_ListofApprover,SA_CurrentApprover,SA_CurrentLoop,SA_CurrentApproverEmailID,SA_ListofDelegate,SA_CurrentDelegateApp,SA_ListofDelegateName,SA_ListofDelegateEmailID,SA_ListofApproverName,SA_ListofAppEmailID,SA_ListofManagerEmailID,SA_CurrentApproverName,SA_CurrentManagerEmailID FROM ext_VM WITH (nolock)WHERE PROCESSINSTID='" + processinstanceid + "'";
        } else if (processinstanceid.substring(0, processinstanceid.indexOf("-")).toUpperCase().contains("PTP")) {
            UserQuery = "SELECT SA_Total,SA_ListofApprover,SA_CurrentApprover,SA_CurrentLoop,SA_CurrentApproverEmailID,SA_ListofDelegate,SA_CurrentDelegateApp,SA_ListofDelegateName,SA_ListofDelegateEmailID,SA_ListofApproverName,SA_ListofAppEmailID,SA_ListofManagerEmailID,SA_CurrentApproverName,SA_CurrentManagerEmailID FROM ext_PTP WITH (nolock)WHERE PROCESSINSTID='" + processinstanceid + "'";
        }

        GenLog("Inside VM---AssigedUserQuery-->> " + UserQuery, "T");
        String OutputXml_VM = selectQuery(UserQuery, 14);
        xmlResponse = new WFXmlResponse(OutputXml_VM);
        String SA_Total = xmlResponse.getVal("Value1");
        String SA_ListofApprover = xmlResponse.getVal("Value2");
        String SA_CurrentApprover = xmlResponse.getVal("Value3");
        String SA_CurrentLoop = xmlResponse.getVal("Value4");
        String SA_CurrentApproverEmailID = xmlResponse.getVal("Value5");
        String SA_ListofDelegate = xmlResponse.getVal("Value6");
        String SA_CurrentDelegateApp = xmlResponse.getVal("Value7");
        String SA_ListofDelegateName = xmlResponse.getVal("Value8");
        String SA_ListofDelegateEmailID = xmlResponse.getVal("Value9");
        String SA_ListofApproverName = xmlResponse.getVal("Value10");
        String SA_ListofAppEmailID = xmlResponse.getVal("Value11");
        String SA_ListofManagerEmailID = xmlResponse.getVal("Value12");
        String SA_CurrentApproverName = xmlResponse.getVal("Value13");
        SA_CurrentManagerEmailID = xmlResponse.getVal("Value14");

        try {

            GenLog("setNextApprover Called ::", "T");
            GenLog("SA_CurrentLoop value ::" + SA_CurrentLoop, "T");
            GenLog("SA_Total value ::" + SA_Total, "T");

            //C3-005 Starts here
            GenLog("Approver" + SA_CurrentLoop + "::" + username, "T");
            String Approver = username;

            //C3-005 Ends here
            if (Integer.parseInt(SA_CurrentLoop) < Integer.parseInt(SA_Total)) {

                GenLog("Increment Cureent Loop ::" + Integer.parseInt(SA_CurrentLoop) + 1, "T");
                cLoop = (Integer.parseInt(SA_CurrentLoop) + 1);
                GenLog("Increment cLoop ::" + cLoop, "T");
                SA_CurrentLoop = Integer.toString(cLoop);

                tempData = SA_ListofApprover;
                tempStore = tempData.split("~");
                GenLog("SA_CurrentApprover ::" + tempStore[cLoop - 1], "T");
                SA_CurrentApprover = tempStore[cLoop - 1];

                GenLog("SA_ListofApproverName ::" + SA_ListofApproverName, "T");
                tempData = SA_ListofApproverName;
                tempStore = tempData.split("~");
                GenLog("SA_CurrentApproverName ::" + tempStore[cLoop - 1], "T");
                SA_ListofApproverName = tempStore[cLoop - 1];

                tempData = SA_ListofAppEmailID;
                tempStore = tempData.split("~");
                GenLog("SA_CurrentApproverEmailID ::" + tempStore[cLoop - 1], "T");
                SA_CurrentApproverEmailID = tempStore[cLoop - 1];

                tempData = SA_ListofManagerEmailID;
                tempStore = tempData.split("~");
                GenLog("SA_CurrentManagerEmailID ::" + tempStore[cLoop - 1], "T");
                SA_ListofManagerEmailID = tempStore[cLoop - 1];

                //C3-005 Starts here
                tempData = SA_ListofDelegate;

                if ((tempData.substring(tempData.length() - 2, tempData.length()).equalsIgnoreCase("~~")) || (tempData.substring(tempData.length() - 2, tempData.length()).equalsIgnoreCase("~~~"))) {
                    GenLog("SA_ListofDelegate ~~ or ~~~ ::" + tempData.substring(tempData.length() - 2, tempData.length()), "T");
                    SA_CurrentDelegateApp = null;
                    Q_UserIndex1 = null;
                    Q_UserIndex2 = null;
                } else {
                    tempStore = tempData.split("~");
                    GenLog("SA_CurrentDelegateApp ::" + tempStore[cLoop - 1], "T");
                    SA_CurrentDelegateApp = tempStore[cLoop - 1];

                    if (!tempStore[cLoop - 1].isEmpty()) {

                        tempData = SA_ListofDelegate;
                        tempStore = tempData.split("~");
                        GenLog("SA_CurrentDelApprover ::" + tempStore[cLoop - 1], "T");
                        strCurApprover = SA_CurrentApprover;
                        SA_CurrentApprover = SA_CurrentApprover + "," + tempStore[cLoop - 1];

                        //set Approver
                        GenLog("Approver" + String.format("%d", (cLoop)) + "::" + SA_CurrentApprover, "T");
                        String Approver1 = SA_CurrentApprover;

                        GenLog("SA_ListofApproverName ::" + SA_ListofApproverName, "T");
                        tempData = SA_ListofDelegateName;
                        tempStore = tempData.split("~");
                        GenLog("SA_CurrentDelApproverName ::" + tempStore[cLoop - 1], "T");
                        strCurApproverName = SA_CurrentApproverName;
                        SA_CurrentApproverName = SA_CurrentApproverName + " / " + tempStore[cLoop - 1];

                        tempData = SA_ListofDelegateEmailID;
                        tempStore = tempData.split("~");
                        GenLog("SA_CurrentDelApproverEmailID ::" + tempStore[cLoop - 1], "T");
                        SA_CurrentApproverEmailID = SA_CurrentApproverEmailID + "," + tempStore[cLoop - 1];

                        //set Userindex in Q_Variable
                        String sQuery = "SELECT UserName + '~' + CAST(UserIndex AS NVARCHAR) from PDBUser with(nolock) where UserName IN('" + strCurApprover + "','" + SA_CurrentDelegateApp + "')";
                        GenLog("User Index sQuery ::" + sQuery, "T");

                        String strFieldValue = selectQuery(sQuery, 1);
                        if (!strFieldValue.isEmpty()) {

                            String values = strFieldValue.toString();
                            values = values.replace("[", "");
                            values = values.replace("]", "");
                            String FieldValue_array[] = values.split(",");
                            for (int i = 0; i < FieldValue_array.length; i++) {
                                tempIDStore = FieldValue_array[i].trim().split("~");
                                GenLog("Q_UserIndex" + (i + 1) + " : " + tempIDStore[1], "T");
                                String strDelID = "Q_UserIndex" + (i + 1);
                                GenLog("strDelID ::" + strDelID, "T");
                                int strDelID_temp = Integer.parseInt(tempIDStore[1]);
                                if (i == 0) {
                                    Q_UserIndex1 = tempIDStore[1];
                                } else if (i == 1) {
                                    Q_UserIndex2 = tempIDStore[1];
                                }

                            }
                        }
                    }
                }
                String updatetable = "Update WFinstrumenttable set var_int1=" + Q_UserIndex1 + ",var_int2=" + Q_UserIndex2 + " where processinstanceid='" + processinstanceid + "'";
                GenLog("updatetable ::" + updatetable, "T");
                ExecuteUpdateQuery(updatetable);
//                if(!(SA_CurrentDelegateApp.equalsIgnoreCase("null")||SA_CurrentDelegateApp.equalsIgnoreCase(null)))
//                {
//                  GenLog("INSIDE STATEMENT ::" + SA_CurrentDelegateApp);
//                  SA_CurrentDelegateApp="'"+SA_CurrentDelegateApp+"'";  
//                  GenLog("AFTER STATEMENT ::" + SA_CurrentDelegateApp);
//                }
                GenLog("<<::Delegate User entry is null::>>", "T");

                //C3-005 Ends here
                GenLog("Inside main table update-->> ", "T");
                //For VM Process
                if (processinstanceid.substring(0, processinstanceid.indexOf("-")).toUpperCase().contains("VM")) {
                    UpdateQuery = "Update ext_VM set SA_CurrentLoop='" + SA_CurrentLoop + "',SA_CurrentApprover='" + SA_CurrentApprover + "',SA_CurrentApproverEmailID='" + SA_CurrentApproverEmailID + "',SA_ListofDelegate='" + SA_ListofDelegate + "',SA_CurrentDelegateApp=" + SA_CurrentDelegateApp + ",SA_ListofDelegateName='" + SA_ListofDelegateName + "',"
                            + "SA_ListofDelegateEmailID='" + SA_ListofDelegateEmailID + "',SA_ListofApproverName='" + SA_ListofApproverName + "',SA_ListofAppEmailID='" + SA_ListofAppEmailID + "',SA_ListofManagerEmailID='" + SA_ListofManagerEmailID + "',SA_CurrentApproverName='" + SA_CurrentApproverName + "',SA_CurrentManagerEmailID='" + SA_CurrentManagerEmailID + "',Approver" + cLoop + "='" + Approver + "' where PROCESSINSTID='" + processinstanceid + "'";
                    CommonMethods.GenLog("Inside main table update1-->> " + UpdateQuery, "T");
                    ExecuteUpdateQuery(UpdateQuery);
                } //For PTP Process
                else if (processinstanceid.substring(0, processinstanceid.indexOf("-")).toUpperCase().contains("PTP")) {
                    UpdateQuery = "Update ext_PTP set SA_CurrentLoop='" + SA_CurrentLoop + "',SA_CurrentApprover='" + SA_CurrentApprover + "',SA_CurrentApproverEmailID='" + SA_CurrentApproverEmailID + "',SA_ListofDelegate='" + SA_ListofDelegate + "',SA_CurrentDelegateApp=" + SA_CurrentDelegateApp + ",SA_ListofDelegateName='" + SA_ListofDelegateName + "',"
                            + "SA_ListofDelegateEmailID='" + SA_ListofDelegateEmailID + "',SA_ListofApproverName='" + SA_ListofApproverName + "',SA_ListofAppEmailID='" + SA_ListofAppEmailID + "',SA_ListofManagerEmailID='" + SA_ListofManagerEmailID + "',SA_CurrentApproverName='" + SA_CurrentApproverName + "',SA_CurrentManagerEmailID='" + SA_CurrentManagerEmailID + "',Approver" + cLoop + "='" + Approver + "' where PROCESSINSTID='" + processinstanceid + "'";
                    CommonMethods.GenLog("Inside main table update2-->> " + UpdateQuery, "T");
                    ExecuteUpdateQuery(UpdateQuery);
                }
                GenLog("After main table Update Query-->> " + UpdateQuery, "T");
            } else if (Integer.parseInt(SA_CurrentLoop) == Integer.parseInt(SA_Total)) {

                GenLog("Increment Cureent Loop ::" + SA_CurrentLoop + 1, "T");
                cLoop = (Integer.parseInt(SA_CurrentLoop) + 1);
                GenLog("Increment cLoop ::" + cLoop, "T");
                SA_CurrentLoop = Integer.toString(cLoop);

                SA_CurrentApprover = "";
                SA_CurrentApproverEmailID = "";
                SA_CurrentManagerEmailID = "";

                if (processinstanceid.substring(0, processinstanceid.indexOf("-")).toUpperCase().contains("VM")) {
                    UpdateQuery = "Update ext_VM set SA_CurrentApprover='" + SA_CurrentApprover + "',SA_CurrentApproverEmailID='" + SA_CurrentApproverEmailID + "',SA_CurrentManagerEmailID='" + SA_CurrentManagerEmailID + "',SA_CurrentLoop='" + SA_CurrentLoop + "' where PROCESSINSTID='" + processinstanceid + "'";
                    ExecuteUpdateQuery(UpdateQuery);

                } else if (processinstanceid.substring(0, processinstanceid.indexOf("-")).toUpperCase().contains("PTP")) {
                    UpdateQuery = "Update ext_PTP set SA_CurrentApprover='" + SA_CurrentApprover + "',SA_CurrentApproverEmailID='" + SA_CurrentApproverEmailID + "',SA_CurrentManagerEmailID='" + SA_CurrentManagerEmailID + "',SA_CurrentLoop='" + SA_CurrentLoop + "' where PROCESSINSTID='" + processinstanceid + "'";
                    ExecuteUpdateQuery(UpdateQuery);

                }
                CommonMethods.GenLog("main table  Update-->> " + UpdateQuery, "T");
            }

        } catch (Exception e) {
            GenLog("Exception caught in setNextApprover Function...." + e, "E");
        }
    }

    /*
     * ----------------------------------------------------------------------------------
     * Function Name :mailTrigger_VM
     * Description   :To trigger mail to user
     * Return Value : NULL
     * -----------------------------------------------------------------------------------
     */
    public static void mailTrigger_VM(String processinstanceid) {
        String strOutputXML = "";
        String strInputXml = "";
        String tempapprover[] = null;
        WFXmlResponse xmlResponse = null;
        String ApprovalDate = "";
        GenLog("Inside MailTrigger Execution function", "T");
        GenLog("processinstanceid::>>" + processinstanceid, "T");

        String sQuery = "Select ConstantValue from CONSTANTDEFTABLE with(nolock) where ConstantName='CONST_LEGAL_USLLogo'";
        String DiageoLogo_temp = selectQuery(sQuery, 1);
        xmlResponse = new WFXmlResponse(DiageoLogo_temp);
        String DiageoLogo = xmlResponse.getVal("Value1");

        String sQuery1 = "Select ConstantValue from CONSTANTDEFTABLE with(nolock) where ConstantName='CONST_LGL_ApproveLogo'";
        String AppLogo_temp = selectQuery(sQuery1, 1);
        xmlResponse = new WFXmlResponse(AppLogo_temp);
        String AppLogo = xmlResponse.getVal("Value1");

        String sQuery2 = "Select ConstantValue from CONSTANTDEFTABLE with(nolock) where ConstantName='CONST_LGL_PendingLogo'";
        String PendLogo_temp = selectQuery(sQuery2, 1);
        xmlResponse = new WFXmlResponse(PendLogo_temp);
        String PendLogo = xmlResponse.getVal("Value1");

        String sQuery3 = "Select activityname from wfinstrumenttable with (nolock) where processinstanceid='" + processinstanceid + "'";
        String Activityname_temp = selectQuery(sQuery3, 1);
        xmlResponse = new WFXmlResponse(Activityname_temp);
        String Activityname = xmlResponse.getVal("Value1");

        String transactiondetails = "select isnull(VendorName,''),isnull(AccountGroup,''),isnull(PurchaseOrganization,''),isnull(TypeofBusiness,''),isnull(TypeofIndustry,''),isnull(KYBPFlag,''),isnull(VendorClassification,''),isnull(INITIATTIONUSER,''),isnull(companycode,''),isnull(location,''),isnull(vmtype,''),CAST(InitiatedDate AS NVARCHAR),isnull(InitationUserMailID,''),isnull(comments,''),isnull(SA_ListofAppEmailID,''),CAST(SA_CurrentLoop AS NVARCHAR),CAST(SA_Total AS NVARCHAR),isnull(SA_CurrentApprover,''),isnull(SA_CurrentApproverEmailID,''),isnull(SA_ListofApprover,'') from ext_VM where PROCESSINSTID='" + processinstanceid + "'";
        String transactiondetails1 = selectQuery(transactiondetails, 20);

        xmlResponse = new WFXmlResponse(transactiondetails1);

        GenLog("String transactiondetails::>>" + transactiondetails, "T");

        String BusinessPartnerName = xmlResponse.getVal("Value1");
        GenLog("BusinessPartnerName::>>" + BusinessPartnerName, "T");
        String AccountGroup = xmlResponse.getVal("Value2");
        GenLog("AccountGroup::>>" + AccountGroup, "T");
        String PurchaseOrganization = xmlResponse.getVal("Value3");
        GenLog("PurchaseOrganization::>>" + PurchaseOrganization, "T");
        String TypeofBusiness = xmlResponse.getVal("Value4");
        GenLog("TypeofBusiness::>>" + TypeofBusiness, "T");
        String TypeofIndustry = xmlResponse.getVal("Value5");
        GenLog("TypeofIndustry::>>" + TypeofIndustry, "T");
        String KYBPFlag = xmlResponse.getVal("Value6");
        GenLog("KYBPFlag::>>" + KYBPFlag, "T");
        String VendorClassification = xmlResponse.getVal("Value7");
        GenLog("VendorClassification::>>" + VendorClassification, "T");
        String INITIATTIONUSER = xmlResponse.getVal("Value8");
        GenLog("INITIATTIONUSER::>>" + INITIATTIONUSER, "T");
        String companycode = xmlResponse.getVal("Value9");
        GenLog("companycode::>>" + companycode, "T");
        String Location = xmlResponse.getVal("Value10");
        GenLog("Location::>>" + Location, "T");
        String vmtype = xmlResponse.getVal("Value11");
        GenLog("vmtype::>>" + vmtype, "T");
        String INITIATTIONEDATE = xmlResponse.getVal("Value12");
        GenLog("INITIATTIONEDATE::>>" + INITIATTIONEDATE, "T");
        String InitationUserMailID = xmlResponse.getVal("Value13");
        GenLog("InitationUserMailID::>>" + InitationUserMailID, "T");
        String comments = xmlResponse.getVal("Value14");
        GenLog("comments::>>" + comments, "T");
        String CurrentUserMailID[] = xmlResponse.getVal("Value15").trim().split("~");
        //GenLog("BusinessPartnerName::>>"+BusinessPartnerName);
        String SA_CurrentLoop = xmlResponse.getVal("Value16");
        GenLog("SA_CurrentLoop::>>" + SA_CurrentLoop, "T");
        String SA_Total = xmlResponse.getVal("Value17");
        GenLog("SA_Total::>>" + SA_Total, "T");
        String SA_CurrentApprover = xmlResponse.getVal("Value18");
        GenLog("SA_CurrentApprover::>>" + SA_CurrentApprover, "T");
        String SA_CurrentApproverEmailID = xmlResponse.getVal("Value19");
        GenLog("SA_CurrentApproverEmailID::>>" + SA_CurrentApproverEmailID, "T");
        String SA_ListofApprover[] = xmlResponse.getVal("Value20").trim().split("~");
        GenLog("SA_ListofApprover::>>" + SA_ListofApprover.length, "T");

        GenLog("$$$$$VariablesValue######::>>" + BusinessPartnerName + AccountGroup + PurchaseOrganization + TypeofBusiness + TypeofIndustry + KYBPFlag + VendorClassification + INITIATTIONUSER + companycode + Location + vmtype + INITIATTIONEDATE + InitationUserMailID + comments, "T");
        //GenLog("$$$$$VariablesValue#####11#::>>"+CurrentUserMailID+SA_CurrentLoop+SA_Total+SA_CurrentApprover+SA_CurrentApproverEmailID);

        String styleTag = "<style>.buttons{ font-family: Tahoma;font-size:20px;font-weight: bold} .button {background-color: #4CAF50;border: none;color: white;height: 32px;width: 120px;text-align: center;";
        styleTag = styleTag + "text-decoration: none;display: inline-block;font-size: 16px;margin: 4px 2px;cursor: pointer;font-weight: bold;font-family:Tahoma;}";
        styleTag = styleTag + ".button3 {background-color: #f44336;}#trans {border-collapse: collapse;width: 800px;font-family: Tahoma;font-size:13px;}";
        styleTag = styleTag + "#cols {border-collapse: collapse;width: 800px;font-family: Tahoma;font-size:13px;}#trans td{\n";
        styleTag = styleTag + "height:35px;border: 2px solid black;padding: 8px;color: black;width: 100px;text-align: center;}";
        styleTag = styleTag + "#cols td{height:35px;border: 2px solid black;padding: 8px;color: black;text-align: center;}";
        styleTag = styleTag + "#trans tr{height:55px;}#trans th {background-color:  #E4DAE8;color: white;border: 2px solid black;padding: 8px;}";
        styleTag = styleTag + "#cols th {background-color:  #E4DAE8;color: white;text-align: center;border: 2px solid black;padding: 6px;}</style>";
        String seqid_temp = selectQuery("SELECT NEXT VALUE FOR SeqVM", 1);
        xmlResponse = new WFXmlResponse(seqid_temp);
        String seqid = xmlResponse.getVal("Value1");
        String scriptTag = "<script language=\"javascript\" type=\"text/javascript\">";
        scriptTag = scriptTag + "function approve() {";

        if (Activityname.equalsIgnoreCase("Initiation")) {
            scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=VM&seqid=VM-" + seqid + "&action=approve\";";
            scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
            scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
            scriptTag = scriptTag + "function reject() {";
            scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=VM&seqid=VM-" + seqid + "&action=reject\";";
            scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
            scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
        } else if (Activityname.equalsIgnoreCase("Approver")) {
            scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=VM&seqid=VM-" + seqid + "&action=approve\";";
            scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
            scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
            scriptTag = scriptTag + "function reject() {";
            scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=VM&seqid=VM-" + seqid + "&action=reject\";";
            scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
            scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
        }

        scriptTag = scriptTag + "</script>";

        String button1CSS = "padding-top: 5px;border: none;color:#4CAF50;text-decoration: none;display: inline-block;font-size: 24px;cursor: pointer;font-weight: bold;font-family:Tahoma;";
        String button2CSS = "padding-top: 5px;border: none;color:#f44336;text-decoration: none;display: inline-block;font-size: 24px;cursor: pointer;font-weight: bold;font-family:Tahoma;";

        String tableContent = "<body><table id=\"cols\"><tbody> <tr>";
        tableContent = tableContent + "<td style=\"width: 120px;height:80px;background-color:#E4DAE8 \"><img src=\"" + DiageoLogo + "\"  alt=\"Diageo India\" width=\"115\" height=\"50\"></td>";
        tableContent = tableContent + "<td style=\"font-size: 22px;background-color:#E4DAE8 \">Vendor " + vmtype + "<span style=\"font-size: 18px \"><br>" + (processinstanceid) + "</br></span></td></tr></table>";
        tableContent = tableContent + "<table id=\"trans\"><tbody><tr>";
        tableContent = tableContent + "<td><b>Company Code</b> <br>" + companycode + "</td><td><b>Request Type</b><br>" + vmtype + "</td><td><b>Location</b><br>" + Location + "</td>";
        tableContent = tableContent + "</tr></tbody></table><table id=\"cols\"><tbody>";
        tableContent = tableContent + "<th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Transaction Details</th></tbody></table>";
        tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";
        tableContent = tableContent + "<td><b>Initiated Date</b></td><td><b>Initiator Name</b></td><td><b>Initiator MailId</b></td></tr><tr>";
        tableContent = tableContent + "<td>" + INITIATTIONEDATE + "</td><td>" + INITIATTIONUSER + "</td><td>" + InitationUserMailID + "</td></tr></tbody></table>";
        tableContent = tableContent + "<table id=\"cols\"><th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Transaction Details</th></tbody></table>";
        tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";
        tableContent = tableContent + "<td><b>Vendor Name</b></td><td><b>Requested By</b></td><td><b>Account Group</b></td><td><b>Purchasing Organization</b></td><td><b>Type of Business</b></td><td><b>Type of Industry</b></td><td><b>KYBP/ Non KYBP Vendor</b></td><td><b>Vendor Type</b></td></tr><tr>";
        tableContent = tableContent + "<td>" + BusinessPartnerName + "</td><td>" + INITIATTIONUSER + "</td><td>" + AccountGroup + "</td><td>" + PurchaseOrganization + "</td><td>" + TypeofBusiness + "</td><td>" + TypeofIndustry + "</td><td>" + KYBPFlag + "</td><td>" + VendorClassification + "</td>";
        tableContent = tableContent + "</tr></tbody></table><table id=\"cols\"><tbody>";
        tableContent = tableContent + "<th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Approval Flow</th></tbody></table>";
        tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";
        String ApprovalQuery = "";
        GenLog("Activityname****" + Activityname, "T");
        if (Activityname.equalsIgnoreCase("Approver")) {
            if (!SA_CurrentLoop.equalsIgnoreCase("") && !SA_Total.equalsIgnoreCase("")) {
                int temp_Currentloop = Integer.parseInt(SA_CurrentLoop) - 1;
                int temp1_TotalLoop = Integer.parseInt(SA_Total);

                for (int i1 = 0; i1 < temp_Currentloop; i1++) {
                    ApprovalQuery = selectQuery("SELECT TOP 1 convert(NVARCHAR,processedDate,100) FROM CommentsHistory_VM with (nolock) WHERE ProcessInstID = '" + processinstanceid + "' AND UserName='" + SA_ListofApprover[i1] + "' AND QueueName='" + Activityname + "' ORDER BY INSERTIONORDERID desc", 1);
                    xmlResponse = new WFXmlResponse(ApprovalQuery);
                    GenLog("ApprovalQuery::>>" + ApprovalQuery, "T");
                    ApprovalDate = xmlResponse.getVal("Value1");
                    tableContent = tableContent + "<td><b>Approver" + (i1 + 1) + "</b><br>" + CurrentUserMailID[i1] + "</br><img src=\"" + AppLogo + "\" width=\"105\" height=\"85\"><br><span style=\"height:22px;color:black;font-size: 14px;font-weight: bold\">Approved On: " + ApprovalDate + "</span></br></td>";

                    //  tableContent = tableContent + "<td><b>Approver" + (i1 + 1) + "</b><br>" + CurrentUserMailID[i1] + "<br><img src=\"" + AppLogo + "\" width=\"105\" height=\"85\"></td>";
                }
                for (int i2 = temp_Currentloop; i2 < temp1_TotalLoop; i2++) {
                    //tableContent = tableContent + "<td><b>Approver" + (i2 + 1) + "</b><br>" + SA_CurrentApproverEmailID + "<br><img src=\"" + PendLogo + "\" width=\"97\" height=\"75\" style=\"padding-top: 7px;transform: rotate(-7deg);\"></td>";
                    tableContent = tableContent + "<td><b>Approver" + (i2 + 1) + "</b><br>" + SA_CurrentApproverEmailID + "<br><img src=\"" + PendLogo + "\" width=\"97\" height=\"75\" style=\"padding-top: 7px;transform: rotate(-7deg);\"><br><span style=\"height:22px;color:black;font-size: 14px;font-weight: bold\">Status:Pending for Approval</span></br></td>";
                }
            }

        }

//        else {
//            tableContent = tableContent + "<td><b>Approver"+(i+1)+"</b><br>" +  formObject.getNGValue("SA_CurrentApproverEmailID") + "<br><img src=\"" + AppLogo + "\" width=\"105\" height=\"85\"></td>";
//        }
//        if (strActivityName.equalsIgnoreCase("Approver")) {
//            
//            
//            tableContent = tableContent + "<td><b>Approver"+(i+1)+"</b><br>" +  formObject.getNGValue("SA_CurrentApproverEmailID") + "<br><img src=\"" + AppLogo + "\" width=\"105\" height=\"85\"></td>";
//        } else  {
//            tableContent = tableContent + "<td><b>Approver"+(i+1)+"</b><br>" +  formObject.getNGValue("SA_CurrentApproverEmailID") + "<br><img src=\"" + PendLogo + "\" width=\"97\" height=\"75\" style=\"padding-top: 7px;transform: rotate(-7deg);\"></td>";
//        }
        tableContent = tableContent + "</tr></tbody></table><table id=\"cols\"><tbody><tr  style=\"height:10px;\">";
        tableContent = tableContent + "<td style=\"width:150px;background-color: #E4DAE8\"><b>Previous User Comments</b></td><td>" + comments + "</td>";
        tableContent = tableContent + "</tr></table><br>";
        tableContent = tableContent + "<table style=\"width:800px;align:center\">";
        tableContent = tableContent + "<tbody><tr class=\"buttons\" style=\"height:50px;\">";
        tableContent = tableContent + "<td style=\"padding-left:120px\">";
        if (Activityname.equalsIgnoreCase("Approver")) {
            tableContent = tableContent + "<a href=\"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=VM&seqid=VM-" + seqid + "&action=approve\" onClick=\"return approve()\">";
            tableContent = tableContent + "<img src=\"" + url + "/webdesktop/resources/images/approve_icon.png\" alt=\"APPROVE\" style=\"width:137px;height:40px;border:0\"></a>";
            tableContent = tableContent + "</td><td>";
            tableContent = tableContent + "<a href=\"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=VM&seqid=VM-" + seqid + "&action=reject\" onClick=\"return reject()\">";
            tableContent = tableContent + "<img src=\"" + url + "/webdesktop/resources/images/reject_icon.png\" alt=\"REJECT\" style=\"width:133px;height:40px;border:0\"></a>";

        }
        tableContent = tableContent + "</td></tr></tbody></table><br>";
        tableContent = tableContent + "<div style=\"font-family: Tahoma; font-size: 12px; color: #696969;padding-bottom: 10px\">";
        tableContent = tableContent + "<em>This is an automated E-Mail. Replies to this E-Mail are not being monitored. PLEASE DO NOT REPLY TO THIS MESSAGE.</em></div></body>";

        try {
            GenLog("strSessionId::::" + SessionId, "T");
            GenLog("SA_CurrentApproverEmailID::::" + SA_CurrentApproverEmailID, "T");
            GenLog("strIP::::" + url, "T");
            strInputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            strInputXml += "<WFAddToMailQueue_Input>";
            strInputXml += "<Option>WFAddToMailQueue</Option>";
            strInputXml = (strInputXml + "<EngineName>" + CabinetName + "</EngineName>");
            strInputXml = (strInputXml + "<SessionId>" + SessionId + "</SessionId>");
            strInputXml += "<MailFrom>workflow@diageo.com</MailFrom>";
            strInputXml = (strInputXml + "<MailTo>" + SA_CurrentApproverEmailID/*toAddress*/ + "</MailTo>");
            strInputXml = (strInputXml + "<MailCC>" + "" + "</MailCC>");
            strInputXml = (strInputXml + "<MailSubject>A VM " + vmtype + " Request under workitem " + processinstanceid + " has been assigned to you for your review and approval.</MailSubject>");
            strInputXml += "<ContentType>text/html;charset=UTF-8</ContentType>";
            strInputXml += "<AttachmentISIndex>";
            strInputXml += "</AttachmentISIndex>";
            strInputXml += "<AttachmentNames>";
            strInputXml += "</AttachmentNames>";
            strInputXml += "<Priority>1</Priority>";
            strInputXml += "<Comments>MailApprovalSystem</Comments>";
            strInputXml += "<MailActionType>TRIGGER</MailActionType>";
            strInputXml += "<ProcessDefId></ProcessDefId>";
            strInputXml += "<ProcessInstanceId>" + processinstanceid + "</ProcessInstanceId>";
            strInputXml += "<WorkitemId>1</WorkitemId><ActivityId></ActivityId>";
            strInputXml += "<MailMessage>";
            strInputXml += "<html>" + scriptTag + styleTag + tableContent + "</html>";
            strInputXml += "</MailMessage>";
            strInputXml += "</WFAddToMailQueue_Input>";
            GenLog("Email Input XML::" + strInputXml, "X");
            strOutputXML = WFCallBroker.execute(strInputXml, "127.0.0.1", 3333, 0);
            GenLog("Email Output XML::" + strOutputXML, "X");
            WFXmlResponse outParser = new WFXmlResponse(strOutputXML);
            String mainCode = outParser.getVal("MainCode");
            if (mainCode.equals("0")) {
                GenLog("mailTrigger_VM Query is Executed successfully", "T");
            } else {
                GenLog("mailTrigger_VM Query is not Executed successfully", "E");
            }
        } catch (Exception e) {
            GenLog("Exception in mailTrigger Function..: " + e.getMessage(), "E");
        }
    }
    
    public static void mailTrigger_Legal(String PId, String Comments) {

        String strOutputXML = "";
        String strInputXml = "";

        String transactionQry = "SELECT isnull(LEGALAPPUSER,'')+'`'+isnull(ROAPPROVALMAIL,'')+'`'+isnull(LEGALAPPROVALMAIL,'')+'`'+isnull(COMPANYCODE,'')+'`'+isnull(REQUESTTYPE,'')+'`'+isnull(LOCATION,'')+'`'+isnull(CONVERT(NVARCHAR,INITIATEDDATE,105),'')+'`'+isnull(INITIATORNAME,'')+'`'+isnull(INITIATORMAILID,'') FROM EXT_LEGAL WITH(NOLOCK) WHERE PROCESSINSTID='" + PId + "'";
        String transXml = selectQuery(transactionQry, 1);
        WFXmlResponse xmlResponse = new WFXmlResponse(transXml);
        String workItemData = xmlResponse.getVal("Value1");
        String[] workItemArr = workItemData.split("`");
        if (workItemArr.length > 7) {
            String ApproverUser = workItemArr[0];
            String ROApproverMail = workItemArr[1];
            String ApproverMail = workItemArr[2];
            String CompanyCode = workItemArr[3];
            String RequestType = workItemArr[4];
            String Location = workItemArr[5];
            String InitiatedDate = workItemArr[6];
            String InitiatorName = workItemArr[7];
            String InitiatorMail = workItemArr[8];

            String styleTag = "<style>.buttons{ font-family: Tahoma;font-size:20px;font-weight: bold} .button {background-color: #4CAF50;border: none;color: white;height: 32px;width: 120px;text-align: center;";
            styleTag = styleTag + "text-decoration: none;display: inline-block;font-size: 16px;margin: 4px 2px;cursor: pointer;font-weight: bold;font-family:Tahoma;}";
            styleTag = styleTag + ".button3 {background-color: #f44336;}#trans {border-collapse: collapse;width: 800px;font-family: Tahoma;font-size:13px;}";
            styleTag = styleTag + "#cols {border-collapse: collapse;width: 800px;font-family: Tahoma;font-size:13px;}#trans td{\n";
            styleTag = styleTag + "height:35px;border: 2px solid black;padding: 8px;color: black;width: 100px;text-align: center;}";
            styleTag = styleTag + "#cols td{height:35px;border: 2px solid black;padding: 8px;color: black;text-align: center;}";
            styleTag = styleTag + "#trans tr{height:55px;}#trans th {background-color:  #E4DAE8;color: white;border: 2px solid black;padding: 8px;}";
            styleTag = styleTag + "#cols th {background-color:  #E4DAE8;color: white;text-align: center;border: 2px solid black;padding: 6px;}</style>";

            String scriptTag = "<script language=\"javascript\" type=\"text/javascript\">";
            scriptTag = scriptTag + "function approve() {";

            scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + ApproverUser + "&pid=" + PId + "&process=Legal&action=approve\";";
            scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
            scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
            scriptTag = scriptTag + "function reject() {";
            scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + ApproverUser + "&pid=" + PId + "&process=Legal&action=reject\";";
            scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
            scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
            scriptTag = scriptTag + "</script>";

            String tableContent = "<body><table id=\"cols\"><tbody> <tr>";
            tableContent = tableContent + "<td style=\"width: 120px;height:80px;background-color:#E4DAE8 \"><img src=\"" + DiageoLogo + "\"  alt=\"Diageo India\" width=\"115\" height=\"50\"></td>";
            tableContent = tableContent + "<td style=\"font-size: 22px;background-color:#E4DAE8 \"><b>" + PId + "</b></td></tr></table>";
            tableContent = tableContent + "<table id=\"trans\"><tbody><tr>";
            tableContent = tableContent + "<td><b>Company Code</b> <br>" + CompanyCode + "</td><td><b>Request Type</b><br>" + RequestType + "</td><td><b>Location</b><br>" + Location + "</td>";
            tableContent = tableContent + "</tr></tbody></table><table id=\"cols\"><tbody>";
            tableContent = tableContent + "<th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Transaction Details</th></tbody></table>";
            tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";
            tableContent = tableContent + "<td><b>Initiated Date</b></td><td><b>Initiator Name</b></td><td><b>Initiator MailId</b></td></tr><tr>";
            tableContent = tableContent + "<td>" + InitiatedDate + "</td><td>" + InitiatorName + "</td><td>" + InitiatorMail + "</td>";
            tableContent = tableContent + "</tr></tbody></table><table id=\"cols\"><tbody>";
            tableContent = tableContent + "<th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Approval Flow</th></tbody></table>";
            tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";

            tableContent = tableContent + "<td><b>RO Approval</b><br>" + ROApproverMail + "<br><img src=\"" + ApproveLogo + "\" width=\"105\" height=\"85\"></td>";
            tableContent = tableContent + "<td><b>LEGAL Approval</b><br>" + ApproverMail + "<br><img src=\"" + PendingLogo + "\" width=\"97\" height=\"75\" style=\"padding-top: 7px;transform: rotate(-7deg);\"></td>";

            tableContent = tableContent + "</tr></tbody></table><table id=\"cols\"><tbody><tr  style=\"height:10px;\">";
            tableContent = tableContent + "<td style=\"width:150px;background-color: #E4DAE8\"><b>Previous User Comments</b></td><td>" + Comments + "</td>";
            tableContent = tableContent + "</tr></table><br>";
            tableContent = tableContent + "<table style=\"width:800px;align:center\">";
            tableContent = tableContent + "<tbody><tr class=\"buttons\" style=\"height:50px;\">";
            tableContent = tableContent + "<td style=\"padding-left:120px\">";

            tableContent = tableContent + "<a href=\"" + url + "/mailapproval/mailApproval.jsp?approver=" + ApproverUser + "&pid=" + PId + "&process=Legal&action=approve\" onClick=\"return approve()\">";
            tableContent = tableContent + "<img src=\"" + url + "/webdesktop/resources/images/approve_icon.png\" alt=\"APPROVE\" style=\"width:137px;height:40px;border:0\"></a>";
            tableContent = tableContent + "</td><td>";
            tableContent = tableContent + "<a href=\"" + url + "/mailapproval/mailApproval.jsp?approver=" + ApproverUser + "&pid=" + PId + "&process=Legal&action=reject\" onClick=\"return reject()\">";
            tableContent = tableContent + "<img src=\"" + url + "/webdesktop/resources/images/reject_icon.png\" alt=\"REJECT\" style=\"width:133px;height:40px;border:0\"></a>";

            tableContent = tableContent + "</td></tr></tbody></table><br>";
            tableContent = tableContent + "<div style=\"font-family: Tahoma; font-size: 12px; color: #696969;padding-bottom: 10px\">";
            tableContent = tableContent + "<em>This is an automated E-Mail. Replies to this E-Mail are not being monitored. PLEASE DO NOT REPLY TO THIS MESSAGE.</em></div></body>";

            try {
                GenLog("strSessionId::::" + SessionId, "T");
                GenLog("strIP::::" + url, "T");
                strInputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                strInputXml += "<WFAddToMailQueue_Input>";
                strInputXml += "<Option>WFAddToMailQueue</Option>";
                strInputXml = (strInputXml + "<EngineName>" + CabinetName + "</EngineName>");
                strInputXml = (strInputXml + "<SessionId>" + SessionId + "</SessionId>");
                strInputXml += "<MailFrom>workflow@diageo.com</MailFrom>";
                strInputXml = (strInputXml + "<MailTo>" + ApproverMail + "</MailTo>");
                strInputXml = (strInputXml + "<MailCC>" + "" + "</MailCC>");
                strInputXml = (strInputXml + "<MailSubject>A Legal " + RequestType + " Request under workitem " + PId + " has been assigned for your review and approval.</MailSubject>");
                strInputXml += "<ContentType>text/html;charset=UTF-8</ContentType>";
                strInputXml += "<AttachmentISIndex>";
//            strInputXml += Doc_Index;
                strInputXml += "</AttachmentISIndex>";
                strInputXml += "<AttachmentNames>";
//            strInputXml += Doc_Names;
                strInputXml += "</AttachmentNames>";
                strInputXml += "<Priority>1</Priority>";
                strInputXml += "<Comments>MailApprovalSystem</Comments>";
                strInputXml += "<MailActionType>TRIGGER</MailActionType>";
                strInputXml += "<ProcessDefId></ProcessDefId>";
                strInputXml += "<ProcessInstanceId>" + PId + "</ProcessInstanceId>";
                strInputXml += "<WorkitemId>1</WorkitemId><ActivityId></ActivityId>";
                strInputXml += "<MailMessage>";
                strInputXml += "<html>" + scriptTag + styleTag + tableContent + "</html>";
                strInputXml += "</MailMessage>";
                strInputXml += "</WFAddToMailQueue_Input>";
                GenLog("Email Input XML::" + strInputXml, "T");
                strOutputXML = WFCallBroker.execute(strInputXml, "127.0.0.1", 3333, 0);
                GenLog("Email Output XML::" + strOutputXML, "T");
                WFXmlResponse outParser = new WFXmlResponse(strOutputXML);
                String mainCode = outParser.getVal("MainCode");
                if (mainCode.equals("0")) {
                    GenLog("mailTrigger Query is Executed successfully", "T");
                } else {
                    GenLog("mailTrigger Query is not Executed successfully", "T");
                }
            } catch (Exception e) {
                GenLog("Exception in mailTrigger Function..: " + e.getMessage(), "E");
            }
        } else {
            GenLog("Error in workitem data fetch mailTrigger_Legal Function..\n mailTrigger_Legal failed...", "E");
        }
    }

    /*
     * ----------------------------------------------------------------------------------
     * Function Name :mailTrigger_PTP
     * Description   :To trigger mail to user
     * Return Value : NULL
     * -----------------------------------------------------------------------------------
     */
    public static void mailTrigger_PTP(String processinstanceid) {
        String strOutputXML = "";
        String strInputXml = "";
        String tempapprover[] = null;
        WFXmlResponse xmlResponse = null;
        WFXmlList xmllist = null;
        String glcode, costcenter, desc, amount = "";
        StringBuffer GLCode;
        StringBuffer CostCenter;
        String InvoiceDocItem;
        StringBuffer WHTcode;
        String WHTTaxcode;
        String baseamount;
        String ApprovalQuery = "";
        String ApprovalDate = "";

        CommonMethods.GenLog("Inside MailTrigger Execution function", "T");
        GenLog("processinstanceid::>>" + processinstanceid, "T");

        String sQuery = "Select ConstantValue from CONSTANTDEFTABLE with(nolock) where ConstantName='CONST_LEGAL_USLLogo'";
        String DiageoLogo_temp = selectQuery(sQuery, 1);
        xmlResponse = new WFXmlResponse(DiageoLogo_temp);
        String DiageoLogo = xmlResponse.getVal("Value1");

        String sQuery1 = "Select ConstantValue from CONSTANTDEFTABLE with(nolock) where ConstantName='CONST_LGL_ApproveLogo'";
        String AppLogo_temp = selectQuery(sQuery1, 1);
        xmlResponse = new WFXmlResponse(AppLogo_temp);
        String AppLogo = xmlResponse.getVal("Value1");

        String sQuery2 = "Select ConstantValue from CONSTANTDEFTABLE with(nolock) where ConstantName='CONST_LGL_PendingLogo'";
        String PendLogo_temp = selectQuery(sQuery2, 1);
        xmlResponse = new WFXmlResponse(PendLogo_temp);
        String PendLogo = xmlResponse.getVal("Value1");

        String sQuery3 = "Select activityname from wfinstrumenttable with (nolock) where processinstanceid='" + processinstanceid + "'";
        String Activityname_temp = selectQuery(sQuery3, 1);
        xmlResponse = new WFXmlResponse(Activityname_temp);
        String Activityname = xmlResponse.getVal("Value1");

        String transactiondetails = "select isnull(comments,''),isnull(SA_ListofAppEmailID,''),CAST(SA_CurrentLoop AS NVARCHAR),CAST(SA_Total AS NVARCHAR),isnull(SA_CurrentApprover,''),isnull(SA_CurrentApproverEmailID,''),isnull(ptype,''),isnull(SA_ListofApprover,'') from ext_PTP with (nolock) where PROCESSINSTID='" + processinstanceid + "'";
        String transactiondetails1 = selectQuery(transactiondetails, 8);
        String paise, ParsedAmount;
        xmlResponse = new WFXmlResponse(transactiondetails1);

        GenLog("String transactiondetails::>>" + transactiondetails, "T");

        String comments = xmlResponse.getVal("Value1");
        GenLog("comments::>>" + comments, "T");
        String CurrentUserMailID[] = xmlResponse.getVal("Value2").trim().split("~");
        //GenLog("BusinessPartnerName::>>"+BusinessPartnerName);
        String SA_CurrentLoop = xmlResponse.getVal("Value3");
        GenLog("SA_CurrentLoop::>>" + SA_CurrentLoop, "T");
        String SA_Total = xmlResponse.getVal("Value4");
        GenLog("SA_Total::>>" + SA_Total, "T");
        String SA_CurrentApprover = xmlResponse.getVal("Value5");
        GenLog("SA_CurrentApprover::>>" + SA_CurrentApprover, "T");
        String SA_CurrentApproverEmailID = xmlResponse.getVal("Value6");
        GenLog("SA_CurrentApproverEmailID::>>" + SA_CurrentApproverEmailID, "T");
        String ptype = xmlResponse.getVal("Value7");
        GenLog("ptype::>>" + ptype, "T");
        String SA_ListofApprover[] = xmlResponse.getVal("Value8").trim().split("~");
        GenLog("SA_ListofApprover::>>" + SA_ListofApprover.length, "T");

        String headercontent = "SELECT BusinessArea,vendorname,InvoiceType,InvoiceNumber,convert(NVARCHAR,InvoiceDate,103),InvoiceAmount FROM EXT_PTP WITH (nolock) WHERE PROCESSINSTID='" + processinstanceid + "'";
        String headercontent1 = selectQuery(headercontent, 6);
        xmlResponse = new WFXmlResponse(headercontent1);
        GenLog("String headercontent1::>>" + headercontent1, "T");
        String BusinessArea = xmlResponse.getVal("Value1");
        String vendorname = xmlResponse.getVal("Value2");
        String InvoiceType = xmlResponse.getVal("Value3");
        String InvoiceNumber = xmlResponse.getVal("Value4");
        String InvoiceDate = xmlResponse.getVal("Value5");
        String InvoiceAmount = xmlResponse.getVal("Value6");
        GenLog("String BusinessArea::>>" + BusinessArea, "T");
        GenLog("String vendorname::>>" + vendorname, "T");
        GenLog("String InvoiceType::>>" + InvoiceType, "T");
        GenLog("String InvoiceNumber::>>" + InvoiceNumber, "T");
        GenLog("String InvoiceDate::>>" + InvoiceDate, "T");

        if (InvoiceAmount.contains(".")) {
            paise = InvoiceAmount.substring(InvoiceAmount.indexOf(".") + 1);
            InvoiceAmount = InvoiceAmount.substring(0, InvoiceAmount.indexOf("."));
            ParsedAmount = getIndianCurrencyFormat(InvoiceAmount);
            ParsedAmount = ParsedAmount + "." + paise;
        } else {
            ParsedAmount = getIndianCurrencyFormat(InvoiceAmount);
            GenLog("InvoiceAmount:" + ParsedAmount, "T");
        }
        GenLog("String InvoiceAmount::>>" + ParsedAmount, "T");
        String styleTag = "<style>.buttons{ font-family: Tahoma;font-size:20px;font-weight: bold} .button {background-color: #4CAF50;border: none;color: white;height: 32px;width: 120px;text-align: center;";
        styleTag = styleTag + "text-decoration: none;display: inline-block;font-size: 16px;margin: 4px 2px;cursor: pointer;font-weight: bold;font-family:Tahoma;}";
        styleTag = styleTag + ".button3 {background-color: #f44336;}#trans {border-collapse: collapse;width: 800px;font-family: Tahoma;font-size:13px;}";
        styleTag = styleTag + "#cols {border-collapse: collapse;width: 800px;font-family: Tahoma;font-size:13px;}#trans td{\n";
        styleTag = styleTag + "height:35px;border: 2px solid black;padding: 8px;color: black;width: 100px;text-align: center;}";
        styleTag = styleTag + "#cols td{height:35px;border: 2px solid black;padding: 8px;color: black;text-align: center;}";
        styleTag = styleTag + "#trans tr{height:55px;}#trans th {background-color:  #E4DAE8;color: white;border: 2px solid black;padding: 8px;}";
        styleTag = styleTag + "#cols th {background-color:  #E4DAE8;color: white;text-align: center;border: 2px solid black;padding: 6px;}</style>";
        String seqid_temp = selectQuery("SELECT NEXT VALUE FOR SeqPTP", 1);
        xmlResponse = new WFXmlResponse(seqid_temp);
        String seqid = xmlResponse.getVal("Value1");
        String scriptTag = "<script language=\"javascript\" type=\"text/javascript\">";
        scriptTag = scriptTag + "function approve() {";
        if (ptype.contains("Invoice")) {
            if (Activityname.equalsIgnoreCase("IPT_Standard") || Activityname.equalsIgnoreCase("IPT_Rework_Standard")) {
                scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=PTPInvoice&seqid=" + seqid + "&action=approve\";";
                scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
                scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
                scriptTag = scriptTag + "function reject() {";
                scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=PTPInvoice&seqid=" + seqid + "&action=reject\";";
                scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
                scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
            } else if (Activityname.equalsIgnoreCase("Approver")) {
                scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=PTPInvoice&seqid=" + seqid + "&action=approve\";";
                scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
                scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
                scriptTag = scriptTag + "function reject() {";
                scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=PTPInvoice&seqid=" + seqid + "&action=reject\";";
                scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
                scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
            }
        }

        scriptTag = scriptTag + "</script>";

        String button1CSS = "padding-top: 5px;border: none;color:#4CAF50;text-decoration: none;display: inline-block;font-size: 24px;cursor: pointer;font-weight: bold;font-family:Tahoma;";
        String button2CSS = "padding-top: 5px;border: none;color:#f44336;text-decoration: none;display: inline-block;font-size: 24px;cursor: pointer;font-weight: bold;font-family:Tahoma;";

        String tableContent = "<body><table id=\"cols\"><tbody> <tr>";
        tableContent = tableContent + "<td style=\"width: 120px;height:80px;background-color:#E4DAE8 \"><img src=\"" + DiageoLogo + "\"  alt=\"Diageo India\" width=\"115\" height=\"50\"></td>";
        tableContent = tableContent + "<td style=\"font-size: 22px;background-color:#E4DAE8 \">INVOICE PROCESSING<span style=\"font-size: 18px \"><br>" + (processinstanceid) + "</br></span></td></tr></table>";

        tableContent = tableContent + "<table id=\"trans\"><tbody><tr>";
        tableContent = tableContent + "<td><b>Business Area</b> <br>" + BusinessArea + "</td><td><b>Supplier Name</b><br>" + vendorname + "</td><td><b>Type Of Invoice</b><br>" + InvoiceType + "</td></tr><tr>";
        tableContent = tableContent + "<td><b>Invoice Number</b> <br>" + InvoiceNumber + "</td><td><b>Invoice Date</b><br>" + InvoiceDate + "</td><td><b>Invoice Amount</b><br>" + ParsedAmount + "</td></tr>";
        tableContent = tableContent + "</tr></tbody></table><table id=\"cols\"><tbody>";

        tableContent = tableContent + "<th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Transaction Details</th></tbody></table>";
        tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";
        tableContent = tableContent + "<td><b>S.No</b></td><td><b>General Ledger Code</b></td><td><b>CostCenter</b></td><td><b>Description</b></td><td><b>Total Amount</b></td></tr><tr>";
        String PTP_NonPO_LineItem = selectQuery("SELECT  InvoiceDocumentItem,GLCode,CostCentreNumber,LineItemText,ItemAmount FROM CMPLX_PTP_NonPO_LineItem with (nolock) WHERE ProcessInstID='" + processinstanceid + "'", 5);
        xmlResponse = new WFXmlResponse(PTP_NonPO_LineItem);
        GenLog("xmlResponse PTP_Nonlineitem****" + xmlResponse, "T");
        xmllist = xmlResponse.createList("DataList", "Data");
        for (; xmllist.hasMoreElements(true); xmllist.skip(true)) {
            InvoiceDocItem = xmllist.getVal("Value1").trim();
            GenLog("InvoiceDocItem****" + InvoiceDocItem, "T");
            glcode = xmllist.getVal("Value2").trim();
            GenLog("glcode****" + glcode, "T");
            desc = xmllist.getVal("Value4").trim();
            GenLog("desc****" + desc, "T");
            amount = xmllist.getVal("Value5").trim();
            GenLog("amount****" + amount, "T");
            if (amount.contains(".")) {
                paise = amount.substring(amount.indexOf(".") + 1);
                amount = amount.substring(0, amount.indexOf("."));
                ParsedAmount = getIndianCurrencyFormat(amount);
                ParsedAmount = ParsedAmount + "." + paise;
            } else {
                ParsedAmount = getIndianCurrencyFormat(amount);
                GenLog("InvoiceAmount:" + ParsedAmount, "T");
            }
            GenLog("String InvoiceAmount::>>" + ParsedAmount, "T");
            costcenter = xmllist.getVal("Value3").trim();
            GenLog("costcenter****" + costcenter, "T");
            String glcode_des = selectQuery("SELECT GLDescription FROM MST_PTP_GLCode with (nolock) WHERE GlCode='" + glcode + "'", 1);
            String costcenter_des = selectQuery("SELECT Description FROM MST_PTP_CostCentre with (nolock) WHERE CostCentre='" + costcenter + "'", 1);
            xmlResponse = new WFXmlResponse(glcode_des);
            GenLog("String glcode_desc::>>" + glcode_des, "T");
            String glcode_desc = xmlResponse.getVal("Value1");
            xmlResponse = new WFXmlResponse(costcenter_des);
            GenLog("String costcenter_desc::>>" + costcenter_des, "T");
            String costcenter_desc = xmlResponse.getVal("Value1");
            GLCode = new StringBuffer(glcode_desc);
            GLCode.append("<br>(" + glcode + ")</br>");
            CostCenter = new StringBuffer(costcenter_desc);
            CostCenter.append("<br>(" + costcenter + ")</br>");
            GenLog("GLCode****" + GLCode, "T");
            GenLog("CostCenter****" + CostCenter, "T");
            tableContent = tableContent + "<td>" + InvoiceDocItem + "</td><td>" + GLCode + "</td><td>" + CostCenter + "</td><td>" + desc + "</td><td>" + ParsedAmount + "</td></tr>";

        }
        tableContent = tableContent + "</tbody></table>";

        String WHTCheck = selectQuery("SELECT count(1) FROM CMPLX_PTP_NonPO_WHT WITH (nolock) WHERE ProcessInstID='" + processinstanceid + "'", 1);
        xmlResponse = new WFXmlResponse(WHTCheck);
        GenLog("WHTCheck Query::>>" + WHTCheck, "T");
        int WHTCheckcount = Integer.parseInt(xmlResponse.getVal("Value1"));

        if (WHTCheckcount > 0) {
            tableContent = tableContent + "<table id=\"cols\"><th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">WHT Details</th></tbody></table>";
            tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";
            tableContent = tableContent + "<td><b>S.No</b></td><td><b>WHT Tax Code</b></td><td><b>Base Amount</b></td></tr>";
            String WHT = selectQuery("SELECT WHTCode,WHTBaseAmount FROM CMPLX_PTP_NonPO_WHT WITH (nolock) WHERE ProcessInstID='" + processinstanceid + "'", 2);
            xmlResponse = new WFXmlResponse(WHT);
            GenLog("WHT****" + xmlResponse, "T");
            xmllist = xmlResponse.createList("DataList", "Data");
            int count = 0;
            for (; xmllist.hasMoreElements(true); xmllist.skip(true)) {
                count++;
                WHTTaxcode = xmllist.getVal("Value1").trim();
                GenLog("WHTTaxcode****" + WHTTaxcode, "T");
                baseamount = xmllist.getVal("Value2").trim();
                GenLog("baseamount****" + baseamount, "T");
                if (baseamount.contains(".")) {
                    paise = baseamount.substring(baseamount.indexOf(".") + 1);
                    baseamount = baseamount.substring(0, baseamount.indexOf("."));
                    ParsedAmount = getIndianCurrencyFormat(baseamount);
                    ParsedAmount = ParsedAmount + "." + paise;
                } else {
                    ParsedAmount = getIndianCurrencyFormat(baseamount);
                    GenLog("InvoiceAmount:" + ParsedAmount, "T");
                }
                String WHTTaxcode_des = selectQuery("SELECT TaxDescription FROM  MST_VM_Tax WITH(nolock) WHERE Taxtype='" + WHTTaxcode + "'", 1);
                xmlResponse = new WFXmlResponse(WHTTaxcode_des);
                GenLog("WHTTaxcode_des****" + WHTTaxcode_des, "T");
                GenLog("String transactiondetails::>>" + WHTTaxcode_des, "T");
                String WHTTaxcode_desc = xmlResponse.getVal("Value1");
                WHTcode = new StringBuffer(WHTTaxcode_desc);
                WHTcode.append("<br>(" + WHTTaxcode + ")</br>");
                GenLog("WHTcode****" + WHTcode, "T");
                tableContent = tableContent + "<tr><td>" + count + "</td><td>" + WHTcode + "</td><td>" + ParsedAmount + "</td></tr>";

            }

            tableContent = tableContent + "</tbody></table>";
        } else {
            GenLog("No WHTDetails present****", "T");
        }
        tableContent = tableContent + "<table id=\"cols\"><tbody><th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Approval Flow</th></tbody></table>";
        tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";

        GenLog("Activityname****" + Activityname, "T");
        if (ptype.contains("Invoice")) {
            if (Activityname.equalsIgnoreCase("Approver")) {
                if (!SA_CurrentLoop.equalsIgnoreCase("") && !SA_Total.equalsIgnoreCase("")) {
                    int temp_Currentloop = Integer.parseInt(SA_CurrentLoop) - 1;
                    int temp1_TotalLoop = Integer.parseInt(SA_Total);
                    for (int i1 = 0; i1 < temp_Currentloop; i1++) {
                        ApprovalQuery = selectQuery("SELECT TOP 1 convert(NVARCHAR,processedDate,100) FROM CommentsHistory_PTP WHERE ProcessInstID = '" + processinstanceid + "' AND UserName='" + SA_ListofApprover[i1] + "' AND QueueName='" + Activityname + "' ORDER BY INSERTIONORDERID desc", 1);
                        xmlResponse = new WFXmlResponse(ApprovalQuery);
                        GenLog("ApprovalQuery::>>" + ApprovalQuery, "T");
                        ApprovalDate = xmlResponse.getVal("Value1");
                        tableContent = tableContent + "<td><b>Approver" + (i1 + 1) + "</b><br>" + CurrentUserMailID[i1] + "</br><img src=\"" + AppLogo + "\" width=\"105\" height=\"85\"><br><span style=\"height:22px;color:black;font-size: 14px;font-weight: bold\">Approved On: " + ApprovalDate + "</span></br></td>";

                    }
                    for (int i2 = temp_Currentloop; i2 < temp1_TotalLoop; i2++) {
                        tableContent = tableContent + "<td><b>Approver" + (i2 + 1) + "</b><br>" + SA_CurrentApproverEmailID + "<br><img src=\"" + PendLogo + "\" width=\"97\" height=\"75\" style=\"padding-top: 7px;transform: rotate(-7deg);\"><br><span style=\"height:22px;color:black;font-size: 14px;font-weight: bold\">Status:Pending for Approval</span></br></td>";
                    }
                }
            }
        }
        tableContent = tableContent + "</tr></tbody></table><table id=\"cols\"><tbody><tr  style=\"height:10px;\">";
        tableContent = tableContent + "<td style=\"width:150px;background-color: #E4DAE8\"><b>Previous User Comments</b></td><td>" + comments + "</td>";
        tableContent = tableContent + "</tr></table><br>";
        tableContent = tableContent + "<table style=\"width:800px;align:center\">";
        tableContent = tableContent + "<tbody><tr class=\"buttons\" style=\"height:50px;\">";
        tableContent = tableContent + "<td style=\"padding-left:120px\">";
        if (ptype.contains("Invoice")) {
            if (Activityname.equalsIgnoreCase("IPT_Standard") || Activityname.equalsIgnoreCase("IPT_Rework_Standard")) {

                tableContent = tableContent + "<a href=\"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=PTPInvoice&seqid=" + seqid + "&action=approve\" onClick=\"return approve()\">";
                tableContent = tableContent + "<img src=\"" + url + "/webdesktop/resources/images/approve_icon.png\" alt=\"APPROVE\" style=\"width:137px;height:40px;border:0\"></a>";
                tableContent = tableContent + "</td><td>";
                tableContent = tableContent + "<a href=\"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=PTPInvoice&seqid=" + seqid + "&action=reject\" onClick=\"return reject()\">";
                tableContent = tableContent + "<img src=\"" + url + "/webdesktop/resources/images/reject_icon.png\" alt=\"REJECT\" style=\"width:133px;height:40px;border:0\"></a>";
            } else {
                tableContent = tableContent + "<a href=\"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=PTPInvoice&seqid=" + seqid + "&action=approve\" onClick=\"return approve()\">";
                tableContent = tableContent + "<img src=\"" + url + "/webdesktop/resources/images/approve_icon.png\" alt=\"APPROVE\" style=\"width:137px;height:40px;border:0\"></a>";
                tableContent = tableContent + "</td><td>";
                tableContent = tableContent + "<a href=\"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=PTPInvoice&seqid=" + seqid + "&action=reject\" onClick=\"return reject()\">";
                tableContent = tableContent + "<img src=\"" + url + "/webdesktop/resources/images/reject_icon.png\" alt=\"REJECT\" style=\"width:133px;height:40px;border:0\"></a>";

            }
        }
        tableContent = tableContent + "</td></tr></tbody></table><br>";
        tableContent = tableContent + "<div style=\"font-family: Tahoma; font-size: 12px; color: #696969;padding-bottom: 10px\">";
        tableContent = tableContent + "<em>This is an automated E-Mail. Replies to this E-Mail are not being monitored. PLEASE DO NOT REPLY TO THIS MESSAGE.</em></div></body>";

        try {
            GenLog("strSessionId::::" + SessionId, "T");
            GenLog("SA_CurrentApproverEmailID::::" + SA_CurrentApproverEmailID, "T");
            GenLog("strIP::::" + url, "T");
            strInputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            strInputXml += "<WFAddToMailQueue_Input>";
            strInputXml += "<Option>WFAddToMailQueue</Option>";
            strInputXml = (strInputXml + "<EngineName>" + CabinetName + "</EngineName>");
            strInputXml = (strInputXml + "<SessionId>" + SessionId + "</SessionId>");
            strInputXml += "<MailFrom>workflow@diageo.com</MailFrom>";
            strInputXml = (strInputXml + "<MailTo>" + SA_CurrentApproverEmailID/*toAddress*/ + "</MailTo>");
            strInputXml = (strInputXml + "<MailCC>" + "" + "</MailCC>");
            strInputXml = (strInputXml + "<MailSubject>A PTP " + ptype + " Request under workitem " + processinstanceid + " has been assigned to you for your review and approval.</MailSubject>");
            strInputXml += "<ContentType>text/html;charset=UTF-8</ContentType>";
            strInputXml += "<AttachmentISIndex>";
            strInputXml += "</AttachmentISIndex>";
            strInputXml += "<AttachmentNames>";
            strInputXml += "</AttachmentNames>";
            strInputXml += "<Priority>1</Priority>";
            strInputXml += "<Comments>MailApprovalSystem</Comments>";
            strInputXml += "<MailActionType>TRIGGER</MailActionType>";
            strInputXml += "<ProcessDefId></ProcessDefId>";
            strInputXml += "<ProcessInstanceId>" + processinstanceid + "</ProcessInstanceId>";
            strInputXml += "<WorkitemId>1</WorkitemId><ActivityId></ActivityId>";
            strInputXml += "<MailMessage>";
            strInputXml += "<html>" + scriptTag + styleTag + tableContent + "</html>";
            strInputXml += "</MailMessage>";
            strInputXml += "</WFAddToMailQueue_Input>";
            GenLog("Email Input XML::" + strInputXml, "X");
            strOutputXML = WFCallBroker.execute(strInputXml, "127.0.0.1", 3333, 0);
            GenLog("Email Output XML::" + strOutputXML, "X");
            WFXmlResponse outParser = new WFXmlResponse(strOutputXML);
            String mainCode = outParser.getVal("MainCode");
            if (mainCode.equals("0")) {
                GenLog("mailTrigger_PTP Query is Executed successfully", "T");
            } else {
                GenLog("mailTrigger_PTP Query is not Executed successfully", "E");
            }
        } catch (Exception e) {
            GenLog("Exception in mailTrigger Function..: " + e.getMessage(), "E");
        }
    }

    /*
     * ----------------------------------------------------------------------------------
     * Function Name : mailTrigger 
     * Description   : Custom mail approval template API
     * Return Value  : Null 
     * -----------------------------------------------------------------------------------
     */
    public static void mailTrigger_RTR(String PId, String toAddress, String Approver1Id, String Approver2Id, String Comments) {
        String strOutputXML = "";
        String strInputXml = "";
        GenLog("ApproveLogo-->> " + ApproveLogo, "T");
        GenLog("PendingLogo-->> " + PendingLogo, "T");
        String SequenceId = "SELECT NEXT VALUE FOR SeqRTR";
        String sOutput = selectQuery(SequenceId, 1);
        WFXmlResponse xmlResponse = new WFXmlResponse(sOutput);
        String sequenceId = xmlResponse.getVal("Value1");

        String styleTag = "<style>.buttons{ font-family: Tahoma;font-size:20px;font-weight: bold} .button {background-color: #4CAF50;border: none;color: white;height: 32px;width: 120px;text-align: center;";
        styleTag = styleTag + "text-decoration: none;display: inline-block;font-size: 16px;margin: 4px 2px;cursor: pointer;font-weight: bold;font-family:Tahoma;}";
        styleTag = styleTag + ".button3 {background-color: #f44336;}#trans {border-collapse: collapse;width: 800px;font-family: Tahoma;font-size:13px;}";
        styleTag = styleTag + "#cols {border-collapse: collapse;width: 800px;font-family: Tahoma;font-size:13px;}#trans td{\n";
        styleTag = styleTag + "height:35px;border: 2px solid black;padding: 8px;color: black;width: 100px;text-align: center;}";
        styleTag = styleTag + "#cols td{height:35px;border: 2px solid black;padding: 8px;color: black;text-align: center;}";
        styleTag = styleTag + "#trans tr{height:55px;}#trans th {background-color:  #E4DAE8;color: white;border: 2px solid black;padding: 8px;}";
        styleTag = styleTag + "#cols th {background-color:  #E4DAE8;color: white;text-align: center;border: 2px solid black;padding: 6px;}</style>";

        String scriptTag = "<script language=\"javascript\" type=\"text/javascript\">";

        scriptTag = scriptTag + "function approve() {";
        scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + Approver2Id + "&pid=" + PId + "&process=RTR&seqid=RTR-" + sequenceId + "&action=approve\";";
        scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
        scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
        scriptTag = scriptTag + "function reject() {";
        scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + Approver2Id + "&pid=" + PId + "&process=RTR&seqid=RTR-" + sequenceId + "&action=reject\";";
        scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
        scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";

        scriptTag = scriptTag + "</script>";

//        //Fetch Transaction Attachments
//        String fetchDocuments = "select convert(NVARCHAR,ImageIndex)+'~'+Name+'~'+AppName from PDBDocument (nolock) where documentindex in (select documentindex from pdbfolder (nolock) tb1, pdbdocumentcontent(nolock) tb2 where tb1.folderindex=tb2.parentfolderindex AND tb1.name='" + PId + "')";
//        GenLog("fetchDocuments :" + fetchDocuments);
//        List DocList = formObject.getDataFromDataSource(fetchDocuments);
//        String[] DocList_arr = null;
//        String Doc_Index = "";
//        String Doc_Names = "";
//        if (!DocList.isEmpty()) {
//            String values = DocList.toString();
//            values = values.replace("[", "");
//            values = values.replace("]", "");
//            DocList_arr = values.split(",");
//
//            for (int i = 0; i < DocList_arr.length; i++) {
//                String temp[] = DocList_arr[i].split("~");
//                GenLog("Doc_Index :" + temp[0]);
//                GenLog("Doc_Names :" + temp[1] + "." + temp[2]);
//                Doc_Index = Doc_Index + temp[0].trim() + "#" + "1" + "#;";
//                Doc_Names = Doc_Names + temp[1].trim() + "." + temp[2].trim() + ";";
//            }
//        }
        String fetchQry = "SELECT isnull(CompanyCode,'')+'`'+isnull(JournalType,'')+'`'+isnull(JournalValue,'')+'`'+isnull(CONVERT(NVARCHAR,INITIATOREDATE,105),'')+'`'+isnull(INITIATORUSER,'')+'`'+isnull(INITIATOREMAIL,'')+'`'+isnull(BusinessArea,'')+'`'+isnull(DocumentType,'') FROM EXT_RTR WITH(NOLOCK) WHERE PROCESSINSTID='" + PId + "'";
        GenLog("fetchQry-->> " + fetchQry, "T");
        String sOutputXml = CommonMethods.selectQuery(fetchQry, 1);
        WFXmlResponse xResponse = new WFXmlResponse(sOutputXml);
        String workitem = "";
        String paise, ParsedAmount = "";
        workitem = xResponse.getVal("Value1");
        GenLog("fetched Data-->> " + workitem, "T");
        String[] workitemData = workitem.split("`");
        if (workitemData.length > 7) {
            String CompanyCode = workitemData[0];
            String JournalType = workitemData[1];
            String JournalValue = workitemData[2];
            String InitiatedDate = workitemData[3];
            String InitiatedBy = workitemData[4];
            String InitiatorMail = workitemData[5];
            String BusinessArea = workitemData[6];
            String DocumentType = workitemData[7];
            if (JournalValue.contains(".")) {
                paise = JournalValue.substring(JournalValue.indexOf(".") + 1);
                JournalValue = JournalValue.substring(0, JournalValue.indexOf("."));
                ParsedAmount = getIndianCurrencyFormat(JournalValue);
                ParsedAmount = ParsedAmount + "." + paise;
            } else {
                ParsedAmount = getIndianCurrencyFormat(JournalValue);
                GenLog("InvoiceAmount:" + ParsedAmount, "T");
            }
            String tableContent = "<body><table id=\"cols\"><tbody> <tr>";
            tableContent = tableContent + "<td style=\"width: 120px;height:80px;background-color:#E4DAE8 \"><img src=\"" + DiageoLogo + "\"  alt=\"Diageo India\" width=\"115\" height=\"50\"></td>";
            tableContent = tableContent + "<td style=\"font-size: 22px;background-color:#E4DAE8 \"><b>" + PId + "</b></td></tr></table>";

            tableContent = tableContent + "<table id=\"trans\"><tbody><tr>";
            tableContent = tableContent + "<td><b>Company Code</b> <br>" + CompanyCode + "</td><td><b>Journal Type</b><br>" + JournalType + "</td><td><b>Journal Value</b><br>" + ParsedAmount + "</td>";
            tableContent = tableContent + "</tr></tbody></table><table id=\"cols\"><tbody>";
            tableContent = tableContent + "<th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Transaction Details</th></tbody></table>";

            tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";
            tableContent = tableContent + "<td><b>Initiated Date</b></td><td><b>Initiator Name</b></td><td><b>Initiator MailId</b></td></tr><tr>";
            tableContent = tableContent + "<td>" + InitiatedDate + "</td><td>" + InitiatedBy + "</td><td>" + InitiatorMail + "</td>";
            tableContent = tableContent + "</tr></tbody></table>";

            tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";
            tableContent = tableContent + "<td><b>Business Area</b></td><td><b>Document Type</b></td></tr><tr>";
            tableContent = tableContent + "<td>" + BusinessArea + "</td><td>" + DocumentType + "</td>";
            tableContent = tableContent + "</tr></tbody></table>";

            //Approval flow
            tableContent = tableContent + "<table id=\"cols\"><tbody><th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Approval Flow</th></tbody></table>";
            tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";
            tableContent = tableContent + "<td><b>Approval-L1</b><br>" + Approver1Id + "<br><img src=\"" + ApproveLogo + "\" width=\"105\" height=\"85\"></td>";
            tableContent = tableContent + "<td><b>Approval-L2</b><br>" + Approver2Id + "<br><img src=\"" + PendingLogo + "\" width=\"97\" height=\"75\" style=\"padding-top: 7px;transform: rotate(-7deg);\"></td>";
            tableContent = tableContent + "</tr></tbody></table>";

            tableContent = tableContent + "<table id=\"cols\"><tbody><tr  style=\"height:10px;\">";
            tableContent = tableContent + "<td style=\"width:150px;background-color: #E4DAE8\"><b>Comments</b></td><td>" + Comments + "</td>";
            tableContent = tableContent + "</tr></table><br>";

            tableContent = tableContent + "<table style=\"width:800px;align:center\">";
            tableContent = tableContent + "<tbody><tr class=\"buttons\" style=\"height:50px;\">";
            tableContent = tableContent + "<td style=\"padding-left:120px\">";

            tableContent = tableContent + "<a href=\"" + url + "/mailapproval/mailApproval.jsp?approver=" + Approver2Id + "&pid=" + PId + "&process=RTR&seqid=RTR-" + sequenceId + "&action=approve\" onClick=\"return approve()\">";
            tableContent = tableContent + "<img src=\"" + url + "/webdesktop/resources/images/approve_icon.png\" alt=\"APPROVE\" style=\"width:137px;height:40px;border:0\"></a>";
            tableContent = tableContent + "</td><td>";
            tableContent = tableContent + "<a href=\"" + url + "/mailapproval/mailApproval.jsp?approver=" + Approver2Id + "&pid=" + PId + "&process=RTR&seqid=RTR-" + sequenceId + "&action=reject\" onClick=\"return reject()\">";
            tableContent = tableContent + "<img src=\"" + url + "/webdesktop/resources/images/reject_icon.png\" alt=\"REJECT\" style=\"width:133px;height:40px;border:0\"></a>";

            tableContent = tableContent + "</td></tr></tbody></table><br>";

            tableContent = tableContent + "<div style=\"font-family: Tahoma; font-size: 12px; color: #696969;\">";
            tableContent = tableContent + "<em>This is an automated E-Mail. Replies to this E-Mail are not being monitored. PLEASE DO NOT REPLY TO THIS MESSAGE.</em></div></body>";

            try {
                GenLog("strSessionId::::" + SessionId, "T");
                GenLog("strIP::::" + IPAddress, "T");
                strInputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                strInputXml += "<WFAddToMailQueue_Input>";
                strInputXml += "<Option>WFAddToMailQueue</Option>";
                strInputXml = (strInputXml + "<EngineName>" + CabinetName + "</EngineName>");
                strInputXml = (strInputXml + "<SessionId>" + SessionId + "</SessionId>");
                strInputXml += "<MailFrom>workflow@diageo.com</MailFrom>";
                strInputXml = (strInputXml + "<MailTo>" + toAddress + "</MailTo>");
                strInputXml = (strInputXml + "<MailCC>" + "" + "</MailCC>");
                strInputXml = (strInputXml + "<MailSubject>A Journal Voucher Entry " + JournalType + " Request under workitem " + PId + " has been assigned for your review & approval.</MailSubject>");
                strInputXml += "<ContentType>text/html;charset=UTF-8</ContentType>";
                strInputXml += "<AttachmentISIndex>";
//            strInputXml += Doc_Index;
                strInputXml += "</AttachmentISIndex>";
                strInputXml += "<AttachmentNames>";
//            strInputXml += Doc_Names;
                strInputXml += "</AttachmentNames>";
                strInputXml += "<Priority>1</Priority>";
                strInputXml += "<Comments>MailApprovalSystem</Comments>";
                strInputXml += "<MailActionType>TRIGGER</MailActionType>";
                strInputXml += "<ProcessDefId></ProcessDefId>";
                strInputXml += "<ProcessInstanceId>" + PId + "</ProcessInstanceId>";
                strInputXml += "<WorkitemId>1</WorkitemId><ActivityId></ActivityId>";
                strInputXml += "<MailMessage>";
                strInputXml += "<html>" + scriptTag + styleTag + tableContent + "</html>";
                strInputXml += "</MailMessage>";
                strInputXml += "</WFAddToMailQueue_Input>";
                GenLog("Email Input XML::" + strInputXml, "X");
                strOutputXML = WFCallBroker.execute(strInputXml, "127.0.0.1", 3333, 0);
                GenLog("Email Output XML::" + strOutputXML, "X");
                WFXmlResponse outParser = new WFXmlResponse(strOutputXML);
                String mainCode = outParser.getVal("MainCode");
                if (mainCode.equals("0")) {
                    GenLog("mailTrigger Query is Executed successfully", "T");
                } else {
                    GenLog("mailTrigger Query is not Executed successfully", "E");
                }
            } catch (Exception e) {
                GenLog("Exception in mailTrigger Function..: " + e.getMessage(), "E");
            }
        } else {
            GenLog(PId + "Error in fetching workitem data", "T");
        }
    }

    /*
     * ----------------------------------------------------------------------------------
     * Function Name : mailTrigger ATL
     * Description   : Custom mail approval template API
     * Return Value  : Null 
     * -----------------------------------------------------------------------------------
     */
    public static void mailTrigger_ATL(String PId, String toAddress, String Approver1Id, String Approver2Id, String Comments) {
        String strOutputXML = "";
        String strInputXml = "";
        GenLog("ApproveLogo-->> " + ApproveLogo, "T");
        GenLog("PendingLogo-->> " + PendingLogo, "T");
        String SequenceId = "SELECT NEXT VALUE FOR SeqRTR";
        String sOutput = selectQuery(SequenceId, 1);
        WFXmlResponse xmlResponse = new WFXmlResponse(sOutput);
        String sequenceId = xmlResponse.getVal("Value1");

        String styleTag = "<style>.buttons{ font-family: Tahoma;font-size:20px;font-weight: bold} .button {background-color: #4CAF50;border: none;color: white;height: 32px;width: 120px;text-align: center;";
        styleTag = styleTag + "text-decoration: none;display: inline-block;font-size: 16px;margin: 4px 2px;cursor: pointer;font-weight: bold;font-family:Tahoma;}";
        styleTag = styleTag + ".button3 {background-color: #f44336;}#trans {border-collapse: collapse;width: 800px;font-family: Tahoma;font-size:13px;}";
        styleTag = styleTag + "#cols {border-collapse: collapse;width: 800px;font-family: Tahoma;font-size:13px;}#trans td{\n";
        styleTag = styleTag + "height:35px;border: 2px solid black;padding: 8px;color: black;width: 100px;text-align: center;}";
        styleTag = styleTag + "#cols td{height:35px;border: 2px solid black;padding: 8px;color: black;text-align: center;}";
        styleTag = styleTag + "#trans tr{height:55px;}#trans th {background-color:  #E4DAE8;color: white;border: 2px solid black;padding: 8px;}";
        styleTag = styleTag + "#cols th {background-color:  #E4DAE8;color: white;text-align: center;border: 2px solid black;padding: 6px;}</style>";

        String scriptTag = "<script language=\"javascript\" type=\"text/javascript\">";

        scriptTag = scriptTag + "function approve() {";
        scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + Approver2Id + "&pid=" + PId + "&process=RTR&seqid=RTR-" + sequenceId + "&action=approve\";";
        scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
        scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
        scriptTag = scriptTag + "function reject() {";
        scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + Approver2Id + "&pid=" + PId + "&process=RTR&seqid=RTR-" + sequenceId + "&action=reject\";";
        scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
        scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";

        scriptTag = scriptTag + "</script>";

//        //Fetch Transaction Attachments
//        String fetchDocuments = "select convert(NVARCHAR,ImageIndex)+'~'+Name+'~'+AppName from PDBDocument (nolock) where documentindex in (select documentindex from pdbfolder (nolock) tb1, pdbdocumentcontent(nolock) tb2 where tb1.folderindex=tb2.parentfolderindex AND tb1.name='" + PId + "')";
//        GenLog("fetchDocuments :" + fetchDocuments);
//        List DocList = formObject.getDataFromDataSource(fetchDocuments);
//        String[] DocList_arr = null;
//        String Doc_Index = "";
//        String Doc_Names = "";
//        if (!DocList.isEmpty()) {
//            String values = DocList.toString();
//            values = values.replace("[", "");
//            values = values.replace("]", "");
//            DocList_arr = values.split(",");
//
//            for (int i = 0; i < DocList_arr.length; i++) {
//                String temp[] = DocList_arr[i].split("~");
//                GenLog("Doc_Index :" + temp[0]);
//                GenLog("Doc_Names :" + temp[1] + "." + temp[2]);
//                Doc_Index = Doc_Index + temp[0].trim() + "#" + "1" + "#;";
//                Doc_Names = Doc_Names + temp[1].trim() + "." + temp[2].trim() + ";";
//            }
//        }
        String fetchQry = "SELECT isnull(CompanyCode,'')+'`'+isnull(JournalType,'')+'`'+isnull(JournalValue,'')+'`'+isnull(CONVERT(NVARCHAR,INITIATIONEDATE,105),'')+'`'+isnull(DBSIRTRUSER,'')+'`'+isnull(INITIATOREMAIL,'')+'`'+isnull(BusinessArea,'')+'`'+isnull(DocumentType,'') FROM EXT_ATL WITH(NOLOCK) WHERE PROCESSINSTID='" + PId + "'";
        GenLog("fetchQry-->> " + fetchQry, "T");
        String sOutputXml = CommonMethods.selectQuery(fetchQry, 1);
        WFXmlResponse xResponse = new WFXmlResponse(sOutputXml);
        String workitem = "";
        String paise, ParsedAmount = "";
        workitem = xResponse.getVal("Value1");
        GenLog("fetched Data ATL-->> " + workitem, "T");
        String[] workitemData = workitem.split("`");
        if (workitemData.length > 7) {
            String CompanyCode = workitemData[0];
            String JournalType = workitemData[1];
            String JournalValue = workitemData[2];
            String InitiatedDate = workitemData[3];
            String InitiatedBy = workitemData[4];
            String InitiatorMail = workitemData[5];
            String BusinessArea = workitemData[6];
            String DocumentType = workitemData[7];
            if (JournalValue.contains(".")) {
                paise = JournalValue.substring(JournalValue.indexOf(".") + 1);
                JournalValue = JournalValue.substring(0, JournalValue.indexOf("."));
                ParsedAmount = getIndianCurrencyFormat(JournalValue);
                ParsedAmount = ParsedAmount + "." + paise;
            } else {
                ParsedAmount = getIndianCurrencyFormat(JournalValue);
                GenLog("InvoiceAmount:" + ParsedAmount, "T");
            }
            String tableContent = "<body><table id=\"cols\"><tbody> <tr>";
            tableContent = tableContent + "<td style=\"width: 120px;height:80px;background-color:#E4DAE8 \"><img src=\"" + DiageoLogo + "\"  alt=\"Diageo India\" width=\"115\" height=\"50\"></td>";
            tableContent = tableContent + "<td style=\"font-size: 22px;background-color:#E4DAE8 \"><b>" + PId + "</b></td></tr></table>";

            tableContent = tableContent + "<table id=\"trans\"><tbody><tr>";
            tableContent = tableContent + "<td><b>Company Code</b> <br>" + CompanyCode + "</td><td><b>Journal Type</b><br>" + JournalType + "</td><td><b>Journal Value</b><br>" + ParsedAmount + "</td>";
            tableContent = tableContent + "</tr></tbody></table><table id=\"cols\"><tbody>";
            tableContent = tableContent + "<th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Transaction Details</th></tbody></table>";

            tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";
            tableContent = tableContent + "<td><b>Initiated Date</b></td><td><b>Initiator Name</b></td><td><b>Initiator MailId</b></td></tr><tr>";
            tableContent = tableContent + "<td>" + InitiatedDate + "</td><td>" + InitiatedBy + "</td><td>" + InitiatorMail + "</td>";
            tableContent = tableContent + "</tr></tbody></table>";

            tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";
            tableContent = tableContent + "<td><b>Business Area</b></td><td><b>Document Type</b></td></tr><tr>";
            tableContent = tableContent + "<td>" + BusinessArea + "</td><td>" + DocumentType + "</td>";
            tableContent = tableContent + "</tr></tbody></table>";

            //Approval flow
            tableContent = tableContent + "<table id=\"cols\"><tbody><th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Approval Flow</th></tbody></table>";
            tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";
            tableContent = tableContent + "<td><b>Approval-L1</b><br>" + Approver1Id + "<br><img src=\"" + ApproveLogo + "\" width=\"105\" height=\"85\"></td>";
            tableContent = tableContent + "<td><b>Approval-L2</b><br>" + Approver2Id + "<br><img src=\"" + PendingLogo + "\" width=\"97\" height=\"75\" style=\"padding-top: 7px;transform: rotate(-7deg);\"></td>";
            tableContent = tableContent + "</tr></tbody></table>";

            tableContent = tableContent + "<table id=\"cols\"><tbody><tr  style=\"height:10px;\">";
            tableContent = tableContent + "<td style=\"width:150px;background-color: #E4DAE8\"><b>Comments</b></td><td>" + Comments + "</td>";
            tableContent = tableContent + "</tr></table><br>";

            tableContent = tableContent + "<table style=\"width:800px;align:center\">";
            tableContent = tableContent + "<tbody><tr class=\"buttons\" style=\"height:50px;\">";
            tableContent = tableContent + "<td style=\"padding-left:120px\">";

            tableContent = tableContent + "<a href=\"" + url + "/mailapproval/mailApproval.jsp?approver=" + Approver2Id + "&pid=" + PId + "&process=RTR&seqid=RTR-" + sequenceId + "&action=approve\" onClick=\"return approve()\">";
            tableContent = tableContent + "<img src=\"" + url + "/webdesktop/resources/images/approve_icon.png\" alt=\"APPROVE\" style=\"width:137px;height:40px;border:0\"></a>";
            tableContent = tableContent + "</td><td>";
            tableContent = tableContent + "<a href=\"" + url + "/mailapproval/mailApproval.jsp?approver=" + Approver2Id + "&pid=" + PId + "&process=RTR&seqid=RTR-" + sequenceId + "&action=reject\" onClick=\"return reject()\">";
            tableContent = tableContent + "<img src=\"" + url + "/webdesktop/resources/images/reject_icon.png\" alt=\"REJECT\" style=\"width:133px;height:40px;border:0\"></a>";

            tableContent = tableContent + "</td></tr></tbody></table><br>";

            tableContent = tableContent + "<div style=\"font-family: Tahoma; font-size: 12px; color: #696969;\">";
            tableContent = tableContent + "<em>This is an automated E-Mail. Replies to this E-Mail are not being monitored. PLEASE DO NOT REPLY TO THIS MESSAGE.</em></div></body>";

            try {
                GenLog("strSessionId::::" + SessionId, "T");
                GenLog("strIP::::" + IPAddress, "T");
                strInputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                strInputXml += "<WFAddToMailQueue_Input>";
                strInputXml += "<Option>WFAddToMailQueue</Option>";
                strInputXml = (strInputXml + "<EngineName>" + CabinetName + "</EngineName>");
                strInputXml = (strInputXml + "<SessionId>" + SessionId + "</SessionId>");
                strInputXml += "<MailFrom>workflow@diageo.com</MailFrom>";
                strInputXml = (strInputXml + "<MailTo>" + toAddress + "</MailTo>");
                strInputXml = (strInputXml + "<MailCC>" + "" + "</MailCC>");
                strInputXml = (strInputXml + "<MailSubject>A Journal Voucher Entry " + JournalType + " Request under workitem " + PId + " has been assigned for your review & approval.</MailSubject>");
                strInputXml += "<ContentType>text/html;charset=UTF-8</ContentType>";
                strInputXml += "<AttachmentISIndex>";
//            strInputXml += Doc_Index;
                strInputXml += "</AttachmentISIndex>";
                strInputXml += "<AttachmentNames>";
//            strInputXml += Doc_Names;
                strInputXml += "</AttachmentNames>";
                strInputXml += "<Priority>1</Priority>";
                strInputXml += "<Comments>MailApprovalSystem</Comments>";
                strInputXml += "<MailActionType>TRIGGER</MailActionType>";
                strInputXml += "<ProcessDefId></ProcessDefId>";
                strInputXml += "<ProcessInstanceId>" + PId + "</ProcessInstanceId>";
                strInputXml += "<WorkitemId>1</WorkitemId><ActivityId></ActivityId>";
                strInputXml += "<MailMessage>";
                strInputXml += "<html>" + scriptTag + styleTag + tableContent + "</html>";
                strInputXml += "</MailMessage>";
                strInputXml += "</WFAddToMailQueue_Input>";
                GenLog("Email Input XML::" + strInputXml, "X");
                strOutputXML = WFCallBroker.execute(strInputXml, "127.0.0.1", 3333, 0);
                GenLog("Email Output XML::" + strOutputXML, "X");
                WFXmlResponse outParser = new WFXmlResponse(strOutputXML);
                String mainCode = outParser.getVal("MainCode");
                if (mainCode.equals("0")) {
                    GenLog("mailTrigger Query is Executed successfully", "T");
                } else {
                    GenLog("mailTrigger Query is not Executed successfully", "E");
                }
            } catch (Exception e) {
                GenLog("Exception in mailTrigger Function..: " + e.getMessage(), "E");
            }
        } else {
            GenLog(PId + "Error in fetching workitem data", "T");
        }
    }

    /*
     * ----------------------------------------------------------------------------------
     * Function Name :mailTrigger 
     * Description   :Custom approval mail trigger template
     * Return Value : null
     * -----------------------------------------------------------------------------------
     */
    public static void mailTrigger_OTC(String PId, String Comments) {
        String strInputXml = "", strOutputXML = "", listOfApprover = "", listOfApproverEmailId = "";

        String fetchQry = "SELECT isnull(SA_ListofAppID,'')+'`'+isnull(SA_ListofAppEmailID,'') FROM EXT_CUSTOMERDISPUTE WITH(NOLOCK) WHERE PROCESSINSTID='" + PId + "'";
        String workItemXml = selectQuery(fetchQry, 1);
        WFXmlResponse xmlResponse = new WFXmlResponse(workItemXml);
        String approvalData = xmlResponse.getVal("Value1");
        String[] approvalArr = approvalData.split("`");
        if (approvalArr.length > 0) {
            listOfApprover = approvalArr[0];
            listOfApproverEmailId = approvalArr[1];
        }
        String SequenceXml = selectQuery("SELECT NEXT VALUE FOR SeqOTC", 1);
        xmlResponse = new WFXmlResponse(SequenceXml);
        String SequenceId = xmlResponse.getVal("Value1");

        String[] approvers = listOfApprover.split(",");
        String[] approversEmail = listOfApproverEmailId.split(",");
//        String Approver1 = "";
        String Approver1Mail = "";
        String Approver2 = "";
        String Approver2Mail = "";
        if (approvers.length > 0) {
//            Approver1 = approvers[0];
            Approver2 = approvers[1];
        }
        if (approversEmail.length > 0) {
            Approver1Mail = approversEmail[0];
            Approver2Mail = approversEmail[1];
        }

        String styleTag = "<style>.buttons{ font-family: Tahoma;font-size:20px;font-weight: bold} .button {background-color: #4CAF50;border: none;color: white;height: 32px;width: 120px;text-align: center;";
        styleTag = styleTag + "text-decoration: none;display: inline-block;font-size: 16px;margin: 4px 2px;cursor: pointer;font-weight: bold;font-family:Tahoma;}";
        styleTag = styleTag + ".button3 {background-color: #f44336;}#trans {border-collapse: collapse;width: 800px;font-family: Tahoma;font-size:13px;}";
        styleTag = styleTag + "#cols {border-collapse: collapse;width: 800px;font-family: Tahoma;font-size:13px;}#trans td{\n";
        styleTag = styleTag + "height:35px;border: 2px solid black;padding: 8px;color: black;width: 100px;text-align: center;}";
        styleTag = styleTag + "#cols td{height:35px;border: 2px solid black;padding: 8px;color: black;text-align: center;}";
        styleTag = styleTag + "#trans tr{height:55px;}#trans th {background-color:  #E4DAE8;color: white;border: 2px solid black;padding: 8px;}";
        styleTag = styleTag + "#cols th {background-color:  #E4DAE8;color: white;text-align: center;border: 2px solid black;padding: 6px;}</style>";

        String scriptTag = "<script language=\"javascript\" type=\"text/javascript\">";

        scriptTag = scriptTag + "function approve() {";
        scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + Approver2 + "&pid=" + PId + "&process=OTC&seqid=OTC-" + SequenceId + "&action=approve\";";
        scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
        scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
        scriptTag = scriptTag + "function reject() {";
        scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + Approver2 + "&pid=" + PId + "&process=OTC&seqid=OTC-" + SequenceId + "&action=reject\";";
        scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
        scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";

        scriptTag = scriptTag + "</script>";

//        //Fetch Transaction Attachments
//        String fetchDocuments = "select convert(NVARCHAR,ImageIndex)+'~'+Name+'~'+AppName from PDBDocument (nolock) where documentindex in (select documentindex from pdbfolder (nolock) tb1, pdbdocumentcontent(nolock) tb2 where tb1.folderindex=tb2.parentfolderindex AND tb1.name='" + PId + "')";
//        GenLog("fetchDocuments :" + fetchDocuments, "T");
//        List DocList = formObject.getDataFromDataSource(fetchDocuments);
//        String[] DocList_arr = null;
//        String Doc_Index = "";
//        String Doc_Names = "";
//        if (!DocList.isEmpty()) {
//            String values = DocList.toString();
//            values = values.replace("[", "");
//            values = values.replace("]", "");
//            DocList_arr = values.split(",");
//
//            for (int i = 0; i < DocList_arr.length; i++) {
//                String temp[] = DocList_arr[i].split("~");
//                GenLog("Doc_Index :" + temp[0], "T");
//                GenLog("Doc_Names :" + temp[1] + "." + temp[2], "T");
//                Doc_Index = Doc_Index + temp[0].trim() + "#" + "1" + "#;";
//                Doc_Names = Doc_Names + temp[1].trim() + "." + temp[2].trim() + ";";
//            }
//        }
        String transactionQry = "SELECT isnull(DISPUTETYPE,'')+'`'+isnull(NOTETYPE,'')+'`'+isnull(CONVERT(NVARCHAR,INITIATEDEDATE,105),'')+'`'+isnull(INITIATEDUSER,'')+'`'+isnull(INITIATORMAILID,'') FROM EXT_CUSTOMERDISPUTE WITH(NOLOCK) WHERE PROCESSINSTID='" + PId + "'";
        String transXml = selectQuery(transactionQry, 1);
        xmlResponse = new WFXmlResponse(transXml);
        String workItemData = xmlResponse.getVal("Value1");
        String[] workItemDataArr = workItemData.split("`");
        if (workItemDataArr.length > 3) {
            String DisputeType = workItemDataArr[0];
            String NoteType = workItemDataArr[1];
            String InitiatedDate = workItemDataArr[2];
            String InitiatedUser = workItemDataArr[3];
            String InitiatorMail = workItemDataArr[4];
            String tableContent = "<body><table id=\"cols\"><tbody> <tr>";
            tableContent = tableContent + "<td style=\"width: 120px;height:80px;background-color:#E4DAE8 \"><img src=\"" + DiageoLogo + "\"  alt=\"Diageo India\" width=\"115\" height=\"50\"></td>";
            tableContent = tableContent + "<td style=\"font-size: 22px;background-color:#E4DAE8 \"><b>" + PId + "</b></td></tr></table>";

            tableContent = tableContent + "<table id=\"trans\"><tbody><tr>";
            tableContent = tableContent + "<td><b>Dispute Type</b> <br>" + DisputeType + "</td><td><b>Note Type</b><br>" + NoteType + "</td>";
            tableContent = tableContent + "</tr></tbody></table>";

            tableContent = tableContent + "<table id=\"cols\"><tbody>";
            tableContent = tableContent + "<th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Transaction Details</th></tbody></table>";
            tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";
            tableContent = tableContent + "<td><b>Initiated Date</b></td><td><b>Initiator Name</b></td><td><b>Initiator MailId</b></td></tr><tr>";
            tableContent = tableContent + "<td>" + InitiatedDate + "</td><td>" + InitiatedUser + "</td><td>" + InitiatorMail + "</td>";
            tableContent = tableContent + "</tr></tbody></table>";

            //Approval flow
            tableContent = tableContent + "<table id=\"cols\"><tbody><th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Approval Flow</th></tbody></table>";
            tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";
            tableContent = tableContent + "<td><b>Approval-L1</b><br>" + Approver1Mail + "<br><img src=\"" + ApproveLogo + "\" width=\"105\" height=\"85\"></td>";
            tableContent = tableContent + "<td><b>Approval-L2</b><br>" + Approver2Mail + "<br><img src=\"" + PendingLogo + "\" width=\"97\" height=\"75\" style=\"padding-top: 7px;transform: rotate(-7deg);\"></td>";
            tableContent = tableContent + "</tr></tbody></table>";

            tableContent = tableContent + "<table id=\"cols\"><tbody><tr  style=\"height:10px;\">";
            tableContent = tableContent + "<td style=\"width:150px;background-color: #E4DAE8\"><b>Comments</b></td><td>" + Comments + "</td>";
            tableContent = tableContent + "</tr></table><br>";
            tableContent = tableContent + "<table style=\"width:800px;align:center\">";
            tableContent = tableContent + "<tbody><tr class=\"buttons\" style=\"height:50px;\">";
            tableContent = tableContent + "<td style=\"padding-left:120px\">";

            tableContent = tableContent + "<a href=\"" + url + "/mailapproval/mailApproval.jsp?approver=" + Approver2 + "&pid=" + PId + "&process=OTC&seqid=OTC-" + SequenceId + "&action=approve\" onClick=\"return approve()\">";
            tableContent = tableContent + "<img src=\"" + url + "/webdesktop/resources/images/approve_icon.png\" alt=\"APPROVE\" style=\"width:137px;height:40px;border:0\"></a>";
            tableContent = tableContent + "</td><td>";
            tableContent = tableContent + "<a href=\"" + url + "/mailapproval/mailApproval.jsp?approver=" + Approver2 + "&pid=" + PId + "&process=OTC&seqid=OTC-" + SequenceId + "&action=reject\" onClick=\"return reject()\">";
            tableContent = tableContent + "<img src=\"" + url + "/webdesktop/resources/images/reject_icon.png\" alt=\"REJECT\" style=\"width:133px;height:40px;border:0\"></a>";

            tableContent = tableContent + "</td></tr></tbody></table><br>";

            tableContent = tableContent + "<div style=\"font-family: Tahoma; font-size: 12px; color: #696969;\">";
            tableContent = tableContent + "<em>This is an automated E-Mail. Replies to this E-Mail are not being monitored. PLEASE DO NOT REPLY TO THIS MESSAGE.</em></div></body>";

            try {
                GenLog("strSessionId::::" + SessionId, "T");
                GenLog("strIP::::" + IPAddress, "T");
                strInputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
                strInputXml += "<WFAddToMailQueue_Input>";
                strInputXml += "<Option>WFAddToMailQueue</Option>";
                strInputXml = (strInputXml + "<EngineName>" + CabinetName + "</EngineName>");
                strInputXml = (strInputXml + "<SessionId>" + SessionId + "</SessionId>");
                strInputXml += "<MailFrom>workflow@diageo.com</MailFrom>";
                strInputXml = (strInputXml + "<MailTo>" + Approver2Mail + "</MailTo>");
                strInputXml = (strInputXml + "<MailCC>" + "" + "</MailCC>");
                //mailSubject
                strInputXml = (strInputXml + "<MailSubject>A Customer -  " + DisputeType + "  Dispute under workitem " + PId + " has been assigned for your review and approval.</MailSubject>");
                strInputXml += "<ContentType>text/html;charset=UTF-8</ContentType>";
                strInputXml += "<AttachmentISIndex>";
//            strInputXml += Doc_Index;
                strInputXml += "</AttachmentISIndex>";
                strInputXml += "<AttachmentNames>";
//            strInputXml += Doc_Names;
                strInputXml += "</AttachmentNames>";
                strInputXml += "<Priority>1</Priority>";
                strInputXml += "<Comments>MailApprovalSystem</Comments>";
                strInputXml += "<MailActionType>TRIGGER</MailActionType>";
                strInputXml += "<ProcessDefId></ProcessDefId>";
                strInputXml += "<ProcessInstanceId>" + PId + "</ProcessInstanceId>";
                strInputXml += "<WorkitemId>1</WorkitemId><ActivityId></ActivityId>";
                strInputXml += "<MailMessage>";
                strInputXml += "<html>" + scriptTag + styleTag + tableContent + "</html>";
                strInputXml += "</MailMessage>";
                strInputXml += "</WFAddToMailQueue_Input>";
                GenLog("Email Input XML::" + strInputXml, "T");
                strOutputXML = WFCallBroker.execute(strInputXml, "127.0.0.1", 3333, 0);
                GenLog("Email Output XML::" + strOutputXML, "T");
                WFXmlResponse outParser = new WFXmlResponse(strOutputXML);
                String mainCode = outParser.getVal("MainCode");
                if (mainCode.equals("0")) {
                    GenLog("mailTrigger Query is Executed successfully", "T");
                } else {
                    GenLog("mailTrigger Query is not Executed successfully", "T");
                }
            } catch (Exception e) {
                GenLog("Exception in mailTrigger Function..: " + e.getMessage(), "E");
            }
        }
    }

    public static void mailTrigger_CCPApproval(String PId, String Comments, String Curr_Workstep, String flag) {

        String strInputXml, strXmlData, strOutputXML, scriptTag = "";
        WFXmlResponse xmlCCPResponse, xmlSeqResponse, xmlRTResponse, xmlResponse = null;
        String[] WorkItemData = {};
        String seqid = selectQuery("SELECT NEXT VALUE FOR SeqPTP", 1);
        xmlSeqResponse = new WFXmlResponse(seqid);
        seqid = xmlSeqResponse.getVal("Value1");
        String strObjective, strProblmStmt, strSupportArgs, strIntendOutcome, strImplication, strInitiator, strInitEmailID, strInitOn, strBrand = "", strCity, strGender, strAge = "", strSec, strConsumerCategory, strFreq, strReq, strCCPApprover, strCCPApproverID = "", strCCPAppEmailID = "";
        String PFApprover, PFApproverID, PFApproverEmailID, CPIApprover, CPIApproverID, CPIApproverEmailID, strApproverList = "", strTotal = "", strCurrentLoop = "";
        String strCCPQuery = "SELECT isnull(CAST(ObjectiveOfStudy AS NVARCHAR),'')+'`'+isnull(ProblemStatement,'')+'`'+isnull(SupportingArgs,'')+'`'+isnull(IntendedOutcome,'')+'`'+isnull(Implications,'')+'`'+isnull(INITIATOR,'')+'`'+isnull(INITIATOREMAILID,'')+'`'+isnull(INITIATEDON,'') +'`'+isnull(Brand,'')+'`'+isnull(CAST(City AS NVARCHAR),'')+'`'+isnull(Gender,'')+'`'+isnull(CAST(Age AS NVARCHAR),'')+'`'+isnull(CAST(Sec AS NVARCHAR),'')+'`'+isnull(ConsumerCategory,'')+'`'+isnull(ConsumptionFrequency,'')+'`'+isnull(ConsumptionRecency,'')+'`'+isnull(CCPApprover,'')+'`'+isnull(CCPApproverID,'')+'`'+isnull(CCPApproverEmailID,'')+'`'+isnull(SA_CurrentApproverID,'')+'`'+isnull(SA_CurrentApproverEmailID,'')+'`'+isnull(SA_ListofApprover,'')+'`'+isnull(SA_Total,'')+'`'+isnull(SA_CurrentLoop,'') FROM ext_ccp with (nolock) where processinstid='" + PId + "'";
        String strResearchQuery = "SELECT isnull(ProjectName,'')+'`'+isnull(INITIATOR,'')+'`'+isnull(INITIATOREMAILID,'')+'`'+isnull(INITIATEDON,'')+'`'+isnull(BrandCategory,'')+'`'+isnull(CPContact,'')+'`'+isnull(CAST(FieldWorkCentre AS NVARCHAR),'')+'`'+isnull(audience,'')+'`'+isnull(ResearchAgency,'')+'`'+isnull(PlanFieldDate,'')+'`'+isnull(TestType,'')+'`'+isnull(TestTypeOthers,'')+'`'+isnull(Methodology,'')+'`'+isnull(MethodologyOthers,'')+'`'+isnull(Budget,'')+'`'+isnull(Version,'')+'`'+isnull(Background,'')+'`'+isnull(ResearchObjective,'')+'`'+isnull(PostResearchAction,'')+'`'+isnull(ResearchKeyArea,'')+'`'+isnull(AdditionalInfoArea,'')+'`'+isnull(RespProfile,'')+'`'+isnull(Markets,'')+'`'+isnull(ActionStandard,'')+'`'+isnull(StimulusMaterial,'')+'`'+isnull(Timing,'')+'`'+isnull(OtherDetails,'')+'`'+isnull(QueueDetails,'')+'`'+isnull(FieldWorkCentreOthers,'')+'`'+isnull(ResearchBrand,'')+'`'+isnull(CCPApprover,'')+'`'+isnull(CCPApproverID,'')+'`'+isnull(CCPApproverEmailID,'')+'`'+isnull(PFHeadApprover,'')+'`'+isnull(PFHeadApproverId,'')+'`'+isnull(PFHeadApproverEmailID,'')+'`'+isnull(CPIHeadApprover,'')+'`'+isnull(CPIHeadApproverId,'')+'`'+isnull(CPIHeadApproverEmailID,'')+'`'+isnull(SA_ListofApprover,'')+'`'+isnull(SA_Total,'')+'`'+isnull(SA_CurrentLoop,'')+'`'+isnull(SA_CurrentApproverID,'')+'`'+isnull(SA_CurrentApproverEmailID,'') FROM ext_ccp with (NOLOCK) where processinstid='" + PId + "'";
        String strResearchType = "select ResearchType from ext_CCP with (nolock) where processinstid='" + PId + "'";

        String ResearchTypeXml = selectQuery(strResearchType, 1);
        xmlRTResponse = new WFXmlResponse(ResearchTypeXml);
        String ResearchType = xmlRTResponse.getVal("Value1");
        GenLog("Research Type::::" + ResearchType + " For PID:::" + PId, "T");
        String strSeqID = "";
        String[] strPrevApprovers, strActivityNames = {};
        String strActivty = "", ApproverID = "", ApproverName = "", ApproverEmailID = "", strRBMailId = "", strLMApprovalID, strLMApprovalEmailID = "";

        if (ResearchType.equalsIgnoreCase("Research Brief")) {
            strActivty = "LMApproval,CPTeamApproval,PortfolioHeadApproval,CPIHeadApproval";
            strActivityNames = strActivty.split(",");
        } else if (ResearchType.equalsIgnoreCase("CCP")) {
            strActivty = "LMApproval,CPTeamApproval";
            strActivityNames = strActivty.split(",");
        }

        String sQuery = "Select ConstantValue from CONSTANTDEFTABLE with(nolock) where ConstantName='CONST_CONST_CCP_USLLogo'";
        String DiageoLogo_temp = selectQuery(sQuery, 1);
        xmlResponse = new WFXmlResponse(DiageoLogo_temp);
        String DiageoLogo = xmlResponse.getVal("Value1");

        String sQuery1 = "Select ConstantValue from CONSTANTDEFTABLE with(nolock) where ConstantName='CONST_CONST_CCP_ApproveLogo'";
        String AppLogo_temp = selectQuery(sQuery1, 1);
        xmlResponse = new WFXmlResponse(AppLogo_temp);
        String AppLogo = xmlResponse.getVal("Value1");

        String sQuery2 = "Select ConstantValue from CONSTANTDEFTABLE with(nolock) where ConstantName='CONST_CONST_CCP_PendingLogo'";
        String PendLogo_temp = selectQuery(sQuery2, 1);
        xmlResponse = new WFXmlResponse(PendLogo_temp);
        String PendLogo = xmlResponse.getVal("Value1");

        String sQuery3 = "Select ConstantValue from CONSTANTDEFTABLE with(nolock) where ConstantName='CONST_CONST_CCP_sendermailid'";
        String Activityname_temp = selectQuery(sQuery3, 1);
        xmlResponse = new WFXmlResponse(Activityname_temp);
        String FromEmailID = xmlResponse.getVal("Value1");

        String sQuery4 = "Select ConstantValue from CONSTANTDEFTABLE with(nolock) where ConstantName='CONST_CONST_CCP_AgencyList'";
        String AgencyList_temp = selectQuery(sQuery4, 1);
        xmlResponse = new WFXmlResponse(AgencyList_temp);
        String strAgencyList = xmlResponse.getVal("Value1");

        if ((Curr_Workstep.equalsIgnoreCase("LMApproval") && ResearchType.equalsIgnoreCase("CCP") || (Curr_Workstep.equalsIgnoreCase("CPTeamApproval")) && ResearchType.equalsIgnoreCase("CCP"))) {
            GenLog("Inside MailTrigger for CCPApproval::>>>" + ResearchType, "T");
            xmlCCPResponse = null;
            GenLog("Activity Name-CCP::>>>" + Curr_Workstep, "T");
            xmlCCPResponse = new WFXmlResponse(selectQuery(strCCPQuery, 1));
            strXmlData = xmlCCPResponse.getVal("Value1");
            WorkItemData = strXmlData.split("`");

        } else if ((Curr_Workstep.equalsIgnoreCase("LMApproval") && ResearchType.equalsIgnoreCase("Research Brief") || (Curr_Workstep.equalsIgnoreCase("CPTeamApproval") && ResearchType.equalsIgnoreCase("Research Brief") || Curr_Workstep.equalsIgnoreCase("PortfolioHeadApproval") && ResearchType.equalsIgnoreCase("Research Brief") || Curr_Workstep.equalsIgnoreCase("CPIHeadApproval") && ResearchType.equalsIgnoreCase("Research Brief")))) {
            GenLog("Inside MailTrigger for CCPApproval::>>>" + ResearchType, "T");
            xmlCCPResponse = null;
            GenLog("Activity Name-Research Brief::>>>" + Curr_Workstep, "T");
            xmlCCPResponse = new WFXmlResponse(selectQuery(strResearchQuery, 1));
            strXmlData = xmlCCPResponse.getVal("Value1");
            WorkItemData = strXmlData.split("`");

        }
        GenLog("WorkItem Data length:::" + WorkItemData.length, "T");
        if (WorkItemData.length == 24) {

            strAge = WorkItemData[11];
            strCCPApprover = WorkItemData[16];
            strCCPApproverID = WorkItemData[17];
            strCCPAppEmailID = WorkItemData[18];
            strLMApprovalID = WorkItemData[19];
            strLMApprovalEmailID = WorkItemData[20];
            strApproverList = WorkItemData[21];
            strTotal = WorkItemData[22];
            strCurrentLoop = WorkItemData[23];
            if (Curr_Workstep.equalsIgnoreCase("LMApproval")) {
                ApproverID = strCCPApproverID;
                ApproverEmailID = strCCPAppEmailID;
                ApproverName = strCCPApprover;
                GenLog("LM ApproverID::::" + ApproverID + "::::ApproverEmailID:::" + ApproverEmailID + ":::ApproverName:::" + ApproverName, "T");
            } else if (Curr_Workstep.equalsIgnoreCase("CPTeamApproval")) {
                if (ResearchType.equalsIgnoreCase("CCP")) {
                    ApproverEmailID = strAgencyList;
                    GenLog("CCP-CCPApproverID::::" + ApproverID + "::::ApproverEmailID:::" + ApproverEmailID + ":::ApproverName:::" + ApproverName, "T");
                }

            }
        } else if (WorkItemData.length == 44) {

            strCCPApprover = WorkItemData[30];
            strCCPApproverID = WorkItemData[31];
            strCCPAppEmailID = WorkItemData[32];
            PFApprover = WorkItemData[33];
            PFApproverID = WorkItemData[34];
            PFApproverEmailID = WorkItemData[35];
            CPIApprover = WorkItemData[36];
            CPIApproverID = WorkItemData[37];
            CPIApproverEmailID = WorkItemData[38];
            strApproverList = WorkItemData[39];
            strTotal = WorkItemData[40];
            strCurrentLoop = WorkItemData[41];
            strLMApprovalID = WorkItemData[42];
            strLMApprovalEmailID = WorkItemData[43];
            if (Curr_Workstep.equalsIgnoreCase("LMApproval")) {
                ApproverID = strCCPApproverID;
                ApproverEmailID = strCCPAppEmailID;
                ApproverName = strCCPApprover;
                GenLog("LM ApproverID::::" + ApproverID + "::::ApproverEmailID:::" + ApproverEmailID + ":::ApproverName:::" + ApproverName, "T");
            } else if (Curr_Workstep.equalsIgnoreCase("CPTeamApproval")) {
                if (ResearchType.equalsIgnoreCase("Research Brief")) {
                    ApproverID = PFApproverID;
                    ApproverEmailID = PFApproverEmailID;
                    ApproverName = PFApprover;
                    GenLog("CCP-RB ApproverID::::" + ApproverID + "::::ApproverEmailID:::" + ApproverEmailID + ":::ApproverName:::" + ApproverName, "T");
                } else if (ResearchType.equalsIgnoreCase("CCP")) {
                    ApproverEmailID = strAgencyList;
                    GenLog("CCP-CCPApproverID::::" + ApproverID + "::::ApproverEmailID:::" + ApproverEmailID + ":::ApproverName:::" + ApproverName, "T");
                }

            } else if (Curr_Workstep.equalsIgnoreCase("PortfolioHeadApproval")) {
                ApproverID = CPIApproverID;
                ApproverEmailID = CPIApproverEmailID;
                ApproverName = CPIApprover;
                GenLog("PF ApproverID::::" + ApproverID + "::::ApproverEmailID:::" + ApproverEmailID + ":::ApproverName:::" + ApproverName, "T");
            } else if (Curr_Workstep.equalsIgnoreCase("CPIHeadApproval")) {
                if (ResearchType.equalsIgnoreCase("Research Brief")) {
                    ApproverEmailID = WorkItemData[6] + "," + strLMApprovalEmailID + "," + CPIApproverEmailID;
                    GenLog("CPI ApproverID::::" + ApproverID + "::::ApproverEmailID:::" + ApproverEmailID + ":::ApproverName:::" + ApproverName, "T");
                }
            }

        } else {
            GenLog("Error in Adding data into Array...", "E");
        }

        String styleTag = "<style>.buttons{ font-family: Tahoma;font-size:20px;font-weight: bold} .button {background-color: #4CAF50;border: none;color: white;height: 32px;width: 120px;text-align: center;";
        styleTag = styleTag + "text-decoration: none;display: inline-block;font-size: 16px;margin: 4px 2px;cursor: pointer;font-weight: bold;font-family:Tahoma;}";
        styleTag = styleTag + ".button3 {background-color: #f44336;}#trans {border-collapse: collapse;width: 800px;font-family: Tahoma;font-size:13px;}";
        styleTag = styleTag + "#cols {border-collapse: collapse;width: 800px;font-family: Tahoma;font-size:13px;}#trans td{\n";
        styleTag = styleTag + "height:35px;border: 2px solid black;padding: 8px;color: black;width: 100px;text-align: center;}";
        styleTag = styleTag + "#cols td{height:35px;border: 2px solid black;padding: 8px;color: black;text-align: center;}";
        styleTag = styleTag + "#trans tr{height:55px;}#trans th {background-color:  #E4DAE8;color: white;border: 2px solid black;padding: 8px;}";
        styleTag = styleTag + "#cols th {background-color:  #E4DAE8;color: white;text-align: center;border: 2px solid black;padding: 6px;}</style>";

        //Rescan
        if ((!((flag.equalsIgnoreCase("Reject")) || (flag.equalsIgnoreCase("Closure"))))) {
            GenLog("Inside Reject function" + flag, "T");
            scriptTag = "<script language=\"javascript\" type=\"text/javascript\">";
            scriptTag = scriptTag + "function approve() {";
            if (ResearchType.equalsIgnoreCase("CCP") || ResearchType.equalsIgnoreCase("Research Brief")) {
                if (Curr_Workstep.equalsIgnoreCase("Initiation") || Curr_Workstep.equalsIgnoreCase("Requestor") || Curr_Workstep.equalsIgnoreCase("LMApproval") || ((Curr_Workstep.equalsIgnoreCase("CPTeamApproval") || Curr_Workstep.equalsIgnoreCase("PortfolioHeadApproval") || Curr_Workstep.equalsIgnoreCase("CPIHeadApproval")) && ResearchType.contains("Research Brief"))) {
                    scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + ApproverName + "&pid=" + PId + "&process=CCP&seqid=" + seqid + "&action=approve\";";
                    scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
                    scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
                    scriptTag = scriptTag + "function reject() {";
                    scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + ApproverName + "&pid=" + PId + "&process=CCP&seqid=" + seqid + "&action=reject\";";
                    scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
                    scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
                }
            }

            scriptTag = scriptTag + "</script>";

        }

        String button1CSS = "padding-top: 5px;border: none;color:#4CAF50;text-decoration: none;display: inline-block;font-size: 24px;cursor: pointer;font-weight: bold;font-family:Tahoma;";
        String button2CSS = "padding-top: 5px;border: none;color:#f44336;text-decoration: none;display: inline-block;font-size: 24px;cursor: pointer;font-weight: bold;font-family:Tahoma;";

        String tableContent = "<body><center><table id=\"cols\"><tbody> <tr>";
        tableContent = tableContent + "<td style=\"width: 120px;height:80px;background-color:#E4DAE8 \"><img src=\"" + DiageoLogo + "\"  alt=\"Diageo India\" width=\"115\" height=\"50\"></td>";
        tableContent = tableContent + "<td style=\"font-size: 22px;background-color:#E4DAE8 \">Consumer Contract Program<span style=\"font-size: 18px \"><br>" + (PId) + "</br></span></td></tr></table>";
        if (ResearchType.equalsIgnoreCase("CCP")) {
            GenLog("Brand::>>>>" + strBrand, "T");
            GenLog("Age::>>>" + strAge, "T");
            if (!strAge.equalsIgnoreCase("")) {
                strAge = strAge.replace("years", "years<br>");
                strAge = strAge.substring(0, strAge.lastIndexOf("<br>")).trim();
            }

            tableContent = tableContent + "<table id=\"trans\"><tbody><tr>";
            tableContent = tableContent + "<td><b>Research Type</b> <br>" + ResearchType + "</td><td><b>Objective of Study</b><br>" + WorkItemData[0] + "</td><td><b>Problem Statement</b><br>" + WorkItemData[1] + "</td></tr><tr>";
            tableContent = tableContent + "<td><b>Supporting Arguments [Facts]</b> <br>" + WorkItemData[2] + "</td><td><b>Intended outcome</b><br>" + WorkItemData[3] + "</td><td><b>Implications</b><br>" + WorkItemData[4] + "</td></tr>";
            tableContent = tableContent + "</tr></tbody></table>";

            tableContent = tableContent + "<table id=\"cols\"><tbody>";
            tableContent = tableContent + "<th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Initiator Details</th></tbody></table>";
            tableContent = tableContent + "<table id=\"trans\"><tbody><tr>";
            tableContent = tableContent + "<td><b>Initiator ID</b><br>" + WorkItemData[5] + "</td><td><b>Initiator Email-ID</b><br>" + WorkItemData[6] + "</td><td><b>Initiated Date</b><br>" + WorkItemData[7] + "</td></tr>";
            tableContent = tableContent + "</tbody></table>";
//            tableContent = tableContent + "</tbody></table>";

            tableContent = tableContent + "<table id=\"cols\"><tbody>";
            tableContent = tableContent + "<th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Consumer Details</th></tbody></table>";
            tableContent = tableContent + "<table id=\"cols\"><tbody>";
            tableContent = tableContent + "<tr><td><b>Brand</b></td><td><b>City</b></td><td><b>Gender</b></td><td><b>Age</b></td></tr>";
            tableContent = tableContent + "<tr><td>" + WorkItemData[8] + "</td><td>" + WorkItemData[9] + "</td><td>" + WorkItemData[10] + "</td><td>" + strAge + "</td></tr>";
            tableContent = tableContent + "<tr><td><b>Section</b></td><td><b>Consumer Category</b></td><td><b>Frequency of consumption</b></td><td><b>Recency of consumption</b></td></tr>";
            tableContent = tableContent + "<tr><td>" + WorkItemData[12] + "</td><td>" + WorkItemData[13] + "</td><td>" + WorkItemData[14] + "</td><td>" + WorkItemData[15] + "</td></tr>";

            tableContent = tableContent + "</tbody></table>";
            GenLog("tableContent::>>>" + tableContent, "T");
        } else if (ResearchType.equalsIgnoreCase("Research Brief")) {
            String strFieldWorkCenter = WorkItemData[7];
            String strFieldWorkOthers = "";
            String strTestType = WorkItemData[10];
            String strTestTypeOthers = "";
            String strMethodology = WorkItemData[12];
            String strMethodOthers = "";
            if (strFieldWorkCenter.contains("Others")) {
                strFieldWorkOthers = WorkItemData[28];
                if (!strFieldWorkOthers.equalsIgnoreCase("")) {
                    strFieldWorkCenter = strFieldWorkCenter + ("<br>(") + (strFieldWorkOthers) + (")");
                }

            }
            if (strTestType.contains("Others")) {
                strTestTypeOthers = WorkItemData[11];
                if (!strTestTypeOthers.equalsIgnoreCase("")) {
                    strTestType = strTestType + ("<br>(") + (strTestTypeOthers) + (")");
                }

            }
            if (strMethodology.contains("Others")) {
                strMethodOthers = WorkItemData[13];
                if (!strMethodOthers.equalsIgnoreCase("")) {
                    strMethodology = strMethodology + ("<br>(") + (strMethodOthers) + (")");
                }

            }
            tableContent = tableContent + "<table id=\"cols\"><tbody>";
            tableContent = tableContent + "<th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Initiator Details</th></tbody></table>";
            tableContent = tableContent + "<table id=\"trans\"><tbody><tr>";
            tableContent = tableContent + "<td><b>Initiator ID</b><br>" + WorkItemData[1] + "</td><td><b>Initiator Email-ID</b><br>" + WorkItemData[2] + "</td><td><b>Initiated Date</b><br>" + WorkItemData[3] + "</td></tr>";
            tableContent = tableContent + "</tbody></table>";

            tableContent = tableContent + "<table id=\"cols\"><tbody>";
            tableContent = tableContent + "<th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Research Brief Details</th></tbody></table>";
            tableContent = tableContent + "<table id=\"cols\"><tbody>";
            tableContent = tableContent + "<tr><td><b>CP Contact</b></td><td><b>Fieldwork Centre</b></td><td><b>Audience Type</b></td><td><b>Research Agency</b></td></tr>";
            tableContent = tableContent + "<tr><td>" + WorkItemData[5] + "</td><td>" + strFieldWorkCenter + "</td><td>" + WorkItemData[7] + "</td><td>" + WorkItemData[8] + "</td></tr>";
            tableContent = tableContent + "<tr><td><b>Planned Fieldwork Date</b></td><td><b>Test Type</b></td><td><b>Methodology</b></td><td><b>Brand</b></td></tr>";
            tableContent = tableContent + "<tr><td>" + WorkItemData[9] + "</td><td>" + strTestType + "</td><td>" + strMethodology + "</td><td>" + WorkItemData[29] + "</td></tr>";

            tableContent = tableContent + "<tr><td><b>Activity Name</b></td><td><b>Budget</b></td><td><b>Version</b></td><td><b>Background</b></td></tr>";
            tableContent = tableContent + "<tr><td>" + WorkItemData[27] + "</td><td>" + WorkItemData[14] + "</td><td>" + WorkItemData[15] + "</td><td>" + WorkItemData[16] + "</td></tr>";

            tableContent = tableContent + "<tr><td><b>Objective of Research</b></td><td><b>Action to be taken post research</b></td><td><b>Key areas to be explored in the research</b></td><td><b>Additional Information Areas</b></td></tr>";
            tableContent = tableContent + "<tr><td>" + WorkItemData[17] + "</td><td>" + WorkItemData[18] + "</td><td>" + WorkItemData[19] + "</td><td>" + WorkItemData[20] + "</td></tr>";

            tableContent = tableContent + "<tr><td><b>Target Respondent Profile</b></td><td><b>Markets</b></td><td><b>Action Standard</b></td><td><b>Stimulus Material</b></td></tr>";
            tableContent = tableContent + "<tr><td>" + WorkItemData[21] + "</td><td>" + WorkItemData[22] + "</td><td>" + WorkItemData[23] + "</td><td>" + WorkItemData[24] + "</td></tr>";

            tableContent = tableContent + "<tr><td><b>Timings</b></td><td><b>Other Details</b></td><td><b>Research Type</b></td><td><b>Project Name</b></td></tr>";
            tableContent = tableContent + "<tr><td>" + WorkItemData[25] + "</td><td>" + WorkItemData[26] + "</td><td>" + ResearchType + "</td><td>" + WorkItemData[0] + "</td></tr>";

            tableContent = tableContent + "</tbody></table>";
        }
        if ((!((flag.equalsIgnoreCase("Reject")) || (flag.equalsIgnoreCase("Closure"))))) {
            GenLog("Inside second function" + flag, "T");
            tableContent = tableContent + "<table id=\"cols\"><tbody><th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Approver Details</th></tbody></table>";
            tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";
            GenLog("strApproverList:::" + strApproverList, "T");
            String[] Approverlist = strApproverList.trim().split("~");
            GenLog("SA_ListofApprover::::>>>" + strApproverList, "T");
            if (ResearchType.equalsIgnoreCase("CCP") || ResearchType.equalsIgnoreCase("Research Brief")) {
                GenLog("ResearchType::::>>>" + ResearchType, "T");
                if (Curr_Workstep.equalsIgnoreCase("Initiation") || Curr_Workstep.equalsIgnoreCase("Requestor")) {
                    tableContent = tableContent + "<td><b>LineManager Approver</b><br>" + ApproverName + "<br>";
                    if (!ApproverEmailID.equalsIgnoreCase("")) {
                        tableContent = tableContent + "(" + ApproverEmailID + ")<br>";
                    }
                    tableContent = tableContent + "<img src=\"" + PendLogo + "\" width=\"97\" height=\"75\" style=\"padding-top: 7px;transform: rotate(-7deg);\"><br><span style=\"height:22px;color:black;font-size: 14px;font-weight: bold\">Status:Pending for Approval</span></br></td>";
                }

                GenLog("SA_CurrentLoop:::" + strCurrentLoop, "T");
                GenLog("SA_Total:::" + strTotal, "T");
                String strPath = "";
                if (!(Curr_Workstep.equalsIgnoreCase("Initiation")|| Curr_Workstep.equalsIgnoreCase("Requestor"))) {
                    for (int i = 1; i <= Integer.parseInt(strCurrentLoop); i++) {
                        String Approvaldate_temp = selectQuery("SELECT TOP 1 convert(NVARCHAR,processedDate,100)+'~'+isnull(username,'') FROM CommentsHistory_CCP with (nolock) WHERE ProcessInstID = '" + PId + "' AND UserName='" + Approverlist[i - 1].trim() + "' AND QueueName='" + strActivityNames[i - 1] + "' ORDER BY INSERTIONORDERID desc", 1);
                        xmlResponse = new WFXmlResponse(Approvaldate_temp);
                        GenLog("xmlResponse-----Date and UserDetails:::" + xmlResponse, "X");
                        String Approvaldate = xmlResponse.getVal("Value1");
                        GenLog("Approvaldate:::" + Approvaldate, "T");
                        strPrevApprovers = Approvaldate.split("~");
                        GenLog("strPrevApprovers[0]::::>>>" + strPrevApprovers[0], "T");
                        GenLog("strPrevApprovers[1]::::>>>" + strPrevApprovers[1], "T");
                        if (strActivityNames[i - 1].equalsIgnoreCase("LMApproval")) {
                            strPath = "LineManager Approver";
                        } else if (strActivityNames[i - 1].equalsIgnoreCase("CPTeamApproval")) {
                            strPath = "CPTeam Approver";
                        } else if (strActivityNames[i - 1].equalsIgnoreCase("PortfolioHeadApproval")) {
                            strPath = "Portfolio Head Approver";
                        } else if (strActivityNames[i - 1].equalsIgnoreCase("CPIHeadApproval")) {
                            strPath = "CPI Head Approver";
                        } else {
                            strPath = "Approver";
                        }
                        String ApprovalEmailID_temp = selectQuery("SELECT isnull(B.MailId,'') FROM CommentsHistory_CCP AS A WITH(nolock) LEFT JOIN PDBUser AS B WITH(nolock) ON A.UserName=B.UserName WHERE A.ProcessInstID='" + PId + "' AND A.UserName='" + strPrevApprovers[1] + "' AND A.QueueName='" + strActivityNames[i - 1] + "'", 1);
                        xmlResponse = new WFXmlResponse(ApprovalEmailID_temp);
                        String ApprovalEmailID = xmlResponse.getVal("Value1");
                        GenLog("ApprovalEmailId::>>>" + ApprovalEmailID, "T");
                        tableContent = tableContent + "<td><b>" + strPath + "</b><br>" + Approverlist[i - 1].trim() + "<br>";
                        if (!ApprovalEmailID.equalsIgnoreCase("")) {
                            tableContent = tableContent + "(" + ApprovalEmailID + ")<br>";
                        }
                        tableContent = tableContent + "<img src=\"" + AppLogo + "\" width=\"105\" height=\"85\"><br><span style=\"height:22px;color:black;font-size: 14px;font-weight: bold\">Approved On: " + strPrevApprovers[0] + "</span></br></td>";
                    }
                    if (!(strCurrentLoop.equalsIgnoreCase(strTotal))) {
                        for (int i = Integer.parseInt(strCurrentLoop) + 1; i <= Integer.parseInt(strTotal); i++) {
//                            ApprovalEmailID = SelectQuery("SELECT isnull(B.MailId,'') FROM CommentsHistory_CCP AS A WITH(nolock) LEFT JOIN PDBUser AS B WITH(nolock) ON A.UserName=B.UserName WHERE A.ProcessInstID='" + strprcsinstid + "' AND A.UserName='" + ApproverID + "' AND A.QueueName='" + strActivityName + "'");
//                            GenLog("ApprovalEmailID::::>>>" + ApprovalEmailID, "T");
                            if (strActivityNames[i - 1].equalsIgnoreCase("LMApproval")) {
                                strPath = "LineManager Approver";
                            } else if (strActivityNames[i - 1].equalsIgnoreCase("CPTeamApproval")) {
                                strPath = "CPTeam Approver";
                            } else if (strActivityNames[i - 1].equalsIgnoreCase("PortfolioHeadApproval")) {
                                strPath = "Portfolio Head Approver";
                            } else if (strActivityNames[i - 1].equalsIgnoreCase("CPIHeadApproval")) {
                                strPath = "CPI Head Approver";
                            } else {
                                strPath = "Approver";
                            }
                            tableContent = tableContent + "<td><b>" + strPath + "</b><br>" + Approverlist[i - 1].trim() + "<br>";
                            if (!ApproverEmailID.equalsIgnoreCase("")) {
                                tableContent = tableContent + "(" + ApproverEmailID + ")<br>";
                            }
                            tableContent = tableContent + "<img src=\"" + PendLogo + "\" width=\"97\" height=\"75\" style=\"padding-top: 7px;transform: rotate(-7deg);\"><br><span style=\"height:22px;color:black;font-size: 14px;font-weight: bold\">Status:Pending for Approval</span></br></td>";
                        }
                    }
                }
            }

            tableContent = tableContent + "</tr></tbody></table>";
        }
        tableContent = tableContent + "<table id=\"cols\"><tbody><tr  style=\"height:10px;\"><td style=\"width:150px;background-color: #E4DAE8\"><b>Previous User Comments</b></td><td>" + Comments + "</td>";
        tableContent = tableContent + "</tr></table><br>";
        if ((!((flag.equalsIgnoreCase("Reject")) || (flag.equalsIgnoreCase("Closure"))))) {
            GenLog("Inside third function" + flag, "T");
            tableContent = tableContent + "<table style=\"width:800px;align:center\">";
            tableContent = tableContent + "<tbody><tr class=\"buttons\" style=\"height:50px;\">";
            tableContent = tableContent + "<td style=\"padding-left:140px\">";
            if (ResearchType.equalsIgnoreCase("CCP") || ResearchType.equalsIgnoreCase("Research Brief")) {
                if (Curr_Workstep.equalsIgnoreCase("Initiation") || Curr_Workstep.equalsIgnoreCase("Requestor") || Curr_Workstep.equalsIgnoreCase("LMApproval") || ((Curr_Workstep.equalsIgnoreCase("CPTeamApproval") && ResearchType.equalsIgnoreCase("Research Brief")) || (Curr_Workstep.equalsIgnoreCase("PortfolioHeadApproval") && ResearchType.equalsIgnoreCase("Research Brief")) || (Curr_Workstep.equalsIgnoreCase("CPIHeadApproval") && ResearchType.equalsIgnoreCase("Research Brief")))) {

                    tableContent = tableContent + "<a href=\"" + url + "/mailapproval/mailApproval.jsp?approver=" + strCCPApproverID + "&pid=" + PId + "&process=CCP&seqid=" + strSeqID + "&action=approve\" onClick=\"return approve()\">";
                    tableContent = tableContent + "<img src=\"" + url + "/webdesktop/resources/images/approve_icon.png\" alt=\"APPROVE\" style=\"width:137px;height:40px;border:0\"></a>";
                    tableContent = tableContent + "</td><td>";
                    tableContent = tableContent + "<a href=\"" + url + "/mailapproval/mailApproval.jsp?approver=" + strCCPApproverID + "&pid=" + PId + "&process=CCP&seqid=" + strSeqID + "&action=reject\" onClick=\"return reject()\">";
                    tableContent = tableContent + "<img src=\"" + url + "/webdesktop/resources/images/reject_icon.png\" alt=\"REJECT\" style=\"width:133px;height:40px;border:0\"></a>";
                } else {
                    GenLog("No Buttons Required for Mail Approval:::" + Curr_Workstep, "T");
                }
            }

            tableContent = tableContent + "</td></tr></tbody></table>";
        }
        tableContent = tableContent + "<br><div style=\"font-family: Tahoma; font-size: 12px; color: #696969;padding-bottom: 10px\">";
        tableContent = tableContent + "<em>This is an automated E-Mail. Replies to this E-Mail are not being monitored. PLEASE DO NOT REPLY TO THIS MESSAGE.</em></div></center></body>";

        try {
            GenLog("strSessionId::::" + SessionId, "T");
            GenLog("strIP::::" + IPAddress, "T");
            strInputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            strInputXml += "<WFAddToMailQueue_Input>";
            strInputXml += "<Option>WFAddToMailQueue</Option>";
            strInputXml = (strInputXml + "<EngineName>" + CabinetName + "</EngineName>");
            strInputXml = (strInputXml + "<SessionId>" + SessionId + "</SessionId>");
            strInputXml += "<MailFrom>" + FromEmailID + "</MailFrom>";
            strInputXml = (strInputXml + "<MailTo>" + ApproverEmailID + "</MailTo>");
            strInputXml = (strInputXml + "<MailCC>" + "" + "</MailCC>");
            if ((flag.equalsIgnoreCase("Approve"))) {
                GenLog("Inside Submit function" + flag, "T");
                strInputXml = (strInputXml + "<MailSubject>A " + ResearchType + " Request under workitem " + PId + " has been assigned to you for your review and approval.</MailSubject>");
            } else if (flag.equalsIgnoreCase("Reject")) {
                GenLog("Inside Reject function" + flag, "T");
                strInputXml = (strInputXml + "<MailSubject>A " + ResearchType + " Request under workitem " + PId + " has been rejected.</MailSubject>");
            } else if (Curr_Workstep == "CPIHeadApproval") {
                GenLog("Inside Exit Function" + Curr_Workstep, "T");
                strInputXml = (strInputXml + "<MailSubject>A " + ResearchType + " Request under workitem " + PId + " has been Completed Successfully.</MailSubject>");
            } else {
                GenLog("Inside Agency Mail Trigger Function" + Curr_Workstep, "T");
                strInputXml = (strInputXml + "<MailSubject>A " + ResearchType + " Request under workitem " + PId + " has been Completed Successfully.</MailSubject>");
            }
            strInputXml += "<ContentType>text/html;charset=UTF-8</ContentType>";
            strInputXml += "<AttachmentISIndex></AttachmentISIndex>";
            strInputXml += "<AttachmentNames></AttachmentNames>";
            strInputXml += "<Priority>1</Priority>";
            strInputXml += "<Comments>MailApprovalSystem</Comments>";
            strInputXml += "<MailActionType>TRIGGER</MailActionType>";
            strInputXml += "<ProcessDefId></ProcessDefId>";
            strInputXml += "<ProcessInstanceId>" + PId + "</ProcessInstanceId>";
            strInputXml += "<WorkitemId>1</WorkitemId><ActivityId></ActivityId>";
            strInputXml += "<MailMessage>";
            strInputXml += "<html>" + scriptTag + styleTag + tableContent + "</html>";
            strInputXml += "</MailMessage>";
            strInputXml += "</WFAddToMailQueue_Input>";
            GenLog("Email Input XML::" + strInputXml, "T");
            strOutputXML = WFCallBroker.execute(strInputXml, "127.0.0.1", 3333, 0);
            GenLog("Email Output XML::" + strOutputXML, "T");
            WFXmlResponse outParser = new WFXmlResponse(strOutputXML);
            String mainCode = outParser.getVal("MainCode");
            if (mainCode.equals("0")) {
                GenLog("CCP mailTrigger Query is Executed successfully", "T");
            } else {
                GenLog("CCP mailTrigger Query is not Executed successfully", "T");
            }
        } catch (Exception e) {
            GenLog("Exception in CCP mailTrigger Function..: " + e.getMessage(), "E");
        }

    }

    /*
     * ----------------------------------------------------------------------------------
     * Function Name :mailTrigger_PTPPayments
     * Description   :To trigger mail to user
     * Return Value : NULL
     * -----------------------------------------------------------------------------------
     */
    public static void mailTrigger_PTPPayments(String processinstanceid) {
        String strOutputXML = "";
        String strInputXml = "";
        String tempapprover[] = null;
        WFXmlResponse xmlResponse = null;
        WFXmlList xmllist = null;
        String glcode, costcenter, desc, amount = "";
        StringBuffer GLCode;
        StringBuffer CostCenter;
        StringBuffer ProfitCenter;
        String InvoiceDocItem;
        StringBuffer WHTcode;
        String WHTSectionType = "";
        String CompanyCode = "";
        String TotalAmount;
        String ParsedAmount, paise;
        String ApprovalQuery = "";
        String ApprovalDate = "";
        String count_len = "";
        String PTP_NonPO_LineItem = "";
        String ExcisePaymentsQuery = "";
        String ListChkFlag = "";
        String CustomerpayQuery = "";
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        //For customerpayments //
        String customercode = null, customername = null, profitcenter = null, headertext = "";
        //End of customerpayments//

        GenLog("Inside MailTrigger Execution function", "T");
        GenLog("processinstanceid::>>" + processinstanceid, "T");

        String sQuery = "Select ConstantValue from CONSTANTDEFTABLE with(nolock) where ConstantName='CONST_LEGAL_USLLogo'";
        String DiageoLogo_temp = selectQuery(sQuery, 1);
        xmlResponse = new WFXmlResponse(DiageoLogo_temp);
        String DiageoLogo = xmlResponse.getVal("Value1");

        String sQuery1 = "Select ConstantValue from CONSTANTDEFTABLE with(nolock) where ConstantName='CONST_LGL_ApproveLogo'";
        String AppLogo_temp = selectQuery(sQuery1, 1);
        xmlResponse = new WFXmlResponse(AppLogo_temp);
        String AppLogo = xmlResponse.getVal("Value1");

        String sQuery2 = "Select ConstantValue from CONSTANTDEFTABLE with(nolock) where ConstantName='CONST_LGL_PendingLogo'";
        String PendLogo_temp = selectQuery(sQuery2, 1);
        xmlResponse = new WFXmlResponse(PendLogo_temp);
        String PendLogo = xmlResponse.getVal("Value1");

        String sQuery3 = "Select activityname from wfinstrumenttable with (nolock) where processinstanceid='" + processinstanceid + "'";
        String Activityname_temp = selectQuery(sQuery3, 1);
        xmlResponse = new WFXmlResponse(Activityname_temp);
        String Activityname = xmlResponse.getVal("Value1");

        String transactiondetails = "select isnull(comments,''),isnull(SA_ListofAppEmailID,''),CAST(SA_CurrentLoop AS NVARCHAR),CAST(SA_Total AS NVARCHAR),isnull(SA_CurrentApprover,''),isnull(SA_CurrentApproverEmailID,''),isnull(SA_ListofApprover,'') from ext_PTP with (nolock) where PROCESSINSTID='" + processinstanceid + "'";
        String transactiondetails1 = selectQuery(transactiondetails, 7);

        xmlResponse = new WFXmlResponse(transactiondetails1);
        GenLog("String transactiondetails::>>" + transactiondetails, "T");
        String comments = xmlResponse.getVal("Value1");
        GenLog("comments::>>" + comments, "T");
        String CurrentUserMailID[] = xmlResponse.getVal("Value2").trim().split("~");
        String SA_CurrentLoop = xmlResponse.getVal("Value3");
        GenLog("SA_CurrentLoop::>>" + SA_CurrentLoop, "T");
        String SA_Total = xmlResponse.getVal("Value4");
        GenLog("SA_Total::>>" + SA_Total, "T");
        String SA_CurrentApprover = xmlResponse.getVal("Value5");
        GenLog("SA_CurrentApprover::>>" + SA_CurrentApprover, "T");
        String SA_CurrentApproverEmailID = xmlResponse.getVal("Value6");
        GenLog("SA_CurrentApproverEmailID::>>" + SA_CurrentApproverEmailID, "T");
        String SA_ListofApprover[] = xmlResponse.getVal("Value7").trim().split("~");
        GenLog("SA_ListofApprover::>>" + SA_ListofApprover.length, "T");

        String headercontent = "SELECT  ptype,TypeofPayment,TypeofPaymentCategory,convert(NVARCHAR,InvoiceDate,103),InvoiceAmount,Businessarea,TypeofProcess,companycode FROM EXT_PTP WITH (nolock) WHERE PROCESSINSTID='" + processinstanceid + "'";
        String headercontent1 = selectQuery(headercontent, 8);
        xmlResponse = new WFXmlResponse(headercontent1);
        GenLog("String headercontent1::>>" + headercontent1, "T");
        String ptype = xmlResponse.getVal("Value1");
        GenLog("ptype::>>" + ptype, "T");
        String TypeofPayment = xmlResponse.getVal("Value2").replaceAll(",", "");
        GenLog("TypeofPayment::>>" + TypeofPayment, "T");
        String TypeofPaymentCategory = xmlResponse.getVal("Value3");
        GenLog("TypeofPaymentCategory::>>" + TypeofPaymentCategory, "T");
        if (TypeofPaymentCategory.equalsIgnoreCase("")) {
            TypeofPaymentCategory = TypeofPayment;
        }
        String InvoiceDate = (xmlResponse.getVal("Value4"));
        GenLog("InvoiceDate::>>" + InvoiceDate, "T");
        String InvoiceAmount = xmlResponse.getVal("Value5");
        GenLog("InvoiceAmount::>>" + InvoiceAmount, "T");
        String BusinessArea = xmlResponse.getVal("Value6");
        GenLog("BusinessArea::>>" + BusinessArea, "T");
        String TypeofProcess = xmlResponse.getVal("Value7");
        GenLog("TypeofProcess::>>" + TypeofProcess, "T");
        CompanyCode = xmlResponse.getVal("Value8");
        GenLog("CompanyCode::>>" + CompanyCode, "T");
        if (InvoiceAmount.contains(".")) {
            paise = InvoiceAmount.substring(InvoiceAmount.indexOf(".") + 1);
            InvoiceAmount = InvoiceAmount.substring(0, InvoiceAmount.indexOf("."));
            ParsedAmount = getIndianCurrencyFormat(InvoiceAmount);
            ParsedAmount = ParsedAmount + "." + paise;
        } else {
            ParsedAmount = getIndianCurrencyFormat(InvoiceAmount);
            GenLog("InvoiceAmount:" + ParsedAmount, "T");
        }
        String styleTag = "<style>.buttons{ font-family: Tahoma;font-size:20px;font-weight: bold} .button {background-color: #4CAF50;border: none;color: white;height: 32px;width: 120px;text-align: center;";
        styleTag = styleTag + "text-decoration: none;display: inline-block;font-size: 16px;margin: 4px 2px;cursor: pointer;font-weight: bold;font-family:Tahoma;}";
        styleTag = styleTag + ".button3 {background-color: #f44336;}#trans {border-collapse: collapse;width: 800px;font-family: Tahoma;font-size:13px;}";
        styleTag = styleTag + "#cols {border-collapse: collapse;width: 800px;font-family: Tahoma;font-size:13px;}#trans td{\n";
        styleTag = styleTag + "height:35px;border: 2px solid black;padding: 8px;color: black;width: 100px;text-align: center;}";
        styleTag = styleTag + "#cols td{height:35px;border: 2px solid black;padding: 8px;color: black;text-align: center;}";
        styleTag = styleTag + "#trans tr{height:55px;}#trans th {background-color:  #E4DAE8;color: white;border: 2px solid black;padding: 8px;}";
        styleTag = styleTag + "#cols th {background-color:  #E4DAE8;color: white;text-align: center;border: 2px solid black;padding: 6px;}</style>";
        String seqid_temp = selectQuery("SELECT NEXT VALUE FOR SeqPTP", 1);
        xmlResponse = new WFXmlResponse(seqid_temp);
        String seqid = xmlResponse.getVal("Value1");
        String scriptTag = "<script language=\"javascript\" type=\"text/javascript\">";
        scriptTag = scriptTag + "function approve() {";
        if (ptype.contains("Payments")) {
            if (Activityname.equalsIgnoreCase("Advance_Initiate") || Activityname.equalsIgnoreCase("Excise_Initiate") || Activityname.equalsIgnoreCase("TDS_Initiate") || Activityname.equalsIgnoreCase("Others_Initiate") || Activityname.equalsIgnoreCase("TCS_Initiate") || Activityname.equalsIgnoreCase("CustomerPayment_Initiate") || Activityname.equalsIgnoreCase("ForeignPayment_Initiate")) {
                scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=PTPPayments&seqid=Payments-" + seqid + "&action=approve\";";
                scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
                scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
                scriptTag = scriptTag + "function reject() {";
                scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=PTPPayments&seqid=Payments-" + seqid + "&action=reject\";";
                scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
                scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
            } else if (Activityname.equalsIgnoreCase("Payments_Approver")) {
                scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=PTPPayments&seqid=Payments-" + seqid + "&action=approve\";";
                scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
                scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
                scriptTag = scriptTag + "function reject() {";
                scriptTag = scriptTag + "var url = \"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=PTPPayments&seqid=Payments-" + seqid + "&action=reject\";";
                scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
                scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
            }
        }

        scriptTag = scriptTag + "</script>";

        String button1CSS = "padding-top: 5px;border: none;color:#4CAF50;text-decoration: none;display: inline-block;font-size: 24px;cursor: pointer;font-weight: bold;font-family:Tahoma;";
        String button2CSS = "padding-top: 5px;border: none;color:#f44336;text-decoration: none;display: inline-block;font-size: 24px;cursor: pointer;font-weight: bold;font-family:Tahoma;";

        String tableContent = "<body><table id=\"cols\"><tbody> <tr>";
        tableContent = tableContent + "<td style=\"width: 120px;height:80px;background-color:#E4DAE8 \"><img src=\"" + DiageoLogo + "\"  alt=\"Diageo India\" width=\"115\" height=\"50\"></td>";
        tableContent = tableContent + "<td style=\"font-size: 22px;background-color:#E4DAE8 \">" + ptype + "<span style=\"font-size: 18px \"><br>" + (processinstanceid) + "</br></span></td></tr></table>";

        tableContent = tableContent + "<table id=\"trans\"><tbody><tr>";
        if (!ptype.equalsIgnoreCase("Customer Payments")) {
            tableContent = tableContent + "<td><b>Company Code</b> <br>" + CompanyCode + "</td><td><b>Business Area</b> <br>" + BusinessArea + "</td><td><b>Type of Payment</b><br>" + TypeofPayment + "</td></tr><tr><td><b>Payment Category</b><br>" + TypeofPaymentCategory + "</td>";
            tableContent = tableContent + "<td><b>Value Date</b><br>" + InvoiceDate + "</td><td><b>Payment Amount</b><br>" + ParsedAmount + "</td></tr>";
        } else if (ptype.equalsIgnoreCase("Customer Payments")) {
            tableContent = tableContent + "<td><b>Company Code</b> <br>" + CompanyCode + "</td><td><b>Business Area</b> <br>" + BusinessArea + "</td>";
            tableContent = tableContent + "<td><b>Value Date</b><br>" + InvoiceDate + "</td><td><b>Payment Amount</b><br>" + ParsedAmount + "</td></tr>";
        }
        tableContent = tableContent + "</tr></tbody></table>";
        String Initiationdetails = "SELECT isnull(SCANUSER,''),CAST(SCANEDATE AS NVARCHAR),isnull(SCANUSERMAILID,'') from ext_PTP where PROCESSINSTID='" + processinstanceid + "'";
        String Initiationdetails1 = selectQuery(Initiationdetails, 3);

        xmlResponse = new WFXmlResponse(Initiationdetails1);

        GenLog("String transactiondetails::>>" + Initiationdetails, "T");

        String SCANUSER = xmlResponse.getVal("Value1");
        GenLog("SCANUSER::>>" + SCANUSER, "T");
        String SCANEDATE = xmlResponse.getVal("Value2");
        GenLog("SCANEDATE::>>" + SCANEDATE, "T");
        String SCANUSERMAILID = xmlResponse.getVal("Value3");
        GenLog("SCANUSERMAILID::>>" + SCANUSERMAILID, "T");
        //String strDate = formatter.format(SCANEDATE);
        tableContent = tableContent + "<table id=\"cols\"><tbody>";
        tableContent = tableContent + "<th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Initiated Details</th></tbody></table>";

        tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";
        tableContent = tableContent + "<td><b>Initiated Date</b></td><td><b>Initiator Name</b></td><td><b>Initiator MailId</b></td></tr><tr>";
        tableContent = tableContent + "<td>" + SCANEDATE + "</td><td>" + SCANUSER + "</td><td>" + SCANUSERMAILID + "</td>";
        tableContent = tableContent + "</tr></tbody></table>";

        if (ptype.equalsIgnoreCase("Advance Payments")) {
            if (TypeofProcess.equalsIgnoreCase("NONPO") || TypeofProcess.equalsIgnoreCase("PO")) {
                /////Start of MainDetails for Advance-PO,NON-PO///////////////
                String PTP_AdvanceMainFrameQuery = selectQuery("SELECT Invoicetype,vendorname,vendorcode  FROM CMPLX_PTP_Advance WITH (nolock) WHERE ProcessInstID='" + processinstanceid + "'", 3);
                xmlResponse = new WFXmlResponse(PTP_AdvanceMainFrameQuery);
                GenLog("PTP_AdvanceMainFrameQuery::>>" + PTP_AdvanceMainFrameQuery, "T");
                String Invoicetype = xmlResponse.getVal("Value1");
                GenLog("Invoicetype::>>" + Invoicetype, "T");
                String vendorname = xmlResponse.getVal("Value2");
                GenLog("vendorname::>>" + vendorname, "T");
                String vendorcode = xmlResponse.getVal("Value3");
                GenLog("vendorcode::>>" + vendorcode, "T");
                tableContent = tableContent + "<table id=\"cols\"><tbody>";
                tableContent = tableContent + "<th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Transaction Details</th></tbody></table>";
                tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";
                tableContent = tableContent + "<td><b>Invoice Type</b></td><td><b>Vendor Name</b></td><td><b>Vendor Code</b></td></tr><tr>";
                tableContent = tableContent + "<td>" + Invoicetype + "</td><td>" + vendorname + "</td><td>" + vendorcode + "</td>";
                tableContent = tableContent + "</tr></tbody></table>";
            } else {
                GenLog("No data available::>>", "T");
            }
        }
        /////End of MainDetails for Advance-PO,NON-PO///////////////

        if (ptype.equalsIgnoreCase("Advance Payments")) {
            if (TypeofProcess.equalsIgnoreCase("NONPO")) {
                count_len = selectQuery("SELECT count(1) FROM CMPLX_PTP_Advance_LI with (nolock) WHERE ProcessInstID='" + processinstanceid + "'", 1);
                ListChkFlag = "Y";
            } else if (TypeofProcess.equalsIgnoreCase("PO")) {
                GenLog("No ListView is available for type::-" + TypeofProcess, "T");
                ListChkFlag = "N";
            }
        } else if (ptype.equalsIgnoreCase("Excise Payments")) {

            ListChkFlag = "N";
        } else if (ptype.equalsIgnoreCase("Other Payments")) {
            count_len = selectQuery("SELECT count(1) FROM CMPLX_PTP_Others_LI with (nolock) WHERE ProcessInstID='" + processinstanceid + "'", 1);
            ListChkFlag = "Y";
        } else if (ptype.equalsIgnoreCase("TDS Payments")) {
            count_len = selectQuery("SELECT count(1) FROM CMPLX_PTP_TDS_LI with (nolock) WHERE ProcessInstID='" + processinstanceid + "'", 1);
            ListChkFlag = "Y";
        } else if (ptype.equalsIgnoreCase("TCS Payments")) {
            count_len = selectQuery("SELECT count(1) FROM CMPLX_PTP_TCS_LI with (nolock) WHERE ProcessInstID='" + processinstanceid + "'", 1);
            ListChkFlag = "Y";
        } else if (ptype.equalsIgnoreCase("Foreign Payments")) {
            GenLog("No ListView is available for type::-" + ptype, "T");
            ListChkFlag = "N";
        } else if (ptype.equalsIgnoreCase("Customer Payments")) {
            GenLog("No ListView is available for type::-" + ptype, "T");
            ListChkFlag = "N";
        }
        if (ListChkFlag.equalsIgnoreCase("Y")) {
            xmlResponse = new WFXmlResponse(count_len);
            GenLog("count_len for listview****" + xmlResponse, "T");
            String count_listlen = xmlResponse.getVal("Value1");
            if (Integer.parseInt(count_listlen) > 0) {
                tableContent = tableContent + "<table id=\"cols\"><tbody><th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">LineItem Details</th></tbody></table>";
                tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";

                if (ptype.equalsIgnoreCase("Advance Payments")) {
                    if (TypeofProcess.equalsIgnoreCase("NONPO")) {
                        tableContent = tableContent + "<td><b>S.No</b></td><td><b>General Ledger Code</b></td><td><b>CostCenter</b></td><td><b>Description</b></td><td><b>Total Amount</b></td></tr><tr>";
                        PTP_NonPO_LineItem = selectQuery("SELECT GLCode,CostCentreNumber,LineItemText,ItemAmount FROM CMPLX_PTP_Advance_LI with (nolock) WHERE ProcessInstID='" + processinstanceid + "'", 4);
                        xmlResponse = new WFXmlResponse(PTP_NonPO_LineItem);
                        GenLog("xmlResponse PTP_Nonlineitem****" + xmlResponse, "T");
                        xmllist = xmlResponse.createList("DataList", "Data");
                        int count = 0;
                        for (; xmllist.hasMoreElements(true); xmllist.skip(true)) {
                            glcode = xmllist.getVal("Value1").trim();
                            GenLog("glcode****" + glcode, "T");
                            costcenter = xmllist.getVal("Value2").trim();
                            GenLog("costcenter****" + costcenter, "T");
                            desc = xmllist.getVal("Value3").trim();
                            GenLog("desc****" + desc, "T");
                            amount = xmllist.getVal("Value4").trim();
                            GenLog("amount****" + amount, "T");
                            if (amount.contains(".")) {
                                paise = amount.substring(amount.indexOf(".") + 1);
                                amount = amount.substring(0, amount.indexOf("."));
                                ParsedAmount = getIndianCurrencyFormat(amount);
                                ParsedAmount = ParsedAmount + "." + paise;
                            } else {
                                ParsedAmount = getIndianCurrencyFormat(amount);
                                GenLog("InvoiceAmount:" + ParsedAmount, "T");
                            }
                            String glcode_des = selectQuery("SELECT GLDescription FROM MST_PTP_GLCode with (nolock) WHERE GlCode='" + glcode + "'", 1);
                            String costcenter_des = selectQuery("SELECT Description FROM MST_PTP_CostCentre with (nolock) WHERE CostCentre='" + costcenter + "'", 1);
                            xmlResponse = new WFXmlResponse(glcode_des);
                            GenLog("String glcode_desc::>>" + glcode_des, "T");
                            String glcode_desc = xmlResponse.getVal("Value1");
                            xmlResponse = new WFXmlResponse(costcenter_des);
                            GenLog("String costcenter_desc::>>" + costcenter_des, "T");
                            String costcenter_desc = xmlResponse.getVal("Value1");
                            GLCode = new StringBuffer(glcode_desc);
                            GLCode.append("<br>(" + glcode + ")</br>");
                            CostCenter = new StringBuffer(costcenter_desc);
                            CostCenter.append("<br>(" + costcenter + ")</br>");
                            GenLog("GLCode****" + GLCode, "T");
                            GenLog("CostCenter****" + CostCenter, "T");
                            tableContent = tableContent + "<td>" + (count + 1) + "</td><td>" + GLCode + "</td><td>" + CostCenter + "</td><td>" + desc + "</td><td>" + ParsedAmount + "</td></tr>";
                            count = count + 1;
                        }
                    } else {
                        GenLog("ListView not available only for type" + TypeofProcess, "T");
                    }
                } else if (ptype.equalsIgnoreCase("Excise Payments")) {
                } else if (ptype.equalsIgnoreCase("Foreign Payments")) {
                } else if (ptype.equalsIgnoreCase("Customer Payments")) {
                } else if (ptype.equalsIgnoreCase("Other Payments")) {
                    GenLog("Inside Ptype " + ptype, "T");
                    tableContent = tableContent + "<td><b>S.No</b></td><td><b>Company Code</b></td><td><b>Business Area</b></td><td><b>Payment Category</b></td><td><b>GLAccount</b></td><td><b>CostCenter</b></td><td><b>Description</b></td></tr><tr>";
                    PTP_NonPO_LineItem = selectQuery("SELECT CompanyCode,BusinessArea,TypeofPaymentCategory,GLaccount,CostCenter,LineItemText FROM CMPLX_PTP_Others_LI with (nolock) WHERE ProcessInstID='" + processinstanceid + "'", 6);
                    xmlResponse = new WFXmlResponse(PTP_NonPO_LineItem);
                    GenLog("xmlResponse PTP_OtherPayments****" + xmlResponse, "T");
                    xmllist = xmlResponse.createList("DataList", "Data");
                    int count = 0;
                    for (; xmllist.hasMoreElements(true); xmllist.skip(true)) {
                        CompanyCode = xmllist.getVal("Value1").trim();
                        GenLog("CompanyCode****" + CompanyCode, "T");
                        BusinessArea = xmllist.getVal("Value2").trim();
                        GenLog("BusinessArea****" + BusinessArea, "T");
                        TypeofPaymentCategory = xmllist.getVal("Value3").trim();
                        GenLog("TypeofPaymentCategory****" + TypeofPaymentCategory, "T");
                        glcode = xmllist.getVal("Value4").trim();
                        GenLog("GLaccount****" + glcode, "T");
                        costcenter = xmllist.getVal("Value5").trim();
                        GenLog("costcenter****" + costcenter, "T");
                        desc = xmllist.getVal("Value6").trim();
                        GenLog("costcenter****" + desc, "T");
                        String glcode_des = selectQuery("SELECT GLDescription FROM MST_PTP_GLCode with (nolock) WHERE GlCode='" + glcode + "'", 1);
                        String costcenter_des = selectQuery("SELECT Description FROM MST_PTP_CostCentre with (nolock) WHERE CostCentre='" + costcenter + "'", 1);
                        xmlResponse = new WFXmlResponse(glcode_des);
                        GenLog("String glcode_desc::>>" + glcode_des, "T");
                        String glcode_desc = xmlResponse.getVal("Value1");
                        xmlResponse = new WFXmlResponse(costcenter_des);
                        GenLog("String costcenter_desc::>>" + costcenter_des, "T");
                        String costcenter_desc = xmlResponse.getVal("Value1");
                        GLCode = new StringBuffer(glcode_desc);
                        GLCode.append("<br>(" + glcode + ")</br>");
                        CostCenter = new StringBuffer(costcenter_desc);
                        CostCenter.append("<br>(" + costcenter + ")</br>");
                        GenLog("GLCode****" + GLCode, "T");
                        GenLog("CostCenter****" + CostCenter, "T");
                        tableContent = tableContent + "<td>" + (count + 1) + "</td><td>" + CompanyCode + "</td><td>" + BusinessArea + "</td><td>" + TypeofPaymentCategory + "</td><td>" + GLCode + "</td><td>" + CostCenter + "</td><td>" + desc + "</td></tr>";
                        count = count + 1;
                    }
                } else if (ptype.equalsIgnoreCase("TDS Payments")) {
                    tableContent = tableContent + "<td><b>S.No</b></td><td><b>Business Area</b></td><td><b>WHTSectionType</b></td><td><b>Total Amount</b></td></tr><tr>";
                    PTP_NonPO_LineItem = selectQuery("SELECT BusinessPlace,WHTSectionType,TotalAmount FROM CMPLX_PTP_TDS_LI with (nolock) WHERE ProcessInstID='" + processinstanceid + "'", 3);
                } else if (ptype.equalsIgnoreCase("TCS Payments")) {
                    tableContent = tableContent + "<td><b>S.No</b></td><td><b>Business Area</b></td><td><b>WHTSectionType</b></td><td><b>Total Amount</b></td></tr><tr>";
                    PTP_NonPO_LineItem = selectQuery("SELECT BusinessArea,WHTSectionType,TotalAmount FROM CMPLX_PTP_TCS_LI with (nolock) WHERE ProcessInstID='" + processinstanceid + "'", 3);
                }
                if ((ptype.equalsIgnoreCase("TDS Payments")) || (ptype.equalsIgnoreCase("TCS Payments"))) {
                    GenLog("Inside PTP_****" + ptype, "T");
                    xmlResponse = new WFXmlResponse(PTP_NonPO_LineItem);
                    xmllist = xmlResponse.createList("DataList", "Data");
                    int count = 0;
                    for (; xmllist.hasMoreElements(true); xmllist.skip(true)) {
                        BusinessArea = xmllist.getVal("Value1").trim();
                        GenLog("BusinessArea****" + BusinessArea, "T");
                        WHTSectionType = xmllist.getVal("Value2").trim();
                        GenLog("WHTSectionType****" + WHTSectionType, "T");
                        TotalAmount = xmllist.getVal("Value3").trim();
                        GenLog("TotalAmount****" + TotalAmount, "T");
                        if (TotalAmount.contains(".")) {
                            paise = TotalAmount.substring(TotalAmount.indexOf(".") + 1);
                            TotalAmount = TotalAmount.substring(0, TotalAmount.indexOf("."));
                            ParsedAmount = getIndianCurrencyFormat(TotalAmount);
                            ParsedAmount = ParsedAmount + "." + paise;
                        } else {
                            ParsedAmount = getIndianCurrencyFormat(TotalAmount);
                            GenLog("InvoiceAmount:" + ParsedAmount, "T");
                        }

                        tableContent = tableContent + "<td>" + (count + 1) + "</td><td>" + BusinessArea + "</td><td>" + WHTSectionType + "</td><td>" + ParsedAmount + "</td></tr>";
                        count = count + 1;
                    }
                }

                tableContent = tableContent + "</tbody></table>";
            } else {
                GenLog("No record present in listview****", "T");
            }
        } else if (ListChkFlag.equalsIgnoreCase("N")) {
            tableContent = tableContent + "<table id=\"cols\"><tbody><th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Transaction Details</th></tbody></table>";
            tableContent = tableContent + "<table id=\"cols\"><tbody>";

            if (ptype.equalsIgnoreCase("Customer Payments")) {
                CustomerpayQuery = selectQuery("SELECT customercode,customername,profitcenter,headertext FROM CMPLX_PTP_CustomerPayments WITH (nolock) WHERE ProcessInstID='" + processinstanceid + "'", 4);
                xmlResponse = new WFXmlResponse(CustomerpayQuery);
                GenLog("CustomerpayQuery::>>" + CustomerpayQuery, "T");
                customercode = xmlResponse.getVal("Value1");
                GenLog("customercode::>>" + customercode, "T");
                customername = xmlResponse.getVal("Value2");
                GenLog("customername::>>" + customername, "T");
                profitcenter = xmlResponse.getVal("Value3");
                GenLog("profitcenter::>>" + profitcenter, "T");
                headertext = xmlResponse.getVal("Value4");
                GenLog("headertext::>>" + headertext, "T");
                //ProfitCenter
                String ProfitCenterQuery = selectQuery("SELECT ProfitCenterdesc FROM mst_PTP_profitcenter WITH (nolock) WHERE profitcenter='" + profitcenter + "'", 1);
                xmlResponse = new WFXmlResponse(ProfitCenterQuery);
                String ProfitCenter_desc = xmlResponse.getVal("Value1");
                GenLog("ProfitCenter_desc::>>" + ProfitCenter_desc, "T");
                ProfitCenter = new StringBuffer(ProfitCenter_desc);
                ProfitCenter.append("<br>(" + profitcenter + ")</br>");

                tableContent = tableContent + "<tr><td><b>Customer Code</b></td><td><b>Customer Name</b></td><td><b>ProfitCenter</b></td><td><b>Description</b></td></tr>";
                tableContent = tableContent + "<tr><td>" + customercode + "</td><td>" + customername + "</td><td>" + ProfitCenter + "</td><td>" + headertext + "</td></tr>";
            } else if (ptype.equalsIgnoreCase("Excise Payments")) {
                ExcisePaymentsQuery = selectQuery("SELECT TypeofPayment FROM CMPLX_PTP_Excise WITH (nolock) WHERE ProcessInstID='" + processinstanceid + "'", 1);
                xmlResponse = new WFXmlResponse(ExcisePaymentsQuery);
                GenLog("ExcisePaymentsQuery::>>" + ExcisePaymentsQuery, "T");
                TypeofPayment = xmlResponse.getVal("Value1");
                TypeofPayment = TypeofPayment.replaceAll(",", "");
                GenLog("TypeofPayment::>>" + TypeofPayment, "T");

                tableContent = tableContent + "<tr><td><b>Beneficiary Name</b></td><td><b>GLCode</b></td><td><b>Amount</b></td><td><b>Description</b></td></tr>";
                if (TypeofPayment.equalsIgnoreCase("Excise Duty")) {
                    GenLog("Inside TypeofPayment::>>" + TypeofPayment, "T");
                    String ExcisePay = selectQuery("SELECT Excise_BeneficiaryName, Excise_GLaccount, Excise_PaymentAmount, Excise_LineItemText FROM CMPLX_PTP_Excise WITH (nolock) WHERE ProcessInstID='" + processinstanceid + "'", 4);
                    xmlResponse = new WFXmlResponse(ExcisePay);
                    GenLog("ExcisePay::>>" + ExcisePay, "T");
                    String Excise_BeneficiaryName = xmlResponse.getVal("Value1");
                    String Excise_GLaccount = xmlResponse.getVal("Value2");
                    String Excise_PaymentAmount = xmlResponse.getVal("Value3");
                    String Excise_LineItemText = xmlResponse.getVal("Value4");
                    if (Excise_PaymentAmount.contains(".")) {
                        paise = Excise_PaymentAmount.substring(Excise_PaymentAmount.indexOf(".") + 1);
                        Excise_PaymentAmount = Excise_PaymentAmount.substring(0, Excise_PaymentAmount.indexOf("."));
                        ParsedAmount = getIndianCurrencyFormat(Excise_PaymentAmount);
                        ParsedAmount = ParsedAmount + "." + paise;
                    } else {
                        ParsedAmount = getIndianCurrencyFormat(Excise_PaymentAmount);
                        GenLog("InvoiceAmount:" + ParsedAmount, "T");
                    }
                    GenLog("Excise_BeneficiaryName::>>" + Excise_BeneficiaryName, "T");
                    GenLog("Excise_GLaccount::>>" + Excise_GLaccount, "T");
                    GenLog("Excise_PaymentAmount::>>" + ParsedAmount, "T");
                    GenLog("Excise_LineItemText::>>" + Excise_LineItemText, "T");

                    tableContent = tableContent + "<tr><td>" + Excise_BeneficiaryName + "</td><td>" + Excise_GLaccount + "</td><td>" + ParsedAmount + "</td><td>" + Excise_LineItemText + "</td></tr>";

                } else if (TypeofPayment.equalsIgnoreCase("AED")) {
                    GenLog("Inside TypeofPayment::>>" + TypeofPayment, "T");
                    String ExcisePay = selectQuery("SELECT EPF_BeneficiaryName, EPF_GLaccount, EPF_PaymentAmount, EPF_LineItemText FROM CMPLX_PTP_Excise WITH (nolock) WHERE ProcessInstID='" + processinstanceid + "'", 4);
                    xmlResponse = new WFXmlResponse(ExcisePay);
                    GenLog("ExcisePay::>>" + ExcisePay, "T");
                    String Excise_BeneficiaryName = xmlResponse.getVal("Value1");
                    String Excise_GLaccount = xmlResponse.getVal("Value2");
                    String Excise_PaymentAmount = xmlResponse.getVal("Value3");
                    String Excise_LineItemText = xmlResponse.getVal("Value4");
                    if (Excise_PaymentAmount.contains(".")) {
                        paise = Excise_PaymentAmount.substring(Excise_PaymentAmount.indexOf(".") + 1);
                        Excise_PaymentAmount = Excise_PaymentAmount.substring(0, Excise_PaymentAmount.indexOf("."));
                        ParsedAmount = getIndianCurrencyFormat(Excise_PaymentAmount);
                        ParsedAmount = ParsedAmount + "." + paise;
                    } else {
                        ParsedAmount = getIndianCurrencyFormat(Excise_PaymentAmount);
                        GenLog("InvoiceAmount:" + ParsedAmount, "T");
                    }
                    GenLog("Excise_BeneficiaryName::>>" + Excise_BeneficiaryName, "T");
                    GenLog("Excise_GLaccount::>>" + Excise_GLaccount, "T");
                    GenLog("Excise_PaymentAmount::>>" + ParsedAmount, "T");
                    GenLog("Excise_LineItemText::>>" + Excise_LineItemText, "T");

                    tableContent = tableContent + "<tr><td>" + Excise_BeneficiaryName + "</td><td>" + Excise_GLaccount + "</td><td>" + ParsedAmount + "</td><td>" + Excise_LineItemText + "</td></tr>";

                } else if (TypeofPayment.equalsIgnoreCase("EPF") || TypeofPayment.equalsIgnoreCase("IPF")) {
                    GenLog("Inside TypeofPayment::>>" + TypeofPayment, "T");
                    String ExcisePay = selectQuery("SELECT IPF_BeneficiaryName, IPF_GLaccount, IPF_PaymentAmount, IPF_LineItemText FROM CMPLX_PTP_Excise WITH (nolock) WHERE ProcessInstID='" + processinstanceid + "'", 4);
                    xmlResponse = new WFXmlResponse(ExcisePay);
                    GenLog("ExcisePay::>>" + ExcisePay, "T");
                    String Excise_BeneficiaryName = xmlResponse.getVal("Value1");
                    String Excise_GLaccount = xmlResponse.getVal("Value2");
                    String Excise_PaymentAmount = xmlResponse.getVal("Value3");
                    String Excise_LineItemText = xmlResponse.getVal("Value4");
                    if (Excise_PaymentAmount.contains(".")) {
                        paise = Excise_PaymentAmount.substring(Excise_PaymentAmount.indexOf(".") + 1);
                        Excise_PaymentAmount = Excise_PaymentAmount.substring(0, Excise_PaymentAmount.indexOf("."));
                        ParsedAmount = getIndianCurrencyFormat(Excise_PaymentAmount);
                        ParsedAmount = ParsedAmount + "." + paise;
                    } else {
                        ParsedAmount = getIndianCurrencyFormat(Excise_PaymentAmount);
                        GenLog("InvoiceAmount:" + ParsedAmount, "T");
                    }
                    GenLog("Excise_BeneficiaryName::>>" + Excise_BeneficiaryName, "T");
                    GenLog("Excise_GLaccount::>>" + Excise_GLaccount, "T");
                    GenLog("Excise_PaymentAmount::>>" + ParsedAmount, "T");
                    GenLog("Excise_LineItemText::>>" + Excise_LineItemText, "T");

                    tableContent = tableContent + "<tr><td>" + Excise_BeneficiaryName + "</td><td>" + Excise_GLaccount + "</td><td>" + ParsedAmount + "</td><td>" + Excise_LineItemText + "</td></tr>";

                }

            } else if (ptype.equalsIgnoreCase("Foreign Payments")) {

                String FPQuery = selectQuery("SELECT TypeofPayment,TypeofPaymentCategory,Amount,vendorcode,vendorname FROM CMPLX_PTP_ForeignPay WITH (nolock) WHERE ProcessInstID='" + processinstanceid + "'", 5);
                xmlResponse = new WFXmlResponse(FPQuery);
                GenLog("FPQuery::>>" + FPQuery, "T");
                TypeofPayment = xmlResponse.getVal("Value1");
                GenLog("TypeofPayment::>>" + TypeofPayment, "T");
                TypeofPaymentCategory = xmlResponse.getVal("Value2");
                GenLog("TypeofPaymentCategory::>>" + customername, "T");
                amount = xmlResponse.getVal("Value3");
                GenLog("Amount::>>" + amount, "T");
                String Vendorcode = xmlResponse.getVal("Value4");
                GenLog("Vendorcode::>>" + Vendorcode, "T");
                String vendorname = xmlResponse.getVal("Value5");
                GenLog("vendorname::>>" + vendorname, "T");
                if (amount.contains(".")) {
                    paise = amount.substring(amount.indexOf(".") + 1);
                    amount = amount.substring(0, amount.indexOf("."));
                    ParsedAmount = getIndianCurrencyFormat(amount);
                    ParsedAmount = ParsedAmount + "." + paise;
                } else {
                    ParsedAmount = getIndianCurrencyFormat(amount);
                    GenLog("InvoiceAmount:" + ParsedAmount, "T");
                }
                tableContent = tableContent + "<tr><td><b>Type of Payment</b></td><td><b>Payment Category</b></td><td><b>Invoice Amount</b></td><td><b>Vendor Code</b></td><td><b>Vendor Name</b></td></tr>";
                tableContent = tableContent + "<tr><td>" + TypeofPayment + "</td><td>" + TypeofPaymentCategory + "</td><td>" + ParsedAmount + "</td><td>" + Vendorcode + "</td><td>" + vendorname + "</td></tr>";
            }

            tableContent = tableContent + "</tbody></table>";
        }

        tableContent = tableContent + "<table id=\"cols\"><tbody><th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Approval Flow</th></tbody></table>";
        tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";

        GenLog("Activityname****" + Activityname, "T");
        if (ptype.contains("Payments")) {
            if (Activityname.equalsIgnoreCase("Payments_Approver")) {
                if (!SA_CurrentLoop.equalsIgnoreCase("") && !SA_Total.equalsIgnoreCase("")) {
                    int temp_Currentloop = Integer.parseInt(SA_CurrentLoop) - 1;
                    int temp1_TotalLoop = Integer.parseInt(SA_Total);
                    for (int i1 = 0; i1 < temp_Currentloop; i1++) {
                        ApprovalQuery = selectQuery("SELECT TOP 1 convert(NVARCHAR,processedDate,100) FROM CommentsHistory_PTP with (nolock) WHERE ProcessInstID = '" + processinstanceid + "' AND UserName='" + SA_ListofApprover[i1] + "' AND QueueName=Approver ORDER BY INSERTIONORDERID desc", 1);
                        xmlResponse = new WFXmlResponse(ApprovalQuery);
                        GenLog("ApprovalQuery::>>" + ApprovalQuery, "T");
                        ApprovalDate = xmlResponse.getVal("Value1");
                        tableContent = tableContent + "<td><b>Approver" + (i1 + 1) + "</b><br>" + CurrentUserMailID[i1] + "</br><img src=\"" + AppLogo + "\" width=\"105\" height=\"85\"><br><span style=\"height:22px;color:black;font-size: 14px;font-weight: bold\">Approved On: " + ApprovalDate + "</span></br></td>";

                    }
                    for (int i2 = temp_Currentloop; i2 < temp1_TotalLoop; i2++) {
                        tableContent = tableContent + "<td><b>Approver" + (i2 + 1) + "</b><br>" + SA_CurrentApproverEmailID + "<br><img src=\"" + PendLogo + "\" width=\"97\" height=\"75\" style=\"padding-top: 7px;transform: rotate(-7deg);\"><br><span style=\"height:22px;color:black;font-size: 14px;font-weight: bold\">Status:Pending for Approval</span></br></td>";
                    }
                }
            }
        }
        tableContent = tableContent + "</tr></tbody></table><table id=\"cols\"><tbody><tr  style=\"height:10px;\">";
        tableContent = tableContent + "<td style=\"width:150px;background-color: #E4DAE8\"><b>Previous User Comments</b></td><td>" + comments + "</td>";
        tableContent = tableContent + "</tr></table><br>";
        tableContent = tableContent + "<table style=\"width:800px;align:center\">";
        tableContent = tableContent + "<tbody><tr class=\"buttons\" style=\"height:50px;\">";
        tableContent = tableContent + "<td style=\"padding-left:120px\">";
        if (ptype.contains("Payments")) {
            if (Activityname.equalsIgnoreCase("Advance_Initiate") || Activityname.equalsIgnoreCase("Excise_Initiate") || Activityname.equalsIgnoreCase("TDS_Initiate") || Activityname.equalsIgnoreCase("Others_Initiate") || Activityname.equalsIgnoreCase("TCS_Initiate") || Activityname.equalsIgnoreCase("CustomerPayment_Initiate") || Activityname.equalsIgnoreCase("ForeignPayment_Initiate")) {

                tableContent = tableContent + "<a href=\"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=PTPPayments&seqid=Payments-" + seqid + "&action=approve\" onClick=\"return approve()\">";
                tableContent = tableContent + "<img src=\"" + url + "/webdesktop/resources/images/approve_icon.png\" alt=\"APPROVE\" style=\"width:137px;height:40px;border:0\"></a>";
                tableContent = tableContent + "</td><td>";
                tableContent = tableContent + "<a href=\"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=PTPPayments&seqid=Payments-" + seqid + "&action=reject\" onClick=\"return reject()\">";
                tableContent = tableContent + "<img src=\"" + url + "/webdesktop/resources/images/reject_icon.png\" alt=\"REJECT\" style=\"width:133px;height:40px;border:0\"></a>";
            } else if (Activityname.equalsIgnoreCase("Payments_Approver")) {
                tableContent = tableContent + "<a href=\"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=PTPPayments&seqid=Payments-" + seqid + "&action=approve\" onClick=\"return approve()\">";
                tableContent = tableContent + "<img src=\"" + url + "/webdesktop/resources/images/approve_icon.png\" alt=\"APPROVE\" style=\"width:137px;height:40px;border:0\"></a>";
                tableContent = tableContent + "</td><td>";
                tableContent = tableContent + "<a href=\"" + url + "/mailapproval/mailApproval.jsp?approver=" + SA_CurrentApprover + "&pid=" + processinstanceid + "&process=PTPPayments&seqid=Payments-" + seqid + "&action=reject\" onClick=\"return reject()\">";
                tableContent = tableContent + "<img src=\"" + url + "/webdesktop/resources/images/reject_icon.png\" alt=\"REJECT\" style=\"width:133px;height:40px;border:0\"></a>";

            }
        }
        tableContent = tableContent + "</td></tr></tbody></table><br>";
        tableContent = tableContent + "<div style=\"font-family: Tahoma; font-size: 12px; color: #696969;padding-bottom: 10px\">";
        tableContent = tableContent + "<em>This is an automated E-Mail. Replies to this E-Mail are not being monitored. PLEASE DO NOT REPLY TO THIS MESSAGE.</em></div></body>";

        try {
            GenLog("strSessionId::::" + SessionId, "T");
            GenLog("SA_CurrentApproverEmailID::::" + SA_CurrentApproverEmailID, "T");
            GenLog("strIP::::" + url, "T");
            strInputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            strInputXml += "<WFAddToMailQueue_Input>";
            strInputXml += "<Option>WFAddToMailQueue</Option>";
            strInputXml = (strInputXml + "<EngineName>" + CabinetName + "</EngineName>");
            strInputXml = (strInputXml + "<SessionId>" + SessionId + "</SessionId>");
            strInputXml += "<MailFrom>workflow@diageo.com</MailFrom>";
            strInputXml = (strInputXml + "<MailTo>" + SA_CurrentApproverEmailID/*toAddress*/ + "</MailTo>");
            strInputXml = (strInputXml + "<MailCC>" + "" + "</MailCC>");
            strInputXml = (strInputXml + "<MailSubject>A PTP " + ptype + " Request under workitem " + processinstanceid + " has been assigned to you for your review and approval.</MailSubject>");
            strInputXml += "<ContentType>text/html;charset=UTF-8</ContentType>";
            strInputXml += "<AttachmentISIndex>";
            strInputXml += "</AttachmentISIndex>";
            strInputXml += "<AttachmentNames>";
            strInputXml += "</AttachmentNames>";
            strInputXml += "<Priority>1</Priority>";
            strInputXml += "<Comments>MailApprovalSystem</Comments>";
            strInputXml += "<MailActionType>TRIGGER</MailActionType>";
            strInputXml += "<ProcessDefId></ProcessDefId>";
            strInputXml += "<ProcessInstanceId>" + processinstanceid + "</ProcessInstanceId>";
            strInputXml += "<WorkitemId>1</WorkitemId><ActivityId></ActivityId>";
            strInputXml += "<MailMessage>";
            strInputXml += "<html>" + scriptTag + styleTag + tableContent + "</html>";
            strInputXml += "</MailMessage>";
            strInputXml += "</WFAddToMailQueue_Input>";
            GenLog("Email Input XML::" + strInputXml, "X");
            strOutputXML = WFCallBroker.execute(strInputXml, "127.0.0.1", 3333, 0);
            GenLog("Email Output XML::" + strOutputXML, "X");
            WFXmlResponse outParser = new WFXmlResponse(strOutputXML);
            String mainCode = outParser.getVal("MainCode");
            if (mainCode.equals("0")) {
                GenLog("mailTrigger_PTP Payments Query is Executed successfully", "T");
            } else {
                GenLog("mailTrigger_PTP Payments Query is not Executed successfully", "E");
            }
        } catch (Exception e) {
            GenLog("Exception in mailTrigger PTP_Payments Function..: " + e.getMessage(), "E");
        }
    }
    /*
     * ----------------------------------------------------------------------------------
     * Function Name :getIndianCurrencyFormat
     * Description   :Convert Amount to Indian standard
     * Return Value : String
     * -----------------------------------------------------------------------------------
     */

    public static String getIndianCurrencyFormat(String amount) {
        StringBuilder stringBuilder = new StringBuilder();
        char amountArray[] = amount.toCharArray();
        int a = 0, b = 0;
        for (int i = amountArray.length - 1; i >= 0; i--) {
            if (a < 3) {
                stringBuilder.append(amountArray[i]);
                a++;
            } else if (b < 2) {
                if (b == 0) {
                    stringBuilder.append(",");
                    stringBuilder.append(amountArray[i]);
                    b++;
                } else {
                    stringBuilder.append(amountArray[i]);
                    b = 0;
                }
            }
        }
        return stringBuilder.reverse().toString();
    }

    public static void setCurrentLoopCount(String ProcessInstId) {

        String strCountQuery = "select isnull(SA_CurrentLoop,'')+'`'+isnull(SA_Total,'') from ext_ccp with (nolock) where processinstid='" + ProcessInstId + "'";
        CommonMethods.GenLog("CCP CountQuery-->> " + strCountQuery, "T");
        String strCountOutputXml = CommonMethods.selectQuery(strCountQuery, 1);
        WFXmlResponse xmlResponse = new WFXmlResponse(strCountOutputXml);
        String CountQuery[] = xmlResponse.getVal("Value1").split("`");
        CommonMethods.GenLog("CurrentLoop-->> " + CountQuery[0], "T");
        CommonMethods.GenLog("Total Lpop-->> " + CountQuery[1], "T");
        int strCurLoop = Integer.parseInt(CountQuery[0]);
        strCurLoop = strCurLoop + 1;
        CommonMethods.GenLog("CurrentLoop After Updation-->> " + strCurLoop, "T");
        String strCountUpdateQry = "Update ext_ccp set SA_CurrentLoop='" + strCurLoop + "' where processinstid='" + ProcessInstId + "'";
        CommonMethods.GenLog("Current Loop Update Query-->> " + strCountUpdateQry, "T");
        CommonMethods.ExecuteUpdateQuery(strCountUpdateQry);
    }

}
