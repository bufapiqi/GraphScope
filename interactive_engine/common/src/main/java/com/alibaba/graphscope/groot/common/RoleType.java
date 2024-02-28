/**
 * Copyright 2020 Alibaba Group Holding Limited.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.graphscope.groot.common;

import java.util.HashMap;
import java.util.Map;

public enum RoleType {
    UNKNOWN("unknown"),
    FRONTEND("frontend"),
    STORE("store"),
    COORDINATOR("coordinator"),
    FRONTEND_SERVICE("frontend_service"),
    GAIA_ENGINE("gaia_engine"),
    GAIA_RPC("gaia_rpc");

    private final String name;

    RoleType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    private static final Map<String, RoleType> lookup = new HashMap<>();

    static {
        for (RoleType role : RoleType.values()) {
            lookup.put(role.getName(), role);
        }
    }

    public static RoleType fromName(String roleName) {
        RoleType roleType = lookup.get(roleName);
        if (roleType == null || roleType == RoleType.UNKNOWN) {
            throw new IllegalArgumentException("Unknown RoleType: [" + roleName + "]");
        }
        return roleType;
    }
}
