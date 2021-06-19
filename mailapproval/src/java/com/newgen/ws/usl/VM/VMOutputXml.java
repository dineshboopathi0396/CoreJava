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
 * File         : VMOutputXml.java
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
package com.newgen.ws.usl.VM;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "NG_MailApproval")
public class VMOutputXml {

    private String MainCode;
    private String Type;
    private String Message;
    private String seqid;
    

    public VMOutputXml() {
    }

    public String getMainCode() {
        return MainCode;
    }

    @XmlElement(name = "MainCode")
    public void setMainCode(String MainCode) {
        this.MainCode = MainCode;
    }

    public String getType() {
        return Type;
    }

    @XmlElement(name = "TYPE")
    public void setType(String Type) {
        this.Type = Type;
    }

    public String getMessage() {
        return Message;
    }

    @XmlElement(name = "Message")
    public void setMessage(String Message) {
        this.Message = Message;
    }
    public String getSeqid() {
        return seqid;
    }

    @XmlElement(name = "Seqid")
    public void setSeqid(String seqid) {
        this.seqid = seqid;
    }    

}
