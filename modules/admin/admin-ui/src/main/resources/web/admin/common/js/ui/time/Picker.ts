import {DivEl} from "../../dom/DivEl";
import {TextInput} from "../text/TextInput";
import {Button} from "../button/Button";
import {Element} from "../../dom/Element";

export class Picker extends DivEl {

        protected popup: any;

        protected input: TextInput;

        protected popupTrigger: Button;

        protected validUserInput: boolean;

        constructor(builder: any, className?: string) {
            super(className);
            this.validUserInput = true;

            this.handleShownEvent();

            this.initData(builder);

            this.initPopup(builder);

            this.initInput(builder);

            this.initPopupTrigger();

            this.wrapChildrenAndAppend();

            this.setupListeners(builder);

            this.setupCommonListeners();
        }

        private setupCommonListeners() {
            this.popup.onShown(e => this.addClass("expanded"));
            this.popup.onHidden(e => this.removeClass("expanded"));
        }

        protected handleShownEvent() {
        }

        protected initData(builder: any) {
        }

        protected initPopup(builder: any) {
            throw new Error("must be implemented by inheritor");
        }

        protected initInput(builder: any) {
            throw new Error("must be implemented by inheritor");
        }

        protected initPopupTrigger() {
            throw new Error("must be implemented by inheritor");
        }

        protected wrapChildrenAndAppend() {
            var wrapper = new DivEl('wrapper');
            wrapper.appendChildren<Element>(this.input, this.popup, this.popupTrigger);

            this.appendChild(wrapper);
        }

        protected setupListeners(builder: any) {
            throw new Error("must be implemented by inheritor");
        }

        protected togglePopupVisibility() {
            this.popup.setVisible(!this.popup.isVisible());
        }

        isDirty(): boolean {
            return this.input.isDirty();
        }

        isValid(): boolean {
            return this.validUserInput;
        }

        updateInputStyling() {
            this.input.updateValidationStatusOnUserInput(this.validUserInput);
        }

        giveFocus(): boolean {
            return this.input.giveFocus();
        }
    }
