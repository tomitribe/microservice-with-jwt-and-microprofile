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

    var deps = ['lib/underscore', 'backbone', 'jwt_decode', 'app/js/model/login', 'lib/moment', 'lib/backbone-localstorage'];
    define(deps, function (_, Backbone, jwtDecode, LoginModel, moment) {
        var AuthModel = Backbone.Model.extend({
            id: 'ux.auth',
            localStorage: new Store('ux.auth'),
            defaults: {
                auth: false,
                username: '',
                email: '',
                groups: '',

                access_token: '',
                access_exp: '',

                token_type: '',
                expires_in: '',

                refresh_token: '',
                refresh_exp: ''
            },
            loginModel: null,
            initialize: function () {
                var me = this;
                me.loginModel = new LoginModel();
                $.ajaxSetup({
                    beforeSend: function ( jqXHR ) {
                        var access_token = me.get('access_token'), token_type = me.get('token_type') + " ";
                        if (typeof access_token !== 'undefined' && !!access_token) {
                            jqXHR.setRequestHeader('Authorization', token_type + access_token);
                        }
                    }
                });
            },
            login: function(creds) {
                var me = this;
                return new Promise( function (res, rej) {
                    me.loginModel.getAccess(creds)
                        .then(function (resp) {
                            me.parseResp(resp);
                            me.save();
                            me.getAuth().then(res).catch(rej);
                        })
                        .catch(rej);
                })
            },
            logout: function() {
                var me = this;
                return new Promise( function (res, rej) {
                    me.parseResp();
                    me.save();
                    res(!me.get('auth'));
                });
            },
            refresh: function() {
                var me = this;
                return new Promise( function (res, rej) {
                    const rt = me.get('refresh_token');
                    if (!rt) return rej('no token to refresh');
                    me.loginModel.getRefresh(rt)
                        .then(function (resp) {
                            me.parseResp(resp);
                            me.save();
                            me.getAuth().then(res).catch(rej);
                        })
                        .catch(rej);
                })
            },
            parseResp: function(resp) {
                var me = this;
                var access_token = resp && resp['access_token'] && jwtDecode(resp['access_token']);
                var refresh_token = resp && resp['refresh_token'] && jwtDecode(resp['refresh_token']);
                if (resp && resp['access_token'] && access_token) {
                    me.set({
                        auth: true,
                        username: access_token['username'],
                        email: access_token['email'],
                        groups: access_token['groups'],

                        access_token: resp['access_token'],
                        access_exp: moment.unix(access_token.exp).valueOf(),

                        token_type: resp['token_type'],
                        expires_in: resp['expires_in'],

                        refresh_token: resp['refresh_token'],
                        refresh_exp: moment.unix(refresh_token.exp).valueOf()
                    });
                } else {
                    me.set({
                        auth: false,
                        username: '',
                        email: '',
                        groups: '',

                        access_token: '',
                        access_exp: '',

                        token_type: '',
                        expires_in: '',

                        refresh_token: '',
                        refresh_exp: ''
                    });
                }
            },
            getAuth: function() {
                var me = this;
                return new Promise(function(res, rej) {
                    me.get('auth') ? res() : rej();
                })
            }
        });
        return AuthModel;

    });
}());
