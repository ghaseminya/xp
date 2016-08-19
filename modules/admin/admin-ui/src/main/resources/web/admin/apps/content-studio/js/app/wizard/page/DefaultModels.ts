import {PageTemplate} from "../../../../../../common/js/content/page/PageTemplate";
import {PageDescriptor} from "../../../../../../common/js/content/page/PageDescriptor";

export class DefaultModels {

    private pageTemplate: PageTemplate;

    private pageDescriptor: PageDescriptor;

    constructor(pageTemplate: PageTemplate, pageDescriptor: PageDescriptor) {
        this.pageTemplate = pageTemplate;
        this.pageDescriptor = pageDescriptor;
    }

    hasPageTemplate(): boolean {
        return !!this.pageTemplate;
    }

    getPageTemplate(): PageTemplate {
        return this.pageTemplate ? this.pageTemplate.clone() : null;
    }

    getPageDescriptor(): PageDescriptor {
        return this.pageDescriptor;
    }
}
