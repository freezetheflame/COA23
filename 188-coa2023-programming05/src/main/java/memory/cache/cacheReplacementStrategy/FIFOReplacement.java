package memory.cache.cacheReplacementStrategy;


import memory.cache.Cache;
import memory.Memory;


/**
 * TODO 先进先出算法
 */
public class FIFOReplacement implements ReplacementStrategy {

    @Override
    public void hit(int rowNO) {
        Cache.getCache().addTimeStamp();
    }

    @Override
    public int replace(int start, int end, char[] addrTag, byte[] input) {
        //GET the longest time stamp
        long max = -1;
        int maxIndex = -1;
        for (int i = start; i < end; i++) {
            if (Cache.getCache().getTimeStamp(i) > max) {
                max = Cache.getCache().getTimeStamp(i);
                maxIndex = i;
            }
            if (Cache.getCache().isDirty(i)&&Cache.getCache().isValid(i)) {
                Memory.getMemory().write(Cache.getCache().getpAddr(i), 64, Cache.getCache().getData(i));
                Cache.getCache().setDirty(i);
            }
        }
        //if some line is dirty, write back

        return maxIndex;
    }

}
