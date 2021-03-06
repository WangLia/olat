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
import org.olat.lms.ims.qti.container.ItemInput;

/**
 *
 */
public class QTI_varequal implements BooleanEvaluable {

    /**
     * var equals qti ims 1.2.1 <!ELEMENT varequal (#PCDATA)> <!ATTLIST varequal %I_Case; %I_RespIdent; %I_Index; > mit I_Case = case (Yes | No ) 'No' e.g. <varequal
     * respident = "LID01">A</varequal>
     */
    @Override
    public boolean eval(final Element boolElement, final ItemContext userContext, final EvalContext ect) {
        final ItemInput iinp = userContext.getItemInput();
        if (iinp.isEmpty()) {
            return false; // user has given no answer
        }
        final String respident = boolElement.attributeValue("respident");
        final String yescase = boolElement.attributeValue("case");
        final boolean caseimp = (yescase == null) ? true : yescase.equals("Yes"); // make it compatible with faulty QTI documentation
        final String shouldVal = boolElement.getText(); // the answer is tested against content of elem.
        final boolean ok = (caseimp ? iinp.contains(respident, shouldVal) : iinp.containsIgnoreCase(respident, shouldVal));
        return ok;
    }

}
