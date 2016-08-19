import {Action} from "../../ui/Action";
import {App} from "../Application";
import {ShowAppLauncherEvent} from "../ShowAppLauncherEvent";

export class ShowAppLauncherAction extends Action {

        constructor(application: App) {
            super('Start', 'mod+esc', true);

            this.onExecuted(() => {
                new ShowAppLauncherEvent(application).fire(window.parent);
                new ShowAppLauncherEvent(application).fire();
            });
        }
    }
