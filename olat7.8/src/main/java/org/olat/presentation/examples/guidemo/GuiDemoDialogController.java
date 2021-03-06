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
package org.olat.presentation.examples.guidemo;

import java.util.ArrayList;

import org.olat.presentation.framework.core.UserRequest;
import org.olat.presentation.framework.core.components.Component;
import org.olat.presentation.framework.core.components.link.Link;
import org.olat.presentation.framework.core.components.link.LinkFactory;
import org.olat.presentation.framework.core.components.panel.Panel;
import org.olat.presentation.framework.core.components.velocity.VelocityContainer;
import org.olat.presentation.framework.core.control.Controller;
import org.olat.presentation.framework.core.control.WindowControl;
import org.olat.presentation.framework.core.control.controller.BasicController;
import org.olat.presentation.framework.core.control.generic.messages.MessageController;
import org.olat.presentation.framework.core.control.generic.messages.MessageUIFactory;
import org.olat.presentation.framework.core.control.generic.modal.DialogBoxController;
import org.olat.presentation.framework.core.control.generic.modal.DialogBoxUIFactory;
import org.olat.presentation.framework.core.dev.controller.SourceViewController;
import org.olat.system.commons.StringHelper;
import org.olat.system.event.Event;
import org.olat.system.exception.AssertException;

/**
 * <h3>Description:</h3> Demonstration of what you can do with dialogs <h3>Events thrown by this controller:</h3>
 * <ul>
 * <li>ButtonClickedEvent: when user clicks a button provided in the constructor</li>
 * <li>Event.CANCELLED_EVENT: when user clicks the close icon in the window bar</li>
 * </ul>
 * <p>
 * Initial Date: 26.11.2007<br>
 * 
 * @author Florian Gnaegi, frentix GmbH, http://www.frentix.com
 */
public class GuiDemoDialogController extends BasicController {

    VelocityContainer vcMain;
    private final Link yesNoButton, okCancelButton, genericDialogButton, noCloseButton, customCssButton;
    private DialogBoxController dialogBoxOne;
    private DialogBoxController dialogBoxTwo;
    private DialogBoxController dialogBoxThree;
    private DialogBoxController dialogBoxSpecialCSS;
    private DialogBoxController dialogBoxWithoutClose;
    private ArrayList<String> myButtons;
    private final Link guimsgButton;
    private final Panel mainP;
    private MessageController guimsg;

    public GuiDemoDialogController(final UserRequest ureq, final WindowControl wControl) {
        super(ureq, wControl);

        vcMain = this.createVelocityContainer("guidemo-dialog");
        yesNoButton = LinkFactory.createButton("guidemo.dialog.yesno", vcMain, this);
        okCancelButton = LinkFactory.createButton("guidemo.dialog.okcancel", vcMain, this);
        genericDialogButton = LinkFactory.createButton("guidemo.dialog.generic", vcMain, this);
        customCssButton = LinkFactory.createButton("guidemo.dialog.customcss", vcMain, this);
        noCloseButton = LinkFactory.createButton("guidemo.dialog.noclose", vcMain, this);
        guimsgButton = LinkFactory.createButton("guidemo.dialog.guimsg", vcMain, this);

        // add source view control
        final Controller sourceview = new SourceViewController(ureq, wControl, this.getClass(), vcMain);
        vcMain.put("sourceview", sourceview.getInitialComponent());

        mainP = putInitialPanel(vcMain);
    }

    @Override
    protected void event(final UserRequest ureq, final Component source, final Event event) {
        if (source == yesNoButton) {
            String untrustedText = "XSS attack: <script>alert('XSS attempt');</script>";
            String text = translate("GuiDemoDialogController.yesNoButton.text", StringHelper.escapeHtml(untrustedText));
            dialogBoxOne = activateYesNoDialog(ureq, "Hello World", text, dialogBoxOne);
        }
        if (source == okCancelButton) {
            dialogBoxTwo = activateOkCancelDialog(ureq, "Hello World",
                    "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aliquam id quam in dui pellentesque sodales?", dialogBoxTwo);
        }
        if (source == genericDialogButton) {
            // create list of internationalized button texsts
            myButtons = new ArrayList<String>();
            myButtons.add("Lorem");
            myButtons.add("Ipsum");
            myButtons.add("Dolor");
            dialogBoxThree = activateGenericDialog(ureq, "Hello World",
                    "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aliquam id quam in dui pellentesque sodales?", myButtons, dialogBoxThree);
        }
        if (source == customCssButton) {
            dialogBoxSpecialCSS = activateYesNoDialog(ureq, "Hello World",
                    "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aliquam id quam in dui pellentesque sodales?", dialogBoxSpecialCSS);
            // use custom CSS: in this case with a special icon
            dialogBoxSpecialCSS.setCssClass("b_warning_icon");
        }
        if (source == noCloseButton) {
            dialogBoxWithoutClose = activateYesNoDialog(ureq, "Hello World",
                    "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aliquam id quam in dui pellentesque sodales?", dialogBoxWithoutClose);
            dialogBoxWithoutClose.setCloseWindowEnabled(false);
        }
        if (source == guimsgButton) {
            guimsg = MessageUIFactory.createInfoMessage(ureq, getWindowControl(), "Helau", "asdifasdlkf sdlfasfd asdf.");
            listenTo(guimsg);
            mainP.pushContent(guimsg.getInitialComponent());
        }

    }

    @Override
    public void event(@SuppressWarnings("unused") final UserRequest ureq, final Controller source, final Event event) {
        String feedbackMessage = null;
        if (source == dialogBoxOne) {
            if (event == Event.CANCELLED_EVENT) {
                feedbackMessage = "close icon clicked";
            } else {
                if (DialogBoxUIFactory.isYesEvent(event)) {
                    feedbackMessage = "yes clicked";
                } else {
                    feedbackMessage = "no clicked";
                }
            }
        } else if (source == dialogBoxTwo) {
            if (event == Event.CANCELLED_EVENT) {
                feedbackMessage = "close icon clicked";
            } else {
                if (DialogBoxUIFactory.isOkEvent(event)) {
                    feedbackMessage = "ok clicked";
                } else {
                    feedbackMessage = "cancel clicked";
                }
            }
        } else if (source == dialogBoxThree) {
            if (event == Event.CANCELLED_EVENT) {
                feedbackMessage = "close icon clicked";
            } else {
                final int pos = DialogBoxUIFactory.getButtonPos(event);
                feedbackMessage = myButtons.get(pos) + " clicked on position:" + pos;
            }
        } else if (source == dialogBoxSpecialCSS) {
            if (event == Event.CANCELLED_EVENT) {
                feedbackMessage = "close icon clicked";
            } else {
                if (DialogBoxUIFactory.isYesEvent(event)) {
                    feedbackMessage = "yes clicked";
                } else {
                    feedbackMessage = "no clicked";
                }
            }
        } else if (source == dialogBoxWithoutClose) {
            if (event == Event.CANCELLED_EVENT) {
                throw new AssertException("close icon pressed, but this should not be available.");
            } else {
                if (DialogBoxUIFactory.isYesEvent(event)) {
                    feedbackMessage = "yes clicked";
                } else {
                    feedbackMessage = "no clicked";
                }
            }
        }
        if (feedbackMessage != null) {
            getWindowControl().setInfo(feedbackMessage + "command was:" + event.getCommand());
        } else {
            throw new AssertException("feedback message is NULL, workflow error!");
        }

    }

    @Override
    protected void doDispose() {
        // dialog controller automatically disposed by BasicController
    }

}
