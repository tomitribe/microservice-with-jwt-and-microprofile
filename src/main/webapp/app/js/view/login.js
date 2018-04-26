/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

(function () {
    'use strict';

    var deps = ['app/js/templates', 'lib/underscore', 'app/js/tools/i18n', 'lib/backbone', 'app/js/tools/alert.view'];
    define(deps, function (templates, underscore, i18n, Backbone, AlertView) {
        var View = Backbone.View.extend({
            initialize: function(options){
                this.options = options || {};
            },
            tagName: 'div',
            className: 'ux-login',
            filterOption: 'title',
            events: {
                'submit .form-login': function (evt) {
                    evt.preventDefault();
                    var me = this,
                        creds = $(evt.target).serialize(),
                        router = window.BackboneApp.getRouter();
                    window.auth.login(creds).then(
                        function () {
                            router.navigate('main/1', {
                                trigger: true
                            });
                        }
                    ).catch(
                        function (e) {
                            AlertView.show('Failed', e['responseJSON']['error_description'], 'danger');
                        }
                    );
                }
            },
            render: function () {
                var me = this;
                if (!this.options.isRendered) {
                    me.$el.empty();
                    me.$el.append(templates.getValue('login', {}));
                    me.options.isRendered = true;
                }
                return this;
            }
        });
        return new View();
    });
}());
