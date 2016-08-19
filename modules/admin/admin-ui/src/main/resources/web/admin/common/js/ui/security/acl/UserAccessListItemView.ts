import {Principal} from "../../../security/Principal";
import {Tooltip} from "../../Tooltip";
import {User} from "../../../security/User";
import {Viewer} from "../../Viewer";
import {DivEl} from "../../../dom/DivEl";
import {ResponsiveItem} from "../../responsive/ResponsiveItem";
import {SpanEl} from "../../../dom/SpanEl";
import {ResponsiveManager} from "../../responsive/ResponsiveManager";
import {Access} from "./Access";
import {EffectivePermission} from "./EffectivePermission";
import {EffectivePermissionMember} from "./EffectivePermissionMember";

export class UserAccessListItemView extends Viewer<EffectivePermission> {

        private userLine: DivEl;

        private accessLine: DivEl;

        private resizeListener: (item: ResponsiveItem) => void;

        private currentUser: User;

        private static OPTIONS: any[] = [
            {value: Access.FULL, name: 'Full Access'},
            {value: Access.PUBLISH, name: 'Can Publish'},
            {value: Access.WRITE, name: 'Can Write'},
            {value: Access.READ, name: 'Can Read'},
            {value: Access.CUSTOM, name: 'Custom...'}
        ];

        public static debug: boolean = false;

        constructor(className?: string) {
            super('user-access-list-item-view' + (className ? " " + className : ""));
        }

        setCurrentUser(user: User) {
            this.currentUser = user;
        }

        doLayout(object: EffectivePermission) {
            super.doLayout(object);

            if (UserAccessListItemView.debug) {
                console.debug("UserAccessListItemView.doLayout");
            }

            if (!this.accessLine && !this.userLine) {
                this.accessLine = new SpanEl("access-line");
                this.userLine = new DivEl("user-line");
                this.appendChildren(this.accessLine, this.userLine);

                this.resizeListener = this.setExtraCount.bind(this);
                ResponsiveManager.onAvailableSizeChanged(this, this.resizeListener);

                this.userLine.onRendered(() => {
                    this.setExtraCount();
                });
            }

            if (object) {
                this.accessLine.setHtml(this.getOptionName(object.getAccess()));

                object.getMembers().forEach((principal: EffectivePermissionMember) => {

                    var display = principal.getDisplayName().split(" ").map(word => word.substring(0, 1).toUpperCase());

                    var icon = new SpanEl("user-icon").setHtml(display.length >= 2
                        ? display.join("").substring(0, 2)
                        : principal.getDisplayName().substring(0, 2).toUpperCase());
                    if (this.currentUser && this.currentUser.getKey().equals(principal.getUserKey())) {
                        icon.addClass("active");
                        this.userLine.insertChild(icon, 0);
                    } else {
                        this.userLine.appendChild(icon);
                    }
                    new Tooltip(icon, principal.getDisplayName(), 200).setMode(Tooltip.MODE_GLOBAL_STATIC);
                });
            }
        }

        remove(): any {
            ResponsiveManager.unAvailableSizeChanged(this);
            return super.remove();
        }

        private setExtraCount() {
            if (this.userLine.getChildren().length > 0) {
                var visibleCount = this.getVisibleCount(),
                    iconCount = this.getObject().getPermissionAccess().getCount(),
                    extraCount = iconCount - visibleCount;

                if (extraCount > 0) {
                    this.userLine.getEl().setAttribute("extra-count", "+" + extraCount);
                } else {
                    this.userLine.getEl().removeAttribute("extra-count");
                }
            }
        }

        private getVisibleCount(): number {
            var userIcons = this.userLine.getChildren(),
                count = 0;
            for (var userIconKey in userIcons) {
                if (userIcons[userIconKey].getEl().getOffsetTopRelativeToParent() == 0) {
                    count++;
                } else {
                    break;
                }
            }
            return count;
        }

        private getOptionName(access: Access): string {
            var currentOption = UserAccessListItemView.OPTIONS.filter(option => {
                return option.value == access;
            });
            if (currentOption && currentOption.length > 0) {
                return currentOption[0].name;
            }

        }


    }
