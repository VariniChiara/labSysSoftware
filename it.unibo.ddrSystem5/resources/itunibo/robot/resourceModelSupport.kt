package itunibo.robot

import it.unibo.kactor.ActorBasic
import kotlinx.coroutines.launch
import itunibo.coap.modelResourceCoap

object resourceModelSupport{
lateinit var resourcecoap : modelResourceCoap

	fun setCoapResource( rescoap : modelResourceCoap ){
		resourcecoap = rescoap
	}

	fun updateRobotModel( actor: ActorBasic, content: String ){
 			actor.solve(  "action(robot, move($content) )" ) //change the robot state model
			actor.solve(  "model( A, robot, STATE, DIR, POS)" )
			val RobotState = actor.getCurSol("STATE")
			val RobotDir = actor.getCurSol("DIR")
			val RobotPos = actor.getCurSol("POS")
			//println("			resourceModelSupport updateModel RobotState=$RobotState")
			actor.scope.launch{
 				actor.emit( "modelChanged" , "modelChanged(  robot,  $content)" )  //for the robotmind
				actor.emit( "modelContent" , "content( robot( $RobotState, $RobotDir, $RobotPos ) )" ) //for the web server
				resourcecoap.updateState( "robot( $RobotState, $RobotDir, $RobotPos )" )
  			}
	}
	fun updateSonarRobotModel( actor: ActorBasic, content: String ){
 			actor.solve( "action( sonarRobot,  $content )" ) //change the robot state model
			actor.solve( "model( A, sonarRobot, STATE )" )
			val SonarState = actor.getCurSol("STATE")
			//println("			resourceModelSupport updateSonarRobotModel SonarState=$SonarState")
			actor.scope.launch{
 				actor.emit( "modelContent" , "content( sonarRobot( $SonarState ) )" )
				resourcecoap.updateState( "sonarRobot( $SonarState )" )
 			}
	}

	fun updateRoomMapModel( actor: ActorBasic, content: String ){
	println("			resourceModelSupport updateRoomMapModel content=$content")
		actor.scope.launch{
			actor.emit( "modelContent" , "content( roomMap( state( '$content' ) ) )" )
			resourcecoap.updateState( "roomMap( '$content' )" )
		}
	}

}
