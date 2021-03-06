/*-
 * ============LICENSE_START=======================================================
 * ONAP - SO
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
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

package org.openecomp.mso.bpmn.infrastructure;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.Assert.assertEquals;
import static org.openecomp.mso.bpmn.common.DeleteAAIVfModuleTest.MockAAIDeleteVfModule;
import static org.openecomp.mso.bpmn.common.DeleteAAIVfModuleTest.MockAAIGenericVnfSearch;
import static org.openecomp.mso.bpmn.mock.StubResponseAAI.MockDeleteGenericVnf;
import static org.openecomp.mso.bpmn.mock.StubResponseAAI.MockGetGenericVnfById;
import static org.openecomp.mso.bpmn.mock.StubResponseDatabase.mockUpdateRequestDB;
import static org.openecomp.mso.bpmn.mock.StubResponseSDNCAdapter.mockSDNCAdapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.camunda.bpm.engine.test.Deployment;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.openecomp.mso.bpmn.common.BPMNUtil;
import org.openecomp.mso.bpmn.common.WorkflowTest;
import org.openecomp.mso.bpmn.mock.FileUtil;

public class DoDeleteVnfAndModulesTest extends WorkflowTest {
	private final CallbackSet callbacks = new CallbackSet();
	private static final String EOL = "\n";
	private final String vnfAdapterDeleteCallback = 
			"<deleteVfModuleResponse>" + EOL +
			"    <vnfId>a27ce5a9-29c4-4c22-a017-6615ac73c721</vnfId>" + EOL +
			"    <vfModuleId>973ed047-d251-4fb9-bf1a-65b8949e0a73</vfModuleId>" + EOL +
			"    <vfModuleDeleted>true</vfModuleDeleted>" + EOL +
			"    <messageId>{{MESSAGE-ID}}</messageId>" + EOL +
			"</deleteVfModuleResponse>" + EOL;

	public DoDeleteVnfAndModulesTest () throws IOException {
		callbacks.put("deactivate", FileUtil.readResourceFile(
				"__files/VfModularity/SDNCTopologyActivateCallback.xml"));
		callbacks.put("unassign", FileUtil.readResourceFile(
				"__files/VfModularity/SDNCTopologyActivateCallback.xml"));
		callbacks.put("vnfDelete", vnfAdapterDeleteCallback);

	}

	@Test
	@Deployment(resources = {"subprocess/DoDeleteVnfAndModules.bpmn", "subprocess/SDNCAdapterV1.bpmn", "subprocess/GenericGetVnf.bpmn", "subprocess/GenericDeleteVnf.bpmn", "subprocess/DoDeleteVnf.bpmn", "subprocess/DoDeleteVfModule.bpmn"})
	public void testDoDeleteVnfAndModules_successVnfOnly() throws Exception{
		MockGetGenericVnfById("testVnfId123.*", "GenericFlows/getGenericVnfByNameResponse.xml");
		MockDeleteGenericVnf("testVnfId123", "testReVer123");
		mockUpdateRequestDB(200, "Database/DBUpdateResponse.xml");
		mockSDNCAdapter(200);

		String businessKey = UUID.randomUUID().toString();
		Map<String, Object> variables = new HashMap<String, Object>();
		setVariablesVnfOnly(variables);
		invokeSubProcess("DoDeleteVnfAndModules", businessKey, variables);
		
		injectSDNCCallbacks(callbacks, "deactivate");
		injectSDNCCallbacks(callbacks, "unassign");

		waitForProcessEnd(businessKey, 10000);

		Assert.assertTrue(isProcessEnded(businessKey));		

		String workflowException = BPMNUtil.getVariable(processEngineRule, "DoDeleteVnfAndModules", "WorkflowException");
		
		assertEquals(null, workflowException);
	}

	
	private void setVariablesVnfOnly(Map<String, Object> variables) {
		variables.put("mso-request-id", "testRequestId123");		
		variables.put("isDebugLogEnabled", "true");
		variables.put("vnfId","testVnfId123");
		variables.put("serviceInstanceId", "MIS%2F1604%2F0026%2FSW_INTERNET");
		//variables.put("vnfName", "testVnfName123");
		variables.put("disableRollback", "true");
		variables.put("msoRequestId", "testVnfId123");
		variables.put("testVnfId","testVnfId123");
		//variables.put("vnfType", "STMTN");
		variables.put("productFamilyId", "a9a77d5a-123e-4ca2-9eb9-0b015d2ee0fb");
		String vnfModelInfo = "{ "+ "\"modelType\": \"vnf\"," +
				"\"modelInvariantUuid\": \"ff5256d2-5a33-55df-13ab-12abad84e7ff\"," +
				"\"modelUuid\": \"fe6478e5-ea33-3346-ac12-ab121484a3fe\"," +
				"\"modelName\": \"vSAMP12\"," +
				"\"modelVersion\": \"1.0\"," +
				"\"modelCustomizationUuid\": \"MODEL-ID-1234\"," +
				"}";
		//variables.put("vnfModelInfo", vnfModelInfo);

		variables.put("lcpCloudRegionId", "mdt1");
		variables.put("tenantId", "88a6ca3ee0394ade9403f075db23167e");		
		
		String serviceModelInfo = "{ "+ "\"modelType\": \"service\"," +
				"\"modelInvariantUuid\": \"995256d2-5a33-55df-13ab-12abad84e7ff\"," +
				"\"modelUuid\": \"ab6478e5-ea33-3346-ac12-ab121484a3fe\"," +
				"\"modelName\": \"ServicevSAMP12\"," +
				"\"modelVersion\": \"1.0\"," +
				"}";
		//variables.put("serviceModelInfo", serviceModelInfo);
		variables.put("globalSubscriberId", "MSO-1610");
		variables.put("sdncVersion", "1707");
		
	}
	
	@Test	
	@Deployment(resources = {"subprocess/DoDeleteVnfAndModules.bpmn", "subprocess/SDNCAdapterV1.bpmn", "subprocess/GenericGetVnf.bpmn", "subprocess/GenericDeleteVnf.bpmn", "subprocess/DoDeleteVnf.bpmn", "subprocess/DoDeleteVfModuleFromVnf.bpmn", "subprocess/VnfAdapterRestV1.bpmn", "subprocess/DeleteAAIVfModule.bpmn"})
	public void testDoDeleteVnfAndModules_successVnfAndModules() throws Exception{
		MockAAIGenericVnfSearch();
		MockGetGenericVnfById("testVnfId123.*", "GenericFlows/getGenericVnfByNameResponse.xml");
		MockDeleteGenericVnf("testVnfId123", "testReVer123");
		mockUpdateRequestDB(200, "Database/DBUpdateResponse.xml");
		mockSDNCAdapter(200);
		MockDoDeleteVfModule_SDNCSuccess();
		MockDoDeleteVfModule_DeleteVNFSuccess();
		MockAAIDeleteVfModule();

		String businessKey = UUID.randomUUID().toString();
		Map<String, Object> variables = new HashMap<String, Object>();
		setVariablesVnfAndModules(variables);
		invokeSubProcess("DoDeleteVnfAndModules", businessKey, variables);
		
		injectSDNCCallbacks(callbacks, "deactivate");
		injectSDNCCallbacks(callbacks, "deactivate");
		injectVNFRestCallbacks(callbacks, "vnfDelete");
		injectSDNCCallbacks(callbacks, "unassign");
		MockGetGenericVnfById("a27ce5a9-29c4-4c22-a017-6615ac73c721", "GenericFlows/getGenericVnfByNameResponse.xml");
		injectSDNCCallbacks(callbacks, "unassign");
		//MockGetGenericVnfById("a27ce5a9-29c4-4c22-a017-6615ac73c721", "GenericFlows/getGenericVnfByNameResponse.xml");

		waitForProcessEnd(businessKey, 10000);

		Assert.assertTrue(isProcessEnded(businessKey));		

		String workflowException = BPMNUtil.getVariable(processEngineRule, "DoDeleteVnfAndModules", "WorkflowException");
		
		assertEquals(null, workflowException);
	}

	
	private void setVariablesVnfAndModules(Map<String, Object> variables) {
		variables.put("mso-request-id", "a27ce5a9-29c4-4c22-a017-6615ac73c721");		
		variables.put("isDebugLogEnabled", "true");
		variables.put("vnfId","a27ce5a9-29c4-4c22-a017-6615ac73c721");
		variables.put("serviceInstanceId", "a27ce5a9-29c4-4c22-a017-6615ac73c721");
				
		variables.put("msoRequestId", "a27ce5a9-29c4-4c22-a017-6615ac73c721");
		//variables.put("testVnfId","testVnfId123");
		
		variables.put("lcpCloudRegionId", "RDM2WAGPLCP");
		variables.put("tenantId", "fba1bd1e195a404cacb9ce17a9b2b421");
		
		variables.put("sdncVersion", "1707");
		
	}
	

	public static void MockDoDeleteVfModule_SDNCSuccess() {
		stubFor(post(urlEqualTo("/SDNCAdapter"))
				  .withRequestBody(containing("SvcAction>deactivate"))
				  .willReturn(aResponse()
				  .withStatus(200)
				  .withHeader("Content-Type", "text/xml")
				  .withBodyFile("DeleteGenericVNFV1/sdncAdapterResponse.xml")));
		stubFor(post(urlEqualTo("/SDNCAdapter"))
				  .withRequestBody(containing("SvcAction>unassign"))
				  .willReturn(aResponse()
				  .withStatus(200)
				  .withHeader("Content-Type", "text/xml")
				  .withBodyFile("DeleteGenericVNFV1/sdncAdapterResponse.xml")));
	}

	
	public static void MockDoDeleteVfModule_DeleteVNFSuccess() {
		stubFor(delete(urlMatching("/vnfs/v1/vnfs/.*/vf-modules/.*"))
				.willReturn(aResponse()
				.withStatus(202)
				.withHeader("Content-Type", "application/xml")));
		stubFor(delete(urlMatching("/vnfs/v1/volume-groups/78987"))
				.willReturn(aResponse()
				.withStatus(202)
				.withHeader("Content-Type", "application/xml")));
	}
}