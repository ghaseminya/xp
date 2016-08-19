import {User} from "../../../../../common/js/security/User";
import {UserBuilder} from "../../../../../common/js/security/User";
import {CreateUserRequest} from "../../../../../common/js/security/CreateUserRequest";
import {UpdateUserRequest} from "../../../../../common/js/security/UpdateUserRequest";
import {Principal} from "../../../../../common/js/security/Principal";
import {PrincipalKey} from "../../../../../common/js/security/PrincipalKey";
import {UserStoreKey} from "../../../../../common/js/security/UserStoreKey";
import {GetPrincipalByKeyRequest} from "../../../../../common/js/security/GetPrincipalByKeyRequest";
import {ConfirmationDialog} from "../../../../../common/js/ui/dialog/ConfirmationDialog";
import {WizardStep} from "../../../../../common/js/app/wizard/WizardStep";
import {UserItemCreatedEvent} from "../../../../../common/js/security/UserItemCreatedEvent";
import {showFeedback} from "../../../../../common/js/notify/MessageBus";
import {UserItemUpdatedEvent} from "../../../../../common/js/security/UserItemUpdatedEvent";
import {ObjectHelper} from "../../../../../common/js/ObjectHelper";
import {StringHelper} from "../../../../../common/js/util/StringHelper";
import {showError} from "../../../../../common/js/notify/MessageBus";

import {PrincipalWizardPanel} from "./PrincipalWizardPanel";
import {UserEmailWizardStepForm} from "./UserEmailWizardStepForm";
import {UserPasswordWizardStepForm} from "./UserPasswordWizardStepForm";
import {UserMembershipsWizardStepForm} from "./UserMembershipsWizardStepForm";
import {PrincipalWizardPanelParams} from "./PrincipalWizardPanelParams";

export class UserWizardPanel extends PrincipalWizardPanel {

    private userEmailWizardStepForm: UserEmailWizardStepForm;
    private userPasswordWizardStepForm: UserPasswordWizardStepForm;
    private userMembershipsWizardStepForm: UserMembershipsWizardStepForm;

    constructor(params: PrincipalWizardPanelParams) {

        super(params);

        this.addClass("user-wizard-panel");
    }

    saveChanges(): wemQ.Promise<Principal> {
        if (!this.isRendered() ||
            (this.userEmailWizardStepForm.isValid()
             && (this.getPersistedItem() || this.userPasswordWizardStepForm.isValid()))) {

            return super.saveChanges();
        } else {
            this.showErrors();

            return wemQ<Principal>(null);
        }
    }

    createSteps(principal?: Principal): WizardStep[] {
        var steps: WizardStep[] = [];

        this.userEmailWizardStepForm = new UserEmailWizardStepForm(this.principalParams.userStore.getKey());
        this.userPasswordWizardStepForm = new UserPasswordWizardStepForm();
        this.userMembershipsWizardStepForm = new UserMembershipsWizardStepForm();

        steps.push(new WizardStep("User", this.userEmailWizardStepForm));
        steps.push(new WizardStep("Authentication", this.userPasswordWizardStepForm));
        steps.push(new WizardStep("Groups & Roles", this.userMembershipsWizardStepForm));

        return steps;
    }

    doLayout(persistedPrincipal: Principal): wemQ.Promise<void> {

        return super.doLayout(persistedPrincipal).then(() => {

            if (this.isRendered()) {

                var viewedPrincipal = this.assembleViewedItem();
                if (!this.isPersistedEqualsViewed()) {

                    console.warn("Received Principal from server differs from what's viewed:");
                    console.warn(" viewedPrincipal: ", viewedPrincipal);
                    console.warn(" persistedPrincipal: ", persistedPrincipal);

                    ConfirmationDialog.get().setQuestion(
                        "Received Principal from server differs from what you have. Would you like to load changes from server?").setYesCallback(
                        () => this.doLayoutPersistedItem(persistedPrincipal.clone())).setNoCallback(() => {/* Do nothing */
                    }).show();
                }

                return wemQ<void>(null);
            } else {
                return this.doLayoutPersistedItem(persistedPrincipal ? persistedPrincipal.clone() : null);
            }

        });
    }

    protected doLayoutPersistedItem(principal: Principal): wemQ.Promise<void> {

        return super.doLayoutPersistedItem(principal).then(() => {
            if (!!principal) {
                this.userEmailWizardStepForm.layout(principal);
                this.userPasswordWizardStepForm.layout(principal);
                this.userMembershipsWizardStepForm.layout(principal);
            }
        });
    }

    persistNewItem(): wemQ.Promise<Principal> {
        return this.produceCreateUserRequest().sendAndParse().then((principal: Principal) => {

            new UserItemCreatedEvent(principal, this.getUserStore(), this.isParentOfSameType()).fire();

            showFeedback('User was created!');
            this.notifyPrincipalNamed(principal);

            return principal;
        });
    }

    produceCreateUserRequest(): CreateUserRequest {
        var wizardHeader = this.getWizardHeader();
        var login = wizardHeader.getName(),
            key = PrincipalKey.ofUser(this.getUserStore().getKey(), login),
            name = wizardHeader.getDisplayName(),
            email = this.userEmailWizardStepForm.getEmail(),
            password = this.userPasswordWizardStepForm.getPassword(),
            memberships = this.userMembershipsWizardStepForm.getMemberships().map((el) => {
                return el.getKey();
            });
        return new CreateUserRequest()
            .setKey(key)
            .setDisplayName(name)
            .setEmail(email)
            .setLogin(login)
            .setPassword(password)
            .setMemberships(memberships);
    }

    updatePersistedItem(): wemQ.Promise<Principal> {
        return this.produceUpdateUserRequest(this.assembleViewedItem()).sendAndParse().then((principal: Principal) => {
            if (!this.getPersistedItem().getDisplayName() && !!principal.getDisplayName()) {
                this.notifyPrincipalNamed(principal);
            }
            this.userEmailWizardStepForm.layout(principal);
            showFeedback('User was updated!');
            new UserItemUpdatedEvent(principal, this.getUserStore()).fire();

            return principal;
        });
    }

    produceUpdateUserRequest(viewedPrincipal: Principal): UpdateUserRequest {
        var user = viewedPrincipal.asUser(),
            key = user.getKey(),
            displayName = user.getDisplayName(),
            email = user.getEmail(),
            login = user.getLogin(),
            oldMemberships = this.getPersistedItem().asUser().getMemberships().map((el) => {
                return el.getKey();
            }),
            oldMembershipsIds = oldMemberships.map((el) => {
                return el.getId();
            }),
            newMemberships = user.getMemberships().map((el) => {
                return el.getKey();
            }),
            newMembershipsIds = newMemberships.map((el) => {
                return el.getId();
            }),
            addMemberships = newMemberships.filter((el) => {
                return oldMembershipsIds.indexOf(el.getId()) < 0;
            }),
            removeMemberships = oldMemberships.filter((el) => {
                return newMembershipsIds.indexOf(el.getId()) < 0;
            });

        return new UpdateUserRequest().setKey(key).setDisplayName(displayName).setEmail(email).setLogin(login).addMemberships(
            addMemberships).removeMemberships(removeMemberships);
    }

    assembleViewedItem(): Principal {
        let wizardHeader = this.getWizardHeader();
        return new UserBuilder(!!this.getPersistedItem() ? this.getPersistedItem().asUser() : null).setEmail(
            this.userEmailWizardStepForm.getEmail()).setLogin(wizardHeader.getName()).setMemberships(
            this.userMembershipsWizardStepForm.getMemberships()).setDisplayName(
            wizardHeader.getDisplayName()).// setDisabled().
        build();
    }

    isPersistedEqualsViewed(): boolean {
        var persistedPrincipal = this.getPersistedItem().asUser();
        var viewedPrincipal = this.assembleViewedItem().asUser();
        // Group/User order can be different for viewed and persisted principal
        viewedPrincipal.getMemberships().sort((a, b) => {
            return a.getKey().getId().localeCompare(b.getKey().getId());
        });
        persistedPrincipal.getMemberships().sort((a, b) => {
            return a.getKey().getId().localeCompare(b.getKey().getId());
        });

        // #hack - The newly added members will have different modifiedData
        var viewedMembershipsKeys = viewedPrincipal.getMemberships().map((el) => {
                return el.getKey()
            }),
            persistedMembershipsKeys = persistedPrincipal.getMemberships().map((el) => {
                return el.getKey()
            });

        if (ObjectHelper.arrayEquals(viewedMembershipsKeys, persistedMembershipsKeys)) {
            viewedPrincipal.setMemberships(persistedPrincipal.getMemberships());
        }

        return viewedPrincipal.equals(persistedPrincipal);
    }

    hasUnsavedChanges(): boolean {
        var persistedPrincipal = this.getPersistedItem(),
            email = this.userEmailWizardStepForm.getEmail(),
            memberships = this.userMembershipsWizardStepForm.getMemberships();
        if (persistedPrincipal == undefined) {
            let wizardHeader = this.getWizardHeader();
            return wizardHeader.getName() !== "" ||
                   wizardHeader.getDisplayName() !== "" ||
                   (!!email && email !== "") ||
                   (!!memberships && memberships.length !== 0);
        } else {
            return !this.isPersistedEqualsViewed();
        }
    }

    private showErrors() {
        if (!this.userEmailWizardStepForm.isValid()) {
            this.showEmailErrors();
        }

        if (!(this.getPersistedItem() || this.userPasswordWizardStepForm.isValid())) {
            this.showPasswordErrors();
        }
    }

    private showEmailErrors() {
        var formEmail = this.userEmailWizardStepForm.getEmail();
        if (StringHelper.isEmpty(formEmail)) {
            showError("E-mail can not be empty.");
        } else if (!this.userEmailWizardStepForm.isValid()) {
            showError("E-mail is invalid.");
        }

    }

    private showPasswordErrors() {
        var password = this.userPasswordWizardStepForm.getPassword();
        if (StringHelper.isEmpty(password)) {
            showError("Password can not be empty.");
        } else if (!this.userEmailWizardStepForm.isValid()) {
            showError("Password is invalid.");
        }
    }
}
