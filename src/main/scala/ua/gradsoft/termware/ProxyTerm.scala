package ua.gradsoft.termware;

import ua.gradsoft.termware.flow._;
import java.io.PrintWriter;

/**
 * proxyterm
 **/
trait ProxyTerm  extends Term
{

  def proxy: Term ;

  override def isBoolean: Boolean = proxy.isBoolean;

  override def getBoolean: Boolean = proxy.getBoolean;

  def isByte:  Boolean = proxy.isByte;

  def getByte: Byte = proxy.getByte;

  def isShort:  Boolean = proxy.isShort;

  def getShort: Short = proxy.getShort;

  def isInt:  Boolean = proxy.isInt;

  def getInt: Int = proxy.getInt;

  def isLong:  Boolean = proxy.isLong;

  def getLong: Long = proxy.getLong;

  def isBigInt: Boolean = proxy.isBigInt;

  def getBigInt: BigInt = proxy.getBigInt;

  def isBigDecimal: Boolean = proxy.isBigDecimal;

  def getBigDecimal: BigDecimal = proxy.getBigDecimal;

  def isFloat: Boolean = proxy.isFloat;

  def getFloat: Float = proxy.getFloat;

  def isDouble: Boolean = proxy.isDouble;

  def getDouble: Double = proxy.getDouble;

  def isNumber: Boolean = proxy.isNumber;

  def getNumber: Number = proxy.getNumber;

  def getNumberKind: Int = proxy.getNumberKind;

  override def isChar:  Boolean =  proxy.isChar;

  override def getChar: Char  = proxy.getChar;

  override def isString:  Boolean = proxy.isString;

  override def getString: String = proxy.getString;

  override def isException: Boolean = proxy.isException;

  override def getException: Exception = proxy.getException;

  override def getMessage: String = proxy.getMessage;

  override def isRef: Boolean = proxy.isRef;

  override def getRef: AnyRef = proxy.getRef;

   def arity:Int = proxy.arity;

   def name = proxy.name;

   def subterm(i:Int) = proxy.subterm(i);

   def subterms = proxy.subterms;

   override def xOwner:XOwner = proxy.xOwner;

   def unify(t:Term, s:Substitution[Term])(implicit ctx:CallContext) = 
          proxy.unify(t,s)(ctx);

   override def isError: Boolean = proxy.isError;

   override def isEta: Boolean = proxy.isEta;

   override def isAtom: Boolean = proxy.isAtom;

   override def isNil: Boolean = proxy.isNil;

   override def termCompare(t:Term)(implicit ctx:CallContext)
                                                :ComputationBounds[Int] =
    proxy.termCompare(t)(ctx);

   override def onTermCompare[T](t:Term)
                      (cont:(Int,CallContext) => ComputationBounds[T])
                      (implicit ctx:CallContext) : ComputationBounds[T] =
    proxy.onTermCompare(t)(cont)(ctx);


   override def termEq(t:Term)(implicit ctx:CallContext): ComputationBounds[Boolean] = proxy.termEq(t)(ctx);


  override def subst(s:PartialFunction[Term,Term])
                    (implicit ctx: CallContext) :
                                           ComputationBounds[Term] = 
    proxy.subst(s)(ctx);

  override def termClassIndex  = proxy.termClassIndex;

  override def termHashCode = proxy.hashCode;

  private lazy val hash: Int = proxy.hashCode;

}
