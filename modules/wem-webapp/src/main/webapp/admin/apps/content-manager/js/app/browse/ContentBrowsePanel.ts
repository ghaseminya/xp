module app_browse {

    export class ContentBrowsePanel extends api_app_browse.BrowsePanel<api_content.ContentSummary> {

        private browseActions:app_browse.ContentBrowseActions;

        private toolbar:ContentBrowseToolbar;

        private contentTreeGridPanel:app_browse.ContentTreeGridPanel;

        private contentTreeGridPanel2:app_browse_grid.ContentGridPanel2;

        private contentFilterPanel:app_browse_filter.ContentBrowseFilterPanel;

        private contentBrowseItemPanel:ContentBrowseItemPanel;

        constructor() {
            var treeGridContextMenu = new ContentTreeGridContextMenu();
            this.contentTreeGridPanel = components.gridPanel = new app_browse.ContentTreeGridPanel({
                contextMenu: treeGridContextMenu
            });

            this.browseActions = ContentBrowseActions.init(this.contentTreeGridPanel);
            treeGridContextMenu.setActions(this.browseActions);

            this.toolbar = new ContentBrowseToolbar(this.browseActions);
            this.contentBrowseItemPanel =
            components.detailPanel = new ContentBrowseItemPanel({actionMenuActions: [
                this.browseActions.SHOW_NEW_CONTENT_DIALOG_ACTION,
                this.browseActions.EDIT_CONTENT,
                this.browseActions.OPEN_CONTENT,
                this.browseActions.DELETE_CONTENT,
                this.browseActions.DUPLICATE_CONTENT,
                this.browseActions.MOVE_CONTENT]});

            this.contentFilterPanel = new app_browse_filter.ContentBrowseFilterPanel();

            this.contentTreeGridPanel2 = new app_browse_grid.ContentGridPanel2();

            super({
                browseToolbar: this.toolbar,
                treeGridPanel: this.contentTreeGridPanel,
                treeGridPanel2: this.contentTreeGridPanel2,
                browseItemPanel: this.contentBrowseItemPanel,
                filterPanel: this.contentFilterPanel});

            ShowPreviewEvent.on((event) => {
                this.contentBrowseItemPanel.setPreviewMode(true);
            });

            ShowDetailsEvent.on((event) => {
                this.contentBrowseItemPanel.setPreviewMode(false);
            });

            api_content.ContentDeletedEvent.on((event) => {
                var contents:api_content.ContentSummary[] = event.getContents();
                for (var i = 0; i < contents.length; i++) {
                    this.contentTreeGridPanel.remove(contents[i].getPath().toString());
                }
            });

            api_content.ContentCreatedEvent.on((event) => {
                console.log('On content created', event.getPath());
                this.setRefreshNeeded(true);
            });

            api_content.ContentUpdatedEvent.on((event) => {
                console.log('On content updated', event.getModel());
                this.setRefreshNeeded(true);
            });

            this.contentTreeGridPanel.addListener(<api_app_browse_grid.TreeGridPanelListener>{
                onSelectionChanged: (event:api_app_browse_grid.TreeGridSelectionChangedEvent) => {
                    var contentSummaries = this.extModelsToContentSummaries(event.selectedModels);
                    this.browseActions.updateActionsEnabledState(contentSummaries);
                }
            });

            ShowNewContentGridEvent.on( () => {
                super.toggleShowingNewGrid();
            });
        }

        showCallback() {
            app.Router.setHash("browse");
        }

        getActions():api_ui.Action[] {
            var actions = super.getActions();
            // TODO: Ensures shortcut for showing new experimental content grid without having the action in the toolbar
            actions.push(app_browse.ContentBrowseActions.get().SHOW_NEW_CONTENT_GRID);
            return actions;
        }

        extModelsToContentSummaries(models:Ext_data_Model[]):api_content.ContentSummary[] {

            var summaries:api_content.ContentSummary[] = [];
            for (var i = 0; i < models.length; i++) {
                summaries.push(new api_content.ContentSummary(<api_content_json.ContentSummaryJson>models[i].data))
            }
            return summaries;
        }

        extModelsToBrowseItems(models:Ext_data_Model[]):api_app_browse.BrowseItem<api_content.ContentSummary>[] {

            var browseItems:api_app_browse.BrowseItem<api_content.ContentSummary>[] = [];
            models.forEach((model:Ext_data_Model) => {
                var content = new api_content.ContentSummary(<api_content_json.ContentSummaryJson>model.data);
                var item = new api_app_browse.BrowseItem<api_content.ContentSummary>(content).
                    setDisplayName(model.data['displayName']).
                    setPath(model.data['path']).
                    setIconUrl(model.data['iconUrl']);
                browseItems.push(item);
            });
            return browseItems;
        }
    }

}
