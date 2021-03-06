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

package org.olat.presentation.framework.core.control.winmgr;

import org.olat.presentation.framework.core.WindowManager;

/**
 * Description:<br>
 * Initial Date: 23.03.2006 <br>
 * 
 * @author Felix Jost
 */
public class AJAXFlags {

    public static final int MODE_NORMAL = 0; // bit 0 not set
    public static final int MODE_TOBGIFRAME = 1; // bit 0 set

    private final WindowManager impl;

    /**
     * @param impl
     */
    public AJAXFlags(WindowManager impl) {
        this.impl = impl;
    }

    /**
     * true, if links should be targeted into the background iframe. and the result should be a json command structure to e.g. replace dom, call a user-js function
     * 
     * @return
     */
    public boolean isIframePostEnabled() {
        return impl.isAjaxEnabled();
    }

    /**
     * @return
     */
    /*
     * public boolean isDragAndDropEnabled() { // drag and drop support makes sense only when dom replacement works also: dom-repl(=iframe-post) -> dnd; // so we are not
     * considering non-dom browsers which could dnd, which seems fine if looking at the current browser's compabilities return impl.isAjaxEnabled(); }
     */

}
