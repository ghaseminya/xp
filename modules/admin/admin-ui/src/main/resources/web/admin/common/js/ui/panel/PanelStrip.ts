module api.ui.panel {

    export class PanelStrip extends Panel {

        private panels: Panel[] = [];

        private headers: api.dom.H2El[] = [];

        private scrollable: api.dom.Element;

        private offset: number = 0;

        private panelShown: Panel = null;

        private panelShownListeners: {(event: PanelShownEvent):void}[] = [];

        constructor(scrollable?: api.dom.Element, className?: string) {
            super('panel-strip' + (className ? ' ' + className : ''));
            if (scrollable) {
                this.scrollable = scrollable;
                this.scrollable.addClass('panel-strip-scrollable');
            } else {
                this.scrollable = this;
            }

            this.onShown(() => {
                this.updateLastPanelHeight();
            });
        }

        private countExistingChildren(index: number): number {
            let count = Math.min(this.panels.length, index);
            for (let i = 0; i < Math.min(this.headers.length, index); i++) {
                if(this.headers[i]) {
                    count++;
                }
            }
            return count;
        }

        insertPanel(panel: Panel, index: number, header?: string ): number {
            panel.setDoOffset(false);
            let previousChildrenIndex = this.countExistingChildren(index);
            let headerEl;
            if (header) {
                headerEl = new api.dom.H2El('panel-strip-panel-header');
                headerEl.getEl().setInnerHtml(header);
                this.insertChild(headerEl, previousChildrenIndex);
            }
            this.panels.splice(index, 0, panel);
            this.headers.splice(index, 0, headerEl);

            if (header) {
                this.insertChild(panel, previousChildrenIndex + 1);
            } else {
                this.insertChild(panel, previousChildrenIndex);
            }

            if (this.isVisible()) {
                this.updateLastPanelHeight();
            }
            return index;
        }

        getPanels(): Panel[] {
            return this.panels;
        }

        getScrollable(): api.dom.Element {
            return this.scrollable;
        }

        setScrollOffset(offset: number): PanelStrip {
            this.offset = offset;
            return this;
        }

        getScrollOffset(): number {
            return this.offset;
        }

        private updateLastPanelHeight() {
            if (this.getSize() === 0) {
                return;
            }

            if (this.getSize() > 1) {
                // restore the one before last panel's height if needed
                let beforeLastEl = this.getPanel(this.getSize() - 2).getEl();
                let originalHeight = beforeLastEl.getData('originalHeight');
                if (originalHeight) {
                    beforeLastEl.setHeight(originalHeight);
                }
            }
            // set the last panel height equal to that of the scrollable
            let lastEl = this.getPanel(this.getSize() - 1).getEl();
            if (!lastEl.getData('originalHeight')) {
                lastEl.setData('originalHeight', lastEl.getHTMLElement().style.height || 'auto');
            }
            lastEl.setHeightPx(this.scrollable.getEl().getHeight());
        }

        removePanel(panelToRemove: Panel, checkCanRemovePanel: boolean = true): number {

            let index: number = this.getPanelIndex(panelToRemove);
            if (index < 0 || checkCanRemovePanel && !this.canRemovePanel(panelToRemove)) {
                return -1;
            }
            this.removeChild(panelToRemove);
            this.removeChild(this.getHeader(index));
            this.panels.splice(index, 1);
            this.headers.splice(index, 1);

            if (this.isEmpty()) {
                this.panelShown = null;
            } else if (panelToRemove === this.getPanelShown()) {
                // show either panel that has the same index now or the last panel
                this.showPanelByIndex(Math.min(index, this.getSize() - 1));
            }

            if (this.isVisible() && index === this.getSize() && !this.isEmpty()) {
                // update if last panel was removed and there are still left
                this.updateLastPanelHeight();
            }

            return index;
        }

        /*
         * Override this method to decide whether given panel at given index can be removed or not. Default is true.
         */
        canRemovePanel(panel: Panel): boolean {
            return true;
        }

        isEmpty(): boolean {
            return this.panels.length === 0;
        }

        getSize(): number {
            return this.panels.length;
        }

        getPanel(index: number): Panel {
            return this.panels[index];
        }

        getHeader(index: number): api.dom.H2El {
            return this.headers[index];
        }

        getPanelShown(): Panel {
            return this.panelShown;
        }

        getPanelShownIndex(): number {
            return this.getPanelIndex(this.panelShown);
        }

        getPanelIndex<T extends Panel>(panel: T): number {
            let size = this.getSize();
            for (let i = 0; i < size; i++) {
                if (this.panels[i] === panel) {
                    return i;
                }
            }
            return -1;
        }

        showPanel(panel: Panel) {
            let index = this.getPanelIndex(panel);
            if (index > -1) {
                this.showPanelByIndex(index);
            }
        }

        showPanelByIndex(index: number) {
            let panelToShow = this.getPanel(index);
            if (panelToShow == null) {
                return;
            }

            wemjq(this.scrollable.getHTMLElement()).animate({
                scrollTop: index === 0 ? 0 : this.getScroll() - this.offset +
                                            panelToShow.getEl().getOffsetToParent().top -
                                            this.headers[index].getEl().getHeightWithBorder()
            }, {
                duration: 500,
                complete: () => {
                    this.notifyPanelShown(panelToShow, index, this.getPanelShown());
                    this.panelShown = panelToShow;
                }
            });
        }

        getScroll(): number {
            return this.scrollable.getHTMLElement().scrollTop;
        }

        setScroll(scrollTop: number) {
            this.scrollable.getHTMLElement().scrollTop = scrollTop;
        }

        onPanelShown(listener: (event: PanelShownEvent)=>void) {
            this.panelShownListeners.push(listener);
        }

        unPanelShown(listener: (event: PanelShownEvent)=>void) {
            this.panelShownListeners = this.panelShownListeners.filter((currentListener: (event: PanelShownEvent) => void) => {
                return  listener !== currentListener;
            });
        }

        private notifyPanelShown(panel: Panel, panelIndex: number, previousPanel: Panel) {
            this.panelShownListeners.forEach((listener: (event: PanelShownEvent) => void) => {
                listener.call(this, new PanelShownEvent(panel, panelIndex, previousPanel));
            });
        }

    }

}
