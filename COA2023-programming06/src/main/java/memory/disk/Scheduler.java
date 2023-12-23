package memory.disk;


import javax.sound.midi.Track;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Scheduler {

    /**
     * 先来先服务算法
     *
     * @param start   磁头初始位置
     * @param request 请求访问的磁道号
     * @return 平均寻道长度
     */
    public double FCFS(int start, int[] request) {
        double sum = 0;
        for (int i = 0; i < request.length; i++) {
            if (i == 0) {
                sum += Math.abs(start - request[i]);
            } else {
                sum += Math.abs(request[i] - request[i - 1]);
            }
        }
        double result = sum / (double) request.length;
        return result;
    }

    /**
     * 最短寻道时间优先算法
     *
     * @param start   磁头初始位置
     * @param request 请求访问的磁道号
     * @return 平均寻道长度
     */
    public double SSTF(int start, int[] request) {
        double sum = 0;
        //list an array including request and start
        ArrayList<Integer> list = new ArrayList<>();
        list.add(start);
        for (int i = 0; i < request.length; i++) {
            list.add(request[i]);
        }
        //sort the list
        list.sort(null);
        //find the index of start
        int index = list.indexOf(start);
        //find the nearest number
        for (int i = 0; i < request.length; i++) {
            if (index == 0) {
                sum += Math.abs(list.get(index) - list.get(index + 1));
                list.remove(index);
            } else if (index == list.size() - 1) {
                sum += Math.abs(list.get(index) - list.get(index - 1));
                list.remove(index);
                index--;
            } else {
                if (Math.abs(list.get(index) - list.get(index - 1)) < Math.abs(list.get(index) - list.get(index + 1))) {
                    sum += Math.abs(list.get(index) - list.get(index - 1));
                    list.remove(index);
                    index--;
                } else {
                    sum += Math.abs(list.get(index) - list.get(index + 1));
                    list.remove(index);
                }
            }
        }
        double result = sum / (double) request.length;
        return result;
    }

    /**
     * 扫描算法
     *
     * @param start     磁头初始位置
     * @param request   请求访问的磁道号
     * @param direction 磁头初始移动方向，true表示磁道号增大的方向，false表示磁道号减小的方向
     * @return 平均寻道长度
     */
    public double SCAN(int start, int[] request, boolean direction) {
        int max = 0;
        int min = 1000;
        for (int i = 0; i < request.length; i++) {
            if (request[i] > max) {
                max = request[i];
            }
            if (request[i] < min) {
                min = request[i];
            }
        }
        double result = 0;
        if (direction) {
            if (start <= min) {
                result += max - start;
            } else {
                result += Math.abs(start - Disk.TRACK_NUM + 1) + Math.abs(Disk.TRACK_NUM - 1 - min);
            }
        } else {
            if (start >= max) {
                result += start - min;
            } else {
                result += start + max;
            }
        }
        return result / request.length;
    }

    /**
     * C-SCAN算法：默认磁头向磁道号增大方向移动
     *
     * @param start   磁头初始位置
     * @param request 请求访问的磁道号
     * @return 平均寻道长度
     */
    public double CSCAN(int start, int[] request) {
        int max = 0;
        int min = 1000;
        int last = 0;
        for (int i = 0; i < request.length; i++) {
            if(request[i]<start&&request[i]>last){
                last = request[i];
            }
            if (request[i] > max) {
                max = request[i];
            }
            if (request[i] < min) {
                min = request[i];
            }
        }
        //System.out.println(last);
        double result = 0;
        if (start <= min) {
            result += max - start;
        }
        else {
            result += Disk.TRACK_NUM-1+(Disk.TRACK_NUM-1-start)+(last);
        }

        return result / request.length;
    }

    /**
     * LOOK算法
     *
     * @param start     磁头初始位置
     * @param request   请求访问的磁道号
     * @param direction 磁头初始移动方向，true表示磁道号增大的方向，false表示磁道号减小的方向
     * @return 平均寻道长度
     */
    public double LOOK(int start, int[] request, boolean direction) {
        int max = 0;
        int min = 1000;
        for (int i = 0; i < request.length; i++) {
            if (request[i] > max) {
                max = request[i];
            }
            if (request[i] < min) {
                min = request[i];
            }
        }
        double result = 0;
        if (direction) {
            if (start < min) {
                result = max - start;
            } else {
                result = Math.abs(start - max) + Math.abs(max - min);
            }
        } else {
            if (start > max) {
                result = start - min;
            } else {
                result = Math.abs(start - min) + Math.abs(max - min);
            }
        }
        return result / request.length;
    }

    /**
     * C-LOOK算法：默认磁头向磁道号增大方向移动
     *
     * @param start   磁头初始位置
     * @param request 请求访问的磁道号
     * @return 平均寻道长度
     */
    public double CLOOK(int start, int[] request) {
        int max = 0;
        int min = 1000;
        int last = 0;
        for (int i = 0; i < request.length; i++) {
            if(request[i]<start&&request[i]>last){
                last = request[i];
            }
            if (request[i] > max) {
                max = request[i];
            }
            if (request[i] < min) {
                min = request[i];
            }
        }
        System.out.println(last);
        double result = 0;
        if (start < min) {
            result += max - start;
        }else if(start > max) {
            result += start - min;
        }
        else {
            result += max-min+(max-start)+(last-min);
        }

        return result / request.length;
    }

}
