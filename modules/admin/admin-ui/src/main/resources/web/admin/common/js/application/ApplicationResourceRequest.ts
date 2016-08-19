import {ResourceRequest} from "../rest/ResourceRequest";
import {Path} from "../rest/Path";
import {ApplicationJson} from "./json/ApplicationJson";
import {Application} from "./Application";

export class ApplicationResourceRequest<JSON_TYPE, PARSED_TYPE> extends ResourceRequest<JSON_TYPE, PARSED_TYPE> {

        private resourcePath: Path;

        constructor() {
            super();
            this.resourcePath = Path.fromParent(super.getRestPath(), "application");
        }

        getResourcePath(): Path {
            return this.resourcePath;
        }

        fromJsonToApplication(json: ApplicationJson): Application {
            return Application.fromJson(json);
        }
    }
