import {ApplicationKey} from "../../application/ApplicationKey";
import {Path} from "../../rest/Path";
import {JsonResponse} from "../../rest/JsonResponse";
import {MacroDescriptor} from "../MacroDescriptor";
import {MacroResourceRequest} from "./MacroResourceRequest";
import {MacrosJson} from "./MacrosJson";

export class GetMacrosRequest extends MacroResourceRequest<MacrosJson, MacroDescriptor[]> {

        private applicationKeys: ApplicationKey[];

        constructor(applicationKeys: ApplicationKey[]) {
            super();
            super.setMethod("POST");
            this.applicationKeys = applicationKeys;
        }

        getParams(): Object {
            return {
                appKeys: ApplicationKey.toStringArray(this.applicationKeys)
            };
        }

        getRequestPath(): Path {
            return Path.fromParent(super.getResourcePath(), "getByApps");
        }

        sendAndParse(): wemQ.Promise<MacroDescriptor[]> {
            return this.send().then((response: JsonResponse<MacrosJson>) => {
                return this.toMacroDescriptors(response.getResult());
            });
        }

        toMacroDescriptors(macrosJson: MacrosJson): MacroDescriptor[] {
            var result: MacroDescriptor[] = [];
            for (var i = 0; i < macrosJson.macros.length; i++) {
                result.push(MacroDescriptor.create().fromJson(macrosJson.macros[i]).build());
            }
            return result;
        }
    }
