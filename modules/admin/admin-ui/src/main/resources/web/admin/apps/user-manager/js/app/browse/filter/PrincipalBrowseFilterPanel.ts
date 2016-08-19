import {AggregationGroupView} from "../../../../../../common/js/aggregation/AggregationGroupView";
import {SearchInputValues} from "../../../../../../common/js/query/SearchInputValues";
import {Principal} from "../../../../../../common/js/security/Principal";
import {FindPrincipalsRequest} from "../../../../../../common/js/security/FindPrincipalsRequest";
import {PrincipalType} from "../../../../../../common/js/security/PrincipalType";
import {BrowseFilterPanel} from "../../../../../../common/js/app/browse/filter/BrowseFilterPanel";
import {Element} from "../../../../../../common/js/dom/Element";
import {FindPrincipalsResult} from "../../../../../../common/js/security/FindPrincipalsResult";
import {StringHelper} from "../../../../../../common/js/util/StringHelper";
import {DefaultErrorHandler} from "../../../../../../common/js/DefaultErrorHandler";

import {PrincipalBrowseResetEvent} from "./PrincipalBrowseResetEvent";
import {PrincipalBrowseSearchEvent} from "./PrincipalBrowseSearchEvent";

export class PrincipalBrowseFilterPanel extends BrowseFilterPanel {


    constructor() {

        super(null);

        this.onReset(()=> {
            this.resetFacets();
        });

        this.onShown(() => {
            this.refresh();
        });

        this.initHitsCounter();
    }

    doRefresh() {
        this.searchFacets(true);
    }

    doSearch(elementChanged?: Element) {
        this.searchFacets();
    }

    private resetFacets(supressEvent?: boolean) {
        this.searchDataAndHandleResponse("", false);

        if (!supressEvent) { // then fire usual reset event with content grid reloading
            new PrincipalBrowseResetEvent().fire();
        }
    }

    private searchFacets(isRefresh: boolean = false) {
        var values = this.getSearchInputValues(),
            searchText = values.getTextSearchFieldValue();
        if (!searchText) {
            this.handleEmptyFilterInput(isRefresh);
            return;
        }

        this.searchDataAndHandleResponse(searchText);
    }

    private handleEmptyFilterInput(isRefresh: boolean) {
        if (isRefresh) {
            this.resetFacets(true);
        } else {
            this.reset();
        }
    }

    private searchDataAndHandleResponse(searchString: string, fireEvent: boolean = true) {
        new FindPrincipalsRequest().setAllowedTypes([PrincipalType.GROUP, PrincipalType.USER, PrincipalType.ROLE]).setSearchQuery(
            searchString).sendAndParse().then((result: FindPrincipalsResult) => {

            let principals = result.getPrincipals();
            if (fireEvent) {
                new PrincipalBrowseSearchEvent(principals).fire();
            }
            this.updateHitsCounter(principals ? principals.length : 0, StringHelper.isBlank(searchString));
        }).catch((reason: any) => {
            DefaultErrorHandler.handle(reason);
        }).done();
    }

    private initHitsCounter() {
        this.searchDataAndHandleResponse("", false);
    }
}
