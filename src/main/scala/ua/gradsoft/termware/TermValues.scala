package ua.gradsoft.termware;

import java.lang.Number;

/**
 * API for access values, wrapped in terms.
 */
trait TValue extends GeneralUtil
{

  /**
   * is this is boolean ?
   */
  def isBoolean: Boolean = false;

  /**
   * get boolean value if one is boolean.
   */
  def getBoolean: Boolean = throwUOE;

  /**
   * is this is byte ?
   **/
  def isByte:  Boolean;

  /**
   * get byte value if one is byte.
   */
  def getByte: Byte;

  /**
   *  is this is short ?
   **/
  def isShort:  Boolean;

  /**
   *  get short value.
   **/
  def getShort: Short;

  /**
   *  is this is integer ?
   **/
  def isInt:  Boolean;

  /**
   * get int value
   **/
  def getInt: Int;

  /**
   * is this is long ?
   **/
  def isLong:  Boolean;

  /**
   * get long is one is long.
   **/
  def getLong: Long;

  /**
   * if this is big integer
   **/
  def isBigInt: Boolean;

  /**
   * get BigInteger is one is big integer
   **/
  def getBigInt: BigInt;

  /**
   * it this term can be represented as big decimal ?
   **/
  def isBigDecimal: Boolean;

  def getBigDecimal: BigDecimal;

  def isFloat: Boolean;

  def getFloat: Float;

  def isDouble: Boolean;

  def getDouble: Double;

  /**
   * if this is number type ?
   **/
  def isNumber: Boolean ;

  def getNumber: Number;

  def getNumberKind: Int;

  def isChar:  Boolean = false;

  def getChar: Char = throwUOE;

  def isString:  Boolean = false;

  def getString: String = throwUOE;

  /**
   * is this a special exception object ?
   **/
  def isException: Boolean = false;

  def getException: Exception = throwUOE;

  def getMessage: String = throwUOE;

  /**
   * is this is reference object ?
   * (i. e. derived from scala AnyRef or Java Object)
   **/
  def isRef: Boolean = false;

  def getRef: AnyRef = None;

}
