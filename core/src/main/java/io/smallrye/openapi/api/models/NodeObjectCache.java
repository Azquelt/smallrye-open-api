package io.smallrye.openapi.api.models;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * A cache for associating {@link JsonWrappingImpl} model objects with the JsonNode that they wrap
 * <p>
 * The cache entry is kept for as long as both the node and the object are reachable.
 * This ensures that a client which retrieves an object twice and compares them for identity will find they match.
 *
 * @param <N> The JSON node type
 * @param <T> The model object type
 */
public class NodeObjectCache<N extends JsonNode, T extends JsonWrappingImpl> {

    private final Function<N, T> constructor;
    private final HashMap<CacheEntry<N, T>, CacheEntry<N, T>> cache;
    private final ReferenceQueue<N> queue = new ReferenceQueue<>();

    public NodeObjectCache(Function<N, T> constructor) {
        cache = new HashMap<>();
        this.constructor = constructor;
    }

    public void put(N node, T object) {
        removeExpired();
        CacheEntry<N, T> newKey = new CacheEntry<>(node, object, queue);
        CacheEntry<N, T> oldKey = cache.get(newKey);

        if (oldKey != null) {
            // Manually remove the old key from the map to ensure we add the new key
            // They compare as equal, so they have the same node, but may have different objects
            cache.remove(oldKey);
            // Clear the oldKey reference without enqueuing it so that it doesn't hang about waiting to be queued
            oldKey.clear();
        }

        cache.put(newKey, newKey);
    }

    public T getOrCreate(N node) {
        removeExpired();
        CacheEntry<N, T> entry = cache.get(new CacheEntry<>(node));
        if (entry != null) {
            T object = entry.getValue();
            if (object != null) {
                return object;
            }
        }

        T object = constructor.apply(node);
        put(node, object);
        return object;
    }

    private void removeExpired() {
        Reference<?> ref;
        while ((ref = queue.poll()) != null) {
            cache.remove(ref);
        }
    }

    /**
     * A key-value pair, suitable for use as a map key
     * <p>
     * Both the key and the value are weakly held.
     * <p>
     * Two cache entries are equal if they are the same object or refer to the same key object
     *
     * @param <K> the key type
     * @param <V> the value type
     */
    private static class CacheEntry<K, V> extends WeakReference<K> {
        private int hashCode;
        private WeakReference<V> valueRef;

        public CacheEntry(K key) {
            super(key);
            hashCode = System.identityHashCode(key);
            valueRef = null;
        }

        public CacheEntry(K key, V value, ReferenceQueue<? super K> queue) {
            super(key, queue);
            hashCode = System.identityHashCode(key);
            valueRef = new WeakReference<>(value);
        }

        public V getValue() {
            return valueRef == null ? null : valueRef.get();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;
            if (obj == null)
                return false;
            if (!(obj instanceof CacheEntry))
                return false;
            // Two cache keys are equal if they refer to the same object, or are both empty
            CacheEntry<?, ?> key = (CacheEntry<?, ?>) obj;
            return this.hashCode == key.hashCode && this.get() == key.get();
        }

        @Override
        public int hashCode() {
            // HashCode is always the identity of the key
            return hashCode;
        }
    }
}
