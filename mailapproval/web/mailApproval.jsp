<%-- 
    Document   : mailApproval.jsp
    Created on : 24 May 2018
    Author     : anil.baggio
--%>

<%@page import="java.lang.String"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.newgen.wfdesktop.xmlapi.WFCallBroker"%>
<%@page import="java.util.*"%>
<%@page import="com.newgen.wfdesktop.xmlapi.WFXmlResponse"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.io.*"%>
<%@page import="sun.misc.BASE64Decoder"%>
<%! String strCurDateTime;
    String strCurDate;
    String strCurDateHour;%>
<%

    //System.out.println("Inside MailApproval JSP");
    String sApprovalUser = request.getParameter("approver");
    //System.out.println("Approval User::>>"+sApprovalUser);
    String sPId = request.getParameter("pid");
    //System.out.println("ProcessID::>>"+sPId);
    String sProcess = request.getParameter("process");
    //System.out.println("process>>"+sProcess);
    String sAction = request.getParameter("action");
    //System.out.println("action>>"+sAction);
    String seqid = request.getParameter("seqid");
    //System.out.println("seqid>>"+seqid);
    String sUserName = "";
    GenLog("Inside MailApproval.jsp::>>", "T", sPId);
    GenLog("sApprovalUser ::" + sApprovalUser, "T", sPId);
    GenLog("sPId ::" + sPId, "T", sPId);
    GenLog("sProcess ::" + sProcess, "T", sPId);
    GenLog("sAction ::" + sAction, "T", sPId);
    GenLog("seqid ::" + seqid, "T", sPId);

    if (!sAction.equals("")) {
        if (sAction.contains("approve")) {
            sAction = "approve";
        } else if (sAction.contains("reject")) {
            sAction = "reject";
        }
    }
    //System.out.println("sAction>>"+sAction);
    // NTLM Protocol Authentication from browser
    String auth = request.getHeader("Authorization");
    //System.out.println("auth>>"+auth);
    if (auth == null) {
        response.setStatus(response.SC_UNAUTHORIZED);
        response.setHeader("WWW-Authenticate", "NTLM");
        return;
    }
    if (auth.startsWith("NTLM ")) {
        byte[] msg = new sun.misc.BASE64Decoder().decodeBuffer(auth.substring(5));
        int off = 0, length, offset;
        String s;

        if (msg[8] == 1) {
            off = 18;

            byte z = 0;
            byte[] msg1
                    = {(byte) 'N', (byte) 'T', (byte) 'L', (byte) 'M', (byte) 'S',
                        (byte) 'S', (byte) 'P', z,
                        (byte) 2, z, z, z, z, z, z, z,
                        (byte) 40, z, z, z, (byte) 1, (byte) 130, z, z,
                        z, (byte) 2, (byte) 2, (byte) 2, z, z, z, z, // 
                        z, z, z, z, z, z, z, z};
            // 
            response.setStatus(response.SC_UNAUTHORIZED);
            response.setHeader("WWW-Authenticate", "NTLM "
                    + new sun.misc.BASE64Encoder().encodeBuffer(msg1).trim());
            return;
        } else if (msg[8] == 3) {
            off = 30;
            length = msg[off + 17] * 256 + msg[off + 16];
            offset = msg[off + 19] * 256 + msg[off + 18];
            s = new String(msg, offset, length);
            //out.println(s + " ");
        } else {
            return;
        }

        length = msg[off + 1] * 256 + msg[off];
        offset = msg[off + 3] * 256 + msg[off + 2];
        s = new String(msg, offset, length);
        //out.println(s + " ");
        length = msg[off + 9] * 256 + msg[off + 8];
        offset = msg[off + 11] * 256 + msg[off + 10];
        s = new String(msg, offset, length);
        sUserName = s.replace("\0", "");
        GenLog("user.name :" + sUserName, "T", sPId);
        //System.out.println("user.name>>"+sUserName);
    }

    FileInputStream fisInput = new FileInputStream(System.getProperty("user.dir") + File.separator + "PropertyFiles" + File.separator + "mailApproval.ini");
    GenLog("Ini path :" + fisInput, "T", sPId);
    //System.out.println("Ini path :>>"+fisInput);
    Properties prop = new Properties();
    prop.load(fisInput);

    String Cabinet = prop.getProperty("CabinetName");
    String UserName = prop.getProperty("UserName");
    String Password = prop.getProperty("Password");
    String SessionId = "";
    String SessionId_mobile = "";
    String result = "";
    String testflag = "N";
    StringBuffer strBuffer = new StringBuffer();
    GenLog("----------Fetching Details from INI----------", "T", sPId);
    GenLog("Cabinet::" + Cabinet, "T", sPId);
    GenLog("UserName::" + UserName, "T", sPId);
    GenLog("Password::" + Password, "T", sPId);
    //System.out.println("Cabinet::>>"+Cabinet);
    //System.out.println("UserName::>>"+UserName);
    //System.out.println("Password::>>"+Password);

    try {
        strBuffer.append(" <?xml version=1.0?>\n");
        strBuffer.append("<NGOConnectCabinet_Input>\n");
        strBuffer.append("<Option>NGOConnectCabinet</Option>\n");
        strBuffer.append("<CabinetName>" + Cabinet + "</CabinetName>\n");
        strBuffer.append("<UserName>" + UserName + "</UserName>\n");
        strBuffer.append("<UserPassword>" + Password + "</UserPassword>");
        strBuffer.append("<CurrentDateTime></CurrentDateTime>");
        strBuffer.append("<UserExist>N</UserExist>");
        strBuffer.append("</NGOConnectCabinet_Input>");
        GenLog(strBuffer.toString(), "T", sPId);
        String xmlout = WFCallBroker.execute(strBuffer.toString(), "127.0.0.1", 3333, 0);
        WFXmlResponse xmlResponse = new WFXmlResponse(xmlout);
        if (xmlResponse.getVal("Status").equals("0")) {
            SessionId = xmlResponse.getVal(("UserDBId"));
            GenLog("SessionID::>>" + SessionId, "T", sPId);

        } else {
            GenLog("Exception::>>Cabinet not Connected", "E", sPId);
            GenLog("xmlout" + xmlout, "E", sPId);
        }
    } catch (Exception e) {
        GenLog("Exception" + e.getMessage() + "\n", "E", sPId);
    }

    String CheckHistory = "SELECT  ACTION FROM WS_MAILAPPROVAL_HISTORY WITH(NOLOCK) WHERE USERNAME='" + sUserName/*sApprovalUser*/ + "' AND PROCESSINSTID='" + sPId + "' AND SequenceNo='" + seqid + "'";
    GenLog("CheckHistory for action item::>>" + CheckHistory, "T", sPId);
    if (!SessionId.equals("")) {
        WFXmlResponse xmlResponse = null;
        strBuffer = new StringBuffer();
        strBuffer.append("<?xml version=\"1.0\"?>");
        strBuffer.append("<WFCustomBean_Input>");
        strBuffer.append("<Option>NGGetData</Option>");
        strBuffer.append("<QryOption>NGGetData</QryOption>");
        strBuffer.append("<EngineName>" + Cabinet + "</EngineName>");
        strBuffer.append("<SessionId>" + SessionId + "</SessionId>");
        strBuffer.append("<QueryString>" + CheckHistory + "</QueryString>");
        strBuffer.append("<ColumnNo>1</ColumnNo>");
        strBuffer.append("</WFCustomBean_Input>");
        String Xmlout = WFCallBroker.execute(strBuffer.toString(), "127.0.0.1", 3333, 0);
        xmlResponse = new WFXmlResponse(Xmlout);
        GenLog("Xmlout" + Xmlout, "T", sPId);
        if (xmlResponse.getVal("MainCode").equals("0")) {
            result = xmlResponse.getVal("Value1");
        }
        xmlResponse = null;
        Xmlout = "";
        try {
            strBuffer = new StringBuffer();
            strBuffer.append("<?xml version='1.0'?>");
            strBuffer.append("<NGODisconnectCabinet_Input>");
            strBuffer.append("<Option>NGODisconnectCabinet</Option>");
            strBuffer.append("<CabinetName>" + Cabinet + "</CabinetName>");
            strBuffer.append("<UserDBId>" + SessionId + "</UserDBId>");
            strBuffer.append("</NGODisconnectCabinet_Input>");
            Xmlout = WFCallBroker.execute(strBuffer.toString(), "127.0.0.1", 3333, 0);
            xmlResponse = new WFXmlResponse(Xmlout);
            if (xmlResponse.getVal("Status").equals("0")) {
                GenLog("Cabinet Disconnected Successfully .....", "T", sPId);
            } else {
                GenLog("Cabinet Disconnection Failed .....", "E", sPId);
            }
        } catch (Exception e) {
            GenLog("Exception in Disconnecting cabinet : " + e, "E", sPId);
        }
    }%>
<html>
    <link href="https://fonts.googleapis.com/css?family=Open+Sans+Condensed:300" rel="stylesheet"> 
    <script type="text/javascript">
        function getCookie(cname) {
            var name = cname + "=";
            var decodedCookie = decodeURIComponent(document.cookie);
            var ca = decodedCookie.split(';');
            for (var i = 0; i < ca.length; i++) {
                var c = ca[i];
                while (c.charAt(0) == ' ') {
                    c = c.substring(1);
                }
                if (c.indexOf(name) == 0) {
                    return c.substring(name.length, c.length);
                }
            }
            return "";
        }

        var comments;
        var password;
        var mode;
        function SubmitClick() {

            password = document.getElementById("ng_userpwd");
            if (password) {
                if (password.value == "") {
                    alert('Please Enter Password');
                    password.focus();
                    return false;
                }

            }
            if (document.getElementById("ng_comments").value == "") {
                alert('Please Enter Comments');
                document.getElementById("ng_comments").focus();
                return false;
            }
            comments = document.getElementById("ng_comments").value;
            if (document.getElementById("ng_userpwd")) {
                var v = document.getElementById("ng_userpwd").value;
                mode = "M";
                // <% String pwd = "<script>document.writeln(v)</script>";%>
                // <% String username = "<script>document.writeln(username)</script>";%>

            } else {
                mode = "D";
            }

            var xmlhttp;
            if (window.XMLHttpRequest)
            {// code for IE7+, Firefox, Chrome, Opera, Safari
                xmlhttp = new XMLHttpRequest();
            } else
            {// code for IE6, IE5
                xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
            }
            xmlhttp.onreadystatechange = function ()
            {
                if (xmlhttp.readyState == 4 && xmlhttp.status == 200)
                {
                    var responseText = xmlhttp.responseText;
                    console.log("responseText:" + responseText);
                    if (responseText == "WorkItem Completed Successfully") {
                        window.location.reload();
                    } else {
                        window.location.reload();
                    }
                }
            }
            var url = "makeCall.jsp?username=<%=sUserName%>&pid=<%=sPId%>&process=<%=sProcess%>&action=<%=sAction%>&comments=" + comments + "&seqid=<%=seqid%>&mode=" + mode;
            console.log("URL--" + url);
            xmlhttp.open("POST", url, true);
            xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
            xmlhttp.send();



        }
    </script>
    <style>
        .note{   
            font-family:'Open Sans Condensed', sans-serif;
            color: #0d3887;
            font-size:10px;
        }
        .textbox_PID {
            background-color: white;
            border: 0px solid #dbdbdb;
            border-collapse: collapse;
            color: #707070;
            /*float: left;*/
            margin: 0px;
            padding: 0pt;
            overflow: hidden;
            font-size:12px;
            line-height: 28px;
            font-weight: bold;
            width: 50px;
        }
        .textbox {
            background-color: white;
            border: 1px solid #dbdbdb;
            border-collapse: collapse;
            color: #707070;
            float: left;
            margin: 0px;
            padding: 0pt;
            overflow: hidden;
            font-size:12px;
            line-height: 28px;
            width: 80px;
        }
        #ng_submit {
            background-color: #0d3887;
            border: none;
            color: white;
            height: 36px;
            width: 103px;
            text-align: center;
            text-transform: uppercase;
            display: inline-block;
            font-size: 20px;
            margin: 4px 2px;
            cursor: pointer;
            font-weight: bolder;
            font-family:'Open Sans Condensed', sans-serif;
        } 
        #content {
            border-collapse: collapse;
            width: 400px;
            font-size:15px;
            color:#0d3887;
            font-family: 'Open Sans Condensed', sans-serif;
            font-weight: normal;
            text-align: left;
        }
        .logo{
            width: 120px;
            height:60px
        }
        #content td
        {
            padding-top: 15px;
            text-align: left;
        }

    </style>
    <body>
        <table style="padding-left: 160px;">
            <tbody>
                <tr>
                    <td class="logo"><img src="images/diageo_logo.png"  alt="Diageo India" width="125" height="55"></td>
                </tr>
                <tr>
        </table>
        <%if (result.equals("approve")) {%>     
        <div style="padding-left: 170px;padding-bottom: 20px;padding-top: 60px">   
            <img src="images/tick.png"  alt="approved" width="105" height="105">
        </div>
        <div class="note" style="padding-left: 109px;padding-bottom: 20px;">
            <span style="font-weight: bold;font-size: 13px"> TRANSACTION <%=sPId%> HAS BEEN APPROVED</span>
        </div>
        <%
        } else if (result.equals("reject")) {%>
        <div style="padding-left: 170px;padding-bottom: 20px;padding-top: 60px">   
            <img src="images/tick.png"  alt="approved" width="105" height="105">
        </div>
        <div class="note" style="padding-left: 109px;padding-bottom: 20px;">
            <span style="font-weight: bold;font-size: 13px;"> TRANSACTION <%=sPId%> HAS BEEN REJECTED</span>
        </div>
        <%} else if (result.equals("Processed")) {%>
        <div style="padding-left: 170px;padding-bottom: 20px;padding-top: 60px">   
            <img src="images/unauthorized.png"  alt="unauthorized" width="105" height="105">
        </div>
        <div class="note" style="padding-left: 96px;padding-bottom: 20px;">
            <span style="font-weight: bold;font-size: 13px;"> TRANSACTION NO <%=sPId%> HAS ALREADY PROCESSED THROUGH IBPS WORK FLOW.</span>
        </div>
        <%} else if (result.equals("InvalidUserID")) {%>
        <div style="padding-left: 170px;padding-bottom: 20px;padding-top: 60px">   
            <img src="images/unauthorized.png"  alt="unauthorized" width="105" height="105">
        </div>
        <div class="note" style="padding-left: 96px;padding-bottom: 20px;">
            <span style="font-weight: bold;font-size: 13px"> SORRY! YOU ARE NOT AUTHORIZED TO PERFORM THIS ACTION</span>
        </div>
        <%} else if (result.equals("Error")) {%>
        <div style="padding-left: 170px;padding-bottom: 20px;padding-top: 60px">   
            <img src="images/unauthorized.png"  alt="unauthorized" width="105" height="105">
        </div>
        <div class="note" style="padding-left: 96px;padding-bottom: 20px;">
            <span style="font-weight: bold;font-size: 13px"> SORRY! AN ERROR OCCUR WHILE PERFORMING THIS ACTION. KINDLY CONTACT YOUR ADMINISTRATOR</span>
        </div>
        <%} else {
        %>
        <% if (sApprovalUser.contains(",")) {
                testflag = "";
                String apporvernames[] = sApprovalUser.split(",");
                for (int i1 = 0; i1 < apporvernames.length; i1++) {
                    if (apporvernames[i1].toLowerCase().equals(sUserName.toLowerCase())) {
                        testflag = testflag + "Y";

                    } else {
                        testflag = testflag + "N";
                    }
                }
            }

            GenLog("sApprovalUser::>>" + sApprovalUser, "T", sPId);
            GenLog("testflag::>>" + testflag, "T", sPId);
            GenLog("sApprovalUser.toLowerCase()::>" + sApprovalUser.toLowerCase(), "T", sPId);
            GenLog("sUserName.toUpperCase()::>" + sUserName.toLowerCase(), "T", sPId);%>

        <%if (sUserName.toLowerCase().equals(sApprovalUser.toLowerCase()) || testflag.contains("Y")) {
                GenLog("Inside FirstCondition::>", "T", sPId);%>
        <div style="padding-left: 40px;padding-top: 20px">
            <table id="content">
                <tbody>
                    <%if (!sUserName.equals("")) {%>
                    <tr>
                        <td>
                            <b>User Name</b>
                        </td><td>
                            <input type="text" id="ng_username" class="textbox_PID" style="height: 30px;width: 250px;font-family:'Tahoma';" value=<%=sUserName%> disabled></td>
                    </tr> 
                    <%} else if (sUserName.equals("") || sUserName.equals(null)) {%>
                    <tr>
                        <td>
                            <b>User Name</b>
                        </td><td>
                            <input type="text" id="ng_username" class="textbox_PID" style="height: 30px;width: 250px;font-family:'Tahoma';" value=<%=sApprovalUser%> disabled></td>
                    </tr> 
                    <tr>
                        <td>
                            <b>Password</b>
                        </td><td>
                            <input type="password" id="ng_userpwd" class="textbox_Pwd" style="height: 30px;width: 250px;font-family:'Tahoma';" ></td>
                    </tr>                      
                    <%}%>
                    <tr>
                        <td>
                            <b>Transaction Number</b>
                        </td><td>
                            <input type="text" id="ng_pid" class="textbox_PID" style="height: 30px;width: 250px;font-family:'Tahoma';" value=<%=sPId%> disabled></td>
                    </tr>
                    <% if (sAction.equals("PartialApproval")) {%>
                    <tr>
                        <td>
                            <b>Approval Amount</b>
                        </td><td>
                            <input type="text" id="ng_amount" class="textbox_amount" style="height: 30px;width: 250px;font-family:'Tahoma';"></td>
                    </tr>
                    <%} else {%>
                    <tr>
                        <td>
                            <b>Approval Amount</b>
                        </td><td>
                            <input type="text" id="ng_amount" class="textbox_amount" style="height: 30px;width: 250px;font-family:'Tahoma';display:none;"></td>
                    </tr>
                    <%}%>
                    <tr>     
                        <td style=" text-align: left;">
                            <b>Comments</b></td><td>
                            <textarea id="ng_comments"  class="textbox" style="height: 100px;width: 250px;font-family:'Tahoma'; "></textarea></td>
                    </tr>
                </tbody>
            </table>
        </div>

        <div style="padding-left: 170px;padding-top: 20px;padding-bottom: 10px">
            <button id="ng_submit" onclick="SubmitClick()">Submit</button>
        </div>
        <div class="note" style="padding-left: 33px;padding-bottom: 20px;padding-top: 12px">
            PLEASE NOTE THAT ON SUBMIT YOU AUTHORIZE TO APPROVE THE TRANSACTION IN NEWGEN iBPS WORKFLOW ON YOUR BEHALF
        </div>

        <%} else if (!sUserName.toLowerCase().equals(sApprovalUser.toLowerCase()) && testflag.contains("N")) {
            GenLog("Inside SecondCondition::>", "T", sPId);%>
        <div style="padding-left: 170px;padding-bottom: 20px;padding-top: 60px">   
            <img src="images/unauthorized.png"  alt="unauthorized" width="105" height="105">
        </div>
        <div class="note" style="padding-left: 96px;padding-bottom: 20px;">
            <span style="font-weight: bold;font-size: 13px"> SORRY! YOU ARE NOT AUTHORIZED TO PERFORM THIS ACTION</span>
        </div>
        <%}
            }%>
    </body></html>


<%!public void GenLog(String strMsg, String logType, String sPId) {
        String strCurDateTime;
        String strCurDate;
        String strCurDateHour;
        String strLogPath = "";
        java.util.Date dateFormat = new java.util.Date();
        SimpleDateFormat sDate = new SimpleDateFormat("dd-MM-yyyy");
        strCurDate = sDate.format(dateFormat);

        java.util.Date dateHourFormat = new java.util.Date();
        SimpleDateFormat sDateHour = new SimpleDateFormat("dd-MM-yyyy HH");
        strCurDateHour = sDateHour.format(dateHourFormat);

        java.util.Date dateTimeFormat = new java.util.Date();
        SimpleDateFormat sDateTime = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
        strCurDateTime = sDateTime.format(dateTimeFormat);

        if (sPId.substring(0, sPId.indexOf("-")).equalsIgnoreCase("VM")) {
            strLogPath = System.getProperty("user.dir") + File.separator + "ProcessLogs" + File.separator + "MailApproval" + File.separator + "VM_MailApproval" + File.separator + strCurDate;
        } else if (sPId.substring(0, sPId.indexOf("-")).equalsIgnoreCase("PTP")) {
            strLogPath = System.getProperty("user.dir") + File.separator + "ProcessLogs" + File.separator + "MailApproval" + File.separator + "PTP_MailApproval" + File.separator + strCurDate;
        } else if (sPId.substring(0, sPId.indexOf("-")).equalsIgnoreCase("RTR")) {
            strLogPath = System.getProperty("user.dir") + File.separator + "ProcessLogs" + File.separator + "MailApproval" + File.separator + "RTR_MailApproval" + File.separator + strCurDate;
        } else if (sPId.substring(0, sPId.indexOf("-")).equalsIgnoreCase("Legal")) {
            strLogPath = System.getProperty("user.dir") + File.separator + "ProcessLogs" + File.separator + "MailApproval" + File.separator + "LEGAL_MailApproval" + File.separator + strCurDate;
        } else if (sPId.substring(0, sPId.indexOf("-")).equalsIgnoreCase("OTC")) {
            strLogPath = System.getProperty("user.dir") + File.separator + "ProcessLogs" + File.separator + "MailApproval" + File.separator + "OTC_MailApproval" + File.separator + strCurDate;
        } else if (sPId.substring(0, sPId.indexOf("-")).equalsIgnoreCase("CMM")) {
            strLogPath = System.getProperty("user.dir") + File.separator + "ProcessLogs" + File.separator + "MailApproval" + File.separator + "CMM_MailApproval" + File.separator + strCurDate;
        } else if (sPId.substring(0, sPId.indexOf("-")).equalsIgnoreCase("CCP")) {
            strLogPath = System.getProperty("user.dir") + File.separator + "ProcessLogs" + File.separator + "MailApproval" + File.separator + "CCP_MailApproval" + File.separator + strCurDate;
        } else if (sPId.substring(0, sPId.indexOf("-")).equalsIgnoreCase("ATL_TTL")) {
            strLogPath = System.getProperty("user.dir") + File.separator + "ProcessLogs" + File.separator + "MailApproval" + File.separator + "ATL_MailApproval" + File.separator + strCurDate;
        }
        String strLogFilePath = "";

        if (logType.equalsIgnoreCase("E")) {
            strLogFilePath = strLogPath + File.separator + "Error_" + strCurDateHour + ".log";
        } else if (logType.equalsIgnoreCase("X")) {
            strLogFilePath = strLogPath + File.separator + "XML_" + strCurDateHour + ".log";
        } else if (logType.equalsIgnoreCase("T")) {
            strLogFilePath = strLogPath + File.separator + "Transaction_" + strCurDateHour + ".log";
        } else {
            strLogFilePath = strLogPath + File.separator + "GenLog_" + strCurDateHour + ".log";
        }

        try {
            File dir = new File(strLogPath);
            if (dir.exists() == false) {
                dir.mkdirs();
            }
            File file = new File(strLogFilePath);
            if (file.exists() == false) {
                file.createNewFile();
            }
            RandomAccessFile logFile = new RandomAccessFile(file, "rw");
            logFile.seek(file.length());
            logFile.writeBytes(strCurDateTime + " : " + sPId + "---" + strMsg + "\r\n");
            logFile.close();
        } catch (Exception e) {
            //System.out.println("Error in generating log File::" + strCurDateTime + " : " + sPId + "---" + e.getMessage());
        }
    }%>