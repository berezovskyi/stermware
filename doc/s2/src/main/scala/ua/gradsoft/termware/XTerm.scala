package ua.gradsoft.termware;

import scala.collection.mutable.HashMap;
import java.io.PrintWriter;
import ua.gradsoft.termware.flow._;

/**
 * class represent variable, bound in eta-term.
 **/
class XTerm(override val name: Name, 
            override val xLabel:Int, 
            val  typeTerm:Term, 
                 owner :XOwner, 
            override val signature:XTermSignature) 
                                            extends Term
                                               with ComplexUnify
                                               with SimpleSubst
                                               with SimpleCompare
                                               with NonNumberTerm
                                               with NonBooleanTerm
{

	
   /**
    * xtern can't represent value	
    */
   def optValue[T](implicit mt:Manifest[T]):Option[T] = None	
	
   /**
    * arity of eta-x term is always 0
    **/
   def arity:Int = 0;

   /**
    * None.
    **/
   def subterm(i:Int) = throwUOE;

   /**
    * None
    **/
   def subterm(name:Name) = throwUOE;

   /**
    * Seq.empty
    */
   def subterms = IndexedSeq.empty;

   /**
    * get xOwner
    **/
   override def xOwner:XOwner = xOwner_;

            def xOwner_=(owner:XOwner):Unit =
                { xOwner_ = owner; }

   /**
    * true if this is X-term.
    **/
   override def isX = true;

   override def unify(t:Term, s:Substitution[Term])(implicit ctx:CallContext) 
                              : ComputationBounds[(Boolean, Substitution[Term])] = {
      if (t.isX && (t.xOwner eq xOwner) ) {
            Done((t.xLabel == xLabel,s));
      } else {
        s+(this,t);
      }
   }

   def fixTermEq(t:Term): Boolean = { t.isX && (t.xOwner eq xOwner) }; 

   def fixTermCompare(t:Term):Int = {
      var c = termClassIndex - t.termClassIndex;
      if (c!=0) return c;
      c = xLabel - t.xLabel;
      if (c!=0) return c;
      c=name.compareTo(t.name);
      if (c!=0) return c;
      return xOwner.compareTo(t.xOwner);
   }


   override def fixSubst(s:PartialFunction[Term,Term]):Term = {
      if (s.isDefinedAt(this)) s(this) else this;
   }
   
   override def isError: Boolean = false;
   override def isEta: Boolean = false;
   override def isLet: Boolean = false;
   override def isWith: Boolean = false;
   override def isAtom: Boolean = false;
   override def isNil: Boolean = false;

   override def termClassIndex: Int = TermClassIndex.X;

   override def termHashCode: Int = name.hashCode+xLabel+(if (xOwner==null) 0 else xOwner.hashCode*31);

   override def print(out:PrintWriter):Unit = { out.print(name.string); }

   lazy val attributes = new HashMap[Name,Term];
   
   private[termware] var xOwner_ = owner;

}

object XTerm
{

  def unapply(t:XTerm):Option[Tuple5[Name,Int,Term,Term,TermSignature]] =
                                 Some((t.name,t.xLabel,t.typeTerm,t.xOwner,t.signature))

}
