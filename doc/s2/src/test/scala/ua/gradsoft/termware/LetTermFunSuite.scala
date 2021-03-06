package ua.gradsoft.termware;

import org.scalatest.FunSuite;
import org.scalatest.matchers.ShouldMatchers;

class LetTermFunSuite extends FunSuite 
                         with ShouldMatchers
{

  test("let term must be able to be created from assignmetns and main term") {
    val r = parser.phrase(parser.term)(new parser.lexical.Scanner("let (x<-f(1)) g(x,x,x)"));
    r match {
       case parser.Success(t,_) => {
                         t match {
                           case FunctionalTerm(theory.Let,
                                               Seq(assignments, body), _) =>
                                  // ok. now we have functional let term.
                                  // let's transform one to let term.
                                  val letTerm = LetTerm.build(assignments,body,theory);
                                  //assert(letTerm.vars.length==1);
                                  //assert(letTerm.vars(0).name.string=="x");
                                  ////assert(letTerm.proxy.name.string=="g");
                                  ////assert(letTerm.proxy.arity==3);
                                  assert(letTerm.arity==3,"resut term must have arity 3, as g(x,x,x)");
                                  assert(letTerm.name.string=="g", "and name of internal term" );
                                  val G = theory.symbolTable.getOrCreate("g");
                                  letTerm match {
                                    case FunctionalTerm(G,Seq(x1,x2,x3),_) =>
                                         //x1.name.string should be ("f");
                                         assert(x1.name.string=="f");
                                         //x1.arity should be (1);
                                         assert(x1.arity==1);
                                    case _ => fail("let expression must be matched as functional expression, if functional expression inside");

                                  }
                                  letTerm match {
                                    case LetTerm(xbindings,xInternalTerm,_) =>
                                           /* ok */
                                    case _ => fail("let expression must be matched as LetTern");
                                  }

                           case _ => fail("let-expresion must be parsed as functional term");
                         }
                       }
       case _ => fail("let term must be parsed");
    }
  }

  test("internal variable in internal let must hide one in external let") {
    val r = parser.phrase(parser.term)(new parser.lexical.Scanner("""
                 let (x<-f(1)) g(x,let (x<-f(2)) x )
            """));
    r match {
       case parser.Success(t,_) => {
         t match {
           case FunctionalTerm(theory.Let, Seq(assignments, body), _) =>
           val letTerm = LetTerm.build(assignments,body,theory);
           val proxyName = letTerm.name;
           letTerm match {
             case LetTerm(bindings0, body0, _) =>
               bindings0.length should be (1)
               bindings0(0).name.string should be("x");
               letTerm.subterm(0).name.string should be("f");
               letTerm.subterm(0).subterm(0).getInt should be(1);
               val letTerm1 = letTerm.subterm(1);
               letTerm1 match {
                  case LetTerm(bindings1, body1, _) =>
                    bindings1.length should be (1)
                    bindings1(0).name.string should be("x");
                    body1.arity should be (1);
                    body1.name.string should be ("f");
                    body1.subterm(0).getInt should be (2);
               }
           }
           case _ => fail("let term must be parset to 'let' functional term");
         }
       }
       case _ => fail("let term with let-term inside must be parsed");
    }
  }

  test("substitution on let must be anoter let") {
    val r1 = parser.phrase(parser.term)(new parser.lexical.Scanner(
                         "let (x<-f(1,y)) g(x,y)"));
    val t1 = r1 match {
       case parser.Success(t,_) => 
         t match {
           case FunctionalTerm(theory.Let, Seq(assignments, body), _) =>
                                LetTerm.build(assignments,body,theory)
           case _ => fail("let term must be parsed to 'let' functional term")
         }
       case _ => fail("let term r1  must be parsed");
    }
    val st1 = t1.fixSubst(s1); 
    st1.name.string should equal("g");
    st1.subterm(0) match {
       case LetProxy(name,index,owner) =>
                 name.string should equal("x")
                 assert(owner eq st1,"owner should be same as st1")
       case _ => fail("first argument must be let-proxy")
    }
    st1.subterm(0).name.string should equal("f");
    st1.subterm(0).subterm(0).value[Int] should equal(1);
    st1.subterm(0).subterm(1).name.string should equal("z");
    st1.subterm(1).name.string should equal("z");
  }

  test("substitution on let must change atoms in internal let's") {
    val r1 = parser.phrase(parser.term)(new parser.lexical.Scanner(
                         "let (x<-f(1,y)) g(x,let (q <- a(x)) g(q,y) )"));
    val t1 = r1 match {
       case parser.Success(t,_) => 
         t match {
           case FunctionalTerm(theory.Let, Seq(assignments, body), _) =>
                                LetTerm.build(assignments,body,theory)
           case _ => fail("let term must be parsed to 'let' functional term")
         }
       case _ => fail("let term r1  must be parsed");
    }
    val st1 = t1.fixSubst(s1); 
    st1.name.string should equal("g");
    st1.subterm(0).name.string should equal("f");
    st1.subterm(1).name.string should equal("g");
    st1.subterm(1).subterm(0).name.string should equal("a");
    st1.subterm(1).subterm(1).name.string should equal("z");
  }

  object s1 extends PartialFunction[Term,Term] {
      def isDefinedAt(x:Term):Boolean = x.isAtom && x.name.string == "y";
      def apply(x:Term):Term = z ;       
      val z = theory.freeAtomSignature.createConstant("z");
  }

  val theory = TermWare.instance.freeTheory;
  val parser = new TermWareParser(theory,"inside-LetTermFunSuite");


}
