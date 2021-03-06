/*-
 * ============LICENSE_START=======================================================
 * ONAP - SO
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * Copyright (C) 2017 Huawei Technologies Co., Ltd. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.openecomp.mso.adapters.tenant;


import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.ws.Holder;

import org.openecomp.mso.adapters.tenant.exceptions.TenantAlreadyExists;
import org.openecomp.mso.adapters.tenant.exceptions.TenantException;
import org.openecomp.mso.adapters.tenantrest.CreateTenantError;
import org.openecomp.mso.adapters.tenantrest.CreateTenantRequest;
import org.openecomp.mso.adapters.tenantrest.CreateTenantResponse;
import org.openecomp.mso.adapters.tenantrest.DeleteTenantError;
import org.openecomp.mso.adapters.tenantrest.DeleteTenantRequest;
import org.openecomp.mso.adapters.tenantrest.DeleteTenantResponse;
import org.openecomp.mso.adapters.tenantrest.QueryTenantError;
import org.openecomp.mso.adapters.tenantrest.QueryTenantResponse;
import org.openecomp.mso.adapters.tenantrest.RollbackTenantError;
import org.openecomp.mso.adapters.tenantrest.RollbackTenantRequest;
import org.openecomp.mso.adapters.tenantrest.RollbackTenantResponse;
import org.openecomp.mso.adapters.tenantrest.TenantRollback;
import org.openecomp.mso.logger.MsoLogger;
import org.openecomp.mso.openstack.beans.MsoTenant;
import org.openecomp.mso.openstack.exceptions.MsoExceptionCategory;

/**
 * This class services calls to the REST interface for Tenants (http://host:port/vnfs/rest/v1/tenants)
 * Both XML and JSON can be produced/consumed.  Set Accept: and Content-Type: headers appropriately.  XML is the default.
 */
@Path("/v1/tenants")
public class TenantAdapterRest {
	private static MsoLogger LOGGER = MsoLogger.getMsoLogger (MsoLogger.Catalog.RA);

	//RAA? No logging in wrappers

	@HEAD
	@GET
	@Path("/healthcheck")
	@Produces(MediaType.TEXT_HTML)
	public Response healthcheck () {
		String CHECK_HTML = "<!DOCTYPE html><html><head><meta charset=\"ISO-8859-1\"><title>Health Check</title></head><body>Application ready</body></html>";
		return Response.status (HttpServletResponse.SC_OK).entity (CHECK_HTML).build ();
	}

	/*
	URL:
	EP: http://host:8080/tenants/rest
	Resource: v1/tenants
	REQ - metadata?
	{
	"cloudSiteId": "DAN",
	"tenantName": "RAA_1",
	"failIfExists": true,
	"msoRequest": {
	"requestId": "ra1",
	"serviceInstanceId": "sa1"
	}}
	RESP-
	{
   "cloudSiteId": "DAN",
   "tenantId": "128e10b9996d43a7874f19bbc4eb6749",
   "tenantCreated": true,
   "tenantRollback":    {
      "tenantId": "128e10b9996d43a7874f19bbc4eb6749",
      "cloudId": "DAN", // RAA? cloudId instead of cloudSiteId
      "tenantCreated": true,
      "msoRequest":       {
         "requestId": "ra1",
         "serviceInstanceId": "sa1"
      }
   	 }
	}
	*/
	@POST
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response createTenant(CreateTenantRequest req) {
		LOGGER.debug("createTenant enter: " + req.toJsonString());

		String newTenantId = null;
		TenantRollback tenantRollback = new TenantRollback ();

		try {
			Holder<String> htenant = new Holder<>();
			Holder<TenantRollback> hrollback = new Holder<>();
			MsoTenantAdapter impl = new MsoTenantAdapterImpl();
		    impl.createTenant(
		    	req.getCloudSiteId(),
		    	req.getTenantName(),
		    	req.getMetadata(),
		    	req.getFailIfExists(),
                req.getBackout(),
                req.getMsoRequest(),
                htenant,
                hrollback);
		    newTenantId = htenant.value;
		    tenantRollback = hrollback.value;
//			TenantAdapterCore TAImpl = new TenantAdapterCore();
//			newTenantId =  TAImpl.createTenant (req.getCloudSiteId(),
//												req.getTenantName(),
//												req.getFailIfExists(),
//												req.getBackout(),
//												req.getMetadata(),
//												req.getMsoRequest(),
//												tenantRollback);
		}
		catch (TenantAlreadyExists tae) {
			LOGGER.debug("Exception :",tae);
			CreateTenantError exc = new CreateTenantError(tae.getMessage(), tae.getFaultInfo().getCategory(), Boolean.TRUE);
			return Response.status(HttpServletResponse.SC_NOT_IMPLEMENTED).entity(exc).build();
		}
		catch (TenantException te) {
			LOGGER.debug("Exception :",te);
			CreateTenantError exc = new CreateTenantError(te.getFaultInfo().getMessage(), te.getFaultInfo().getCategory(), Boolean.TRUE);
			return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity(exc).build();
		}
		catch (Exception e) {
			LOGGER.debug("Exception :",e);
			CreateTenantError exc = new CreateTenantError(e.getMessage(), MsoExceptionCategory.INTERNAL, Boolean.TRUE);
			return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity(exc).build();
		}

		CreateTenantResponse resp = new CreateTenantResponse (req.getCloudSiteId(), newTenantId, tenantRollback.getTenantCreated(), tenantRollback);
		return Response.status(HttpServletResponse.SC_OK).entity(resp).build();
	}

	/*
	URL:
	http://host:8080/tenants/rest
	Resource: v1/tenant/tennatId
	REQ:
	{"cloudSiteId": "DAN",
	"tenantId": "ca84cd3d3df44272845da554656b3ace",
	"msoRequest": {
	"requestId": "ra1",
	"serviceInstanceId": "sa1"
	}
	}
	RESP:
	{"tenantDeleted": true}
	 */
	@DELETE
	@Path("{tenantId}")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response deleteTenant(
		@PathParam("tenantId") String tenantId,
		DeleteTenantRequest req)
	{
		boolean tenantDeleted = false;

		try {
			Holder<Boolean> deleted = new Holder<>();
			MsoTenantAdapter impl = new MsoTenantAdapterImpl();
		    impl.deleteTenant(
		    	req.getCloudSiteId(),
		    	req.getTenantId(),
		    	req.getMsoRequest(),
		    	deleted);
		    tenantDeleted = deleted.value;
		}
		catch (TenantException te) {
			LOGGER.debug("Exception :",te);
			DeleteTenantError exc = new DeleteTenantError(te.getFaultInfo().getMessage(), te.getFaultInfo().getCategory(), Boolean.TRUE);
			return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity(exc).build();
		}
		catch (Exception e) {
			LOGGER.debug("Exception :",e);
			DeleteTenantError exc = new DeleteTenantError(e.getMessage(), MsoExceptionCategory.INTERNAL, Boolean.TRUE);
			return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity(exc).build();
		}
		DeleteTenantResponse resp = new DeleteTenantResponse();
		resp.setTenantDeleted(tenantDeleted);
		return Response.status(HttpServletResponse.SC_OK).entity(resp).build();
	}

	/*
	URL
	EP://http://host:8080/tenants/rest
	Resource: /v1/tenants
	Params:?tenantNameOrId=RAA_1&cloudSiteId=DAN
	RESP
	{
		   "tenantId": "214b428a1f554c02935e66330f6a5409",
		   "tenantName": "RAA_1",
		   "metadata": {}
	}
	*/
	@GET
	@Path("{tenantId}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response queryTenant(
			@PathParam("tenantId") String tenantId,
//			@QueryParam("tenantNameOrId") String tenantNameOrId, //RAA? diff from doc
			@QueryParam("cloudSiteId") String cloudSiteId,
			@QueryParam("msoRequest.requestId") String requestId,
			@QueryParam("msoRequest.serviceInstanceId") String serviceInstanceId)
	{
		MsoTenant tenant = null;
		try {
			Holder<String> htenant = new Holder<>();
			Holder<String> tenantName = new Holder<>();
			Holder<Map<String,String>> metadata = new Holder<>();
			MsoTenantAdapter impl = new MsoTenantAdapterImpl();
		    impl.queryTenant(
		    	cloudSiteId,
		    	tenantId,
		    	null,
		    	htenant,
		    	tenantName,
		    	metadata
		    	);
		    tenant = new MsoTenant(htenant.value, tenantName.value, metadata.value);
//			TenantAdapterCore TAImpl = new TenantAdapterCore();
//			MsoRequest msoReq = new MsoRequest();
//			tenant = TAImpl.queryTenant (cloudSiteId, tenantId, msoReq);
		}
		catch (TenantException te) {
			LOGGER.debug("Exception :",te);
			QueryTenantError exc = new QueryTenantError(te.getFaultInfo().getMessage(), te.getFaultInfo().getCategory());
			return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity(exc).build();
		}
		catch (Exception e) {
			LOGGER.debug("Exception :",e);
			QueryTenantError exc = new QueryTenantError(e.getMessage(), MsoExceptionCategory.INTERNAL);
			return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity(exc).build();
		}
		QueryTenantResponse resp = new QueryTenantResponse(tenant.getTenantId(), tenant.getTenantName(), tenant.getMetadata());
		return Response.status(HttpServletResponse.SC_OK).entity(resp).build();
	}

	/*
	URL
	EP: //http://host:8080/tenants/rest
	Resource: /v1/tenants/rollback
	REQ
	{"cloudSiteId": "DAN",
	"tenantId": "f58abb05041d4ff384d4d22d1ccd2a6c",
	"msoRequest": {
	"requestId": "ra1",
	"serviceInstanceId": "sa1"
	}
	}
	RESP:
	{"tenantDeleted": true}
	 */
	@DELETE
	@Path("")
	@Consumes({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Response rollbackTenant(
		@QueryParam("rollback") String action, // WTF?
		RollbackTenantRequest req)
	{
		try {
			MsoTenantAdapter impl = new MsoTenantAdapterImpl();
		    impl.rollbackTenant(req.getTenantRollback());
		}
		catch (TenantException te) {
			LOGGER.debug("Exception :",te);
			RollbackTenantError exc = new RollbackTenantError(te.getFaultInfo().getMessage(), te.getFaultInfo().getCategory(), Boolean.TRUE);
			return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity(exc).build();
		}
		catch (Exception e) {
			LOGGER.debug("Exception :",e);
			RollbackTenantError exc = new RollbackTenantError(e.getMessage(), MsoExceptionCategory.INTERNAL, Boolean.TRUE);
			return Response.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).entity(exc).build();
		}

		RollbackTenantResponse resp = new RollbackTenantResponse ();
		resp.setTenantRolledback(req != null);
		return Response.status(HttpServletResponse.SC_OK).entity(resp).build();
	}
}
