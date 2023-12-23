package memory.cache.cacheReplacementStrategy;

import memory.cache.Cache;

/**
 * TODO 最近最少用算法
 */
public class LRUReplacement implements ReplacementStrategy {

    @Override
    public void hit(int rowNO) {
        Cache.getCache().addTimeStamp();
        Cache.getCache().setTimeStamp(rowNO);
    }

    @Override
    public int replace(int start, int end, char[] addrTag, byte[] input) {
        long max =  -1;
        int maxIndex = -1;
        for (int i = start; i < end; i++) {
            if (Cache.getCache().getTimeStamp(i) > max) {
                max = Cache.getCache().getTimeStamp(i);
                maxIndex = i;
            }
        }
        return maxIndex;
    }

}





























