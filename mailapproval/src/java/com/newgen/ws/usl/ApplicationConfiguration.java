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
 * Module       : mailapproval
 * File         : ApplicationConfiguration.java
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
package com.newgen.ws.usl;

import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("usl")
public class ApplicationConfiguration extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Function Name : addRestResourceClasses Input Parameters : resources
     * Output parameters : Null Return Values : void Description : Function to
     * add Rest Resource Classes Connection Global variables : Null
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(com.newgen.ws.usl.CCP.CCPApproval.class);
        resources.add(com.newgen.ws.usl.OTCBRZR.OTCBRZRApproval.class);
        resources.add(com.newgen.ws.usl.PTPInvoice.PTPInvoiceApproval.class);
        resources.add(com.newgen.ws.usl.PTPPayments.PTPPaymentsApproval.class);
        resources.add(com.newgen.ws.usl.SRApproval.SRApproval.class);
        resources.add(com.newgen.ws.usl.VM.VMApproval.class);
        resources.add(com.newgen.ws.usl.atl.ATLApproval.class);
        resources.add(com.newgen.ws.usl.legal.LegalApproval.class);
        resources.add(com.newgen.ws.usl.otc.OTCApproval.class);
        resources.add(com.newgen.ws.usl.rtr.RTRApproval.class);
    }

}
