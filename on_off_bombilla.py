from PyP100 import PyL530
import json

bombilla = PyL530.L530("192.168.0.29", "franlince.4@gmail.com", "Fran.32353235")

bombilla.handshake()
bombilla.login()

bombilla.toggleState()