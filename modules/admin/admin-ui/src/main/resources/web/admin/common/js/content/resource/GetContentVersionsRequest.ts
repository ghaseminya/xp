module api.content.resource {

    import GetContentVersionsResultsJson = api.content.json.GetContentVersionsResultsJson;

    export class GetContentVersionsRequest extends ContentResourceRequest<GetContentVersionsResultsJson, ContentVersion[]> {

        private contentId: ContentId;
        private from: number;
        private size: number;

        constructor(contentId: ContentId) {
            super();
            super.setMethod('POST');
            this.contentId = contentId;
        }

        setFrom(from: number): GetContentVersionsRequest {
            this.from = from;
            return this;
        }

        setSize(size: number): GetContentVersionsRequest {
            this.size = size;
            return this;
        }

        getParams(): Object {
            return {
                contentId: this.contentId.toString(),
                from: this.from,
                size: this.size
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'getVersions');
        }

        sendAndParse(): wemQ.Promise<ContentVersion[]> {

            return this.send().then((response: api.rest.JsonResponse<GetContentVersionsResultsJson>) => {
                return this.fromJsonToContentVersions(response.getResult().contentVersions);
            });
        }

        private fromJsonToContentVersions(json: api.content.json.ContentVersionJson[]): ContentVersion[] {

            let contentVersions: ContentVersion[] = [];
            json.forEach((contentVersionJson: api.content.json.ContentVersionJson) => {
                contentVersions.push(ContentVersion.fromJson(contentVersionJson));
            });

            return contentVersions;
        }

    }
}
