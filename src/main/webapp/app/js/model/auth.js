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

    var deps = ['lib/underscore', 'lib/backbone'];
    define(deps, function (_, Backbone) {
        var SessionModel = Backbone.Model.extend({
            urlRoot: '/session',
            initialize: function () {
                var that = this;
                $.ajaxPrefilter( function( options, originalOptions, jqXHR ) {
                    if(typeof that.get('_auToken') !== 'undefined') {
                        jqXHR.setRequestHeader('Authorization', that.get('_auToken'));
                    }
                });
            },
            login: function(creds) {
                this.save(creds, {
                    success: function () {}
                });
            },
            logout: function() {
                var that = this;
                this.destroy({
                    success: function (model, resp) {
                        model.clear();
                        model.id = null;
                        that.set({auth: false, _auToken: resp._auToken});
                    }
                });
            },
            getAuth: function() {
                return new Promise(function(res, rej) {
                    /*this.fetch({
                        success: res,
                        failure: rej
                    })*/
                    // TODO: finish AUTH from token;
                    res();
                })
            }
        });
        return new SessionModel();

    });
}());
