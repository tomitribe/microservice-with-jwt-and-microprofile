/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.superbiz.moviefun;

import org.apache.johnzon.mapper.JohnzonProperty;

import java.beans.ConstructorProperties;
import java.io.Serializable;

public class TokenErrorResponse implements Serializable {
    @JohnzonProperty("error")
    private final String error;

    @JohnzonProperty("error_description")
    private final String errorDescription;

    @ConstructorProperties({"error", "error_description"})
    public TokenErrorResponse(final String error, final String errorDescription) {
        this.error = error;
        this.errorDescription = errorDescription;
    }

    @Override
    public String toString() {
        return "TokenErrorResponse{" +
                "error='" + error + '\'' +
                ", errorDescription='" + errorDescription + '\'' +
                '}';
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }
}