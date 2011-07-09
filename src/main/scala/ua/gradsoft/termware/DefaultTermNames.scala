package ua.gradsoft.termware;

trait DefaultTermNames
{

   def theory: Theory;

   lazy val Rule = theory.symbolTable.getOrCreate("rule");
   lazy val ConditionalRule = theory.symbolTable.getOrCreate("conditionalRule");
    
}