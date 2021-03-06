module api.ui.security.acl {

    import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;

    interface AccessSelectorOption {
        value: Access;
        name: string;
    }

    export class AccessSelector extends api.ui.tab.TabMenu {

        private static OPTIONS: AccessSelectorOption[] = [
            {value: Access.READ, name: 'Can Read'},
            {value: Access.WRITE, name: 'Can Write'},
            {value: Access.PUBLISH, name: 'Can Publish'},
            {value: Access.FULL, name: 'Full Access'},
            {value: Access.CUSTOM, name: 'Custom...'}
        ];

        private value: Access;
        private valueChangedListeners: {(event: api.ValueChangedEvent): void}[] = [];

        constructor() {
            super('access-selector');

            AccessSelector.OPTIONS.forEach((option: AccessSelectorOption, index: number) => {
                let menuItem = (<TabMenuItemBuilder>new TabMenuItemBuilder().setLabel(option.name).setAddLabelTitleAttribute(
                    false)).build();
                this.addNavigationItem(menuItem);
            });

            this.onNavigationItemSelected((event: api.ui.NavigatorEvent) => {
                let item: api.ui.tab.TabMenuItem = <api.ui.tab.TabMenuItem> event.getItem();
                this.setValue(AccessSelector.OPTIONS[item.getIndex()].value);
            });
        }

        getValue(): Access {
            return this.value;
        }

        setValue(value: Access, silent?: boolean): AccessSelector {
            let option = this.findOptionByValue(value);
            if (option) {
                this.selectNavigationItem(AccessSelector.OPTIONS.indexOf(option));
                if (!silent) {
                    this.notifyValueChanged(new api.ValueChangedEvent(Access[this.value], Access[value]));
                }
                this.value = value;
            }
            return this;
        }

        protected setButtonLabel(value: string): AccessSelector {
            this.getTabMenuButtonEl().setLabel(value, false);
            return this;
        }

        private findOptionByValue(value: Access): AccessSelectorOption {
            for (let i = 0; i < AccessSelector.OPTIONS.length; i++) {
                let option = AccessSelector.OPTIONS[i];
                if (option.value === value) {
                    return option;
                }
            }
            return undefined;
        }

        showMenu() {

            if (this.getSelectedNavigationItem().isVisibleInMenu()) {
                this.resetItemsVisibility();
                this.getSelectedNavigationItem().setVisibleInMenu(false);
            }

            let menu = this.getMenuEl();
            let entry = menu.getParentElement().getParentElement();
            let list = entry.getParentElement();
            let offset = entry.getEl().getOffsetTopRelativeToParent() -
                (list.getEl().getOffsetTopRelativeToParent() + list.getEl().getPaddingTop() + list.getEl().getScrollTop());
            let height = menu.getEl().getHeightWithoutPadding();

            if (offset > height) {
                menu.addClass('upward');
            } else {
                menu.removeClass('upward');
            }

            super.showMenu();
        }

        onValueChanged(listener: (event: api.ValueChangedEvent)=>void) {
            this.valueChangedListeners.push(listener);
        }

        unValueChanged(listener: (event: api.ValueChangedEvent)=>void) {
            this.valueChangedListeners = this.valueChangedListeners.filter((curr) => {
                return curr !== listener;
            });
        }

        private notifyValueChanged(event: api.ValueChangedEvent) {
            this.valueChangedListeners.forEach((listener) => {
                listener(event);
            });
        }

    }

}
