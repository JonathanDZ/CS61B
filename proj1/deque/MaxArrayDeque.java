package deque;

import java.util.Comparator;

class MaxArrayDeque<item> extends ArrayDeque<item> {
    private Comparator<item> comparator;

    public MaxArrayDeque(Comparator<item> c){
        comparator = c;
    }

    public item max(){
        return max(comparator);
    }

    public item max(Comparator<item> c){
        if (this.size() == 0){
            return null;
        }
        item maxItem = this.get(0);
        for (int i = 0; i < this.size(); i += 1){
            item currItem = this.get(i);
            if (c.compare(currItem, maxItem) > 0){
                maxItem = currItem;
            }
        }
        return maxItem;
    }

//     public class MaxElement implements Comparator<item>{
//        public int compare(item item1, item item2){
//            if (item1 instanceof Integer){
//                return ((Integer) item1).compareTo(((Integer) item2));
//            }else if (item1 instanceof String){
//                return ((String) item1).compareTo(((String) item2));
//            }else if (item1 instanceof Double){
//                return ((Double) item1).compareTo(((Double) item2));
//            }else if (item1 instanceof Float) {
//                return ((Float) item1).compareTo(((Float) item2));
//            }else if (item1 instanceof Boolean){
//                return ((Boolean) item1).compareTo(((Boolean) item2));
//            }else if(item1 instanceof Long){
//                return ((Long) item1).compareTo(((Long) item2));
//            }else {
//                return ((Short) item1).compareTo(((Short) item2));
//            }
//        }

         public class MaxElement<item extends Comparable<item>> implements Comparator<item>{
             public int compare(item item1, item item2){
                 return item1.compareTo(item2);
             }
     }
}