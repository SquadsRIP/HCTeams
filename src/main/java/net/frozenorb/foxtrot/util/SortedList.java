package net.frozenorb.foxtrot.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SortedList<E> extends AbstractList<E> {
   private ArrayList<E> internalList = new ArrayList<>();
   private Comparator<E> comparator;

   public SortedList(Comparator<E> comparator) {
      this.comparator = comparator;
   }

   @Override
   public void add(int position, E e) {
      this.internalList.add(e);
      Collections.sort(this.internalList, this.comparator);
   }

   @Override
   public E get(int i) {
      return this.internalList.get(i);
   }

   @Override
   public int size() {
      return this.internalList.size();
   }
}
