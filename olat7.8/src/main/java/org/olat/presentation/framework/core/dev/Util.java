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
 * Copyright (c) 1999-2006 at Multimedia- & E-Learning Services (MELS),<br>
 * University of Zurich, Switzerland.
 * <p>
 */
package org.olat.presentation.framework.core.dev;

import java.util.List;

import org.olat.presentation.framework.core.components.Component;
import org.olat.presentation.framework.core.control.Controller;

/**
 * Description:<br>
 * Util class
 * <P>
 * Initial Date: 16.03.2007 <br>
 * 
 * @author Felix Jost, http://www.goodsolutions.ch
 */
public class Util {
    /**
     * returns the listener of a component. by legacy this could be more than one, but in practice it is never more than one, and it is also discouraged to have more than
     * one listener, since then there would be no clear responsibility for a component.
     * 
     * @param comp
     * @return
     */
    public static Controller getListeningControllerFor(Component comp) {
        Controller c = null;
        List<Controller> controllers = comp.debuginfoGetListeners();
        if (controllers.size() > 0) {
            // legacy, in future a component may only have one listener (in practice no component ever uses more than one listener; one listener = assigned
            // responsability)
            c = controllers.get(0);
        }
        return c;
    }
}
