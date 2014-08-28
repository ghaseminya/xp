/**
 * @module view/xslt
 */

/**
 * Render using xslt stylesheet.
 *
 * @param {Object} xslt resolved xslt stylesheet
 * @param {String} doc xml input document
 * @param {Object} params xslt parameters
 */
exports.render = function (xslt, doc, params) {
    var helper = __('xsltScriptHelper');

    var request = helper.newRenderParams();
    request.view(xslt);
    request.inputXml(doc);
    request.parameters(params);

    return helper.processor.render(request);
};
