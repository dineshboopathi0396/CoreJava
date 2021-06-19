<%-- 
    Document   : makeCall.jsp
    Created on : 24 May 2018
    Author     : anil.baggio
--%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Properties"%>
<%@page import="com.newgen.wfdesktop.xmlapi.WFXmlResponse"%>
<%@page import="java.net.*"%>
<%@page import="java.io.*"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%! String strCurDateTime;
    String strCurDate;
    String strCurDateHour;%>
<%  FileInputStream fisInput = new FileInputStream(System.getProperty("user.dir") + File.separator + "PropertyFiles" + File.separator + "mailApproval.ini");
    Properties prop = new Properties();
    prop.load(fisInput);

    String sURL = prop.getProperty("URL");
    String sComments = request.getParameter("comments");
    String sPId = request.getParameter("pid");
    String sProcess = request.getParameter("process");
    String sAction = request.getParameter("action");
    String sUserName = request.getParameter("username");
    String mode = request.getParameter("mode");
    String seqid = request.getParameter("seqid");
    GenLog("Input Parameters for XML", "T", sPId);
    GenLog("sURL::>>" + sURL, "T", sPId);
    GenLog("sComments::>>" + sComments, "T", sPId);
    GenLog("sPId::>>" + sPId, "T", sPId);
    GenLog("sProcess::>>" + sProcess, "T", sPId);
    GenLog("sAction::>>" + sAction, "T", sPId);
    GenLog("sUserName>>" + sUserName, "T", sPId);
    GenLog("mode>>" + mode, "T", sPId);
    GenLog("seqid>>" + seqid, "T", sPId);
    GenLog("End of Input Parameters for XML", "T", sPId);

    String sInputXML = "<NG_MailApproval>";
    sInputXML = sInputXML + "<Username>" + sUserName + "</Username>";
    sInputXML = sInputXML + "<Process>" + sProcess + "</Process>";
    sInputXML = sInputXML + "<Processinstid>" + sPId + "</Processinstid>";
    sInputXML = sInputXML + "<Action>" + sAction + "</Action>";
    sInputXML = sInputXML + "<Comments>" + sComments + "</Comments>";
    sInputXML = sInputXML + "<Mode>" + mode + "</Mode>";
    sInputXML = sInputXML + "<Seqid>" + seqid + "</Seqid>";
    sInputXML = sInputXML + "</NG_MailApproval>";

    GenLog("sInputXML after framing XML->>" + sInputXML, "T", sPId);
    String sOutputXml = "";
    String sWebservice = "";

    if (sProcess.toUpperCase().equals("PTPINVOICE")) {
        sWebservice = sURL + "/mailapproval/usl/PTPInvoice/action";
    } else if (sProcess.toUpperCase().equals("VM")) {
        sWebservice = sURL + "/mailapproval/usl/VM/action";
    } else if (sProcess.toUpperCase().equals("RTR")) {
        sWebservice = sURL + "/mailapproval/usl/RTR/action";
    } else if (sProcess.toUpperCase().equals("OTC")) {
        sWebservice = sURL + "/mailapproval/usl/OTC/action";
    } else if (sProcess.toUpperCase().equals("CMM")) {
        sWebservice = sURL + "/mailapproval/usl/CMM/action";
    } else if (sProcess.equals("Legal")) {
        sWebservice = sURL + "/mailapproval/usl/legal/action";
    } else if (sProcess.equals("PTPPayments")) {
        sWebservice = sURL + "/mailapproval/usl/PTPPayments/action";
    } else if (sProcess.equals("CCP")) {
        sWebservice = sURL + "/mailapproval/usl/CCP/action";
    }
    else if (sProcess.equals("ATL")) {
        sWebservice = sURL + "/mailapproval/usl/ATL/action";
    }
    

    GenLog("sWebservice URL::>>>" + sWebservice, "T", sPId);
    try {
        URL url = new URL(sWebservice);
        URLConnection urlConnection = url.openConnection();
        if (urlConnection instanceof HttpURLConnection) {
            ((HttpURLConnection) urlConnection).setRequestMethod("POST");
        } else {
            throw new Exception("this connection is NOT an HttpUrlConnection connection");
        }
        urlConnection.setDoInput(true);
        urlConnection.setDoOutput(true);
        urlConnection.setRequestProperty("Content-Type", "application/xml");

        BufferedWriter os = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
        os.write(sInputXML, 0, sInputXML.length());
        os.close();
        os = null;

        ByteArrayOutputStream baos = null;
        InputStream is = null;

        if ((is = urlConnection.getInputStream()) != null) {
            baos = new ByteArrayOutputStream();
            byte ba[] = new byte[1];

            while ((is.read(ba, 0, 1)) != (-1)) {
                baos.write(ba, 0, 1);
            }
            baos.flush();
            is.close();
            sOutputXml = new String(baos.toByteArray(), "UTF-8");
            GenLog("sOutputXml::>>" + sOutputXml, "T", sPId);
            WFXmlResponse xmlResponse = new WFXmlResponse(sOutputXml);
            String returnType = xmlResponse.getVal("TYPE");
            GenLog("SType of the process::>>" + xmlResponse.getVal("Message"), "T", sPId);
            if (returnType.equals("S")) {
                out.println(xmlResponse.getVal("Message"));
                GenLog("Message of the process::>>" + xmlResponse.getVal("Message"), "T", sPId);
            } else if (returnType.equals("E")) {
                out.println("ResponseBody -->> " + sWebservice + " -->> " + xmlResponse.getVal("MainCode") + "/" + returnType + "/" + xmlResponse.getVal("Message"));
                GenLog("Message of the process::>>" + xmlResponse.getVal("Message"), "T", sPId);
            }
        }

    } catch (Exception e) {
        GenLog("Exception in mailApproval.jsp webservice call->" + e, "E", sPId);
    }


%>



<%!public void GenLog(String strMsg, String logType, String sPId) {

        java.util.Date dateFormat = new java.util.Date();
        SimpleDateFormat sDate = new SimpleDateFormat("dd-MM-yyyy");
        strCurDate = sDate.format(dateFormat);
        String strLogPath = "";
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
            System.out.println("Error in generating log File::" + strCurDateTime + " : " + sPId + "---" + e.getMessage());
            //GenLog("Error in generating log File: " + e.getMessage(), "E");
        }
    }%>