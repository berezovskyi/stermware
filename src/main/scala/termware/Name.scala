package termware


sealed trait Name extends Ordered[Name]
{

  def typeIndex: Int

  def compare(that: Name): Int =
  {
    val cmp = typeIndex - that.typeIndex
    if (cmp !=0) cmp else compareSameTypeIndex(that)
  }

  def compareSameTypeIndex(that: Name): Int

}

sealed abstract class StringLikeName(val value:String) extends Name
{

   def compareSameTypeIndex(that: Name) =
     value compare that.asInstanceOf[StringLikeName].value

}

object NameTypeIndexes
{
  final val ATOM = 1
  final val STRING = 2
  final val LONG = 3
  val INT = 4
  val CHAR = 5
  val DOUBLE = 6
  val OPAQUE = 7
  val SET = 8
  val ARROW = 9
  val UNIVERSUM = 10
  val ERROR = 11
}

import NameTypeIndexes._

case class AtomName(v:String) extends StringLikeName(v)
{
  def typeIndex = ATOM
}

case class StringName(v:String) extends StringLikeName(v)
{
  def typeIndex = STRING
}

sealed abstract class CharLikeName(val value:Char) extends Name
{
  def typeIndex = CHAR

  def compareSameTypeIndex(that: Name) =
     value compare that.asInstanceOf[CharLikeName].value
}

case class CharName(v: Char) extends CharLikeName(v)

case class LongName(val value:Long) extends Name
{

  def typeIndex = LONG

  def compareSameTypeIndex(that: Name) =
     value compare that.asInstanceOf[LongName].value

}

case class IntName(val value:Int) extends Name
{

  def typeIndex = INT

  def compareSameTypeIndex(that: Name) =
     value compare that.asInstanceOf[IntName].value

}

case class DoubleName(val value: Double) extends Name
{

  def typeIndex = DOUBLE

  def compareSameTypeIndex(that: Name) =
     value compare that.asInstanceOf[DoubleName].value

}

case class OpaqueName(val value: Array[Byte]) extends Name
{

  def typeIndex = OPAQUE

  def compareSameTypeIndex(that: Name) =
  {
    val other = that.asInstanceOf[OpaqueName]
    var c = value.length - other.value.length
    if (c!=0) {
      c
    } else {
      var i=0;
      while(c!=0 && i<value.length) {
        c = value(i) - other.value(i)
        i += 1
      }
      c
    }
  }

}

case object SetName extends StringLikeName("set")
{
  def typeIndex = SET
}

case object ArrowName extends StringLikeName("arrow")
{
  def typeIndex = ARROW
}

case object UniversumName extends StringLikeName("Universum")
{
  def typeIndex = UNIVERSUM
}

case object ErrorName extends StringLikeName("Error")
{
  def typeIndex = ERROR
}

