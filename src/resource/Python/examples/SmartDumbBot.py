# SmartDumbBot script for MakerFaire
port = "/dev/ttyACM0"
joystickIndex = 0
 
 
# start services
arduino = Runtime.createAndStart("arduino","Arduino")
joy = runtime.createAndStart("joy","Joystick")
dx 	= Runtime.createAndStart("dx","Servo")
sx	= Runtime.createAndStart("sx","Servo")
 
arduino.connect(port)
joy.setController(joystickIndex)
joy.startPolling()
 
arduino.attach(dx.getName() , 3)
arduino.attach(sx.getName(), 6)
def forward():
 dx.moveTo(60)
 sx.moveTo(120)
 return
def back():
 dx.moveTo(120)
 sx.moveTo(60)
 return
def turnR ():
 dx.moveTo(120)
 sx.moveTo(120)
 return
def turnL ():
 dx.moveTo(60)
 sx.moveTo(60)
 return
def stop ():
 dx.moveTo(90)
 sx.moveTo(90)
 return
 
def x():
    global runningX
    runningX = False
    x = msg_joy_XAxisRaw.data[0]
    print x
    if (x == 1):
     turnR()
     runningX = True
    elif (x == -1):
     turnL()
     runningX = True
    elif (runningY):
     print 'y is running'
    else :
     stop()
    return
def y():
    global runningY
    runningY = False
    y = msg_joy_YAxisRaw.data[0]
    print y
    if (y == 1):
     forward()
     runningY = True
    elif (y == -1):
     back()
     runningY = True
    elif (runningX):
     print 'x is running'
    else :
     stop()
    return 
#create a message route from joy to python so we can listen for button
joy.addListener("XAxisRaw", python.name, "x")
joy.addListener("YAxisRaw", python.name, "y")
 
stop()