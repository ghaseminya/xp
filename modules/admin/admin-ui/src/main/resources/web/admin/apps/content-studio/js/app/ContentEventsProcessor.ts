import "../api.ts";
import {ContentWizardPanelParams} from "./wizard/ContentWizardPanelParams";
import {NewContentEvent} from "./create/NewContentEvent";
import {SortContentEvent} from "./browse/SortContentEvent";
import {OpenSortDialogEvent} from "./browse/OpenSortDialogEvent";
import {MoveContentEvent} from "./browse/MoveContentEvent";
import {OpenMoveDialogEvent} from "./browse/OpenMoveDialogEvent";
import AppBarTabId = api.app.bar.AppBarTabId;
import Content = api.content.Content;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import ContentUpdatedEvent = api.content.event.ContentUpdatedEvent;
import ShowBrowsePanelEvent = api.app.ShowBrowsePanelEvent;

export class ContentEventsProcessor {

    static openWizardTab(params: ContentWizardPanelParams, tabId: AppBarTabId, targetWindow?: Window): Window {
        let wizardUrl = `content-studio#/${params.toString()}`,
            isNew = !params.contentId,
            wizardId;

        if (targetWindow) {
            targetWindow.location.href = wizardUrl;
            return targetWindow;
        }

        if (navigator.userAgent.search("Chrome") > -1) {
            // add tab id for browsers that can focus tabs by id
            // add timestamp to new wizard id to be able to open multiple
            // wizards of the same type simultaneously
            if (!isNew) {
                wizardId = tabId.toString();
            } else {
                wizardId = `${tabId.toString()}-${Date.now()}`;
            }
        }
        return window.open(wizardUrl, wizardId);
    }

    static handleNew(newContentEvent: NewContentEvent) {

        var contentTypeSummary = newContentEvent.getContentType();
        var tabId = AppBarTabId.forNew(contentTypeSummary.getName());

        var wizardParams = new ContentWizardPanelParams()
            .setTabId(tabId)
            .setContentTypeName(contentTypeSummary.getContentTypeName())
            .setParentContentId(newContentEvent.getParentContent() ? newContentEvent.getParentContent().getContentId() : undefined)
            .setCreateSite(newContentEvent.getContentType().isSite());

        ContentEventsProcessor.openWizardTab(wizardParams, tabId, newContentEvent.getTargetWindow());
    }

    static handleEdit(event: api.content.event.EditContentEvent) {

        event.getModels().forEach((content: ContentSummaryAndCompareStatus) => {

            if (!content || !content.getContentSummary()) {
                return;
            }

            var contentSummary = content.getContentSummary(),
                contentTypeName = contentSummary.getType();

            var tabId = AppBarTabId.forEdit(contentSummary.getId());

            var wizardParams = new ContentWizardPanelParams()
                .setTabId(tabId)
                .setContentTypeName(contentTypeName)
                .setContentId(contentSummary.getContentId());

            ContentEventsProcessor.openWizardTab(wizardParams, tabId);
        });
    }

    static handleUpdated(event: ContentUpdatedEvent) {
        // do something when content is updated
    }

    static handleSort(event: SortContentEvent) {

        var contents: ContentSummaryAndCompareStatus[] = event.getModels();
        new OpenSortDialogEvent(contents[0]).fire();
    }

    static handleMove(event: MoveContentEvent) {

        var contents: ContentSummaryAndCompareStatus[] = event.getModels();
        new OpenMoveDialogEvent(contents.map(content => content.getContentSummary())).fire();
    }
}