import {ApplicationInstallResultJson} from "./json/ApplicationInstallResultJson";
import {Equitable} from "../Equitable";
import {Application} from "./Application";
import {ObjectHelper} from "../ObjectHelper";

export class ApplicationInstallResult implements Equitable{

        private application: Application;

        private failure: string;

        setFailure(value: string) {
            this.failure = value;
        }

        setApplication(application: Application) {
            this.application = application;
        }

        public getApplication(): Application {
            return this.application;
        }

        public getFailure(): string {
            return this.failure;
        }

        equals(o: Equitable): boolean {

            if (!ObjectHelper.iFrameSafeInstanceOf(o, ApplicationInstallResult)) {
                return false;
            }

            var other = <ApplicationInstallResult>o;
            return ObjectHelper.stringEquals(this.failure, other.failure) &&
                   ObjectHelper.equals(this.application, other.application);
        }

        static fromJson(json: ApplicationInstallResultJson): ApplicationInstallResult {
            let result = new ApplicationInstallResult();
            result.application = json.applicationInstalledJson ? Application.fromJson(json.applicationInstalledJson) : null;
            result.failure = json.failure;
            return result;
        }
    }
