package ua.gradsoft.termware;

import scala.collection.immutable.TreeMap;

import ua.gradsoft.termware.flow._;


trait Substitution[A] extends PartialFunction[A,A]
{

  def +(kv:(A,A))(implicit ctx:CallContext)
                           : ComputationBounds[(Boolean,Substitution[A])] ;

  def withIndex(newZipIndex:BigInt): Substitution[A]

  def lastZipIndex: BigInt;

}


object STMSubstitution
{
  def empty[A<:Unificable[A]](implicit ma:Manifest[A]) = new STMSubstitution[A](TreeMap[A,Pair[BigInt,A]](),BigInt(1));
}


class STMSubstitution[A<:Unificable[A]](val v: Map[A,Pair[BigInt,A]],
                    val lastZipIndex: BigInt)
                    (implicit ma:Manifest[A]) extends Substitution[A]
{
  def withIndex(newZipIndex:BigInt):STMSubstitution[A] = {
        val cmp = lastZipIndex.compare(newZipIndex);
        if (cmp > 0) 
             new STMSubstitution(v,newZipIndex)
        else if (cmp < 0)
             new STMSubstitution(
                   v.filter({ _._2._1 < newZipIndex }),newZipIndex)
        else /* newZipIndex == lastZipIndex */
             this;
  }
                    
  def +(kv:(A,A))(implicit ctx:CallContext):
                            ComputationBounds[(Boolean,Substitution[A])] = {
    val r = v.get(kv._1);
    val zkv = kv._1->Pair(lastZipIndex,kv._2);
    if (r==None) 
       Done((true,new STMSubstitution(v+zkv,lastZipIndex)));
    else 
       r.get._2.unify(kv._2,new STMSubstitution(v+zkv,lastZipIndex));
  }

  override def isDefinedAt(a:A) = v.isDefinedAt(a);
  override def apply(a:A):A = v.apply(a)._2;

}



