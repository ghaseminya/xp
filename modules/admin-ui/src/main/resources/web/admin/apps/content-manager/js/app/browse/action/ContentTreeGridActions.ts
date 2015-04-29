module app.browse.action {

    import Action = api.ui.Action;
    import TreeGridActions = api.ui.treegrid.actions.TreeGridActions;
    import BrowseItem = api.app.browse.BrowseItem;
    import ContentSummary = api.content.ContentSummary;
    import Content = api.content.Content;
    import AccessControlEntry = api.security.acl.AccessControlEntry;
    import AccessControlList = api.security.acl.AccessControlList;

    export class ContentTreeGridActions implements TreeGridActions<ContentSummary> {

        public SHOW_NEW_CONTENT_DIALOG_ACTION: Action;
        public PREVIEW_CONTENT: Action;
        public EDIT_CONTENT: Action;
        public DELETE_CONTENT: Action;
        public DUPLICATE_CONTENT: Action;
        public MOVE_CONTENT: Action;
        public SORT_CONTENT: Action;
        public PUBLISH_CONTENT: Action;
        public TOGGLE_SEARCH_PANEL: Action;

        private actions: api.ui.Action[] = [];

        constructor(grid: ContentTreeGrid) {
            this.TOGGLE_SEARCH_PANEL = new ToggleSearchPanelAction();

            this.SHOW_NEW_CONTENT_DIALOG_ACTION = new ShowNewContentDialogAction(grid);
            this.PREVIEW_CONTENT = new PreviewContentAction(grid);
            this.EDIT_CONTENT = new EditContentAction(grid);
            this.DELETE_CONTENT = new DeleteContentAction(grid);
            this.DUPLICATE_CONTENT = new DuplicateContentAction(grid);
            this.MOVE_CONTENT = new MoveContentAction(grid);
            this.SORT_CONTENT = new SortContentAction(grid);
            this.PUBLISH_CONTENT = new PublishContentAction(grid);

            this.actions.push(
                this.SHOW_NEW_CONTENT_DIALOG_ACTION,
                this.PREVIEW_CONTENT, this.EDIT_CONTENT,
                this.DELETE_CONTENT, this.DUPLICATE_CONTENT,
                this.MOVE_CONTENT, this.SORT_CONTENT,
                this.PUBLISH_CONTENT
            );

        }

        getAllActions(): api.ui.Action[] {
            return this.actions;
        }

        updateActionsEnabledState(contentBrowseItems: BrowseItem<ContentSummary>[]): wemQ.Promise<BrowseItem<ContentSummary>[]> {
            this.TOGGLE_SEARCH_PANEL.setVisible(false);

            var contentSummaries: ContentSummary[] = contentBrowseItems.map((elem: BrowseItem<ContentSummary>) => {
                return elem.getModel();
            });

            var deferred = wemQ.defer<BrowseItem<ContentSummary>[]>();

            switch (contentBrowseItems.length) {
            case 0:
                this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(true);
                this.EDIT_CONTENT.setEnabled(false);
                this.DELETE_CONTENT.setEnabled(false);
                this.DUPLICATE_CONTENT.setEnabled(false);
                this.MOVE_CONTENT.setEnabled(false);
                this.SORT_CONTENT.setEnabled(false);
                this.PREVIEW_CONTENT.setEnabled(false);
                this.PUBLISH_CONTENT.setEnabled(false);
                deferred.resolve(contentBrowseItems);
                break;
            case 1:
                var contentSummary = contentSummaries[0];
                this.EDIT_CONTENT.setEnabled(!contentSummary ? false : contentSummary.isEditable());
                this.DELETE_CONTENT.setEnabled(!contentSummary ? false : contentSummary.isDeletable());
                this.DUPLICATE_CONTENT.setEnabled(true);
                this.MOVE_CONTENT.setEnabled(true);
                this.PUBLISH_CONTENT.setEnabled(true);
                this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(false);
                this.PREVIEW_CONTENT.setEnabled(false);
                var parallelPromises: wemQ.Promise<any>[] = [
                    new api.content.page.IsRenderableRequest(contentSummary.getContentId()).sendAndParse().
                        then((renderable: boolean) => {
                            this.PREVIEW_CONTENT.setEnabled(renderable);
                            if(contentBrowseItems.length > 0) {
                                contentBrowseItems[0].setRenderable(renderable);
                            }
                        }),
                    // check if selected content allows children and if user has create permission for it
                    new api.schema.content.GetContentTypeByNameRequest(contentSummary.getType()).
                        sendAndParse().
                        then((contentType: api.schema.content.ContentType) => {
                            var allowsChildren = (contentType && contentType.isAllowChildContent());
                            this.SORT_CONTENT.setEnabled(allowsChildren);
                            var hasCreatePermission = false;
                            new api.security.auth.IsAuthenticatedRequest().
                                sendAndParse().
                                then((loginResult: api.security.auth.LoginResult) => {
                                    new api.content.GetContentPermissionsByPathRequest(contentSummary.getPath()).
                                        sendAndParse().
                                        then((accessControlList: AccessControlList) => {
                                            hasCreatePermission = this.hasPermission(api.security.acl.Permission.CREATE, loginResult, accessControlList);
                                            this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(allowsChildren && hasCreatePermission);
                                        })
                                })
                        })
                ];
                wemQ.all(parallelPromises).spread<void>(() => {
                    deferred.resolve(contentBrowseItems);
                    return wemQ(null);
                }).done();
                break;
            default:
                this.SHOW_NEW_CONTENT_DIALOG_ACTION.setEnabled(false);
                this.PREVIEW_CONTENT.setEnabled(false);
                this.EDIT_CONTENT.setEnabled(this.anyEditable(contentSummaries));
                this.DELETE_CONTENT.setEnabled(this.anyDeletable(contentSummaries));
                this.DUPLICATE_CONTENT.setEnabled(false);
                this.MOVE_CONTENT.setEnabled(true);
                this.SORT_CONTENT.setEnabled(false);
                this.PUBLISH_CONTENT.setEnabled(true);
                deferred.resolve(contentBrowseItems);
            }
            return deferred.promise;
        }

        private anyEditable(contentSummaries: api.content.ContentSummary[]): boolean {
            for (var i = 0; i < contentSummaries.length; i++) {
                var content: api.content.ContentSummary = contentSummaries[i];
                if (!!content && content.isEditable()) {
                    return true;
                }
            }
            return false;
        }

        private anyDeletable(contentSummaries: api.content.ContentSummary[]): boolean {
            for (var i = 0; i < contentSummaries.length; i++) {
                var content: api.content.ContentSummary = contentSummaries[i];
                if (!!content && content.isDeletable()) {
                    return true;
                }
            }
            return false;
        }

        private isPrincipalPresent(principalKey: api.security.PrincipalKey,
                                   accessEntriesToCheck: AccessControlEntry[]): boolean {
            var result = false;
            accessEntriesToCheck.some((entry: AccessControlEntry) => {
                if (entry.getPrincipalKey().equals(principalKey)) {
                    result = true;
                    return true;
                }
            });

            return result;
        }

        private hasPermission(permission: api.security.acl.Permission,
                              loginResult: api.security.auth.LoginResult,
                              accessControlList: AccessControlList): boolean {
            var result = false;
            var entries = accessControlList.getEntries();
            var accessEntriesWithGivenPermissions: AccessControlEntry[] = entries.filter((item: AccessControlEntry) => {
                return item.isAllowed(permission);
            });

            loginResult.getPrincipals().some((principalKey: api.security.PrincipalKey) => {
                if (api.security.RoleKeys.ADMIN.equals(principalKey) ||
                    this.isPrincipalPresent(principalKey, accessEntriesWithGivenPermissions)) {
                    result = true;
                    return true;
                }
            });
            return result;
        }
    }
}
