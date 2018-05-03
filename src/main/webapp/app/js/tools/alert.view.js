(function () {
    'use strict';

    var deps = ['app/js/templates', 'lib/underscore', 'app/js/tools/i18n', 'backbone'];
    define(deps, function (templates, _, i18n, Backbone) {
        var AlertView = Backbone.View.extend({
            tagName: "div",
            className: "alert",

            initialize: function (options) {
                this.options = Object.assign({
                    alert: "info",
                    title: "",
                    message: "",
                    fixed: false
                }, options);
            },

            render: function () {
                var that = this,
                    output = templates.getValue('alert', {
                        title: this.options.title,
                        message: this.options.message
                    });

                this.$el.addClass("alert-" + this.options.alert).html(output).alert();

                if (this.options.fixed) {
                    this.$el.addClass("fixed");
                }

                window.setTimeout(function () {
                    that.$el.addClass("in");
                }, 20);

                return this;
            },

            remove: function () {
                var that = this;

                this.$el.removeClass("in");

                window.setTimeout(function () {
                    that.$el.remove();
                }, 1000);
            }
        });

        AlertView.show = function (title, message, alertType, timeout) {
            var alert = new AlertView({
                alert: alertType,
                title: title,
                message: message,
                fixed: true
            });

            $('.ux-content-area').append(alert.render().$el);

            window.setTimeout(function () {
                alert.remove();
            }, timeout || 1500);

            return alert;
        };

        return AlertView;
    });
})();
