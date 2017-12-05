/*
 * Copyright (c) 2017 Brocade Communications Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.aaa.shiro.filters;

import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.base.Optional;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.opendaylight.aaa.impl.AAAShiroProvider;
import org.opendaylight.aaa.impl.shiro.principal.ODLPrincipalImpl;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.aaa.rev161214.HttpAuthorization;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.aaa.rev161214.http.authorization.Policies;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.aaa.rev161214.http.permission.Permissions;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.net.URL;
import java.util.*;
import org.json.*;
import java.io.*;

/**
 * Provides a dynamic authorization mechanism for restful web services with permission grain
 * scope.  <code>aaa.yang</code> defines the model for this filtering mechanism.
 * This model exposes the ability to manipulate policy information for specific paths
 * based on a tuple of (role, http_permission_list).
 *
 * This mechanism will only work when put behind <code>authcBasic</code>
 */
public class MoonAuthorizationFilter extends AuthorizationFilter {

    private String RestServerURL = "http://localhost:8081/shiroservlet-1.0-SNAPSHOT/ShiroServlet";

    private static final Logger LOG = LoggerFactory.getLogger(MoonAuthorizationFilter.class);

    private static final InstanceIdentifier<HttpAuthorization> AUTHZ_CONTAINER_IID =
            InstanceIdentifier.builder(HttpAuthorization.class).build();

    public static Optional<HttpAuthorization> getHttpAuthzContainer(final DataBroker dataBroker)
            throws ExecutionException, InterruptedException, ReadFailedException {

        try (ReadOnlyTransaction ro = dataBroker.newReadOnlyTransaction()) {
            final CheckedFuture<Optional<HttpAuthorization>, ReadFailedException> result =
                    ro.read(LogicalDatastoreType.CONFIGURATION, AUTHZ_CONTAINER_IID);
            return result.get();
        }
    }

    @Override
    public boolean isAccessAllowed(final ServletRequest request, final ServletResponse response,
                                   final Object mappedValue) {
        final Subject subject = getSubject(request, response);
        final ODLPrincipalImpl principal = (ODLPrincipalImpl) subject.getPrincipal();
        final String username = principal.getUsername();
        final HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        final String requestURI = httpServletRequest.getRequestURI();
        final String method = httpServletRequest.getMethod();
        
        return RestAuthorization(username, requestURI, method);
    }

    private boolean RestAuthorization(final String username, final String requestURI, final String method) {
        final JSONTokener tokener;
        final JSONObject object;
        final String input = "{\"method\":\"authz\",\"username\":\""+ username + "\",\"uri\":\"" + requestURI + "\",\"action\":\"" + method + "\"}";
        final ClientConfig config = new DefaultClientConfig();
        final Client client = Client.create(config);
        final WebResource webResource = client.resource(RestServerURL);
        final ClientResponse response = webResource.type("application/json").post(ClientResponse.class, input);
        final String output = response.getEntity(String.class);
        tokener = new JSONTokener(output);
        object = new JSONObject(tokener);
	String result;
        try {
            result = (String) object.get("response");
        } catch (NullPointerException e){
            return false;
        }
        if (result.equals("true"))return true;
        else return false;
    }
}
