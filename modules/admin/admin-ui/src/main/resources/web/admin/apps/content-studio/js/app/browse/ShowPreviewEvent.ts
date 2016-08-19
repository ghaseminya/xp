import {Event} from "../../../../../common/js/event/Event";
import {ClassHelper} from "../../../../../common/js/ClassHelper";

import {BaseContentModelEvent} from "./BaseContentModelEvent";

export class ShowPreviewEvent extends BaseContentModelEvent {

    static on(handler: (event: ShowPreviewEvent) => void) {
        Event.bind(ClassHelper.getFullName(this), handler);
    }

    static un(handler?: (event: ShowPreviewEvent) => void) {
        Event.unbind(ClassHelper.getFullName(this), handler);
    }
}
