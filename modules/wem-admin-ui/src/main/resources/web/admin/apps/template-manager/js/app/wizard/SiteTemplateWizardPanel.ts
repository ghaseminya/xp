module app.wizard {

    import WizardStep = api.app.wizard.WizardStep;
    import WizardStepForm = api.app.wizard.WizardStepForm;

    export class SiteTemplateWizardPanel extends api.app.wizard.WizardPanel<api.content.site.template.SiteTemplate> {

        private static DEFAULT_SITE_TEMPLATE_ICON_URL: string = api.util.getAdminUri("common/images/icons/icoMoon/128x128/earth.png");
        private formIcon: api.app.wizard.FormIcon;
        private wizardHeader: api.app.wizard.WizardHeaderWithDisplayNameAndName;
        private iconUploadId: string;
        private siteTemplateWizardActions : app.wizard.action.SiteTemplateWizardActions;

        private siteTemplateStep: SiteTemplateWizardStepForm;

        constructor(tabId: api.app.bar.AppBarTabId, siteTemplate?: api.content.site.template.SiteTemplate) {
            this.wizardHeader = new api.app.wizard.WizardHeaderWithDisplayNameAndNameBuilder().build();
            if (siteTemplate) {
                this.wizardHeader.initNames(siteTemplate.getDisplayName(), siteTemplate.getName(), true);
            }
            this.siteTemplateWizardActions = new app.wizard.action.SiteTemplateWizardActions(this);

            var iconUrl = SiteTemplateWizardPanel.DEFAULT_SITE_TEMPLATE_ICON_URL;
            this.formIcon = new api.app.wizard.FormIcon(iconUrl, "Click to upload icon",
                api.util.getRestUri("upload"));

            this.formIcon.onUploadFinished((event: api.app.wizard.UploadFinishedEvent) => {

                this.iconUploadId = event.getUploadItem().getName();
                this.formIcon.setSrc(api.util.getRestUri('upload/' + event.getUploadItem().getName() + '?' +
                                                         event.getUploadItem().getMimeType()));
            });

            var mainToolbar = new SiteTemplateWizardToolbar({
                saveAction: this.siteTemplateWizardActions.getSaveAction(),
                duplicateAction: this.siteTemplateWizardActions.getDuplicateAction(),
                moveAction: this.siteTemplateWizardActions.getMoveAction(),
                deleteAction: this.siteTemplateWizardActions.getDeleteAction(),
                closeAction: this.siteTemplateWizardActions.getCloseAction()
            });

            super({
                tabId: tabId,
                persistedItem: siteTemplate,
                formIcon: this.formIcon,
                mainToolbar: mainToolbar,
                header: this.wizardHeader,
                actions: this.siteTemplateWizardActions,
                steps: this.createSteps()
            }, () => {
            });
        }

        createSteps(): WizardStep[] {
            var steps: WizardStep[] = [];
            this.siteTemplateStep = new SiteTemplateWizardStepForm();
            steps.push(new WizardStep("Site Template", this.siteTemplateStep));
            steps.push(new WizardStep("Content", new WizardStepForm()));
            steps.push(new WizardStep("Components", new WizardStepForm()));
            steps.push(new WizardStep("Summary", new WizardStepForm()));
            return steps;
        }

        layoutNew(): wemQ.Promise<void> {

            return super.layoutNew().
                then((): void => {

                    this.siteTemplateStep.renderNew();

                });
        }

        layoutPersistedItem(siteTemplate: api.content.site.template.SiteTemplate): wemQ.Promise<void> {
            var deferred = wemQ.defer<void>();

            this.siteTemplateStep.renderExisting(siteTemplate);
            deferred.resolve(null);

            return deferred.promise;
        }

        persistNewItem(): wemQ.Promise<api.content.site.template.SiteTemplate> {
            var deferred = wemQ.defer<api.content.site.template.SiteTemplate>();
            var validationResult: api.ui.form.ValidationResult = this.siteTemplateStep.validate(true);
            if (validationResult.isValid()) {
                var data = this.siteTemplateStep.getFormData();

                //TODO

            }
            return deferred.promise
        }

        updatePersistedItem(): wemQ.Promise<api.content.site.template.SiteTemplate> {
            var deferred = wemQ.defer<api.content.site.template.SiteTemplate>();
            var validationResult: api.ui.form.ValidationResult = this.siteTemplateStep.validate(true);
            if (validationResult.isValid()) {
                var data = this.siteTemplateStep.getFormData();

                //TODO

            }
            return deferred.promise
        }

        getCloseAction() : api.ui.Action {
            return this.siteTemplateWizardActions.getCloseAction();
        }
    }
}