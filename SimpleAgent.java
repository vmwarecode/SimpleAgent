/*
 * ****************************************************************************
 * Copyright VMware, Inc. 2010-2016.  All Rights Reserved.
 * ****************************************************************************
 *
 * This software is made available for use under the terms of the BSD
 * 3-Clause license:
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the 
 *    distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its 
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package com.vmware.simpleagent;

import com.vmware.common.annotations.Action;
import com.vmware.common.annotations.Option;
import com.vmware.common.annotations.Sample;
import com.vmware.connection.Connection;
import com.vmware.connection.ConnectionFactory;
import com.vmware.security.credstore.CredentialStore;
import com.vmware.security.credstore.CredentialStoreAdmin;
import com.vmware.security.credstore.CredentialStoreFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.IOException;
import java.util.Set;

/**
 * <pre>
 * SimpleAgent
 *
 * The SimpleAgent class uses the local credential store to obtain user account
 * and password information, for automated logon to the target host system.
 * SimpleAgent can be used with {@link CreateUser}, to
 * demonstrate using the {@link CredentialStore} client API.
 * SimpleAgent accesses the local credential store to obtain a single user
 * account to login to the specified server (--hostName is the only common-line
 * argument). If more than one user account exists in the credential store,
 * an error message displays.
 * To create user accounts and store them in the local credential store, use
 * the {@link CredentialStoreAdmin} client utility.
 *
 * <b>Parameters:</b>
 * hostName           [required] : The fully-qualified domain name of the server
 *
 * <b>Command Line:</b>
 * run.bat com.vmware.simpleagent.SimpleAgent --hostName [myServerName]
 * </pre>
 */
@Sample(name = "simple-agent", description = "" +
        "The SimpleAgent class uses the local credential store to obtain user account " +
        "and password information, for automated logon to the target host system. " +
        "SimpleAgent can be used with CreateUser, to " +
        "demonstrate using the CredentialStore client API. " +
        "SimpleAgent accesses the local credential store to obtain a single user " +
        "account to login to the specified server (--hostName is the only common-line " +
        "argument). If more than one user account exists in the credential store, " +
        "an error message displays. " +
        "To create user accounts and store them in the local credential store, use " +
        "the CredentialStoreAdmin client utility. "
)
public class SimpleAgent {

    String hostName = null;

    @Option(name = "hostname", description = " The fully-qualified domain name of the server")
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    @Action
    public void connectAndLogin() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException, IllegalBlockSizeException, BadPaddingException {
        Connection connection = ConnectionFactory.newConnection();
        CredentialStore csObj = CredentialStoreFactory.getCredentialStore();

        String userName = "";
        Set<String> userNames = csObj.getUsernames(hostName);
        if (userNames.size() == 0) {
            System.out.println("No user found in this host");
            return;
        } else if (userNames.size() > 1) {
            System.out.println("Found two users for this host");
            return;
        } else {
            Object[] names = userNames.toArray();
            userName = (String) names[0];
        }

        String url = "https://" + hostName + "/sdk/vimService";
        char[] arr = csObj.getPassword(hostName, userName);
        String password = new String(arr);

        connection.setUrl(url);
        connection.setUsername(userName);
        connection.setPassword(password);
        connection.connect();


        System.out.println("Connected Successfully "
                + connection.getServiceContent().getAbout().getFullName());

        connection.disconnect();
    }


}
