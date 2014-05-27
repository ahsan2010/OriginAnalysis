package com.marge.split;





public class Pair {
	  public Object o1;
	  public Object o2;
	  
	  public Pair(Object o1, Object o2) { 
		  this.o1 = o1; this.o2 = o2; 
		  }
	 
	  public static boolean same(Object o1, Object o2) {
	    return o1 == null ? o2 == null : o1.equals(o2);
	  }
	 
	  Object getFirst() { return o1; }
	  Object getSecond() { return o2; }
	 
	  void setFirst(Object o) { o1 = o; }
	  void setSecond(Object o) { o2 = o; }
	 
	  /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((o1 == null) ? 0 : o1.hashCode());
		result = prime * result + ((o2 == null) ? 0 : o2.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		if (o1 == null) {
			if (other.o1 != null)
				return false;
		} else if (!o1.equals(other.o1))
			return false;
		if (o2 == null) {
			if (other.o2 != null)
				return false;
		} else if (!o2.equals(other.o2))
			return false;
		return true;
	}
	 
	  public String toString() {
	    return "Pair{"+o1+", "+o2+"}";
	  }
	  
	
}