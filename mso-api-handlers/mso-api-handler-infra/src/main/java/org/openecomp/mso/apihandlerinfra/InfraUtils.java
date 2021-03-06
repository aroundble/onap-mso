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



import java.util.Arrays;

import org.openecomp.mso.properties.MsoJavaProperties;

public class InfraUtils {
	public static boolean isActionAllowed(MsoJavaProperties props, String requestType, String version, String action) {
		 // Check for allowable actions
        String actionsPropertyName = requestType + "." + version + ".ApiAllowableActions";
        String allowableActions = props.getProperty(actionsPropertyName, null);
        return allowableActions == null || Arrays.asList(allowableActions.split(",")).contains(action);
	}
	
	// Checks if the name is acceptable for heat stack
	public static boolean isValidHeatName(String name) {
		return name.matches("^[a-zA-Z][a-zA-Z0-9_\\.-]*$");
	}
	
	
}
