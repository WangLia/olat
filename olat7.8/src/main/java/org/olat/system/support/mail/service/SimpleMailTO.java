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
package org.olat.system.support.mail.service;

/**
 * Initial Date: 19.12.2011 <br>
 * 
 * @author guretzki
 */
public class SimpleMailTO extends CommonMailTO {
    String bodyText;

    private SimpleMailTO(String toMailAddress, String fromMailAddress, String subject, String bodyText) {
        super(toMailAddress, fromMailAddress, subject);
        this.bodyText = bodyText;
    }

    public static SimpleMailTO getValidInstance(String toMailAddress, String fromMailAddress, String subject, String bodyText) {
        SimpleMailTO mail = new SimpleMailTO(toMailAddress, fromMailAddress, subject, bodyText);
        mail.validate();
        return mail;
    }

    public void validate() {
        super.validate();
        if (bodyText == null || bodyText.isEmpty()) {
            throw new IllegalArgumentException("Mail body text is not set.");
        }
    }

    public String getBodyText() {
        return bodyText;
    }

}