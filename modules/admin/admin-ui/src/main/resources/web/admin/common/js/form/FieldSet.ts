import {FieldSetJson} from "./json/FieldSetJson";
import {FormItemJson} from "./json/FormItemJson";
import {FormItemTypeWrapperJson} from "./json/FormItemTypeWrapperJson";
import {Equitable} from "../Equitable";
import {ObjectHelper} from "../ObjectHelper";
import {FormItem} from "./FormItem";
import {FormItemContainer} from "./FormItemContainer";
import {FormItemFactory} from "./FormItemFactory";
import {Layout} from "./Layout";

export class FieldSet extends Layout implements FormItemContainer {

        private label: string;

        private formItems: FormItem[] = [];

        constructor(fieldSetJson: FieldSetJson) {
            super(fieldSetJson.name);
            this.label = fieldSetJson.label;

            if (fieldSetJson.items != null) {
                fieldSetJson.items.forEach((formItemJson: FormItemJson) => {
                    var formItem = FormItemFactory.createFormItem(formItemJson);
                    if (formItem) {
                        this.addFormItem(formItem);
                    }
                });
            }
        }

        addFormItem(formItem: FormItem) {
            this.formItems.push(formItem);
        }

        getLabel(): string {
            return this.label;
        }

        getFormItems(): FormItem[] {
            return this.formItems;
        }

        public toFieldSetJson(): FormItemTypeWrapperJson {

            return <FormItemTypeWrapperJson>{ FieldSet: <FieldSetJson>{
                name: this.getName(),
                items: FormItem.formItemsToJson(this.getFormItems()),
                label: this.getLabel()
            }};
        }

        equals(o: Equitable): boolean {

            if (!ObjectHelper.iFrameSafeInstanceOf(o, FieldSet)) {
                return false;
            }

            if (!super.equals(o)) {
                return false;
            }

            var other = <FieldSet>o;

            if (!ObjectHelper.stringEquals(this.label, other.label)) {
                return false;
            }

            if (!ObjectHelper.arrayEquals(this.formItems, other.formItems)) {
                return false;
            }

            return true;
        }
    }
