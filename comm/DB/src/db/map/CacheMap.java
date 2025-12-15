package db.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheMap<K, V> {

    /**	
     * map cache
     */
    private final Map<K, V> map = Collections.synchronizedMap(new ConcurrentHashMap<K, V>());

    // private final Map<K, V> map = new ConcurrentHashMap<K, V>();

    /**	
     * Associates the specified value with the specified key in this map
     * 
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     */
    public void put(K key, V value) {
        map.put(key, value);
    }

    /**	
     * Removes the mapping for a key from this map if it is present
     * 
     * @param key key whose mapping is to be removed from the map
     */
    public void remove(K key) {
        map.remove(key);
    }

    /**	
     * Returns the number of key-value mappings in this map.
     * 
     * @return the size of map
     */
    public int size() {
        return map.size();
    }

    /**	
     * Removes all of the mappings from this map.
     */
    public void clear() {
        map.clear();
    }

    /**	
     * Returns a {@link Collection} view of the values contained in this map.
     * 
     * @return  a collection view of the values contained in this map
     */
    public Collection<V> values() {
        return map.values();
    }

    /**	
     * Copy a the values contained in this map to a list.
     * 
     * @return a list of the values contained in this map
     */
    public List<V> valuesCopy() {
        Collection<V> values = map.values();

        return new ArrayList<V>(values);
    }

    /**	
     * Returns the value to which the specified key is mapped
     * 
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or
     *         {@code null} if this map contains no mapping for the key
     */
    public V get(K key) {
        return map.get(key);
    }

    /**	
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key.
     * @param key key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified
     *         key
     */
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }
}
