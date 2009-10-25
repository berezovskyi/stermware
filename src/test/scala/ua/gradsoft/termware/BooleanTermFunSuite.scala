package ua.gradsoft.termware;

import org.scalatest.FunSuite;

class BooleanTermFunSuite extends FunSuite {

  test("boolean term must be isBoolean") {
     val t = TermWare.instance.getBooleanSignature.createConstant(false).get;
     assert(t.isBoolean);
  }

  test("boolean term must unificated with self") {
     val t = TermWare.instance.getBooleanSignature.createConstant(false).get;
     val optS = t.termUnify(t,Substitution.empty);
     assert(optS!=None);
  }

  test("true and false term must not be unificated") {
     val t = TermWare.instance.getBooleanSignature.createConstant(true).get;
     val f = TermWare.instance.getBooleanSignature.createConstant(false).get;
     val optS = t.termUnify(f,Substitution.empty);
     assert(optS==None);
  }

}
