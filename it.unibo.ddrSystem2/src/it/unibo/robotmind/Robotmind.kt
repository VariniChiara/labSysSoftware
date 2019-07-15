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
		var Curmove     = ""  
		var IterCounter = 0 
		var backHome = false
		var maxX = 0
		var maxY = 0
		var finish = false
		
		//VIRTUAL ROBOT
		var StepTime   = 330	 
		 
		var Tback       = 0
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("&&&  robotmind STARTED")
						solve("consult('ddrsys.pl')","") //set resVar	
						solve("consult('resourceModel.pl')","") //set resVar	
						println("Robot intialized")
						itunibo.planner.plannerUtil.initAI(  )
						println("INITIAL MAP")
						itunibo.planner.plannerUtil.showMap(  )
					}
					 transition( edgeName="goto",targetState="waitForStart", cond=doswitch() )
				}	 
				state("waitForStart") { //this:State
					action { //it:State
						println("$name in ${currentState.stateName} | $currentMsg")
					}
					 transition(edgeName="t00",targetState="startExploration",cond=whenDispatch("startCmd"))
					transition(edgeName="t01",targetState="startExplorationTest",cond=whenDispatch("startTest"))
				}	 
				state("startExplorationTest") { //this:State
					action { //it:State
						finish = true
								  backHome = false
						
								  var x = ""
								  var y = ""	
						println("&&&  exploration TEST")
						println("$name in ${currentState.stateName} | $currentMsg")
						if( checkMsgContent( Term.createTerm("startTest(X,Y)"), Term.createTerm("startTest(X,Y)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								 x =payloadArg(0) 
											   y =payloadArg(1) 
								itunibo.planner.plannerUtil.setGoal( x, y  )
								itunibo.planner.moveUtils.doPlan(myself)
						}
					}
					 transition( edgeName="goto",targetState="doPlan", cond=doswitch() )
				}	 
				state("startExploration") { //this:State
					action { //it:State
						println("&&&  exploration STARTED")
						itunibo.planner.plannerUtil.setGoal( "1", "1"  )
						itunibo.planner.moveUtils.doPlan(myself)
					}
					 transition( edgeName="goto",targetState="doPlan", cond=doswitch() )
				}	 
				state("doPlan") { //this:State
					action { //it:State
						itunibo.planner.plannerUtil.showMap(  )
						solve("retract(move(M))","") //set resVar	
						if(currentSolution.isSuccess()) { Curmove = getCurSol("M").toString()
						 }
						else
						{ Curmove="nomove" 
						 }
					}
					 transition( edgeName="goto",targetState="handlemove", cond=doswitchGuarded({(Curmove != "nomove")}) )
					transition( edgeName="goto",targetState="choose", cond=doswitchGuarded({! (Curmove != "nomove")}) )
				}	 
				state("handlemove") { //this:State
					action { //it:State
					}
					 transition( edgeName="goto",targetState="domove", cond=doswitchGuarded({(Curmove != "w")}) )
					transition( edgeName="goto",targetState="attempttogoahead", cond=doswitchGuarded({! (Curmove != "w")}) )
				}	 
				state("domove") { //this:State
					action { //it:State
						itunibo.planner.moveUtils.doPlannedMove(myself ,Curmove )
						forward("robotCmd", "robotCmd($Curmove)" ,"robotactuator" ) 
						delay(700) 
						forward("robotCmd", "robotCmd(h)" ,"robotactuator" ) 
					}
					 transition( edgeName="goto",targetState="doPlan", cond=doswitch() )
				}	 
				state("attempttogoahead") { //this:State
					action { //it:State
						itunibo.planner.moveUtils.attemptTomoveAhead(myself ,StepTime )
					}
					 transition(edgeName="t02",targetState="stepDone",cond=whenDispatch("stepOk"))
					transition(edgeName="t03",targetState="stepFailed",cond=whenDispatch("stepFail"))
				}	 
				state("stepDone") { //this:State
					action { //it:State
						itunibo.planner.moveUtils.doPlannedMove(myself ,"w" )
					}
					 transition( edgeName="goto",targetState="doPlan", cond=doswitch() )
				}	 
				state("stepFailed") { //this:State
					action { //it:State
						println("&&&  FOUND WALL")
						var TbackLong = 0L
						if( checkMsgContent( Term.createTerm("stepFail(R,T)"), Term.createTerm("stepFail(Obs,Time)"), 
						                        currentMsg.msgContent()) ) { //set msgArgList
								Tback=payloadArg(1).toLong().toString().toInt() / 2
											TbackLong = Tback.toLong()
								println("stepFailed ${payloadArg(1).toString()}")
						}
						println(" backToCompensate stepTime=$Tback")
						forward("robotCmd", "robotCmd(s)" ,"robotactuator" ) 
						delay(TbackLong)
						forward("robotCmd", "robotCmd(h)" ,"robotactuator" ) 
						delay(700) 
						itunibo.planner.plannerUtil.wallFound(  )
					}
					 transition( edgeName="goto",targetState="setGoalAfterWall", cond=doswitch() )
				}	 
				state("setGoalAfterWall") { //this:State
					action { //it:State
						solve("retractall(move(_))","") //set resVar	
						
							if( itunibo.planner.plannerUtil.getDirection() == "downDir" ){ 
								maxY = itunibo.planner.plannerUtil.getPosY()
								if(maxX == 0 ){
									itunibo.planner.plannerUtil.setGoal(IterCounter, maxY)
								} else {itunibo.planner.plannerUtil.setGoal(maxX, maxY)}
							} 
							else if( itunibo.planner.plannerUtil.getDirection() == "rightDir" ){ 
								maxX = itunibo.planner.plannerUtil.getPosX()
								if (maxY == 0 ){
									itunibo.planner.plannerUtil.setGoal(maxX, IterCounter)
								} else { itunibo.planner.plannerUtil.setGoal(maxX, maxY) }
							} else {
								itunibo.planner.plannerUtil.setGoal(0, 0)
							}
						itunibo.planner.moveUtils.doPlan(myself)
					}
					 transition( edgeName="goto",targetState="doPlan", cond=doswitch() )
				}	 
				state("choose") { //this:State
					action { //it:State
					}
					 transition( edgeName="goto",targetState="goBackHome", cond=doswitchGuarded({backHome}) )
					transition( edgeName="goto",targetState="nextStep", cond=doswitchGuarded({! backHome}) )
				}	 
				state("goBackHome") { //this:State
					action { //it:State
						backHome = false
						println("&&&  returnToHome")
						itunibo.planner.plannerUtil.setGoal( 0, 0  )
						itunibo.planner.moveUtils.doPlan(myself)
						delay(700) 
					}
					 transition( edgeName="goto",targetState="doPlan", cond=doswitch() )
				}	 
				state("nextStep") { //this:State
					action { //it:State
					}
					 transition( edgeName="goto",targetState="endOfJob", cond=doswitchGuarded({finish}) )
					transition( edgeName="goto",targetState="calculatenextstep", cond=doswitchGuarded({! finish}) )
				}	 
				state("calculatenextstep") { //this:State
					action { //it:State
						IterCounter++
							backHome = true
							if (maxX == 0 && maxY == 0){ itunibo.planner.plannerUtil.setGoal(IterCounter,IterCounter) }
							else if( maxX != 0 && maxY == 0 ){ itunibo.planner.plannerUtil.setGoal(maxX,IterCounter) } 
							else if( maxX == 0 && maxY != 0 ){ itunibo.planner.plannerUtil.setGoal(IterCounter, maxY) } 
							else {
						 		itunibo.planner.plannerUtil.setGoal(maxX, maxY)
								finish = true 
						}
						println("&&&  nextStep")
						itunibo.planner.moveUtils.doPlan(myself)
					}
					 transition( edgeName="goto",targetState="doPlan", cond=doswitch() )
				}	 
				state("endOfJob") { //this:State
					action { //it:State
						if (maxX != 0 && maxY != 0) {itunibo.planner.plannerUtil.fixwalls(maxX, maxY)}
						println("FINAL MAP")
						itunibo.planner.plannerUtil.showMap(  )
						println("&&&  planex0 ENDS")
					}
				}	 
			}
		}
}
