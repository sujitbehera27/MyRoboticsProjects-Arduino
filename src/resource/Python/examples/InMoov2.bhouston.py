#Bhouston script - modified Gael's script
 
# this script is provided as a basic guide
# most parts can be run by uncommenting them
# InMoov now can be started in modular pieces
 
leftPort = "COM7"
rightPort = "COM3"
 
i01 = Runtime.createAndStart("i01", "InMoov")
cleverbot = Runtime.createAndStart("cleverbot","CleverBot")
 
# starts everything
#i01.startAll(leftPort, rightPort)
 
# starting parts
i01.startMouth()
#to tweak the default voice
i01.mouth.setGoogleURI("http://thehackettfamily.org/Voice_api/api2.php?voice=Will&txt=")
 
 
i01.startHead(leftPort)
i01.startMouthControl(leftPort)
i01.startEar()
i01.startLeftHand(leftPort)
i01.startLeftArm(leftPort)
i01.startRightHand(rightPort)
i01.startRightArm(rightPort)
i01.startHeadTracking(leftPort)
i01.startEyesTracking(leftPort)
 
# starting part with a reference, with a reference
# you can interact further
#opencv = i01.startOpenCV()
#opencv.startCapture()
# or you can use i01's reference
#i01.opencv.startCapture()
 
#i01.headTracking.faceDetect()
#i01.eyesTracking.faceDetect()
#i01.headTracking.pyramidDown()
 
 
# after a start you may call detach to detach all
# currently attached servos
#i01.detach()
#i01.attach()
 
# auto detaches any attached servos after 120 seconds of inactivity
i01.autoPowerDownOnInactivity(120)
 
###########################
i01.startPIR("COM7",28)
 
 
 
def input():
    print 'python object is ', msg_clock_pulse
    pin = msg_i01_right_publishPin.data[0]
    print 'pin data is ', pin.pin, pin.value
    if (pin.value == 1):
      #i01.powerUp()
      mouth.speak("who's there")
      i01.head.neck.moveTo(85)
      sleep(2)
      i01.head.neck.moveTo(90)
###########################
 
############################################################
#if needed we can tweak the default settings with these lines
#i01.head.eyeY.map(0,180,80,100)
#i01.head.eyeY.setRest(78)
#i01.head.eyeX.map(0,180,75,100)
#i01.head.eyeX.setRest(78)
 
i01.head.eyeY.setMinMax(80,100)
i01.head.eyeY.setRest(80)
i01.head.eyeX.setMinMax(75,100)
i01.head.eyeX.setRest(78)
i01.head.neck.setRest(80)
#i01.head.jaw.map(0,180,60,75)
i01.head.jaw.setMinMax(75,60)
i01.mouthControl.setmouth(75,60)
# tweaking right hand
i01.rightHand.thumb.map(0,180,10,170)
i01.rightHand.index.map(0,180,0,160)
i01.rightHand.majeure.map(0,180,0,140)
i01.rightHand.ringFinger.map(0,180,48,145)
i01.rightHand.pinky.map(0,180,10,170)
# tweaking left hand
i01.leftHand.thumb.map(0,180,10,170)
i01.leftHand.index.map(0,180,40,140)
i01.leftHand.majeure.map(0,180,30,118)
i01.leftHand.ringFinger.map(0,180,25,134)
i01.leftHand.pinky.map(0,180,35,130)
############################################################
#to tweak the default PID values
i01.headTracking.xpid.setPID(10.0,5.0,0.1)
i01.headTracking.ypid.setPID(15.0,5.0,0.1)
i01.eyesTracking.xpid.setPID(15.0,5.0,0.1)
i01.eyesTracking.ypid.setPID(22.0,5.0,0.1)
############################################################
 
#i01.speakErrors(false)
# purges any "auto" methods
#i01.purgeAllTasks()
 
# remote control services
# WebGUI - for more information see
# http://myrobotlab.org/service/WebGUI
 
# XMPP - for more information see
# http://myrobotlab.org/service/XMPP
 
# system check - called at anytime
#i01.systemCheck()
 
# take the current position of all attached servos <- FIXME
# and create a new method named "newGesture"
#i01.captureGesture("newGesture")
 
# all ear associations are done python startEar() only starts
# the peer service
# After ear.startListening(), the ear will listen for commands
 
# i01.systemCheck()
 
i01.mouth.speakBlocking(cleverbot.chat("hi"))
#i01.mouth.speakBlocking(cleverbot.chat("how are you"))
 
# verbal commands
ear = i01.ear
 
ear.addCommand("rest", i01.getName(), "rest")
 
ear.addCommand("attach head", "i01.head", "attach")
ear.addCommand("disconnect head", "i01.head", "detach")
ear.addCommand("attach eyes", "i01.head.eyeY", "attach")
ear.addCommand("disconnect eyes", "i01.head.eyeY", "detach")
ear.addCommand("attach right hand", "i01.rightHand", "attach")
ear.addCommand("disconnect right hand", "i01.rightHand", "detach")
ear.addCommand("attach left hand", "i01.leftHand", "attach")
ear.addCommand("disconnect left hand", "i01.leftHand", "detach")
ear.addCommand("attach all", "i01", "attach")
ear.addCommand("disconnect all", "i01", "detach")
ear.addCommand("attach left arm", "i01.leftArm", "attach")
ear.addCommand("disconnect left arm", "i01.leftArm", "detach")
ear.addCommand("attach right arm", "i01.rightArm", "attach")
ear.addCommand("disconnect right arm", "i01.rightArm", "detach")
ear.addCommand("search humans", "python", "trackHumans")
ear.addCommand("quit search", "python", "stopTracking")
ear.addCommand("track", "python", "trackPoint")
ear.addCommand("freeze track", "python", "stopTracking")
 
ear.addCommand("open hand", "python", "handopen")
ear.addCommand("close hand", "python", "handclose")
ear.addCommand("camera on", i01.getName(), "cameraOn")
ear.addCommand("off camera", i01.getName(), "cameraOff")
ear.addCommand("capture gesture", i01.getName(), "captureGesture")
# FIXME - lk tracking setpoint
#ear.addCommand("track", i01.getName(), "track")
#ear.addCommand("freeze track", i01.getName(), "clearTrackingPoints")
#ear.addCommand("hello", i01.getName(), "hello")
ear.addCommand("hello", "python", "hello")
ear.addCommand("giving", i01.getName(), "giving")
ear.addCommand("fighter", i01.getName(), "fighter")
ear.addCommand("fist hips", i01.getName(), "fistHips")
ear.addCommand("look at this", i01.getName(), "lookAtThis")
ear.addCommand("victory", i01.getName(), "victory")
ear.addCommand("arms up", i01.getName(), "armsUp")
ear.addCommand("arms front", i01.getName(), "armsFront")
ear.addCommand("da vinci", i01.getName(), "daVinci")
ear.addCommand("what can i do","python", "whatcanido")
# FIXME -  
ear.addCommand("manual", ear.getName(), "lockOutAllGrammarExcept", "voice control")
ear.addCommand("voice control", ear.getName(), "clearLock")
ear.addCommand("stop listening", ear.getName(), "stopListening")
 
##sets the servos back to full speed, anywhere in sequence or gestures
ear.addCommand("full speed", "python", "fullspeed")
##sequence1
ear.addCommand("grab the bottle", "python", "grabthebottle")
ear.addCommand("take the glass", "python", "grabtheglass")
ear.addCommand("poor bottle", "python", "poorbottle")
ear.addCommand("give the glass", "python", "givetheglass")
##sequence2
ear.addCommand("take the ball", "python", "takeball")
ear.addCommand("keep the ball", "python", "keepball")
ear.addCommand("approach the left hand", "python", "approachlefthand")
ear.addCommand("use the left hand", "python", "uselefthand")
ear.addCommand("more", "python", "more")
ear.addCommand("hand down", "python", "handdown")
ear.addCommand("is it a ball", "python", "isitaball")
ear.addCommand("put it down", "python", "putitdown")
ear.addCommand("drop it", "python", "dropit")
ear.addCommand("remove your left arm", "python", "removeleftarm")
ear.addCommand("relax", "python", "relax")
##extras
ear.addCommand("perfect", "python", "perfect")
ear.addCommand("delicate grab", "python", "delicategrab")
ear.addCommand("release delicate", "python", "releasedelicate")
ear.addCommand("open your right hand", "python", "openrighthand")
ear.addCommand("open your left hand", "python", "openlefthand")
ear.addCommand("close your right hand", "python", "closerighthand")
ear.addCommand("close your left hand", "python", "closelefthand")
ear.addCommand("surrender", "python", "surrender")
ear.addCommand("picture on the right side", "python", "picturerightside")
ear.addCommand("picture on the left side", "python", "pictureleftside")
ear.addCommand("picture on both sides", "python", "picturebothside")
ear.addCommand("look on the right side", "python", "lookrightside")
ear.addCommand("look on your left side", "python", "lookleftside")
ear.addCommand("look in your middle", "python", "lookinmiddle")
ear.addCommand("before happy", "python", "beforehappy")
ear.addCommand("happy birthday", "python", "happy")
#ear.addCommand("photo", "python", "photo")
ear.addCommand("about", "python", "about")
ear.addCommand("power down", "python", "powerdown")
ear.addCommand("power up", "python", "powerup")
ear.addCommand("servo", "python", "servos")
ear.addCommand("how many fingers do you have", "python", "howmanyfingersdoihave")
 
ear.addComfirmations("yes","correct","ya","yeah")
ear.addNegations("no","wrong","nope","nah")
 
ear.startListening()
 
#def findFace():
  #i01.headTracking.faceDetect(True)
  #i01.eyesTracking.faceDetect(True)
  #i01.headTracking.pyramidDown(True)
 
#def stopScan():
  #i01.headTracking.faceDetect(False)
  #i01.eyesTracking.faceDetect(False)
  #i01.headTracking.pyramidDown(False)
 
#def track():
  #i01.headTracking.startLKTracking()
 
def trackHumans():
     i01.headTracking.faceDetect()
     i01.eyesTracking.faceDetect()
 
def trackPoint():
     i01.headTracking.startLKTracking()
     i01.eyesTracking.startLKTracking()
 
def stopTracking():
     i01.headTracking.stopTracking()
     i01.eyesTracking.stopTracking()
 
 
def fullspeed():
  i01.setHandSpeed("left", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
  i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
  i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
  i01.setArmSpeed("right", 1.0, 1.0, 1.0, 1.0)
  i01.setHeadSpeed(1.0, 1.0)
 
def delicategrab():
  i01.setHandSpeed("left", 0.70, 0.60, 1.0, 1.0, 1.0, 1.0)
  i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
  i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
  i01.setArmSpeed("right", 1.0, 1.0, 1.0, 1.0)
  i01.setHeadSpeed(0.65, 0.75)
  i01.moveHead(21,98)
  i01.moveArm("left",30,72,77,10)
  i01.moveArm("right",0,91,28,17)
  i01.moveHand("left",180,130,4,0,0,180)
  i01.moveHand("right",86,51,133,162,153,180)
 
def perfect():
  i01.setHandSpeed("left", 0.80, 0.80, 1.0, 1.0, 1.0, 1.0)
  i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
  i01.setArmSpeed("left", 0.85, 0.85, 0.85, 0.95)
  i01.setArmSpeed("right", 1.0, 1.0, 1.0, 1.0)
  i01.setHeadSpeed(0.65, 0.75)
  i01.moveHead(88,79)
  i01.moveArm("left",89,75,93,11)
  i01.moveArm("right",0,91,28,17)
  i01.moveHand("left",130,160,83,40,0,34)
  i01.moveHand("right",86,51,133,162,153,180)
 
def releasedelicate():
  i01.setHandSpeed("left", 0.60, 0.60, 1.0, 1.0, 1.0, 1.0)
  i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
  i01.setArmSpeed("left", 0.75, 0.75, 0.75, 0.95)
  i01.setArmSpeed("right", 1.0, 1.0, 1.0, 1.0)
  i01.setHeadSpeed(0.65, 0.75)
  i01.moveHead(20,98)
  i01.moveArm("left",30,72,64,10)
  i01.moveArm("right",0,91,28,17)
  i01.moveHand("left",101,74,66,58,44,180)
  i01.moveHand("right",86,51,133,162,153,180)
 
def grabthebottle():
  i01.setHandSpeed("left", 1.0, 0.80, 0.80, 0.80, 1.0, 0.80)
  i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
  i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
  i01.setArmSpeed("right", 1.0, 1.0, 1.0, 1.0)
  i01.setHeadSpeed(0.90, 0.80)
  i01.moveHead(20,88)
  i01.moveArm("left",77,85,45,15)
  i01.moveArm("right",0,90,30,10)
  i01.moveHand("left",180,138,140,164,180,60)
  i01.moveHand("right",0,0,0,0,0,90)
 
def grabtheglass():
  i01.setHandSpeed("left", 0.60, 0.60, 1.0, 1.0, 1.0, 1.0)
  i01.setHandSpeed("right", 1.0, 0.60, 0.60, 1.0, 1.0, 0.70)
  i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
  i01.setArmSpeed("right", 1.0, 1.0, 1.0, 1.0)
  i01.setHeadSpeed(0.65, 0.65)
  i01.moveHead(20,68)
  i01.moveArm("left",77,85,45,15)
  i01.moveArm("right",48,91,72,10)
  i01.moveHand("left",180,138,140,164,180,60)
  i01.moveHand("right",140,112,127,105,143,133)
 
def poorbottle():
  i01.setHandSpeed("left", 0.60, 0.60, 0.60, 0.60, 0.60, 0.60)
  i01.setHandSpeed("right", 0.60, 0.80, 0.60, 0.60, 0.60, 0.60)
  i01.setArmSpeed("left", 0.60, 0.60, 0.60, 0.60)
  i01.setArmSpeed("right", 0.60, 0.60, 0.60, 0.60)
  i01.setHeadSpeed(0.65, 0.65)
  i01.moveHead(0,92)
  i01.moveArm("left",53,40,95,55)
  i01.moveArm("right",80,59,43,10)
  i01.moveHand("left",180,140,150,164,180,0)
  i01.moveHand("right",145,112,127,105,143,133)
 
def givetheglass():
  i01.headTracking.faceDetect()
  i01.eyesTracking.faceDetect()
  i01.setHandSpeed("left", 0.60, 0.60, 0.60, 0.60, 0.60, 0.60)
  i01.setHandSpeed("right", 0.60, 0.80, 0.60, 0.60, 0.60, 0.60)
  i01.setArmSpeed("left", 0.60, 0.60, 0.60, 0.60)
  i01.setArmSpeed("right", 0.60, 0.60, 0.60, 0.60)
  i01.setHeadSpeed(0.65, 0.65)
  i01.moveHead(84,79)
  i01.moveArm("left",77,75,45,17)
  i01.moveArm("right",21,80,77,10)
  i01.moveHand("left",109,138,180,164,180,60)
  i01.moveHand("right",102,86,105,105,143,133)
  i01.mouth.speakBlocking("please, take the glass")
 
def takeball():
  i01.setHandSpeed("right", 0.75, 0.75, 0.75, 0.75, 0.85, 0.75)
  i01.setArmSpeed("right", 0.85, 0.85, 0.85, 0.85)
  i01.setHeadSpeed(0.65, 0.65)
  i01.moveHead(30,70)
  i01.moveArm("left",0,84,16,15)
  i01.moveArm("right",6,73,76,16)
  i01.moveHand("left",50,50,40,20,20,90)
  i01.moveHand("right",150,153,153,153,153,11)
 
 
def keepball():
  i01.setHandSpeed("left", 0.65, 0.65, 0.65, 0.65, 0.65, 1.0)
  i01.setHandSpeed("right", 0.65, 0.65, 0.65, 0.65, 0.65, 1.0)
  i01.setArmSpeed("right", 0.75, 0.85, 0.95, 0.85)
  i01.setArmSpeed("left", 0.75, 0.85, 0.95, 0.85)
  i01.setHeadSpeed(0.65, 0.65)
  i01.moveHead(20,70)
  i01.moveArm("left",0,84,16,15)
  i01.moveArm("right",54,77,55,16)
  i01.moveHand("left",50,65,80,46,74,90)
  i01.moveHand("right",40,40,40,106,180,0)
 
def approachlefthand():
  i01.setHandSpeed("right", 0.75, 0.75, 0.75, 0.75, 0.75, 0.65)
  i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
  i01.setArmSpeed("right", 0.25, 0.25, 0.25, 0.25)
  i01.setHeadSpeed(0.65, 0.65)
  i01.moveHead(20,70)
  i01.moveArm("left",90,52,59,23)
  i01.moveArm("right",54,77,55,16)
  i01.moveHand("left",0,0,30,10,10,15)
  i01.moveHand("right",30,30,30,106,180,0)
 
def uselefthand():
  i01.setHandSpeed("right", 0.75, 0.75, 0.75, 0.75, 0.75, 0.65)
  i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
  i01.setArmSpeed("right", 0.25, 0.25, 0.25, 0.25)
  i01.setHeadSpeed(0.65, 0.65)
  i01.moveHead(18,84)
  i01.moveArm("left",90,52,59,23)
  i01.moveArm("right",60,64,55,16)
  i01.moveHand("left",0,0,30,10,10,15)
  i01.moveHand("right",20,20,20,106,180,0)
 
 
def more():
  i01.setHandSpeed("right", 0.75, 0.75, 0.75, 0.75, 0.75, 0.65)
  i01.setArmSpeed("left", 0.85, 0.85, 0.85, 0.95)
  i01.setArmSpeed("right", 0.75, 0.65, 0.65, 0.65)
  i01.setHeadSpeed(0.65, 0.65)
  i01.moveHead(16,84)
  i01.moveArm("left",90,52,59,23)
  i01.moveArm("right",65,56,59,16)
  i01.moveHand("left",145,75,148,85,10,15)
  i01.moveHand("right",20,20,20,106,180,0)
 
 
 
def handdown():
  i01.setHandSpeed("left", 0.75, 0.75, 0.75, 0.75, 0.75, 0.75)
  i01.setHandSpeed("right", 0.70, 0.70, 0.70, 0.70, 0.70, 1.0)
  i01.moveHead(16,84)
  i01.moveArm("left",90,52,59,23)
  i01.moveArm("right",39,56,59,16)
  i01.moveHand("left",145,75,148,85,10,15)
  i01.moveHand("right",103,66,84,106,180,0)
 
def isitaball():
  i01.setHandSpeed("left", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
  i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
  i01.setArmSpeed("right", 0.75, 0.85, 0.95, 0.85)
  i01.setArmSpeed("left", 0.75, 0.85, 0.90, 0.85)
  i01.setHeadSpeed(0.65, 0.75)
  i01.moveHead(90,83)
  i01.moveArm("left",70,64,87,15)
  i01.moveArm("right",0,82,33,15)
  i01.moveHand("left",147,130,140,34,34,164)
  i01.moveHand("right",20,40,40,30,30,80)
  sleep(2)
 
def putitdown():
  i01.setHandSpeed("left", 0.90, 0.90, 0.90, 0.90, 0.90, 0.90)
  i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
  i01.setArmSpeed("right", 0.75, 0.85, 0.95, 0.85)
  i01.setArmSpeed("left", 0.75, 0.85, 0.95, 0.85)
  i01.setHeadSpeed(0.75, 0.75)
  i01.moveHead(20,99)
  i01.moveArm("left",1,45,87,31)
  i01.moveArm("right",0,82,33,15)
  i01.moveHand("left",147,130,135,34,34,35)
  i01.moveHand("right",20,40,40,30,30,72)
  sleep(2)
 
def dropit():
  i01.setHandSpeed("left", 0.85, 0.85, 0.85, 0.85, 0.85, 0.85)
  i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
  i01.setArmSpeed("right", 0.75, 0.85, 0.95, 0.85)
  i01.setArmSpeed("left", 0.75, 0.85, 1.0, 0.85)
  i01.setHeadSpeed(0.75, 0.75)
  i01.moveHead(20,99)
  i01.moveArm("left",1,45,87,31)
  i01.moveArm("right",0,82,33,15)
  sleep(3)
  i01.moveHand("left",60,61,67,34,34,35)
  i01.moveHand("right",20,40,40,30,30,72)
 
 
def removeleftarm():
  i01.setHandSpeed("left", 0.85, 0.85, 0.85, 0.85, 0.85, 0.85)
  i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
  i01.setArmSpeed("right", 0.75, 0.85, 0.95, 0.85)
  i01.setArmSpeed("left", 0.95, 0.65, 0.75, 0.75)
  i01.setHeadSpeed(0.75, 0.75)
  i01.moveHead(20,100)
  i01.moveArm("left",71,94,41,31)
  i01.moveArm("right",0,82,28,15)
  i01.moveHand("left",60,43,45,34,34,35)
  i01.moveHand("right",20,40,40,30,30,72)
 
 
def relax():
  i01.setHandSpeed("left", 0.85, 0.85, 0.85, 0.85, 0.85, 0.85)
  i01.setHandSpeed("right", 0.85, 0.85, 0.85, 0.85, 0.85, 0.85)
  i01.setArmSpeed("right", 0.75, 0.85, 0.65, 0.85)
  i01.setArmSpeed("left", 0.95, 0.65, 0.75, 0.75)
  i01.setHeadSpeed(0.75, 0.75)
  i01.moveHead(79,100)
  i01.moveArm("left",5,94,28,15)
  i01.moveArm("right",5,82,28,15)
  i01.moveHand("left",42,10,20,30,50,35)
  i01.moveHand("right",81,50,82,60,105,113)
 
def handopen():
  i01.moveHand("left",0,0,0,0,0)
  i01.moveHand("right",0,0,0,0,0)
 
def handclose():
  i01.moveHand("left",180,180,180,180,180)
  i01.moveHand("right",180,180,180,180,180)
 
def openlefthand():
  i01.moveHand("left",0,0,0,0,0)
 
 
def openrighthand():
  i01.moveHand("right",0,0,0,0,0)
 
def closelefthand():
  i01.moveHand("left",180,180,180,180,180)
 
 
def closerighthand():
  i01.moveHand("right",180,180,180,180,180)
 
 
def surrender():
  i01.setHandSpeed("left", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
  i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
  i01.setArmSpeed("right", 0.75, 0.85, 0.95, 0.85)
  i01.setArmSpeed("left", 0.75, 0.85, 0.95, 0.85)
  i01.setHeadSpeed(0.65, 0.65, 0.65, 0.65, 0.65)
  i01.moveHead(90,90,78,100,38)
  i01.moveArm("left",90,139,15,80)
  i01.moveArm("right",90,145,37,80)
  i01.moveHand("left",50,28,30,10,10,76)
  i01.moveHand("right",10,10,10,10,10,139)
 
def pictureleftside():
  i01.setHandSpeed("left", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
  i01.setHandSpeed("right", 0.85, 0.85, 0.85, 0.85, 0.85, 0.85)
  i01.setArmSpeed("right", 1.0, 1.0, 1.0, 1.0)
  i01.setArmSpeed("left", 0.75, 0.85, 0.95, 0.85)
  i01.setHeadSpeed(0.65, 0.65)
  i01.moveHead(109,90)
  i01.moveArm("left",90,105,24,80)
  i01.moveArm("right",0,82,28,15)
  i01.moveHand("left",50,86,97,74,106,119)
  i01.moveHand("right",81,65,82,60,105,113)
 
def picturerightside():
  i01.setHandSpeed("left", 0.85, 0.85, 0.85, 0.85, 0.85, 0.85)
  i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
  i01.setArmSpeed("right", 0.85, 0.85, 0.85, 0.85)
  i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
  i01.setHeadSpeed(0.65, 0.65)
  i01.moveHead(109,90)
  i01.moveArm("left",0,94,28,15)
  i01.moveArm("right",90,115,23,68)
  i01.moveHand("left",42,58,87,55,71,35)
  i01.moveHand("right",10,112,95,91,125,45)
 
def picturebothside():
  i01.setHandSpeed("left", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
  i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
  i01.setArmSpeed("right", 1.0, 1.0, 1.0, 1.0)
  i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
  i01.setHeadSpeed(0.65, 0.65)
  i01.moveHead(109,90)
  i01.moveJaw(50)
  i01.moveArm("left",90,105,24,80)
  i01.moveArm("right",90,115,23,68)
  i01.moveHand("left",50,86,97,74,106,119)
  i01.moveHand("right",10,112,95,91,125,45)
 
def lookrightside():
  i01.setHeadSpeed(0.70, 0.70)
  i01.moveHead(85,40)
 
def lookleftside():
  i01.setHeadSpeed(0.70, 0.70)
  i01.moveHead(85,140)
 
def lookinmiddle():
  i01.setHeadSpeed(0.70, 0.70)
  i01.moveHead(85,90)
 
def powerdown():
        i01.powerDown()
        #sleep(2)        
        #ear.pauseListening()
        #i01.rest()
        #i01.mouth.speakBlocking("I'm powering down")
        #sleep(2)
        #i01.moveHead(40, 85);
        #sleep(4)
        #rightSerialPort.digitalWrite(53, Arduino.LOW)
        #leftSerialPort.digitalWrite(53, Arduino.LOW)
        #ear.lockOutAllGrammarExcept("power up")
        #sleep(2)
        #ear.resumeListening()
 
def powerup():
        i01.powerUp()
        #sleep(2)        
        #ear.pauseListening()
        #rightSerialPort.digitalWrite(53, Arduino.HIGH)
        #leftSerialPort.digitalWrite(53, Arduino.HIGH)
        #i01.mouth.speakBlocking("Im powered up")
        #i01.rest()
        #ear.clearLock()
        #sleep(2)
        #ear.resumeListening()
 
def hello():
     i01.setHandSpeed("left", 0.60, 0.60, 1.0, 1.0, 1.0, 1.0)
     i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
     i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
     i01.setArmSpeed("right", 1.0, 1.0, 1.0, 1.0)
     i01.setHeadSpeed(0.65, 0.75)
     i01.moveHead(105,78)
     i01.moveArm("left",78,48,37,10)
     i01.moveArm("right",90,144,60,75)
     i01.moveHand("left",112,111,105,102,81,10)
     i01.moveHand("right",0,0,0,50,82,180)
     ear.pauseListening()
     sleep(1)
 
     for w in range(0,3):
          i01.setHandSpeed("left", 0.60, 0.60, 1.0, 1.0, 1.0, 1.0)
          i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 0.60)
          i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
          i01.setArmSpeed("right", 0.60, 1.0, 1.0, 1.0)
          i01.setHeadSpeed(0.65, 0.75)
          i01.moveHead(83,98)
          i01.moveArm("left",78,48,37,10)
          i01.moveArm("right",90,157,47,75)
          i01.moveHand("left",112,111,105,102,81,10)
          i01.moveHand("right",3,0,62,41,117,94)
          sleep(1)
 
          if w==1:
                     i01.setHandSpeed("left", 0.60, 0.60, 1.0, 1.0, 1.0, 1.0)
                     i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 0.60)
                     i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
                     i01.setArmSpeed("right", 0.65, 1.0, 1.0, 1.0)
                     i01.setHeadSpeed(0.65, 0.75)
                     i01.moveHead(83,70)
                     i01.mouth.speakBlocking("hello, my name is inmov")
                     i01.moveArm("left",78,48,37,10)
                     i01.moveArm("right",57,145,50,68)
                     i01.moveHand("left",100,90,85,80,71,15)
                     i01.moveHand("right",3,0,31,12,26,45)
                     sleep(1)
                     i01.moveHead(83,98)
                     i01.moveArm("left",78,48,37,10)
                     i01.moveArm("right",90,157,47,75)
                     i01.moveHand("left",112,111,105,102,81,10)
                     i01.moveHand("right",3,0,62,41,117,94)
                     sleep(1)
                     i01.setHandSpeed("left", 0.85, 0.85, 0.85, 0.85, 0.85, 0.85)
                     i01.setHandSpeed("right", 0.85, 0.85, 0.85, 0.85, 0.85, 0.85)
                     i01.setArmSpeed("right", 0.75, 0.85, 0.95, 0.85)
                     i01.setArmSpeed("left", 0.95, 0.65, 0.75, 0.75)
                     i01.setHeadSpeed(0.75, 0.75)
                     i01.moveHead(79,100)
                     i01.moveArm("left",0,94,28,15)
                     i01.moveArm("right",0,82,28,15)
                     i01.moveHand("left",42,58,42,55,71,35)
                     i01.moveHand("right",81,50,82,60,105,113)
                     ear.resumeListening()
 
def photo():    
        i01.moveHead(87,60)
        i01.moveArm("left",78,48,37,10)
        i01.moveArm("right",46,147,5,75)
        i01.moveHand("left",138,52,159,106,120,90)
        i01.moveHand("right",80,65,94,63,70,140)
 
def beforehappy():
        i01.setHandSpeed("left", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
        i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
        i01.setArmSpeed("right", 0.85, 0.85, 0.85, 1.0)
        i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
        i01.setHeadSpeed(0.65, 0.65)
        i01.moveHead(84,88)
        i01.moveArm("left",0,82,36,10)
        i01.moveArm("right",74,112,61,29)
        i01.moveHand("left",0,88,135,94,96,90)
        i01.moveHand("right",81,79,118,47,0,90)
 
def happy():
     for w in range(0,3):
         sleep(1)
         i01.setHandSpeed("left", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
         i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
         i01.setArmSpeed("right", 0.85, 0.85, 0.85, 1.0)
         i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
         i01.setHeadSpeed(0.65, 0.65)
         i01.moveHead(84,88)
         i01.moveArm("left",0,82,36,10)
         i01.moveArm("right",74,112,61,29)
         i01.moveHand("left",0,88,135,94,96,90)
         i01.moveHand("right",81,79,118,47,0,90)
         sleep(1)
         if w==1:
                     i01.mouth.speakBlocking("happy birthday grog")
                     i01.setHandSpeed("left", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
                     i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
                     i01.setArmSpeed("right", 0.85, 0.85, 0.85, 1.0)
                     i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
                     i01.setHeadSpeed(0.65, 0.65)
                     i01.moveHead(42,76)
                     i01.moveArm("left",0,90,30,10)
                     i01.moveArm("right",74,70,61,10)
                     i01.moveHand("left",0,0,0,0,0,90)
                     i01.moveHand("right",81,79,118,47,0,90)
                     sleep(5)
                     ear.resumeListening()
 
def about():
        sleep(2)        
        ear.pauseListening()
        sleep(2)
        i01.setArmSpeed("right", 0.1, 0.1, 0.2, 0.2);
        i01.setArmSpeed("left", 0.1, 0.1, 0.2, 0.2);
        i01.setHeadSpeed(0.2,0.2)
        i01.moveArm("right", 64, 94, 10, 10);
 
 
        i01.mouth.speakBlocking("I am the first life size humanoid robot you can 3D print and animate")
        i01.moveHead(65,66)
        i01.moveArm("left", 64, 104, 10, 10);
        i01.moveArm("right", 44, 84, 10, 10);
        i01.mouth.speakBlocking("my designer creator is Gael Langevin a French sculptor, model maker")
        i01.moveHead(75,86)
        i01.moveArm("left", 54, 104, 10, 10);
        i01.moveArm("right", 64, 84, 10, 20);
        i01.mouth.speakBlocking("who has released my files  to the opensource 3D world.")
        i01.moveHead(65,96)
        i01.moveArm("left", 44, 94, 10, 20);
        i01.moveArm("right", 54, 94, 20, 10);
        i01.mouth.speakBlocking("this is where my builder downloaded my files.")
 
        i01.moveHead(75,76)
        i01.moveArm("left", 64, 94, 20, 10);
        i01.moveArm("right", 34, 94, 10, 10);
        i01.mouth.speakBlocking("after five hundred hours of printing, four kilos of plastic, twenty five hobby servos, blood and sweat.I was brought to life") # should be " i was borght to life."
        i01.moveHead(65,86)
        i01.moveArm("left", 24, 94, 10, 10);
        i01.moveArm("right", 24, 94, 10, 10);  
        i01.mouth.speakBlocking("so if You have a 3D printer, some building skills, then you can build your own version of me") # mabe add in " alot of money"
        i01.moveHead(85,86)
        i01.moveArm("left", 4, 94, 20, 30);
        i01.moveArm("right", 24, 124, 10, 20);
        i01.mouth.speakBlocking("and if enough people build me, some day my kind could take over the world") # mabe add in " alot of money"
        i01.moveHead(75,96)
        i01.moveArm("left", 24, 104, 10, 10);
        i01.moveArm("right", 4, 94, 20, 30);
        i01.mouth.speakBlocking("I'm just kidding. i need some legs to get around, and i have to over come my  pyro-phobia, a fear of fire") # mabe add in " alot of money"
        i01.moveHead(75,96)
        i01.moveArm("left", 4, 94, 10, 10)
        i01.moveArm("right", 4, 94, 10, 10);
        i01.mouth.speakBlocking("so, until then. i will be humankind's humble servant")
 
        i01.rest()
        i01.setArmSpeed("right", 1, 1, 1, 1);
        i01.setArmSpeed("left", 1, 1, 1, 1);
        i01.setHeadSpeed(1,1)
        sleep(2)
        ear.resumeListening()
 
def servos():  
        ear.pauseListening()
        sleep(2)
        i01.setHandSpeed("left", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
        i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
        i01.setArmSpeed("right", 0.85, 0.85, 0.85, 0.85)
        i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
        i01.setHeadSpeed(0.65, 0.65)
        i01.moveHead(79,100)
        i01.moveArm("left",0,119,28,15)
        i01.moveArm("right",0,111,28,15)
        i01.moveHand("left",42,58,87,55,71,35)
        i01.moveHand("right",81,20,82,60,105,113)
        i01.mouth.speakBlocking("I currently have twenty five  hobby servos installed in my body to give me life")
        i01.setHandSpeed("left", 0.85, 0.85, 0.85, 0.85, 0.85, 0.85)
        i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
        i01.setArmSpeed("right", 0.85, 0.85, 0.85, 0.85)
        i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
        i01.setHeadSpeed(0.65, 0.65)
        i01.moveHead(124,90)
        i01.moveArm("left",89,94,91,35)
        i01.moveArm("right",20,67,31,22)
        i01.moveHand("left",106,41,161,147,138,90)
        i01.moveHand("right",0,0,0,54,91,90)
        i01.mouth.speakBlocking("there's one servo  for moving my mouth up and down")
        sleep(1)
        i01.setHandSpeed("left", 0.85, 0.85, 1.0, 0.85, 0.85, 0.85)
        i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
        i01.setArmSpeed("right", 0.85, 0.85, 0.85, 0.85)
        i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
        i01.setHeadSpeed(0.65, 0.65)
        i01.moveHead(105,76);
        i01.moveArm("left",89,106,103,35);
        i01.moveArm("right",35,67,31,22);
        i01.moveHand("left",106,0,0,147,138,7);
        i01.moveHand("right",0,0,0,54,91,90);
        i01.mouth.speakBlocking("two for my eyes")
        sleep(0.2)
        i01.setHandSpeed("left", 0.85, 0.85, 1.0, 1.0, 1.0, 0.85)
        i01.moveHand("left",106,0,0,0,0,7);
        i01.mouth.speakBlocking("and two more for my head")
        sleep(0.5)
        i01.setHandSpeed("left", 0.85, 0.9, 0.9, 0.9, 0.9, 0.85)
        i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
        i01.setArmSpeed("right", 0.85, 0.85, 0.85, 0.85)
        i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
        i01.setHeadSpeed(0.65, 0.65)
        i01.moveHead(90,40);
        i01.moveArm("left",89,106,103,35);
        i01.moveArm("right",35,67,31,20);
        i01.moveHand("left",106,140,140,140,140,7);
        i01.moveHand("right",0,0,0,54,91,90);
        i01.mouth.speakBlocking("so i can look around")
        sleep(0.5)
        i01.setHeadSpeed(0.65, 0.65)
        i01.moveHead(105,125);
        i01.setArmSpeed("left", 0.9, 0.9, 0.9, 0.9)
        i01.moveArm("left",60,100,85,30);
        i01.mouth.speakBlocking("and see who's there")
        i01.setHeadSpeed(0.65, 0.65)
        i01.moveHead(40,56);
        sleep(0.5)
        i01.setArmSpeed("right", 1.0, 1.0, 1.0, 1.0)
        i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
        i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0);
        i01.setArmSpeed("right", 0.5, 0.6, 0.5, 0.6);
        i01.moveArm("left",87,41,64,10)
        i01.moveArm("right",0,95,40,10)
        i01.moveHand("left",98,150,160,160,160,104)
        i01.moveHand("right",0,0,50,54,91,90);
        i01.mouth.speakBlocking("there's three servos  in each shoulder")
        i01.moveHead(40,67);
        sleep(2)
        i01.setHandSpeed("left", 0.8, 0.9, 0.8, 0.8, 0.8, 0.8)
        i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
        i01.setArmSpeed("right", 0.85, 0.85, 0.85, 0.85)
        i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
        i01.setHeadSpeed(0.8, 0.8)
        i01.moveHead(43,69)
        i01.moveArm("left",87,41,64,10)
        i01.moveArm("right",0,95,40,42)
        i01.moveHand("left",42,0,100,80,113,35)
        i01.moveHand("left",42,10,160,160,160,35)
        i01.moveHand("right",81,20,82,60,105,113)
        i01.mouth.speakBlocking("here is the first servo movement")
        sleep(1)
        i01.moveHead(37,60);
        i01.setHandSpeed("left", 1.0, 1.0, 0.9, 0.9, 1.0, 0.8)
        i01.setArmSpeed("right", 1.0, 1.0, 1.0, 1.0)
        i01.moveArm("right",0,95,67,42)
        i01.moveHand("left",42,10,10,160,160,30)
        i01.mouth.speakBlocking("this is the second one")
        sleep(1)
        i01.moveHead(43,69);
        i01.setArmSpeed("right", 1.0, 1.0, 1.0, 1.0)
        i01.moveArm("right",0,134,67,42)
        i01.moveHand("left",42,10,10,10,160,35)
        i01.mouth.speakBlocking("now you see the third")
        sleep(1)
        i01.setArmSpeed("right", 0.8, 0.8, 0.8, 0.8)
        i01.moveArm("right",20,90,45,16)
        i01.mouth.speakBlocking("they give me a more human like movement")
        sleep(1)
        i01.setHandSpeed("left", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
        i01.setArmSpeed("right", 1.0, 1.0, 1.0, 1.0);
        i01.moveHead(43,72)
        i01.moveArm("left",90,44,66,10)
        i01.moveArm("right",90,100,67,26)
        i01.moveHand("left",42,80,100,80,113,35)
        i01.moveHand("right",81,0,82,60,105,69)
        i01.mouth.speakBlocking("but, i have only  one servo, to move each elbow")
        i01.setHandSpeed("left", 0.85, 0.85, 0.85, 0.85, 0.85, 0.85)
        i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
        i01.setArmSpeed("right", 0.85, 0.85, 0.85, 0.85)
        i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
        i01.setHeadSpeed(0.8, 0.8)
        i01.moveHead(45,62)
        i01.moveArm("left",72,44,90,10)
        i01.moveArm("right",90,95,68,15)
        i01.moveHand("left",42,0,100,80,113,35)
        i01.moveHand("right",81,0,82,60,105,0)
        i01.mouth.speakBlocking("that, leaves me, with one servo per wrist")
        i01.moveHead(40,60)
        i01.setHandSpeed("left", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
        i01.setHandSpeed("right", 0.9, 0.9, 0.9, 0.9, 0.9, 0.9)
        i01.moveArm("left",72,44,90,9)
        i01.moveArm("right",90,95,68,15)
        i01.moveHand("left",42,0,100,80,113,35)
        i01.moveHand("right", 10, 140,82,60,105,10)
        i01.mouth.speakBlocking("and one servo for each finger.")
        sleep(0.5)
        i01.moveHand("left",42,0,100,80,113,35)
        i01.moveHand("right", 50, 51, 15,23, 30,140)
        i01.mouth.speakBlocking("these servos are located in my forearms")
        i01.setHandSpeed("left", 0.8, 0.8, 0.8, 0.8,0.8, 0.8)
        i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
        i01.moveHand("left", 36, 52, 8,22, 20);
        i01.moveHand("right", 120, 147, 130,110, 125)
        sleep(1)
        i01.mouth.speakBlocking("they are hooked up, by the use of tendons")
        i01.moveHand("left",10,20,30,40,60,150);
        i01.moveHand("right",110,137,120,100,105,130)
        i01.setHeadSpeed(1,1)
        i01.setArmSpeed("right", 1.0,1.0, 1.0, 1.0)
        i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
        sleep(2)
        ear.resumeListening()
 
def howmanyfingersdoihave():
     ear.pauseListening()
     sleep(1)
     i01.moveHead(49,74)
     i01.moveArm("left",75,83,79,24)
     i01.moveArm("right",65,82,71,24)
     i01.moveHand("left",74,140,150,157,168,92)
     i01.moveHand("right",89,80,98,120,114,0)
     sleep(2)
     i01.moveHand("right",0,80,98,120,114,0)
     i01.mouth.speakBlocking("ten")
 
     sleep(.1)
     i01.moveHand("right",0,0,98,120,114,0)
     i01.mouth.speakBlocking("nine")
 
     sleep(.1)
     i01.moveHand("right",0,0,0,120,114,0)
     i01.mouth.speakBlocking("eight")
 
     sleep(.1)
     i01.moveHand("right",0,0,0,0,114,0)
     i01.mouth.speakBlocking("seven")
 
     sleep(.1)
     i01.moveHand("right",0,0,0,0,0,0)
     i01.mouth.speakBlocking("six")
 
     sleep(.5)
     i01.setHeadSpeed(.70,.70)
     i01.moveHead(40,105)
     i01.moveArm("left",75,83,79,24)
     i01.moveArm("right",65,82,71,24)
     i01.moveHand("left",0,0,0,0,0,180)
     i01.moveHand("right",0,0,0,0,0,0)
     sleep(0.1)
     i01.mouth.speakBlocking("and five makes eleven")
 
     sleep(0.7)
     i01.setHeadSpeed(0.7,0.7)
     i01.moveHead(40,50)
     sleep(0.5)
     i01.setHeadSpeed(0.7,0.7)
     i01.moveHead(49,105)
     sleep(0.7)
     i01.setHeadSpeed(0.7,0.8)
     i01.moveHead(40,50)
     sleep(0.7)
     i01.setHeadSpeed(0.7,0.8)
     i01.moveHead(49,105)
     sleep(0.7)
     i01.setHeadSpeed(0.7,0.7)
     i01.moveHead(90,85)
     sleep(0.7)
     i01.mouth.speakBlocking("eleven")
     i01.moveArm("left",70,75,70,20)
     i01.moveArm("right",60,75,65,20)
     sleep(1)
     i01.mouth.speakBlocking("that doesn't seem right")
     sleep(2)
     i01.mouth.speakBlocking("I think I better try that again")
 
     i01.moveHead(40,105)
     i01.moveArm("left",75,83,79,24)
     i01.moveArm("right",65,82,71,24)
     i01.moveHand("left",140,168,168,168,158,90)
     i01.moveHand("right",87,138,109,168,158,25)
     sleep(2)
 
     i01.moveHand("left",10,140,168,168,158,90)
     i01.mouth.speakBlocking("one")
     sleep(.1)
 
 
     i01.moveHand("left",10,10,168,168,158,90)
     i01.mouth.speakBlocking("two")
     sleep(.1)
 
     i01.moveHand("left",10,10,10,168,158,90)
     i01.mouth.speakBlocking("three")
     sleep(.1)
     i01.moveHand("left",10,10,10,10,158,90)
 
     i01.mouth.speakBlocking("four")
     sleep(.1)
     i01.moveHand("left",10,10,10,10,10,90)
 
     i01.mouth.speakBlocking("five")
     sleep(.1)
     i01.setHeadSpeed(0.65,0.65)
     i01.moveHead(53,65)
     i01.moveArm("right",48,80,78,10)
     i01.setHandSpeed("left", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
     i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
     i01.moveHand("left",10,10,10,10,10,90)
     i01.moveHand("right",10,10,10,10,10,25)
     sleep(1)
     i01.mouth.speakBlocking("and five makes ten")
     sleep(.5)
     i01.mouth.speakBlocking("there that's better")
     i01.moveHead(95,85)
     i01.moveArm("left",75,83,79,24)
     i01.moveArm("right",40,70,70,10)
     sleep(0.5)
     i01.mouth.speakBlocking("inmoov has ten fingers")
     sleep(0.5)
     i01.moveHead(90,90)
     i01.setHandSpeed("left", 0.8, 0.8, 0.8, 0.8, 0.8, 0.8)
     i01.setHandSpeed("right", 0.8, 0.8, 0.8, 0.8, 0.8, 0.8)
     i01.moveHand("left",140,140,140,140,140,60)
     i01.moveHand("right",140,140,140,140,140,60)
     sleep(1.0)
     i01.setArmSpeed("right", 1.0, 1.0, 1.0, 1.0)
     i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
     i01.moveArm("left",0,90,30,10)
     i01.moveArm("right",0,90,30,10)
     sleep(0.5)
     further()
     sleep(0.5)
 
     ear.resumeListening()
 
def systemcheck():
     sleep(2)
     i01.setHeadSpeed(.75,.75)
     i01.moveHead(90,90)
     sleep(1)
     i01.moveHead(72,64)
 
     sleep(2)
     i01.moveHead(155,94)
     sleep(2)
 
     i01.moveHead(90,138)
     sleep(2)
 
     i01.moveHead(29,95)
     sleep(2)
     i01.moveHead(90,90)
     sleep(1.5)
     i01.mouth.speakBlocking("Head, neck and mouth,   check")
     sleep(1)
     i01.setHeadSpeed(.9,.9)
     i01.moveHead(25,61)
     i01.moveArm("left",0,90,30,10)
     i01.setArmSpeed("right",.75,.75,.75,.75)
     i01.moveArm("right",24,62,52,45)
     i01.moveHand("left",0,0,0,0,0,90)
     i01.moveHand("right",0,0,0,0,0,90)
     sleep(2)
     i01.moveHead(90,90)
     i01.setHeadSpeed(.9,.9)
     sleep(1)
     i01.mouth.speakBlocking("right arm and right shoulder,    check")
     sleep(1)
     i01.setHeadSpeed(.9,.9)
     i01.moveHead(20,122)
     i01.setArmSpeed("left",.75,.75,.75,.75)
     i01.moveArm("left",24,62,52,45)
     sleep(2)
     i01.moveHead(90,90)
     i01.setHeadSpeed(.9,.9)
     sleep(1)
     i01.mouth.speakBlocking("left arm and left shoulder,    check")
     sleep(1)
     i01.setHeadSpeed(.9,.9)
     i01.moveHead(20,120)
 
     i01.moveArm("left",75,123,52,45)
     i01.moveArm("right",75,123,52,45)
     i01.moveHand("left",180,180,180,180,180,30)
     i01.moveHand("right",180,180,180,180,180,170)
     sleep(3)
     i01.setHeadSpeed(.9,.9)
     i01.moveHead(59,67)
 
     i01.moveHand("right",0,0,0,0,0,19)
     i01.moveHand("left",0,0,0,0,0,170)
     sleep(1)
     i01.moveHand("left",180,180,180,180,180,30)
     i01.moveHand("right",180,180,180,180,180,170)
     sleep(1.5)
     i01.moveHead(90,90)
     i01.setHeadSpeed(.9,.9)
     sleep(1)
     i01.mouth.speakBlocking(" hands and Wrists,    check")
     sleep(1)
 
     i01.moveHead(90,90)
     i01.moveArm("left",0,90,30,10)
     i01.moveArm("right",0,90,30,10)
     i01.moveHand("left",0,0,0,0,0,90)
     i01.moveHand("right",0,0,0,0,0,90)
     i01.mouth.speakBlocking("all servos are functioning properly")
     sleep(1.5)
     i01.mouth.speakBlocking("awaiting your commands")
 
def whatcanido():
    sleep(10)
    i01.mouth.speakBlocking("people ask")
 
    i01.mouth.speakBlocking("what can I do?")
    sleep(.1)
    i01.mouth.speakBlocking("Well, I can do what ever Bob programs me to do")
    sleep(.3)
 
    i01.moveHead(94,90)
    i01.setArmSpeed("left",.9,.9,.9,.9)
    i01.setArmSpeed("right",.9,.9,.9,.9)
    i01.moveArm("left",19,103,69,30)
    i01.moveArm("right",35,100,50,34)
    i01.moveHand("left",0,0,0,0,0,28)
    i01.moveHand("right",0,0,0,0,0,148)
    i01.mouth.speakBlocking("For example, I can welcome you with open arms")
    sleep(1)
    i01.moveHead(90,90)
    i01.moveArm("left",0,90,30,10)
    i01.moveArm("right",0,90,30,10)
    i01.moveHand("left",0,0,0,0,0,90)
    i01.moveHand("right",0,0,0,0,0,90)
    sleep(.1)
 
    i01.mouth.speakBlocking("and introduce myself")
    i01.setHandSpeed("left", 0.60, 0.60, 1.0, 1.0, 1.0, 1.0)
    i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
    i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
    i01.setArmSpeed("right", 1.0, 1.0, 1.0, 1.0)
    i01.setHeadSpeed(0.65, 0.75)
    i01.moveHead(105,78)
    i01.moveArm("left",78,48,37,10)
    i01.moveArm("right",90,144,60,75)
    i01.moveHand("left",112,111,105,102,81,10)
    i01.moveHand("right",0,0,0,50,82,180)
    ear.pauseListening()
    sleep(.1)
 
    for w in range(0,3):
         i01.setHandSpeed("left", 0.60, 0.60, 1.0, 1.0, 1.0, 1.0)
         i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 0.60)
         i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
         i01.setArmSpeed("right", 0.60, 1.0, 1.0, 1.0)
         i01.setHeadSpeed(0.65, 0.75)
         i01.moveHead(83,98)
         i01.moveArm("left",78,48,37,10)
         i01.moveArm("right",90,157,47,75)
         i01.moveHand("left",112,111,105,102,81,10)
         i01.moveHand("right",3,0,62,41,117,94)
         sleep(.5)
 
         if w==1:
	     i01.mouth.speakBlocking("hello, my name is inmoov")
	     i01.setHandSpeed("left", 1.0,1.0, 1.0, 1.0, 1.0, 1.0)
	     i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 0.60)
	     i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
	     i01.setArmSpeed("right", 0.65, 1.0, 1.0, 1.0)
	     i01.setHeadSpeed(0.65, 0.75)
	     i01.moveHead(83,70)
	     i01.moveArm("left",78,48,37,10)
	     i01.moveArm("right",57,145,50,68)
	     i01.moveHand("left",100,90,85,80,71,15)
	     i01.moveHand("right",3,0,31,12,26,45)
	     sleep(1)
	     i01.moveHead(83,98)
	     i01.moveArm("left",78,48,37,10)
	     i01.moveArm("right",90,157,47,75)
	     i01.moveHand("left",112,111,105,102,81,10)
	     i01.moveHand("right",3,0,62,41,117,94)
	     ear.resumeListening()
    sleep(1)
    i01.mouth.speakBlocking(" I can shake yor hand")
    i01.moveHead(90,90)
    i01.moveArm("left",0,90,30,10)
    i01.moveArm("right",16,80,69,10)
    i01.moveHand("left",0,0,0,0,0,90)
    i01.moveHand("right",0,0,0,0,0,90)
    sleep(1)
    i01.moveHead(90,90)
    i01.moveArm("left",0,90,30,10)
    i01.moveArm("right",0,90,30,10)
    i01.moveHand("left",0,0,0,0,0,90)
    i01.moveHand("right",0,0,0,0,0,90)
 
    sleep(1)
    i01.moveHead(90,90)
    i01.moveArm("left",0,90,30,10)
    i01.moveArm("right",16,80,69,10)
    i01.moveHand("left",0,0,0,0,0,90)
    i01.moveHand("right",75,50,82,113,140,90)
    i01.mouth.speakBlocking("Nice to meet you")
    sleep(.5)
    i01.moveHead(90,90)
    i01.moveArm("left",0,90,30,10)
    i01.moveArm("right",22,80,69,10)
    i01.moveHand("left",0,0,0,0,0,90)
    i01.moveHand("right",95,23,59,109,131,90)
    sleep(.5)
    i01.moveHead(90,90)
    i01.moveArm("left",0,90,30,10)
    i01.moveArm("right",5,80,69,10)
    i01.moveHand("left",0,0,0,0,0,90)
    i01.moveHand("right",75,50,82,113,140,90)
    sleep(.5)
    i01.mouth.speakBlocking("whats your name")
    i01.moveHead(90,90)
    i01.moveArm("left",0,90,30,10)
    i01.moveArm("right",22,80,69,10)
    i01.moveHand("left",0,0,0,0,0,90)
    i01.moveHand("right",95,23,59,109,131,90)
    sleep(1)    
    i01.moveArm("left",0,90,30,10)
    i01.moveArm("right",0,90,30,10)
    i01.moveHand("left",0,0,0,0,0,90)
    i01.moveHand("right",0,0,0,0,0,90)
    sleep(1)
    i01.mouth.speakBlocking("I should really start by checking my servos")
    i01.mouth.speakBlocking("to make sure everything is working properly")
    i01.setHeadSpeed(.75,.75)
    sleep(1)
    i01.moveHead(72,64)
 
    sleep(2)
    i01.moveHead(155,94)
    sleep(2)
    i01.moveHead(90,138)
    sleep(2)
 
    i01.moveHead(29,95)
    sleep(2)
    i01.moveHead(90,90)
    sleep(1.5)
    i01.mouth.speakBlocking("Head, neck and mouth,   check")
    sleep(.7)
    i01.setHeadSpeed(.9,.9)
    i01.moveHead(25,61)
    i01.moveArm("left",0,90,30,10)
    i01.setArmSpeed("right",.75,.75,.75,.75)
    i01.setHandSpeed("left",1,1,1,1,1,1)
    i01.moveArm("right",24,62,52,45)
    i01.moveHand("left",0,0,0,0,0,90)
    i01.moveHand("right",0,0,0,0,0,90)
    sleep(2)
    i01.moveHead(90,90)
    i01.setHeadSpeed(.9,.9)
    sleep(1)
    i01.mouth.speakBlocking("right arm and right shoulder,    check")
    sleep(1)
    i01.setHeadSpeed(.9,.9)
    i01.moveHead(20,122)
    i01.setArmSpeed("left",.75,.75,.75,.75)
    i01.moveArm("left",24,62,52,45)
    sleep(2)
    i01.moveHead(90,90)
    i01.setHeadSpeed(.9,.9)
    sleep(1)
    i01.mouth.speakBlocking("left arm and left shoulder,    check")
    sleep(1)
    i01.setHeadSpeed(.9,.9)
    i01.moveHead(20,120)
 
    i01.moveArm("left",75,123,52,45)
    i01.moveArm("right",75,123,52,45) 
    i01.moveHand("left",180,180,180,180,180,30)
    i01.moveHand("right",180,180,180,180,180,170)
    sleep(3)
    i01.setHeadSpeed(.9,.9)
    i01.moveHead(59,67)
 
    i01.moveHand("right",0,0,0,0,0,19)
    i01.moveHand("left",0,0,0,0,0,170)
    sleep(1)
    i01.moveHand("left",180,180,180,180,180,30)
    i01.moveHand("right",180,180,180,180,180,170)
    sleep(1.5)
    i01.moveHead(90,90)
    i01.setHeadSpeed(.9,.9)
    sleep(1)
    i01.mouth.speakBlocking(" hands and Wrists,    check")
    sleep(1)
    i01.setHandSpeed("Left",1,1,1,1,1,1)
    i01.setHandSpeed("right",1,1,1,1,1,1) 
    i01.moveHead(90,90)
    i01.moveArm("left",0,90,30,10)
    i01.moveArm("right",0,90,30,10)
    i01.moveHand("left",0,0,0,0,0,90)
    i01.moveHand("right",0,0,0,0,0,90)
    i01.mouth.speakBlocking("and report that all servos are functioning properly")
    sleep(.3)
    i01.mouth.speakBlocking("if they are not, I would say")
    sleep(.3)
    i01.mouth.speakBlocking("Houston we have a problem")
    sleep(1.5)
    i01.mouth.speakBlocking("I can tell you about myself")
 
    sleep(.5)
    i01.setArmSpeed("right", 0.1, 0.1, 0.2, 0.2);
    i01.setArmSpeed("left", 0.1, 0.1, 0.2, 0.2);
    i01.setHeadSpeed(0.2,0.2)
    i01.moveArm("right", 64, 94, 10, 10);
 
 
    i01.mouth.speakBlocking("I am the first life size humanoid robot")
    sleep(.1)
    i01.mouth.speakBlocking("that you can 3D print and animate")
    i01.moveHead(65,66)
    i01.moveArm("left", 64, 104, 10, 10);
    i01.moveArm("right", 44, 84, 10, 10);
    i01.mouth.speakBlocking("my designer creator is Gael Langevin a French sculptor, model maker")
    i01.mouth.speakBlocking("as well as being very smart, Gael is also a generous man")
 
    i01.moveHead(75,86)
    i01.moveArm("left", 54, 104, 10, 10);
    i01.moveArm("right", 64, 84, 10, 20);
    i01.mouth.speakBlocking("he released my files  to the opensource 3D world.")
    i01.mouth.speakBlocking("check out his website at InMoov dot F R")
    sleep(.1)
    i01.mouth.speakBlocking("this is where Bob, my builder downloaded my files.")
    i01.mouth.speakBlocking("to print me and get the information to build me so I would work properly")
    i01.moveHead(65,96)
    i01.moveArm("left", 44, 94, 10, 20);
    i01.moveArm("right", 54, 94, 20, 10);
    sleep(.1)
    i01.mouth.speakBlocking("my basic programing was done by GroG, from My Robot Lab")
 
    i01.mouth.speakBlocking("so nothing should go wrong")
    i01.setHeadSpeed(1,1)
    i01.moveHead(90,90)
    sleep(.1)
    i01.moveHead(85,64)
    sleep(.5)
    i01.moveHead(90,90)
    sleep(.3)
    i01.mouth.speakBlocking("go wrong")
    sleep(.1)
    i01.moveHead(85,64)
    sleep(.5)
    i01.moveHead(90,90)
    sleep(.3)
    i01.mouth.speakBlocking("go wrong")
    sleep(.1)
    i01.setHeadSpeed(.5,.5)
    i01.moveHead(20,140)
    sleep(2)
    i01.setHeadSpeed(1,1)
    i01.mouth.speakBlocking("go wrong")
    sleep(.1)
    i01.moveHead(85,64)
    sleep(.1)
    i01.moveHead(90,90)
    sleep(.1)
    i01.moveHead(75,76)
    i01.moveArm("left", 64, 94, 20, 10);
    i01.moveArm("right", 34, 94, 10, 10);
    i01.mouth.speakBlocking("after five hundred hours of printing, four kilos of plastic")
    sleep(.5)
    i01.mouth.speakBlocking("twenty five hobby servos.I was brought to life") # should be " i was borght to life."
    i01.moveHead(65,86)
    i01.moveArm("left", 24, 94, 10, 10);
    i01.moveArm("right", 24, 94, 10, 10);	
    i01mouth.speakBlocking("so if You have a 3D printer, some building skills")
    sleep(.1)
    i01.mouth.speakBlocking("then you can build your own version of me") # mabe add in " alot of money"
    i01.moveHead(85,86)
    i01.moveArm("left", 4, 94, 20, 30);
    i01.moveArm("right", 24, 124, 10, 20);
    i01.mouth.speakBlocking("and if enough people build me, some day my kind could serve mankind") # mabe add in " alot of money"
    i01.moveHead(75,96)
    i01.moveArm("left", 24, 104, 10, 10);
    i01.moveArm("right", 4, 94, 20, 30);
    i01.mouth.speakBlocking("But first, i need some legs to get around") 
    i01.moveHead(75,96)
    i01.moveArm("left", 4, 94, 10, 10)
    i01.moveArm("right", 4, 94, 10, 10);
    i01.mouth.speakBlocking("I know Gael is working hard and wil release those componets soon")
    sleep(1)
    i01.mouth.speakBlocking("Let me tell you about how I move")
    sleep(.5)
    i01.setArmSpeed("right", 1, 1, 1, 1);
    i01.setArmSpeed("left", 1, 1, 1, 1);
    i01.setHeadSpeed(1,1)
    sleep(.5)
    i01.setHandSpeed("left", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
    i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
    i01.setArmSpeed("right", 0.85, 0.85, 0.85, 0.85)
    i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
    i01.setHeadSpeed(0.65, 0.65)
    i01.moveHead(79,100)
    i01.moveArm("left",0,119,28,15)
    i01.moveArm("right",0,111,28,15)
    i01.moveHand("left",42,58,87,55,71,35)
    i01.moveHand("right",81,20,82,60,105,113)
    i01.mouth.speakBlocking("I currently have twenty five  hobby servos installed in my body to give me life")
    i01.setHandSpeed("left", 0.85, 0.85, 0.85, 0.85, 0.85, 0.85)
    i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
    i01.setArmSpeed("right", 0.85, 0.85, 0.85, 0.85)
    i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
    i01.setHeadSpeed(0.65, 0.65)
    i01.moveHead(124,90)
    i01.moveArm("left",89,94,91,35)
    i01.moveArm("right",20,67,31,22)
    i01.moveHand("left",106,41,161,147,138,90)
    i01.moveHand("right",0,0,0,54,91,90)
    i01.mouth.speakBlocking("there's one servo  for moving my mouth up and down")
    sleep(1)
    i01.setHandSpeed("left", 0.85, 0.85, 1.0, 0.85, 0.85, 0.85)
    i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
    i01.setArmSpeed("right", 0.85, 0.85, 0.85, 0.85)
    i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
    i01.setHeadSpeed(0.65, 0.65)
    i01.moveHead(105,76);
    i01.moveArm("left",89,106,103,35);
    i01.moveArm("right",35,67,31,22);
    i01.moveHand("left",106,41,31,147,138,7);
    i01.moveHand("right",0,0,0,54,91,90);
    i01.mouth.speakBlocking("two for my eyes")
    sleep(0.2)
    i01.setHandSpeed("left", 0.85, 0.85, 1.0, 1.0, 1.0, 0.85)
    i01.moveHand("left",106,41,31,31,31,7);
    i01.mouth.speakBlocking("and two more for my head")
    sleep(0.5)
    i01.setHandSpeed("left", 0.85, 0.9, 0.9, 0.9, 0.9, 0.85)
    i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
    i01.setArmSpeed("right", 0.85, 0.85, 0.85, 0.85)
    i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
    i01.setHeadSpeed(0.65, 0.65)
    i01.moveHead(90,40);
    sleep(.3)
    i01.mouth.speakBlocking("I also have a camera in my eye")
    i01.moveArm("left",89,106,103,35);
    i01.moveArm("right",35,67,31,20);
    i01.moveHand("left",106,140,140,140,140,7);
    i01.moveHand("right",0,0,0,54,91,90);
    i01.mouth.speakBlocking("so i can look around")
    sleep(.2)
    i01.setHeadSpeed(0.65, 0.65)
    i01.moveHead(105,125);
    i01.setArmSpeed("left", 0.9, 0.9, 0.9, 0.9)
    i01.moveArm("left",60,100,85,30);
    i01.mouth.speakBlocking("and see who's there")
    i01.setHeadSpeed(0.65, 0.65)
    i01.moveHead(40,56);
    sleep(0.5)
    i01.setArmSpeed("right", 1.0, 1.0, 1.0, 1.0)
    i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
    i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0);
    i01.setArmSpeed("right", 0.5, 0.6, 0.5, 0.6);
    i01.moveArm("left",87,41,64,10)
    i01.moveArm("right",0,95,40,10)
    i01.moveHand("left",98,150,160,160,160,104)
    i01.moveHand("right",0,0,50,54,91,90);
    i01.mouth.speakBlocking("there's three servos  in each shoulder")
    i01.moveHead(40,67);
    sleep(1)
    i01.setHandSpeed("left", 0.8, 0.9, 0.8, 0.8, 0.8, 0.8)
    i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
    i01.setArmSpeed("right", 0.85, 0.85, 0.85, 0.85)
    i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
    i01.setHeadSpeed(0.8, 0.8)
    i01.moveHead(43,69)
    i01.moveArm("left",87,41,64,10)
    i01.moveArm("right",0,95,40,42)
    i01.moveHand("left",42,0,100,80,113,35)
    i01.moveHand("left",42,10,160,160,160,35)
    i01.moveHand("right",81,20,82,60,105,113)
    i01.mouth.speakBlocking("here is the first servo movement")
    sleep(1)
    i01.moveHead(37,60);
    i01.setHandSpeed("left", 1.0, 1.0, 0.9, 0.9, 1.0, 0.8)
    i01.setArmSpeed("right", 1.0, 1.0, 1.0, 1.0)
    i01.moveArm("right",0,95,67,42)
    i01.moveHand("left",42,10,10,160,160,30)
    i01.mouth.speakBlocking("this is the second one")
    sleep(1)
    i01.moveHead(43,69);
    i01.setArmSpeed("right", 1.0, 1.0, 1.0, 1.0)
    i01.moveArm("right",0,134,67,42)
    i01.moveHand("left",42,10,10,10,160,35)
    i01.mouth.speakBlocking("now you see the third")
    sleep(1)
    i01.setArmSpeed("right", 0.8, 0.8, 0.8, 0.8)
    i01.moveArm("right",20,90,45,16)
    i01.mouth.speakBlocking("they give me a more human like movement")
    sleep(1)
    i01.setHandSpeed("left", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
    i01.setArmSpeed("right", 1.0, 1.0, 1.0, 1.0);
    i01.moveHead(43,72)
    i01.moveArm("left",90,44,66,10)
    i01.moveArm("right",90,100,67,26)
    i01.moveHand("left",42,80,100,80,113,35)
    i01.moveHand("right",81,0,82,60,105,69)
    i01.mouth.speakBlocking("but, i have only  one servo, to move each elbow")
    i01.setHandSpeed("left", 0.85, 0.85, 0.85, 0.85, 0.85, 0.85)
    i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
    i01.setArmSpeed("right", 0.85, 0.85, 0.85, 0.85)
    i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0)
    i01.setHeadSpeed(0.8, 0.8)
    i01.moveHead(45,62)
    i01.moveArm("left",72,44,90,10)
    i01.moveArm("right",90,95,68,15)
    i01.moveHand("left",42,0,100,80,113,35)
    i01.moveHand("right",81,0,82,60,105,0)
    i01.mouth.speakBlocking("that, leaves me, with one servo per wrist")
    i01.moveHead(40,70)
    i01.setHandSpeed("left", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
    i01.setHandSpeed("right", 0.9, 0.9, 0.9, 0.9, 0.9, 0.9)
    i01.moveArm("left",72,44,90,9)
    i01.moveArm("right",90,95,68,15)
    i01.moveHand("left",42,0,100,80,113,35)
    i01.moveHand("right", 10, 140,82,60,105,10)
    i01.mouth.speakBlocking("and one servo for each finger.")
    sleep(0.5)
    i01.moveHand("left",42,0,100,80,113,35)
    i01.moveHand("right", 50, 51, 15,23, 30,140);
    i01.mouth.speakBlocking("these servos are located in my forearms")
    i01.setHandSpeed("left", 1,1,1,1,1,1,)
    i01.setHandSpeed("right", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0)
    i01.moveHand("left", 36, 52, 8,22, 20);
    i01.moveHand("right", 120, 147, 130,110, 125);
    further()
    sleep(1)
    i01.mouth.speakBlocking("they are hooked up, by the use of tendons")
    i01.moveHand("left",10,20,30,40,60,150);
    i01.moveHand("right",110,137,120,100,105,130);
    i01.setHeadSpeed(1,1)
    i01.setArmSpeed("right", 1.0,1.0, 1.0, 1.0);
    i01.setArmSpeed("left", 1.0, 1.0, 1.0, 1.0);
    i01.setHandSpeed("left",1,1,1,1,1,1)
    i01.setHandSpeed("right",1,1,1,1,1,1)
 
    sleep(1.5)
    i01.mouth.speakBlocking("Bob, has even taught me to count my fingers")
    sleep(1)
    i01.setHandSpeed("left",1,1,1,1,1,1)
    i01.setHandSpeed("right",1,1,1,1,1,1)
    i01.moveHead(49,87)
    i01.moveArm("left",75,83,79,24)
    i01.moveArm("right",65,82,71,24)
    i01.moveHand("left",74,117,125,157,168,92)
    i01.moveHand("right",0,0,0,0,0,180)
    sleep(2)
    i01.moveHand("right",0,80,98,120,114,180)
    i01.mouth.speakBlocking("ten")
 
    sleep(.1)
    i01.moveHand("right",0,0,98,120,114,180)
    i01.mouth.speakBlocking("nine")
 
    sleep(.1)
    i01.moveHand("right",0,0,0,120,114,180)
    i01.mouth.speakBlocking("eight")
 
    sleep(.1)
    i01.moveHand("right",0,0,0,0,114,180)
    i01.mouth.speakBlocking("seven")
 
    sleep(.1)
    i01.moveHand("right",0,0,0,0,0,180)
    i01.mouth.speakBlocking("six")
 
    sleep(.5)
    i01.setHeadSpeed(.70,.70)
    i01.moveHead(40,105)
    i01.moveArm("left",75,83,79,24)
    i01.moveArm("right",65,82,71,24)
    i01.moveHand("left",0,0,0,0,0,10)
    i01.moveHand("right",0,0,0,0,0,180)
    sleep(.1)
    i01.mouth.speakBlocking("and five makes eleven")
 
    sleep(.7)
    i01.setHeadSpeed(.7,.7)
    i01.moveHead(49,87)
    sleep(.5)
    i01.setHeadSpeed(.7,.7)
    i01.moveHead(40,105)
    sleep(.7)
    i01.setHeadSpeed(.7,.7)
    i01.moveHead(49,87)
    sleep(.5)
    i01.setHeadSpeed(.7,.7)
    i01.moveHead(40,105)
    sleep(.7)
    i01.setHeadSpeed(.7,.7)
    i01.moveHead(90,96)
    sleep(.5)
    i01.mouth.speakBlocking("eleven")
    sleep(1)
    i01.mouth.speakBlocking("that doesn't seem right")
    sleep(1)
 
    i01.mouth.speakBlocking("Oh ya, I need to start from one")
    i01.moveHead(40,105)
    i01.moveArm("left",75,83,79,24)
    i01.moveArm("right",65,82,71,24)
    i01.moveHand("left",87,138,109,168,158,25)
    i01.moveHand("right",87,138,109,168,158,90)
    sleep(2)
 
    i01.moveHand("left",0,138,109,168,158,25)
    i01.mouth.speakBlocking("one")
    sleep(.1)
 
 
    i01.moveHand("left",0,0,109,168,158,25)
    i01.mouth.speakBlocking("two")
    sleep(.1)
 
    i01.moveHand("left",0,0,0,168,158,25)
    i01.mouth.speakBlocking("three")
    sleep(.1)
    i01.moveHand("left",0,0,0,0,158,25)
    i01.mouth.speakBlocking("four")
    sleep(.1)
    i01.moveHand("left",0,0,0,0,0,25)
 
    i01.mouth.speakBlocking("five")
    sleep(.1)
    i01.setHeadSpeed(.65,.65)
    i01.moveHead(53,90)
    i01.moveArm("right",48,80,78,10)
    i01.moveHand("left",0,0,0,0,0,25)
    i01.moveHand("right",0,0,0,0,0,161)
    sleep(1)
    i01.mouth.speakBlocking("and five makes ten")
    sleep(.5)
    i01.mouth.speakBlocking("there that's better")
    i01.moveHead(90,90)
    sleep(.5)
    i01.moveHead(90,90)
    i01.moveArm("left",0,90,30,10)
    i01.moveArm("right",0,90,30,10)
    i01.moveHand("left",0,0,0,0,0,90)
    i01.moveHand("right",0,0,0,0,0,90)
    i01.mouth.speakBlocking("inMoov has ten fingers")
    sleep(1.5)
    i01.mouth.speakBlocking(" i was taught to say the alphabet")
    i01.mouth.speakBlocking("A")
    i01.mouth.speakBlocking("bea")
    i01.mouth.speakBlocking("see")
    i01.mouth.speakBlocking("dee")
    i01.mouth.speakBlocking("E")
    i01.mouth.speakBlocking("Eff")
    i01.mouth.speakBlocking("Gee")
    sleep(.2)
    i01.mouth.speakBlocking("well")
    i01.mouth.speakBlocking("you get it")
    sleep(.5)
    i01.mouth.speakBlocking(" As you can hear I can say any word that I am programed to say")
    sleep(.1)
    i01.setHeadSpeed(.8,.8)
    i01.moveHead(90,110)
    sleep(.2)
 
    i01.mouth.speakBlocking("cat")
    i01.mouth.speakBlocking("dog")
    i01.mouth.speakBlocking("spot")
    i01.mouth.speakBlocking("data")
    i01.mouth.speakBlocking("3D printer")
    i01.inMoov.moveHead(90,80)
    i01.setHeadSpeed(.75,.75)
    i01.mouth.speakBlocking("Arduino")
    i01.mouth.speakBlocking("solar energy")
    i01.mouth.speakBlocking("antidisestabishmentaryism")
    i01.mouth.speakBlocking("supercalifrajewlisticexpialidocious")
 
    sleep(.1)
 
 
    i01.moveHead(90,100)
    i01.setHeadSpeed(.8,.8)
    sleep(.3)
    i01.mouth.speakBlocking("some don't even make sense!!")
 
    sleep(.75)
    i01.mouth.speakBlocking(" I can also do some different poses")
    sleep(.5)
 
    i01.moveHead(75,79)
    i01.moveArm("left",9,115,28,80)
    i01.moveArm("right",13,118,26,80)
    i01.moveHand("left",61,49,14,38,15,64)
    i01.moveHand("right",0,24,54,50,82,180)
    i01.mouth.speakBlocking("like the famous Davinci pose")
    sleep(1)
 
 
    i01.setHeadSpeed(.75,.75)
    i01.moveHead(63,48)
    i01.moveArm("left",77,162,30,78)
    i01.moveArm("right",73,160,30,79)
    i01.moveHand("left",121,163,153,119,158,0)
    i01.moveHand("right",76,169,180,141,180,150)
    i01.mouth.speakBlocking("or how about Arnold showing off his mussels")
    sleep(2)
    i01.setHeadSpeed(.75,.75)
    i01.moveHead(63,150)
    i01.moveArm("left",77,162,30,78)
    i01.moveArm("right",73,160,30,79)
    i01.moveHand("left",121,163,153,119,158,0)
    i01.moveHand("right",76,169,180,141,180,150)
    sleep(1)
    i01.setHeadSpeed(.9,.9)
    i01.moveHead(118,102)
    i01.moveArm("left",77,162,30,78)
    i01.moveArm("right",73,160,30,79)
    i01.moveHand("left",121,163,153,119,158,0)
    i01.moveHand("right",76,169,180,141,180,150)
    sleep(1)
    i01.mouth.speakBlocking("I can do some gestures, like")
 
 
    i01.moveHead(90,90)
    i01.moveArm("left",0,90,30,10)
    i01.moveArm("right",68,105,72,10)
    i01.moveHand("left",0,0,0,0,0,90)
    i01.moveHand("right",60,0,0,180,175,1)
    i01.mouth.speakBlocking(" Peace or victory, depending on your perspective")
    sleep(2)
 
    i01.moveHead(90,90)
    i01.moveArm("left",0,90,30,10)
    i01.moveArm("right",75,160,43,71)
    i01.moveHand("left",0,0,0,0,0,90)
    i01.moveHand("right",180,180,180,180,180,90)
    i01.mouth.speakBlocking("right on, man")
    sleep(2)
 
    i01.setHeadSpeed(.7,.7)    
    i01.moveHead(12,75)
    i01.moveArm("left",15,77,68,10)
    i01.moveArm("right",13,67,57,13)
    i01.moveHand("left",0,0,17,47,0,0)
    i01.moveHand("right",0,0,7,44,23,180)
    i01.mouth.speakBlocking("caring")
    sleep(4)
 
    i01.moveHead(90,90)
    i01.moveArm("left",54,90,46,70)
    i01.moveArm("right",54,90,46,70)
    i01.moveHand("left",0,0,0,0,0,90)
    i01.moveHand("right",0,0,0,0,0,172)
    i01.mouth.speakBlocking("do you need a hug?")
    sleep(3)
    i01v.moveHead(90,90)
    i01.moveArm("left",0,90,30,10)
    i01.moveArm("right",0,90,30,10)
    i01.moveHand("left",0,0,0,0,0,90)
    i01.moveHand("right",0,0,0,0,0,90)
    i01.mouth.speakBlocking(" soon I will be able to sense when someone is in the room")
 
    i01.mouth.speakBlocking(" with my camera and an additional sensor")
    i01.moveHead(90,110)
    i01.moveArm("left",88,90,79,42)
    i01.moveArm("right",0,90,30,10)
    i01.moveHand("left",82,0,140,149,162,90)
    i01.moveHand("right",0,0,0,0,0,90)
    sleep(.1)
    i01.mouth.speakBlocking("that Bob is going to install in me")
    i01.setArmSpeed("left",.9,.9,.9,.9)
    i01.setArmSpeed("right",.9,.9,.9,.9)
    i01.setHandSpeed("left",.9,.9,.9,.9,.9,.9)
    i01.moveHead(90,90)
    i01.moveArm("left",54,90,46,64)
    i01.moveArm("right",54,90,46,64)
    i01.moveHand("left",0,0,0,0,0,90)
    i01.moveHand("right",0,0,0,0,0,172)
    sleep(1)
    i01.setArmSpeed("left",.9,.9,.9,.9)
    i01.setArmSpeed("right",.9,.9,.9,.9)
    i01.moveArm("left",0,90,35,10)
    i01.moveArm("right",10,80,35,15)
 
 
    i01.mouth.speakBlocking("and follow them as they move around")
    i01.moveHead(90,40)
    sleep(1)
    i01.moveHead(90,90)
    i01.moveArm("left",0,90,52,22)
    i01.moveArm("right",12,90,45,30)
    i01.moveHand("left",0,0,0,0,0,90)
    i01.moveHand("right",0,0,0,0,0,90)
    i01.mouth.speakBlocking("Bob is also working on an artificial Intelligence package for me")
    sleep(.1)
    i01.setArmSpeed("left",.9,.9,.9,.9)
    i01.setArmSpeed("right",.9,.9,.9,.9)
    i01.moveArm("left", 0,90,35,15)
    i01.moveArm("right",10,90,30,12)
    i01.mouth.speakBlocking("so I will be able to have a conversation with you")
    sleep(.5)
    i01.mouth.speakBlocking("I am finished telling you about myself")
    sleep(.1)
 
    i01.setHandSpeed("left", 1,1,1,1,1,1)
    i01.setHandSpeed("right", 1,1,1,1,1,1)
    i01.setArmSpeed("left", 1,1,1,1)
    i01.setArmSpeed("right", 1,1,1,1)
    i01.setHeadSpeed(0.65, 0.75)
    i01.moveHead(88,79)
    i01.moveArm("left",89,75,93,11)
    i01.moveArm("right",0,91,28,17)
    i01.moveHand("left",120,130,0,0,0,180)
    i01.moveHand("right",86,51,133,162,153,180)
    i01.mouth.speakBlocking("I think everything went OK")
    sleep(1)
 
 
    i01.moveHead(90,90)
    i01.moveArm("left",0,90,30,10)
    i01.moveArm("right",50,135,73,51)
    i01.moveHand("left",0,0,0,0,0,90)
    i01.moveHand("right",0,0,0,0,0,6)
    sleep(.1)
    i01.moveHand("right",0,0,0,0,0,65)
    sleep(.2)
    i01.moveHand("right",0,0,0,0,0,6)
    i01.mouth.speakBlocking("until next time, Goodbye")
    sleep(.2)
    i01.moveHand("right",0,0,0,0,0,65)
    sleep(.2)
    i01.moveHand("right",0,0,0,0,0,6)
    sleep(.2)
    i01.moveHand("right",0,0,0,0,0,65)
    sleep(.2)
 
    i01.moveHand("right",0,0,0,0,0,6)
    sleep(.2)
    i01.moveHand("right",0,0,0,0,0,65)
    sleep(.2)
    i01.moveHand("right",0,0,0,0,0,6)
    sleep(.2)
    i01.moveHand("right",0,0,0,0,0,65)
    sleep(.2)
    i01.moveHand("right",0,0,0,0,0,6)
    sleep(.2)
    i01.moveHand("right",0,0,0,0,0,65)
    sleep(1)
    i01.moveHead(90,90)
    i01.moveArm("left",0,90,30,10)
    i01.moveArm("right",0,90,30,10)
    i01.moveHand("left",0,0,0,0,0,90)
    i01.moveHand("right",0,0,0,0,0,90)
    