package org.openecomp.mso.bpmn.vcpe.scripts


import org.camunda.bpm.engine.ProcessEngineServices
import org.camunda.bpm.engine.RepositoryService
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity
import org.camunda.bpm.engine.repository.ProcessDefinition
import org.camunda.bpm.engine.runtime.Execution
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.Ignore
import org.mockito.MockitoAnnotations
import org.mockito.ArgumentCaptor
import org.camunda.bpm.engine.delegate.BpmnError
import org.openecomp.mso.bpmn.common.scripts.MsoUtils
import org.openecomp.mso.bpmn.core.WorkflowException
import org.openecomp.mso.bpmn.mock.FileUtil

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.patch
import static com.github.tomakehurst.wiremock.client.WireMock.put
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching
import static org.junit.Assert.*;
import static org.mockito.Mockito.*
import static org.openecomp.mso.bpmn.mock.StubResponseAAI.MockGetAllottedResource
import org.openecomp.mso.bpmn.core.RollbackData

import com.github.tomakehurst.wiremock.junit.WireMockRule

class DoCreateAllottedResourceBRGTest  {
	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(28090)
	
		static def urnProps = new Properties()
		static def aaiUriPfx
	
		String Prefix="DCARBRG_"
		def utils = new MsoUtils()

  		@BeforeClass
		public static void setUpBeforeClass() {
			def fr = new FileReader("src/test/resources/mso.bpmn.urn.properties")
			urnProps.load(fr)
			fr.close()
			
			aaiUriPfx = urnProps.get("aai.endpoint")
		}
		  
	    @Before
		public void init()
		{
			MockitoAnnotations.initMocks(this)
		}
		
		
		// ***** preProcessRequest *****
				
		@Test
//		@Ignore  
		public void preProcessRequest() {
			ExecutionEntity mex = setupMock()
			initPreProcess(mex)
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			DoCreateAllottedResourceBRG.preProcessRequest(mex)

			verify(mex).getVariable("isDebugLogEnabled")
			verify(mex).setVariable("prefix", Prefix)
					
			assertTrue(checkMissingPreProcessRequest("URN_mso_workflow_sdncadapter_callback"))
			assertTrue(checkMissingPreProcessRequest("serviceInstanceId"))
			assertTrue(checkMissingPreProcessRequest("parentServiceInstanceId"))
			assertTrue(checkMissingPreProcessRequest("allottedResourceModelInfo"))
			assertTrue(checkMissingPreProcessRequest("vni"))
			assertTrue(checkMissingPreProcessRequest("vgmuxBearerIP"))
			assertTrue(checkMissingPreProcessRequest("brgWanMacAddress"))
			assertTrue(checkMissingPreProcessRequest("allottedResourceRole"))
			assertTrue(checkMissingPreProcessRequest("allottedResourceType"))
		}
		
		
		// ***** getAaiAR *****
		
		@Test
//		@Ignore
		public void getAaiAR() {
			def arData = FileUtil.readResourceFile("__files/VCPE/DoCreateAllottedResourceBRG/getAR.xml")
			def arBrg = FileUtil.readResourceFile("__files/VCPE/DoCreateAllottedResourceBRG/getArBrg.xml")
			
			wireMockRule
				.stubFor(get(urlMatching("/aai/v[0-9]+/mylink"))
						.willReturn(aResponse()
							.withStatus(200)
							.withHeader("Content-Type", "text/xml")
							.withBodyFile("VCPE/DoCreateAllottedResourceBRG/getArBrg.xml")))
			
			ExecutionEntity mex = setupMock()
			
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("allottedResourceType")).thenReturn("BRGt")
			when(mex.getVariable("allottedResourceRole")).thenReturn("BRGr")
			when(mex.getVariable("CSI_service")).thenReturn(arData)
			when(mex.getVariable("URN_aai_endpoint")).thenReturn(aaiUriPfx)
			when(mex.getVariable("aaiAROrchStatus")).thenReturn("Active")
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			DoCreateAllottedResourceBRG.getAaiAR(mex)
			
			verify(mex).setVariable("foundActiveAR", true)
		}
		
		@Test
//		@Ignore
		public void getAaiAR_Duplicate() {
			def arData = FileUtil.readResourceFile("__files/VCPE/DoCreateAllottedResourceBRG/getAR.xml")
			def arBrg = FileUtil.readResourceFile("__files/VCPE/DoCreateAllottedResourceBRG/getArBrg.xml")
			
			wireMockRule
				.stubFor(get(urlMatching("/aai/v[0-9]+/mylink"))
						.willReturn(aResponse()
							.withStatus(200)
							.withHeader("Content-Type", "text/xml")
							.withBodyFile("VCPE/DoCreateAllottedResourceBRG/getArBrg.xml")))
			
			ExecutionEntity mex = setupMock()
			
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("allottedResourceType")).thenReturn("BRGt")
			when(mex.getVariable("allottedResourceRole")).thenReturn("BRGr")
			when(mex.getVariable("CSI_service")).thenReturn(arData)
			when(mex.getVariable("URN_aai_endpoint")).thenReturn(aaiUriPfx)
			when(mex.getVariable("aaiAROrchStatus")).thenReturn("Active")
			
			// fail if duplicate
			when(mex.getVariable("failExists")).thenReturn("true")
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			assertTrue(doBpmnError( { _ -> DoCreateAllottedResourceBRG.getAaiAR(mex) }))
		}
		
		@Test
//		@Ignore
		public void getAaiAR_NotActive() {
			def arData = FileUtil.readResourceFile("__files/VCPE/DoCreateAllottedResourceBRG/getAR.xml")
			def arBrg = FileUtil.readResourceFile("__files/VCPE/DoCreateAllottedResourceBRG/getArBrg.xml")
			
			wireMockRule
				.stubFor(get(urlMatching("/aai/v[0-9]+/mylink"))
						.willReturn(aResponse()
							.withStatus(200)
							.withHeader("Content-Type", "text/xml")
							.withBodyFile("VCPE/DoCreateAllottedResourceBRG/getArBrg.xml")))
			
			ExecutionEntity mex = setupMock()
			
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("allottedResourceType")).thenReturn("BRGt")
			when(mex.getVariable("allottedResourceRole")).thenReturn("BRGr")
			when(mex.getVariable("CSI_service")).thenReturn(arData)
			when(mex.getVariable("URN_aai_endpoint")).thenReturn(aaiUriPfx)
			
			// not active
			when(mex.getVariable("aaiAROrchStatus")).thenReturn("not-active")
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			assertTrue(doBpmnError( { _ -> DoCreateAllottedResourceBRG.getAaiAR(mex) }))
		}
		
		@Test
//		@Ignore
		public void getAaiAR_NoStatus() {
			def arData = FileUtil.readResourceFile("__files/VCPE/DoCreateAllottedResourceBRG/getAR.xml")
			def arBrg = FileUtil.readResourceFile("__files/VCPE/DoCreateAllottedResourceBRG/getArBrg.xml")
			
			wireMockRule
				.stubFor(get(urlMatching("/aai/v[0-9]+/mylink"))
						.willReturn(aResponse()
							.withStatus(200)
							.withHeader("Content-Type", "text/xml")
							.withBodyFile("VCPE/DoCreateAllottedResourceBRG/getArBrg.xml")))
			
			ExecutionEntity mex = setupMock()
			
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("allottedResourceType")).thenReturn("BRGt")
			when(mex.getVariable("allottedResourceRole")).thenReturn("BRGr")
			when(mex.getVariable("CSI_service")).thenReturn(arData)
			when(mex.getVariable("URN_aai_endpoint")).thenReturn(aaiUriPfx)
			
			when(mex.getVariable("aaiAROrchStatus")).thenReturn(null)
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			DoCreateAllottedResourceBRG.getAaiAR(mex)
			
			verify(mex, never()).setVariable("foundActiveAR", true)
		}
		
		
		// ***** createAaiAR *****
		
		@Test
//		@Ignore
		public void createAaiAR() {
			ExecutionEntity mex = setupMock()
			initCreateAaiAr(mex)
			
			wireMockRule
				.stubFor(put(urlMatching("/aai/v[0-9]+/mypsi/allotted-resources/allotted-resource/myid"))
						.willReturn(aResponse()
							.withStatus(200)))
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			DoCreateAllottedResourceBRG.createAaiAR(mex)
			
			ArgumentCaptor<String> keycap = ArgumentCaptor.forClass(String.class)
			ArgumentCaptor<Object> valcap = ArgumentCaptor.forClass(Object.class)
			
			verify(mex, times(3)).setVariable(keycap.capture(), valcap.capture())
			
			assertFalse(keycap.getAllValues().isEmpty())
			assertEquals("rollbackData", keycap.getAllValues().get(keycap.getAllValues().size()-1))
			
			def data = valcap.getAllValues().get(valcap.getAllValues().size()-1)
			assertNotNull(data)
			assertTrue(data instanceof RollbackData)
			
			assertEquals("45", data.get(Prefix, "disableRollback"))
			assertEquals("true", data.get(Prefix, "rollbackAAI"))
			assertEquals("myid", data.get(Prefix, "allottedResourceId"))
			assertEquals("sii", data.get(Prefix, "serviceInstanceId"))
			assertEquals("psii", data.get(Prefix, "parentServiceInstanceId"))
			assertEquals(aaiUriPfx+"/aai/v9/mypsi/allotted-resources/allotted-resource/myid", data.get(Prefix, "aaiARPath"))
		}
		
		@Test
//		@Ignore
		public void createAaiAR_NoArid_NoModelUuids() {
			ExecutionEntity mex = setupMock()
			initCreateAaiAr(mex)
				
			// no allottedResourceId - will be generated
			
			when(mex.getVariable("allottedResourceId")).thenReturn(null)
			
			wireMockRule
				.stubFor(put(urlMatching("/aai/v[0-9]+/mypsi/allotted-resources/allotted-resource/.*"))
						.willReturn(aResponse()
							.withStatus(200)))
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			DoCreateAllottedResourceBRG.createAaiAR(mex)
			
			ArgumentCaptor<String> keycap = ArgumentCaptor.forClass(String.class)
			ArgumentCaptor<Object> valcap = ArgumentCaptor.forClass(Object.class)
			
			verify(mex, times(4)).setVariable(keycap.capture(), valcap.capture())
			
			assertFalse(keycap.getAllValues().isEmpty())
			assertEquals("allottedResourceId", keycap.getAllValues().get(0))
			assertEquals("rollbackData", keycap.getAllValues().get(keycap.getAllValues().size()-1))
			
			def arid = valcap.getAllValues().get(0)
			assertNotNull(arid)
			assertFalse(arid.isEmpty())
			
			def data = valcap.getAllValues().get(valcap.getAllValues().size()-1)
			assertNotNull(data)
			assertTrue(data instanceof RollbackData)
			
			assertEquals(arid, data.get(Prefix, "allottedResourceId"))
		}
		
		@Test
//		@Ignore
		public void createAaiAR_MissingPsiLink() {
			ExecutionEntity mex = setupMock()
			initCreateAaiAr(mex)
			
			when(mex.getVariable("PSI_resourceLink")).thenReturn(null)
			
			wireMockRule
				.stubFor(put(urlMatching("/aai/v[0-9]+/mypsi/allotted-resources/allotted-resource/myid"))
						.willReturn(aResponse()
							.withStatus(200)))
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			assertTrue(doBpmnError({ _ -> DoCreateAllottedResourceBRG.createAaiAR(mex) }))
		}
		
		@Test
//		@Ignore
		public void createAaiAR_HttpFailed() {
			ExecutionEntity mex = setupMock()
			initCreateAaiAr(mex)
			
			// return 500 status
			wireMockRule
				.stubFor(put(urlMatching("/aai/v[0-9]+/mypsi/allotted-resources/allotted-resource/myid"))
						.willReturn(aResponse()
							.withStatus(500)))
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			assertTrue(doBpmnError({ _ -> DoCreateAllottedResourceBRG.createAaiAR(mex) }))
		}
		
		@Test
//		@Ignore
		public void createAaiAR_BpmnError() {
			ExecutionEntity mex = setupMock()
			initCreateAaiAr(mex)
			
			when(mex.getVariable("URN_aai_endpoint")).thenThrow(new BpmnError("expected exception"))
			
			wireMockRule
				.stubFor(put(urlMatching("/aai/v[0-9]+/mypsi/allotted-resources/allotted-resource/myid"))
						.willReturn(aResponse()
							.withStatus(200)))
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			assertTrue(doBpmnError({ _ -> DoCreateAllottedResourceBRG.createAaiAR(mex) }))
		}
		
		@Test
//		@Ignore
		public void createAaiAR_Ex() {
			ExecutionEntity mex = setupMock()
			initCreateAaiAr(mex)
			
			when(mex.getVariable("URN_aai_endpoint")).thenThrow(new RuntimeException("expected exception"))
			
			wireMockRule
				.stubFor(put(urlMatching("/aai/v[0-9]+/mypsi/allotted-resources/allotted-resource/myid"))
						.willReturn(aResponse()
							.withStatus(200)))
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			assertTrue(doBpmnError({ _ -> DoCreateAllottedResourceBRG.createAaiAR(mex) }))
		}
		
		
		// ***** buildSDNCRequest *****
		
		@Test
//		@Ignore
		public void buildSDNCRequest() {
			ExecutionEntity mex = setupMock()
			initBuildSDNCRequest(mex)
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			String result = DoCreateAllottedResourceBRG.buildSDNCRequest(mex, "myact", "myreq")
			
			assertTrue(result.indexOf("<sdncadapter:RequestId>myreq</") >= 0)
			assertTrue(result.indexOf("<sdncadapter:SvcAction>myact</") >= 0)
			assertTrue(result.indexOf("<allotted-resource-id>ari</") >= 0)
			assertTrue(result.indexOf("<sdncadapter:SvcInstanceId>sii</") >= 0)
			assertTrue(result.indexOf("<service-instance-id>psii</") >= 0)
			assertTrue(result.indexOf("<parent-service-instance-id>psii</") >= 0)
			assertTrue(result.indexOf("<sdncadapter:CallbackUrl>scu</") >= 0)
			assertTrue(result.indexOf("<request-id>mri</") >= 0)
			assertTrue(result.indexOf("<brg-wan-mac-address>bwma</") >= 0)
			assertTrue(result.indexOf("<vni>myvni</") >= 0)
			assertTrue(result.indexOf("<vgmux-bearer-ip>vbi</") >= 0)
			assertTrue(result.indexOf("<model-invariant-uuid>miu</") >= 0)
			assertTrue(result.indexOf("<model-uuid>mu</") >= 0)
			assertTrue(result.indexOf("<model-customization-uuid>mcu</") >= 0)
			assertTrue(result.indexOf("<model-version>mv</") >= 0)
			assertTrue(result.indexOf("<model-name>mn</") >= 0)
		}
		
		@Test
//		@Ignore
		public void buildSDNCRequest_EmptyModelInfo() {
			ExecutionEntity mex = setupMock()
			initBuildSDNCRequest(mex)
			
			when(mex.getVariable("allottedResourceModelInfo")).thenReturn("{}")
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			String result = DoCreateAllottedResourceBRG.buildSDNCRequest(mex, "myact", "myreq")
			
			assertTrue(result.indexOf("<sdncadapter:RequestId>myreq</") >= 0)
			assertTrue(result.indexOf("<sdncadapter:SvcAction>myact</") >= 0)
			assertTrue(result.indexOf("<allotted-resource-id>ari</") >= 0)
			assertTrue(result.indexOf("<sdncadapter:SvcInstanceId>sii</") >= 0)
			assertTrue(result.indexOf("<service-instance-id>psii</") >= 0)
			assertTrue(result.indexOf("<parent-service-instance-id>psii</") >= 0)
			assertTrue(result.indexOf("<sdncadapter:CallbackUrl>scu</") >= 0)
			assertTrue(result.indexOf("<request-id>mri</") >= 0)
			assertTrue(result.indexOf("<brg-wan-mac-address>bwma</") >= 0)
			assertTrue(result.indexOf("<vni>myvni</") >= 0)
			assertTrue(result.indexOf("<vgmux-bearer-ip>vbi</") >= 0)
			assertTrue(result.indexOf("<model-invariant-uuid/>") >= 0)
			assertTrue(result.indexOf("<model-uuid/>") >= 0)
			assertTrue(result.indexOf("<model-customization-uuid/>") >= 0)
			assertTrue(result.indexOf("<model-version/>") >= 0)
			assertTrue(result.indexOf("<model-name/>") >= 0)
		}
		
		@Test
//		@Ignore
		public void buildSDNCRequest_Ex() {
			ExecutionEntity mex = setupMock()
			initBuildSDNCRequest(mex)
			
			when(mex.getVariable("allottedResourceId")).thenThrow(new RuntimeException("expected exception"))
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			assertTrue(doBpmnError({ _ -> DoCreateAllottedResourceBRG.buildSDNCRequest(mex, "myact", "myreq") }))
		}
		
		
		// ***** preProcessSDNCAssign *****
		
		@Test
//		@Ignore
		public void preProcessSDNCAssign() {
			ExecutionEntity mex = setupMock()
			def data = initPreProcessSDNC(mex)
						
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			DoCreateAllottedResourceBRG.preProcessSDNCAssign(mex)
			
			ArgumentCaptor<String> keycap = ArgumentCaptor.forClass(String.class)
			ArgumentCaptor<Object> valcap = ArgumentCaptor.forClass(Object.class)
			
			verify(mex, times(2)).setVariable(keycap.capture(), valcap.capture())
			
			assertFalse(keycap.getAllValues().isEmpty())
			assertEquals("sdncAssignRequest", keycap.getAllValues().get(0))
			assertEquals("rollbackData", keycap.getAllValues().get(keycap.getAllValues().size()-1))
			
			def req = valcap.getAllValues().get(0)
			assertNotNull(req)
			
			assertEquals(data, valcap.getAllValues().get(valcap.getAllValues().size()-1))
			
			def rbreq = data.get(Prefix, "sdncAssignRollbackReq")
			
			assertTrue(req.indexOf("<sdncadapter:SvcAction>assign</") >= 0)
			assertTrue(req.indexOf("<request-action>CreateBRGInstance</") >= 0)
			assertTrue(req.indexOf("<sdncadapter:RequestId>") >= 0)
			
			assertTrue(rbreq.indexOf("<sdncadapter:SvcAction>unassign</") >= 0)
			assertTrue(rbreq.indexOf("<request-action>DeleteBRGInstance</") >= 0)
			assertTrue(rbreq.indexOf("<sdncadapter:RequestId>") >= 0)
		}
		
		@Test
//		@Ignore
		public void preProcessSDNCAssign_BpmnError() {
			ExecutionEntity mex = setupMock()
			initPreProcessSDNC(mex)
			
			when(mex.getVariable("rollbackData")).thenThrow(new BpmnError("expected exception"))
						
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			assertTrue(doBpmnError({ _ -> DoCreateAllottedResourceBRG.preProcessSDNCAssign(mex) }))
		}
		
		@Test
//		@Ignore
		public void preProcessSDNCAssign_Ex() {
			ExecutionEntity mex = setupMock()
			initPreProcessSDNC(mex)
			
			when(mex.getVariable("rollbackData")).thenThrow(new RuntimeException("expected exception"))
						
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			assertTrue(doBpmnError({ _ -> DoCreateAllottedResourceBRG.preProcessSDNCAssign(mex) }))
		}
		
		
		// ***** preProcessSDNCCreate *****
		
		@Test
//		@Ignore
		public void preProcessSDNCCreate() {
			ExecutionEntity mex = setupMock()
			def data = initPreProcessSDNC(mex)
						
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			DoCreateAllottedResourceBRG.preProcessSDNCCreate(mex)
			
			ArgumentCaptor<String> keycap = ArgumentCaptor.forClass(String.class)
			ArgumentCaptor<Object> valcap = ArgumentCaptor.forClass(Object.class)
			
			verify(mex, times(2)).setVariable(keycap.capture(), valcap.capture())
			
			assertFalse(keycap.getAllValues().isEmpty())
			assertEquals("sdncCreateRequest", keycap.getAllValues().get(0))
			assertEquals("rollbackData", keycap.getAllValues().get(keycap.getAllValues().size()-1))
			
			def req = valcap.getAllValues().get(0)
			assertNotNull(req)
			
			assertEquals(data, valcap.getAllValues().get(valcap.getAllValues().size()-1))
			
			def rbreq = data.get(Prefix, "sdncCreateRollbackReq")
			
			assertTrue(req.indexOf("<sdncadapter:SvcAction>create</") >= 0)
			assertTrue(req.indexOf("<request-action>CreateBRGInstance</") >= 0)
			assertTrue(req.indexOf("<sdncadapter:RequestId>") >= 0)
			
			assertTrue(rbreq.indexOf("<sdncadapter:SvcAction>delete</") >= 0)
			assertTrue(rbreq.indexOf("<request-action>DeleteBRGInstance</") >= 0)
			assertTrue(rbreq.indexOf("<sdncadapter:RequestId>") >= 0)
			
		}
		
		@Test
//		@Ignore
		public void preProcessSDNCCreate_BpmnError() {
			ExecutionEntity mex = setupMock()
			initPreProcessSDNC(mex)
			
			when(mex.getVariable("rollbackData")).thenThrow(new BpmnError("expected exception"))
						
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			assertTrue(doBpmnError({ _ -> DoCreateAllottedResourceBRG.preProcessSDNCCreate(mex) }))
		}
		
		@Test
//		@Ignore
		public void preProcessSDNCCreate_Ex() {
			ExecutionEntity mex = setupMock()
			initPreProcessSDNC(mex)
			
			when(mex.getVariable("rollbackData")).thenThrow(new RuntimeException("expected exception"))
						
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			assertTrue(doBpmnError({ _ -> DoCreateAllottedResourceBRG.preProcessSDNCCreate(mex) }))
		}
		
		
		// ***** preProcessSDNCActivate *****
		
		@Test
//		@Ignore
		public void preProcessSDNCActivate() {
			ExecutionEntity mex = setupMock()
			def data = initPreProcessSDNC(mex)
						
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			DoCreateAllottedResourceBRG.preProcessSDNCActivate(mex)
			
			ArgumentCaptor<String> keycap = ArgumentCaptor.forClass(String.class)
			ArgumentCaptor<Object> valcap = ArgumentCaptor.forClass(Object.class)
			
			verify(mex, times(2)).setVariable(keycap.capture(), valcap.capture())
			
			assertFalse(keycap.getAllValues().isEmpty())
			assertEquals("sdncActivateRequest", keycap.getAllValues().get(0))
			assertEquals("rollbackData", keycap.getAllValues().get(keycap.getAllValues().size()-1))
			
			def req = valcap.getAllValues().get(0)
			assertNotNull(req)
			
			assertEquals(data, valcap.getAllValues().get(valcap.getAllValues().size()-1))
			
			def rbreq = data.get(Prefix, "sdncActivateRollbackReq")
			
			assertTrue(req.indexOf("<sdncadapter:SvcAction>activate</") >= 0)
			assertTrue(req.indexOf("<request-action>CreateBRGInstance</") >= 0)
			assertTrue(req.indexOf("<sdncadapter:RequestId>") >= 0)
			
			assertTrue(rbreq.indexOf("<sdncadapter:SvcAction>deactivate</") >= 0)
			assertTrue(rbreq.indexOf("<request-action>DeleteBRGInstance</") >= 0)
			assertTrue(rbreq.indexOf("<sdncadapter:RequestId>") >= 0)
			
		}
		
		@Test
//		@Ignore
		public void preProcessSDNCActivate_BpmnError() {
			ExecutionEntity mex = setupMock()
			initPreProcessSDNC(mex)
			
			when(mex.getVariable("rollbackData")).thenThrow(new BpmnError("expected exception"))
						
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			assertTrue(doBpmnError({ _ -> DoCreateAllottedResourceBRG.preProcessSDNCActivate(mex) }))
		}
		
		@Test
//		@Ignore
		public void preProcessSDNCActivate_Ex() {
			ExecutionEntity mex = setupMock()
			initPreProcessSDNC(mex)
			
			when(mex.getVariable("rollbackData")).thenThrow(new RuntimeException("expected exception"))
						
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			assertTrue(doBpmnError({ _ -> DoCreateAllottedResourceBRG.preProcessSDNCActivate(mex) }))
		}
		
		
		// ***** validateSDNCResp *****
		
		@Test
//		@Ignore
		public void validateSDNCResp() {
			ExecutionEntity mex = setupMock()
			def data = initValidateSDNCResp(mex)
			def resp = initValidateSDNCResp_Resp()
			
			when(mex.getVariable(Prefix+"sdncResponseSuccess")).thenReturn(true)
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			DoCreateAllottedResourceBRG.validateSDNCResp(mex, resp, "create")
			
			verify(mex).getVariable("WorkflowException")
			verify(mex).getVariable("SDNCA_SuccessIndicator")
			verify(mex).getVariable("rollbackData")
			
			ArgumentCaptor<String> keycap = ArgumentCaptor.forClass(String.class)
			ArgumentCaptor<Object> valcap = ArgumentCaptor.forClass(Object.class)
			
			verify(mex, times(4)).setVariable(keycap.capture(), valcap.capture())
			
			assertFalse(keycap.getAllValues().isEmpty())
			assertEquals("rollbackData", keycap.getAllValues().get(keycap.getAllValues().size()-1))
			
			assertEquals(data, valcap.getAllValues().get(valcap.getAllValues().size()-1))
			
			assertEquals("true", data.get(Prefix, "rollback" +  "SDNCcreate"))
			
		}
		
		@Test
//		@Ignore
		public void validateSDNCResp_Get() {
			ExecutionEntity mex = setupMock()
			def data = initValidateSDNCResp(mex)
			def resp = initValidateSDNCResp_Resp()
			
			when(mex.getVariable(Prefix+"sdncResponseSuccess")).thenReturn(true)
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			DoCreateAllottedResourceBRG.validateSDNCResp(mex, resp, "get")
			
			verify(mex).getVariable("WorkflowException")
			verify(mex).getVariable("SDNCA_SuccessIndicator")
			
			verify(mex, never()).getVariable("rollbackData")
		}
		
		@Test
//		@Ignore
		public void validateSDNCResp_Unsuccessful() {
			ExecutionEntity mex = setupMock()
			initValidateSDNCResp(mex)
			def resp = initValidateSDNCResp_Resp()
			
			// unsuccessful
			when(mex.getVariable(Prefix+"sdncResponseSuccess")).thenReturn(false)
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			assertTrue(doBpmnError({ _ -> DoCreateAllottedResourceBRG.validateSDNCResp(mex, resp, "create") }))
		}
		
		@Test
//		@Ignore
		public void validateSDNCResp_BpmnError() {
			ExecutionEntity mex = setupMock()
			initValidateSDNCResp(mex)
			def resp = initValidateSDNCResp_Resp()
			
			when(mex.getVariable("WorkflowException")).thenThrow(new BpmnError("expected exception"))
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			assertTrue(doBpmnError({ _ -> DoCreateAllottedResourceBRG.validateSDNCResp(mex, resp, "create") }))
		}
		
		@Test
//		@Ignore
		public void validateSDNCResp_Ex() {
			ExecutionEntity mex = setupMock()
			initValidateSDNCResp(mex)
			def resp = initValidateSDNCResp_Resp()
			
			when(mex.getVariable("WorkflowException")).thenThrow(new RuntimeException("expected exception"))
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			assertTrue(doBpmnError({ _ -> DoCreateAllottedResourceBRG.validateSDNCResp(mex, resp, "create") }))
		}
		
		
		// ***** preProcessSDNCGet *****
		
		@Test
//		@Ignore
		public void preProcessSDNCGet_FoundAR() {
			ExecutionEntity mex = setupMock()
			initPreProcessSDNCGet(mex)
						
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			DoCreateAllottedResourceBRG.preProcessSDNCGet(mex)
			
			ArgumentCaptor<String> keycap = ArgumentCaptor.forClass(String.class)
			ArgumentCaptor<Object> valcap = ArgumentCaptor.forClass(Object.class)
			
			verify(mex, times(1)).setVariable(keycap.capture(), valcap.capture())
			
			assertFalse(keycap.getAllValues().isEmpty())
			assertEquals("sdncGetRequest", keycap.getAllValues().get(keycap.getAllValues().size()-1))
			
			String req = valcap.getAllValues().get(valcap.getAllValues().size()-1)
			
			assertTrue(req.indexOf("<sdncadapter:RequestId>") >= 0)
			assertTrue(req.indexOf("<sdncadapter:SvcInstanceId>sii</") >= 0)
			assertTrue(req.indexOf("<sdncadapter:SvcOperation>arlink</") >= 0)
			assertTrue(req.indexOf("<sdncadapter:CallbackUrl>myurl</") >= 0)
			
		}
		
		@Test
//		@Ignore
		public void preProcessSDNCGet_NotFoundAR() {
			ExecutionEntity mex = setupMock()
			initPreProcessSDNCGet(mex)
			
			when(mex.getVariable("foundActiveAR")).thenReturn(false)
						
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			DoCreateAllottedResourceBRG.preProcessSDNCGet(mex)
			
			ArgumentCaptor<String> keycap = ArgumentCaptor.forClass(String.class)
			ArgumentCaptor<Object> valcap = ArgumentCaptor.forClass(Object.class)
			
			verify(mex, times(1)).setVariable(keycap.capture(), valcap.capture())
			
			assertFalse(keycap.getAllValues().isEmpty())
			assertEquals("sdncGetRequest", keycap.getAllValues().get(keycap.getAllValues().size()-1))
			
			String req = valcap.getAllValues().get(valcap.getAllValues().size()-1)
			
			assertTrue(req.indexOf("<sdncadapter:RequestId>") >= 0)
			assertTrue(req.indexOf("<sdncadapter:SvcInstanceId>sii</") >= 0)
			assertTrue(req.indexOf("<sdncadapter:SvcOperation>assignlink</") >= 0)
			assertTrue(req.indexOf("<sdncadapter:CallbackUrl>myurl</") >= 0)
			
		}
		
		@Test
//		@Ignore
		public void preProcessSDNCGet_Ex() {
			ExecutionEntity mex = setupMock()
			initPreProcessSDNCGet(mex)
			
			when(mex.getVariable("foundActiveAR")).thenThrow(new RuntimeException("expected exception"))
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			assertTrue(doBpmnError({ _ -> DoCreateAllottedResourceBRG.preProcessSDNCGet(mex) }))
		}
		
		
		// ***** updateAaiAROrchStatus *****
		
		@Test
//		@Ignore
		public void updateAaiAROrchStatus() {
			ExecutionEntity mex = setupMock()
			initUpdateAaiAROrchStatus(mex)
						
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			DoCreateAllottedResourceBRG.updateAaiAROrchStatus(mex, "success")
		}
		
		
		// ***** generateOutputs *****
		
		@Test
//		@Ignore
		public void generateOutputs() {
			ExecutionEntity mex = setupMock()
			def brgtop = FileUtil.readResourceFile("__files/VCPE/DoCreateAllottedResourceBRG/SDNCTopologyQueryCallback.xml")
			
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("enhancedCallbackRequestData")).thenReturn(brgtop)
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			DoCreateAllottedResourceBRG.generateOutputs(mex)
			
			verify(mex).setVariable("allotedResourceName", "namefromrequest")
			
		}
		
		@Test
//		@Ignore
		public void generateOutputs_BadXml() {
			ExecutionEntity mex = setupMock()
			
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("enhancedCallbackRequestData")).thenReturn("invalid xml")
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			DoCreateAllottedResourceBRG.generateOutputs(mex)
			
			verify(mex, never()).setVariable(anyString(), anyString())
			
		}
		
		@Test
//		@Ignore
		public void generateOutputs_BpmnError() {
			ExecutionEntity mex = setupMock()
			
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("enhancedCallbackRequestData")).thenThrow(new BpmnError("expected exception"))
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			DoCreateAllottedResourceBRG.generateOutputs(mex)
			verify(mex, never()).setVariable(anyString(), anyString())
			
		}
		
		@Test
//		@Ignore
		public void generateOutputs_Ex() {
			ExecutionEntity mex = setupMock()
			
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("enhancedCallbackRequestData")).thenThrow(new RuntimeException("expected exception"))
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			DoCreateAllottedResourceBRG.generateOutputs(mex)
			verify(mex, never()).setVariable(anyString(), anyString())
			
		}
		
		
		// ***** preProcessRollback *****
		
		@Test
//		@Ignore
		public void preProcessRollback() {
			ExecutionEntity mex = setupMock()
			WorkflowException wfe = mock(WorkflowException.class)
			
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("WorkflowException")).thenReturn(wfe)
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			DoCreateAllottedResourceBRG.preProcessRollback(mex)
			
			verify(mex).setVariable("prevWorkflowException", wfe)
			
		}
		
		@Test
//		@Ignore
		public void preProcessRollback_NotWFE() {
			ExecutionEntity mex = setupMock()
			
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("WorkflowException")).thenReturn("I'm not a WFE")
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			DoCreateAllottedResourceBRG.preProcessRollback(mex)
			
//			verify(mex, never()).setVariable("prevWorkflowException", any())
			
		}
		
		@Test
//		@Ignore
		public void preProcessRollback_BpmnError() {
			ExecutionEntity mex = setupMock()
			
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("WorkflowException")).thenThrow(new BpmnError("expected exception"))
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			DoCreateAllottedResourceBRG.preProcessRollback(mex)
			
		}
		
		@Test
//		@Ignore
		public void preProcessRollback_Ex() {
			ExecutionEntity mex = setupMock()
			
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("WorkflowException")).thenThrow(new RuntimeException("expected exception"))
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			DoCreateAllottedResourceBRG.preProcessRollback(mex)
			
		}
		
		
		// ***** postProcessRollback *****
		
		@Test
//		@Ignore
		public void postProcessRollback() {
			ExecutionEntity mex = setupMock()
			WorkflowException wfe = mock(WorkflowException.class)
			
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("prevWorkflowException")).thenReturn(wfe)
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			DoCreateAllottedResourceBRG.postProcessRollback(mex)
			
			verify(mex).setVariable("WorkflowException", wfe)
			verify(mex).setVariable("rollbackData", null)
			
		}
		
		@Test
//		@Ignore
		public void postProcessRollback_NotWFE() {
			ExecutionEntity mex = setupMock()
			
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("prevWorkflowException")).thenReturn("I'm not a WFE")
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			DoCreateAllottedResourceBRG.postProcessRollback(mex)
			
//			verify(mex, never()).setVariable("WorkflowException", any())
			verify(mex).setVariable("rollbackData", null)
			
		}
		
		@Test
//		@Ignore
		public void postProcessRollback_BpmnError() {
			ExecutionEntity mex = setupMock()
			
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("prevWorkflowException")).thenThrow(new BpmnError("expected exception"))
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			assertTrue(doBpmnError({ _ -> DoCreateAllottedResourceBRG.postProcessRollback(mex) }))
			verify(mex, never()).setVariable("rollbackData", null)
			
		}
		
		@Test
//		@Ignore
		public void postProcessRollback_Ex() {
			ExecutionEntity mex = setupMock()
			
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("prevWorkflowException")).thenThrow(new RuntimeException("expected exception"))
			
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			DoCreateAllottedResourceBRG.postProcessRollback(mex)
			verify(mex, never()).setVariable("rollbackData", null)
			
		}
		
		private boolean checkMissingPreProcessRequest(String fieldnm) {
			ExecutionEntity mex = setupMock()
			initPreProcess(mex)
									
			DoCreateAllottedResourceBRG DoCreateAllottedResourceBRG = new DoCreateAllottedResourceBRG()
			
			when(mex.getVariable(fieldnm)).thenReturn("")
			
			return doBpmnError( { _ -> DoCreateAllottedResourceBRG.preProcessRequest(mex) })
		}
		
		private boolean doBpmnError(def func) {
			
			try {
				func()
				return false;
				
			} catch(BpmnError e) {
				return true;
			}
		}
		
		private void initPreProcess(ExecutionEntity mex) {
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("URN_mso_workflow_sdncadapter_callback")).thenReturn("sdncurn")
			when(mex.getVariable("serviceInstanceId")).thenReturn("sii")
			when(mex.getVariable("parentServiceInstanceId")).thenReturn("psii")
			when(mex.getVariable("allottedResourceModelInfo")).thenReturn("armi")
			when(mex.getVariable("vni")).thenReturn("myvni")
			when(mex.getVariable("vgmuxBearerIP")).thenReturn("vbi")
			when(mex.getVariable("brgWanMacAddress")).thenReturn("bwma")
			when(mex.getVariable("allottedResourceRole")).thenReturn("arr")
			when(mex.getVariable("allottedResourceType")).thenReturn("art")
		}
		
		private initCreateAaiAr(ExecutionEntity mex) {				
			when(mex.getVariable("disableRollback")).thenReturn(45)
			when(mex.getVariable("serviceInstanceId")).thenReturn("sii")
			when(mex.getVariable("parentServiceInstanceId")).thenReturn("psii")
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("allottedResourceId")).thenReturn("myid")
			when(mex.getVariable("URN_aai_endpoint")).thenReturn(aaiUriPfx)
			when(mex.getVariable("URN_mso_workflow_global_default_aai_namespace")).thenReturn(urnProps.get("mso.workflow.global.default.aai.namespace"))
			when(mex.getVariable("PSI_resourceLink")).thenReturn(aaiUriPfx+"/aai/v9/mypsi")
			when(mex.getVariable("allottedResourceType")).thenReturn("BRGt")
			when(mex.getVariable("allottedResourceRole")).thenReturn("BRGr")
			when(mex.getVariable("CSI_resourceLink")).thenReturn(aaiUriPfx+"/aai/v9/mycsi")
			when(mex.getVariable("allottedResourceModelInfo")).thenReturn("""
					{
						"modelInvariantUuid":"modelinvuuid",
						"modelUuid":"modeluuid",
						"modelCustomizationUuid":"modelcustuuid"
					}
				""")
		}
		
		private initBuildSDNCRequest(ExecutionEntity mex) {
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("allottedResourceId")).thenReturn("ari")
			when(mex.getVariable("serviceInstanceId")).thenReturn("sii")
			when(mex.getVariable("parentServiceInstanceId")).thenReturn("psii")
			when(mex.getVariable("sdncCallbackUrl")).thenReturn("scu")
			when(mex.getVariable("msoRequestId")).thenReturn("mri")
			when(mex.getVariable("brgWanMacAddress")).thenReturn("bwma")
			when(mex.getVariable("vni")).thenReturn("myvni")
			when(mex.getVariable("vgmuxBearerIP")).thenReturn("vbi")
			when(mex.getVariable("allottedResourceModelInfo")).thenReturn("""
					{
						"modelInvariantUuid":"miu",
						"modelUuid":"mu",
						"modelCustomizationUuid":"mcu",
						"modelVersion":"mv",
						"modelName":"mn"
					}
				""")
		}
		
		private RollbackData initPreProcessSDNC(ExecutionEntity mex) {
			def data = new RollbackData()
			
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("rollbackData")).thenReturn(data)
			
			return data
		}
		
		private initPreProcessSDNCGet(ExecutionEntity mex) {
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("sdncCallbackUrl")).thenReturn("myurl")
			when(mex.getVariable("foundActiveAR")).thenReturn(true)
			when(mex.getVariable("aaiARGetResponse")).thenReturn("<selflink>arlink</selflink>")
			when(mex.getVariable("sdncAssignResponse")).thenReturn("<response-data>&lt;object-path&gt;assignlink&lt;/object-path&gt;</response-data>")
			when(mex.getVariable("serviceInstanceId")).thenReturn("sii")
			when(mex.getVariable("junitSleepMs")).thenReturn("5")
			when(mex.getVariable("sdncCallbackUrl")).thenReturn("myurl")
		}
		
		private RollbackData initValidateSDNCResp(ExecutionEntity mex) {
			def data = new RollbackData()
			
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("prefix")).thenReturn(Prefix)
			when(mex.getVariable("SDNCA_SuccessIndicator")).thenReturn(true)
			when(mex.getVariable("rollbackData")).thenReturn(data)
			
			return data
		}
		
		private String initValidateSDNCResp_Resp() {
			return "<response-data>&lt;response-code&gt;200&lt;/response-code&gt;</response-data>"
		}
		
		private initUpdateAaiAROrchStatus(ExecutionEntity mex) {
			when(mex.getVariable("isDebugLogEnabled")).thenReturn("true")
			when(mex.getVariable("aaiARPath")).thenReturn(aaiUriPfx+"/aai/v9/myurl")
			
			wireMockRule
				.stubFor(patch(urlMatching("/aai/v[0-9]+/myurl"))
							.willReturn(aResponse()
								.withStatus(200)))
		}
		
		private ExecutionEntity setupMock() {
			
			ProcessDefinition mockProcessDefinition = mock(ProcessDefinition.class)
			when(mockProcessDefinition.getKey()).thenReturn("DoCreateAllottedResourceBRG")
			RepositoryService mockRepositoryService = mock(RepositoryService.class)
			when(mockRepositoryService.getProcessDefinition()).thenReturn(mockProcessDefinition)
			when(mockRepositoryService.getProcessDefinition().getKey()).thenReturn("DoCreateAllottedResourceBRG")
			when(mockRepositoryService.getProcessDefinition().getId()).thenReturn("100")
			ProcessEngineServices mockProcessEngineServices = mock(ProcessEngineServices.class)
			when(mockProcessEngineServices.getRepositoryService()).thenReturn(mockRepositoryService)
			
			ExecutionEntity mex = mock(ExecutionEntity.class)
			
			when(mex.getId()).thenReturn("100")
			when(mex.getProcessDefinitionId()).thenReturn("DoCreateAllottedResourceBRG")
			when(mex.getProcessInstanceId()).thenReturn("DoCreateAllottedResourceBRG")
			when(mex.getProcessEngineServices()).thenReturn(mockProcessEngineServices)
			when(mex.getProcessEngineServices().getRepositoryService().getProcessDefinition(mex.getProcessDefinitionId())).thenReturn(mockProcessDefinition)
			
			return mex
		}
		
}