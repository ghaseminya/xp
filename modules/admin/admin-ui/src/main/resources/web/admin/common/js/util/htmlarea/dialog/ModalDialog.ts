module api.util.htmlarea.dialog {

    import Form = api.ui.form.Form;
    import Fieldset = api.ui.form.Fieldset;
    import FormItem = api.ui.form.FormItem;
    import FormItemBuilder = api.ui.form.FormItemBuilder;

    export class ModalDialog extends api.ui.dialog.ModalDialog {
        private fields: { [id: string]: api.dom.FormItemEl } = {};
        private validated: boolean = false;
        private editor: HtmlAreaEditor;
        private mainForm: Form;
        private firstFocusField: api.dom.Element;
        private submitAction: api.ui.Action;

        protected static VALIDATION_CLASS: string = 'display-validation-errors';

        public static CLASS_NAME: string = 'html-area-modal-dialog';

        constructor(editor: HtmlAreaEditor, title: string, cls?: string) {

            super(title);

            this.editor = editor;

            this.getEl().addClass(ModalDialog.CLASS_NAME + (cls ? ' ' + cls : ''));

            this.layout();
            this.initializeActions();
            this.initializeListeners();
        }

        setSubmitAction(action: api.ui.Action) {
            this.submitAction = action;
        }

        protected getEditor(): HtmlAreaEditor {
            return this.editor;
        }

        protected setValidated() {
            this.validated = true;
        }

        protected setFirstFocusField(field: api.dom.Element) {
            this.firstFocusField = field;
        }

        private focusFirstField() {
            this.firstFocusField.giveFocus();
        }

        protected layout() {
            this.appendChildToContentPanel(<api.dom.Element>this.createMainForm());
        }

        protected getMainFormItems(): FormItem[] {
            return [];
        }

        protected getMainForm(): Form {
            return this.mainForm;
        }

        protected createMainForm(): Form {
            return this.mainForm = this.createForm(this.getMainFormItems());
        }

        protected validate(): boolean {
            this.setValidated();

            return this.mainForm.validate(true).isValid();
        }

        protected hasSubDialog(): boolean {
            // html area dialogs can't have sub dialogs
            return false;
        }

        show() {
            api.dom.Body.get().appendChild(this);
            super.show();
            if (this.firstFocusField) {
                this.focusFirstField();
            }
        }

        protected createForm(formItems: FormItem[]): Form {
            let form = new Form();
            let validationCls = api.form.FormView.VALIDATION_CLASS;

            formItems.forEach((formItem: FormItem) => {
                form.add(this.createFieldSet(formItem));
                if (formItem.getValidator() && validationCls) {
                    form.addClass(validationCls);
                    validationCls = '';
                }
            });

            return form;
        }

        protected displayValidationErrors(value: boolean) {
            if (value) {
                this.mainForm.addClass(api.form.FormView.VALIDATION_CLASS);
            } else {
                this.mainForm.removeClass(api.form.FormView.VALIDATION_CLASS);
            }
        }

        protected createFormPanel(formItems: FormItem[]): api.ui.panel.Panel {
            let panel = new api.ui.panel.Panel();
            let form = this.createForm(formItems);

            panel.appendChild(form);

            return panel;
        }

        public createFieldSet(formItem: FormItem): Fieldset {
            let fieldSet = new Fieldset();

            fieldSet.addClass('modal-dialog-fieldset');
            fieldSet.add(formItem);

            if (formItem.getValidator()) {
                let validationRecordingViewer = new api.form.ValidationRecordingViewer();

                fieldSet.appendChild(validationRecordingViewer);
                fieldSet.onValidityChanged((event: ValidityChangedEvent) => {
                    validationRecordingViewer.setError(formItem.getError());
                });
            }

            return fieldSet;
        }

        onValidatedFieldValueChanged(formItem: FormItem) {
            if (this.validated) {
                formItem.validate(new api.ui.form.ValidationResult(), true);
            }
        }

        protected createFormItem(id: string, label: string, validator?: (input: api.dom.FormInputEl) => string, value?: string,
                                 inputEl?: api.dom.FormItemEl): FormItem {
            let formItemEl = inputEl || new api.ui.text.TextInput();
            let formItemBuilder = new FormItemBuilder(formItemEl).setLabel(label);
            let inputWrapper = new api.dom.DivEl('input-wrapper');
            let formItem;

            if (this.fields[id]) {
                throw 'Element with id ' + id + ' already exists';
            }

            if (value) {
                (<api.dom.InputEl>formItemEl).setValue(value);
            }

            this.fields[id] = formItemEl;

            if (validator) {
                formItemBuilder.setValidator(validator);
            }

            formItem = formItemBuilder.build();

            formItem.getInput().wrapWithElement(inputWrapper);

            if (validator) {
                if (api.ObjectHelper.iFrameSafeInstanceOf(formItemEl, api.ui.text.TextInput)) {
                    (<api.ui.text.TextInput>formItemEl).onValueChanged(this.onValidatedFieldValueChanged.bind(this, formItem));
                }
                if (api.ObjectHelper.iFrameSafeInstanceOf(formItemEl, api.ui.selector.combobox.RichComboBox)) {
                    (<api.ui.selector.combobox.RichComboBox<any>>formItemEl).onOptionSelected(this.onValidatedFieldValueChanged.bind(this,
                        formItem));
                    (<api.ui.selector.combobox.RichComboBox<any>>formItemEl).onOptionDeselected(this.onValidatedFieldValueChanged.bind(this,
                        formItem));
                }
            }

            return formItem;
        }

        protected initializeActions() {
            this.addCancelButtonToBottom();
        }

        protected getFieldById(id: string): api.dom.FormItemEl {
            return this.fields[id];
        }

        close() {
            super.close();
            if (!this.editor['destroyed']) {
                this.editor.focus();
            }
            this.remove();
        }

        private initializeListeners() {
            if(this.submitAction) {
                this.listenEnterKey();
            }
        }

        private listenEnterKey() {
            this.onKeyDown((event: KeyboardEvent) => {
                if(event.which === 13) { // enter
                    if(this.isTextInput(<Element>event.target)) {
                        setTimeout(() => { // TinyMCE in FF behaves bad without timeout
                            this.submitAction.execute();
                        }, 50);

                    }
                }
            });
        }

        private isTextInput(element: Element): boolean {
            return element.tagName.toUpperCase() === 'INPUT' && element.id.indexOf('TextInput') > 0;
        }
    }

    export interface HtmlAreaAnchor {
        editor: HtmlAreaEditor;
        element: HTMLElement;
        text: string;
        anchorList: string[];
        onlyTextSelected: boolean;
    }

    export interface HtmlAreaImage {
        editor: HtmlAreaEditor;
        element: HTMLElement;
        container: HTMLElement;
        callback: Function;
    }

    export interface HtmlAreaMacro {
        editor: HtmlAreaEditor;
        callback: Function;
    }
}
