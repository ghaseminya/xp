module app_browse {

    export class ContentBrowsePanel extends api_app_browse.BrowsePanel {

        private browseActions:app_browse.ContentBrowseActions;

        private toolbar:ContentBrowseToolbar;

        private contentTreeGridPanel:app_browse.ContentTreeGridPanel;

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
            var params = createLoadContentParams({});

            api_remote_content.RemoteContentService.content_find(params, (response:api_remote_content.FindResult) => {
                // set facet data
                var termsFacets:api_facet.Facet[] = api_facet.FacetFactory.createFacets(response.facets)
                this.contentFilterPanel.updateFacets(termsFacets);
            });

            super({
                browseToolbar: this.toolbar,
                treeGridPanel: this.contentTreeGridPanel,
                browseItemPanel: this.contentBrowseItemPanel,
                filterPanel: this.contentFilterPanel});

            ShowPreviewEvent.on((event) => {
                this.contentBrowseItemPanel.setPreviewMode(true);
            });

            ShowDetailsEvent.on((event) => {
                this.contentBrowseItemPanel.setPreviewMode(false);
            });

            this.contentTreeGridPanel.addListener(<api_app_browse_grid.TreeGridPanelListener>{
                onSelectionChanged: (event:api_app_browse_grid.TreeGridSelectionChangedEvent) => {
                    this.browseActions.updateActionsEnabledState(<any[]>event.selectedModels);
                }
            });
        }

        extModelsToBrowseItems(models:api_model.ContentExtModel[]) {

            var browseItems:api_app_browse.BrowseItem[] = [];
            models.forEach((model:api_model.ContentExtModel, index:number) => {
                var item = new api_app_browse.BrowseItem(models[index]).
                    setDisplayName(model.data.displayName).
                    setPath(model.data.path).
                    setIconUrl(model.data.iconUrl);
                browseItems.push(item);
            });
            return browseItems;
        }
    }

    export function createLoadContentParams(values:any) {

        var now = new Date();
        var oneDayAgo = new Date();
        var oneWeekAgo = new Date();
        var oneHourAgo = new Date();
        oneDayAgo.setDate(now.getDate() - 1);
        oneWeekAgo.setDate(now.getDate() - 7);
        Admin.lib.DateHelper.addHours(oneHourAgo, -1);

        var facets = {
            "space": {
                "terms": {
                    "field": "space",
                    "size": 10,
                    "all_terms": true,
                    "order": "term"
                }
            },
            "contentType": {
                "terms": {
                    "field": "contentType",
                    "size": 10,
                    "all_terms": true,
                    "order": "term"
                }
            },
            "< 1 day": {
                "query": {
                    "range": {
                        "lastModified.date": {
                            "from": oneDayAgo.toISOString(),
                            "include_lower": true
                        }
                    }
                }
            },
            "< 1 hour": {
                "query": {
                    "range": {
                        "lastModified.date": {
                            "from": oneHourAgo.toISOString(),
                            "include_lower": true
                        }
                    }
                }
            },
            "< 1 week": {
                "query": {
                    "range": {
                        "lastModified.date": {
                            "from": oneWeekAgo.toISOString(),
                            "include_lower": true
                        }
                    }
                }
            }
        };

        var ranges = [];
        if (values.ranges) {
            for (var i = 0; i < values.ranges.length; i++) {
                var lower;
                switch (values.ranges[i]) {
                case '< 1 day':
                    lower = oneDayAgo;
                    break;
                case '< 1 hour':
                    lower = oneHourAgo;
                    break;
                case '< 1 week':
                    lower = oneWeekAgo;
                    break;
                default:
                    lower = null;
                    break;
                }
                ranges.push({
                    lower: lower,
                    upper: null
                })
            }
        }

        return {
            fulltext: values.query || '',
            contentTypes: values.contentType || [],
            spaces: values.space || [],
            ranges: ranges || [],
            facets: facets || {},
            include: true
        };
    }
}
