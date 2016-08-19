import {ContentSummary} from "../../../ContentSummary";
import {UploadItem} from "../../../../ui/uploader/UploadItem";
import {ContentId} from "../../../ContentId";
import {ContentIconUrlResolver} from "../../../util/ContentIconUrlResolver";

export class ImageSelectorDisplayValue {

        private uploadItem: UploadItem<ContentSummary>;

        private content: ContentSummary;

        private empty: boolean;

        constructor() {
        }

        static fromUploadItem(item: UploadItem<ContentSummary>): ImageSelectorDisplayValue {
            return new ImageSelectorDisplayValue().setUploadItem(item);
        }

        static fromContentSummary(content: ContentSummary) {
            return new ImageSelectorDisplayValue().setContentSummary(content);
        }

        static makeEmpty() {
            return new ImageSelectorDisplayValue().setEmpty(true);
        }

        isEmptyContent(): boolean {
            return this.empty;
        }

        setEmpty(value: boolean): ImageSelectorDisplayValue {
            this.empty = value;
            return this;
        }

        setUploadItem(item: UploadItem<ContentSummary>): ImageSelectorDisplayValue {
            this.uploadItem = item;
            return this;
        }

        setContentSummary(contentSummary: ContentSummary): ImageSelectorDisplayValue {
            this.content = contentSummary;
            return this;
        }

        getUploadItem(): UploadItem<ContentSummary> {
            return this.uploadItem;
        }

        getContentSummary(): ContentSummary {
            return this.content;
        }

        getId(): string {
            return this.content ? this.content.getId() : this.uploadItem.getId();
        }

        getContentId(): ContentId {
            return this.content ? this.content.getContentId() : null;
        }

        getImageUrl(): string {
            return this.content ? new ContentIconUrlResolver().setContent(this.content).resolve() : null;
        }

        getLabel(): string {
            return this.content ? this.content.getName().toString() : this.uploadItem.getName();
        }

        getDisplayName(): string {
            return this.content ? this.content.getDisplayName() : null;
        }

        getTypeLocaleName(): string {
            return (this.content && this.content.getType()) ? this.content.getType().getLocalName() : null;
        }

        getPath(): string {
            return this.content ? this.content.getPath().toString() : null;
        }

    }
