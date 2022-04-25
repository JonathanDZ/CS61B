package deque;

import java.util.Comparator;

class MaxArrayDeque<item> extends ArrayDeque<item> {
    public Comparator<item> comparator;

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

    public class MaxElement<item extends Comparable<item>> implements Comparator<item>{
         public int compare(item item1, item item2){
             return item1.compareTo(item2);
         }
    }
}