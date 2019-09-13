%====================================================================================
% exploration description   
%====================================================================================
mqttBroker("192.168.1.55", "1883").
context(robotmindctx, "192.168.1.55",  "MQTT", "0" ).
context(robotresourcectx, "localhost",  "MQTT", "0" ).
 qactor( planexecutor, robotmindctx, "it.unibo.planexecutor.Planexecutor").
  qactor( robotmind, robotmindctx, "it.unibo.robotmind.Robotmind").
  qactor( onestepahead, robotmindctx, "it.unibo.onestepahead.Onestepahead").
  qactor( robotactuator, robotresourcectx, "it.unibo.robotactuator.Robotactuator").
  qactor( blinkinghandler, robotresourcectx, "it.unibo.blinkinghandler.Blinkinghandler").
  qactor( sonarhandler, robotmindctx, "it.unibo.sonarhandler.Sonarhandler").
  qactor( resourcemodel, robotmindctx, "it.unibo.resourcemodel.Resourcemodel").
