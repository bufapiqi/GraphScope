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
package com.alibaba.graphscope.groot.store;

import com.alibaba.graphscope.groot.operation.OperationBatch;
import com.alibaba.graphscope.groot.store.external.ExternalStorage;
import com.alibaba.graphscope.proto.groot.GraphDefPb;

import java.io.Closeable;
import java.io.IOException;

public interface GraphPartition extends Closeable {

    /**
     * @param snapshotId
     * @param operationBatch
     * @return True if batch has DDL operation
     * @throws IOException
     */
    boolean writeBatch(long snapshotId, OperationBatch operationBatch) throws IOException;

    GraphDefPb getGraphDefBlob() throws IOException;

    void ingestExternalFile(ExternalStorage storage, String fullPath) throws IOException;

    GraphPartitionBackup openBackupEngine();

    int getId();

    void garbageCollect(long snapshotId) throws IOException;

    void tryCatchUpWithPrimary() throws IOException;

    void reopenSecondary() throws IOException;
}
