package ua.gradsoft.termware;

import java.lang.Number;

/**
 * Term which holds long value
 **/
case class LongTerm(v:Long, s:LongTermSignature) 
                                  extends NumberPrimitiveTerm[Long](v,s)
{

  override def isByte: Boolean = 
         (value.toByte.toLong==value);
  override def getByte: Option[Byte] =
          if (isByte) Some(value.toByte) else None;

  override def isShort: Boolean = 
         (value.toShort.toLong==value);
  override def getShort: Option[Short] =
          if (isShort) Some(value.toShort) else None;

  override def isInt: Boolean = 
         (value.toInt.toLong==value);
  override def getInt: Option[Int] =
          if (isInt) Some(value.toInt) else None;

  override def isLong: Boolean = true;
  override def getLong: Option[Long] = Some(value);

  override def isFloat:  Boolean = true;
  override def getFloat: Option[Float] = Some(value.toFloat);

  override def isDouble:  Boolean = true;
  override def getDouble: Option[Double] = Some(value.toDouble);

  override def isBigInt:  Boolean = true;
  override def getBigInt: Option[BigInt] = 
     Some(new BigInt(java.math.BigInteger.valueOf(value)));

  override def isBigDecimal:  Boolean = true;
  override def getBigDecimal: Option[BigDecimal] = 
     Some(new BigDecimal(new java.math.BigDecimal(value)));

  override def getNumber: Some[Number] = Some(new java.lang.Long(value));
  override def getNumberKind: Some[Int] = Some(NumberKind.LONG.id);

  def termCompare(t: Term):Int = {
    var c = termClassIndex - t.termClassIndex;
    if (c!=0) return 0;
    return (value - t.getLong.get).toInt;
  }


  lazy val name = new StringName[java.lang.Long](new java.lang.Long(value),
                                                 NameKindIndex.LONG.id
                                                );
  lazy val termHashCode = value.toInt;
}

