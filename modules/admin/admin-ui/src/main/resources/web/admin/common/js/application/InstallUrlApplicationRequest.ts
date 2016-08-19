import {ApplicationInstallResultJson} from "./json/ApplicationInstallResultJson";
import {Path} from "../rest/Path";
import {JsonResponse} from "../rest/JsonResponse";
import {ApplicationInstallResult} from "./ApplicationInstallResult";
import {ApplicationResourceRequest} from "./ApplicationResourceRequest";

export class InstallUrlApplicationRequest extends ApplicationResourceRequest<ApplicationInstallResultJson, ApplicationInstallResult> {

        private applicationUrl: string;

        constructor(applicationUrl: string) {
            super();
            super.setMethod("POST");
            this.applicationUrl = applicationUrl;
            this.setHeavyOperation(true);
        }

        getParams(): Object {
            return {
                URL: this.applicationUrl
            };
        }

        getRequestPath(): Path {
            return Path.fromParent(super.getResourcePath(), "installUrl");
        }

        sendAndParse(): wemQ.Promise<ApplicationInstallResult> {
            return this.send().then((response: JsonResponse<ApplicationInstallResultJson>) => {
                return ApplicationInstallResult.fromJson(response.getResult());
            });
        }
    }
