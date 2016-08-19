import {UserStoreAccessControlList} from "./acl/UserStoreAccessControlList";
import {Path} from "../rest/Path";
import {JsonResponse} from "../rest/JsonResponse";
import {AuthConfig} from "./AuthConfig";
import {SecurityResourceRequest} from "./SecurityResourceRequest";
import {UserStore} from "./UserStore";
import {UserStoreJson} from "./UserStoreJson";
import {UserStoreKey} from "./UserStoreKey";

export class UpdateUserStoreRequest extends SecurityResourceRequest<UserStoreJson, UserStore> {

        private userStoreKey: UserStoreKey;
        private displayName: string;
        private description: string;
        private authConfig: AuthConfig;
        private permissions: UserStoreAccessControlList;

        constructor() {
            super();
            super.setMethod("POST");
        }

        getParams(): Object {
            return {
                key: this.userStoreKey.toString(),
                displayName: this.displayName,
                description: this.description,
                authConfig: this.authConfig ? this.authConfig.toJson() : undefined,
                permissions: this.permissions ? this.permissions.toJson() : undefined
            };
        }

        setKey(userStoreKey: UserStoreKey): UpdateUserStoreRequest {
            this.userStoreKey = userStoreKey;
            return this;
        }

        setDisplayName(displayName: string): UpdateUserStoreRequest {
            this.displayName = displayName;
            return this;
        }

        setDescription(description: string): UpdateUserStoreRequest {
            this.description = description;
            return this;
        }

        setAuthConfig(authConfig: AuthConfig): UpdateUserStoreRequest {
            this.authConfig = authConfig;
            return this;
        }

        setPermissions(permissions: UserStoreAccessControlList): UpdateUserStoreRequest {
            this.permissions = permissions;
            return this;
        }

        getRequestPath(): Path {
            return Path.fromParent(super.getResourcePath(), 'userstore/update');
        }

        sendAndParse(): wemQ.Promise<UserStore> {
            return this.send().then((response: JsonResponse<UserStoreJson>) => {
                return this.fromJsonToUserStore(response.getResult());
            });
        }

        fromJsonToUserStore(json: UserStoreJson): UserStore {
            return UserStore.fromJson(json);
        }
    }
