module api.security.acl {

    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import BaseSelectedOptionView = api.ui.selector.combobox.BaseSelectedOptionView;

    export class AccessControlComboBox extends api.ui.selector.combobox.RichComboBox<AccessControlEntry> {

        private aceSelectedOptionsView: ACESelectedOptionsView;

        constructor() {
            this.aceSelectedOptionsView = new ACESelectedOptionsView();

            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<AccessControlEntry>().
                setMaximumOccurrences(0).
                setComboBoxName("principalSelector").
                setIdentifierMethod("getPrincipalKey").
                setLoader(new AccessControlEntryLoader()).
                setSelectedOptionsView(this.aceSelectedOptionsView).
                setOptionDisplayValueViewer(new AccessControlEntryViewer()).
                setDelayedInputValueChangedHandling(500);
            super(builder);
        }

        onOptionValueChanged(listener: (item: AccessControlEntry) => void) {
            this.aceSelectedOptionsView.onItemValueChanged(listener);
        }

        unItemValueChanged(listener: (item: AccessControlEntry) => void) {
            this.aceSelectedOptionsView.unItemValueChanged(listener);
        }
    }

    class ACESelectedOptionView extends AccessControlEntryView implements api.ui.selector.combobox.SelectedOptionView<AccessControlEntry> {

        private option: Option<AccessControlEntry>;

        constructor(option: Option<AccessControlEntry>) {
            var ace = option.displayValue;
            if (ace.getAllowedPermissions().length == 0 && ace.getDeniedPermissions().length == 0) {
                // allow read by default
                ace.allow(Permission.READ);
            }
            super(ace);
            this.option = option;
        }

        setOption(option: Option<AccessControlEntry>) {
            this.option = option;
            this.setAccessControlEntry(option.displayValue);
        }

        getOption(): Option<AccessControlEntry> {
            return this.option;
        }

        onSelectedOptionRemoveRequest(listener: {(): void}) {
            this.onRemoveClicked(listener);
        }

        unSelectedOptionRemoveRequest(listener: {(): void}) {
            this.unRemoveClicked(listener);
        }

    }

    class ACESelectedOptionsView extends AccessControlListView implements api.ui.selector.combobox.SelectedOptionsView<AccessControlEntry> {

        private maximumOccurrences: number;
        private list: SelectedOption<AccessControlEntry>[] = [];

        private selectedOptionRemovedListeners: {(removed: SelectedOption<AccessControlEntry>): void;}[] = [];

        constructor(className?: string) {
            super(className);
        }

        setMaximumOccurrences(value: number) {
            this.maximumOccurrences = value;
        }

        getMaximumOccurrences(): number {
            return this.maximumOccurrences;
        }

        createSelectedOption(option: Option<AccessControlEntry>): SelectedOption<AccessControlEntry> {
            throw new Error('Not supported, use createItemView instead');
        }

        createItemView(entry: AccessControlEntry): ACESelectedOptionView {

            var option = {
                displayValue: entry,
                value: this.getItemId(entry)
            };
            var itemView = new ACESelectedOptionView(option);
            itemView.onValueChanged((item: AccessControlEntry) => {
                // update our selected options list with new values
                var selectedOption = this.getById(item.getPrincipalKey().toString());
                if (selectedOption) {
                    selectedOption.getOption().displayValue = item;
                }
                this.notifyItemValueChanged(item);
            });
            var selectedOption = new SelectedOption<AccessControlEntry>(itemView, this.list.length);

            itemView.onSelectedOptionRemoveRequest(() => {
                this.removeOption(option, false);
            });

            // keep track of selected options for SelectedOptionsView
            this.list.push(selectedOption);
            return itemView;
        }


        addOption(option: Option<AccessControlEntry>): boolean {
            this.addItem(option.displayValue);
            return true;
        }

        removeOption(optionToRemove: Option<AccessControlEntry>, silent: boolean) {
            api.util.assertNotNull(optionToRemove, "optionToRemove cannot be null");

            var selectedOption = this.getByOption(optionToRemove);
            api.util.assertNotNull(selectedOption, "Did not find any selected option to remove from option: " + optionToRemove.value);

            this.removeItem(optionToRemove.displayValue);

            this.list = this.list.filter((option: SelectedOption<AccessControlEntry>) => {
                return option.getOption().value != selectedOption.getOption().value;
            });

            // update item indexes to the right of removed item
            if (selectedOption.getIndex() < this.list.length) {
                for (var i: number = selectedOption.getIndex(); i < this.list.length; i++) {
                    this.list[i].setIndex(i);
                }
            }

            if (!silent) {
                this.notifySelectedOptionRemoved(selectedOption);
            }
        }

        count(): number {
            return this.list.length;
        }

        getSelectedOptions(): SelectedOption<AccessControlEntry>[] {
            return this.list;
        }

        getByIndex(index: number): SelectedOption<AccessControlEntry> {
            return this.list[index];
        }

        getByOption(option: Option<AccessControlEntry>): SelectedOption<AccessControlEntry> {
            return this.getById(option.value);
        }

        getById(id: string): SelectedOption<AccessControlEntry> {
            return this.list.filter((selectedOption: SelectedOption<AccessControlEntry>) => {
                return selectedOption.getOption().value == id;
            })[0];
        }

        isSelected(option: Option<AccessControlEntry>): boolean {
            return this.getByOption(option) != null;
        }

        maximumOccurrencesReached(): boolean {
            if (this.maximumOccurrences == 0) {
                return false;
            }
            return this.count() >= this.maximumOccurrences;
        }

        moveOccurrence(formIndex: number, toIndex: number) {
            api.util.ArrayHelper.moveElement(formIndex, toIndex, this.list);
            api.util.ArrayHelper.moveElement(formIndex, toIndex, this.getChildren());

            this.list.forEach((selectedOption: SelectedOption<AccessControlEntry>, index: number) => selectedOption.setIndex(index));
        }

        private notifySelectedOptionRemoved(removed: SelectedOption<AccessControlEntry>) {
            this.selectedOptionRemovedListeners.forEach((listener) => {
                listener(removed);
            });
        }

        onOptionDeselected(listener: {(removed: SelectedOption<AccessControlEntry>): void;}) {
            this.selectedOptionRemovedListeners.push(listener);
        }

        unOptionDeselected(listener: {(removed: SelectedOption<AccessControlEntry>): void;}) {
            this.selectedOptionRemovedListeners = this.selectedOptionRemovedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

    }

}