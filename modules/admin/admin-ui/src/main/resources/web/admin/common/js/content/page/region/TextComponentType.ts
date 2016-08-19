import {ComponentType} from "./ComponentType";
import {TextComponentBuilder} from "./TextComponent";

export class TextComponentType extends ComponentType {

        private static INSTANCE = new TextComponentType();

        constructor() {
            super("text");
        }

        newComponentBuilder(): TextComponentBuilder {
            return new TextComponentBuilder();
        }

        public static get(): TextComponentType {
            return TextComponentType.INSTANCE;
        }
    }

