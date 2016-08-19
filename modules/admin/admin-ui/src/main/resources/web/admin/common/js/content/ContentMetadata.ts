import {Equitable} from "../Equitable";
import {ObjectHelper} from "../ObjectHelper";

export class ContentMetadata implements Equitable {

        private hits: number;

        private totalHits: number;

        constructor(hits: number, totalHits: number) {
            this.hits = hits;
            this.totalHits = totalHits;
        }

        getHits(): number {
            return this.hits;
        }

        getTotalHits(): number {
            return this.totalHits;
        }

        setHits(hits: number) {
            this.hits = hits;
        }

        setTotalHits(totalHits: number) {
            this.totalHits = totalHits;
        }

        equals(o: Equitable): boolean {
            if (!ObjectHelper.iFrameSafeInstanceOf(o, ContentMetadata)) {
                return false;
            }

            var other = <ContentMetadata>o;

            if (this.hits !== other.hits ||
                this.totalHits !== other.totalHits) {

                return false;
            }

            return true;
        }
    }
