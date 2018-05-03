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

define(['lib/handlebars', 'lib/moment'], function (Handlebars, moment) {
    'use strict';

    function formatTime(dateStr) {
        var momentDate = moment(java2jsDate(dateStr)),
            nowDate = moment(),
            days = nowDate.diff(momentDate, 'days');
        if (days < 4) {
            return momentDate.fromNow();
        }
        else if(nowDate.year() === momentDate.year()){
           return momentDate.format('DD MMMM');
        } else {
            return momentDate.format('DD MMMM YYYY');
        }
    }

    function java2jsDate(dateStr) {
        return moment(dateStr, "YYYYMMDDHHmmssZZ").valueOf();
    }

    function java2jsString(dateStr) {
        return moment(dateStr, "YYYYMMDDHHmmssZZ").toString();
    }

    function java2jsISO(dateStr) {
        return moment(dateStr, "YYYYMMDDHHmmssZZ").toISOString();
    }

    Handlebars.registerHelper("formatTime", formatTime);
    Handlebars.registerHelper("java2jsString", java2jsString);
    Handlebars.registerHelper("java2jsISO", java2jsISO);
    Handlebars.registerHelper("java2jsDate", java2jsDate);

    return {
        formatTime: formatTime,
        java2jsString: java2jsString,
        java2jsISO: java2jsISO,
        java2jsDate: java2jsDate
    };
});
