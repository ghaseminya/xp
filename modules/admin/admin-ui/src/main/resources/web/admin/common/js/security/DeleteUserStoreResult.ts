import {DeleteUserStoreResultJson} from "./DeleteUserStoreResultJson";
import {UserStoreKey} from "./UserStoreKey";

export class DeleteUserStoreResult {

        private userStoreKey: UserStoreKey;
        private deleted: boolean;
        private reason: string;

        constructor() {
        }

        getUserStoreKey(): UserStoreKey {
            return this.userStoreKey;
        }

        isDeleted(): boolean {
            return this.deleted;
        }

        getReason(): string {
            return this.reason;
        }

        static fromJson(json: DeleteUserStoreResultJson): DeleteUserStoreResult {
            var result = new DeleteUserStoreResult();
            result.userStoreKey = UserStoreKey.fromString(json.userStoreKey);
            result.deleted = json.deleted;
            result.reason = json.reason;
            return result;
        }
    }

