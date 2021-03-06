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

package org.openecomp.mso.requestsdb;

// Generated Jul 27, 2015 3:05:00 PM by Hibernate Tools 3.4.0.CR1

import java.sql.Timestamp;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.openecomp.mso.requestsdb.adapter.TimestampXMLAdapter;

/**
 * InfraActiveRequests generated by hbm2java
 */
public class InfraRequests implements java.io.Serializable {

    /**
     * Serialization id.
     */
    private static final long serialVersionUID = -661307666798018192L;

    private String requestId;
	private String clientRequestId;
	private String action;
	private String requestStatus;
	private String statusMessage;
	private Long progress;
	private Timestamp startTime;
	private Timestamp endTime;
	private String source;
	private String vnfId;
	private String vnfName;
	private String vnfType;
	private String serviceType;
	private String aicNodeClli;
	private String tenantId;
	private String provStatus;
	private String vnfParams;
	private String vnfOutputs;
	private String requestBody;
	private String responseBody;
	private String lastModifiedBy;
	private Timestamp modifyTime;
	private String requestType;
	private String volumeGroupId;
	private String volumeGroupName;
	private String vfModuleId;
	private String vfModuleName;
	private String vfModuleModelName;
	private String aaiServiceId;
	private String aicCloudRegion;
	private String callBackUrl;
	private String correlator;
	private String serviceInstanceId;
	private String serviceInstanceName;
	private String requestScope;
	private String requestAction;
	private String networkId;
	private String networkName;
	private String networkType;
	private String requestorId;

	public InfraRequests() {
	}

	public InfraRequests(String requestId, String action) {
		this.requestId = requestId;
		this.action = action;
	}

	public String getRequestId() {
		return this.requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getClientRequestId() {
		return clientRequestId;
	}

	public void setClientRequestId(String clientRequestId) {
		this.clientRequestId = clientRequestId;
	}

	public String getAction() {
		return this.action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getRequestStatus() {
		return this.requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	public String getStatusMessage() {
		return this.statusMessage;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public Long getProgress() {
		return this.progress;
	}

	public void setProgress(Long progress) {
		this.progress = progress;
	}

    @XmlJavaTypeAdapter(TimestampXMLAdapter.class)
	public Timestamp getStartTime() {
		return this.startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

    @XmlJavaTypeAdapter(TimestampXMLAdapter.class)
	public Timestamp getEndTime() {
		return this.endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public String getSource() {
		return this.source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getVnfId() {
		return this.vnfId;
	}

	public void setVnfId(String vnfId) {
		this.vnfId = vnfId;
	}

	public String getVnfName() {
		return this.vnfName;
	}

	public void setVnfName(String vnfName) {
		this.vnfName = vnfName;
	}

	public String getVnfType() {
		return this.vnfType;
	}

	public void setVnfType(String vnfType) {
		this.vnfType = vnfType;
	}

	public String getServiceType() {
		return this.serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getAicNodeClli() {
		return this.aicNodeClli;
	}

	public void setAicNodeClli(String aicNodeClli) {
		this.aicNodeClli = aicNodeClli;
	}

	public String getTenantId() {
		return this.tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getProvStatus() {
		return this.provStatus;
	}

	public void setProvStatus(String provStatus) {
		this.provStatus = provStatus;
	}

	public String getVnfParams() {
		return this.vnfParams;
	}

	public void setVnfParams(String vnfParams) {
		this.vnfParams = vnfParams;
	}

	public String getVnfOutputs() {
		return this.vnfOutputs;
	}

	public void setVnfOutputs(String vnfOutputs) {
		this.vnfOutputs = vnfOutputs;
	}

	public String getRequestBody() {
		return this.requestBody;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public String getResponseBody() {
		return this.responseBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	public String getLastModifiedBy() {
		return this.lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

    @XmlJavaTypeAdapter(TimestampXMLAdapter.class)
	public Timestamp getModifyTime() {
		return this.modifyTime;
	}

	public void setModifyTime(Timestamp modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getRequestType() {
		return this.requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getVolumeGroupId() {
		return this.volumeGroupId;
	}

	public void setVolumeGroupId(String volumeGroupId) {
		this.volumeGroupId = volumeGroupId;
	}

	public String getVolumeGroupName() {
		return this.volumeGroupName;
	}

	public void setVolumeGroupName(String volumeGroupName) {
		this.volumeGroupName = volumeGroupName;
	}

	public String getVfModuleId() {
		return this.vfModuleId;
	}

	public void setVfModuleId(String vfModuleId) {
		this.vfModuleId = vfModuleId;
	}

	public String getVfModuleName() {
		return this.vfModuleName;
	}

	public void setVfModuleName(String vfModuleName) {
		this.vfModuleName = vfModuleName;
	}

	public String getVfModuleModelName() {
		return this.vfModuleModelName;
	}

	public void setVfModuleModelName(String vfModuleModelName) {
		this.vfModuleModelName = vfModuleModelName;
	}

	public String getAaiServiceId() {
		return this.aaiServiceId;
	}

	public void setAaiServiceId(String aaiServiceId) {
		this.aaiServiceId = aaiServiceId;
	}

	public String getAicCloudRegion() {
		return this.aicCloudRegion;
	}

	public void setAicCloudRegion(String aicCloudRegion) {
		this.aicCloudRegion = aicCloudRegion;
	}

	public String getCallBackUrl() {
		return callBackUrl;
	}

	public void setCallBackUrl(String callBackUrl) {
		this.callBackUrl = callBackUrl;
	}

	public String getCorrelator() {
		return correlator;
	}

	public void setCorrelator(String correlator) {
		this.correlator = correlator;
	}

	public String getServiceInstanceId() {
		return serviceInstanceId;
	}

	public void setServiceInstanceId(String serviceInstanceId) {
		this.serviceInstanceId = serviceInstanceId;
	}

	public String getServiceInstanceName() {
		return serviceInstanceName;
	}

	public void setServiceInstanceName(String serviceInstanceName) {
		this.serviceInstanceName = serviceInstanceName;
	}

	public String getRequestScope() {
		return requestScope;
	}

	public void setRequestScope(String requestScope) {
		this.requestScope = requestScope;
	}

	public String getRequestAction() {
		return requestAction;
	}

	public void setRequestAction(String requestAction) {
		this.requestAction = requestAction;
	}

	public String getNetworkId() {
		return networkId;
	}

	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}

	public String getNetworkName() {
		return networkName;
	}

	public void setNetworkName(String networkName) {
		this.networkName = networkName;
	}

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public String getRequestorId() {
		return requestorId;
	}

	public void setRequestorId(String requestorId) {
		this.requestorId = requestorId;
	}

}
