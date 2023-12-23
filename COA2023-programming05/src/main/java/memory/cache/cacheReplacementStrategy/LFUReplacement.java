package memory.cache.cacheReplacementStrategy;


import memory.cache.Cache;

/**
 * TODO 最近不经常使用算法
 */
public class LFUReplacement implements ReplacementStrategy {

    @Override
    public void hit(int rowNO) {
        Cache.getCache().addVisited(rowNO);
    }

    @Override
    public int replace(int start, int end, char[] addrTag, byte[] input) {
        //GET the minest used times
        long min = Long.MAX_VALUE;
        int minIndex = -1;
        for (int i = start; i < end; i++) {
            if (Cache.getCache().getVisited(i) < min) {
                min = Cache.getCache().getVisited(i);
                minIndex = i;
            }
        }
        return minIndex;
    }

}
