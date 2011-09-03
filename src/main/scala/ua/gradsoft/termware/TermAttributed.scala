package ua.gradsoft.termware;

import scala.collection.mutable.Map;
import scala.collection.mutable.HashMap;
import ua.gradsoft.termware.flow._;

trait TermAttributed
{
   type AttributeMapType = Map[Name,ComputationBounds[Term]];

   def getAttribute(name:Name):Option[ComputationBounds[Term]]
         = attributes.get(name);

   def setAttribute(name:Name,value:ComputationBounds[Term]):Unit
         = attributes.update(name,value);

   def resetAttribute(name:Name):Option[ComputationBounds[Term]]
        = attributes.remove(name);

   def attributes:AttributeMapType;
}


