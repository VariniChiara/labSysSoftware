/* Generated by AN DISI Unibo */ 
package it.unibo.robotmind

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Robotmind ( name: String, scope: CoroutineScope ) : ActorBasicFsm( name, scope){
 	
	override fun getInitialState() : String{
		return "s0"
	}
		
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						solve("consult('resourceModel.pl')","") //set resVar	
						println("ROBOT MIND STARTED")
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("waitCmd") { //this:State
					action { //it:State
					}
					 transition(edgeName="t00",targetState="handleUserCmd",cond=whenEvent("userCmd"))
				}	 
				state("handleUserCmd") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("userCmd(X)"), Term.createTerm("userCmd(X)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								forward("robotCmd", "robotCmd(${payloadArg(0)})" ,"robotactuator" ) 
								solve("action(robot,move(${payloadArg(0)}))","") //set resVar	
								solve("showResourceModel","") //set resVar	
						}
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
			}
		}
}
