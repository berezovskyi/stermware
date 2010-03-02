package ua.gradsoft.termware;


/**
 * Signature for long
 */
class LongTermSignature(th:Theory) extends PrimitiveTermSignature(th)
{

  override def createConstant(arg:Any):Option[Term] = arg match {
    case x:Long => Some(LongTerm(x,this))
    case _ => None
  }

  val typeName = "Long" ;

}
