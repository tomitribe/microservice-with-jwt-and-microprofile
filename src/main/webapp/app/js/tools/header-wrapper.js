(function () {
    'use strict';
    function wrapXHR(xhr){
        const proto = (xhr.prototype||xhr);
        if(!proto.wrappedSetRequestHeader) {
            proto.wrappedSetRequestHeader = proto.setRequestHeader;
            proto.setRequestHeader = function (header, value) {
                this.wrappedSetRequestHeader(header, value);
                if (!this.requestHeaders) {
                    this.requestHeaders = {};
                } else if (!this.requestHeaders[header]) {
                    this.requestHeaders[header] = [value];
                }else {
                    this.requestHeaders[header].push(value);
                }
            };
            return {headerWrapped: true, xhr};
        } else return {headerWrapped: false, xhr};
    }
    define([], function () {
        return {wrapXHR};
    })
})();
