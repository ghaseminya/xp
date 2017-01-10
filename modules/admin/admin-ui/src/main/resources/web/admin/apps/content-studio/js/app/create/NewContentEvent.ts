import "../../api.ts";

import ContentTypeSummary = api.schema.content.ContentTypeSummary;
import Content = api.content.Content;

export class NewContentEvent extends api.event.Event {

    private contentType: ContentTypeSummary;

    private parentContent: Content;

    private targetWindow: Window;

    constructor(contentType: ContentTypeSummary, parentContent: Content, targetWindow?: Window) {
        super();
        this.contentType = contentType;
        this.parentContent = parentContent;
        this.targetWindow = targetWindow;
    }

    getContentType(): ContentTypeSummary {
        return this.contentType;
    }

    getParentContent(): Content {
        return this.parentContent;
    }

    getTargetWindow(): Window {
        return this.targetWindow;
    }

    static on(handler: (event: NewContentEvent) => void) {
        api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
    }

    static un(handler?: (event: NewContentEvent) => void) {
        api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
    }
}
