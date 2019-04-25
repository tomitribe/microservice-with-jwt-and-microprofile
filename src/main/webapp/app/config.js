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

var APP_CONFIG = {
    baseUrl: window.ux.ROOT_URL,
    paths: {
        'text': 'webjars/requirejs-text/2.0.15/text',
        'lib/less': 'webjars/less/2.7.2/less.min',
        'lib/jquery': 'webjars/jquery/3.3.1/jquery.min',
        'lib/bootstrap': 'webjars/bootstrap/4.1.0/js/bootstrap.bundle.min',
        'lib/handlebars': 'webjars/handlebars/4.0.6/handlebars.min',
        'lib/underscore': 'webjars/underscorejs/1.8.3/underscore-min',
        'lib/json2': 'webjars/json2/20140204/json2.min',
        'backbone': 'webjars/backbonejs/1.3.3/backbone',
        'jwt_decode': 'webjars/jwt-decode/2.2.0/build/jwt-decode.min',
        'jwk-js': 'app/js/tools/jwk-js.min',
        'http-signatures-js': 'app/js/tools/http-signatures-js.min',
        'lib/crypto': 'webjars/crypto-js/3.1.9-1/crypto-js',
        'lib/moment': 'webjars/momentjs/2.22.1/moment',
        'lib/backbone-localstorage': 'webjars/backbone-localstorage/1.1.16/backbone.localStorage-min'
    },
    shim: {
        'lib/bootstrap': {
            deps: ['lib/jquery']
        },
        'lib/underscore': {
            exports: '_'
        },
        'backbone': {
            deps: ['lib/jquery', 'lib/json2', 'lib/underscore']
        },
        'app/js/templates': {
            deps: ['lib/underscore', 'app/js/tools/i18n']
        },
        'lib/backbone-localstorage': {
            deps: ['backbone'],
            exports: 'Store'
        }
    }
};
