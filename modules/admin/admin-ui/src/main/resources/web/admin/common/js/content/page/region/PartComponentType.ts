import {ComponentType} from "./ComponentType";
import {PartComponentBuilder} from "./PartComponent";

export class PartComponentType extends ComponentType {

        private static INSTANCE = new PartComponentType();

        constructor() {
            super("part");
        }

        newComponentBuilder(): PartComponentBuilder {
            return new PartComponentBuilder();
        }

        public static get(): PartComponentType {
            return PartComponentType.INSTANCE;
        }
    }

