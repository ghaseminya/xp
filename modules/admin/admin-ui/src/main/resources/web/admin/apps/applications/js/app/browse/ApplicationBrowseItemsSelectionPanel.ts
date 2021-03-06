import '../../api.ts';

import BrowseItem = api.app.browse.BrowseItem;
import Application = api.application.Application;
import ApplicationViewer = api.application.ApplicationViewer;

export class ApplicationBrowseItemsSelectionPanel extends api.app.browse.BrowseItemsSelectionPanel<Application> {

    createItemViewer(item: BrowseItem<Application>): ApplicationViewer {
        let viewer = new ApplicationViewer();
        viewer.setObject(item.getModel());
        return viewer;
    }

}
