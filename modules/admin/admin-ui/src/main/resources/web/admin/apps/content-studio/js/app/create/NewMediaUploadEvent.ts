import "../../api.ts";

import Content = api.content.Content;
import UploadItem = api.ui.uploader.UploadItem;

export class NewMediaUploadEvent extends api.event.Event {

    private uploadItems: UploadItem<Content>[];

    private parentContent: Content;

    private targetWindow: Window;

    constructor(items: UploadItem<Content>[], parentContent: Content, targetWindow?: Window) {
        super();
        this.uploadItems = items;
        this.parentContent = parentContent;
        this.targetWindow = targetWindow;
    }

    getUploadItems(): UploadItem<Content>[] {
        return this.uploadItems;
    }

    getParentContent(): Content {
        return this.parentContent;
    }


    getTargetWindow(): Window {
        return this.targetWindow;
    }

    static on(handler: (event: NewMediaUploadEvent) => void) {
        api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
    }

    static un(handler?: (event: NewMediaUploadEvent) => void) {
        api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
    }
}
