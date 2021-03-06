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
 * Copyright (c) since 2004 at Multimedia- & E-Learning Services (MELS),<br>
 * University of Zurich, Switzerland.
 * <p>
 */

package org.olat.lms.ims.qti.process.elements;

import org.dom4j.Element;
import org.olat.lms.ims.qti.container.ItemContext;
import org.olat.lms.ims.qti.process.QTIHelper;

public class QTI_item {

    /**
	 * 
	 */
    public QTI_item() {
        //
    }

    /**
     * @param uic
     */
    public void evalAnswer(final ItemContext uic) {
        final QTI_resprocessing resprocessing = QTIHelper.getQTI_resprocessing();
        final Element item = uic.getEl_item();
        final Element curQtiElement = (Element) item.selectSingleNode("resprocessing");
        if (curQtiElement != null) {
            resprocessing.evalAnswer(curQtiElement, uic);
        }
    }

}
