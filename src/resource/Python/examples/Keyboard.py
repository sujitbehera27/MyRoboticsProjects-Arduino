# create services
keyboard = Runtime.start("keyboard", "Keyboard")
python = Runtime.getService("python")

# non blocking event example
keyboard.addKeyListener(python);

def onKey(key):
    print "you pressed ", key

# blocking example
print "here waiting"
keypress = keyboard.readKey()
print "finally you pressed", keypress, "!"
