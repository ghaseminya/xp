import {Equitable} from "../../Equitable";
import {ObjectHelper} from "../../ObjectHelper";
import {Mixin} from "./Mixin";
import {MixinName} from "./MixinName";

export class MixinNames implements Equitable {

        private array: MixinName[];

        constructor(array: MixinName[]) {
            this.array = [];
            array.forEach((mixinName: MixinName) => {

                var duplicate = this.array.some((possibleDuplicate: MixinName) => {
                    return mixinName.equals(possibleDuplicate);
                });

                if (!duplicate) {
                    this.array.push(mixinName);
                }
                else {
                    throw Error("MixinNames do not allow duplicates, found: '" + mixinName.toString() + "'");
                }
            });
        }

        forEach(callback: (mixinName: MixinName, index?: number) => void) {
            this.array.forEach((mixinName: MixinName, index: number) => {
                callback(mixinName, index);
            });
        }

        contains(mixinName: MixinName): boolean {
            var containName = this.array.some((curMixin: MixinName) => {
                return curMixin.equals(mixinName);
            });
            return !!containName;
        }

        filter(callbackfn: (value: MixinName, index?: number) => boolean): MixinNames {
            return new MixinNames(this.array.filter((value: MixinName, index: number) => {
                return callbackfn(value, index);
            }));
        }

        map<U>(callbackfn: (value: MixinName, index?: number) => U): U[] {
            return this.array.map((value: MixinName, index: number) => {
                return callbackfn(value, index);
            });
        }

        equals(o: Equitable): boolean {
            if (!ObjectHelper.iFrameSafeInstanceOf(o, MixinNames)) {
                return false;
            }

            var other = <MixinNames>o;
            return ObjectHelper.arrayEquals(this.array, other.array);
        }

        static create(): MixinNamesBuilder {
            return new MixinNamesBuilder();
        }
    }

    export class MixinNamesBuilder {

        array: MixinName[] = [];

        fromStrings(values: string[]): MixinNamesBuilder {
            if (!!values) {
                values.forEach((value: string) => {
                    this.addMixinName(new MixinName(value));
                });
            }
            return this;
        }

        fromMixins(mixins: Mixin[]): MixinNamesBuilder {
            if (!!mixins) {
                mixins.forEach((mixin: Mixin) => {
                    this.addMixinName(mixin.getMixinName());
                });
            }
            return this;
        }

        addMixinName(value: MixinName): MixinNamesBuilder {
            this.array.push(value);
            return this;
        }

        build(): MixinNames {
            return new MixinNames(this.array);
        }
    }
