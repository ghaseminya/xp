module api.liveedit {

    import ComponentPath = api.content.page.ComponentPath;
    import PageComponentView = api.liveedit.PageComponentView;

    export class PageComponentSelectEvent extends api.event.Event2 {

        private path: ComponentPath;

        private pageItemView: PageComponentView;

        constructor(path: ComponentPath, itemView: PageComponentView) {
            super();
            this.path = path;
            this.pageItemView = itemView;
        }

        getPath(): ComponentPath {
            return this.path;
        }

        getItemView(): PageComponentView {
            return this.pageItemView;
        }

        isComponentEmpty(): boolean {
            return this.pageItemView.isEmpty();
        }

        static on(handler: (event: PageComponentSelectEvent) => void, contextWindow: Window = window) {
            api.event.Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler?: (event: PageComponentSelectEvent) => void, contextWindow: Window = window) {
            api.event.Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }
}