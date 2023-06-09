from PyP100 import PyL530
import json
import sys

ip = None
usuario = None
passwd = None

if (len(sys.argv) == 4):
    ip = str(sys.argv[1])
    usuario = str(sys.argv[2])
    passwd = str(sys.argv[3])

if (ip != None and ip != "" and usuario != None and usuario != "" and passwd != None and passwd != ""):
    bombilla = PyL530.L530(ip, usuario, passwd)

    bombilla.handshake()
    bombilla.login()

    bombilla.toggleState()