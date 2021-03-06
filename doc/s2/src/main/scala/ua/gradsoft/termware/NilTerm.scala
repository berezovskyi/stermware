package ua.gradsoft.termware;


class NilTerm(s:NilTermSignature) extends PrimitiveTerm(s)
                                    with FixedNameTerm
                                    with NonNumberTerm
                                    with NonBooleanTerm
{

	
  override def isNil = true;

  /**
   * can represent empty list.
   */
  override def optValue[T](implicit mt: Manifest[T]) =
  {
  	if (mt <:< manifest[List[_]]) Some(Nil.asInstanceOf[T]) else None 
  }
  
  override def isAtom = false;
  
  def termClassIndex=TermClassIndex.NIL;

  def fixTermCompare(t:Term):Int = 
    termClassIndex - t.termClassIndex;

  def fixTermEq(t:Term):Boolean = t.isNil;

  def termHashCode: Int = 1+name.hashCode;

  override def toString = "nil";

}

