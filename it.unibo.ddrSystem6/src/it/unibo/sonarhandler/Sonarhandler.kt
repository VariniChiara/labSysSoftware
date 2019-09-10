/* Generated by AN DISI Unibo */ 
package it.unibo.sonarhandler

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Sonarhandler ( name: String, scope: CoroutineScope ) : ActorBasicFsm( name, scope){
 	
	override fun getInitialState() : String{
		return "s0"
	}
		
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		var foundObstacle = false
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("========== sonarhandler: s0 ==========")
					}
					 transition( edgeName="goto",targetState="waitForEvents", cond=doswitch() )
				}	 
				state("waitForEvents") { //this:State
					action { //it:State
						println("========== sonarhandler: waitForEvents ==========")
					}
					 transition(edgeName="t022",targetState="handleSonar",cond=whenEvent("sonarRobot"))
				}	 
				state("handleSonar") { //this:State
					action { //it:State
						println("========== sonarhandler: handleSonar ==========")
						if( checkMsgContent( Term.createTerm("sonar(DISTANCE)"), Term.createTerm("sonar(DISTANCE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								if((payloadArg(0).toInt() <= 10)){ println("$name in ${currentState.stateName} | $currentMsg")
								println("trueee")
								foundObstacle = true
								forward("sonar", "sonar" ,"onestepahead" ) 
								 }
						}
					}
					 transition( edgeName="goto",targetState="waitToDiscard", cond=doswitchGuarded({(foundObstacle)}) )
					transition( edgeName="goto",targetState="waitForEvents", cond=doswitchGuarded({! (foundObstacle)}) )
				}	 
				state("waitToDiscard") { //this:State
					action { //it:State
					}
					 transition(edgeName="t023",targetState="discardSonar",cond=whenEvent("sonarRobot"))
				}	 
				state("discardSonar") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("sonar(DISTANCE)"), Term.createTerm("sonar(DISTANCE)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								if((payloadArg(0).toInt() > 10)){ println("falseeeee")
								foundObstacle = false
								 }
						}
					}
					 transition( edgeName="goto",targetState="waitToDiscard", cond=doswitchGuarded({(foundObstacle)}) )
					transition( edgeName="goto",targetState="waitForEvents", cond=doswitchGuarded({! (foundObstacle)}) )
				}	 
			}
		}
}
