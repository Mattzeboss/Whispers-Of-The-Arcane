package src;

import java.util.ArrayList;

public class SwapAndPopList<T> extends ArrayList<T> {
    //when we want to remove an element from the list, we swap it with the last element in the array and pop it off
    //this makes removal O(1) rather than O(n) b\c we don't have to shift every element above the removed element by 1
    @Override
    public T remove(int i){
        T temp = this.get(i);
        /*
        An optimization some may think to do is to replace super.get with super.remove because it would save calling it later
        That would not work in the case where we want to remove the last element in the list, because we would try to set
        for an index that is too large and get an array out of bounds error
         */
        this.set(i, get(size()-1)); //swap
        super.remove(size()-1); //pop
        return temp;
    }
}
