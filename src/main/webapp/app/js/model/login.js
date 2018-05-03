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

    var deps = ['lib/underscore', 'backbone'];
    define(deps, function (_, Backbone) {
        var LoginModel = Backbone.Model.extend({
            urlRoot: window.tokenHost || (window.ux.ROOT_URL + 'rest/token'),
            initialize: function () {
            },
            getAccess: function(creds) {
                console.log(creds);
                var me = this;
                return new Promise( function (res, rej) {
                    if(!creds || !creds.length) return rej({'responseJSON':{'error_description': 'Credentials are required'}});
                    $.ajax({
                        method: "POST",
                        url: me.urlRoot,
                        data: creds,
                        contentType: 'application/x-www-form-urlencoded'
                    })
                    .done(res)
                    .fail(rej);
                });
            },
            getRefresh: function(token) {
                var me = this;
                return new Promise( function (res, rej) {
                    if(!token || !token.length) return rej({'responseJSON':{'error_description': 'No token'}});
                    $.ajax({
                        method: "POST",
                        url: me.urlRoot,
                        data: $.param({
                            refresh_token: token,
                            type: 'oauth2',
                            grant_type: 'refresh_token'
                        }),
                        contentType: 'application/x-www-form-urlencoded'
                    })
                        .done(res)
                        .fail(rej);
                });
            }
        });
        return LoginModel;

    });
}());
