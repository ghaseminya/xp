import {LiEl} from "../../dom/LiEl";
import {Action} from "../Action";

export class ActionMenuItem extends LiEl {

        private action: Action;

        constructor(action: Action) {

            super("action");
            this.action = action;

            this.getEl().setInnerHtml(this.action.getLabel());

            this.action.onPropertyChanged(() => {
                if (this.action.isEnabled()) {
                    this.show();
                }
                else if (!this.action.isEnabled()) {
                    this.hide();
                }
            });

            this.onClicked(() => {
                this.action.execute();
            });
        }
    }
