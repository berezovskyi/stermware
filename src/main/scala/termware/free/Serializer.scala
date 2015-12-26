package termware.free

import java.nio._
import termware._
import termware.util._
import termware.TermAttributes.{empty=>emptyAttributes}

// TODO: write input pos on error.

trait Serializer extends TermSerializer
{

   def apply(t: Term, out: Output): Unit = ???

   def unapply(input: Input): Term = ???

   def termSystem: TermSystem


   case class BlockContext(
     val structures: Map[Int, TermStructure] = Map(),
     val invStructures: Map[TermStructure, Int] = Map(),
     val structureIndex: Int = 0,
     val scopes:     Map[Int, Term] = Map(),
     val invScopes:  Map[Term,Int] = Map(),
     val scopeIndex: Int = 0
   ) {

     def withStructure(structure: TermStructure): BlockContext =
     {
       invStructures.get(structure) match {
         case None => val si = structureIndex+1
                      copy(structures = structures.updated(si, structure),
                      invStructures = invStructures.updated(structure,si),
                      structureIndex = si)
         case Some(si) => this
       }
     }

     def withScope(scope: Term): BlockContext =
     {
       invScopes.get(scope) match {
         case None =>
              val si = scopeIndex+1
              copy(scopes = scopes.updated(si,scope),
                   invScopes = invScopes.updated(scope,si),
                   scopeIndex = si)
         case Some(_) => this
       }
     }

   }

    
   final val TAG_TERM = 100
   final val TAG_TERMSTRUCTURE = 101
   final val TAG_ATTRIBUTES = 102

   final val TAG_END = 400
   
   

   final val TAG_ATOM = 1;
   final val TAG_STRUCTURED=2;
   final val TAG_VAR = 3;

   final val TAG_PRIMITIVE = 0x1000;


   private def writeTerm(t: Term, bc: BlockContext, out: Output): BlockContext =
   {
    val nbc0 = t match {
      case a: AtomTerm       => out << TAG_ATOM << a.value ; bc
      case p: PrimitiveTerm  => writePrimitive(p,out)
      case s: StructuredTerm => writeStructured(s,bc,out)
      case v: VarTerm        => writeVar(v,bc,out)
    }     
    writeAttributes(t.attributes,bc,out)
   }

   private def readTerm(in: Input, bc: BlockContext): (Term, BlockContext) =
   {
      val tag = in.readInt
      val (t,nbc0) = tag match {
        case TAG_ATOM => (AtomTerm(in.readString,Map(),termSystem),bc)
        case TAG_STRUCTURED => readStructured(in, bc)
        case TAG_TERMSTRUCTURE => readTerm(in,readTermStructure(in,bc)) 
        case TAG_VAR => readVar(in,bc)
        case x if ((x|TAG_PRIMITIVE)!=0) => (readPrimitive(x,in), bc)
        case _ => throw new IllegalStateException("Invalid term tag:"+tag)
      }
      val (attributes, nbc) = readAttributes(in,nbc0)
      (t withAttributes attributes, nbc)
   }


   private def writePrimitive(p: PrimitiveTerm, out: Output): Unit =
   {
     out << (TAG_PRIMITIVE | p.name.typeIndex)
     p match {
       case StringTerm(v,_,_) => out << v
       case CharTerm(v,_,_)   => out << v
       case Int32Term(v,_,_)  => out << v
       case Int64Term(v,_,_)  => out << v
       case DoubleTerm(v,_,_) => out << v
       case OpaqueTerm(v,_,_) => (out << v.size).write(v)
     }
   }

   private def readPrimitive(tag: Int, in: Input): PrimitiveTerm =
   {
      import NameTypeIndexes._
      (tag & (~TAG_PRIMITIVE)) match {
        case STRING =>  StringTerm(in.readString,Map(),termSystem)
        case CHAR   =>  CharTerm(in.readChar,Map(),termSystem)
        case INT    =>  Int32Term(in.readInt,Map(),termSystem)
        case LONG   =>  Int64Term(in.readLong,Map(),termSystem)
        case DOUBLE =>  DoubleTerm(in.readDouble(),Map(),termSystem) 
        case OPAQUE =>  OpaqueTerm(in.readOpaque(),Map(),termSystem)       
        case _ => throw new IllegalStateException("Invalid primitive tag: "+tag)
      }
   }
 
   private def writeStructured(t: StructuredTerm, bc: BlockContext, out: Output): BlockContext =
   {
      val ts = t.termStructure;
      val nbc0 = bc.invStructures.get(ts) match {
                  case Some(tsi) => bc
                  case None =>
                         val newTsi = bc.structureIndex+1
                         val newTs = bc.structures.updated(newTsi,ts)
                         val newInvTs = bc.invStructures.updated(ts, newTsi)
                         out.writeInt(TAG_TERMSTRUCTURE) 
                         writeStructure(ts,bc,out)
                         bc.copy(structures=newTs, invStructures=newInvTs, structureIndex=newTsi)
      }
      val newScopeIndex = bc.scopeIndex+1
      val newScopes = bc.scopes.updated(newScopeIndex,t)
      val newInvScopes = bc.invScopes.updated(t,newScopeIndex)
      val nbc = nbc0.copy(scopes=newScopes, invScopes=newInvScopes, scopeIndex=newScopeIndex) 
      out.writeInt(TAG_STRUCTURED) 
      out.writeInt(nbc.structureIndex)
      out.writeInt(t.arity)
      t.components.foldLeft(nbc) { (s,e) =>
         writeTerm(e,s,out)
      } 
   }

   private def readStructured(in: Input, bc: BlockContext): (Term, BlockContext) =
   {
     val tsi = in.readInt
     val ts: TermStructure = bc.structures.getOrElse(tsi,throw new IllegalStateException(
                                           "invalid structure index "))
     val arity = in.readInt 
     val subterms = new Array[Term](arity)
     var cbc = bc
     for(i <- (0 until arity)) {
        val (t, nbc) = readTerm(in, cbc)
        subterms(i)=t
        cbc = nbc
     }
     (StructuredTerm(ts,subterms.toIndexedSeq,emptyAttributes,termSystem), cbc)
   }

   private def writeVar(t: VarTerm, bc: BlockContext, out: Output): BlockContext =
   {
     val scopeIndex = t.scope match {
                       case Some(u) => bc.invScopes.getOrElse(u,
                                        throw new IllegalStateException("scope not found in uplevel"))
                       case None => -1
                    }
     out.writeInt(scopeIndex)
     bc
   }

   private def readVar(in: Input, bc: BlockContext): (VarTerm, BlockContext) = ???

   private def writeName(name:Name, out: Output): Unit = ???

   private def readName(in: Input): Name = ???

   private def writeAttributes(attributes: Map[Name,Term], bc: BlockContext, out: Output): BlockContext = 
   {
     out.writeInt(attributes.size)
     attributes.foldLeft(bc){ (s,e) =>
       writeName(e._1,out)
       writeTerm(e._2,s,out)
     }
   }
   
   private def readAttributes(in: Input, bc: BlockContext): (Map[Name,Term], BlockContext) = ???

   private def writeStructure(ts: TermStructure, bc: BlockContext, out: Output): BlockContext = ???

   private def readTermStructure(in: Input, bc: BlockContext): BlockContext = ???


}

object Serializer extends Serializer
{
  def termSystem = FreeTermSystem
}