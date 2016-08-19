import {Equitable} from "../Equitable";
import {ObjectHelper} from "../ObjectHelper";
import {StringHelper} from "../util/StringHelper";
import {Reference} from "../util/Reference";

export class ContentId implements Equitable {

        private value: string;

        constructor(value: string) {
            if (!ContentId.isValidContentId(value)) {
                throw new Error("Invalid content id: " + value)
            }
            this.value = value;
        }

        toString(): string {
            return this.value;
        }

        equals(o: Equitable): boolean {

            if (!ObjectHelper.iFrameSafeInstanceOf(o, ContentId)) {
                return false;
            }

            var other = <ContentId>o;

            if (!ObjectHelper.stringEquals(this.value, other.value)) {
                return false;
            }

            return true;
        }

        static isValidContentId(id: string): boolean {
            return !StringHelper.isEmpty(id) && !StringHelper.isBlank(id);
        }

        static fromReference(reference: Reference) {
            return new ContentId(reference.getNodeId());
        }
    }
