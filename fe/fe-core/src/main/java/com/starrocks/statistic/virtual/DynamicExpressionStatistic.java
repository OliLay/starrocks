// Copyright 2021-present StarRocks, Inc. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.starrocks.statistic.virtual;

import com.starrocks.qe.ConnectContext;
import com.starrocks.type.PrimitiveType;
import com.starrocks.type.Type;

import java.util.Set;

/**
 * A dynamically parameterized VirtualStatistic for arbitrary scalar expressions.
 * One instance per registered expression (e.g. upper, lower, date_trunc).
 */
public class DynamicExpressionStatistic implements VirtualStatistic {

    private final String name;
    private final String exprTemplate;
    private final Type resultType;
    private final Set<PrimitiveType> applicableBaseTypes;
    private final boolean requiresLateral;

    public DynamicExpressionStatistic(String name, String exprTemplate, Type resultType,
                                      Set<PrimitiveType> applicableBaseTypes, boolean requiresLateral) {
        this.name = name;
        this.exprTemplate = exprTemplate;
        this.resultType = resultType;
        this.applicableBaseTypes = applicableBaseTypes;
        this.requiresLateral = requiresLateral;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAnalyzePropertyKey() {
        return name.toLowerCase() + "_virtual_statistics";
    }

    @Override
    public boolean appliesTo(Type columnType) {
        return applicableBaseTypes.contains(columnType.getPrimitiveType());
    }

    @Override
    public Type getVirtualExpressionType(Type sourceType) {
        return resultType;
    }

    @Override
    public String getVirtualExpression(String columnName) {
        return String.format(exprTemplate, columnName);
    }

    @Override
    public boolean isQueryingEnabled() {
        return ConnectContext.get() != null;
    }

    @Override
    public boolean requiresLateralJoin() {
        return requiresLateral;
    }
}

