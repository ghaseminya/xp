module app_wizard {

    export class ContentTypeWizardPanel extends api_app_wizard.WizardPanel<api_schema_content.ContentType> {

        public static NEW_WIZARD_HEADER = "New Content Type";

        private static  DEFAULT_CHEMA_ICON_URL:string = api_util.getRestUri('schema/image/ContentType:structured');

        private formIcon :api_app_wizard.FormIcon;

        private contentTypeWizardHeader :api_app_wizard.WizardHeaderWithName;

        private persistedContentType :api_schema_content.ContentType;

        private contentTypeForm :app_wizard.ContentTypeForm;

        constructor() {
            this.contentTypeWizardHeader = new api_app_wizard.WizardHeaderWithName();
            this.formIcon = new api_app_wizard.FormIcon(ContentTypeWizardPanel.DEFAULT_CHEMA_ICON_URL, "Click to upload icon",
                api_util.getRestUri("upload"));

            var actions = new ContentTypeWizardActions(this);

            var toolbar = new ContentTypeWizardToolbar({
                saveAction: actions.getSaveAction(),
                duplicateAction: actions.getDuplicateAction(),
                deleteAction: actions.getDeleteAction(),
                closeAction: actions.getCloseAction()
            });

            super({
                formIcon: this.formIcon,
                toolbar: toolbar,
                actions: actions,
                header: this.contentTypeWizardHeader
            });

            this.contentTypeWizardHeader.setName(ContentTypeWizardPanel.NEW_WIZARD_HEADER);

            this.contentTypeForm = new ContentTypeForm();

            this.addStep(new api_app_wizard.WizardStep("Content Type"), this.contentTypeForm);
        }

        setPersistedItem(contentType:api_schema_content.ContentType) {
            super.setPersistedItem(contentType);

            this.contentTypeWizardHeader.setName(contentType.getName());
            this.formIcon.setSrc(contentType.getIcon());

            this.persistedContentType = contentType;

            new api_schema_content.GetContentTypeConfigByQualifiedNameRequest(contentType.getName()).send().
                done((response:api_rest.JsonResponse <api_schema_content.GetContentTypeConfigResult>) => {
                this.contentTypeForm.setFormData({"xml": response.getResult().contentTypeXml});
            });
        }

        persistNewItem(successCallback ? : () => void) {
            var formData = this.contentTypeForm.getFormData();
            var createContentTypeRequest = new api_schema_content.CreateContentTypeRequest(this.contentTypeWizardHeader.getName(), formData.xml,
                this.getIconUrl());
            createContentTypeRequest.send().done((response:api_rest.JsonResponse) => {
                new app_wizard.ContentTypeCreatedEvent().fire();
                api_notify.showFeedback('Content type was created!');

                new api_schema.SchemaCreatedEvent( api_schema.SchemaKind.CONTENT_TYPE, "TODO: get name" ).fire();

                if (successCallback) {
                    successCallback.call(this);
                }
            });
        }

        updatePersistedItem(successCallback ? : () => void) {
            var formData = this.contentTypeForm.getFormData();
            var updateContentTypeRequest = new api_schema_content.UpdateContentTypeRequest(this.contentTypeWizardHeader.getName(), formData.xml,
                this.getIconUrl());

            updateContentTypeRequest.send().done((response:api_rest.JsonResponse) => {
                new app_wizard.ContentTypeUpdatedEvent().fire();
                api_notify.showFeedback('Content type was saved!');

                new api_schema.SchemaUpdatedEvent( api_schema.SchemaKind.CONTENT_TYPE, "TODO: get name" ).fire();

                if (successCallback) {
                    successCallback.call(this);
                }
            });
        }
    }
}