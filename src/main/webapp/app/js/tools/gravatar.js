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
 "use strict";
 */

define(['lib/handlebars', 'lib/crypto'], function (Handlebars, CryptoJS) {
    'use strict';

    function gravatar(emailStr) {
        const gr = (emailStr || '').trim();
        if (!gr || !gr.length) return "//www.gravatar.com/avatar/?s=90";
        return "//www.gravatar.com/avatar/" + CryptoJS.MD5(gr).toString() + "?s=90&d=retro"
    }

    Handlebars.registerHelper("gravatar", gravatar);

    return {
        gravatar: gravatar
    };
});
