/*-
 * ============LICENSE_START=======================================================
 * ONAP - SO
 * ================================================================================
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

package org.openecomp.mso.apihandlerinfra.serviceinstancebeans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties({ "additionalProperties" })
public class E2EParameters {

	@JsonProperty("domainHost")
	private String domainHost;
	@JsonProperty("nodeTemplateName")
	private String nodeTemplateName;
	@JsonProperty("nodeType")
	private String nodeType;
	@JsonProperty("segments")
	
	
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<>();

	public String getDomainHost() {
		return domainHost;
	}

	public void setDomainHost(String domainHost) {
		this.domainHost = domainHost;
	}

	public String getNodeTemplateName() {
		return nodeTemplateName;
	}

	public void setNodeTemplateName(String nodeTemplateName) {
		this.nodeTemplateName = nodeTemplateName;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}


	public Map<String, Object> getAdditionalProperties() {
		return additionalProperties;
	}

	public void setAdditionalProperties(Map<String, Object> additionalProperties) {
		this.additionalProperties = additionalProperties;
	}

}
