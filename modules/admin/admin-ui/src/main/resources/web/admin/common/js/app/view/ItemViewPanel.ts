import {Equitable} from "../../Equitable";
import {Panel} from "../../ui/panel/Panel";
import {Closeable} from "../../ui/Closeable";
import {Toolbar} from "../../ui/toolbar/Toolbar";
import {Action} from "../../ui/Action";
import {ItemViewClosedEvent} from "./ItemViewClosedEvent";
import {ViewItem} from "./ViewItem";

export class ItemViewPanel<M extends Equitable> extends Panel implements Closeable {

        private toolbar: Toolbar;

        private panel: Panel;

        private browseItem: ViewItem<M>;

        private closedListeners: {(event: ItemViewClosedEvent<M>):void}[] = [];

        constructor(toolbar: Toolbar, panel: Panel) {
            super("item-view-panel");
            this.toolbar = toolbar;
            this.panel = panel;
            this.appendChild(this.toolbar);
            this.appendChild(this.panel);
        }

        /*
         As long as the close action is excluded from the toolbar,
         we should add it along with the other toolbar actions to be able to close tabs.
         */
        getActions(): Action[] {
            return [];
        }

        setItem(item: ViewItem<M>) {
            this.browseItem = item;
        }

        getItem(): ViewItem<M> {
            return this.browseItem;
        }

        close(checkCanClose: boolean = false) {
            if (!checkCanClose || this.canClose()) {
                this.notifyClosed();
            }
        }

        canClose(): boolean {
            return true;
        }


        onClosed(listener: (event: ItemViewClosedEvent<M>)=>void) {
            this.closedListeners.push(listener);
        }

        unClosed(listener: (event: ItemViewClosedEvent<M>)=>void) {
            this.closedListeners = this.closedListeners.filter((currentListener: (event: ItemViewClosedEvent<M>)=>void) => {
                return currentListener != listener;
            })
        }

        private notifyClosed() {
            this.closedListeners.forEach((listener: (event: ItemViewClosedEvent<M>)=>void) => {
                listener.call(this, new ItemViewClosedEvent(this));
            });
        }

    }

