/*
 * Copyright (c) 2017 original authors and authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dsngroup.broke.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The InMemoryPool class, used as singleton class.
 */
public class InMemoryPool {

    // TODO: Assignable pool size
    // TODO: Extends this to be offset based data structure.
    // TODO: May consider to replace ConcurrentHashMap into more performant data structure.
    private static Map<String, String> inMemoryPool = new ConcurrentHashMap<>();

    /**
     * Insert storage on a specific topic.
     * @param topic the topic(key) of the broker.
     * @param content the storage of the associated topic.
     */
    public static void putContentOnTopic(String topic, String content) {
        // Ignore the return value
        inMemoryPool.put(topic, content);
    }

    /**
     * Get storage from a specific topic.
     * @param topic the topic(key) of the broker.
     * @return the storage of the associated topic.
     */
    public static String getContentFromTopic(String topic) {
        return inMemoryPool.get(topic);
    }

    private InMemoryPool() {}
}