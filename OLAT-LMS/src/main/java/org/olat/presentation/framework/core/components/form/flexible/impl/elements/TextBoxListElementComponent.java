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
 * Copyright (c) frentix GmbH<br>
 * http://www.frentix.com<br>
 * <p>
 */
package org.olat.presentation.framework.core.components.form.flexible.impl.elements;

import java.util.Map;

import org.olat.presentation.framework.core.components.ComponentRenderer;
import org.olat.presentation.framework.core.components.textboxlist.TextBoxListComponent;
import org.olat.presentation.framework.core.components.textboxlist.TextBoxListRenderer;
import org.olat.presentation.framework.core.translator.Translator;

/**
 * Description:<br>
 * TODO: rhaag Class Description for TextBoxListElementComponent
 * <P>
 * Initial Date: 27.08.2010 <br>
 * 
 * @author Roman Haag, roman.haag@frentix.com, http://www.frentix.com
 */
public class TextBoxListElementComponent extends TextBoxListComponent {

    private ComponentRenderer RENDERER = new TextBoxListRenderer(true);
    private TextBoxListElementImpl element;

    public TextBoxListElementComponent(TextBoxListElementImpl element, String name, String inputHint, Map<String, String> initialItems, Translator translator) {
        super(name, inputHint, initialItems, translator);
        this.element = element;
    }

    public TextBoxListElementImpl getTextElementImpl() {
        return element;
    }

    /**
	 */
    @Override
    public ComponentRenderer getHTMLRendererSingleton() {
        return RENDERER;
    }

}