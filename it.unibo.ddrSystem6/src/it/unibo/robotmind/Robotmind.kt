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
		
			var iterCounter = 1
			var X = iterCounter
			var Y = iterCounter
			var backHome = true
		
			var plan : List<aima.core.agent.Action>? = null
			var dirtyCell : Pair<Int,Int>? = null
			var Luggage_num = 0
			var Map = ""
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
					 transition(edgeName="t05",targetState="startExploration",cond=whenEvent("startCmd"))
					transition(edgeName="t06",targetState="startExploration",cond=whenEvent("temperatureOk"))
				}	 
				state("startExploration") { //this:State
					action { //it:State
						println("&&&  exploration STARTED")
						itunibo.planner.plannerUtil.setGoal( X, Y  )
						forward("doPlan", "doPlan($X,$Y)" ,"planexecutor" ) 
					}
					 transition(edgeName="t17",targetState="stopAppl",cond=whenEvent("stopCmd"))
					transition(edgeName="t18",targetState="nextGoal",cond=whenDispatch("planOk"))
					transition(edgeName="t19",targetState="newLuggageFound",cond=whenDispatch("planFail"))
					transition(edgeName="t110",targetState="stopAppl",cond=whenEvent("temperatureTooHigh"))
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
						dirtyCell = itunibo.planner.moveUtils.getDirtyCell()
						if((dirtyCell != null)){ X = dirtyCell!!.first
									Y = dirtyCell!!.second
						itunibo.planner.plannerUtil.setGoal( X, Y  )
						 }
						else
						 { if(backHome){ 
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
					}
					 transition( edgeName="goto",targetState="startExploration", cond=doswitch() )
				}	 
				state("newLuggageFound") { //this:State
					action { //it:State
						Luggage_num++
						forward("modelUpdate", "modelUpdate(luggage,$Luggage_num)" ,"resourcemodel" ) 
						itunibo.planner.moveUtils.setObstacleOnCurrentDirection(myself)
						Map =  itunibo.planner.plannerUtil.getMapOneLine()
						forward("modelUpdate", "modelUpdate(roomMap,$Map)" ,"resourcemodel" ) 
					}
					 transition(edgeName="t211",targetState="handleObstacle",cond=whenDispatch("luggageSafe"))
					transition(edgeName="t212",targetState="endExploration",cond=whenDispatch("luggageDanger"))
				}	 
				state("handleObstacle") { //this:State
					action { //it:State
						println("---CheckIfObstacle---")
						itunibo.planner.moveUtils.setObstacleOnCurrentDirection(myself)
						itunibo.planner.plannerUtil.resetGoal( X, Y  )
						itunibo.planner.moveUtils.setObstacleOnCurrentDirection(myself)
						plan = itunibo.planner.plannerUtil.doPlan()
					}
					 transition( edgeName="goto",targetState="checkPlan", cond=doswitch() )
				}	 
				state("checkPlan") { //this:State
					action { //it:State
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
						dirtyCell = itunibo.planner.moveUtils.getDirtyCell()
						println("---finishChecking---")
					}
					 transition( edgeName="goto",targetState="exploreDirtyCell", cond=doswitchGuarded({(dirtyCell != null)}) )
					transition( edgeName="goto",targetState="endExploration", cond=doswitchGuarded({! (dirtyCell != null)}) )
				}	 
				state("exploreDirtyCell") { //this:State
					action { //it:State
						println("---exploreDirtyCell---")
						
									 X = dirtyCell!!.first
									 Y = dirtyCell!!.second
						itunibo.planner.plannerUtil.setGoal( X, Y  )
						plan = itunibo.planner.plannerUtil.doPlan()
					}
					 transition( edgeName="goto",targetState="doExploration", cond=doswitchGuarded({(plan != null)}) )
					transition( edgeName="goto",targetState="endExploration", cond=doswitchGuarded({! (plan != null)}) )
				}	 
				state("doExploration") { //this:State
					action { //it:State
						forward("doPlan", "doPlan($X,$Y)" ,"planexecutor" ) 
					}
					 transition(edgeName="t213",targetState="stopAppl",cond=whenEvent("stopCmd"))
					transition(edgeName="t214",targetState="finishChecking",cond=whenDispatch("planOk"))
					transition(edgeName="t215",targetState="setObstacle",cond=whenDispatch("planFail"))
					transition(edgeName="t216",targetState="stopAppl",cond=whenEvent("temperatureTooHigh"))
				}	 
				state("setObstacle") { //this:State
					action { //it:State
						println("---setObstacle---")
						Luggage_num++
						forward("modelUpdate", "modelUpdate(luggage,$Luggage_num)" ,"resourcemodel" ) 
						itunibo.planner.moveUtils.setObstacleOnCurrentDirection(myself)
						Map =  itunibo.planner.plannerUtil.getMapOneLine()
						forward("modelUpdate", "modelUpdate(roomMap,$Map)" ,"resourcemodel" ) 
					}
					 transition(edgeName="t117",targetState="endExploration",cond=whenDispatch("luggageDanger"))
					transition(edgeName="t118",targetState="finishChecking",cond=whenDispatch("luggageSafe"))
				}	 
				state("endExploration") { //this:State
					action { //it:State
						println("---endExploration---")
						itunibo.planner.plannerUtil.setGoal( 0, 0  )
						forward("doPlan", "doPlan(0,0)" ,"planexecutor" ) 
					}
				}	 
			}
		}
}
