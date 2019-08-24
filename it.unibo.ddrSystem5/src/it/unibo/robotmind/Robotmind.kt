/* Generated by AN DISI Unibo */ 
package it.unibo.robotmind

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import aima.core.agent.Action
	
class Robotmind ( name: String, scope: CoroutineScope ) : ActorBasicFsm( name, scope){
 	
	override fun getInitialState() : String{
		return "s0"
	}
		
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		
		//import aima.core.agent.Action
			var iterCounter = 1
			var X = iterCounter
			var Y = iterCounter
			var backHome = true
		
			var plan : List<Action>? = null
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						solve("consult('resourceModel.pl')","") //set resVar	
						println("&&&  robotmind STARTED")
						itunibo.planner.plannerUtil.initAI(  )
						println("INITIAL MAP")
						itunibo.planner.plannerUtil.showMap(  )
					}
					 transition( edgeName="goto",targetState="waitForStart", cond=doswitch() )
				}	 
				state("waitForStart") { //this:State
					action { //it:State
					}
					 transition(edgeName="t05",targetState="startExploration",cond=whenDispatch("startCmd"))
				}	 
				state("startExploration") { //this:State
					action { //it:State
						println("&&&  exploration STARTED")
						itunibo.planner.plannerUtil.setGoal( X, Y  )
						forward("doPlan", "doPlan($X,$Y)" ,"planexecutor" ) 
					}
					 transition(edgeName="t16",targetState="stopAppl",cond=whenEvent("stopCmd"))
					transition(edgeName="t17",targetState="nextGoal",cond=whenDispatch("planOk"))
					transition(edgeName="t18",targetState="checkIfObstacle",cond=whenDispatch("planFail"))
				}	 
				state("stopAppl") { //this:State
					action { //it:State
						println("%% robotmind stopped %%")
						forward("stopCmd", "stopCmd" ,"planexecutor" ) 
					}
					 transition( edgeName="goto",targetState="waitForStart", cond=doswitch() )
				}	 
				state("nextGoal") { //this:State
					action { //it:State
						if(backHome){ 
									backHome = false
									X = 0
									Y = 0
									iterCounter++
						 }
						else
						 { 
						 			backHome = true
						 			X = iterCounter
						 			Y = iterCounter
						  }
					}
					 transition( edgeName="goto",targetState="startExploration", cond=doswitch() )
				}	 
				state("checkIfObstacle") { //this:State
					action { //it:State
						println("---CheckIfObstacle---")
						itunibo.planner.moveUtils.setObstacleOnCurrentDirection(myself)
						itunibo.planner.plannerUtil.resetGoal( X, Y  )
						plan = itunibo.planner.plannerUtil.doPlan()
					}
					 transition( edgeName="goto",targetState="startExploration", cond=doswitchGuarded({(plan != null)}) )
					transition( edgeName="goto",targetState="checkNull", cond=doswitchGuarded({! (plan != null)}) )
				}	 
				state("checkNull") { //this:State
					action { //it:State
						println("---CheckNull---")
					}
					 transition( edgeName="goto",targetState="nextGoal", cond=doswitchGuarded({(!itunibo.planner.plannerUtil.currentGoalApplicable)}) )
					transition( edgeName="goto",targetState="finishChecking", cond=doswitchGuarded({! (!itunibo.planner.plannerUtil.currentGoalApplicable)}) )
				}	 
				state("finishChecking") { //this:State
					action { //it:State
						println("---finishChecking---")
						itunibo.planner.plannerUtil.setGoal( 0, 0  )
						forward("doPlan", "doPlan(0,0)" ,"planexecutor" ) 
					}
				}	 
			}
		}
}
