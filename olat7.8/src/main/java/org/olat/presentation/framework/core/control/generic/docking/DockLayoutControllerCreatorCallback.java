/**
 * OLAT - Online Learning and Training<br>
 * http://www.olat.org
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Copyright (c) since 2009 at frentix GmbH, Switzerland<br>
 * http://www.frentix.com
 * <p>
 */
package org.olat.presentation.framework.core.control.generic.docking;

import org.olat.presentation.framework.core.UserRequest;
import org.olat.presentation.framework.core.control.creator.ControllerCreator;

/**
 * Description:<br>
 * This callback provides methods to create the layout controller and a possibility to execute code after the cloning took place and the cloned content opened in a new
 * window.
 * <P>
 * Initial Date: 24.03.2009 <br>
 * 
 * @author gnaegi
 */
public interface DockLayoutControllerCreatorCallback {

    /**
     * This method is called when the content should be docked to create a controller creator
     * 
     * @param ureq
     * @param contentControllerCreator
     * @return
     */
    public ControllerCreator createLayoutControllerCreator(UserRequest ureq, ControllerCreator contentControllerCreator);
}
