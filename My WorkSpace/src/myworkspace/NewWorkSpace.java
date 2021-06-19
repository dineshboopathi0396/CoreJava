/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myworkspace;

/**
 *
 * @author Administrator
 */
public class NewWorkSpace {

    public void mailTrigger_Invoice(FormReference formObject, String toAddress, String flags) {
        String strOutputXML = "";
        String strInputXml = "";
        String docname = "";
        String docindex = "";
        String appname = "";
        String FieldValue_array[] = null;
        String scriptTag = "";
        StringBuffer GLCode;
        StringBuffer CostCenter;
        StringBuffer WHTcode;
        String ApprovalQuery = "";
        String Approvaldate = "";
        String WHTTaxcode;
        String seqid = "";
        String baseamount;
        String processinstid = formObject.getNGValue("PROCESSINSTID");
        String tempapprover[] = null;
        GenLog("flags:>>>>>>>>>" + flags, "T");
        String docattach = "select convert(NVARCHAR,ImageIndex)+'~'+Name+'~'+AppName from PDBDocument (nolock) where documentindex in (select documentindex from pdbfolder (nolock) tb1, pdbdocumentcontent(nolock) tb2 where tb1.folderindex=tb2.parentfolderindex AND tb1.name='" + processinstid + "') AND Name='Invoice'";
        List strdocattach = formObject.getDataFromDataSource(docattach);
        GenLog("formObject.getNGValue(\"VendorName\")::>>>>>>>>>" + formObject.getNGValue("VendorName"), "T");
        String sQuery = "Select ConstantValue from CONSTANTDEFTABLE with(nolock) where ConstantName='CONST_LEGAL_USLLogo'";
        String DiageoLogo = SelectQuery(sQuery);
        String sQuery1 = "Select ConstantValue from CONSTANTDEFTABLE with(nolock) where ConstantName='CONST_LGL_ApproveLogo'";
        String AppLogo = SelectQuery(sQuery1);
        String sQuery2 = "Select ConstantValue from CONSTANTDEFTABLE with(nolock) where ConstantName='CONST_LGL_PendingLogo'";
        String PendLogo = SelectQuery(sQuery2);
        String glcode, costcenter, desc, amount, paise, ParsedAmount = "";

        

        String styleTag = "<style>.buttons{ font-family: Tahoma;font-size:20px;font-weight: bold} .button {background-color: #4CAF50;border: none;color: white;height: 32px;width: 120px;text-align: center;";
        styleTag = styleTag + "text-decoration: none;display: inline-block;font-size: 16px;margin: 4px 2px;cursor: pointer;font-weight: bold;font-family:Tahoma;}";
        styleTag = styleTag + ".button3 {background-color: #f44336;}#trans {border-collapse: collapse;width: 800px;font-family: Tahoma;font-size:13px;}";
        styleTag = styleTag + "#cols {border-collapse: collapse;width: 800px;font-family: Tahoma;font-size:13px;}#trans td{\n";
        styleTag = styleTag + "height:35px;border: 2px solid black;padding: 8px;color: black;width: 100px;text-align: center;}";
        styleTag = styleTag + "#cols td{height:35px;border: 2px solid black;padding: 8px;color: black;text-align: center;}";
        styleTag = styleTag + "#trans tr{height:55px;}#trans th {background-color:  #E4DAE8;color: white;border: 2px solid black;padding: 8px;}";
        styleTag = styleTag + "#cols th {background-color:  #E4DAE8;color: white;text-align: center;border: 2px solid black;padding: 6px;}</style>";

        //Rescan
        if (!(flags.equalsIgnoreCase("Reject")) && !(flags.equalsIgnoreCase("Rescan"))) {
            GenLog("Inside first function" + flags, "T");
            scriptTag = "<script language=\"javascript\" type=\"text/javascript\">";
            seqid = SelectQuery("SELECT NEXT VALUE FOR SeqPTP");
            scriptTag = scriptTag + "function approve() {";
            if (formObject.getNGValue("PType").contains("Invoice")) {
//                if (strActivityName.equalsIgnoreCase("IPT_Standard") || strActivityName.equalsIgnoreCase("IPT_Rework_Standard") || (strActivityName.equalsIgnoreCase("POCreditNoteProcessing")) || (strActivityName.equalsIgnoreCase("NonPOCreditNoteProcessing"))) { //C13-005 Starts & Ends here
//                    scriptTag = scriptTag + "var url = \"" + strIP + "/mailapproval/SRmailApproval.jsp?approver=" + formObject.getNGValue("SA_CurrentApprover") + "&pid=" + strprcsinstid + "&process=2Way&seqid=" + seqid + "&action=approve\";";
//                    scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
//                    scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
//                    scriptTag = scriptTag + "function reject() {";
//                    scriptTag = scriptTag + "var url = \"" + strIP + "/mailapproval/SRmailApproval.jsp?approver=" + formObject.getNGValue("SA_CurrentApprover") + "&pid=" + strprcsinstid + "&process=2Way&seqid=" + seqid + "&action=reject\";";
//                    scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
//                    scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
//                } else 
                if (strActivityName.equalsIgnoreCase("ServiceRequestor")) {
                    scriptTag = scriptTag + "var url = \"" + strIP + "/mailapproval/SRmailApproval.jsp?approver=" + formObject.getNGValue("SA_CurrentApprover") + "&pid=" + strprcsinstid + "&process=2Way&seqid=" + seqid + "&action=approve\";";
                    scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
                    scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
                    scriptTag = scriptTag + "function PartialApprove() {";
                    scriptTag = scriptTag + "var url = \"" + strIP + "/mailapproval/SRmailApproval.jsp?approver=" + formObject.getNGValue("SA_CurrentApprover") + "&pid=" + strprcsinstid + "&process=2Way&seqid=" + seqid + "&action=PartialApproval\";";
                    scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
                    scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
                    scriptTag = scriptTag + "function reject() {";
                    scriptTag = scriptTag + "var url = \"" + strIP + "/mailapproval/SRmailApproval.jsp?approver=" + formObject.getNGValue("SA_CurrentApprover") + "&pid=" + strprcsinstid + "&process=2Way&seqid=" + seqid + "&action=reject\";";
                    scriptTag = scriptTag + "var newwindow=window.open(url,'', 'resizable,height=455,width=485');";
                    scriptTag = scriptTag + "if (window.focus) {newwindow.focus()}return false;}";
                }
            }

            scriptTag = scriptTag + "</script>";

        }
        String button1CSS = "padding-top: 5px;border: none;color:#4CAF50;text-decoration: none;display: inline-block;font-size: 24px;cursor: pointer;font-weight: bold;font-family:Tahoma;";
        String button2CSS = "padding-top: 5px;border: none;color:#f44336;text-decoration: none;display: inline-block;font-size: 24px;cursor: pointer;font-weight: bold;font-family:Tahoma;";

        String tableContent = "<body><table id=\"cols\"><tbody> <tr>";
        tableContent = tableContent + "<td style=\"width: 120px;height:80px;background-color:#E4DAE8 \"><img src=\"" + DiageoLogo + "\"  alt=\"Diageo India\" width=\"115\" height=\"50\"></td>";
        tableContent = tableContent + "<td style=\"font-size: 22px;background-color:#E4DAE8 \">INVOICE PROCESSING<span style=\"font-size: 18px \"><br>" + (strprcsinstid) + "</br></span></td></tr></table>";

        tableContent = tableContent + "</td></tr></tbody></table></td></tr><tr><td colspan=\"2\" style=\"width:640px\"><p style=\"font:18px Tahoma;color:#0066ff;margin:15px 20px 0 20px\">";
        tableContent = tableContent + "Hello " + sUserName.toUpperCase() + ",</p><br /></td></tr><tr><td colspan=\"2\" style=\"padding:0 20px;width:640px\">";
        tableContent = tableContent + "<table cellspacing=\"0\" style=\"border-top:3px solid #2d3741;width:640px\" cellpadding=\"0\">";
        tableContent = tableContent + "<tbody><tr style=\"font:14px Calibri;padding:11px 0 14px 18px;width:280px;background-color:#efefef\"><td colspan=\"2\" style=\"font-size:10px;color:#666;padding:0 10px 10px 10px;line-height:16px;width:640px\">";
        tableContent = tableContent + "<p style=\"margin:10px 0 0 0;font:13px/16px Calibri;color:#333\">" + Subject + "</p></td>";
        tableContent = tableContent + "</tr><tr><td colspan=\"2\" style=\"font-size:10px;color:#666;padding:0 10px 20px 10px;line-height:16px;width:640px\"><p style=\"margin:5px 0 0 0;font:11px/16px Calibri;color:#333\"></p></td></tr></tbody>";
        tableContent = tableContent + "</table></td></tr><tr><td colspan=\"2\" style=\"width:640px\">";
        tableContent = tableContent + "<p style=\"font:14px Tahoma;color:#0066ff;border-bottom:1px solid #ccc;margin:0 20px 3px 20px;padding:5px 0 3px 0\"> WorkItem Details </p></td></tr><tr><td colspan=\"2\" style=\"padding:16px 40px;width:640px\" id=\"\">";
        
        tableContent = tableContent + "<table id=\"trans\"><tbody><tr>";
        tableContent = tableContent + "<td><b>Company Code</b> <br>" + formObject.getNGValue("BusinessArea") + "</td><td><b>Business Area</b><br>" + formObject.getNGValue("vendorname") + "</td><td><b>Process Type</b><br>" + formObject.getNGValue("InvoiceType") + "</td></tr><tr>";
        tableContent = tableContent + "<td><b>Document Subcategory</b> <br>" + formObject.getNGValue("BusinessArea") + "</td><td><b>Document Number</b><br>" + formObject.getNGValue("vendorname") + "</td><td><b>Total Amount</b><br>" + formObject.getNGValue("InvoiceType") + "</td></tr><tr>";
        tableContent = tableContent + "<td><b>Base Amount</b> <br>" + formObject.getNGValue("InvoiceNumber") + "</td><td><b>Due Date</b><br>" + formObject.getNGValue("InvoiceDate") + "</td><td><b>Vendor Type</b><br>" + ParsedAmount + "</td></tr>";
        tableContent = tableContent + "</tr></tbody></table><table id=\"cols\"><tbody>";

        tableContent = tableContent + "<th style=\"height:20px;color:black;font-size: 13px;font-style: italic\">Transaction Details</th></tbody></table>";
//        tableContent = tableContent + "<table id=\"cols\"><tbody><tr>";
//        tableContent = tableContent + "<td><b>S.No</b></td><td><b>General Ledger Code</b></td><td><b>CostCenter</b></td><td><b>Description</b></td><td><b>Total Amount</b></td></tr><tr>";
//        
//        tableContent = tableContent + "</tbody></table>";

        tableContent = tableContent + "<table id=\"cols\"><tbody><tr  style=\"height:10px;\"><td style=\"width:150px;background-color: #E4DAE8\"><b>Previous User Comments</b></td><td>" + formObject.getNGValue("COMMENTS") + "</td>";
        tableContent = tableContent + "</tr></table><br>";
        if (!(flags.equalsIgnoreCase("Reject")) && !(flags.equalsIgnoreCase("Rescan"))) {
            GenLog("Inside third function" + flags, "T");
            tableContent = tableContent + "<table style=\"width:800px;align:center\">";
            tableContent = tableContent + "<tbody><tr class=\"buttons\" style=\"height:50px;\">";
            tableContent = tableContent + "<td style=\"padding-left:120px\">";
//            if (formObject.getNGValue("PType").contains("Invoice")) {
                if (strActivityName.equalsIgnoreCase("ServiceRequestor")) { //C13-005 Starts & Ends here

                    tableContent = tableContent + "<a href=\"" + strIP + "/mailapproval/SRmailApproval.jsp?approver=" + formObject.getNGValue("SA_CurrentApprover") + "&pid=" + strprcsinstid + "&process=2Way&seqid=" + seqid + "&action=approve\" onClick=\"return approve()\">";
                    tableContent = tableContent + "<img src=\"" + strIP + "/webdesktop/resources/images/approve_icon.png\" alt=\"APPROVE\" style=\"width:137px;height:40px;border:0\"></a>";
                    tableContent = tableContent + "</td><td>";
                    tableContent = tableContent + "<a href=\"" + strIP + "/mailapproval/SRmailApproval.jsp?approver=" + formObject.getNGValue("SA_CurrentApprover") + "&pid=" + strprcsinstid + "&process=2Way&seqid=" + seqid + "&action=PartialApproval\" onClick=\"return Partialapprove()\">";
                    tableContent = tableContent + "<img src=\"" + strIP + "/webdesktop/resources/images/approve_icon.png\" alt=\"APPROVE\" style=\"width:137px;height:40px;border:0\"></a>";
                    tableContent = tableContent + "</td><td>";
                    tableContent = tableContent + "<a href=\"" + strIP + "/mailapproval/SRmailApproval.jsp?approver=" + formObject.getNGValue("SA_CurrentApprover") + "&pid=" + strprcsinstid + "&process=2Way&seqid=" + seqid + "&action=reject\" onClick=\"return reject()\">";
                    tableContent = tableContent + "<img src=\"" + strIP + "/webdesktop/resources/images/reject_icon.png\" alt=\"REJECT\" style=\"width:133px;height:40px;border:0\"></a>";
                } else {
                    tableContent = tableContent + "<a href=\"" + strIP + "/mailapproval/SRmailApproval.jsp?approver=" + formObject.getNGValue("SA_CurrentApprover") + "&pid=" + strprcsinstid + "&process=2Way&seqid=" + seqid + "&action=approve\" onClick=\"return approve()\">";
                    tableContent = tableContent + "<img src=\"" + strIP + "/webdesktop/resources/images/approve_icon.png\" alt=\"APPROVE\" style=\"width:137px;height:40px;border:0\"></a>";
                    tableContent = tableContent + "</td><td>";
                    tableContent = tableContent + "<a href=\"" + strIP + "/mailapproval/SRmailApproval.jsp?approver=" + formObject.getNGValue("SA_CurrentApprover") + "&pid=" + strprcsinstid + "&process=2Way&seqid=" + seqid + "&action=PartialApproval\" onClick=\"return Partialapprove()\">";
                    tableContent = tableContent + "<img src=\"" + strIP + "/webdesktop/resources/images/approve_icon.png\" alt=\"APPROVE\" style=\"width:137px;height:40px;border:0\"></a>";
                    tableContent = tableContent + "</td><td>";
                    tableContent = tableContent + "<a href=\"" + strIP + "/mailapproval/SRmailApproval.jsp?approver=" + formObject.getNGValue("SA_CurrentApprover") + "&pid=" + strprcsinstid + "&process=2Way&seqid=" + seqid + "&action=reject\" onClick=\"return reject()\">";
                    tableContent = tableContent + "<img src=\"" + strIP + "/webdesktop/resources/images/reject_icon.png\" alt=\"REJECT\" style=\"width:133px;height:40px;border:0\"></a>";
                }
//            }

            tableContent = tableContent + "</td></tr></tbody></table>";
        }
        tableContent = tableContent + "<br><div style=\"font-family: Tahoma; font-size: 12px; color: #696969;padding-bottom: 10px\">";
        tableContent = tableContent + "<em>This is an automated E-Mail. Replies to this E-Mail are not being monitored. PLEASE DO NOT REPLY TO THIS MESSAGE.</em></div></body>";

        try {
            GenLog("strSessionId::::" + strSessionId, "T");
            GenLog("strIP::::" + strIP, "T");
            strInputXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
            strInputXml += "<WFAddToMailQueue_Input>";
            strInputXml += "<Option>WFAddToMailQueue</Option>";
            strInputXml = (strInputXml + "<EngineName>" + formObject.getWFEngineName() + "</EngineName>");
            strInputXml = (strInputXml + "<SessionId>" + strSessionId + "</SessionId>");
            strInputXml += "<MailFrom>workflow@diageo.com</MailFrom>";
            strInputXml = (strInputXml + "<MailTo>" + toAddress + "</MailTo>");
            strInputXml = (strInputXml + "<MailCC>" + "" + "</MailCC>");
            if (!(flags.equalsIgnoreCase("Reject")) && !(flags.equalsIgnoreCase("Rescan"))) {
                GenLog("Inside $$$$ function" + flags, "T");
                strInputXml = (strInputXml + "<MailSubject>" + strprcsinstid + " -- Awaiting Service Confirmation Approval</MailSubject>");
            } else if (flags.equalsIgnoreCase("Reject")) {
                GenLog("Inside #### function" + flags, "T");
                strInputXml = (strInputXml + "<MailSubject>A PTP " + formObject.getNGValue("PType") + " Request under workitem " + strprcsinstid + " has been rejected with Rejection Reason -" + formObject.getNGValue("RejectReason") + ".</MailSubject>");
            } else if (flags.equalsIgnoreCase("Rescan")) {
                strInputXml = (strInputXml + "<MailSubject>A PTP " + formObject.getNGValue("PType") + "  Document with transaction number " + strprcsinstid + " Requires Re-Scanning.</MailSubject>");
            }
            strInputXml += "<ContentType>text/html;charset=UTF-8</ContentType>";
            //RE: Workitem No: &<PROCESSINSTID>& with has been rejected with Rejection Reason - &<RejectReason>&
            if (!(flags.equalsIgnoreCase("Reject")) && !(flags.equalsIgnoreCase("Rescan"))) {
                if (formObject.getNGValue("PType").contains("Invoice")) {
                    if (!strdocattach.isEmpty()) {
                        String values = strdocattach.toString();
                        values = values.replace("[", "");
                        values = values.replace("]", "");
                        GenLog("values>>>>>" + values, "T");
                        FieldValue_array = values.split(",");
                        strInputXml += "<AttachmentISIndex>";
                        for (int i = 0; i < FieldValue_array.length; i++) {
                            String temp[] = FieldValue_array[i].split("~");
                            GenLog("docindex::" + temp[0], "T");
                            GenLog("docname::" + temp[1] + "." + temp[2], "T");
                            docindex = docindex + temp[0].trim() + "#" + "1" + "#;";
                            docname = docname + temp[1].trim() + "." + temp[2].trim() + ";";
                        }
                        strInputXml += docindex;
                        strInputXml += "</AttachmentISIndex>";
                        strInputXml += "<AttachmentNames>";
                        strInputXml += docname;
                        strInputXml += "</AttachmentNames>";
                    }
                } else {
                    strInputXml += "<AttachmentISIndex>";
                    strInputXml += "</AttachmentISIndex>";
                    strInputXml += "<AttachmentNames>";
                    strInputXml += "</AttachmentNames>";
                }
            } else {
                strInputXml += "<AttachmentISIndex>";
                strInputXml += "</AttachmentISIndex>";
                strInputXml += "<AttachmentNames>";
                strInputXml += "</AttachmentNames>";
            }

            strInputXml += "<Priority>1</Priority>";
            strInputXml += "<Comments>MailApprovalSystem</Comments>";
            strInputXml += "<MailActionType>TRIGGER</MailActionType>";
            strInputXml += "<ProcessDefId></ProcessDefId>";
            strInputXml += "<ProcessInstanceId>" + strprcsinstid + "</ProcessInstanceId>";
            strInputXml += "<WorkitemId>1</WorkitemId><ActivityId></ActivityId>";
            strInputXml += "<MailMessage>";
            strInputXml += "<html>" + scriptTag + styleTag + tableContent + "</html>";
            strInputXml += "</MailMessage>";
            strInputXml += "</WFAddToMailQueue_Input>";
            GenLog("Email Input XML::" + strInputXml, "T");
            strOutputXML = WFCallBroker.execute(strInputXml, "127.0.0.1", 3333, "JbossEAP");
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

    public static void main(String[] args) {
        //1-n
        int a[] = {1, 2, 3, 5, 6, 7, 8};
        int val = a[0];
        for (int i = 0; i < a.length; i++) {
            if (val != a[i]) {
                System.out.println(val);
                break;
            }
            val++;
        }
    }
}
