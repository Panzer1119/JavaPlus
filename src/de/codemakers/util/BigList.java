package de.codemakers.util;

import java.util.List;

public class BigList<T> extends List<T> { //TODO implement this

  public static final BigInteger MAX_VALUE = BigInteger.valueOf(Integer.MAX_VALUE);

  private BigInteger index;
  private final List<T> data;
  private BigListEntry<T> child = null;
  
  public BigList(BigInteger index, List<T> data) {
    this.index = index;
    this.data = data;
  }
  
  public final T get(BigInteger i) {
    BigList list = this;
    while (i.compareTo(MAX_VALUE) >= 0) {
      i = i.subtract(MAX_VALUE);
      list = list.child; //TODO null Checking
    }
    return child.data.get(i.toInt());
  }

}