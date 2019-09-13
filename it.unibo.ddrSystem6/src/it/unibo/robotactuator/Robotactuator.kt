/* Generated by AN DISI Unibo */ 
package it.unibo.robotactuator

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Robotactuator ( name: String, scope: CoroutineScope ) : ActorBasicFsm( name, scope){
 	
	override fun getInitialState() : String{
		return "s0"
	}
		
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						solve("consult('basicRobotConfig.pl')","") //set resVar	
						solve("robot(R,PORT)","") //set resVar	
						if(currentSolution.isSuccess()) { println("USING ROBOT : ${getCurSol("R")},  port= ${getCurSol("PORT")} ")
						itunibo.robot.robotSupport.create(myself ,getCurSol("R").toString(), getCurSol("PORT").toString(), null )
						 }
						else
						{ println("no robot")
						 }
						itunibo.robot.robotSupport.move( "msg(a)"  )
						delay(500) 
						itunibo.robot.robotSupport.move( "msg(d)"  )
						delay(500) 
						itunibo.robot.robotSupport.move( "msg(h)"  )
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
				state("waitCmd") { //this:State
					action { //it:State
					}
					 transition(edgeName="t032",targetState="handleRobotCmd",cond=whenDispatch("robotCmd"))
				}	 
				state("handleRobotCmd") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("robotCmd(CMD)"), Term.createTerm("robotCmd(MOVE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								if((payloadArg(0) == "z")){ itunibo.robot.robotSupport.move( "msg(a)"  )
								delay(100) 
								itunibo.robot.robotSupport.move( "msg(h)"  )
								 }
								else
								 { if((payloadArg(0) == "x")){ itunibo.robot.robotSupport.move( "msg(d)"  )
								 delay(100) 
								 itunibo.robot.robotSupport.move( "msg(h)"  )
								  }
								 else
								  { itunibo.robot.robotSupport.move( "msg(${payloadArg(0)})"  )
								   }
								  }
						}
					}
					 transition( edgeName="goto",targetState="waitCmd", cond=doswitch() )
				}	 
			}
		}
}
