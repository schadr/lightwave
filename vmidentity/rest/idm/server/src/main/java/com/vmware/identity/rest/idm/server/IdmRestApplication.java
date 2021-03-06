/*
 *  Copyright (c) 2012-2015 VMware, Inc.  All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *  use this file except in compliance with the License.  You may obtain a copy
 *  of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, without
 *  warranties or conditions of any kind, EITHER EXPRESS OR IMPLIED.  See the
 *  License for the specific language governing permissions and limitations
 *  under the License.
 */
package com.vmware.identity.rest.idm.server;

import java.util.logging.Logger;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ServerProperties;
import com.vmware.identity.idm.client.CasIdmClient;
import com.vmware.identity.rest.core.server.resources.BaseRestApplication;

/**
 * Base application for IDM REST.
 *
 * @author Balaji Boggaram Ramanarayan
 * @author Travis Hall
 */
public class IdmRestApplication extends BaseRestApplication {

    private static final String ENTITY_LOG_PROPERTY = "vmware.rest.idm.entitylogging";
    private static final String TRACE_LOG_PROPERTY = "vmware.rest.idm.tracing";

    public IdmRestApplication() {

        super();

        // Register resources and providers under com.vmware.identity.rest.idm.server
        packages(true, "com.vmware.identity.rest.idm.server");

        // Register LoggingFilter with optional entity logging
        Logger log = Logger.getLogger(IdmRestApplication.class.getName());
        boolean entityLog = Boolean.parseBoolean(System.getProperty(ENTITY_LOG_PROPERTY, "false"));
        // Register the LoggingFilter so it occurs after the ContextFilter so Correlation ID works correctly
        register(new LoggingFilter(log, entityLog), Integer.MIN_VALUE + 1);

        // Enable tracing support
        // Supply header during request: "X-Jersey-Tracing-Accept:true"
        boolean traceLog = Boolean.parseBoolean(System.getProperty(TRACE_LOG_PROPERTY, "false"));
        if (traceLog) {
            property(ServerProperties.TRACING, "ON_DEMAND");
        }
    }

    /**
     * This is the constructor called directly by unit tests. This allows us to pass in a mocked
     * version of {@link CasIdmClient} to just test the functionality of the API and not the CasIdmClient itself.
     *
     * @param mockCasIDMClient
     */
    public IdmRestApplication(final CasIdmClient casIDMClient) {
        /**
         * Register all necessary test dependencies
         */
        register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(casIDMClient).to(CasIdmClient.class);
            }
        });
        /**
         * Specify where resource classes are located. These classes will constituted by API
         */
        packages(true,"com.vmware.identity.rest.idm.server");
    }
}
