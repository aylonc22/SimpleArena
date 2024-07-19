package org.example;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ArenaPoolPolicy implements AutoCloseable {

    private final GenericObjectPool<Arena> objectPool;

    public ArenaPoolPolicy(int defaultSize) {
        objectPool = new GenericObjectPool<>(new ArenaFactory(defaultSize));
    }

    public Arena borrowObject() throws Exception {
        return objectPool.borrowObject();
    }

    public void returnObject(Arena arena) {
        objectPool.returnObject(arena);
    }

    @Override
    public void close() throws Exception {
        objectPool.close();
    }

    private static class ArenaFactory extends BasePooledObjectFactory<Arena> {

        private final int defaultSize;

        public ArenaFactory(int defaultSize) {
            this.defaultSize = defaultSize;
        }

        @Override
        public Arena create() throws Exception {
            return new Arena(defaultSize);
        }

        @Override
        public PooledObject<Arena> wrap(Arena arena) {
            return new DefaultPooledObject<>(arena);
        }

        @Override
        public void passivateObject(PooledObject<Arena> pooledObject) throws Exception {
            pooledObject.getObject().reset(); // Reset Arena state when returning to pool
        }

        @Override
        public void destroyObject(PooledObject<Arena> pooledObject) throws Exception {
            pooledObject.getObject().close(); // Dispose Arena when destroyed from pool
        }
    }
}
