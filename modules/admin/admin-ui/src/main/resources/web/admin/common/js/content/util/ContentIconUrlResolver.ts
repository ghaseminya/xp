import {IconUrlResolver} from "../../icon/IconUrlResolver";
import {UriHelper} from "../../util/UriHelper";
import {ContentSummary} from "../ContentSummary";

export class ContentIconUrlResolver extends IconUrlResolver {

        private content: ContentSummary;

        private crop: boolean;

        setContent(value: ContentSummary): ContentIconUrlResolver {
            this.content = value;
            return this;
        }

        resolve(): string {

            var url = this.content.getIconUrl();
            if (!url) {
                return null;
            }
            // CMS-4677: using crop=false for images only by default
            if (this.crop == undefined) {
                this.crop = !this.content.isImage();
            }

            url = this.appendParam("crop", this.crop.toString(), url);
            return url;
        }

        static default(): string {

            return UriHelper.getAdminUri("common/images/default_content.png");
        }
    }
