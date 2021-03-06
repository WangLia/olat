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
package org.olat.presentation.course.wizard.close;

import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.VelocityContext;
import org.olat.data.basesecurity.Identity;
import org.olat.data.repository.RepositoryEntry;
import org.olat.lms.catalog.CatalogService;
import org.olat.lms.commons.mail.MailTemplateHelper;
import org.olat.lms.course.CourseGroupsEBL;
import org.olat.lms.repository.RepositoryEBL;
import org.olat.lms.user.UserService;
import org.olat.presentation.commons.mail.MailNotificationEditController;
import org.olat.presentation.framework.core.UserRequest;
import org.olat.presentation.framework.core.components.Component;
import org.olat.presentation.framework.core.components.form.Form;
import org.olat.presentation.framework.core.components.form.flexible.FormItemContainer;
import org.olat.presentation.framework.core.components.form.flexible.elements.MultipleSelectionElement;
import org.olat.presentation.framework.core.components.form.flexible.impl.FormBasicController;
import org.olat.presentation.framework.core.components.form.flexible.impl.FormEvent;
import org.olat.presentation.framework.core.components.form.flexible.impl.FormLayoutContainer;
import org.olat.presentation.framework.core.components.form.flexible.impl.elements.FormReset;
import org.olat.presentation.framework.core.components.form.flexible.impl.elements.FormSubmit;
import org.olat.presentation.framework.core.components.link.Link;
import org.olat.presentation.framework.core.components.link.LinkFactory;
import org.olat.presentation.framework.core.components.panel.Panel;
import org.olat.presentation.framework.core.components.velocity.VelocityContainer;
import org.olat.presentation.framework.core.control.Controller;
import org.olat.presentation.framework.core.control.WindowControl;
import org.olat.presentation.framework.core.control.generic.wizard.WizardController;
import org.olat.presentation.framework.core.translator.PackageUtil;
import org.olat.presentation.framework.core.translator.Translator;
import org.olat.presentation.repository.WizardCloseResourceController;
import org.olat.system.event.Event;
import org.olat.system.mail.MailTemplate;
import org.olat.system.mail.MailerResult;
import org.olat.system.mail.MailerWithTemplate;
import org.olat.system.security.OLATPrincipal;
import org.olat.system.spring.CoreSpringFactory;

/**
 * Description:<br>
 * A wizard to close a course.
 * <P>
 * Initial Date: 28.10.2008 <br>
 * 
 * @author bja <bja@bps-system.de>
 */
public class WizardCloseCourseController extends WizardController implements WizardCloseResourceController {

    private static final int NUM_STEPS = 3; // 3 steps
    public static final String COURSE_CLOSED = "course.closed";
    private RepositoryEntry repositoryEntry;
    private final VelocityContainer mainVc;
    private final Panel panel;
    private Link nextStep1;
    private MailNotificationEditController mailNotificationCtr;
    private VelocityContainer step1Vc;
    private VelocityContainer sendNotificationVC;
    private CloseRessourceOptionForm formStep2;
    private final CatalogService catalogService;
    private RepositoryEBL repositoryEbl;
    private UserService userService;
    private CourseGroupsEBL courseGroupsEBL;

    public WizardCloseCourseController(final UserRequest ureq, final WindowControl control, final RepositoryEntry repositoryEntry) {
        super(ureq, control, NUM_STEPS);

        this.setBasePackage(WizardCloseCourseController.class);
        this.setTranslator(PackageUtil.createPackageTranslator(WizardCloseResourceController.class, ureq.getLocale(), getTranslator()));
        this.repositoryEntry = repositoryEntry;
        repositoryEbl = CoreSpringFactory.getBean(RepositoryEBL.class);
        courseGroupsEBL = CoreSpringFactory.getBean(CourseGroupsEBL.class);
        catalogService = CoreSpringFactory.getBean(CatalogService.class);
        userService = CoreSpringFactory.getBean(UserService.class);
        this.mainVc = createVelocityContainer("wizard");
        this.panel = new Panel("panel");
    }

    @Override
    public void startWorkflow() {
        buildStep1();
        mainVc.put("panel", panel);

        this.setWizardTitle(translate("wizard.closecourse.title"));
        this.setNextWizardStep(translate("close.ressource.step1"), mainVc);
    }

    /**
     * @param ureq
     */
    private void buildStep1() {
        step1Vc = createVelocityContainer("step1_wizard_close_resource");
        nextStep1 = LinkFactory.createButtonSmall("next", step1Vc, this);
        panel.setContent(step1Vc);
    }

    /**
     * @param ureq
     */
    private void buildStep2(final UserRequest ureq) {
        formStep2 = new CloseRessourceOptionForm(ureq, getWindowControl());
        listenTo(formStep2);
        panel.setContent(formStep2.getInitialComponent());
    }

    /**
     * @param ureq
     */
    private void buildStep3(final UserRequest ureq) {
        final String courseTitle = "'" + repositoryEntry.getDisplayname() + "'";
        final MailTemplate mailTempl = createMailTemplate(translate("wizard.step3.mail.subject", new String[] { courseTitle }),
                translate("wizard.step3.mail.body", new String[] { courseTitle, userService.getFirstAndLastname(ureq.getIdentity().getUser()) }), ureq.getIdentity());
        if (mailNotificationCtr != null) {
            mailNotificationCtr.dispose();
        }
        mailNotificationCtr = new MailNotificationEditController(getWindowControl(), ureq, mailTempl, false);
        mailNotificationCtr.addControllerListener(this);
        sendNotificationVC = createVelocityContainer("sendnotification");
        sendNotificationVC.put("notificationForm", mailNotificationCtr.getInitialComponent());
        panel.setContent(sendNotificationVC);
    }

    /**
     * Create default template which fill in context 'firstname' , 'lastname' and 'username'.
     * 
     * @param subject
     * @param body
     * @return
     */
    private MailTemplate createMailTemplate(final String subject, final String body, Identity sender) {
        return new MailTemplate(subject, body, MailTemplateHelper.getMailFooter(null, sender), null) {
            @Override
            public void putVariablesInMailContext(final VelocityContext context, final OLATPrincipal identity) {
                // nothing to do
            }
        };
    }

    @Override
    protected void event(final UserRequest ureq, final Component source, final Event event) {
        super.event(ureq, source, event);
        // forward to step 2
        if (source == nextStep1) {
            buildStep2(ureq);
            this.setNextWizardStep(translate("close.ressource.step2"), mainVc);
        }
    }

    @Override
    protected void event(final UserRequest ureq, final Controller source, final Event event) {
        if (source == mailNotificationCtr && event == Event.DONE_EVENT) {
            if (mailNotificationCtr.getMailTemplate() != null) {
                List<Identity> ownerList = repositoryEbl.getOwnersWhenInOwnerGroup(repositoryEntry, ureq.getIdentity());
                List<Identity> ccIdentities = getCcIdentities(ureq.getIdentity(), mailNotificationCtr.getMailTemplate());
                final MailerResult mailerResult = MailerWithTemplate.getInstance().sendMailAsSeparateMails(ownerList, ccIdentities, null,
                        mailNotificationCtr.getMailTemplate(), ureq.getIdentity());
                final StringBuilder errorMessage = new StringBuilder();
                final StringBuilder warningMessage = new StringBuilder();
                MailTemplateHelper.appendErrorsAndWarnings(mailerResult, errorMessage, warningMessage, ureq.getLocale());
                if (warningMessage.length() > 0) {
                    getWindowControl().setWarning(warningMessage.toString());
                }
                if (errorMessage.length() > 0) {
                    getWindowControl().setError(errorMessage.toString());
                }
                ownerList.clear();
            }
            repositoryEbl.setStatusClosed(repositoryEntry);
            if ((formStep2 != null) && (formStep2.isCheckboxCleanCatalog())) {
                doCleanCatalog();
            }
            if ((formStep2 != null) && (formStep2.isCheckboxCleanGroups())) {
                courseGroupsEBL.removeAllMemberFromCourseGroups(ureq.getIdentity(), repositoryEntry);
            }
            if (mailNotificationCtr != null) {
                disposeAndSetNull(mailNotificationCtr);
            }
            fireEvent(ureq, Event.DONE_EVENT);
        } else if (source == mailNotificationCtr && event == Event.CANCELLED_EVENT) {
            disposeAndSetNull(mailNotificationCtr);
            fireEvent(ureq, Event.CANCELLED_EVENT);
        }
        // forward to step 3
        if (source == formStep2) {
            if (event.equals(Event.DONE_EVENT)) {
                buildStep3(ureq);
                this.setNextWizardStep(translate("close.ressource.step3"), mainVc);
            } else if (event.equals(Event.BACK_EVENT)) {
                buildStep1();
                this.setBackWizardStep(translate("close.ressource.step1"), mainVc);
            }

        }
    }

    /**
     * @param mailTemplate
     * @param ureq
     * @return
     */
    private List<Identity> getCcIdentities(final Identity requestIdentity, MailTemplate mailTemplate) {
        List<Identity> ccIdentities = new ArrayList<Identity>();
        if (mailTemplate.getCpfrom()) {
            ccIdentities.add(requestIdentity);
        } else {
            ccIdentities = null;
        }
        return ccIdentities;
    }

    /**
     * 
     */
    private void disposeAndSetNull(Controller controller) {
        controller.dispose();
        controller = null;
    }

    /**
     * clean all references from catalog
     */
    private void doCleanCatalog() {
        catalogService.resourceableDeleted(repositoryEntry);
    }

}

class CloseRessourceOptionForm extends FormBasicController {

    private FormSubmit submit;
    private MultipleSelectionElement checkboxClean;
    private FormReset back;
    private final Translator translator;

    public CloseRessourceOptionForm(final UserRequest ureq, final WindowControl control) {
        super(ureq, control);
        this.translator = PackageUtil.createPackageTranslator(this.getClass(), ureq.getLocale());
        initForm(ureq);
    }

    public boolean isCheckboxCleanCatalog() {
        return checkboxClean.isSelected(0);
    }

    public boolean isCheckboxCleanGroups() {
        return checkboxClean.isSelected(1);
    }

    @Override
    protected void doDispose() {
        // nothing to do
    }

    @Override
    @SuppressWarnings("unused")
    protected void formOK(final UserRequest ureq) {
        // nothing to do
    }

    @Override
    @SuppressWarnings("unused")
    protected void event(final UserRequest ureq, final Component source, final Event event) {
        if (event.getCommand().equals(Form.EVNT_VALIDATION_OK.getCommand())) {
            fireEvent(ureq, Event.DONE_EVENT);
        } else if (event.getCommand().equals(FormEvent.RESET.getCommand())) {
            fireEvent(ureq, Event.BACK_EVENT);
        }
    }

    @Override
    @SuppressWarnings("unused")
    protected void initForm(final FormItemContainer formLayout, final Controller listener, final UserRequest ureq) {

        final String[] keys = new String[] { "form.clean.catalog", "form.clean.groups" };
        final String[] values = new String[] { translator.translate("form.clean.catalog"), translator.translate("form.clean.groups") };
        checkboxClean = uifactory.addCheckboxesVertical("form.clean.catalog", null, formLayout, keys, values, null, 1);

        submit = new FormSubmit("next", "next");
        back = new FormReset("back", "back");
        final FormLayoutContainer horizontalL = FormLayoutContainer.createHorizontalFormLayout("horiz", getTranslator());
        formLayout.add(horizontalL);
        horizontalL.add(back);
        horizontalL.add(submit);

    }

}
