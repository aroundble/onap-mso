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

package org.openecomp.mso.apihandlerinfra;


public class Constants {

	private Constants() {
	}

	public static final String VNF_TYPES_PATH = "/{version: v1|v2|v3}/vnf-types";
	public static final String NETWORK_TYPES_PATH = "/{version: v1|v2|v3}/network-types";
	public static final String VF_MODULE_MODEL_NAMES_PATH = "/{version: v2|v3}/vf-module-model-names";
	public static final String REQUEST_ID_PATH = "/{request-id}";

	public static final String STATUS_SUCCESS = "SUCCESS";

	public static final String MODIFIED_BY_APIHANDLER = "APIH";

	public static final String SCHEMA_VERSION_V1 = "v1";
	public static final String SCHEMA_VERSION_V2 = "v2";
	public static final String SCHEMA_VERSION_V3 = "v3";

	public static final long PROGRESS_REQUEST_COMPLETED = 100L;
	public static final long PROGRESS_REQUEST_RECEIVED = 0L;
	public static final long PROGRESS_REQUEST_IN_PROGRESS = 20L;

	public static final String VNF_TYPE_WILDCARD = "*";

	public static final String VOLUME_GROUP_COMPONENT_TYPE = "VOLUME_GROUP";

	public static final String VALID_INSTANCE_NAME_FORMAT = "^[a-zA-Z][a-zA-Z0-9._-]*$";

	public static final String A_LA_CARTE = "aLaCarte";
}
