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
 * File         : VMInputXml.java
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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "NG_MailApproval")
public class PTPInvoiceInputXml {

    private String Username;
    private String password;
    private String Processinstid;
    private String Process;
    private String status;
    private String Comments;
    private String mode;
    private String seqid;

    public PTPInvoiceInputXml() {
    }

    public String getUsername() {
        return Username;
    }

    @XmlElement(name = "Username")
    public void setUsername(String Username) {
        this.Username = Username;
    }

    public String getPassword() {
        return password;
    }

    @XmlElement(name = "password")
    public void setPassword(String password) {
        this.password = password;
    }

    public String getProcessinstid() {
        return Processinstid;
    }

    @XmlElement(name = "Processinstid")
    public void setProcessinstid(String Processinstid) {
        this.Processinstid = Processinstid;
    }

    public String getStatus() {
        return status;
    }

    @XmlElement(name = "Action")
    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return Comments;
    }

    @XmlElement(name = "Comments")
    public void setComments(String Comments) {
        this.Comments = Comments;
    }

    public String getProcess() {
        return Process;
    }

    @XmlElement(name = "Process")
    public void setProcess(String Process) {
        this.Process = Process;
    }

    public String getMode() {
        return mode;
    }

    @XmlElement(name = "Mode")
    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getSeqid() {
        return seqid;
    }

    @XmlElement(name = "Seqid")
    public void setSeqid(String seqid) {
        this.seqid = seqid;
    }

}
