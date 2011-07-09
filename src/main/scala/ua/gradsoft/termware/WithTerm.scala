package ua.gradsoft.termware;

import scala.collection.Set;
import scala.collection.immutable.Map;
import scala.collection.immutable.TreeMap;
import scala.collection.mutable.HashMap;
import java.io.PrintWriter;
import ua.gradsoft.termware.fn._;
import ua.gradsoft.termware.util._;
import ua.gradsoft.termware.flow._;


class WithTerm(vars:IndexedSeq[XTerm], val p:Term, ws: WithTermSignature)  
                             extends XOwner(vars)
                                         with ComplexUnify
                                         with ComplexSubst
                                         with ComplexCompare
                                         with NonNumberTerm
{

  def isNil = p.isNil;

  def isAtom = p.isAtom;

  def isEta = p.isEta;

  def isError = p.isError;

  override def name = p.name;

  override def arity = p.arity;

  override def subterms = p.subterms;

  override def subterm(index: Int) = p.subterm(index); 

  override def unify(t:Term,s:Substitution)(implicit ctx:CallContext)
                               :ComputationBounds[(Boolean,Substitution)]=
    p.unify(t,s)(ctx);

  override def subst(s:PartialFunction[Term,Term])
                    (implicit ctx: CallContext) :
                                                  ComputationBounds[Term] = 
    ctx.withCall { 
       (ctx:CallContext) => implicit val ictx = ctx;
       val (newVars, sv) = copyVarFun;
       val s1 = sv.orElse(s);
       CallCC.compose(p.subst(s1),
              { (t:Term) => Done(new WithTerm(newVars,t,ws)) }
       );
    }

  override def termClassIndex  = p.termClassIndex;

  override def termHashCode = hashCode;

  override def termCompare(t:Term)(implicit ctx:CallContext)
                                                :ComputationBounds[Int] = 
     p.termCompare(t);
  

  override def print(out:PrintWriter):Unit = {
    out.print("with (");
    var frs = true;
    for(x <- vars) {
       if (!frs) {
          out.print(", ");
       }else{
          frs=false;
       }
       x.print(out);
    }
    out.print("):");
    p.print(out);
  }

  def signature = ws.apply(p.signature);

  var attributes=new HashMap[Name,ComputationBounds[Term]]();
       
  private lazy val hash: Int = p.hashCode;

}